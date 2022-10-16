package com.example.publisher.service;

import com.example.core.dto.PayloadDTO;
import com.example.core.model.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayloadPubService {
    private final ReactiveRedisOperations<String,Object> redisOperations;
    private static final String PAYLOAD = "payload";

    public Mono<PayloadDTO> save(PayloadDTO dto) {
        return Mono.just(new Payload(dto))
                .flatMap(
                        payload -> redisOperations.opsForHash().put(PAYLOAD,payload.getId(),payload).map(p -> dto)
                );
    }
}
