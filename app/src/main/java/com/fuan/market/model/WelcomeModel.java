package com.fuan.market.model;

import com.yft.zbase.base.BaseModel;
import com.yft.zbase.bean.TargetBean;
import com.yft.zbase.xnet.InterfacePath;
import com.yft.zbase.xnet.ResponseDataListener;

import java.util.TreeMap;

public class WelcomeModel extends BaseModel {
    public void postOpenStart(ResponseDataListener<TargetBean> responseDataListener) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        mNetWork.easyPost(mUser.getBastServiceURL() + InterfacePath.START_PAGE,
                getRequestParameter(treeMap),
                responseDataListener,
                TargetBean.class);
    }
}
