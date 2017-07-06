package com.eshangke.framework.util;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.HttpTask;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 相关请求api接口，底层调用的GET，POST请求
 * 作者：chenlipeng
 * 创建时间：2016/1/29 16:05
 */
public class HttpUtil {
    private static final Object lock = new Object();
    private static HttpUtil instance = null;

    private HttpUtil() {
    }

    public static HttpUtil getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new HttpUtil();
                }
            }
        }
        return instance;
    }

    public <T> Callback.Cancelable get(RequestParams entity, Callback.CommonCallback<T> callback) {
        if (entity != null) {// 可在此处添加通用的请求参数
            entity.addParameter("from", "2");
            entity.addParameter("version", "2");
        }
        return request(HttpMethod.GET, entity, callback);
    }

    public <T> Callback.Cancelable post(RequestParams entity, Callback.CommonCallback<T> callback) {
        if (entity != null) {// 可在此处添加通用的请求参数
            entity.addParameter("from", "2");
            entity.addParameter("version", "2");
        }
        return request(HttpMethod.POST, entity, callback);
    }

    public <T> Callback.Cancelable request(HttpMethod method, RequestParams entity, Callback.CommonCallback<T> callback) {
        entity.setMethod(method);
        Callback.Cancelable cancelable = null;
        if (callback instanceof Callback.Cancelable) {
            cancelable = (Callback.Cancelable) callback;
        }
        HttpTask<T> task = new HttpTask<T>(entity, cancelable, callback);
        return x.task().start(task);
    }

    public <T> T getSync(RequestParams entity, Callback.TypedCallback<T> resultType) throws Throwable {
        return requestSync(HttpMethod.GET, entity, resultType);
    }

    public <T> T postSync(RequestParams entity, Callback.TypedCallback<T> resultType) throws Throwable {
        return requestSync(HttpMethod.POST, entity, resultType);
    }

    public <T> T requestSync(HttpMethod method, RequestParams entity, Callback.TypedCallback<T> callback) throws Throwable {
        entity.setMethod(method);
        HttpTask<T> task = new HttpTask<T>(entity, null, callback);
        return x.task().startSync(task);
    }

}
