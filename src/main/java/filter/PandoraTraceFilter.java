package filter;

import java.util.HashSet;
import java.util.Set;

public class PandoraTraceFilter implements Filter{

    /**
     * 过滤pk
     */
    private static Set<String> excludePackage = new HashSet<String>();
    
    
    static {
        // 默认过滤Package
        excludePackage.add("java.");// 包含javax
        excludePackage.add("sun.");// 包含sunw
        excludePackage.add("com.sun");
        excludePackage.add("org");// 包含org/xml org/jboss org/apache/xerces org/objectweb/asm         
    }
    
    @Override
    public  boolean doFilter(String className) {
       
        String icaseName = className.toLowerCase();
        for (String v : excludePackage) {
            if (icaseName.startsWith(v)) {
                return true;
            }
        }
        return false;
    }
    
    
 

}
