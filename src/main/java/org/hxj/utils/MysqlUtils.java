package org.hxj.utils;

import lombok.extern.slf4j.Slf4j;
import org.hxj.enums.MethodEnum;
import org.hxj.table.TableMetaData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author xiaojun
 * @date 2023/12/29 23:10
 */
@Slf4j
public class MysqlUtils {
    private static Connection conn;

    public static void connectionMysql() {
        try {
            //1.实例化Driver
              /*
            JDBC连接DBMS的标准流程
            1:加载驱动:Class.forName("不同数据库厂商提供的Driver类")
            2:与数据库建立连接:DriverManager.getConnection()
            3:通过Connection对象创建用于执行SQL语句的Statement对象
            4:通过Statement执行SQ语句
            5:如果执行的是DQL，则可以遍历查询结果集
             */
            //1 JDBC中Driver接口的实现类不同数据库厂商提供的的包名与类名不相同
            String className = "com.mysql.cj.jdbc.Driver";
            Class clazz = Class.forName(className);
            Driver driver = (Driver) clazz.newInstance();
            //2.提供url，指明具体操作的数据
            String url = "jdbc:mysql://localhost:3306/cswl?serverTimezone=UTC";
            //3.提供Properties的对象，指明用户名和密码
            Properties info = new Properties();
            info.setProperty("user", "root");
            info.setProperty("password", "root");
            //4.调用driver的connect()，获取连接
            Connection conn = driver.connect(url, info);
            MysqlUtils.conn = conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static String packageResultSqlByClass(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String underScoreCase = StringUtils.toUnderScoreCase(field.getName());
            list.add(underScoreCase);
        }
        return String.join(",", list);
    }

    public static String packBaseSql(String resultSql, String tableName, String whereSql) {
        return "select " + resultSql + " from " + tableName + " " + whereSql + ";";
    }

    public static ResultSet executeSql(StringBuilder sql) {
        try {
            Connection conn = MysqlUtils.getConnection();
            Statement statement = conn.createStatement();
            log.info("SQL语句：" + sql.toString());
            return statement.executeQuery(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object readAndReturnResult(ResultSet resultSet, Method method, TableMetaData tableMetaData) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        if (MethodEnum.SELECT_BY_ID.getMethodName().equals(method.getName())) {
            Constructor<?> declaredConstructor = tableMetaData.getTableClass().getDeclaredConstructor();
            Object instance = null;
            instance = declaredConstructor.newInstance();
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
        } else if (MethodEnum.SELECT_BY_PARAM.getMethodName().equals(method.getName())) {
            List<Object> result = new ArrayList<>();
            Constructor<?> declaredConstructor = tableMetaData.getTableClass().getDeclaredConstructor();
            Object instance = declaredConstructor.newInstance();
            Field[] declaredFields = tableMetaData.getTableClass().getDeclaredFields();
            while (resultSet.next()) {
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
            result.add(instance);
            return result;
        }
        return null;
    }
}
