package com.example.jiangyue.androidap.util.imageload;

public class ReqFileBean {

    public static final int REQ_MODE_DOWNLOAD = 1;// 下载
    // 正文的id
    private String newsId = "";
    // 图片地址的URL
    private String mDownLoadUrl = "";
    // 图片存储地址
    // 如用到listView或者其他组件的下载,对象下标
    private int index = -1;
    // 需要的图片宽度
    private int imageWidth = 0;
    // 需要的图片高度
    private int imageHeight = 0;
    // 下载图片需要的json
    private String json = "";
    // 主线程创建handler实例
    // 主线程创建监听器

    private String fileName;// 需要下载的图片名字

    private OnFileLoadListener listener = null;

    // 上传的进度
    private int mPercent;

    // 文件总长度
    private long fileLength;
    // 文件当前上传或下载的长度
    private long currentLen;

    // 请求来源
    private int reqMode = 0;

    // 多文件上传时代表第几个位置
    private int mPostion;

    private String mChatId;

    // 原始的图片本地地址
    private String mFilePath = "";

    public String getChatId() {
        return mChatId;
    }

    public void setChatId(String chatId) {
        this.mChatId = chatId;
    }

    public int getPercent() {
        return mPercent;
    }

    public void setPercent(int mPercent) {
        this.mPercent = mPercent;
    }

    public void setFilePath(String filename) {
        this.mFilePath = filename;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public int getReqMode() {
        return reqMode;
    }

    public void setReqMode(int reqMode) {
        this.reqMode = reqMode;
    }

    public OnFileLoadListener getListener() {
        return listener;
    }

    public void setListener(OnFileLoadListener listener) {
        this.listener = listener;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getDownLoadUrl() {
        return mDownLoadUrl;
    }

    public void setDownLoadUrl(String imgUrl) {
        this.mDownLoadUrl = imgUrl;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj.getClass().getName().equals(this.getClass().getName())) {
            if (obj instanceof ReqFileBean) {
                ReqFileBean temp = (ReqFileBean) obj;
                if (temp.getDownLoadUrl().equalsIgnoreCase(this.getDownLoadUrl())) {
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

    public int getPostion() {
        return mPostion;
    }

    public void setPostion(int position) {
        this.mPostion = position;
    }
}