package example;

import java.util.List;

import com.github.skyisbule.db.SkyDB;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.util.ConsoleUtil;
import example.po.User;

/**
 * 这是一个示例 主要的用途是用来展示：
 * 通过skydb创建一个数据集 并通过api的形式插入数据
 * 插入成功后再打印一下库的状态
 */
public class SimpleInsertExample {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        //创建SkyDB入口
        SkyDB db = SkyDB.getInstance();
        //创建一个数据集，指定版本为1.0
        DataObject dataObject = db.create(User.class, "1.0");

        //演示一下逐个插入
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.uid = i;
            user.name = "sky";
            user.age = 18 + i;
            dataObject.doInsert(user);
        }

        //获取第一个页表的数据
        List<Object> objects = dataObject.getPage(1);
        //你可以自己打印信息
        for (Object object : objects) {
            User temp = (User)object;
            System.out.println(temp.uid + "_" + temp.name + "_" + temp.age);
        }
        //你也可以用内置的util来打印出漂亮的表格，更方便查看。
        ConsoleUtil.show(objects);
    }

}
