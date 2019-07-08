package com.github.skyisbule.db;

import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.io.IOCenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class DbCreateTest {

    public static void main(String[] a) throws IOException {

        InstanceManager.addInstance("IOCenter",new IOCenter());

        MainService service = new MainService();
        service.init();

        //建库
        service.createDb("test");
        //建表
        ArrayList<String> names = new ArrayList<>();
        names.add("name");
        names.add("sex");
        names.add("age");

        ArrayList<ColumnTypeEnum> types = new ArrayList<>();
        types.add(ColumnTypeEnum.STRING);
        types.add(ColumnTypeEnum.STRING);
        types.add(ColumnTypeEnum.INT);

        service.createTable("test","user",names,types);

        for (int i = 0; i < 10; i++) {
            ArrayList<String> record = new ArrayList<>();
            record.add("skyisbule");
            record.add("man");
            record.add(String.valueOf(i));
            service.doInsert("test","user",record);
        }

        LinkedList<ArrayList<String>> data = service.doSelect("test","user");

        data.forEach(list->{
            list.forEach(v->{
                System.out.print(v+" ");
            });
            System.out.println();
        });

    }

}
