package com.github.skyisbule.db;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.engine.Engine;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.util.ByteUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SelectTest {

    public static void main(String[] s){

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
        LinkedList<ArrayList<String>> res = engine.doReadPage("db_test","user",1);
        res.forEach(list->{
            System.out.println("key:"+list.get(0)+" val:"+list.get(1));
        });
    }


}
