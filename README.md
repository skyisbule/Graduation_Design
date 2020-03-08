# 关于本项目
如你所见，它是我的本科毕业设计，从商汤开始写到阿里结束，拖拖拉拉了半年，hhhhh。

# 关于项目背景
如果一个没有大数据背景的人去处理大数据，那么他会怎么做？      
大概率会把数据导入MySql，然后用蹩脚的SQL去尝试把数据统计出来，这是比较理想的情况。     
那么万一数据量很大MySql响应非常慢呢？万一统计很复杂SQL根本无法实现呢？万一每步统计出的数据量贼大，直接把内存撑爆了呢？      
你可能会说"这简单，上Hadoop+Spark啊"，但很显然，对新手来说，你让他学这两个新人杀手的东西，很不现实。      
于是你又说"可以自己写程序，把数据读出来，然后自己实现算法处理"，没错，但这个过程仍然对新手非常非常不友好。     

# 解决方案
于是针对这个问题，本系统应运而生，拥有和MySql类似的库表设计架构，同时拥有Spark的复杂数据运算接口，让新手也可以很方便的对数据进行读写、运算，
而不用关心诸如文件操作、内存溢出、迭代运算、数据缓存、数据媒介等，只需要完成数据处理的逻辑即可，其他的操作都会由引擎层完成。      
ps：其实也不仅仅是大数据处理，包括爬虫之类的，凡是涉及存储+计算的，都可以使用本引擎。

# 核心概念及使用
那么直奔主题，介绍一下本引擎的核心设计思路及使用方法。

## 数据模型
本引擎定义了一种叫做"dataObject"的东西，你可以暂时把它理解为数据集，每一个"dataObject"就像MySql中的一张表一样，它是本引擎中的一等公民，
你对数据的一切操作都基于它所提供的API。那么如何获取一个"dataObject"呢？它的唯一入口是通过引擎实例的create及衍生方法，如下面的示例代码。
``` java
    //我们首先定义一个普通的JavaBean
    public class User {

        public User(){

        }

        public User(Integer uid, String name, Integer age) {
            this.uid = uid;
            this.name = name;
            this.age = age;
        }

        public Integer uid;
        public String name;
        public Integer age;

    }

//然后写主函数
    public static void main(String[] args) {
        //创建SkyDB入口
        SkyDB db = SkyDB.getInstance();
        //创建一个数据集，指定版本为1.0
        DataObject dataObject = db.create(User.class, "1.0");
    }
```
至此我们就新建并拿到了一个数据集，即dataObject，此时你可能会好奇版本1.0是什么意思，但请不要着急，后文会详细介绍它的意义及使用。

## 简单的内存插入
上文也说过，引擎中所有对数据的操作都是针对dataObject的，那么在创建dataObject后如何插入数据呢？插入完后，怎么显示插入的数据呢？
直接上代码。
``` java
    public static void main(String[] args) {
        //创建SkyDB入口
        SkyDB db = SkyDB.getInstance();
        //创建一个数据集，指定版本为1.0
        DataObject dataObject = db.create(User.class, "1.0");

        //演示一下逐个插入
        for (int i = 0; i < 10; i++) {
            User user = new User(i, "sky", 18 + i);
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
        //最后再打印一下数据集的基础信息
        dataObject.showInfo();
    }
```

```
输出：
[skyDB info] db starting...
[skyDB info] db start success!
[skyDB] create success: example.po.User_1.0
0_sky_18
1_sky_19
2_sky_20
3_sky_21
4_sky_22
5_sky_23
6_sky_24
7_sky_25
8_sky_26
9_sky_27
----------
|0|sky|18|
|1|sky|19|
|2|sky|20|
|3|sky|21|
|4|sky|22|
|5|sky|23|
|6|sky|24|
|7|sky|25|
|8|sky|26|
|9|sky|27|
----------
{"tableName":"1.0","pageNum":1,"recordNum":10,"types":["INT","STRING","INT"],"columnNames":["uid","name","age"]}
```

