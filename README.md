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
# 下载最新版本的JVM-SANDBOX
wget http://ompc.oss-cn-hangzhou.aliyuncs.com/jvm-sandbox/release/sandbox-stable-bin.zip

# 解压
unzip sandbox-stable-bin.zip

# 应用启动参数添加javaagent
-javaagent: ${jvm-sanbox路径}/sandbox-agent.jar -DLABEL=标识符 -DAPPNAME=应用名 -Drocketmq.namesrv.domain=${namesever地址}

```

