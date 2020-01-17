package com.github.skyisbule.db.io;

import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.enty.Page;
import com.github.skyisbule.db.index.Index;
import com.github.skyisbule.db.index.SimpleIndex;
import com.github.skyisbule.db.util.ByteUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IOCenter {

    private Index index = new SimpleIndex();

    public boolean writePage(String db, String table, Page page) {
        String path = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".db";
        try {
            RandomAccessFile file = new RandomAccessFile(path, "rws");
            int size = 16 * 1024;
            file.seek((long)(page.getPageNum() - 1) * size);
            file.write(page.getData());
            file.close();
            Index indexImpl = index.getInstance(db, table);
            indexImpl.flush(page);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //todo 这里目前版本预计只实现连续写，即批量写页中页表号表须是连续的 等有空再实现真正的批量写 使其可以跨页表 多次io写
    public boolean writePages(String db, String table, List<Page> pages) {
        if (pages == null || pages.size() < 1) {
            return false;
        }
        String path = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".db";
        try {
            RandomAccessFile file = new RandomAccessFile(path, "rws");
            int size = 16 * 1024;
            file.seek((long)(pages.get(0).getPageNum() - 1) * size);
            byte[] bytes = new byte[0];
            for (Page page : pages) {
                bytes = ByteUtil.byteMerger(bytes, page.getData());
            }
            file.write(bytes);
            file.close();
            Index indexImpl = index.getInstance(db, table);
            indexImpl.flush(pages);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void createEmptyTable(String db, String table) throws IOException {
        String path = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".db";
        File file = new File(path);
        if (file.exists()) {
            boolean delete = file.delete();
        } else {
            if (file.createNewFile()) {
                writePage(db, table, buildEmptyPage());
            }
        }
    }

    private Page buildEmptyPage() {
        byte[] pageIdBytes = ByteUtil.int2byte(1);
        byte[] pageIndexId = ByteUtil.int2byte(1);
        byte[] pageEndPos = ByteUtil.int2byte(16);
        byte[] header = new byte[0];
        header = ByteUtil.byteMerger(header, pageIdBytes); // page num
        header = ByteUtil.byteMerger(header, pageIdBytes); // min id
        header = ByteUtil.byteMerger(header, pageIndexId); // max id
        header = ByteUtil.byteMerger(header, pageEndPos);  // end pos
        //build header end   =>>>>   start build record
        byte[] data = ByteUtil.byteMerger(header, new byte[1024 * 16 - header.length]);
        return new Page(1, 0, 0, 16, data, null);
    }

    public Page getPage(String db, String table, int pageNum) {
        String path = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".db";
        Page page = null;
        try {
            RandomAccessFile file = new RandomAccessFile(path, "r");
            int size = 16 * 1024;
            if (file.length() < 1) { return buildEmptyPage(); }
            byte[] data = new byte[size];
            file.seek((pageNum - 1) * size);
            file.read(data);
            file.close();
            int minId = ByteUtil.byte2int(ByteUtil.cut(data, 4, 4));
            int maxId = ByteUtil.byte2int(ByteUtil.cut(data, 8, 4));
            int pageEndPos = ByteUtil.byte2int(ByteUtil.cut(data, 12, 4));
            page = new Page(pageNum, minId, maxId, pageEndPos, data, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    public List<Page> getPages(String db, String table, int pageBegin, int pageEnd) {

        return null;
    }

    public List<String> getDBs() {
        List<String> dbs = new ArrayList<>();
        File folder = new File(DefaultConfig.BASE_WORK_PATH);
        File[] files = folder.listFiles();
        if (files != null) {
            Arrays.stream(files)
                .filter(file -> file.getAbsolutePath().endsWith(".db"))
                .forEach(file -> dbs.add(file.getName()));
        }
        return dbs;
    }

    public boolean drop(String db) {
        if (db == null) {
            return true;
        }
        File file = new File(db);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

}
