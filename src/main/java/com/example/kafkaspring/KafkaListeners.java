package com.example.kafkaspring;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaListeners {

    @KafkaListener(
            topics = "linh",
            groupId = "groupId"
    )
    void listener(String data){
        System.out.println("Listener received : "+data+" 🥑");

    }
}
