package com.github.skyisbule.db;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.engine.Engine;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

public class InsertTest {

    public static void main(String[] s){

        System.out.println(ByteUtil.byte2int(ByteUtil.int2byte(10)));

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

        List<String> record = new ArrayList<>();
        record.add("skyisbule");
        record.add("21");

        Engine engine = new Engine();
        engine.doInsert("db_test","user",record);

    }

}
