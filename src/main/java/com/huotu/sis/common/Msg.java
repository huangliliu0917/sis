package com.huotu.sis.common;

/**
 * 传给前端的js信息
 * Created by xhk on 2015/12/15.
 */

public class Msg {

    /**
     * 服务器处理状态码
     */
    private Integer code;

    /**
     * 服务器返回的信息
     */
    private String msg;

    /**
     * 图片的相对路径
     */
    private String relativeUrl;

    /**
     * 上传图片后返回的服务器图片存储地址
     */
    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRelativeUrl(){
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl){
        this.relativeUrl=relativeUrl;
    }
}
