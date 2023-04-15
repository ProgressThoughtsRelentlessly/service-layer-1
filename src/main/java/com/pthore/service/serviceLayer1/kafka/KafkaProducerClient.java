package com.pthore.service.serviceLayer1.kafka;

import java.time.Duration;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@DependsOn(value = "kafka_producer")   // PROBABLY THIS HANDLES THE ORDER OF DESTRUCTION DURING SHUTDOWN.
public class KafkaProducerClient implements DisposableBean {

//	@PreDestroy
	@Override
	public void destroy() throws Exception {
		kafkaProducer.close(Duration.ofSeconds(1L));
	}
	
	@Autowired
	@Qualifier(value= "kafka_producer")
	private KafkaProducer<String, String> kafkaProducer;
	
	@Autowired
	private ObjectMapper mapper;
	
	public <T> void send(String topicName, String key, T messageObject, Class<?> clazz) throws JsonProcessingException {
		String jsonSerializedString = mapper.writeValueAsString(messageObject);
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, key, jsonSerializedString);
		kafkaProducer.send(record);
	}
	
}
