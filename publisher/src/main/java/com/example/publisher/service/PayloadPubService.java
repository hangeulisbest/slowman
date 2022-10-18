package com.example.publisher.service;

import com.example.core.dto.PayloadDTO;
import com.example.core.model.Payload;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

    public Flux<PayloadDTO> saveForTest(){
//        List<Mono<PayloadDTO>> monoList = new ArrayList<>();
//        for(int i=0;i<100000;i++) {
//            String randomMessage = "THIS IS RANDOM MESSAGE " + i + " !";
//            PayloadDTO randomPayloadDTO = new PayloadDTO(randomMessage);
//            Payload randomPayload = new Payload(randomPayloadDTO);
//            monoList.add(redisOperations.opsForHash().put(PAYLOAD, randomPayload.getId(), randomPayload)
//                .flatMap(o -> Mono.just(randomPayloadDTO)));
//        }
//        return Flux.concat(monoList);

        // 3번
        return Flux.range(0,100000)
            .flatMap(i -> {
                PayloadDTO dto = new PayloadDTO("HELLO" + i);
                Payload payload = new Payload(dto);
                return redisOperations.opsForHash().put(PAYLOAD,payload.getId(),payload).map(t->dto);
            });
    }

    public Mono<Boolean> deleteAll() {
        return redisOperations.opsForHash().delete(PAYLOAD);
    }
}
