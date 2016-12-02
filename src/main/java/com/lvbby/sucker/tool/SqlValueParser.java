package com.lvbby.sucker.tool;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lipeng on 16/12/1.
 */
public class SqlValueParser {

    public static <T> T parse(String s, Class<T> c) {
        return (T) _parse(s, c);
    }

    public static <T> Object _parse(String s, Class<T> c) {
        if (String.class.equals(c))
            return s;
        if (Integer.class.equals(c))
            return Integer.parseInt(s);

        if (Double.class.equals(c))
            return Double.parseDouble(s);
        if (BigDecimal.class.equals(c))
            new BigDecimal(s);
        if (Float.class.equals(c))
            return Float.parseFloat(s);
        if (Date.class.equals(c))
            return new SimpleDateFormat("yyyyMMdd:hh:mm:ss");
        throw new IllegalArgumentException("unknown type" + c.getName());
    }


}
