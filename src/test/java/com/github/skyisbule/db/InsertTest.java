package com.github.skyisbule.db;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InsertTest {

    public static void main(String[] s){

        InstanceManager.addInstance("IOCenter",new IOCenter());

        Db db = new Db();
        db.setDbName("db_test");

        Table table = new Table();
        table.setTableName("user");
        table.setRecordNum(0);
        table.setPageNum(3);
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

        Random random = new Random();

        MainService service = new MainService();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            List<String> record = new ArrayList<>();
            record.add(getRandomString(random.nextInt(15)));
            record.add(String.valueOf(random.nextInt(50)));

            service.doInsert("db_test","user",record);
        }
        long end = System.currentTimeMillis();
        System.out.println("use time to insert:"+(end -begin));



    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
