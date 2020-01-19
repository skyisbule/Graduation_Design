package example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.skyisbule.db.SkyDB;
import com.github.skyisbule.db.compute.Computer;
import com.github.skyisbule.db.context.ComputeContext;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.util.ConsoleUtil;
import example.po.Paper;

/**
 * 这是一个来自真实案例的例子：
 * 首先说一下项目背景，大一的时候老师交给我一份论文数据，让我去做引用关系分析，它的表现形式是在mysql里的一张表。
 * 这张表有两列 论文ID 引用的论文ID 所以看上去它长这个样子:
 * --------
 * |id |ref|
 * | 1 | 2 |
 * | 1 | 3 |
 * | 1 | 4 |
 * | 2 | 8 |
 * | 2 | 11|
 * | 3 | 5 |
 * ---------
 * 第一列代表一篇论文的唯一id，第二列代表这篇论文它引用了哪些论文，从上图的数据中（当然这个数据是我随便打的，不是真实数据）可以看到，
 * id为1的这篇论文它引用了id为2、3、4的论文，id为2的论文引用了id为8、11的论文，等等。
 * 嗯，听起来很简单的样子，就是个一对多的关系，但问题就在于，这张表有9亿8000多万行，并且没有任何索引，所以基于它的任何sql查询
 * 都将扫全表，且必然会非常非常慢，select * from table where id = 1,这条语句将运行数十秒才返回。
 * 而我当时的需求是要对论文的引用关系进行递归的查询，即找出一篇论文引用了哪些论文后，再找引用的这些论文又引用了哪些论文，
 * 重复3-4次后得到一个论文引用网络，对这个网络再次进行处理，从而分析出核心论文，以及不同论文的网络特征。
 * 所以可以看到这个计算过程中需要大量的查询操作，每深入一层数据量都将成指数形式递增，比如每篇论文都引用10篇论文的话（实际上大多数远不止10篇），
 * 在最坏的情况下，第一次查询需要查 1 次db，第二次查询需要查 10 次db，第三次查询需要查 100 次db，第四次。。。
 * 而单次查询就要数十秒的表来说，分析一次可能需要几天甚至更长的时间。
 * 那么当前的简单架构显然无法支撑起业务，那么就必然要对数据进行一次预处理，去优化查询效率。
 * 当时的我虽然刚开始学编程，但也有索引和数据集合的概念，于是我很容易的想到了一个办法：压缩数据，建立唯一索引。
 * 即把这张表变为：
 * -----------------
 * |id |    refs    |
 * | 1 |    2,3,4   |
 * | 2 |    8,11    |
 * | 3 |      5     |
 * ------------------
 * 并把id这列变成主键，那么我们查找引用关系的话就是针对主键的查找了，并且因为压缩的原因，整张表的行数也会非常明显的下降，mysql的压力也会小很多，
 * 最终的话，批量处理过后，整张表大概只有3000万行数据，而又因为所有的查询都是主键查询，于是单次db查询基本是秒级返回。
 * 于是一次论文网络计算的时间也从数天被压缩到几分钟到几十分钟的级别，并且如果引用关系不复杂的话还要更快一些（因为很多论文都会引用同几篇论文，
 * 所以网络的传递会出现断层，不会再有新的要查询的论文，于是指数增长会被打破，查询次数就会少很多，返回的网络也"更小"，更精准）。
 * 但由于当时的我毕竟是一只只学了两个多月编程的萌新，所以做到这个程度后，我就将这个项目（表、查询及分析代码、前端页面展示及网络切割）和当时的论文成果
 * 都交给了学长，然后自己就回去上课去了，也没再管过这个事儿了。
 * ------------------------------------------------------------------------------------------------------------
 * 但这样仍然不够，因为我们当时的期望是用户可以在网页上，实时的去分析，即要求最多几十秒就要将数据返回给客户端，几十分钟仍然不满足要求。
 * 其次，我当时为了将表转成目标表，是直接暴力读源.sql文件拼接sql语句再插入新表的，中间遇到了各种问题：文件难读，会爆内存，效率低等等，
 * 并且即便是压缩后，走查询仍然不够快，分析起来仍然有很大的困难。
 * 但现在不一样了，有了我的这个计算引擎，即便是一个刚学编程的初学者，也能分分钟完成上述的需求，并且实现毫秒级查询！
 * 故这个示例文件又是为了演示如何使用本计算引擎，来优雅地解决这个问题。
 */
