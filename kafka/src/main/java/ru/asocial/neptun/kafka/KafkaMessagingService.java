package ru.asocial.neptun.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.asocial.games.core.IMessageService;

import java.net.InetAddress;
import java.util.List;
import java.util.Properties;

public class KafkaMessagingService implements IMessageService {

    private KafkaProducer kafkaProducer;

    public KafkaMessagingService() {
        try {
            Properties config = new Properties();
            config.put("client.id", InetAddress.getLocalHost().getHostName());
            config.put("bootstrap.servers", "localhost:9093");
            kafkaProducer = new KafkaProducer<>(config, new StringSerializer(), new StringSerializer());

            List<PartitionInfo> partitions = kafkaProducer.partitionsFor("neptun-events");
            for (PartitionInfo partitionInfo : partitions) {
                System.out.println("Partition: " + partitionInfo.partition() + partitionInfo.topic());
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void writeMessage(String tag, String message) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("neptun-events", 0, tag, message);
            kafkaProducer.send(record);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
    }
}
