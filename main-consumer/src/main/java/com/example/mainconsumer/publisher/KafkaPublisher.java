package com.example.mainconsumer.publisher;

import com.example.mainconsumer.dto.MyDto;
import com.example.mainconsumer.enums.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaPublisher {

    @Value(value = "${spring.kafka.topic}")
    private String topic;

    @Value(value = "${spring.kafka.message_topic}")
    private String messageTopic;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(MyDto dto) {
        try {
            String message = objectMapper.writeValueAsString(dto);
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, dto.getEventType().name(), message);
            getHeaders(dto.getEventType()).forEach((k, v) -> {
                producerRecord.headers().add(k, v.getBytes(StandardCharsets.UTF_16));
            });
            kafkaTemplate.send(producerRecord);
            log.info("Message sent {}", dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(MyDto dto){
        try{
            String payload = objectMapper.writeValueAsString(dto);
            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.KEY, dto.getEventType().getEvent())
                    .setHeader("LINH", "LINH TEST")
                    .setHeader(KafkaHeaders.TOPIC, messageTopic)
                    .build();

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
            // Success case
           future.whenComplete((rs, ex) -> {
               if (ex != null){
                   ex.printStackTrace();
               }else{
                   System.out.println("okokokokokok rs ------> "+rs);
               }
           });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map<String, String> getHeaders(EventType eventType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("eventType", eventType.getEvent());
        return headers;
    }
}
