package com.example.mainconsumer.controller;

import com.example.mainconsumer.dto.MyDto;
import com.example.mainconsumer.publisher.KafkaPublisher;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PublisherController {

    @Autowired
    KafkaPublisher publisher;

    @PostMapping(value = "/publish")
    public void publish(@RequestBody @Valid MyDto dto) {
        log.info("Publishing the event {}", dto);
        publisher.publish(dto);
    }

    @PostMapping(value = "/publish-message")
    public void publishMessage(@RequestBody @Valid MyDto dto) {
        log.info("Publishing the event {}", dto);
        publisher.publishMessage(dto);
    }

    @GetMapping(value = "/hello")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("okokoko");
    }
}
