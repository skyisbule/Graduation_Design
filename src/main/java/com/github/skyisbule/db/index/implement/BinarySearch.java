package com.github.skyisbule.db.index.implement;

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

    public void addIndex(int id){

    }

    public void delIndex(int id){

    }

}
