package com.github.skyisbule.db.index.implement;

import java.util.List;

import com.github.skyisbule.db.enty.Page;
import com.github.skyisbule.db.index.Index;

public class BinarySearch implements Index {


    public boolean sync(){

        return true;
    }

    public void initFromDisk(){

    }

    public boolean createIndex() {
        return false;
    }

    public int getPageNum(int id){

        return 0;
    }

    @Override
    public void addIndex(Page page) {

    }

    @Override
    public void addIndex(List<Page> page) {

    }

    public void addIndex(int id){

    }

    public void delIndex(int id){

    }

    @Override
    public void flush(Page page) {

    }

    @Override
    public void flush(List<Page> pages) {

    }

    @Override
    public Index getInstance(String db, String table) {
        return null;
    }

}
