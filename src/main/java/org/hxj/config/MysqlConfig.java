package org.hxj.config;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

public class MysqlConfig {
    public static Connection connectionMysql() {
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
            System.out.println(conn);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
