package com.yft.zbase.server;

public interface IDevice extends IServerAgent {
    /**
     * 获取设备id(唯一id)
     * @return
     */
    String getDeviceId();
    /**
     * 获取状态栏高度
     * @return
     */
    int getStatusBarHi();

    String getAndroid();

    void saveAppAlias(String alias);

    String getAppAlias();


    String getModel();

    boolean isDebug();

    void saveDebug(boolean isDebug);

    String getVersion();

    void saveVersion(String version);

    /**
     * 获取当前渠道名称
     * @return
     */
    String getFlavor();

    /**
     * 保存渠道名称
     * @param flavor
     * @return
     */
    boolean saveFlavor(String flavor);

    // 渠道（二渠道名称）， 可能在腾讯平台下推广，但是又细分不同的渠道
    String getChannel();
    boolean saveChannel(String channe);
}
