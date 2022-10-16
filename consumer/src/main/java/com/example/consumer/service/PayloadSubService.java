package com.example.consumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayloadSubService {

    private final ReactiveRedisOperations<String,Object> redisOperations;

    @Scheduled(fixedDelay = 1000)
    public void subscribe() throws InterruptedException{
        redisOperations.opsForHash().size("payload")
                        .subscribe(o->{
                            log.info("PAYLOAD SIZE = {}",o);
                        });
    }
}
