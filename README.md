# newsql-agent

> 慢查询系统客户端, 采集基于jdbc的java应用产生sql的信息

## Build Setup

```bash
# Clone project
git clone git@github.com:gannicus-yu/newsql-agent.git

# package
mvn assemly:assemly

# archive the jar file
ls /target/newsql-agent-${version}-SNAPSHOT.jar

```


## Run

```bash
# 下载最新版本的JVM-SANDBOX,https://github.com/alibaba/jvm-sandbox
wget http://ompc.oss-cn-hangzhou.aliyuncs.com/jvm-sandbox/release/sandbox-stable-bin.zip

# 找个地方解压,得到${jvm-sanbox路径}
unzip sandbox-stable-bin.zip

# 添加自定义module, newsql-agent
将newsql-agent-${version}-SNAPSHOT.jar添加到${jvm-sanbox路径}/example

# 修改${jvm-sanbox路径}/cfg/sandbox.properties
system_module = ${jvm-sanbox路径}/module;
user_module = ${jvm-sanbox路径}/example;

# 应用启动参数添加javaagent
-javaagent: ${jvm-sanbox路径}/sandbox-agent.jar -DLABEL=标识符 -DAPPNAME=应用名 -Drocketmq.namesrv.domain=${namesever地址}
```

## Check
```bash
# 查看${user.home}/logs/sandbox/sandbox.log
# 类似以下输出则说明加载成功,agent已成功注入应用
2019-03-02 15:09:39 default INFO  active module, module=mbappe-mysql-logger;class=com.alibaba.jvm.sandbox.module.JdbcLoggerModule;module-jar=/Users/gannicus/Downloads/sandbox/example/newsql-agent-1.0.jar;
2019-03-02 15:09:39 default INFO  firing module-event: event=ACTIVE;module=mbappe-mysql-logger;
2019-03-02 15:09:39 default INFO  module[id:mbappe-mysql-logger;class:class com.alibaba.jvm.sandbox.module.JdbcLoggerModule;] watch[id:1000] found classes:0 in loaded for watch(ing).
2019-03-02 15:09:39 default INFO  activated listener[id=1;target=com.alibaba.jvm.sandbox.api.listener.ext.AdviceAdapterListener@f5ac9e4;] event=BEFORE,RETURN,THROWS,IMMEDIATELY_RETURN,IMMEDIATELY_THROWS
2019-03-02 15:09:39 default INFO  firing module-event: event=LOAD_COMPLETED;module=mbappe-mysql-logger;
2019-03-02 15:09:39 default INFO  loaded module-jar completed, loaded 1 module in module-jar=/Users/gannicus/Downloads/sandbox/example/newsql-agent-1.0.jar, modules=[mbappe-mysql-logger]
```
