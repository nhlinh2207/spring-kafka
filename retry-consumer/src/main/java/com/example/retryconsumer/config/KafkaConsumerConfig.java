package com.example.retryconsumer.config;

import com.example.retryconsumer.entity.FailedMessage;
import com.example.retryconsumer.repository.IFailMessageRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.consumer.key-deserializer}")
    private String consumerKeyDeserializer;
    @Value(value = "${spring.kafka.consumer.value-deserializer}")
    private String consumerValueDeserializer;
    @Value(value = "${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    private final IFailMessageRepo failMessageRepo;

    KafkaConsumerConfig(IFailMessageRepo failMessageRepo) {
        this.failMessageRepo = failMessageRepo;
    }

    private Map<String, String> consumerProps() {
        Map<String, String> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ByteArrayDeserializer.class.getName());
        return props;
    }

    @Bean("kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> concurrentKafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        concurrentKafkaListenerContainerFactory.setCommonErrorHandler(getDefaultErrorHandler());
        return concurrentKafkaListenerContainerFactory;
    }

    private DefaultErrorHandler getDefaultErrorHandler() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new DefaultErrorHandler((record, exception) -> {
            FailedMessage failedMessageEntity = new FailedMessage();
            try {
                failedMessageEntity.setMessage(objectMapper.writeValueAsString(record.value()));
                failedMessageEntity.setException(exception.getClass().toString());
                failedMessageEntity.setTopic(record.topic());
                failedMessageEntity.setConsumerOffset(record.offset());
                failMessageRepo.save(failedMessageEntity);
                log.error("Saved the failed message to db {}", failedMessageEntity);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, new FixedBackOff(10000L, 2L)); // Thử lại tối đa 2 lần, mỗi lần cách nhau 10 giây trước khi lưu vào dâtbase
    }
}
