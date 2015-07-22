package org.zywx.wbpalmstar.plugin.uexmultidownloader.vo;

import java.io.Serializable;

/**
 * Created by ylt on 15/7/9.
 */
public class DownloadItemVO implements Serializable{

    private static final long serialVersionUID = -4910964250791853480L;

    private String id;
    private String name;
    private int totalLength;
    private int downloadLength;
    private String url;
    private String savePath;
    private State state=State.PAUSE;//下载状态  0.下载中  1.暂停  2.已完成
    private int progress;
    private String mimeType;

    private int speed=0;

    public enum State {
        LOADING,PAUSE,FINISH
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public int getDownloadLength() {
        return downloadLength;
    }

    public void setDownloadLength(int downloadLength) {
        this.downloadLength = downloadLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
