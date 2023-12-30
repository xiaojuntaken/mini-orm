package org.hxj.utils;

import java.lang.reflect.Field;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @authorxiaojun
 * @date2023/12/29 23:10
 */
public class MysqlUtils {
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
