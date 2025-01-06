package com.yft.zbase.base;


import static com.yft.zbase.utils.Logger.LOGE;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hk.xnet.IParameter;
import com.hk.xnet.IXNet;
import com.hk.xnet.RequestUtils;
import com.hk.xnet.XNetImpl;
import com.yft.zbase.BuildConfig;
import com.yft.zbase.bean.AddressBean;
import com.yft.zbase.bean.DownLoadBean;
import com.yft.zbase.bean.ServiceBean;
import com.yft.zbase.bean.UserInfo;
import com.yft.zbase.server.DynamicMarketManage;
import com.yft.zbase.server.IDevice;
import com.yft.zbase.server.ILanguage;
import com.yft.zbase.server.IServerAgent;
import com.yft.zbase.server.IUser;
import com.yft.zbase.utils.JsonUtil;
import com.yft.zbase.utils.UtilsPaths;
import com.yft.zbase.xnet.InterfacePath;
import com.yft.zbase.xnet.ResponseDataListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * 封装请求方式
 */
public class BaseModel {
    // 接口版本
    protected IXNet mNetWork;
    protected final IUser mUser;
    protected ILanguage iLanguage;
    protected final IDevice mDevice;

    private IParameter iParameter;

    //渠道编码
    public BaseModel() {
        mNetWork = getNetWork();
        iLanguage = DynamicMarketManage.getInstance().getServer(IServerAgent.LANGUAGE_SERVER);
        mUser = DynamicMarketManage.getInstance().getServer(IServerAgent.USER_SERVER);
        mDevice = DynamicMarketManage.getInstance().getServer(IServerAgent.DEVICE_SERVER);
        //
        iParameter = new Parameter(mUser, mDevice, iLanguage);
    }

    public ServiceBean getServiceBean() {
        return mUser.getServiceUrl();
    }

    /**
     * 保存用户信息
     * @param userinfo
     * @return
     */
    public boolean saveUserInfo(UserInfo userinfo) {
        if (userinfo == null) {
            return false;
        }
        return mUser.sveaUserInfo(userinfo);
    }

    public UserInfo getUserInfo() {
        return mUser.getUserInfo();
    }

    protected boolean isLogin() {
        return mUser.isLogin();
    }

    /**
     * 保存用户请求地址
     * @param serviceBean
     * @return
     */
    public boolean saveServiceAddress(ServiceBean serviceBean) {
        if (serviceBean == null) {
            return false;
        }
        return mUser.sveaServiceUrl(serviceBean);
    }

    protected IXNet getNetWork() {
        return  XNetImpl.getInstance();
    }


    public Map<String, String> getRequestParameter(TreeMap<String, String> values) {
        return RequestUtils.getRequestParameter(values, iParameter);
    }

    public Map<String, String> getRequestParameter() {
        return RequestUtils.getRequestParameter(iParameter);
    }

    public void requestServiceAddress(final int position,final String path, final IServiceNotice iServiceNotice) {
        // 获取原始地址
        String basePath = "";
        if (mUser.getServiceUrl() == null) {
            // 获取到附路径
            LOGE("BaseModel=>", "第一次加载");
            basePath = UtilsPaths.getBaseUrl(position);
        } else {
            if ((mUser.getServiceUrl().getBackupUris().size() -1) >= position)  {
                basePath = mUser.getServiceUrl().getBackupUris().get(position);
            }
        }

        if (TextUtils.isEmpty(basePath)) {
            iServiceNotice.onFail();
            return;
        }

        LOGE(getClass().getSimpleName(), "发起第几次请求" + position);
        basePath = basePath + path;
        mNetWork.easyPost(basePath, getRequestParameter(new TreeMap<>()), new ResponseDataListener<ServiceBean>() {
            @Override
            public void success(ServiceBean data) {
                if (data != null) {
                    data.setAssignUri(data.getAssignUri() + "/proxy");
                    if (!com.yft.zbase.utils.Utils.isCollectionEmpty(data.getBackupUris())) {
                        List<String> list = new ArrayList<>();
                        for (String s: data.getBackupUris()) {
                            list.add(s + "/proxy");
                        }
                        data.setBackupUris(list);
                    }
                }
                // 保存服务器地址
                saveServiceAddress(data);

                // 通知请求成功
                iServiceNotice.onSuccess();
            }

            @Override
            public void fail(Throwable throwable) {
                int index = position + 1;
                requestServiceAddress(index, path, iServiceNotice);
            }
        }, ServiceBean.class);
    }


