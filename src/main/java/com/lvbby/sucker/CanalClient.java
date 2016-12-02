package com.lvbby.sucker;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.common.collect.Lists;
import com.lvbby.sucker.entity.Column;
import com.lvbby.sucker.entity.Row;
import com.lvbby.sucker.entity.SqlType;
import com.lvbby.sucker.event.BinlogEvent;
import com.lvbby.sucker.event.EventListener;
import com.lvbby.sucker.event.PrintEventListener;
import org.apache.commons.lang3.Validate;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by lipeng on 16/12/1.
 */
public class CanalClient implements Runnable {
    private CanalConnector connector;
    private volatile boolean isRunning = false;
    private List<EventListener> listeners = Lists.newArrayList();
    private int batchSize = 1000;

    private String instance;
    private String mysqlUserName = "";
    private String mysqlPassword = "";
    private int canalPort = 11111;
    private Thread thread;

    public CanalClient(String instance, String mysqlUserName, String mysqlPassword) {
        this.instance = instance;
        this.mysqlUserName = mysqlUserName;
        this.mysqlPassword = mysqlPassword;
        Validate.notBlank(instance);
        // 创建链接
        connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(), canalPort), instance, mysqlUserName, mysqlPassword);
        init();
    }

    private CanalClient init() {
        connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();
        return this;
    }

    public void runAsync() {
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            System.out.println("canal server running ....");
            isRunning = true;
            while (isRunning) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    continue;
                }

                handleEvents(convertEvent(message.getEntries()));
                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
        } finally {
            connector.disconnect();
        }
    }

    public void stop() {
        this.isRunning = false;
        if (connector != null) {
            try {
                connector.disconnect();
            } catch (CanalClientException e) {
            }
        }
    }

    private void handleEvents(List<BinlogEvent> event) {
        for (EventListener listener : listeners) {
            listener.handleEvent(event);
        }
    }

    public CanalClient addListener(EventListener eventListener) {
        listeners.add(eventListener);
        return this;
    }

    /***
     * 一个entry就代表在一个table下被改变的若干行
     *
     * @param entrys
     */
    private List<BinlogEvent> convertEvent(List<Entry> entrys) {

        List<BinlogEvent> binlogEvents = Lists.newLinkedList();
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }

            EventType eventType = rowChage.getEventType();

            BinlogEvent binlogEvent = new BinlogEvent();
            binlogEvent.setDb(entry.getHeader().getSchemaName());
            binlogEvent.setTable(entry.getHeader().getTableName());
            binlogEvent.setSqlType(convertSqlEvent(eventType));
            binlogEvent.setRows(rowChage.getRowDatasList().stream()
                    .map(rowData -> new Row(rowData.getAfterColumnsList().stream().map(e -> convert(e)).collect(Collectors.toList())))//rowData -> ROw
                    .collect(Collectors.toList()));
            binlogEvents.add(binlogEvent);
        }
        return binlogEvents;
    }

    private Column convert(com.alibaba.otter.canal.protocol.CanalEntry.Column column) {
        Column re = new Column();
        re.setColumn(column.getName());
        re.setValue(column.getValue());
        re.setPk(column.getIsKey());
        return re;
    }

    private SqlType convertSqlEvent(EventType eventType) {
        switch (eventType) {
            case INSERT:
                return SqlType.insert;
            case UPDATE:
                return SqlType.update;
            case DELETE:
                return SqlType.delete;
        }
        return null;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getMysqlUserName() {
        return mysqlUserName;
    }

    public void setMysqlUserName(String mysqlUserName) {
        this.mysqlUserName = mysqlUserName;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }

    public int getCanalPort() {
        return canalPort;
    }

    public void setCanalPort(int canalPort) {
        this.canalPort = canalPort;
    }

    public static void main(String[] args) {
        //        new CanalClient("lee", "canal", "canal")
        new CanalClient("lee", "root", "")
                .addListener(new PrintEventListener())
                .run();
    }
}