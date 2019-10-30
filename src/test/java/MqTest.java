import com.google.gson.Gson;
import metaq.producer.DbInfo;
import metaq.producer.SqlDto;
import metaq.producer.SqlProducer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;
import utils.TraceHelper;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author : gannicus at 2019/1/26
 */
public class MqTest {

    private static final Gson GSON = new Gson();

    @Test
    public void testProduce() {
        SqlDto sqlDto = new SqlDto();
        sqlDto.setAppId(1);
        sqlDto.setAppName("agent-test");
        sqlDto.setCostedTime(23L);
        DbInfo dbInfo = new DbInfo();
        dbInfo.setmHost("127.0.0.1");
        dbInfo.setmPort("3306");
        dbInfo.setDb("mbappe");
        dbInfo.setmUserName("gannicus");
        dbInfo.setmPwd("123456");
        sqlDto.setDbInfo(dbInfo);
        sqlDto.setLabel("docker");
        sqlDto.setOriginalSql("select * from new_sql_explain;");
        sqlDto.setSql("select * from new_sql_explain;");
        sqlDto.setTrace(TraceHelper.getTrace());
        sqlDto.setCreatedTime(System.currentTimeMillis());
        sqlDto.setCostedTime(21L);
        SqlProducer.get().send(sqlDto);
    }

    public static void main(String[] args) throws MQClientException {
        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("mbappe");
        consumer.setNamesrvAddr("localhost:9876");
        // Subscribe one more topics to consume.
        consumer.subscribe("MBAPPE", "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                final Charset UTF_8 = Charset.forName("UTF-8");
                for (MessageExt msg : msgs) {
                    String receivedMsg = new String(msg.getBody(), UTF_8);
//                    SqlDto sqlDto = GSON.fromJson(receivedMsg, SqlDto.class);
                    System.out.printf("Receive msg:%s", receivedMsg);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }

}
