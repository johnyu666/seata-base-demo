# 一个单体的三个服务，分别使用了三个数据源，完成事务的AT模式
## 1. 首选需要启动seata，方式如下：
在seata1.1.zip 下的readme.MD 中有说明
本案例虽然没有使用nacos,但演示了如何将TC Server 注册到nacos中
## 2. 业务数据初始化脚本：
resource/data.sql

## 3. test/test.http中有完整的e2e测试
