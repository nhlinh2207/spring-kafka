package com.example.retryconsumer.listener;

import com.example.retryconsumer.dto.MyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@NoArgsConstructor
public class KafkaRetryListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "${spring.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());

        if (consumerRecord.offset() % 2 != 0) throw new IllegalStateException("This is something odd.");

        try {
            MyDto myDto = objectMapper.readValue(consumerRecord.value(), MyDto.class);
            log.info("Finished consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                    consumerRecord.offset(), myDto);
            // do something with the deserialized object
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
