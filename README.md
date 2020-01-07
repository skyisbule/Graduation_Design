<h1 align="center">Welcome to skydb 👋</h1>
<p>
  <img src="https://img.shields.io/badge/version-1.0.0-blue.svg?cacheSeconds=2592000" />
  <img src="https://img.shields.io/badge/jdk-%3E%3D1.8-blue.svg" />
  <a href="https://github.com/kefranabg/readme-md-generator#readme">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" target="_blank" />
  </a>
  <a href="https://github.com/kefranabg/readme-md-generator/graphs/commit-activity">
    <img alt="Maintenance" src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" target="_blank" />
  </a>
  <a href="https://github.com/kefranabg/readme-md-generator/blob/master/LICENSE">
    <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg" target="_blank" />
  </a>
</p>

> 0.0.1

### 🏠 [Homepage](https://github.com/skyisbule/Graduation_Design)

## 愿景
假设给你一个.sql文档，里边有学生的选课信息，然后你让一个编程初学者，去尝试从这份原始数据分析出点东西出来，他会怎么做？      
熟练地打开mysql，建个表（或许连索引是啥都不太清楚夏吉尔建一个），再熟练地把文件拖进去，最后对着sql页面一脸懵逼,因为复杂查询对于sql编写者来说，相当蛋疼...        
且在很多场景下一旦牵扯到运算,尤其是A->B->C那种,对于新手来说完全搞不定啊，有些人可能连视图是啥头没听过。     
你可能会说，"那我为什么不用hadoop+spark？不用pandas？那么多专业的分析软件和框架？"       
首先，搭建太麻烦了，能直接劝退一大波新手，同时很多高级的特性也不适合让你去做"小"数据的分析，我就让你统计下挂科率，找一下到课率和期末成绩的关系，你给我搞这个？            
所以我希望能有一个东西，让我可以直接上手，在我不需要写什么代码的情况下管理我的数据文件，让我可以方便的去读数据，可以方便地去处理数据，甚至还能把中间数据也一并交给它去管理，
且完全不需要关心文件读写，内存管理等问题，我只需要知道如何处理数据即可。      
所以他虽然在最终表现上会很**靠近**spark的模式，但它真的要足够易用，足够面向新手，且效率非常高。


## What is the project
This project is my graduation project. Its main function is to process big data. Traditionally, beginners of GB-level data tend to choose MYSQL or SPARK, but the insertion and analysis of MYSQL will become extremely slow when the data volume reaches 10 million levels. Very useful, although SPARK can deal with large-scale data and support clustering, it is customized for data analysis professionals and is too high. Configuration and learning threshold make it difficult for non-computer professionals or amateurs to control, and inevitably let novices say, "I just want to analyze the data, but it's so difficult."      

So skydb came into being.
      
I did targeted optimization for a small amount of data (GB level), so it has a very high insertion and query efficiency, and support clustering, easy to install and configure. It is located in MYSQL and SPARK, and is very friendly to beginners or amateur data analysts.

## Prerequisites

- jdk >= 1.8

## Install

```sh
mvn package
```

## Usage

```sh
jdk1.8 or newer version
develop by idea
```

## Run tests

```sh
mvn test
```

## Author

👤 **skyisbule**

* Twitter: [@skyisbule](https://twitter.com/skyisbule)
* Github: [@skyisbule](https://github.com/skyisbule)

## Develop log
目前的打算是主分支专门做存储+计算引擎，另一个分支（transaction）针对小数据场景做好锁、事务及优化。

## 🤝 Contributing

Contributions, issues and feature requests are welcome!

## Show your support

Give a ⭐️ if this project helped you!

## 📝 License

Copyright © 2019 [skyisbule](https://github.com/skyisbule).<br />
This project is MIT licensed.
