package com.example.demo.email;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class EmailTemplate {
    @Autowired
    private UuidGenerater uuidGenerater;
    @Autowired private RedisTemplate<Object,Object> redisTemplate;

    public UuidGenerater getUuidGenerater() {
        return uuidGenerater;
    }

    public String setRedisTime(String email) {
        uuidGenerater.getUuid().toString();
        redisTemplate.opsForValue().set(email,uuidGenerater.getUuid(),5, TimeUnit.MINUTES);
        return redisTemplate.opsForValue().get(email).toString();
    }
    public String getRedisTime(String email) {
        String emailData;
        try {
            emailData = redisTemplate.opsForValue().get(email).toString();
        } catch (Exception e) {
            return null;
        }
        return emailData;

    }
}