    /**
     * h5交互请求...
     * @param responseDataListener
     */
    public void requestAny(String url, String json, ResponseDataListener<String> responseDataListener) {
        TreeMap<String,String> treeMap = new TreeMap<>();
        if (!TextUtils.isEmpty(json)) {
            JsonObject jsonObject = JsonUtil.parseJsonToJsonObj(json);
            if (jsonObject != null) {
                Iterator<String> iter = jsonObject.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    JsonElement value = jsonObject.get(key);
                    if (!TextUtils.isEmpty(value.getAsString())) {
                        treeMap.put(key, value.getAsString());
                    }
                }
            }
        }

        mNetWork.easyPostAny(url, getRequestParameter(treeMap), responseDataListener);
    }

    /**
     * 请求最新版本信息
     * @param responseDataListener
     */
    public void postDownload(ResponseDataListener<DownLoadBean> responseDataListener) {
        getNetWork()
                // 取消上次的请求
                .easyPost(mUser.getBastServiceURL() + InterfacePath.NEWEST_VERSION,
                        getRequestParameter(), responseDataListener, DownLoadBean.class);
    }

    /**
     * 发送验证码
     * @param action
     * @param phone
     * @param responseDataListener
     */
    public void sendCode(String action,String phone, ResponseDataListener<String> responseDataListener) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("action", action);
        treeMap.put("phone", phone);
        mNetWork.easyPost(mUser.getBastServiceURL() + InterfacePath.SEND_CODE,
                getRequestParameter(treeMap), responseDataListener, String.class);
    }

    public void downLoadApk(ResponseDataListener responseDataListener) {
        mNetWork.downLoadFile("http://106.52.81.117/images/flag/xinjiapo.png", responseDataListener);
    }

    public void uploadFile(File file, String type, ResponseDataListener responseDataListener, Class<?> aClass) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        try {
            int last = file.getName().lastIndexOf(".");
            if (last > 0) {
                String hz = file.getName().substring(last +1);
                treeMap.put("fileType", hz);
            } else {
                treeMap.put("fileType", "jpg");
            }
            treeMap.put("module", type);
        } catch (Exception e) {
            treeMap.clear();
            treeMap.put("fileType", "jpg");
            treeMap.put("module", type);
        }
        // 上传文件
        mNetWork.updateFile(mUser.getBastServiceURL() + InterfacePath.UPDATE_FILE, getRequestParameter(treeMap), file, responseDataListener, aClass);
    }


    /**
     * 获取地址列表
     * @param responseDataListener
     */
    public void requestAddressCoddList(final ResponseDataListener<List<AddressBean>> responseDataListener) {
        getNetWork()
                // 取消上次的请求
                .cancelTagRequest(mUser.getBastServiceURL() +InterfacePath.USER_ADDRESS_LIST)
                .easyPost(mUser.getBastServiceURL() + InterfacePath.USER_ADDRESS_LIST,
                        getRequestParameter(), responseDataListener, new TypeToken<List<AddressBean>>() {}.getType());
    }

    /**
     * 清除全部请求
     */
    public void clearRequestAll() {
        if (mNetWork != null) {
            mNetWork.cancelAllRequest();
        }
    }

    /**
     * 清除单个请求
     * @param tag
     */
    public void clearRequestTag(String tag) {
        if (mNetWork != null) {
            mNetWork.cancelTagRequest(tag);
        }
    }

    /**
     * 避免内存泄露，尽量使用静态的内部类
     */
    public static class Parameter implements IParameter {
        private IUser iUser;
        private IDevice iDevice;
        private   ILanguage iLanguage;
        public Parameter(IUser iUser, IDevice iDevice, ILanguage iLanguage) {
            this.iDevice = iDevice;
            this.iUser = iUser;
            this.iLanguage = iLanguage;
        }


        @Override
        public String getVersion() {
            return iDevice.getVersion();
        }

        @Override
        public String getLanguageType() {
            return iLanguage.getLanguageType();
        }

        @Override
        public String getDeviceId() {
            return iDevice.getDeviceId();
        }

        @Override
        public String getAndroid() {
            return iDevice.getAndroid();
        }

        @Override
        public String getPromoteChannel() {
            return iDevice.getChannel();
        }

        @Override
        public String getToken() {
            return iUser.getUserInfo() == null ? "" : iUser.getUserInfo().getToken();
        }

        @Override
        public String getClientModel() {
            return iDevice.getModel();
        }

        @Override
        public String getKey(long requestTimestamp, String uuid, String token, String random) {
            return cn.sd.ld.ui.helper.Utils.getMD5KeyByRequest(requestTimestamp, String.valueOf(requestTimestamp), uuid, token, random);
        }
    }

    public interface IServiceNotice {
        void onSuccess();
        void onFail();
    }
}
