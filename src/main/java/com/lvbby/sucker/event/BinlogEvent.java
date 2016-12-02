package com.lvbby.sucker.event;

import com.google.common.collect.Lists;
import com.lvbby.sucker.entity.Row;
import com.lvbby.sucker.entity.SqlType;

import java.util.List;

/**
 * Created by lipeng on 2016/11/30.
 */
public class BinlogEvent {

    String table;
    String db;
    private SqlType sqlType;
    private List<Row> rows = Lists.newLinkedList();

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
