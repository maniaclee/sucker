package com.lvbby.sucker.entity;

import java.util.List;

/**
 * Created by lipeng on 2016/12/1.
 */
public class Row {
    List<Column> columns;

    public Row(List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
