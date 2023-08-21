package com.example.kafkaspring;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    private String data;
}