public class PaperAnalysisExample {

    private static void buildData(DataObject dataObject) throws IllegalAccessException {

        int insertNum = 10000;

        List<Object> insertList = new ArrayList<>(10000);
        Random random = new Random();
        for (int i = 1; i < insertNum; i++) {

            int refNum = random.nextInt(29) + 1;
            Paper paper = new Paper();
            StringBuilder sb = new StringBuilder();
            for (int loop = 0; loop < refNum - 1; loop++) {
                int ref = random.nextInt(insertNum - 1) + 1;
                sb.append(ref).append(" ");
            }
            int ref = random.nextInt(insertNum - 1) + 1;
            sb.append(ref).append(" ");

            paper.setId(i);
            paper.setRefs(sb.toString());

            insertList.add(paper);

            if (insertList.size() > 10000) {
                dataObject.bathInsert(insertList);
                insertList = new ArrayList<>(10000);
            }
        }

        dataObject.bathInsert(insertList);

    }

    private static void buildSourceData(DataObject dataObject) {
        int totalRecord = 0;
        int insertNum = 10000;
        List<Object> insertList = new ArrayList<>(10000);
        Random random = new Random();
        for (int i = 1; i < insertNum; i++) {
            //这篇论文引用了多少篇论文
            int refsNum = random.nextInt(20) + 1;
            for (int loop = 0; loop < refsNum; loop++) {
                Paper paper = new Paper();
                paper.setId(i);
                paper.setRefs(String.valueOf(random.nextInt(insertNum)));
                insertList.add(paper);
                if (insertList.size() > 10000) {
                    dataObject.bathInsert(insertList);
                    totalRecord += insertList.size();
                    insertList.clear();
                }
            }
        }
        dataObject.bathInsert(insertList);
        totalRecord += insertList.size();
        insertList.clear();
        System.out.println("complete total: " + totalRecord);

    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        SkyDB skyDB = SkyDB.getInstance();
        DataObject dataObject = skyDB.getOrCreate(Paper.class, "1.0");
        buildSourceData(dataObject);

        List<Object> data = dataObject.getPage(1);

        ConsoleUtil.show(data);

        if (1==1)return;

        DataObject mergedObject = skyDB.create(Paper.class, "1.1");

        dataObject.doCompute(new Computer<Paper>() {

            static final int mergedListSize = 1000;

            private List<Object> mergedList = new ArrayList<>(mergedListSize);

            @Override
            public void init(ComputeContext computeContext) {

            }

            @Override
            public void doCompute(Paper paper, ComputeContext context) {
                Integer id = paper.getId();
                List<Object> temp = context.getResultList();
                if (temp.size() == 0) {
                    temp.add(paper);
                } else {
                    //当前列表里的最后一个论文对象
                    Paper leastPaper = ((Paper)temp.get(temp.size() - 1));
                    if (leastPaper.getId().equals(id)) {
                        temp.add(paper);
                    } else {
                        doMergePaper(context.getResultList());
                        context.getResultList().clear();
                        context.getResultList().add(paper);
                    }
                }
            }

            @Override
            public void afterOnePage(ComputeContext context) {

            }

            @Override
            public void afterAll(ComputeContext context) {
                List<Object> list = context.getResultList();
                if (list.size() > 0) {
                    doMergePaper(list);
                }
                if (mergedList.size() > 0) {
                    mergedObject.bathInsert(mergedList);
                }
            }

            private void doMergePaper(List<Object> list) {
                //把列表中的论文拿出来合并一下
                StringBuilder refs = new StringBuilder();
                for (Object o : list) {
                    refs.append(((Paper)o).refs).append(" ");
                }
                Paper newPaper = new Paper();
                newPaper.setId(((Paper)list.get(0)).getId());
                newPaper.setRefs(refs.toString());
                mergedList.add(newPaper);
                if (mergedList.size() > mergedListSize){
                    mergedObject.bathInsert(mergedList);
                    mergedList.clear();
                }
            }

        });

        data = mergedObject.getPage(1);

        ConsoleUtil.show(data);

    }

}
