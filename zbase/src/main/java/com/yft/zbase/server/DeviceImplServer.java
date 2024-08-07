package com.yft.zbase.server;

import android.content.Context;
import android.media.MediaDrm;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.tencent.mmkv.MMKV;
import com.yft.zbase.ZBaseApplication;
import com.yft.zbase.utils.Constant;
import com.yft.zbase.utils.Md5Encryption;
import com.yft.zbase.utils.Utils;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


public class DeviceImplServer implements IDevice {
    private MMKV paraMk;
    // 设备唯一码(保持一致)
    private String serialNumber;
    private Context mContext;

    @Override
    public void initServer(Context context) {
        mContext = context;
        paraMk =  MMKV.mmkvWithID(Constant.PARAMETER, MMKV.MULTI_PROCESS_MODE);
        if (paraMk.containsKey(Constant.PARAMETER_KEY_UUID)) {
            serialNumber = paraMk.decodeString(Constant.PARAMETER_KEY_UUID);
        } else {
            // 获取设备唯一码
            serialNumber = getSerialNumber();
            serialNumber = serialNumber.replaceAll("-","");
            paraMk.encode(Constant.PARAMETER_KEY_UUID, serialNumber);
        }
        getStatusBarHeight(context);
    }

    @Override
    public <T extends IServerAgent> T getServer() {
        return (T) this;
    }

    @Override
    public String serverName() {
        return DEVICE_SERVER;
    }

    @Override
    public void cleanInfo() {
        if (paraMk != null) {
            paraMk.clear();
        }
    }

    @Override
    public String getDeviceId() {
        return serialNumber;
    }

    @Override
    public int getStatusBarHi() {
        return getStatusBarHeight(mContext);
    }

    @Override
    public String getAndroid() {
        return "Android";
    }

    @Override
    public String getAppAlias() {
        return "rshort";
    }

    @Override
    public String getModel() {
        return Build.MODEL;
    }

    public int getStatusBarHeight(Context context) {
        int statusBarHeight = paraMk.decodeInt(Constant.PARAMETER_STATUS_BAR_HI, 0);
        if (statusBarHeight == 0) {
            statusBarHeight = getStatusBar(context);
            if (statusBarHeight == 0) {
                // 给个默认值
                paraMk.encode(Constant.PARAMETER_STATUS_BAR_HI, Utils.dip2px(context, 34));
            } else {
                paraMk.encode(Constant.PARAMETER_STATUS_BAR_HI, statusBarHeight);
            }
        }
        return  statusBarHeight;
    }


    private int getStatusBar(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object object = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(object);
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (statusBarHeight == 0) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }

        if (statusBarHeight == 0) {
            statusBarHeight = (int) Math.ceil(34 * context.getResources().getDisplayMetrics().density);
        }

        return statusBarHeight;
    }

    public String getSerialNumber() {
        return getDeviceOther();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            serial = getDeviceOther();
//            return serial;
//        }
//        UUID wideVineUuid = new UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L);
//        MediaDrm wvDrm = null;
//        try {
//            wvDrm = new MediaDrm(wideVineUuid);
//            byte[] wideVineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
//            //optional encoding to convert the array in string.
//            return Base64.encodeToString(wideVineId, Base64.NO_WRAP);
//        } catch (Exception e) {
//            return getDeviceOther();
//        } finally {
//            if (wvDrm != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    wvDrm.close();
//                } else {
//                    wvDrm.release();
//                }
//            }
//        }
    }

    public String getDeviceOther() {
        String deviceId = getDeviceOtherStr();
        try {
            return Md5Encryption.parseStrToMd5U32(deviceId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDeviceOtherStr() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
