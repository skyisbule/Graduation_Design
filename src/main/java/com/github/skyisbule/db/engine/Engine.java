package com.github.skyisbule.db.engine;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Page;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;
import com.github.skyisbule.db.util.ByteUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Engine {

    private IOCenter ioCenter = (IOCenter)InstanceManager.getInstance("IOCenter");

    public boolean doCreateTable(String dbName,String tableName){
        try {
            ioCenter.createEmptyTable(dbName,tableName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 批量插入的思想就是一次性写满一个page，写满page后再进行io
     * todo 看未来能不能把这坨丑丑的代码优化一下
     */
    public boolean batchInsert(String dbName, String tableName, LinkedList<List<String>> records, int pageNum){
        Db    db    = ConfigCenter.getDbByName(dbName);
        Table table = db.getTableByName(tableName);

        List<ColumnTypeEnum> types = table.getTypes();
        Page page        = ioCenter.getPage(dbName,tableName,pageNum);
        byte[] pageBytes = page.getData();
        int pageEndPos   = page.getPageEndPos();

        table.setRecordNum(table.getRecordNum() + records.size());
        List<Page> pageList = new ArrayList<>();

        for (List<String> columns : records) {

            byte[] data = record2bytes(columns,types);
            //代表没写满  那就不io接着往后写
            if (pageEndPos + data.length < 1024*16){
                pageBytes = ByteUtil.write(pageBytes,data,pageEndPos);
                pageEndPos += data.length;
                page.increaseMaxId();
                page.setData(pageBytes);
            }else{
                //写满了  那就更新信息写入io
                pageBytes = ByteUtil.updateMaxId(pageBytes,page.getMaxId());
                pageBytes = ByteUtil.updateEndPos(pageBytes,pageEndPos);
                page.setPageEndPos(pageEndPos);
                page.setData(pageBytes);
                ConfigCenter.flushConfig(dbName);
                //ioCenter.writePage(dbName,tableName,page);
                pageList.add(page);

                table.setPageNum(table.getPageNum() + 1);//更新一下页号
                Page newPage = new Page();
                newPage.setPageNum(table.getPageNum());
                newPage.setMinId(page.maxId + 1);
                newPage.setMaxId(page.maxId + 1);
                newPage.setPageEndPos(16 + data.length);
                byte[] newData = getEmptyPage(page.pageNum+1,page.minId+1,data);
                pageEndPos = newPage.getPageEndPos();
                pageBytes = newData;
                newPage.setData(newData);
                page = newPage;
                //ioCenter.writePage(dbName,tableName,page);
                pageList.add(page);
            }

        }
        ioCenter.writePages(dbName,tableName,pageList);

        pageBytes = ByteUtil.updateMaxId(pageBytes,page.getMaxId());
        pageBytes = ByteUtil.updateEndPos(pageBytes,pageEndPos);
        page.setData(pageBytes);

        ConfigCenter.flushConfig(dbName);
        ioCenter.writePage(dbName,tableName,page);

        return true;
    }

    private byte[] record2bytes(List<String> columns,List<ColumnTypeEnum> types){
        //开始构建字节序列
        byte[] data = new byte[0];
        for (int i = 0; i < types.size(); i++) {
            switch (types.get(i)){
                case TIME://跟INT处理方式一样
                case INT://直接往后续   int类型末尾不需要终止位  因为我们知道它一定是4个字节
                    byte[] num;
                    if (columns.get(i) == null)
                        num    = ByteUtil.getDataNull();
                    else
                        num    = ByteUtil.int2byte(Integer.parseInt(columns.get(i)));
                    data       = ByteUtil.byteMerger(data,num);
                    break;
                case STRING:
                    byte[] strBytes;
                    String str = columns.get(i);
                    if (str == null)
                        strBytes = ByteUtil.getDataNull();
                    else
                        strBytes = str.getBytes();
                    data = ByteUtil.byteMerger(data,strBytes);
                    byte[] columnEnd = ByteUtil.getColumnEnd();
                    data = ByteUtil.byteMerger(data,columnEnd);
                    break;
            }
        }
        //拼接一下这条数据有没有被删除
        data    = ByteUtil.byteMerger(data,ByteUtil.int2byte(DefaultConfig.ALIVE_FLAG));
        return data;
    }

    public boolean doInsert(String dbName,String tableName,List<String> columns,int pageNum){
        Db    db    = ConfigCenter.getDbByName(dbName);
        Table table = db.getTableByName(tableName);

        Page page   = ioCenter.getPage(dbName,tableName,pageNum);
        List<ColumnTypeEnum> types = table.getTypes();
        //开始构建字节序列
        byte[] data = null;
        data = record2bytes(columns,types);
        //接下来要把数据写入page数组
        int len = data.length;
        //构建完成  此时data  已经为记录数据+换行符的格式了
        //接下来要判断当前页表能否有足够空间供我们插入数据  如果没有的话需要新建页表
        byte[] source  = page.data;
        int pageEndPos = page.getPageEndPos();
        if (pageEndPos + len < (1024*16)){ //代表此时不需要分页,直接在页末添加数据
            int    newMaxPos     = pageEndPos + len;               //0
            byte[] endPosHeader  = ByteUtil.int2byte(newMaxPos);  //1
            int    newRecordId   = page.maxId + 1;
            source = ByteUtil.write(source,ByteUtil.int2byte(newRecordId),8);
            source = ByteUtil.write(source,endPosHeader,12); //2 这3行更新header头信息 maxPos 和 maxId
            page.setData(ByteUtil.write(source,data,pageEndPos));//数据写入source
        }else{
            table.setPageNum(pageNum + 1);//更新一下页号
            Page newPage = new Page();
            newPage.setPageNum(page.getPageNum() + 1);
            newPage.setMinId(page.maxId + 1);
            newPage.setMaxId(page.maxId + 1);
            newPage.setPageEndPos(16 + len);
            byte[] newData = getEmptyPage(page.pageNum+1,page.minId+1,data);
            newPage.setData(newData);
            page = newPage;
        }
        //更新记录
        table.setRecordNum(table.getRecordNum() + 1 );
        ConfigCenter.flushConfig(dbName);
        return ioCenter.writePage(dbName,tableName,page);
    }

    public LinkedList<ArrayList<String>> doReadPage(String dbName,String tableName,int pageNum){
        Db    db    = ConfigCenter.getDbByName(dbName);
        Table table = db.getTableByName(tableName);

        Page   page = ioCenter.getPage(dbName,tableName,pageNum);
        byte[] data = page.getData();
        LinkedList<ArrayList<String>> result = new LinkedList<>();
        int begin = 16;
        do {
            ArrayList<String> recordTemp = new ArrayList<>();
            for (ColumnTypeEnum type : table.getTypes()) {
                switch (type) {
                    case TIME:
                    case INT:
                        byte[] tempInt = ByteUtil.cut(data, begin, 4);
                        int num = ByteUtil.byte2int(tempInt);
                        recordTemp.add(String.valueOf(num));
                        begin += 4;
                        break;
                    case STRING:
                        //这个字段有可能是空的
                        if (data[begin] == DefaultConfig.DATA_NULL_FLAG) {
                            recordTemp.add("");
                            break;
                        }
                        int end = 0;
                        for (int i = begin; i < data.length; i++) {
                            if (data[i] == DefaultConfig.COLUMN_END_FLAG) {
                                end = i;
                                break;
                            }
                        }
                        byte[] strTemp = ByteUtil.cut(data, begin, end - begin);
                        recordTemp.add(new String(strTemp));
                        begin = end + 1;
                }
            }
            result.add(recordTemp);
            begin += 4;
        } while (begin < page.getPageEndPos());
        return result;
    }

    //todo 这里考虑一下是不是可以过滤一下文件名，比如把后缀去掉 比如引入分表概念
    public List<String> getDBs(){
        return ioCenter.getDBs();
    }

    public boolean drop(String db){
        return ioCenter.drop(db);
    }

    private byte[] getEmptyPage(int pageId,int indexId,byte[] data){
        byte[] pageIdBytes = ByteUtil.int2byte(pageId);
        byte[] pageIndexId = ByteUtil.int2byte(indexId);
        byte[] pageEndPos  = ByteUtil.int2byte(data.length + 16);
        byte[] header      = new byte[0];
        header = ByteUtil.byteMerger(header,pageIdBytes);
        header = ByteUtil.byteMerger(header,pageIndexId);
        header = ByteUtil.byteMerger(header,pageIndexId);
        header = ByteUtil.byteMerger(header,pageEndPos);
        //build header end   =>>>>   start build record
        byte[] result = new byte[0];
        result = ByteUtil.byteMerger(result,header);
        result = ByteUtil.byteMerger(result,data);
        result = ByteUtil.byteMerger(result,new byte[1024 * 16 - result.length]);
        return result;
    }

}
