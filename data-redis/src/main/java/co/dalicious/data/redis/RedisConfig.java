package co.dalicious.data.redis;

import co.dalicious.data.redis.entity.CertificationHash;
import co.dalicious.data.redis.entity.NotificationHash;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@EnableRedisRepositories
@PropertySource("classpath:application-redis.properties")
public class RedisConfig {

    @Value("${spring.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
        return new LettuceConnectionFactory(redisClusterConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Configure Jackson2JsonRedisSerializer for multiple entities
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();

        // Use activateDefaultTyping() along with a PolymorphicTypeValidator
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.registerModule(new JavaTimeModule());

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // Set the value serializer
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        return container;
    }

    @Bean(name = "redisTopic")
    public ChannelTopic topic() {
        return new ChannelTopic("makers_reload");
    }
}

//@Configuration
//@EnableRedisRepositories
//@PropertySource("classpath:application-redis.properties")
//public class RedisConfig {
//
//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.port}")
//    private int port;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(host, port);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//
//        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
//
//        ObjectMapper mapper = new ObjectMapper();
//        // Java 8 date/time 타입을 지원하기 위해 모듈을 등록
//        mapper.registerModule(new JavaTimeModule());
//        serializer.setObjectMapper(mapper);
//
//        redisTemplate.setValueSerializer(serializer);
//
//        return redisTemplate;
//    }
//    @Bean
//    public StringRedisTemplate stringRedisTemplate() {
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
//        return stringRedisTemplate;
//    }
//
//    @Bean
//    public RedisMessageListenerContainer redisContainer() {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory());
//        return container;
//    }
//
//    @Bean(name = "redisTopic")
//    public ChannelTopic topic() {
//        return new ChannelTopic("makers_reload");
//    }
//
//}
