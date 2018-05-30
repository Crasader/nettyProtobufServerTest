package com.edaijia.drivertraceservice.common.utils;

import org.apache.commons.lang.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by tianhong on 2018/5/15.
 */
public class DateUtils {
    public static String getFormatDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            String formatDateTime = dateTime.format(format);
            if (StringUtils.isNotBlank(formatDateTime)) {
                return formatDateTime;
            }
        } catch (DateTimeException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getFormatDateTime());
    }
}
