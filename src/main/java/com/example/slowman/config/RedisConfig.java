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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.example.slowman.constants.Topic.MESSAGE_PAYLOAD;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.cluster.nodes}")
    List<String> clusterNodes;

    @Bean
    public LettuceConnectionFactory createReactiveLettuceConnectionFactory() {

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


    @Bean
    public ReactiveRedisOperations<String, Object> redisOperations() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> context = builder.value(serializer).hashValue(serializer)
            .hashKey(serializer).build();

        return new ReactiveRedisTemplate<>(createReactiveLettuceConnectionFactory(), context);
    }


    @Bean(name = "redisClusterClient")
    public RedisClusterClient redisClusterClient() {
        Iterable<RedisURI> nodes = clusterNodes.stream().map(
                node -> RedisURI.Builder.redis(node.split(":")[0], Integer.parseInt(node.split(":")[1])).build()
        ).collect(Collectors.toList());
        return RedisClusterClient.create(nodes);
    }

    /*
    *  PubSub 을 위한 메세지 리스너
    * */

    @Bean
    public ChannelTopic payloadTopic() {
        return new ChannelTopic(MESSAGE_PAYLOAD);
    }

    @Bean
    public ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer() {
        ReactiveRedisMessageListenerContainer con = new ReactiveRedisMessageListenerContainer(createReactiveLettuceConnectionFactory());
        // default topic
        con.receive(payloadTopic());
        return con;
    }



}
