package com.eugene.sumarry.richclient.service.biz.impl;

import com.eugene.sumarry.richclient.client.domain.CacheUserInfoDO;
import com.eugene.sumarry.richclient.service.anno.CacheOperateType;
import com.eugene.sumarry.richclient.service.anno.Cached;
import com.eugene.sumarry.richclient.service.biz.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muyang
 * @create 2024/10/25 10:57
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    @Cached(cacheKey = "", operator = CacheOperateType.GET, expireTime = "500")
    public List<CacheUserInfoDO> list(String key) {
        List<CacheUserInfoDO> result = new ArrayList<>();
        result.add(new CacheUserInfoDO(Math.random() + ""));
        result.add(new CacheUserInfoDO(Math.random() + ""));
        result.add(new CacheUserInfoDO(Math.random() + ""));
        return result;
    }

    @Override
    @Cached(cacheKey = "", operator = CacheOperateType.DELETE)
    public void insertOrUpdate(String key) {
        // 更新数据库
        System.out.println("模拟更新数据库");

        // 删除远端缓存（放在切面做）
    }
}
