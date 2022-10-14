package com.example.slowman.messaging;


import static com.example.slowman.constants.Topic.MESSAGE_PAYLOAD;

import com.example.slowman.basicrepository.ReactiveRedisRepository;
import com.example.slowman.model.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Slf4j
@RequiredArgsConstructor
public class RedisMessagePublisher {
    private final ReactiveRedisRepository reactiveRedisRepository;

    public Mono<Long> publishPayload(Payload payload) {
        return reactiveRedisRepository.pub(MESSAGE_PAYLOAD,payload)
            .doOnSuccess(aLong -> log.debug("OK!"))
            .doOnError(throwable -> log.error("Error publishing message.", throwable));
    }
}
