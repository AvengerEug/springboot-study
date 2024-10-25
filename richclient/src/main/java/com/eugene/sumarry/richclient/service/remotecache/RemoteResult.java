package com.eugene.sumarry.richclient.service.remotecache;

import java.io.Serializable;

/**
 * @author muyang
 * @create 2024/10/25 14:43
 */
public class RemoteResult<V> implements Serializable {

    private V value;

    /**
     * 默认值0： 为成功
     */
    private boolean success;

    private Integer code;

    private String msg;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
