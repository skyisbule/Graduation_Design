package com.github.skyisbule.db;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.engine.Engine;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;

import java.util.List;

//启动主类
public class Main {

    Engine engine = new Engine();

    public void init(){
        InstanceManager.addInstance("IOCenter",new IOCenter());
    }

    public void createTable(String dbName, String tableName, List<String> names, List<ColumnTypeEnum> types){
        engine.doCreateTable(dbName,tableName);
        Db db = new Db();
        db.setDbName(dbName);
        Table table = new Table();
        table.setPageNum(1);
        table.setColumnNames(names);
        table.setTypes(types);
        table.setRecordNum(0);
        db.tables.put(tableName,table);
        ConfigCenter.dbInfo.put(dbName,db);
    }

    public void doInsert(String dbName,String tableName,List<String> columns){
        engine.doInsert(dbName,tableName,columns);
    }

    public void doSelect(){

    }

    public static void main(String args[]){

    }

}