## 何为页表
在演示简单插入数据的示例中，示例程序从库里取出了"第一个页表"，并打印出了页表里的数据，那么何为页表？     
简单点说，页表就是引擎层读写数据的最小的长度单元，为16KB，这点在设计上和MySql等主流数据库是一致的，只不过相对于它们，本引擎将这种针对
页表的读写能力直接暴露给开发者，让开发者可以基于此实现一些很骚的操作，而不像SQL的语法那样限制特别多。

## 文件读取、批量插入
如果说前边的都是些开胃菜，那么从这里开始就是一个开发者非常关心的东西了，如何从文件中读取数据？如何在内存中，通过代码执行高效的批量数据插入？
同样的，我们直接看代码。

``` java

    public static void main(String[] args) {
        //创建SkyDB入口
        SkyDB db = SkyDB.getInstance();
        //创建一个数据集，指定版本为1.1，不和1.0冲突
        DataObject dataObject = db.create(User.class, "1.1");
        //指定文件，并使用默认的解析器
        dataObject.fromFile("/Users/hqt/Desktop/data.text", new SimpleParser(" "));
        //打印一下数据
        ConsoleUtil.show(dataObject.getPage(1));
    }

    /**
     * 内置的一个简单的文件解析器
     */
    public class SimpleParser implements Parser {

        public SimpleParser(String separator) {
            this.separator = separator;
        }

        /**
        * 代表 line 分隔符格式的结构 比如：
        * id name age
        * -----------以下为文本内的数据，则在构造parser的时候传入" "即可
        * 1 sky 23
        * 2 sky 23
        */
        private String separator;

        @Override
        public List<String> doParser(String line, ParseContext context) {
            return Arrays.asList(line.split(separator));
        }

    }
```
代码并不复杂，我就不多解释了，如果你的文件格式是其他的的类型，那么只需要实现Parser接口，重写doParser方法即可。      
同样的，批量插入的代码也不就解释了，很容易理解。

``` java
    public static void main(String[] args) {
        //创建db实例
        SkyDB skyDB = SkyDB.getInstance();
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
```

## 如何对数据进行运算
通过上文提到的功能，相信你已经可以创建数据集并把自己需要的数据写入引擎了，那么接下来就开始进行数据的运算，废话不多说先看代码。
``` java
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        //创建SkyDB入口
        SkyDB db = SkyDB.getInstance();
        //创建一个数据集，指定版本为1.0
        DataObject dataObject = db.getOrCreate(User.class, "1.0");

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
                System.out.println("已经计算完了一页的数据");
            }

            @Override
            public void afterAll(ComputeContext context) {
                System.out.println("所有的数据都已经处理完了,打印一下结果：");
                ConsoleUtil.show(context.getResultList());
            }
        });
    }
```
可以看到，计算数据的核心为dataObject的doCompute方法，而开发者也仅仅需要实现Computer接口的一系列方法，引擎就会自动帮我们执行计算了。      
在这背后，引擎会将数据从文件中读出来，将每条数据转成user对象，传入并调用doCompute方法，即开发者实现的方法，供开发者对数据进行处理。      
这样，开发者就不需要关心那么多关于文件操作、数据操作之类的问题了，只需要关心自己拿到了一条数据后应该怎么办即可。      
那么你可能会问，我处理了单条数据后是需要和其他数据做聚合的，是有关联的，怎么做？很简单，看到ComputeContext了吗？塞进去即可！

## 关于版本号
在看完计算示例后，你应该差不多可以理解它存在的意义了，没错！你可以随时随意地创建不同的dataObject，并在任何时机，包括doCompute方法里，对数据进行
任何操作，比如上文的计算示例中，直接将age为偶数的插入上下文，最终打印，而你完全可以把它改成插入一个新的版本为1.1的dataObject，它就会立刻同步进文件，
并且之后你也可以基于它再做任何操作，可见，自由度是非常之高的。

# 一个真实的项目
可以看看example包下边的paperAnalysis的那个示例，很完整了。