package ru.asocial.games.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

public class Neptun extends Game implements IGame{

	private Map<String, BaseScreen> screens = new HashMap<>();

	private Graphics.DisplayMode displayMode;

	private ResourcesManager resourcesManager;

	private KafkaProducer<String, String> kafkaProducer;

	private IMessagingService messagingService;

	@Override
	public ResourcesManager getResourcesManager() {
		return resourcesManager;
	}

	@Override
	public void setResourceManager(ResourcesManager resourcesManager) {
		this.resourcesManager = resourcesManager;
	}

	public Neptun(Graphics.DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public Neptun() {

	}

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(1200, 900);

		//Gdx.graphics.setWindowedMode(1000, 1000);

		setScreen(new SplashScreen(this));

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

		messagingService = (tag, message) -> {
			try {
				ProducerRecord<String, String> record = new ProducerRecord<>("neptun-events", 0, tag, message);
				kafkaProducer.send(record);


			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		};
	}

	@Override
	public void dispose() {
		if (resourcesManager != null) {
			resourcesManager.dispose();
		}

		for (Screen screen : screens.values()) {
			screen.dispose();
		}

		kafkaProducer.close();

		Gdx.app.exit();
	}

	@Override
	public void onLoad() {
		Screen exaustedScreen = super.getScreen();
		exaustedScreen.dispose();
		setScreen(new GameScreen(this));
	}

	@Override
	public IMessagingService getMessagingService() {
		return messagingService;
	}

}
