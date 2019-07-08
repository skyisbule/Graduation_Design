package com.github.skyisbule.db;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.engine.Engine;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//启动主类
public class MainService {

    private Engine engine = new Engine();

    MainService(){
        init();
    }

    void init(){
        try {
            InstanceManager.addInstance("IOCenter",new IOCenter());
            ConfigCenter.getFromDisk();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("config start fail...\nsystem close");
        }

    }

    void createDb(String dbName){
        try {
            ConfigCenter.createDB(dbName);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("create db fail");
        }
    }

    void createTable(String dbName, String tableName, List<String> names, List<ColumnTypeEnum> types) throws IOException {
        engine.doCreateTable(dbName,tableName);
        Db db = new Db();
        db.setDbName(dbName);
        Table table = new Table();
        table.setTableName(tableName);
        table.setPageNum(1);
        table.setColumnNames(names);
        table.setTypes(types);
        table.setRecordNum(0);
        db.tables.put(tableName,table);
        //序列化配置到本地 同时由配置中心更新缓存
        ConfigCenter.createTable(dbName,table);
    }

    void doInsert(String dbName,String tableName,List<String> columns){
        Db    db    = ConfigCenter.getDbByName(dbName);
        Table table = db.getTableByName(tableName);
        int pageNum = table.getPageNum();

        engine.doInsert(dbName,tableName,columns,pageNum);
    }

    LinkedList<ArrayList<String>> doSelect(String dbName,String tableName){
        //这里写成select *   之后再补充filter
        Db db = ConfigCenter.getDbByName(dbName);
        int pages = db.getTableByName(tableName).pageNum;
        LinkedList<ArrayList<String>> result = new LinkedList<>();
        for (int i = 1; i <= pages; i++) {
            LinkedList<ArrayList<String>> temp = engine.doReadPage(dbName,tableName,i);
            result.addAll(temp);
        }
        return result;
    }

    public static void main(String args[]){

    }

}
