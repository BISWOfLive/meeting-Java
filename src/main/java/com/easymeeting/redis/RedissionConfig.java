package com.easymeeting.redis;

import com.easymeeting.entity.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = Constants.MESSAGEING_HANDLE_CHANNEL_KEY,havingValue = Constants.MESSAGEING_HANDLE_CHANNEL_REDIS)
@Slf4j
public class RedissionConfig {
    @Value("${spring.redis.host:}")
    private String redisHost;

    @Value("@{spring.redis.port:}")
    private Integer redisPort;

    @Bean(name = "redissionClient",destroyMethod = "shutdown")
    public RedissonClient redissonClient(){
        try {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort);
            RedissonClient redissonClient = Redisson.create(config);
            return redissonClient;
        }catch (Exception e){
            log.error("redission配置错误,请检查");
        }
        return null;
    }
}
