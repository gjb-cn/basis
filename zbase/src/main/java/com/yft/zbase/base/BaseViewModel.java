package com.yft.zbase.base;

import static com.yft.zbase.utils.Logger.LOGE;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hk.xnet.XNetSystemErrorCode;
import com.yft.zbase.ZBaseApplication;
import com.yft.zbase.bean.DownLoadBean;
import com.yft.zbase.bean.KVBean;
import com.yft.zbase.bean.ServiceBean;
import com.yft.zbase.bean.UserInfo;
import com.yft.zbase.server.DynamicMarketManage;
import com.yft.zbase.server.IDevice;
import com.yft.zbase.server.ILanguage;
import com.yft.zbase.server.IServerAgent;
import com.yft.zbase.server.IUser;
import com.yft.zbase.xnet.InterfacePath;
import com.yft.zbase.xnet.ResponseDataListener;

import java.io.File;

import top.zibin.luban2.Luban;
import top.zibin.luban2.OnCompressListener;


/**
 * BaseViewModel
 */
public class BaseViewModel extends ViewModel {
    // 当前的服务器地址监听器
    private MutableLiveData<ServiceBean> mServiceLiveChangeDate = new MutableLiveData<>();
    // 请求服务器任意地址
    private MutableLiveData<KVBean> mKvBeanMutableLiveData = new MutableLiveData<>();
    // 请求出错，发送界面处理
    private MutableLiveData<String> mErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DownLoadBean> mDownLoadBeanMutableLiveData = new MutableLiveData<>();
    protected ILanguage iLanguage;
    protected BaseModel mBaseModel;

    public BaseViewModel() {
        iLanguage = DynamicMarketManage.getInstance().getServer(IServerAgent.LANGUAGE_SERVER);
        mBaseModel = new BaseModel();
    }

    public MutableLiveData<ServiceBean> getServiceLiveChangeDate() {
        return mServiceLiveChangeDate;
    }

    public void setErrorMutableLiveData(MutableLiveData<String> mErrorMutableLiveData) {
        this.mErrorMutableLiveData = mErrorMutableLiveData;
    }

    public MutableLiveData<String> getErrorMutableLiveData() {
        return mErrorMutableLiveData;
    }

    public MutableLiveData<KVBean> getKvBeanMutableLiveData() {
        return mKvBeanMutableLiveData;
    }


    public MutableLiveData<DownLoadBean> getDownLoadBeanMutableLiveData() {
        return mDownLoadBeanMutableLiveData;
    }


    /**
     * 请求服务器地址
     */
    public void requestService() {
        mBaseModel.requestServiceAddress(0, InterfacePath.getBasePath, new BaseModel.IServiceNotice() {
            @Override
            public void onSuccess() {
                // 更换地址成功了， 通知各个页面做更新页面动作
                ServiceBean serviceBean = mBaseModel.getServiceBean();
                serviceBean.setSuss(true);
                getServiceLiveChangeDate().postValue(serviceBean);
            }

            @Override
            public void onFail() {
                // 通知当前页面 失败了就该干嘛干嘛
                ServiceBean serviceBean = new ServiceBean();
                serviceBean.setSuss(false);
                getServiceLiveChangeDate().postValue(serviceBean);
            }
        });
    }


    public void requestAny(final String url, final String callbackFunction, final String json) {
        mBaseModel.requestAny(mBaseModel.getServiceBean().getAssignUri() + url, json, new ResponseDataListener<String>() {
            @Override
            public void success(String data) {
                KVBean kvBean = new KVBean();
                kvBean.key = "0";
                kvBean.value = data;
                kvBean.value2 = callbackFunction;
                getKvBeanMutableLiveData().postValue(kvBean);
            }

            @Override
            public void fail(Throwable throwable) {
                KVBean kvBean = new KVBean();
                kvBean.key = XNetSystemErrorCode.NET_ERROR.getCode();
                kvBean.value = "{\"code\":\"" + XNetSystemErrorCode.NET_ERROR.getCode() + "\",\"msg\":\"网络异常！\"}";
                kvBean.value2 = callbackFunction;
                getKvBeanMutableLiveData().postValue(kvBean);
            }
        });
    }

    /**
     * 上传文件
     *
     * @param url
     */
    public void uploadFile(final String url,final String type, final ResponseDataListener<?> responseDataListener, Class<?> aClass) {
        // 压缩文件
        Luban.with(ZBaseApplication.get())
                .ignoreBy(150)
                .load(url)//目标图片
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {//开始压缩
                        LOGE("--------> 开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {//压缩成功，拿到压缩的图片，在UI线程
                        mBaseModel.uploadFile(file, type, responseDataListener, aClass);
                    }

                    @Override
                    public void onError(Throwable e) {//压缩失败
                        LOGE("--------> 压缩异常" + e.getMessage());
                        responseDataListener.fail(e);
                    }
                }).launch();//开启压缩

    }

    /**
     * 请求版本信息
     */
    public void requestDownload() {
        mBaseModel.postDownload(new ResponseDataListener<DownLoadBean>() {
            @Override
            public void success(DownLoadBean data) {
                getDownLoadBeanMutableLiveData().postValue(data);
            }

            @Override
            public void fail(Throwable throwable) {
                // 是被就不管了
            }
        });
    }

    public IUser getUserServer() {
        return mBaseModel.mUser;
    }

    public IDevice getDevice() {
        return mBaseModel.mDevice;
    }

    public UserInfo getUserInfo() {
        return mBaseModel.mUser.getUserInfo();
    }

    public String getDeviceId() {
        return mBaseModel.mDevice.getDeviceId();
    }

    public boolean isLogin() {
        return mBaseModel.mUser.isLogin();
    }

    public ServiceBean getService() {
        if (getUserServer() == null) {
            return new ServiceBean();
        }

        return getUserServer().getServiceUrl();
    }

    public String getLanguageType() {
        return iLanguage.getLanguageType();
    }

    public boolean saveLanguageType(String type) {
        return iLanguage.saveLanguage(type);
    }

    public String getString(String key) {
        return iLanguage.getString(key);
    }
}
