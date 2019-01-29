package com.alibaba.jvm.sandbox.module;


import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.LoadCompleted;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import metaq.producer.DbInfo;
import metaq.producer.SqlDto;
import metaq.producer.SqlProducer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.kohsuke.MetaInfServices;
import utils.TraceHelper;

import javax.annotation.Resource;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * 基于JDBC的SQL日志拦截
 *
 * @author  gannicus-yu
 */
@MetaInfServices(Module.class)
@Information(id = "mbappe-mysql-sql-intercepter", version = "1.0", author = "gannicus-yu")
public class JdbcLoggerModule implements Module, LoadCompleted {

    private final Logger logger = Logger.getLogger(JdbcLoggerModule.class.getName());

    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Override
    public void loadCompleted() {

        monitorJavaSqlPreparedStatement();
    }


    private void monitorJavaSqlPreparedStatement() {

        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.mysql.jdbc.PreparedStatement")
                .includeSubClasses()
                .onBehavior("fillSendPacket")
                .withParameterTypes(byte[][].class, InputStream[].class, boolean[].class,
                        int[].class)

                .onWatch(new AdviceListener() {

                    @Override
                    public void before(Advice advice) {
                        final String behaviorName = advice.getBehavior().getName();
                    }

                    @Override
                    public void afterReturning(Advice advice) {

                        final String behaviorName = advice.getBehavior().getName();
                        // PreparedStatement.fillSendPacket()
                        if ("fillSendPacket".equals(behaviorName)) {
                            byte[][] parms = (byte[][]) advice.getParameterArray()[0];
                            List<String> parametersList = new ArrayList<String>();
                            for (byte[] x : parms) {
                                parametersList.add(new String(x));
                            }
                            try {
                                String originalSql = (String) FieldUtils.readField(advice.getTarget(), "originalSql", true);
                                Object mySQLConnection = FieldUtils.readField(advice.getTarget(), "connection", true);

                                DbInfo dbInfo = buildDbInfo(mySQLConnection);

                                String buildsql = buildSql(originalSql, parametersList);

                                String appNAME = System.getProperty("APPNAME");
                                String env = System.getProperty("LABEL");
                                if (null == appNAME || null == env) {
                                    logger.warning("APPNAME or LABEL is empty.");
                                    return;
                                }
                                SqlDto sqlDto = new SqlDto(buildsql, originalSql, 0, appNAME, env, System.currentTimeMillis(), 1L, TraceHelper.getTrace(), dbInfo);
                                SqlProducer.get().send(sqlDto);
                            } catch (IllegalAccessException e) {
                                logger.warning(e.getMessage());
                            }

                        }

                    }


                });
    }

    /**
     * 拼接完整的sql
     *
     * @param sql
     * @param sqlParams
     * @return
     */
    private String buildSql(String sql, List<String> sqlParams) {

        // MbappeLogger.log("PreparedStatement buildSql start: " + sql);
        String command = null;
        String[] parms = null;
        try {
            command = sql.replace("?", "%s");
            parms = new String[sqlParams.size()];
            for (int i = 0; i < sqlParams.size(); i++) {
                parms[i] = sqlParams.get(i);
            }
            command = String.format(command, parms);
        } catch (Throwable e) {

            command = sql;
            // logger.error("PreparedStatement buildSql error: " + e.getMessage() + "----" + sqlParams.toString()+"---"+sql);
        }

        // MbappeLogger.log("PreparedStatement buildSql end: " + command);
        return command;
    }


    private DbInfo buildDbInfo(Object connection) {

        DbInfo dbInfo = new DbInfo();
        Properties properties = null;
        String url = "";
        try {
            properties = invokeMethod(connection, "getProperties", null);
            url = invokeMethod(connection, "getURL", null);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dbInfo.setmUserName((String) properties.get("user"));
        dbInfo.setmPwd((String) properties.get("password"));
        // 解析url

        String host = url.substring(url.indexOf("//") + 2, url.lastIndexOf(":"));
        dbInfo.setmHost(parse(host));
        String port = url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf("/"));
        dbInfo.setmPort(port);
        String db = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));
        dbInfo.setDb(db);
        return dbInfo;
    }

    private String parse(String host) {

        if (!ipCheck(host)) {
            try {
                return InetAddress.getByName(host).getHostAddress();
            } catch (UnknownHostException e) {

                e.printStackTrace();
                return host;
            }
        }
        return host;
    }

    /*
     * 泛型转换方法调用
     * 底层使用apache common实现
     */
    private static <T> T invokeMethod(final Object object,
                                      final String methodName,

                                      final Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return (T) MethodUtils.invokeMethod(object, methodName, args);
    }


    private boolean ipCheck(String str) {
        if (str != null && !str.isEmpty()) {
            // 定义正则表达式
            String regex = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";
            // 判断ip地址是否与正则表达式匹配
            if (str.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }

}