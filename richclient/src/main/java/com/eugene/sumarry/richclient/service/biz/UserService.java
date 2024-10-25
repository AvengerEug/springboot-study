package com.eugene.sumarry.richclient.service.biz;

import com.eugene.sumarry.richclient.client.domain.CacheUserInfoDO;

import java.util.List;

/**
 * @author muyang
 * @create 2024/10/25 10:57
 */
public interface UserService {
    /**
     * 一个方法对应一个业务。
     * 对应读取操作
     * @param key
     * @return 不要返回字符串，直接返回对象，防止重复的序列化与反序列化，消耗cpu
     */
    List<CacheUserInfoDO> list(String key);

    /**
     * 一个方法对应一个业务
     * 对应写操作
     * @param key
     * @return
     */
    void insertOrUpdate(String key);

}
