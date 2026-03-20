package com.yft.zbase.updateapk;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;


import com.hk.xnet.IXNet;
import com.hk.xnet.XNetImpl;
import com.yft.zbase.server.Versions;
import com.yft.zbase.ZBaseApplication;
import com.yft.zbase.bean.TargetBean;
import com.yft.zbase.router.RouterFactory;
import com.yft.zbase.xnet.ResponseDataListener;

import java.io.File;
import java.util.ArrayList;

public class VersionUpdateUtil {
    // 更新的对话框
    private DialogUpData dialogUpData;
    private UpdateApkListener updateApkListener;
    private IXNet iNetWork;
    private boolean isMustUpdate;
    private String mPackageName;

    public boolean isMustUpdate() {
        return isMustUpdate;
    }

    public VersionUpdateUtil() {
        // 获取请求类
        iNetWork = XNetImpl.getInstance();
    }

    public void setUpdateApkListener(UpdateApkListener updateApkListener) {
        this.updateApkListener = updateApkListener;
    }

    /**
     * 下载最新版本的apk
     *
     * @param list   更新内容
     * @param apkUrl 下载地址
     * @param con
     */
    public void showDownloadApkDialog(ArrayList<String> list, final String apkUrl,
                                      boolean isMustUpdate,String packageName,
                                      final FragmentActivity con, final String shortName) {
        this.mPackageName = packageName;
        this.isMustUpdate = isMustUpdate;
        dialogUpData = new DialogUpData(con, list, isMustUpdate, new UpdataDialogCallback() {
            @Override
            public void cancle(Dialog dialog) {
                iNetWork.cancelAllRequest();
            }

            @Override
            public void updata(Dialog dialog) {
                // 正式下载
                startDownApk(apkUrl, con);
//                if (Versions.OFFICIAL_VERSION_NAME.getName().equals(shortName)) {
//                     startDownApk(apkUrl, con);
//                } else {
//                    // 分享的是调整到外部下载游览器下载
//                    TargetBean targetBean = new TargetBean();
//                    targetBean.setActionType(RouterFactory.JUMP_OUT_LINK_MODULE);
//                    targetBean.setTarget(apkUrl);
//                    RouterFactory.jumpToActivity(con, targetBean);
//                }
            }
        });
        dialogUpData.show();
    }

    /**
     * 开始下载
     *
     */
    public void startDownApk(final String apdUrl, FragmentActivity activity) {
        if (!dialogUpData.isShowing()) {
            dialogUpData.show();
        }

        iNetWork.downLoadFile(apdUrl, ZBaseApplication.get().getFilesDir().toString(),new ResponseDataListener<String>() {
            @Override
            public void success(String data) {
                if (dialogUpData.isShowing() && !isMustUpdate) {
                    // 非强制更新，就要关闭
                    dialogUpData.dismiss();
                }

                if (!TextUtils.isEmpty(data)) {
                    File file = new File(data);
                    installApk(file, activity);
                }
            }

            @Override
            public void fail(Throwable throwable) {

            }

            @Override
            public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                double d = ((double)currentSize / (double)totalSize);
                int bfb = (int) (100 * d);
                dialogUpData.setProgress(String.valueOf(bfb));

            }
        });
    }

    /**
     * 安装下载完成的APK
     * @param apkFile
     */
    public boolean installApk(File apkFile, FragmentActivity activity){
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(ZBaseApplication.get(), mPackageName + ".fileProvider", apkFile);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        activity.startActivity(install);
        return true;
    }

    public interface UpdateApkListener {
        // 需要打开权限。 才能继续
        void openPermission();
    }
}
