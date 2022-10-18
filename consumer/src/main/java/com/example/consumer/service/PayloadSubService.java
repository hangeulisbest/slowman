package com.example.consumer.service;

import com.example.core.model.Payload;
import com.example.core.util.ObjectMapperUtils;
import java.util.Map.Entry;
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
    private static final int TPS = 10000;

//    @Scheduled(fixedDelay = 1000)
//    public void subscribe() throws InterruptedException{
//        redisOperations.opsForHash().size("payload")
//                        .subscribe(o->{
//                            log.info("PAYLOAD SIZE = {}",o);
//                        });
//    }

    @Scheduled(fixedDelay = 1000)
    public void consumeTest() {
        redisOperations.opsForHash().randomEntries("payload",TPS)
            .map(o->ObjectMapperUtils.objectMapper(o.getValue(), Payload.class)
            )
            .flatMap(o->{
                return redisOperations.opsForHash().remove("payload",o.getId()).map(t->o);
            }).subscribe(
                o->{
                    // webClient를 통해 다른곳으로 보낼수 있지 않을까????
                    //log.info("PAYLOAD {} IS DELETED ",o);
                }
            );



    }
}
