package com.eugene.sumarry.richclient.service.remotecache.impl;

import com.eugene.sumarry.richclient.service.remotecache.RemoteCacheService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author muyang
 * @create 2024/10/25 11:25
 */
@Service
public class RemoteCacheServiceImpl implements RemoteCacheService {

    @Autowired
    private RedissonClient redissonClient;

    public boolean set(String key, Serializable value) {
        redissonClient.getBucket(key).set(value);
        return true;
    }

    @Override
    public <T> T get(String key, Serializable value) {
        return (T) redissonClient.getBucket(key).get();
    }

    @Override
    public boolean putList(String key, List<?> value) {
        redissonClient.getBucket(key).set(value);
        return false;
    }

    public <T> T getList(String key) {
        return (T) redissonClient.getBucket(key).get();
    }

    @Override
    public boolean remove(String key) {
        return redissonClient.getBucket(key).delete();
    }
}
