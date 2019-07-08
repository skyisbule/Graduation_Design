package com.github.skyisbule.db;

import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.filter.Filter;
import com.github.skyisbule.db.filter.FilterChain;
import com.github.skyisbule.db.filter.FilterEnum;
import com.github.skyisbule.db.io.IOCenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DBFilterTest {

    public static void main(String[] strings){
        InstanceManager.addInstance("IOCenter",new IOCenter());
        MainService service = new MainService();
        service.init();

        FilterChain chain = new FilterChain();
        Filter filter1 = new Filter();
        filter1.setDoFilter(true);
        filter1.setTarget("skyisbule1");
        filter1.setType(FilterEnum.EQUALS);
        List<Filter> filters = new LinkedList<>();
        filters.add(filter1);
        Filter filter2 = new Filter();
        filter2.setDoFilter(false);
        Filter filter3 = new Filter();
        filter3.setDoFilter(false);
        filters.add(filter2);
        filters.add(filter3);
        chain.setFilters(filters);

        for (int i = 0; i < 3; i++) {
            ArrayList<String> record = new ArrayList<>();
            record.add("skyisbule1");
            record.add("man");
            record.add(String.valueOf(i));
            service.doInsert("test","user",record);
        }

        LinkedList<ArrayList<String>> data = service.doSelect("test","user",chain);

        data.forEach(list->{
            list.forEach(v->{
                System.out.print(v+" ");
            });
            System.out.println();
        });
    }

}
