package com.yft.zbase.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceBean extends KVBean implements Parcelable {

    @SerializedName("assignUri")
    private String assignUri;

    @SerializedName("block")
    private boolean block;

    @SerializedName("backupUris")
    private List<String> backupUris;

    @SerializedName("h5Server")
    private String h5Server;

    @SerializedName("shareServer")
    private String shareServer;

    @SerializedName("customerServiceEmail")
    private String customerServiceEmail;

    @SerializedName("customerServiceNumber")
    private String customerServiceNumber;

    @SerializedName("privacyAgreementUrl")
    private String privacyAgreementUrl;

    @SerializedName("userAgreementUrl")
    private String userAgreementUrl;

    @SerializedName("serviceLinkUrl")
    private String serviceLinkUrl; // 客服

    @SerializedName("realnameUrl")
    private String realnameUrl; // 实名认证告知书

    @SerializedName("serverIps")
    private List<String> serverIps;

    @SerializedName("svipSupportClient")
    private String svipSupportClient;

    @SerializedName("vipSupportClient")
    private String vipSupportClient;

    /**
     * {"assignUri":"http://119.91.137.201",
     * "backupUris":["http://119.91.137.201","http://119.91.137.201","http://119.91.137.201"],
     * "block":false,"h5Server":"http://119.91.137.201","shareServer":"http://119.91.137.201"}
     *privacyAgreementUrl
     * "customerServiceEmail":"xxxx@163.com","customerServiceNumber":"13800138000",
     */

    private boolean isSuss;

    public ServiceBean(){
    }

    //        assignUri = in.readString();
//        block = in.readByte() != 0;
//        backupUris = in.createStringArrayList();
//        h5Server = in.readString();
//        shareServer = in.readString();
//        customerServiceEmail = in.readString();
//        customerServiceNumber = in.readString();
//        privacyAgreementUrl = in.readString();
//        userAgreementUrl = in.readString();
//        isSuss = in.readByte() != 0;
//        serviceLinkUrl = in.readString();
//        realnameUrl = in.readString();

    protected ServiceBean(Parcel in) {
        assignUri = in.readString();
        block = in.readByte() != 0;
        backupUris = in.createStringArrayList();
        h5Server = in.readString();
        shareServer = in.readString();
        customerServiceEmail = in.readString();
        customerServiceNumber = in.readString();
        privacyAgreementUrl = in.readString();
        userAgreementUrl = in.readString();
        isSuss = in.readByte() != 0;
        serviceLinkUrl = in.readString();
        realnameUrl = in.readString();
        serverIps = in.createStringArrayList();
        svipSupportClient = in.readString();
        vipSupportClient = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(assignUri);
        dest.writeByte((byte) (block ? 1 : 0));
        dest.writeStringList(backupUris);
        dest.writeString(h5Server);
        dest.writeString(shareServer);
        dest.writeString(customerServiceEmail);
        dest.writeString(customerServiceNumber);
        dest.writeString(privacyAgreementUrl);
        dest.writeString(userAgreementUrl);
        dest.writeByte((byte) (isSuss ? 1 : 0));
        dest.writeString(serviceLinkUrl);
        dest.writeString(realnameUrl);
        dest.writeStringList(serverIps);
        dest.writeString(svipSupportClient);
        dest.writeString(vipSupportClient);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServiceBean> CREATOR = new Creator<ServiceBean>() {
        @Override
        public ServiceBean createFromParcel(Parcel in) {
            return new ServiceBean(in);
        }

        @Override
        public ServiceBean[] newArray(int size) {
            return new ServiceBean[size];
        }
    };

    public String getAssignUri() {
        return assignUri;
    }

    public void setAssignUri(String assignUri) {
        this.assignUri = assignUri;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public List<String> getBackupUris() {
        return backupUris;
    }

    public void setBackupUris(List<String> backupUris) {
        this.backupUris = backupUris;
    }

    public boolean isSuss() {
        return isSuss;
    }

    public void setSuss(boolean suss) {
        isSuss = suss;
    }

    public String getH5Server() {
        return h5Server;
    }

    public void setH5Server(String h5Server) {
        this.h5Server = h5Server;
    }

    public String getShareServer() {
        return shareServer;
    }

    public void setShareServer(String shareServer) {
        this.shareServer = shareServer;
    }

    public String getCustomerServiceEmail() {
        return customerServiceEmail;
    }

    public void setCustomerServiceEmail(String customerServiceEmail) {
        this.customerServiceEmail = customerServiceEmail;
    }

    public String getCustomerServiceNumber() {
        return customerServiceNumber;
    }

    public void setCustomerServiceNumber(String customerServiceNumber) {
        this.customerServiceNumber = customerServiceNumber;
    }

    public String getPrivacyAgreementUrl() {
        return privacyAgreementUrl;
    }

    public void setPrivacyAgreementUrl(String privacyAgreementUrl) {
        this.privacyAgreementUrl = privacyAgreementUrl;
    }

    public String getUserAgreementUrl() {
        return userAgreementUrl;
    }

    public void setUserAgreementUrl(String userAgreementUrl) {
        this.userAgreementUrl = userAgreementUrl;
    }

    public String getServiceLinkUrl() {
        return serviceLinkUrl;
    }

    public void setServiceLinkUrl(String serviceLinkUrl) {
        this.serviceLinkUrl = serviceLinkUrl;
    }

    public String getRealnameUrl() {
        return realnameUrl;
    }

    public void setRealnameUrl(String realnameUrl) {
        this.realnameUrl = realnameUrl;
    }

    public List<String> getServerIps() {
        return serverIps;
    }

    public void setServerIps(List<String> serverIps) {
        this.serverIps = serverIps;
    }

    public String getSvipSupportClient() {
        return svipSupportClient;
    }

    public void setSvipSupportClient(String svipSupportClient) {
        this.svipSupportClient = svipSupportClient;
    }

    public String getVipSupportClient() {
        return vipSupportClient;
    }

    public void setVipSupportClient(String vipSupportClient) {
        this.vipSupportClient = vipSupportClient;
    }
}
