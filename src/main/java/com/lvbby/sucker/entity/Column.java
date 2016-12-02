package com.lvbby.sucker.entity;

/**
 * Created by lipeng on 2016/11/30.
 */
public class Column {

    private String column;
    private String value;
    private boolean pk;

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
