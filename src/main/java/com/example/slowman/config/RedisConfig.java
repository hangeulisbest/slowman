package com.example.slowman.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.DnsResolvers;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisConfig {
    @Value("${spring.redis.cluster.nodes}")
    private List<String> clusterNodes;

    public RedisConnectionFactory createLettuceConnectionFactory() {

        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
            .enablePeriodicRefresh(Duration.ofSeconds(30))
            .enableAllAdaptiveRefreshTriggers()
            .build();
        ClientOptions clientOptions = ClusterClientOptions.builder()
            .topologyRefreshOptions(topologyRefreshOptions)
            .build();
        ClientResources clientResources = DefaultClientResources.builder().dnsResolver(DnsResolvers.JVM_DEFAULT).build();

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .clientResources(clientResources)
            .clientOptions(clientOptions).build();
        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }

    public ReactiveRedisConnectionFactory createReactiveLettuceConnectionFactory() {

        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
            .enablePeriodicRefresh(Duration.ofSeconds(30))
            .enableAllAdaptiveRefreshTriggers()
            .build();
        ClientOptions clientOptions = ClusterClientOptions.builder()
            .topologyRefreshOptions(topologyRefreshOptions)
            .build();
        ClientResources clientResources = DefaultClientResources.builder().dnsResolver(DnsResolvers.JVM_DEFAULT).build();

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .clientResources(clientResources)
            .clientOptions(clientOptions).build();
        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }

    public RedisClusterClient createClusterClient() {
        Iterable<RedisURI> nodes = clusterNodes.stream().map(
            node -> RedisURI.Builder.redis(node.split(":")[0], Integer.parseInt(node.split(":")[1])).build()
        ).collect(Collectors.toList());
        return RedisClusterClient.create(nodes);
    }


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return createLettuceConnectionFactory();  // Redis DB 선택
    }

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return createReactiveLettuceConnectionFactory();  // Redis DB 선택
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object>  template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

    //    @Bean
//    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate() {
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//
//        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext.<String, String>newSerializationContext(stringRedisSerializer)
//            .value(stringRedisSerializer)
//            .build();
//        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(), serializationContext);
//    }
    @Bean
    public ReactiveRedisOperations<String, Object> redisOperations() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> context = builder.value(serializer).hashValue(serializer)
            .hashKey(serializer).build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(), context);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }

    @Bean(name = "redisClusterClient")
    public RedisClusterClient redisClusterClient() {
        return createClusterClient();
    }
}
