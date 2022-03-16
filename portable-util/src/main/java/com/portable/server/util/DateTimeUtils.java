package com.portable.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shiroha
 */
public class DateTimeUtils {

    public static Long formatLong(Date date) {
        //noinspection SpellCheckingInspection
        String strDateFormat = "yyyyMMddHHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat);
        return Long.valueOf(simpleDateFormat.format(date));
    }
}
