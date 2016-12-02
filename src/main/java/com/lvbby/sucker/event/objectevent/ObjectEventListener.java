package com.lvbby.sucker.event.objectevent;

import com.google.common.collect.Lists;
import com.lvbby.sucker.event.BinlogEvent;
import com.lvbby.sucker.event.EventListener;
import com.lvbby.sucker.tool.Map2Pojo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lipeng on 16/12/1.
 */
public abstract class ObjectEventListener<T> implements EventListener {

    private String db;
    private String table;
    private Map2Pojo<T> map2Pojo;

    @Override
    public void handleEvent(List<BinlogEvent> binlogEvents) {
        if (map2Pojo == null) {
            synchronized (this) {
                if (map2Pojo == null) {
                    map2Pojo = new Map2Pojo<>(getType());
                }
            }
        }
        List<ObjectEvent<T>> events = Lists.newLinkedList();
        for (BinlogEvent binlogEvent : binlogEvents)
            if (StringUtils.equals(db, binlogEvent.getDb()) && StringUtils.equals(binlogEvent.getTable(), table)) {
                ObjectEvent<T> objectEvent = new ObjectEvent().event(binlogEvent);
                if (CollectionUtils.isNotEmpty(binlogEvent.getRows())) {
                    objectEvent.object(binlogEvent.getRows().stream()
                            .map(row -> map2Pojo.parse(row.getColumns().stream().collect(Collectors.toMap(t -> t.getColumn(), t -> t.getValue()))))
                            .collect(Collectors.toList()));
                }
                events.add(objectEvent);
            }
        if (CollectionUtils.isNotEmpty(events))
            handleEvent(new ObjectEventContext(events));
    }

    public abstract void handleEvent(ObjectEventContext<T> re);

    public Class<T> getType() {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) (parameterizedType.getActualTypeArguments()[0]);
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
