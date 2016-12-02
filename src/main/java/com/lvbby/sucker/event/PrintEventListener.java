package com.lvbby.sucker.event;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by lipeng on 2016/12/1.
 */
public class PrintEventListener implements EventListener {
    @Override
    public void handleEvent(List<BinlogEvent> binlogEvent) {
        binlogEvent.forEach(e -> System.out.println(JSON.toJSONString(binlogEvent)));
    }
}
