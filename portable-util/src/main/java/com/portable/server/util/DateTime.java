package com.portable.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shiroha
 */
public class DateTime {

    public static Long formatLong(Date date) {
        String strDateFormat = "yyyyMMddHHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat);
        return Long.valueOf(simpleDateFormat.format(date));
    }
}
