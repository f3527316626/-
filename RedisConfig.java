package com.easy.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration
//用于解决Redis序列化乱码、对象序列化失败的问题，让Redis的操作更简洁、数据更规范、可读性更强。
public class RedisConfig {
    //@Bean：把这个方法返回的RedisTemplate对象，交给 Spring 容器管理，成为全局可用的单例 Bean，后续项目中注入 RedisTemplate 就是用这个配置好的实例。
    @Bean
    //redisTemplate给Redis的Key/Value/HashKey/HashValue，全部配置统一、规范的序列化器
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);//绑定 Redis 的连接工厂
        template.setKeySerializer(new StringRedisSerializer());//设置普通的序列化器
        // 设置value的序列化方式，将普通的变成json序列化的
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 设置hash key的序列化方式，将hash结构的的序列化器变成字符串序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash value的序列化方式，hash序列化变成json序列化
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();//初始化，执行了这个上面的操作才执行成功
        return template;
    }
}