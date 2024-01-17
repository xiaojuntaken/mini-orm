package org.hxj.factory;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.hxj.core.mapper.BaseMapper;
import org.hxj.entity.po.Activity;
import org.hxj.enums.MethodEnum;
import org.hxj.entity.common.TableMetaData;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * @authorxiaojun
 * @date2023/12/31 15:27
 */
@Slf4j
@Data
public class DynamicMapperProxy<T> implements InvocationHandler {
    //创建属性接受接口类
    private Class<T> interfaceClass;

    private TableMetaData tableMetaData;

    //动态代理创建对象时将将被代理的接口传入
    public DynamicMapperProxy(Class<T> interfaceClass, TableMetaData tableMetaData) {
        this.interfaceClass = interfaceClass;
        this.tableMetaData = tableMetaData;
    }

    //被动态代理创建的bean，被依赖注入的时候
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //因为debug时候发现动态代理类出现null现象
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        //判断方法的底层成员声明类的对象是不是BaseMapper
        StringBuilder sql = new StringBuilder();
        if (BaseMapper.class.equals(method.getDeclaringClass())) {
            //处理
            sql = packageBaseMapperSql(method, args);
        } else {
            sql = packageXmlSql(method, args);
        }
        ResultSet resultSet = MysqlUtils.executeSql(sql.toString());
        return MysqlUtils.readAndReturnResult(resultSet, method, tableMetaData.getTableClass());
    }

    private StringBuilder packageBaseMapperSql(Method method, Object[] args) throws IllegalAccessException {
        StringBuilder sql = new StringBuilder();
        String methodName = method.getName();
        if (MethodEnum.SELECT_BY_ID.getMethodName().equals(methodName)) {
            sql.append("select * from ").append(tableMetaData.getTableName()).append(" where " + tableMetaData.getPkColumnName() + " = ").append(args[0]);
        } else if (MethodEnum.SELECT_BY_PARAM.getMethodName().equals(methodName)) {
            sql.append("select * from ").append(tableMetaData.getTableName()).append(" where 1=1 ");
            analysisParamAndPackageSql(args, sql, method.getName());
        } else if (MethodEnum.DELETE_BY_ID.getMethodName().equals(methodName)) {
            sql.append("delete from ").append(tableMetaData.getTableName()).append(" where " + tableMetaData.getPkColumnName() + " = ").append(args[0]);
        } else if (MethodEnum.UPDATE_BY_ID.getMethodName().equals(methodName)) {
            sql.append("update ").append(tableMetaData.getTableName()).append(" set ");
            analysisParamAndPackageSql(args, sql, method.getName());
        }
        sql.append(";");
        return sql;
    }

    private void analysisParamAndPackageSql(Object[] args, StringBuilder sql, String methodName) throws IllegalAccessException {
        Object arg = args[0];
        Class<?> paramClass = arg.getClass();
        Field[] fields = paramClass.getDeclaredFields();
        StringBuilder whereIdSql = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name = StringUtils.toUnderScoreCase(field.getName());
            Object value = field.get(arg);
            if (methodName.equals(MethodEnum.SELECT_BY_PARAM.getMethodName())) {
                packEqualsSql(sql, name, String.valueOf(value), false);
            } else if (methodName.equals(MethodEnum.UPDATE_BY_ID.getMethodName())) {
                String paramName = field.getName();
                if (value != null) {
                    if (paramName.equals(tableMetaData.getPkColumnName())) {
                        whereIdSql.append(" where ").append(paramName).append(" = ").append("'").append(value).append("' ");
                    }
                    sql.append(paramName).append(" = ").append("'").append(value).append("'");
                    if ((i + 1) == fields.length) {
                        sql.append(",");
                    }
                }
            }
        }
        sql.append(whereIdSql);
    }

    public StringBuilder packageXmlSql(Method method, Object[] args) throws IllegalAccessException, NoSuchFieldException {
        StringBuilder sql = new StringBuilder();
        //都不匹配就是xml中的方法
        Map<String, Element> methodMap = MysqlUtils.getXmlMethodMap().get(interfaceClass.getSimpleName());
        if (methodMap != null) {
            Element element = methodMap.get(method.getName());
            List<Element> elements = element.elements();
            analysisElement(elements, method, args);
            for (int i = 0; i < elements.size(); i++) {
                String name = elements.get(i).getName();
                analysisSingleElement(elements.get(i), method, args);
//                //是for节点
//                if (name.equals("for")) {
//                    //xml中方法参数的名称
//                    analysisForElement(method, args, elements, i);
//                }
//                if (name.equals("if")) {
//                    analysisForElement(method, args, elements, i);
//                }
            }
            String text = element.getStringValue();
            text = text.replaceAll("\\n", "").replaceAll("\\\\n", "");
            sql.append(text);
        }
        sql.append(";");
        return sql;
    }

    void analysisElements(List<Element> elements) {
//        for (int i = 0; i < elements.size(); i++) {
//            if (elements.get(i).hasContent()) {
//
//            }
//        }
//        String elementName = element.getName();
//        //判断是否有子节点
//        if (element.hasContent()) {
//            List<Element> elements = element.elements();
//            for (int i = 0; i < elements.size(); i++) {
//                if ("if".equals(elementName)) {
//
//                } else if ("for".equals(elementName)) {
//
//                }
//            }
//        }
    }

    void analysisSingleElement(Element element, Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {
        if (!CollectionUtils.isEmpty(element.elements())) {
            String elementName = element.getName();
            if ("if".equals(elementName)) {
                String testValue = element.attribute("test").getValue();
                String[] split = testValue.split("and");
                System.out.println("");
            } else if ("for".equals(elementName)) {
                String xmlParamName = element.attribute("collection").getValue();
                String xmlSeparator = element.attribute("separator").getValue();
                //节点中的值
                String forText = element.getTextTrim();
                //获取调用方法的参数
                Parameter[] methodParameters = method.getParameters();
                for (int j = 0; j < methodParameters.length; j++) {
                    String paramName = methodParameters[j].getName();
                    if (xmlParamName.equals(paramName)) {
                        //传入的集合参数
                        List<Object> forParamList = (List<Object>) args[j];
                        String substring = forText.substring(2, forText.length() - 1);
                        List<String> xmlArgs = Arrays.asList(substring.split("\\."));
                        StringBuilder forParam = new StringBuilder();
                        packageFroParam(xmlArgs, forParamList, forParam, xmlSeparator, 0);
                        element.setText(forParam.toString());
                        break;
                    }
                }
            }

        } else {
            analysisElement(element.elements(), method, args);
        }
    }

    private void analysisElement(List<Element> elements, Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {
        elements.stream().forEach(x -> {
            if (CollectionUtils.isEmpty(x.elements())) {

            } else {
                //如果是if节点如果不符合直接remove节点
                if (x.getName().equals("if")) {
                    String test = x.attribute("test").getValue();
                    String[] split = test.split("and");
                    for (int i = 0; i < split.length; i++) {
                        String item = split[i];
                        try {
                            String s = checkIfValue(item, method, args);
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("");
                }
            }

        });


    }

    /**
     * 解析获取 element 节点的值
     *
     * @return
     */
    public String checkIfValue(String testValue, Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {
        String symbol = null;
        if (testValue.contains(">=")) {
            symbol = ">=";
        } else if (testValue.contains(">")) {
            symbol = ">";
        } else if (testValue.contains("==")) {
            symbol = "==";
        } else if (testValue.contains("!=")) {
            symbol = "!=";
        } else if (testValue.contains("<=")) {
            symbol = "<=";
        } else if (testValue.contains("<")) {
            symbol = "<";
        }
        String[] split = testValue.split(symbol);
        //完整字段名
        String wholeAttrName = split[0];
        String comparisonValue = split[1];
        //完整字段名
        String[] attrArr = wholeAttrName.split("\\.");
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].getName();
            if (name.equals(attrArr[0])) {
                //按照索引查找对应的参数
                Object arg = args[i];
                if (attrArr.length > 1) {
                    //获取属性值
                    Field declaredField = arg.getClass().getDeclaredField(attrArr[1]);
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(arg);

                    Object valueByWholeAttrName = getValueByWholeAttrName(new ArrayList<>(Arrays.asList(attrArr)), arg);
                    judgeIsTrue(symbol, valueByWholeAttrName, comparisonValue);
                    Field declaredField1 = valueByWholeAttrName.getClass().getDeclaredField("size");
                    declaredField1.setAccessible(true);
                    break;
                }
            }
        }
        return null;
    }

    public boolean judgeIsTrue(String symbol, Object attrValue, String comparisonValue) {
        boolean flag = false;
        Class<?> clazz = attrValue.getClass();
        if (symbol.equals("!=")) {
            if (comparisonValue.equals("null")) {
                if (attrValue != null) {
                    flag = true;
                }
            } else if (String.valueOf(attrValue).equals(comparisonValue)) {
                flag = true;
            }
        } else if (symbol.equals("==")) {
            if (comparisonValue.equals("null")) {
                if (attrValue != null) {
                    flag = true;
                }
            } else if (String.valueOf(attrValue).equals(comparisonValue)) {
                flag = true;
            }
        }
        if (clazz.getName().equals("java.util.List")) {

        }
        return flag;

    }

    /**
     * 解析返回if参数比较的值
     *
     * @return
     */
    public String returnXmlParamValueByParam(String comparisonValue) {
        if (comparisonValue.equals("null")) {
            return null;
        } else if (comparisonValue.contains("'")) {
            return comparisonValue.replaceAll("'", "");
        } else {
            return comparisonValue;
        }

    }

    public Object getValueByWholeAttrName(List<String> wholeAttrNameArr, Object arg) throws NoSuchFieldException, IllegalAccessException {
        if (wholeAttrNameArr.size() == 1) {
//            Field declaredField = arg.getClass().getDeclaredField(wholeAttrNameArr.get(0));
//            declaredField.setAccessible(true);
//            value = declaredField.get(arg);
            return arg;
        } else {
            Field declaredField = arg.getClass().getDeclaredField(wholeAttrNameArr.get(1));
            declaredField.setAccessible(true);
            Object valueArg = declaredField.get(arg);
            wholeAttrNameArr.remove(0);
            return getValueByWholeAttrName(wholeAttrNameArr, valueArg);
        }
    }

    private void analysisForElement(Method method, Object[] args, List<Element> elements, int i) throws NoSuchFieldException, IllegalAccessException {
        String xmlParamName = elements.get(i).attribute("collection").getValue();
        String xmlItemName = elements.get(i).attribute("item").getValue();
        String xmlSeparator = elements.get(i).attribute("separator").getValue();
        //节点中的值
        String forText = elements.get(i).getTextTrim();

        //获取调用方法的参数
        Parameter[] methodParameters = method.getParameters();
        for (int j = 0; j < methodParameters.length; j++) {
            String paramName = methodParameters[j].getName();
            if (xmlParamName.equals(paramName)) {
                //传入的集合参数
                List<Object> forParamList = (List<Object>) args[j];
                String substring = forText.substring(2, forText.length() - 1);
                List<String> xmlArgs = Arrays.asList(substring.split("\\."));
                StringBuilder forParam = new StringBuilder();
                packageFroParam(xmlArgs, forParamList, forParam, xmlSeparator, 0);
                elements.get(i).setText(forParam.toString());
                break;
            }
        }
    }


    private static void packageFroParam(List<String> xmlArgs, List<Object> forParamList, StringBuilder forParam, String xmlSeparator, int index) throws NoSuchFieldException, IllegalAccessException {
        if (!forParam.isEmpty()) {
            forParam.substring(0, forParam.length() - 1);
            return;
        }
        for (int l = 0; l < forParamList.size(); l++) {
            if ((index + 1) == xmlArgs.size()) {
                forParam.append("'").append(forParamList.get(l).toString()).append("'");
                if ((l + 1) != forParamList.size()) {
                    forParam.append(xmlSeparator);
                }
            } else {
                Field field = forParamList.get(l).getClass().getDeclaredField(xmlArgs.get(index + 1));
                field.setAccessible(true);
                Object item = field.get(forParamList.get(l));
                forParamList.set(l, item);
            }
        }
        packageFroParam(xmlArgs, forParamList, forParam, xmlSeparator, ++index);

    }

    public void packEqualsSql(StringBuilder stringBuilder, String columnName, String value, boolean isString) {
        if (isString) {
            stringBuilder.append(" and ").append(columnName).append(" = '").append(value).append("'");
        } else {
            stringBuilder.append(" and ").append(columnName).append(" = ").append(value).append(" ");
        }
    }

    public List<String> analysisParam(String param) {
        return null;
    }
}
