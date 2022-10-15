package com.example.slowman.basicrepository;


import com.example.slowman.model.Payload;
import com.example.slowman.util.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ReactiveRedisRepository {
    private final ReactiveRedisComponent reactiveRedisComponent;

    public <T> Mono<T> save(String key, String hashKey, T t) {
        return reactiveRedisComponent
                .set(key.toString(),hashKey,t)
                .map(o->t);
    }

    public <T> Mono<T> get(String key,String hashKey,Class<T> clazz) {
        return reactiveRedisComponent.get(key.toString(),hashKey)
                .map(o-> ObjectMapperUtils.objectMapper(o,clazz));
    }

    public <T> Flux<T> get(String key, Class<T> clazz) {
        return reactiveRedisComponent.get(key.toString())
                .map(o -> ObjectMapperUtils.objectMapper(o,clazz));
    }

    public Mono<Long> delete(String key,String hashKey) {
        return reactiveRedisComponent.remove(key.toString(),hashKey);
    }


    public <T> Mono<Long> pub(ChannelTopic topic, T t) {
        return reactiveRedisComponent.pub(topic,t);
    }

}
