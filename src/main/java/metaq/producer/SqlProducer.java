package metaq.producer;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;


public class SqlProducer {

    public static final String TOPIC = "MBAPPE";

    private static volatile SqlProducer producer = null;

    private DefaultMQProducer metaProducer;

    static {

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                SqlProducer.get().destory();
            }
        }));
    }

    private SqlProducer() {

        metaProducer = new DefaultMQProducer("MBAPPE");
        try {
            metaProducer.start();
        } catch (MQClientException e) {

            e.printStackTrace();
        }
    }

    public static SqlProducer get() {
        if (producer == null) {
            synchronized (SqlProducer.class) {
                if (producer == null) {
                    producer = new SqlProducer();
                }
            }
        }
        return producer;
    }

    public void send(Object msg) {
        try {
            Message message = new Message();
            message.setBody(JSONObject.toJSONString(msg).getBytes("utf-8"));
            message.setTopic(TOPIC);
            String tag =System.getProperty("APPNAME");
            message.setTags(tag);
            SendResult sendResult = metaProducer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destory() {

        if (metaProducer != null) {
            metaProducer.shutdown();
        }

    }
    public static void main(String[] args) {
        System.out.println("1111111111");
        SqlProducer.get().send("11111111111");
    }

}
