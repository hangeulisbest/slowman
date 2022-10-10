package com.example.slowman.basicrepository;


import com.example.slowman.util.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReactiveRedisRepository {
    private final ReactiveRedisComponent reactiveRedisComponent;

    public <T> Mono<T> save(String key, String hashKey,T t) {
        return reactiveRedisComponent
                .set(key,hashKey,t)
                .map(o->t);
    }

    public <T> Mono<T> get(String key,String hashKey,Class<T> clazz) {
        return reactiveRedisComponent.get(key,hashKey)
                .map(o-> ObjectMapperUtils.objectMapper(o,clazz));
    }

    public <T> Flux<T> get(String key, Class<T> clazz) {
        return reactiveRedisComponent.get(key)
                .map(o -> ObjectMapperUtils.objectMapper(o,clazz));
    }

    public Mono<Long> delete(String key,String hashKey) {
        return reactiveRedisComponent.remove(key,hashKey);
    }


}
