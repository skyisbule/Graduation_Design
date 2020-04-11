package example;

import java.util.LinkedList;
import java.util.List;

import com.github.skyisbule.db.SkyDB;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.util.ConsoleUtil;
import example.po.User;

/**
 * 本示例主要演示：
 * 从内存的批量插入功能以及性能展示
 */
public class BathInsertExample {

    private static final String workPath = "/users/hqt/Desktop/db/";

    public static void main(String[] args) {
        //创建db实例
        SkyDB skyDB = SkyDB.getInstance(workPath);
        skyDB.dropIfExit(User.class, "1.0");
        //由于 simpleInsertExample中已经创建了这个数据集 所以这里直接get即可
        //如果你把数据文件删除了 或执行了drop 则在这里你需要使用create
        DataObject dataObject = skyDB.getOrCreate(User.class, "1.0");

        //准备数据
        List<Object> data = new LinkedList<>();
        for (int i = 0; i < 10000; i++) {
            User user = new User(i, "sky", 18 + i);
            data.add(user);
        }
        long beginTime = System.currentTimeMillis();
        //执行批量插入
        dataObject.bathInsert(data);
        long endTime = System.currentTimeMillis();
        //看一下插入所花费的时间
        System.out.println("插入10000条数据总共花费：" + (endTime - beginTime) + "ms");
        //打印一下此时的数据集信息
        dataObject.showInfo();

        //获取第一个页表的数据
        List<Object> objects = dataObject.getPage(1);
        //你也可以用内置的util来打印出漂亮的表格，更方便查看。
        ConsoleUtil.show(objects);

    }

}
