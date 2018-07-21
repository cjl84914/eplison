package com.geetion.epsilon.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by alexcai on 2018/4/23.
 */
@Configuration
public class RedisConfig {

    @Bean(name = "spring.redis.pool")
    @Autowired
    public JedisPool jedisPool(@Qualifier("spring.redis.pool.config") JedisPoolConfig config,
                               @Value("${spring.redis.host}") String host,
                               @Value("${spring.redis.port}") int port,
                               @Value("${spring.redis.timeout}") int timeout,
                               @Value("${spring.redis.password}") String password) {
        return new JedisPool(config, host, port);
    }

    @Bean(name = "spring.redis.pool.config")
    public JedisPoolConfig jedisPoolConfig(@Value("${spring.redis.pool.maxTotal}") int maxTotal,
                                           @Value("${spring.redis.pool.maxIdle}") int maxIdle,
                                           @Value("${spring.redis.pool.maxWaitMillis}") int maxWaitMillis) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        return config;
    }

}
