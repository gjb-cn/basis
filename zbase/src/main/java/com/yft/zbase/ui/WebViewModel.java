package com.yft.zbase.ui;

import androidx.lifecycle.MutableLiveData;

import com.yft.zbase.base.BaseViewModel;
import com.yft.zbase.server.DynamicMarketManage;
import com.yft.zbase.server.IPay;
import com.yft.zbase.server.IServerAgent;
import com.yft.zbase.xnet.ResponseDataListener;

public class WebViewModel extends BaseViewModel {

    private IPay iPay;

    public WebViewModel() {
        super();
        iPay = DynamicMarketManage.getInstance().getServer(IServerAgent.PAY_SERVER);
    }

    private MutableLiveData<String> mSuccessMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<String> getSuccessMutableLiveData() {
        return mSuccessMutableLiveData;
    }

    public void updateImageFile(String url, String module) {
        uploadFile(url, module, new ResponseDataListener<String>() {
            @Override
            public void success(String data) {
                mSuccessMutableLiveData.postValue(data);
            }

            @Override
            public void fail(Throwable throwable) {
                if (getErrorMutableLiveData() != null) {
                    getErrorMutableLiveData().postValue(throwable.getMessage());
                }
            }
        }, String.class);
    }

    public IPay getPay() {
        return iPay;
    }
}
