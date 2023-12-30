package org.hxj.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementUtils {
    public static void querySql(Connection conn,String sql) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute(sql);
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println("");
    }
}
