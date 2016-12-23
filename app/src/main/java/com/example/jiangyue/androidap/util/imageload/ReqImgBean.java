package com.example.jiangyue.androidap.util.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ReqImgBean {

    // 当前的上下文
    private Context mContext = null;

    // 图片地址的URL
    private String mImgUrl = "";

    // 图片存储地址
    // 如用到listView或者其他组件的下载,对象下标
    private int index = -1;

    // 需要的图片宽度
    private int imageWidth = 0;

    // 需要的图片高度
    private int imageHeight = 0;

    private int mImgType = CommonConstants.TYPE_HEAD_IMG;

    private int mPostion;

    public int getPostion() {
        return mPostion;
    }

    public void setPostion(int position) {
        this.mPostion = position;
    }

    public int getImgType() {
        return mImgType;
    }

    public void setImgType(int imgType) {
        this.mImgType = imgType;
    }

    // 下载图片需要的json
    private String json = "";
    // 主线程创建handler实例
    // 主线程创建监听器
    private String localPath = "";
    // 图片存储地址
    Bitmap mBitmap;
    // 请求来源
    private int reqMode = 0;
    // 监听
    private OnImgLoadListener listener = null;

    public static final int REQ_MODE_UPLOAD = 0;
    public static final int REQ_MODE_DOWNLOAD = 1;
    public static final int REQ_MODE_UPLOADPIC = 2;

    // 图片长度
    private long fileLength;
    // 当前长度
    private long currentLen;

    private ImageView mIcon;

    private String mChatId;



    public ImageView getIcon() {
        return mIcon;
    }

    public void setIcon(ImageView mIcon) {
        this.mIcon = mIcon;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getReqMode() {
        return reqMode;
    }

    public void setReqMode(int reqMode) {
        this.reqMode = reqMode;
    }

    public OnImgLoadListener getListener() {
        return listener;
    }

    public void setListener(OnImgLoadListener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return mContext;
    }

    public String getChatId() {
        return mChatId;
    }

    public void setChatId(String chatId) {
        this.mChatId = chatId;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.mImgUrl = imgUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getCurrentLen() {
        return currentLen;
    }

    public void setCurrentLen(long currentLen) {
        this.currentLen = currentLen;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj.getClass().getName().equals(this.getClass().getName())) {
            if (obj instanceof ReqImgBean) {
                ReqImgBean temp = (ReqImgBean) obj;
                if (temp.getImgUrl().equals(this.getImgUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
}