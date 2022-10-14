package com.example.slowman.service;

import com.example.slowman.basicrepository.ReactiveRedisRepository;
import com.example.slowman.dto.PayloadDTO;
import com.example.slowman.model.Payload;
import com.example.slowman.rediskey.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PayloadService {
    private final ReactiveRedisRepository reactiveRedisRepository;

    public Mono<Payload> save(PayloadDTO dto) {
        Payload payload = new Payload(dto); // refactory 나중에
        return reactiveRedisRepository.save(RedisKey.Payload,payload.getId(),payload);
    }

    public Mono<Payload> get(String id) {
        return reactiveRedisRepository.get(RedisKey.Payload,id,Payload.class);
    }


    public Flux<Payload> get() {
        return reactiveRedisRepository.get(RedisKey.Payload,Payload.class);
    }

}
