package org.hxj.factory;


import com.alibaba.fastjson2.JSONWriterUTF16JDK8UF;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hxj.config.MysqlConfig;
import org.hxj.entity.po.Activity;
import org.hxj.enums.MethodEnum;
import org.hxj.table.TableMetaData;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.StringUtils;

import java.lang.reflect.*;
import java.sql.*;

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
    public DynamicMapperProxy(Class<T> interfaceClass,TableMetaData tableMetaData) {
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
        return executeSql(method, args);
    }

    public Object executeSql(Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        StringBuilder sql = new StringBuilder();
        String methodName = method.getName();
        if (MethodEnum.SELECT_BY_ID.getMethodName().equals(methodName)) {
            sql.append("select * from ").append(tableMetaData.getTableName()).append(" where " + tableMetaData.getPkColumnName() + " = ").append(args[0]);
//        /*
//            通过Connection获取用于执行SQL语句的执行对象Statement
//            Statement对象的作用就是向数据库执行指定的SQL语句。
//         */
            try {
                Connection conn = MysqlUtils.getConnection();
                Statement statement = conn.createStatement();
                statement.execute(sql.toString());
                ResultSet resultSet = statement.executeQuery(sql.toString());
                Constructor<?> declaredConstructor = tableMetaData.getTableClass().getDeclaredConstructor();
                Object instance = declaredConstructor.newInstance();
                while (resultSet.next()) {
                    Field[] declaredFields = tableMetaData.getTableClass().getDeclaredFields();
                    for (Field field : declaredFields) {
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        String fieldTypeName = fieldType.getName();
                        if ("java.lang.Integer".equals(fieldTypeName)) {
                            field.set(instance, resultSet.getInt(StringUtils.toUnderScoreCase(field.getName())));
                        }
                        if ("java.lang.String".equals(fieldTypeName)) {
                            field.set(instance, resultSet.getString(StringUtils.toUnderScoreCase(field.getName())));
                        }
                    }
                }

                return instance;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(MethodEnum.SELECT_BY_PARAM.getMethodName().equals(methodName)){

        }
        return null;
    }

}
