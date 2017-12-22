package com.elasticsearch_java.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDateString(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }

}
