package com.eugene.sumarry.richclient.service.remotecache;

import java.io.Serializable;
import java.util.List;

/**
 * @author muyang
 * @create 2024/10/25 11:25
 */
public interface RemoteCacheService {

    boolean set(String key, Serializable value);

    <T> T get(String key, Serializable value);

    boolean putList(String key, List<?> value);

    <T> T getList(String key);

    boolean remove(String key);

}
