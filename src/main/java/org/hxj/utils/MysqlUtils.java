package org.hxj.utils;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.hxj.enums.MethodEnum;
import org.hxj.entity.common.TableMetaData;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * @author xiaojun
 * @date 2023/12/29 23:10
 */
@Slf4j
public class MysqlUtils {
    private static Connection conn;
    private static Map<String, Map<String, Element>> xmlMethodMap = new HashMap<>();

    public static Connection getConnection() {
        return conn;
    }

    public static Map<String, Map<String, Element>> getXmlMethodMap() {
        return xmlMethodMap;
    }

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
//            String url = "jdbc:mysql://localhost:3306/cswl?serverTimezone=UTC";
//            //3.提供Properties的对象，指明用户名和密码
//            Properties info = new Properties();
//            info.setProperty("user", "root");
//            info.setProperty("password", "root");
//            //2.提供url，指明具体操作的数据
            String url = "jdbc:mysql://172.30.192.254:3306/bps?serverTimezone=UTC";
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

    public static ResultSet executeSql(String sql) {
        try {
            Connection conn = MysqlUtils.getConnection();
            Statement statement = conn.createStatement();
            log.info("SQL语句：" + sql);
            return statement.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object readAndReturnResult(ResultSet resultSet, Method method, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        if (MethodEnum.SELECT_BY_ID.getMethodName().equals(method.getName())) {
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            Object instance = null;
            instance = declaredConstructor.newInstance();
            return readSingleResult(resultSet, instance.getClass());
        } else if (MethodEnum.SELECT_BY_PARAM.getMethodName().equals(method.getName())) {
            return readListResult(resultSet, clazz);
        } else {
            //是xml中的方法
            //获取返回值类型
            Type type = method.getGenericReturnType();
            //判断是否是泛型类型
            if (type instanceof ParameterizedType parameterizedType) {
                //返回泛型对象信息
                Type actualTypeArguments = parameterizedType.getActualTypeArguments()[0];
                //将其强转为方法返回的实际的class
                Class<?> actualClass = (Class<?>) actualTypeArguments;
                //返回声明当前的类或接口并强转为实际的class
                Type rawType = parameterizedType.getRawType();
                String rawTypeName = rawType.getTypeName();
                Class<?> statementType = (Class<?>) rawType;
                if (rawTypeName.equals("java.util.List")) {
                    return readListResult(resultSet, actualClass);
                }
            }
        }
        return null;
    }

    private static Object readSingleResult(ResultSet resultSet, Class<?> actualClass) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Field[] declaredFields = actualClass.getDeclaredFields();
        Object obj = actualClass.getDeclaredConstructor().newInstance();
        while (resultSet.next()) {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                String fieldTypeName = fieldType.getName();
                if ("java.lang.Integer".equals(fieldTypeName)) {
                    field.set(obj, resultSet.getInt(StringUtils.toUnderScoreCase(field.getName())));
                }
                if ("java.lang.String".equals(fieldTypeName)) {
                    field.set(obj, resultSet.getString(StringUtils.toUnderScoreCase(field.getName())));
                }
            }
        }
        return obj;
    }

    private static List<Object> readListResult(ResultSet resultSet, Class<?> actualClass) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<Object> result = new ArrayList<>();
        while (resultSet.next()) {
            Field[] declaredFields = actualClass.getDeclaredFields();
            Object obj = actualClass.getDeclaredConstructor().newInstance();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                String fieldTypeName = fieldType.getName();
                if ("java.lang.Integer".equals(fieldTypeName)) {
                    field.set(obj, resultSet.getInt(StringUtils.toUnderScoreCase(field.getName())));
                }
                if ("java.lang.String".equals(fieldTypeName)) {
                    field.set(obj, resultSet.getString(StringUtils.toUnderScoreCase(field.getName())));
                }
            }
            result.add(obj);
        }
        return result;
    }
}
