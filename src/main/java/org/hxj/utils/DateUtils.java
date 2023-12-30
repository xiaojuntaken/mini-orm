package org.hxj.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static SimpleDateFormat yyyyMMddHHmmSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getDateFormat(Date date){
        return yyyyMMddHHmmSS.format(date);
    }
}
