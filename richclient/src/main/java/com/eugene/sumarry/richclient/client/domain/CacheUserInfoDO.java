package com.eugene.sumarry.richclient.client.domain;

import java.io.Serializable;

/**
 * @author muyang
 * @create 2024/10/25 11:43
 */
public class CacheUserInfoDO implements Serializable {
    private String id;

    public CacheUserInfoDO() {
    }

    public CacheUserInfoDO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
