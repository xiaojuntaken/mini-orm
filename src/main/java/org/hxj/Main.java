package org.hxj;

import lombok.extern.slf4j.Slf4j;
import org.hxj.annotation.MiniMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@MiniMapperScan("org.hxj.mapper")
@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
//        Connection conn = MysqlConfig.connectionMysql();
//        /*
//            通过Connection获取用于执行SQL语句的执行对象Statement
//            Statement对象的作用就是向数据库执行指定的SQL语句。
//         */
//        try {
//            Statement statement = conn.createStatement();
//            String sql = "select * from activity";
//            log.info("sql语句："+sql);
//            statement.execute(sql);
//            ResultSet resultSet = statement.executeQuery(sql);
//            /*
//                ResultSet遍历结果集中重要的方法:
//                boolean next()
//                让结果集指针向下移动一条数据，如果结果集存在下一条数据则返回
//                true否则返回false。
//                注:指针默认在结果集第一条记录之前。
//                获取指针当前指向的结果集中该条记录的字段值对应的方法:
//                String getString(int c)
//                获取字符串类型字段的值，c表示结果集中该条记录的第几个字段
//                1表示第一个字段，2表示第二个字段以此类推
//                String getString(String cname)
//                获取字符串类型字段的值,cname表示结果集中该条记录指定名字的字段值
//                int getInt(int c)
//                int getInt(String cname)
//                获取int型字段值
//                同样还有获取浮点型，日期型等方法...
//             */
//            resultSet.next();
//            while(resultSet.next()){
//                int id = resultSet.getInt("id");
//                System.out.println("id==="+id);
//            }
//            System.out.println("搜索完毕");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("连接成功");
    }
}