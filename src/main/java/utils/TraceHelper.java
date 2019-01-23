package utils;

import filter.PandoraTraceFilter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 获取调用栈
 * 
 * @author
 *
 */
public class TraceHelper {

    private static Method getStackTraceElement;
    private static Method getStackTraceDepth;
    private static int DEPTH=20;
    private static filter.Filter pandorafiFilter=new PandoraTraceFilter();

    static {
        try {
            getStackTraceElement = Throwable.class.getDeclaredMethod("getStackTraceElement",
                int.class);
            getStackTraceElement.setAccessible(true);

            getStackTraceDepth = Throwable.class.getDeclaredMethod("getStackTraceDepth");
            getStackTraceDepth.setAccessible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTrace() {

        try {
            Throwable throwable = new Throwable();
            int depth = (Integer)getStackTraceDepth.invoke(throwable);
        
            String placeholder = "";
          
            List<StackTraceElement> elements=new ArrayList<StackTraceElement>();
            for (int i = depth-1 ; i >= 0; i--) {               
                StackTraceElement element = (StackTraceElement)getStackTraceElement.invoke(throwable, i);
                String clsName=element.getClassName();
                if (!pandorafiFilter.doFilter(clsName)) {
                    elements.add(element);
                } 
            }
 
            StringBuilder builder = new StringBuilder();
          
            for (int i =0; i <  elements.size(); i++) {        
                StackTraceElement element=elements.get(i);
                String trace = String.format("%s%s.%s(%s:%s)", placeholder, element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
                placeholder += " ";
                builder.append(trace);
                builder.append("\r\n");
     
            }
            return builder.toString();

        } catch (Exception e) {

            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(TraceHelper.getTrace());
    }
}
