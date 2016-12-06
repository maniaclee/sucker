package com.lvbby.sucker.tool;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lipeng on 16/12/1.
 */
public class Map2Pojo<T> {
    Class<T> t;
    Map<String, Field> fieldMap;

    public Map2Pojo(Class<T> t) {
        this.t = t;
        init();
    }

    private void init() {
        fieldMap = Arrays.asList(t.getDeclaredFields()).stream()
                .filter(e -> filterField(e))
                .collect(Collectors.toMap(e -> {
                    e.setAccessible(true);
                    return format(e.getName());
                }, e -> e));
    }

    private boolean filterField(Field e) {
        return !Modifier.isStatic(e.getModifiers()) && !Modifier.isTransient(e.getModifiers());
    }

    private String format(String s) {
        return s.replaceAll("[^1-9a-zA-Z]", "").toLowerCase();
    }

    public T fromObjectMap(Map<String, Object> map) {
        return _parseObject(map, e -> e.getValue());
    }

    public T fromStringMap(Map<String, String> map) {
        return _parseObject(map, e -> SqlValueParser.parse((String) e.getKey(), e.getValue()));
    }


    public T _parseObject(Map<String, ?> map, Function<Pair<Object, Class>, Object> function) {
        T re = null;
        try {
            re = this.t.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        T finalRe = re;
        map.forEach((k, v) -> {
            Field field = fieldMap.get(format(k));
            if (field != null)
                try {
                    field.set(finalRe, function.apply(Pair.of(v, field.getType())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
        return finalRe;
    }


}
