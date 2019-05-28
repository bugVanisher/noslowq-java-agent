package filter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gannicus
 */

public class CommonFilter implements Filter{

    /**
     * 过滤
     */
    private static Set<String> excludePackage = new HashSet<String>();

    // 默认过滤Package
    static {
        // 包含javax
        excludePackage.add("java.");
        // 包含sun
        excludePackage.add("sun.");
        excludePackage.add("com.sun.");
        excludePackage.add("utils.TraceHelper.");
        // 包含org/xml org/jboss org/apache/xerces org/objectweb/asm
        excludePackage.add("org.");
    }
    
    @Override
    public  boolean match(String className) {
       
        String icaseName = className.toLowerCase();
        for (String v : excludePackage) {
            if (icaseName.startsWith(v)) {
                return true;
            }
        }
        return false;
    }
    
    
 

}
