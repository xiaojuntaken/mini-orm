package org.hxj.utils;

import java.lang.reflect.Field;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author xiaojun
 * @date 2023/12/29 23:10
 */
public class MysqlUtils {
    private static Connection conn ;

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

    public static Connection getConnection(){
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
}
