package com.eugene.sumarry.richclient.service.remotecache;

import java.io.Serializable;
import java.util.List;

/**
 * 构建一个缓存的请求对象。方便使用
 *
 * @author muyang
 * @create 2024/10/25 11:25
 */
public interface RemoteCacheService {

    RemoteResult set(String key, Serializable value, Long expireTime);

    RemoteResult get(String key);

    RemoteResult putList(String key, List<?> value, Long expireTime);

    RemoteResult getList(String key);

    RemoteResult remove(String key);

}
