package com.github.skyisbule.db.io;

import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.enty.Page;
import com.github.skyisbule.db.util.ByteUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IOCenter {

    public boolean writePage(String db, String table, Page page){
        String path = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".db";
        try{
            RandomAccessFile file = new RandomAccessFile(path, "rws");
            int size = 16*1024;
            file.seek((long)(page.getPageNum()-1)*size);
            file.write(page.getData());
            file.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void createEmptyTable(String db, String table) throws IOException {
        String path = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".db";
        File   file = new File(path);
        if (file.exists()){
            boolean delete = file.delete();
        }else {
            if (file.createNewFile()){
                writePage(db,table,buildEmptyPage());
            }
        }
    }

    public Page buildEmptyPage(){
        byte[] pageIdBytes = ByteUtil.int2byte(1);
        byte[] pageIndexId = ByteUtil.int2byte(0);
        byte[] pageEndPos  = ByteUtil.int2byte(16);
        byte[] header      = new byte[0];
        header = ByteUtil.byteMerger(header,pageIdBytes); // page num
        header = ByteUtil.byteMerger(header,pageIdBytes); // min id
        header = ByteUtil.byteMerger(header,pageIndexId); // max id
        header = ByteUtil.byteMerger(header,pageEndPos);  // end pos
        //build header end   =>>>>   start build record
        byte[] data = ByteUtil.byteMerger(header,new byte[1024*16 - header.length]);
        return new Page(1,1,0,16,data,null);
    }

    public Page getPage(String db, String table, int pageNum) {
        String path = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".db";
        Page page = null;
        try{
            RandomAccessFile file = new RandomAccessFile(path, "r");
            int size = 16*1024;
            if (file.length() < 1) return buildEmptyPage();
            byte[] data = new byte[size];
            file.seek((pageNum-1)*size);
            file.read(data);
            file.close();
            int minId      = ByteUtil.byte2int(ByteUtil.cut(data,4,4));
            int maxId      = ByteUtil.byte2int(ByteUtil.cut(data,8,4));
            int pageEndPos = ByteUtil.byte2int(ByteUtil.cut(data,12,4));
            page = new Page(pageNum,minId,maxId,pageEndPos,data,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return page;
    }

}
