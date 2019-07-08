package com.github.skyisbule.db;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.engine.Engine;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectTest {

    public static void main(String[] s){

        InstanceManager.addInstance("IOCenter",new IOCenter());

        Db db = new Db();
        db.setDbName("db_test");

        Table table = new Table();
        table.setTableName("user");
        table.setRecordNum(0);
        table.setPageNum(1);
        db.tables.put("user",table);

        List<ColumnTypeEnum> types = new ArrayList<>();
        List<String>         names = new ArrayList<>();
        types.add(ColumnTypeEnum.STRING);
        types.add(ColumnTypeEnum.INT);
        names.add("name");
        names.add("age");

        table.setTypes(types);
        table.setColumnNames(names);

        ConfigCenter.dbInfo.put("db_test",db);



        Engine engine = new Engine();
        AtomicInteger integer = new AtomicInteger(1);
        LinkedList<ArrayList<String>> res = engine.doReadPage("db_test","user",2);
        res.forEach(list->{
            System.out.println("key:"+list.get(0)+" val:"+list.get(1) + " id:" + integer);
            integer.addAndGet(1);
        });
    }


}
