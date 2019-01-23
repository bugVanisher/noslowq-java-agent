package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class MbappeLogger {

    private static PrintStream ps = System.err;
    static {
        try {
            File log = new File(System.getProperty("user.home") + File.separator + "logs" + File.separator
                    + "mbappe-agent" + File.separator + "mbappe-agent.log");
            if (!log.exists()) {
                log.getParentFile().mkdir();
                log.createNewFile();
            }
            ps = new PrintStream(new FileOutputStream(log, true));
        } catch (Throwable t) {
            t.printStackTrace(ps);
        }
    }
    
    
    public static void info(String msg){
        log("INFO",msg);
    }
    
    public static void error(String msg){
        log("ERROR",msg);
    }
    
    public static void log(String type ,String msg){
        String message=String.format("%s %s  %s", DateUtil.getCurrectData(),type,msg);
        ps.println(message);
    }
}
