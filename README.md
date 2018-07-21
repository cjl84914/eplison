#geetion_predict

epsilon是第五维度，具有预测未来的意思。

## 项目整合框架:
springboot
spark
xxl-job
mysql
redis

## 项目以文章分类为例：
数据下载地址
https://pan.baidu.com/s/1seb3KCBtCu2W5_WIrrRXpg

## 运行步骤：
- 运行redis
- 运行mysql，创建数据库epsilon，配置查看application.xml（目前没有表结构，数据库为日后持久化做准备）
- 下载数据，修改SogouTrainHandler的训练数据文件的路径
- 运行EpsilonServerApplicaitonTests训练模型
- 运行EpsilonCoreApplication
- 打开http://localhost:8081/swagger-ui.html

文章标签

{"vals":["房产","体育","时政","财经","娱乐","游戏","科技","家居","教育","时尚"],"type":"nominal","name":"label"}

