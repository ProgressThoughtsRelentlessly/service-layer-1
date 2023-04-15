package com.pthore.service.serviceLayer1.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.pthore.service.serviceLayer1.utils.AppConstants;

@Configuration
@Scope(scopeName = "singleton")
public class KafkaProducerConfigs {

	public Properties kafkaProducerProperties() {
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConstants.KAFKA.KAFKA_BOOTSTRAP_SERVER);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		// safe producer Idempotence
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
		
		// high throughput at the expense of a blit of latency and CPU usage.
		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // lz4, gzip
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
		return properties;
	}
	
	
	@Bean(name = "kafka_producer")
	public KafkaProducer<String, String> getKafkaProducer() {
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProducerProperties());
		return kafkaProducer;
	}
	
	
	
}
