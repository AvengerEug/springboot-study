package com.eugene.sumarry.richclient.service.remotecache.impl;

import com.eugene.sumarry.richclient.service.remotecache.RemoteCacheService;
import com.eugene.sumarry.richclient.service.remotecache.RemoteResult;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author muyang
 * @create 2024/10/25 11:25
 */
@Service
public class RemoteCacheServiceImpl implements RemoteCacheService {

    @Autowired
    private RedissonClient redissonClient;

    public RemoteResult set(String key, Serializable value, Long expireTime) {
        RemoteResult result = new RemoteResult();
        if (expireTime != null && expireTime > 0) {
            redissonClient.getBucket(key).set(value, expireTime, TimeUnit.SECONDS);
        } else {
            redissonClient.getBucket(key).set(value);
        }

        result.setSuccess(true);
        return result;
    }

    @Override
    public RemoteResult get(String key) {
        RemoteResult result = new RemoteResult();
        Object object = redissonClient.getBucket(key).get();
        result.setValue(object);
        result.setSuccess(true);
        return result;
    }

    @Override
    public RemoteResult putList(String key, List<?> value, Long expireTime) {
        RemoteResult result = new RemoteResult();
        if (expireTime != null && expireTime > 0) {
            redissonClient.getBucket(key).set(value, expireTime, TimeUnit.SECONDS);
        } else {
            redissonClient.getBucket(key).set(value);
        }
        result.setSuccess(true);
        return result;
    }

    public RemoteResult getList(String key) {
        RemoteResult result = new RemoteResult();
        result.setValue(redissonClient.getBucket(key).get());
        result.setSuccess(true);
        return result;
    }

    @Override
    public RemoteResult remove(String key) {
        RemoteResult result = new RemoteResult();
        redissonClient.getBucket(key).delete();
        result.setSuccess(true);
        return result;
    }
}
