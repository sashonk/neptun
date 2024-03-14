package ru.asocial.neptun.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import ru.asocial.games.core.IMessageDeliveryService;
import ru.asocial.games.core.Message;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class KafkaConsumer implements IMessageDeliveryService {
    private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;
    private ExecutorService executorService;

    private volatile boolean initialized = false;

    private Set<Listener> listeners = new CopyOnWriteArraySet<>();
    public KafkaConsumer() {
        try {
            String groupId = "my-consumer-app";
            Properties config = new Properties();
            //config.put("client.id", InetAddress.getLocalHost().getHostName());
            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
            config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            //config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(config, new StringDeserializer(), new StringDeserializer());

            executorService = Executors.newSingleThreadExecutor();
        }
        catch (Exception ex) {
            if (executorService != null) {
                executorService.shutdown();
            }
            if (consumer != null) {
                consumer.close();
            }
            throw new RuntimeException(ex);
        }

        consumer.subscribe(Collections.singleton("neptun-events"));
        initialized = true;
    }

    public void close(){
        if (consumer != null) {
            consumer.close();
        }
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void startDeliveryAsync() {
        executorService.execute(() -> {
            while (true) {
                if (!initialized) {
                    continue;
                }

                Logger.getLogger("kafka consumer").info("poll");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    Logger.getLogger("kafka consumer").info("some records retrieved");
                    for (Listener listener : listeners) {
                        Message m = new Message();
                        m.setContent(record.value());
                        try {
                            listener.onMessage(m);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }
}

