##Elastic-Jobx - distributed scheduled job solution
[当当Elastic-Job](https://github.com/dangdangdotcom/elastic-job) 

  Elastic-Jobx扩展至当当的Elastic-job，增加如下功能:
  1. 支持jdk1.6;
  2. 管控平台增加任务添加功能；
  3. 指定时间段任务跳过执行功能；
  4. 动态分片策略功能。

  


## Elastic-Jobx扩展功能

### 扩展功能

* **管控平台任务添加功能：** 可在管控平台预先添加任务，方便集中管理。
* ![任务添加功能](https://github.com/artoderk/elastic-jobx/blob/master/elastic-jobx-doc/content/img/console_F01.png)

* **指定时间段任务跳过执行功能：** 在有大促或其它需求时，可提前配置全局或单个任务的暂停时间段。在指定时间段内任务将跳过执行。
* ![任务跳过执功能](https://github.com/artoderk/elastic-jobx/blob/master/elastic-jobx-doc/content/img/console_F02.png)

* **动态分片策略功能：** 可以在管控平台上传分片策略类，动态调整分片策略类。
* ![任务跳过执功能](https://github.com/artoderk/elastic-jobx/blob/master/elastic-jobx-doc/content/img/console_F03.png)

### ZK添加节点
 ![ZK添加节点](https://github.com/artoderk/elastic-jobx/blob/master/elastic-jobx-doc/content/img/zk_01.png)
 
## Quick Start

* **引入maven依赖**

elastic-job已经发布到中央仓库，可以在pom.xml文件中直接引入maven坐标。

```xml
<!-- 引入elastic-job核心模块 -->
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-core</artifactId>
    <version>${lasted.release.version}</version>
</dependency>

<!-- 使用springframework自定义命名空间时引入 -->
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-spring</artifactId>
    <version>${lasted.release.version}</version>
</dependency>
```
* **作业开发**

```java
public class MyElasticJob extends AbstractSimpleElasticJob {
    
    @Override
    public void process(JobExecutionMultipleShardingContext context) {
        // do something by sharding items
    }
}
```

* **作业配置**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:reg="http://www.dangdang.com/schema/ddframe/reg"
    xmlns:job="http://www.dangdang.com/schema/ddframe/job"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://www.dangdang.com/schema/ddframe/reg
                        http://www.dangdang.com/schema/ddframe/reg/reg.xsd
                        http://www.dangdang.com/schema/ddframe/job
                        http://www.dangdang.com/schema/ddframe/job/job.xsd
                        ">
    <!--配置作业注册中心 -->
    <reg:zookeeper id="regCenter" serverLists=" yourhost:2181" namespace="dd-job" baseSleepTimeMilliseconds="1000" maxSleepTimeMilliseconds="3000" maxRetries="3" />

    <!-- 配置作业-->
    <job:bean id="myElasticJob" class="xxx.MyElasticJob" regCenter="regCenter" cron="0/10 * * * * ?"   shardingTotalCount="3" shardingItemParameters="0=A,1=B,2=C" />
</beans>
```
