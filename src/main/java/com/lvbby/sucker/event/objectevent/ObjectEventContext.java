package com.lvbby.sucker.event.objectevent;

import java.util.List;

/**
 * Created by lipeng on 2016/12/1.
 */
public class ObjectEventContext<T> {

    List<ObjectEvent<T>> events;

    public ObjectEventContext(List<ObjectEvent<T>> events) {
        this.events = events;
    }

    public List<ObjectEvent<T>> getEvents() {
        return events;
    }

    public void setEvents(List<ObjectEvent<T>> events) {
        this.events = events;
    }


}
