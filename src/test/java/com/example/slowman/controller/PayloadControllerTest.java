package com.example.slowman.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.example.slowman.dto.PayloadDTO;
import com.example.slowman.model.Payload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PayloadControllerTest {

    @Autowired
    private WebTestClient webTestClient;


    @DisplayName("webClient로 저장 테스트")
    @Test
    @Order(1)
    public void saveTest() {
        PayloadDTO dto = new PayloadDTO("Hello");

        FluxExchangeResult<Payload> payloadFluxExchangeResult = webTestClient.post().uri("/api/payload")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(dto), PayloadDTO.class)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(Payload.class);

        StepVerifier
            .create(payloadFluxExchangeResult.getResponseBody())
            .assertNext(payload -> {
                assertNotNull(payload.getId());
                assertNotNull(payload.getCreatedAt());
                assertEquals(payload.getMessage(),dto.getMessage());
            })
            .verifyComplete();

//        payloadFluxExchangeResult.getResponseBody()
//            .subscribe(payload -> {
//                log.info("PAYLOAD = {}",payload);
//            });

    }
}