package metaq.producer;

import java.io.Serializable;
import java.util.List;

/**
 * 采集慢查询信息
 * @author jun7.lj7
 *
 */
public class SqlDto implements Serializable {

    
    private static final long serialVersionUID = 7832157771469590728L;

    // 执行sql
    private String sql;
    //未解析
    private String originalSql; 

    // uae应用id
    private Integer appId;   

    //应用
    private String appName;
    //标签环境
    private String label;
    // 生成时间
    private Long createdTime;
    // 执行耗时
    private Long costedTime;
    // 数据库实例信息
    private DbInfo dbInfo;
    
    //调用栈
    private String trace;
    
    
    public SqlDto(String sql, String originalSql, Integer appId,String appName, String env, Long createdTime, Long costedTime,String trace, DbInfo dbInfo) {
        super();
        this.sql = sql;
        this.originalSql = originalSql;

        this.appId = appId;
        this.label = env;
        this.createdTime = createdTime;
        this.costedTime = costedTime;
        this.dbInfo = dbInfo;
        this.trace = trace;
        this.appName=appName;
    }

    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }

    
    
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }



    public String getOriginalSql() {
        return originalSql;
    }

    public void setOriginalSql(String originalSql) {
        this.originalSql = originalSql;
    }



    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }



    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getCostedTime() {
        return costedTime;
    }

    public void setCostedTime(Long costedTime) {
        this.costedTime = costedTime;
    }

    public DbInfo getDbInfo() {
        return dbInfo;
    }

    public void setDbInfo(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
    }
    

}
