package org.hxj.factory;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.hxj.enums.MethodEnum;
import org.hxj.entity.common.TableMetaData;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.StringUtils;

import java.lang.reflect.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        StringBuilder sql = packageSql(method, args);

        ResultSet resultSet = MysqlUtils.executeSql(sql);
        return MysqlUtils.readAndReturnResult(resultSet, method, tableMetaData);
    }

    public StringBuilder packageSql(Method method, Object[] args) throws IllegalAccessException, SQLException, NoSuchFieldException {
        StringBuilder sql = new StringBuilder();
        String methodName = method.getName();
        if (MethodEnum.SELECT_BY_ID.getMethodName().equals(methodName)) {
            sql.append("select * from ").append(tableMetaData.getTableName()).append(" where " + tableMetaData.getPkColumnName() + " = ").append(args[0]);
//        /*
//            通过Connection获取用于执行SQL语句的执行对象Statement
//            Statement对象的作用就是向数据库执行指定的SQL语句。
//         */
        } else if (MethodEnum.SELECT_BY_PARAM.getMethodName().equals(methodName)) {
            sql.append("select * from ").append(tableMetaData.getTableName()).append(" where 1=1 ");
            Object arg = args[0];
            Class<?> paramClass = arg.getClass();
            Field[] fields = paramClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = StringUtils.toUnderScoreCase(field.getName());
                Type genericType = field.getGenericType();
                String typeName = genericType.getTypeName();
                if (typeName.equals("java.lang.Integer")) {
                    if (field.get(arg) != null) {
                        Integer value = (Integer) field.get(arg);
                        packEqualsSql(sql, name, String.valueOf(value), false);
                    }
                } else if (typeName.equals("java.lang.String")) {
                    String value = field.get(name).toString();
                    if (field.get(name) != null) {
                        packEqualsSql(sql, name, value, true);
                    }
                }
            }
        } else {
            //都不匹配就是xml中的方法
            Map<String, Element> methodMap = MysqlUtils.getXmlMethodMap().get(interfaceClass.getSimpleName());
            if (methodMap != null) {
                Element element = methodMap.get(method.getName());
                List<Element> elements = element.elements();
                for (int i = 0; i < elements.size(); i++) {
                    String name = elements.get(i).getName();
                    //是for节点
                    if (name.equals("for")) {
                        Parameter[] parameters = method.getParameters();
                        for (Parameter parameter : parameters) {
                            String name1 = parameter.getName();
                            System.out.println("1");
                        }
                        String string = Arrays.toString(args);
                        //几何参数的名称
                        String paramName = elements.get(i).attribute("collection").getName();
                        for (Object obj : args) {

                        }
//                        Field[] declaredFields = arg.getClass().getDeclaredFields();
//                        for (Field field : declaredFields) {
//
//                        }


                    }

                }
                System.out.println("11");
            }

        }
        sql.append(";");
        return sql;
    }

    public void packEqualsSql(StringBuilder stringBuilder, String columnName, String value, boolean isString) {
        if (isString) {
            stringBuilder.append(" and ").append(columnName).append(" = '").append(value).append("'");
        } else {
            stringBuilder.append(" and ").append(columnName).append(" = ").append(value).append(" ");
        }
    }

}
