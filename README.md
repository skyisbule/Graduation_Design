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


# 关于本分支
本分支的主要目的为实现一个小型数据库（针对小数据，但提供完整的事务管理），类似于sqlite，但会对java做针对性的优化。

> 0.0.1

### 🏠 [Homepage](https://github.com/skyisbule/Graduation_Design)

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
