package com.github.skyisbule.db.io;

import com.github.skyisbule.db.enty.Page;

public class IOCenter {

    public boolean writePage(String db,String talbe, Page page){

        return true;
    }

    public boolean appendPage(String db,Page page){

        return true;
    }

    public Page getPage(String db,String table,int page){

        return new Page();
    }

}
