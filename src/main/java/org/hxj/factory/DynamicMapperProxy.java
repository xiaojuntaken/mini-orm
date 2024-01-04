package org.hxj.factory;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hxj.config.MysqlConfig;
import org.hxj.entity.po.Activity;
import org.hxj.enums.MethodEnum;
import org.hxj.utils.StringUtils;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @authorxiaojun
 * @date2023/12/31 15:27
 */
@Slf4j
@Data
public class DynamicMapperProxy<T> implements InvocationHandler {
    //创建属性接受接口类
    private Class<T> interfaceClass;

    private String tableName;

    private Class<?> tableClass;


    //动态代理创建对象时将将被代理的接口传入
    public DynamicMapperProxy(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    //被动态代理创建的bean，被依赖注入的时候
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //因为debug时候发现动态代理类出现null现象
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        Type[] genericSuperclass = interfaceClass.getGenericInterfaces();
        for (Type type : genericSuperclass) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (Type a : actualTypeArguments) {
                String enumClass = a.getTypeName();
                Class<?> aClass = Class.forName(enumClass);
                tableClass = aClass;
                String[] split = enumClass.split("\\.");
                String enumClassName = split[split.length - 1];
                tableName = enumClassName;
            }
            System.out.println("");
        }
        //Type强转成子类ParameterizedType这样就可以通过子类的getActualTypeArguments获取类对象
        String name = proxy.getClass().getName();
        System.out.println("method:" + method.getName());
        return executeSql(method, args);
    }

    public Object executeSql(Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        String methodName = method.getName();
        if (MethodEnum.SELECT_BY_ID.getMethodName().equals(methodName)) {
            Connection conn = MysqlConfig.connectionMysql();

            //根据主键ID查询，返回实体类
            StringBuilder sql = new StringBuilder();
            ResultSet banner = conn.getMetaData().getPrimaryKeys(null, null, "banner");
            sql.append("select * from ").append(tableName).append(" where id = ").append(args[0]);
//        /*
//            通过Connection获取用于执行SQL语句的执行对象Statement
//            Statement对象的作用就是向数据库执行指定的SQL语句。
//         */
            try {
                Statement statement = conn.createStatement();
                ResultSet generatedKeys = statement.getGeneratedKeys();
//                while(banner.next()){
//                    String columnName = generatedKeys.getString("COLUMN_NAME");
//                    System.out.println("");
//                }
                statement.execute(sql.toString());
                ResultSet resultSet = statement.executeQuery(sql.toString());
                Constructor<?> declaredConstructor = tableClass.getDeclaredConstructor();
                Object instance = declaredConstructor.newInstance();
                while (resultSet.next()) {
                    Field[] declaredFields = tableClass.getDeclaredFields();
                    for (Field field:declaredFields){
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        String fieldTypeName = fieldType.getName();
                        if("int".equals(fieldTypeName)){
                            field.set(instance,resultSet.getInt(StringUtils.toUnderScoreCase(field.getName())));
                        }
                    }
                }

                return instance;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
