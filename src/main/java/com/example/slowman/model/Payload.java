package com.example.slowman.model;

import com.example.slowman.dto.PayloadDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.util.Random;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@RedisHash("Payload")
public class Payload {

    @Id
    private String id;

    private String message;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    public Payload(PayloadDTO dto) {
        this.id = UUID.randomUUID().toString();
        this.message = dto.getMessage();
        this.createdAt = LocalDateTime.now();
    }
}
