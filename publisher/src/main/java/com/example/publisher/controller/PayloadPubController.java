package com.example.publisher.controller;

import com.example.core.dto.PayloadDTO;
import com.example.publisher.service.PayloadPubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class PayloadPubController {
    private final PayloadPubService payloadPubService;

    @PostMapping("/payload")
    public Mono<PayloadDTO> save(@RequestBody PayloadDTO dto) {
        return payloadPubService.save(dto);
    }

    @GetMapping("/payload-test")
    public Flux<PayloadDTO> saveForTest(){
        return payloadPubService.saveForTest();
    }

    @DeleteMapping("/payload/deleteall")
    public Mono<Boolean> deleteAll() {
        return payloadPubService.deleteAll();
    }

}
