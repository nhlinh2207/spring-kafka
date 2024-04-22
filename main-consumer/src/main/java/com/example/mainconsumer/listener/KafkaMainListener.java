package com.example.mainconsumer.listener;

import com.example.mainconsumer.dto.MyDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaMainListener {

    @KafkaListener(
            topics = "${spring.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, MyDto> consumerRecord) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());

        if(consumerRecord.offset() % 2 != 0)
            throw new RuntimeException("This is really odd.");

        log.info("Finished consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());
    }

    @KafkaListener(
            topics = "${spring.kafka.message_topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenMessage(@Headers MessageHeaders headers, String message) {
        String header1 = (String)headers.get("LINH");
        String header2= (String)headers.get(KafkaHeaders.RECEIVED_KEY);
        System.out.println("Header1 : " + header1);
        System.out.println("Header2 : " + header2);
        System.out.println("Message: "+message);
    }
}
