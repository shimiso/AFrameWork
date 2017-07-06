package com.eshangke.framework.common;

/**
 * 网络请求的常量
 *
 * @author juanqiang  Create at: 16/1/9  下午1:42
 */
public interface UrlConstant {
    public static final String HTTP_HOST ="http://10.1.2.191:8080/";


    //登陆验证
    public static final String LOGIN_CHECKOUT =HTTP_HOST+"loginCheckout";

    //获取验证码
    public static final String GET_VERI_CODE =HTTP_HOST+"getVerificationCode";

    //获取私钥
    public static final String GET_PRIVATE_KEY =HTTP_HOST+"getPrivateKey";
}
