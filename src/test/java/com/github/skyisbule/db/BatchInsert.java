package com.github.skyisbule.db;

import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.io.IOCenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.skyisbule.db.InsertTest.getRandomString;

public class BatchInsert {

    public static void main(String[] args) throws IllegalAccessException {
        InstanceManager.addInstance("IOCenter",new IOCenter());
        MainService service = new MainService();
        service.init();

        selectAll(service);

        Random random = new Random();
        LinkedList<List<String>> records = new LinkedList<>();


        for (int i = 0; i < 5000; i++) {
            List<String> record = new ArrayList<>();
            record.add(getRandomString(random.nextInt(15)));
            record.add(getRandomString(random.nextInt(15)));
            record.add(String.valueOf(i));
            records.add(record);
            //service.doInsert("db_test","user",record);
        }
        long begin = System.currentTimeMillis();
        service.batchInsert("test","user",records);
        long end = System.currentTimeMillis();
        System.out.println("use time to insert:"+(end -begin));

    }

    private static void selectAll(MainService service) throws IllegalAccessException {
        LinkedList<ArrayList<String>> list = service.doSelect("test","user");
        AtomicInteger id = new AtomicInteger(0);
        list.forEach(r->{
            id.addAndGet(1);
            System.out.print(id+" ");
            r.forEach(c->{
                System.out.print(c+" ");
            });
            System.out.println();
        });
        throw new IllegalAccessException();
    }


}
