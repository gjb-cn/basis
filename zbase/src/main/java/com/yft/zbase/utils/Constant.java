package com.yft.zbase.utils;

public class Constant {
    /* 系统参数相关 */
    // 存放系统参数
    public final static String PARAMETER = "parameter";
    // 系统状态栏的高
    public final static String PARAMETER_STATUS_BAR_HI = "StatusBar";
    // 系统设备唯一码
    public static final String PARAMETER_KEY_UUID = "serialNumber";

    /* 用户信息相关 */
    public final static String USER = "user";

    // 用户令牌
    public final static String USER_TOKEN = "user_token";
    // 服务器地址（真实地址）, 从服务器获取到的真实请求地址
    public final static String SERVICE_ADDRESS = "service_address";
    // 开屏广告信息
    public final static String SERVICE_OPEN = "service_open";
    // 用户信息
    public final static String USER_INFO = "user_info";
    // 地址相关
    public final static String ADDRESS = "site_address";
    // 地址列表
    public final static String ADDRESS_LIST = "site_address_list";
    // 实名认证
    public final static String AUTHENTICATION = "Authentication";
    // 搜索关键字
    public final static String SEARCH_KEY = "search_key";
    // 是否同意隐私权限
    public final static String PRIVACY = "privacy";
    // 渠道相关
    public final static String FLAVOR_KEY = "flavor";
    // 渠道下的细分渠道
    public final static String CHANNEL_KEY = "channel";

    // 同意(相机/照片)权限说明
    public final static String CAMERA_STORAGE_PERMISSIONS = "CAMERA_STORAGE_PERMISSIONS";
    // 同意相机权限
    public final static String CAMERA_PERMISSIONS = "CAMERA_PERMISSIONS";
    // 存储权限
    public final static String STORAGE_PERMISSIONS = "STORAGE_PERMISSIONS";

    // 支付相关
    public final static String WX_PAY = "WX_PAY";
    /* 语言相关 */
    public final static String LANGUAGE = "language";
    // 当前语言环境
    public final static String LANGUAGE_TYPE = "language_type";

    /* 存储相关(提供给关联的app) */
    public final static String FREE_STORAGE = "free_storage";


    public final static String NORMAL = "normal"; //普通区
    public final static String ACTIVITY = "activity"; // 活动区（品鉴区）
    public final static String SPECIAL = "special"; // 抽奖区


    // -- 调试模式
    public final static String DEBUG_KEY = "debug_key";
    // -- 版本（软件版本）
    public final static String VERSION_KEY = "version_key";
    // -- 软件别名
    public final static String ALIAS_KEY = "alias_key";
}
