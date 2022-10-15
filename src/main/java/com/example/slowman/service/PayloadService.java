package com.example.slowman.service;


import static com.example.slowman.constants.RedisKey.PAYLOAD;
import static com.example.slowman.constants.Topic.MESSAGE_PAYLOAD;

import com.example.slowman.basicrepository.ReactiveRedisRepository;
import com.example.slowman.dto.PayloadDTO;
import com.example.slowman.model.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PayloadService {
    private final ReactiveRedisRepository reactiveRedisRepository;
    private final ChannelTopic payloadTopic;

    public Mono<Payload> save(PayloadDTO dto) {
        Payload payload = new Payload(dto); // refactory 나중에
        return reactiveRedisRepository.save(PAYLOAD,payload.getId(),payload);
    }

    public Mono<Payload> get(String id) {
        return reactiveRedisRepository.get(PAYLOAD,id,Payload.class);
    }


    public Flux<Payload> get() {
        return reactiveRedisRepository.get(PAYLOAD,Payload.class);
    }

    public Mono<Long> publish(PayloadDTO dto) {
        return Mono.fromCallable(() -> new Payload(dto))
                .flatMap(d-> reactiveRedisRepository.pub(payloadTopic, d));
    }

}
