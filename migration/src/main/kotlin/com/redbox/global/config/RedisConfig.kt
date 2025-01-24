package com.redbox.global.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
@EnableRedisRepositories
@Profile("!test")
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,

    @Value("\${spring.data.redis.port}")
    private val port: Int,

    @Value("\${spring.data.redis.password}")
    private val password: String
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration()
        redisConfig.hostName = host
        redisConfig.port = port
        redisConfig.setPassword(password)
        return LettuceConnectionFactory(redisConfig)
    }

    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory

        // ObjectMapper 설정
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule()) // Java 8의 시간 관련 클래스(LocalDateTime 등) 직렬화 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 날짜를 timestamp가 아닌 ISO-8601 형식으로 직렬화
        objectMapper.activateDefaultTyping(
            BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Any::class.java) // Object.class를 기반으로 하는 모든 타입 허용
                .build(),
            ObjectMapper.DefaultTyping.NON_FINAL,  // final이 아닌 클래스들에 대해 타입 정보 포함
            JsonTypeInfo.As.PROPERTY // 타입 정보를 JSON 속성으로 저장
        )


        // Value Serializer 설정
        val jsonSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.keySerializer = StringRedisSerializer() // 키는 문자열로 직렬화
        template.valueSerializer = jsonSerializer // 값은 JSON으로 직렬화
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = jsonSerializer

        template.afterPropertiesSet()
        return template
    }
}