package example;

import com.github.skyisbule.db.SkyDB;
import com.github.skyisbule.db.compute.Computer;
import com.github.skyisbule.db.context.ComputeContext;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.util.ConsoleUtil;
import example.po.User;

/**
 * 这个类主要演示：
 * 插入数据后，如何对数据进行遍历 查询
 */
public class SimpleComputeExample {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        //创建SkyDB入口
        SkyDB db = SkyDB.getInstance();
        //创建一个数据集，指定版本为1.0
        DataObject dataObject = db.get(User.class, "1.0");

        //开始对任务进行计算，如果计算比较复杂的话，推荐把computer实现另写一个类，而不是像下面这样只适合简单的情况。
        dataObject.doCompute(new Computer<User>() {
            @Override
            public void init(ComputeContext computeContext) {
                System.out.println("开始计算前，你可以在这里做一些诸如初始化之类的操作。");
            }

            @Override
            public void doCompute(User user, ComputeContext context) {
                System.out.println("db里的每一条数据都会被传进这个函数，这里是你处理数据的最核心的方法");
                //这里只是简单演示一下计算
                if (user.age % 2 == 0){
                    //把它扔到上下文中
                    context.getResultList().add(user);
                }
            }

            @Override
            public void afterOnePage(ComputeContext context) {
                //页表的概念属于进阶的东西，在这里我们暂时不用理会
                System.out.println("已经计算完了一页的数据");
            }

            @Override
            public void afterAll(ComputeContext context) {
                System.out.println("所有的数据都已经处理完了,打印一下结果：");
                ConsoleUtil.show(context.getResultList());
            }
        });
    }

}
