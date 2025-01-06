package com.yft.user.model;

import static com.yft.zbase.utils.Logger.LOGE;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.gongjiebin.latticeview.KVBean;
import com.google.gson.JsonObject;
import com.yft.zbase.base.BaseViewModel;
import com.yft.zbase.bean.UserInfo;
import com.yft.zbase.utils.JsonUtil;
import com.yft.zbase.utils.ToastUtils;
import com.yft.zbase.xnet.ResponseDataListener;

public class UserViewModel extends BaseViewModel {

    private MutableLiveData<UserInfo> mMutableUserLiveData = new MutableLiveData<>();

    private MutableLiveData<KVBean> mMutableSuccessLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mExitMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mUpdatePwdMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<UserInfo> getMutableUserLiveData() {
        return mMutableUserLiveData;
    }

    public MutableLiveData<KVBean> getMutableSuccessLiveData() {
        return mMutableSuccessLiveData;
    }

    public MutableLiveData<String> getExitMutableLiveData() {
        return mExitMutableLiveData;
    }

    public MutableLiveData<String> getUpdatePwdMutableLiveData() {
        return mUpdatePwdMutableLiveData;
    }

    private LoginModel mLoginModel;
    public UserViewModel() {
        super();
        mLoginModel  = new LoginModel();
    }


    public int getStatusBarHeight() {
        return getDevice().getStatusBarHi();
    }

    public void requestMyInfo() {
        mLoginModel.postMyUserInfo(new ResponseDataListener<UserInfo>() {
            @Override
            public void success(UserInfo data) {
                if (data != null) {
                    if(mLoginModel.getUserInfo() != null) {
                        data.setToken(mLoginModel.getUserInfo().getToken());
                    }
                    mBaseModel.saveUserInfo(data);
                    mMutableUserLiveData.postValue(data);
                }
            }

            @Override
            public void fail(Throwable throwable) {
                if (getErrorMutableLiveData() != null) {
                    getErrorMutableLiveData().postValue(throwable.getMessage());
                }
            }
        });
    }


    public void requestUpdateUser(final String logo,final String nikeName) {
        mLoginModel.postUpdateUserInfo(logo, nikeName, new ResponseDataListener<String>() {
            @Override
            public void success(String data) {
                UserInfo userInfo = getUserServer().getUserInfo();
                if (!TextUtils.isEmpty(logo)) {
                    userInfo.setLogo(logo);
                }

                if (!TextUtils.isEmpty(nikeName)) {
                    userInfo.setNickname(nikeName);
                }
                getUserServer().sveaUserInfo(userInfo);
                KVBean kvBean = new KVBean();
                kvBean.setKey(1);
                mMutableSuccessLiveData.postValue(kvBean);
            }

            @Override
            public void fail(Throwable throwable) {
                ToastUtils.currencyToast(throwable);
                if (getErrorMutableLiveData()!=null) {
                    getErrorMutableLiveData().postValue("1");
                }
            }
        });
    }

    public void updatePhotoFile(String url) {
        uploadFile(url, "logo", new ResponseDataListener<String>() {
            @Override
            public void success(String data) {
                KVBean kvBean = new KVBean();

                JsonObject jsonObject = JsonUtil.parseJsonToJsonObj(data);
                if (jsonObject.has("data")) {
                   JsonObject dataJson =  jsonObject.getAsJsonObject("data");
                   if (dataJson != null && dataJson.has("url")) {
                       kvBean.setValue(dataJson.get("url").getAsString());
                       kvBean.setKey(0);
                       mMutableSuccessLiveData.postValue(kvBean);
                       return;
                   }
                }

                kvBean.setValue("");
                kvBean.setKey(-1);
                mMutableSuccessLiveData.postValue(kvBean);
            }

            @Override
            public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
               LOGE("UserViewModel","totalSize=>" + totalSize + "progress=>" + progress);
            }

            @Override
            public void fail(Throwable throwable) {
                ToastUtils.currencyToast(throwable);
                if (getErrorMutableLiveData()!=null) {
                    getErrorMutableLiveData().postValue("0");
                }
            }
        }, String.class);
    }


    /**
     * 退出登录
     */
    public void exitLogin() {
        mLoginModel.exitLogin(new ResponseDataListener<String>() {
            @Override
            public void success(String data) {
                mExitMutableLiveData.postValue("0");
            }

            @Override
            public void fail(Throwable throwable) {
                ToastUtils.currencyToast(throwable);
                if (getErrorMutableLiveData()!=null) {
                    getErrorMutableLiveData().postValue("3");
                }
            }
        });
    }

    /**
     * logOff
     */
    public void userLogOff(String describe) {
        mLoginModel.userLogOff(describe, new ResponseDataListener<String>() {
            @Override
            public void success(String data) {
                mExitMutableLiveData.postValue("1");
            }

            @Override
            public void fail(Throwable throwable) {
                ToastUtils.currencyToast(throwable);
                if (getErrorMutableLiveData()!=null) {
                    getErrorMutableLiveData().postValue("3");
                }
            }
        });
    }
}
