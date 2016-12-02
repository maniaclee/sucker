package com.lvbby.sucker.event.objectevent;

import com.lvbby.sucker.event.BinlogEvent;

import java.util.List;

/**
 * Created by lipeng on 2016/12/1.
 */
public class ObjectEvent<T> {
    private BinlogEvent binlogEvent;
    private List<T> object;


    public ObjectEvent<T> event(BinlogEvent binlogEvent) {
        setBinlogEvent(binlogEvent);
        return this;
    }

    public ObjectEvent<T> object(List<T> object) {
        setObject(object);
        return this;
    }

    public BinlogEvent getBinlogEvent() {
        return binlogEvent;
    }

    public void setBinlogEvent(BinlogEvent binlogEvent) {
        this.binlogEvent = binlogEvent;
    }

    public List<T> getObject() {
        return object;
    }

    public void setObject(List<T> object) {
        this.object = object;
    }
}
