package com.lvbby.sucker.event;

import java.util.List;

/**
 * Created by lipeng on 16/12/1.
 */
public interface EventListener {

    void handleEvent(List<BinlogEvent> binlogEvent);
}
