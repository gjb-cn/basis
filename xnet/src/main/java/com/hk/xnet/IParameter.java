package com.hk.xnet;

import java.util.UUID;

/**
 * 请求中的固定参数
 */
public interface IParameter {
    String getVersion(); // 当前应用版本号
    String getLanguageType(); // 当前语言
    String getDeviceId();// 设备ID
    String getAndroid(); //androidID
    String getPromoteChannel(); // 渠道
    String getToken(); // token
    String getClientModel(); //设备型号
    String getKey(long requestTimestamp, String uuid, String token, String random);
    default String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
