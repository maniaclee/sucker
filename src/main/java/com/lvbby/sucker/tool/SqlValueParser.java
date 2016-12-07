package com.lvbby.sucker.tool;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lipeng on 16/12/1.
 */
public class SqlValueParser {

    public static <T> T parse(String s, Class<T> c) {
        return (T) _parse(s, c);
    }


    public static <T> T parseFromObject(Object s, Class<T> c) {
        return (T) _parseFromObject(s, c);
    }

    private static <T> Object _parseFromObject(Object s, Class<T> c) {
        if (s == null)
            return s;
        Class<?> clz = s.getClass();
        if (c.equals(Date.class)) {
            if (Long.class.equals(clz) || long.class.equals(clz))
                return new Date((Long) s);
            if (Integer.class.equals(clz) || int.class.equals(clz))
                return new Date(1000 * (int) s);
        }
        return s;
    }

    private static <T> Object _parse(String s, Class<T> c) {
        if (Number.class.isAssignableFrom(c) && StringUtils.isBlank(s))
            return null;
        if (c.isPrimitive() && StringUtils.isBlank(s))
            s = "0";

        if (Integer.class.equals(c) || int.class.equals(c))
            return Integer.parseInt(s);
        if (Long.class.equals(c) || long.class.equals(c))
            return Long.parseLong(s);
        if (Short.class.equals(c) || short.class.equals(c))
            return Short.parseShort(s);
        if (Double.class.equals(c) || double.class.equals(c))
            return Double.parseDouble(s);
        if (Float.class.equals(c) || float.class.equals(c))
            return Float.parseFloat(s);

        if (String.class.equals(c))
            return s;
        if (BigDecimal.class.equals(c))
            return new BigDecimal(s);

        if (Date.class.equals(c))
            try {
                return StringUtils.isBlank(s) ? null : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(s);
            } catch (ParseException e) {
                throw new IllegalArgumentException("invalid date format : " + s);
            }
        throw new IllegalArgumentException("unknown type" + c.getName());
    }

}
