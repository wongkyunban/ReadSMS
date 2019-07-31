package com.ti.readsms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType) {
        Date date = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        return new SimpleDateFormat(formatType, Locale.getDefault()).format(date);
    }

}
