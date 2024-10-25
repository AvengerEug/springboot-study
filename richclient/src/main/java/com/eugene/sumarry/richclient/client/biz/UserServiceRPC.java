package com.eugene.sumarry.richclient.client.biz;

import com.eugene.sumarry.richclient.client.domain.CacheUserInfoDO;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @author muyang
 * @create 2024/10/25 16:39
 */
public interface UserServiceRPC {

    /**
     * rpc服务，调用元数据的list方法
     *
     * @return
     */
    List<CacheUserInfoDO> list(String key) throws URISyntaxException;

}
