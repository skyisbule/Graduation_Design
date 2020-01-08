package example;

import com.github.skyisbule.db.SkyDB;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.parser.SimpleParser;
import com.github.skyisbule.db.util.ConsoleUtil;
import example.po.User;

/**
 * 这个类主要使用来演示：
 * 从文件中加载数据
 */
public class SimpleFileInsertExample {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        //创建SkyDB入口
        SkyDB db = SkyDB.getInstance();
        //创建一个数据集，指定版本为1.1，不和1.0冲突
        DataObject dataObject = db.create(User.class, "1.1");
        //指定文件，并使用默认的解析器
        dataObject.fromFile("/Users/hqt/Desktop/data.text",new SimpleParser());
        //打印一下数据
        ConsoleUtil.show(dataObject.getPage(1));
    }

}
