package com.eshangke.framework.bean;

/**
 * Entity mapped to table NEWS_CHANNEL_TABLE.
 */
public class Channel {
    //频道名称
    private String channelName;
    //频道ID
    private String channelId;
    //频道类型
    private String channelType;
    //是否选中 0选中 1没选
    private boolean channelSelect;
    //排序
    private int channelIndex;
    //是否固定 0固定 1不固定
    private Boolean channelFixed;

    public Channel() {
    }

    public Channel(String channelName) {
        this.channelName = channelName;
    }

    public Channel(String channelName, String channelId, String channelType, boolean channelSelect, int channelIndex, Boolean channelFixed) {
        this.channelName = channelName;
        this.channelId = channelId;
        this.channelType = channelType;
        this.channelSelect = channelSelect;
        this.channelIndex = channelIndex;
        this.channelFixed = channelFixed;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public boolean getChannelSelect() {
        return channelSelect;
    }

    public void setChannelSelect(boolean channelSelect) {
        this.channelSelect = channelSelect;
    }

    public int getChannelIndex() {
        return channelIndex;
    }

    public void setChannelIndex(int channelIndex) {
        this.channelIndex = channelIndex;
    }

    public Boolean getChannelFixed() {
        return channelFixed;
    }

    public void setChannelFixed(Boolean channelFixed) {
        this.channelFixed = channelFixed;
    }

}
