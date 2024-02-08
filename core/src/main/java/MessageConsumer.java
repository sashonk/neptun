import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.net.InetAddress;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class MessageConsumer {

    public static void main(String[] argc) {

        KafkaConsumer<String, String> consumer;
        try {

            String groupId = "my-consumer-app";
            Properties config = new Properties();
            //config.put("client.id", InetAddress.getLocalHost().getHostName());
            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
            config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            //config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            consumer = new KafkaConsumer<>(config, new StringDeserializer(), new StringDeserializer());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        consumer.subscribe(Collections.singleton("neptun-events"));
        System.out.println("wait for events");
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records){

                System.out.println(record.key() + " " + record.value());
                //log.info("Key: " + record.key() + ", Value: " + record.value());
                ///log.info("Partition: " + record.partition() + ", Offset:" + record.offset());
            }
        }

        //consumer.close();

    }
}
