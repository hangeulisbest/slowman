package com.example.slowman.controller;


import com.example.slowman.basicrepository.ReactiveRedisRepository;
import com.example.slowman.dto.PayloadDTO;
import com.example.slowman.service.PayloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class PayloadMessageController {
    private final ReactiveRedisRepository reactiveRedisRepository;
    private final ReactiveRedisMessageListenerContainer reactiveMsgListenerContainer;
    private final PayloadService payloadService;
    private final ChannelTopic payloadTopic;


    @GetMapping("/topic")
    public Mono<String> getTopic() {
        return Mono.fromCallable(payloadTopic::getTopic);
    }

    @PostMapping("/payload")
    public Mono<Long> publishPayload(@RequestBody PayloadDTO payloadDTO) {
        return payloadService.publish(payloadDTO);
    }

    /**
     * 리팩토링 하나도 안한 지저분한 코드임 ㅎㅎ
     */

    @GetMapping("/payload")
    public Flux<String> subscribe() {
        log.info("SUBSCRIBE PAYLOAD START ! topic = {}",payloadTopic.getTopic());

        return reactiveMsgListenerContainer
                .receive(payloadTopic)
                .map(ReactiveSubscription.Message::getMessage)
                .map(msg -> {
                    log.info("New Message received: '" + msg.toString() + "'.");
                    return msg.toString() + "/n";
                });

    }

}
