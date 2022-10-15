package com.example.slowman.controller;

import com.example.slowman.dto.PayloadDTO;
import com.example.slowman.model.Payload;
import com.example.slowman.service.PayloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PayloadApiController {

    private final PayloadService payloadService;

    @PostMapping("/payload")
    public Mono<Payload> save(@RequestBody PayloadDTO dto) {
        return payloadService.save(dto);
    }

    @GetMapping("/payload/{id}")
    public Mono<Payload> findOne(@PathVariable String id) {
        return payloadService.get(id);
    }

    @GetMapping("/payload")
    public Flux<Payload> findAll() {
        return payloadService.get();
    }
}
