package com.example.kafkaspring;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/message")
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public MessageController(KafkaTemplate<String, String> template){
        this.kafkaTemplate = template;
    }

    @PostMapping
    public void publish(@RequestBody MessageRequest request){
        kafkaTemplate.send("linh", request.getData());
    }
}
