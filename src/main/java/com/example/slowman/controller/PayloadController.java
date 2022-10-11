package com.example.slowman.controller;

import com.example.slowman.basicrepository.ReactiveRedisRepository;
import com.example.slowman.model.Payload;
import com.example.slowman.rediskey.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PayloadController {
    private final ReactiveRedisRepository reactiveRedisRepository;

    @PostMapping("/payload")
    public Mono<Payload> save(@RequestBody Payload payload) {
        payload.setCreatedAt(LocalDateTime.now());
        return reactiveRedisRepository.save(RedisKey.Payload,payload.getId(),payload);
    }

    @GetMapping("/payload/{id}")
    public Mono<Payload> findOne(@PathVariable String id) {
        return reactiveRedisRepository.get(RedisKey.Payload,id,Payload.class);
    }

    @GetMapping("/payload")
    public Flux<Payload> findAll() {
        return reactiveRedisRepository.get(RedisKey.Payload,Payload.class);
    }
}
