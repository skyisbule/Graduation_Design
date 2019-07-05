package com.github.skyisbule.db.enty;

import lombok.Data;

import java.util.List;

/**
 *  代表一行数据
 */
@Data
public class Record {

    public int id;
    public List<Object> column;
    public boolean deleted;

}
