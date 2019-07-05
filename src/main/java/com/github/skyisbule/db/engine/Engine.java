package com.github.skyisbule.db.engine;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Page;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;

import java.util.List;

public class Engine {

    //todo 这里未来改成从 instance center获取
    private IOCenter ioCenter = new IOCenter();

    public List<String> doSelect(){
        return null;
    }

    public boolean doInsert(String dbName,String tableName,List<String> columns){
        Db    db    = ConfigCenter.getDbByName(dbName);
        Table table = db.getTableByName(tableName);
        int pageNum = table.getPageNum();

        Page page   = ioCenter.getPage(dbName,tableName,pageNum);
        List<ColumnTypeEnum> types = table.getTypes();
        //构建字节序列
        byte[] data = new byte[10];

        return true;
    }

}
