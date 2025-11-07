package com.eshangke.framework.presenter;

import android.content.Context;

import com.eshangke.framework.bean.Channel;
import com.eshangke.framework.db.DBManager;
import com.eshangke.framework.db.SQLiteTemplate;

import java.util.List;

/**
 * 类的说明：
 * 作者：shims
 * 创建时间：2016/10/27 0027 17:24
 */
public class ChannelPresenter {
    private SQLiteTemplate template;

    public ChannelPresenter() {

    }

    public ChannelPresenter(Context context) {
        DBManager dbManager = DBManager.create(context);
        template = SQLiteTemplate.getInstance(dbManager);
    }

    /**
     * 添加或者移除我的频道
     *
     * @param channel
     * @param isChannelMine
     */
    public void onItemAddOrRemove(Channel channel, boolean isChannelMine) {
        Integer maxIndex = template.queryForObject((cursor, index) -> {
            Integer maxIndex1 = cursor.getInt(cursor.getColumnIndex("max_index"));
            return maxIndex1;
        }, "select max(channel_index) as max_index from channel where channel_select="+(isChannelMine?"1":"0"), null);

        if (isChannelMine) {
            template.execSQL("update CHANNEL set channel_select=1, channel_index="+maxIndex+"+1 where _id=" + channel.getChannelId());
        } else {
            template.execSQL("update CHANNEL set channel_select=0, channel_index="+maxIndex+"+1 where _id=" + channel.getChannelId());
        }

    }

    /**
     * 获取我的频道列表
     *
     * @return
     */
    public List<Channel> getChannelsMine() {
        List<Channel> channels = template.queryForList((cursor, index) -> {
            Channel channel = new Channel();
            channel.setChannelId(cursor.getString(cursor.getColumnIndex("_ID")));
            channel.setChannelName(cursor.getString(cursor.getColumnIndex("CHANNEL_NAME")));
            channel.setChannelType(cursor.getString(cursor.getColumnIndex("CHANNEL_TYPE")));
            template.execSQL("update channel set channel_index=" + index + "  where _id=" + channel.getChannelId());
            channel.setChannelIndex(index);
            channel.setChannelSelect((cursor.getInt(cursor.getColumnIndex("CHANNEL_SELECT")) == 0 ? true : false));
            channel.setChannelFixed((cursor.getInt(cursor.getColumnIndex("CHANNEL_FIXED")) == 0 ? true : false));
            return channel;
        }, "SELECT * FROM CHANNEL WHERE CHANNEL_SELECT=? order by channel_index asc", new String[]{"0"});

        return channels;
    }


    /**
     * 获取更多频道列表
     *
     * @return
     */
    public List<Channel> getChannelsMore() {
        List<Channel> channels = template.queryForList((cursor, index) -> {
            Channel channel = new Channel();
            channel.setChannelId(cursor.getString(cursor.getColumnIndex("_ID")));
            channel.setChannelName(cursor.getString(cursor.getColumnIndex("CHANNEL_NAME")));
            channel.setChannelType(cursor.getString(cursor.getColumnIndex("CHANNEL_TYPE")));
            template.execSQL("update channel set channel_index=" + index + "  where _id=" + channel.getChannelId());
            channel.setChannelIndex(index+1);
            channel.setChannelSelect((cursor.getInt(cursor.getColumnIndex("CHANNEL_SELECT")) == 0 ? true : false));
            channel.setChannelFixed((cursor.getInt(cursor.getColumnIndex("CHANNEL_FIXED")) == 0 ? true : false));
            return channel;
        }, "SELECT * FROM CHANNEL WHERE CHANNEL_SELECT=? ORDER BY CHANNEL_INDEX", new String[]{"1"});

        return channels;
    }
    /**
     * 我的频道项移动
     *
     * @param fromPosition
     * @param toPosition
     */
    public void onItemMove(int fromPosition, int toPosition) {
        List<Channel> channels =  getChannelsMine();
        Channel fromChannel = channels.get(fromPosition);
        Channel toChannel =  channels.get(toPosition);

        if(Math.abs(fromPosition - toPosition) == 1){
            template.execSQL("update CHANNEL set channel_index=" + toPosition + " where _id=" + fromChannel.getChannelId());
            template.execSQL("update CHANNEL set channel_index=" + fromPosition + " where _id=" + toChannel.getChannelId());
        }else if(fromPosition - toPosition > 0){
            List<Channel> inChannels=getMineChannelsIn(toPosition-1,fromPosition);
            for(Channel channel:inChannels){
                template.execSQL("update CHANNEL set channel_index=" + (channel.getChannelIndex()+1) + " where _id=" + channel.getChannelId());
            }
            template.execSQL("update CHANNEL set channel_index=" + toPosition + " where _id=" + fromChannel.getChannelId());
        }else if(fromPosition - toPosition < 0){
            List<Channel> inChannels=getMineChannelsIn(fromPosition,toPosition+1);
            for(Channel channel:inChannels){
                template.execSQL("update CHANNEL set channel_index=" + (channel.getChannelIndex()-1) + " where _id=" + channel.getChannelId());
            }
            template.execSQL("update CHANNEL set channel_index=" + toPosition + " where _id=" + fromChannel.getChannelId());
        }
    }

    /**
     * 获取我的频道列表
     * @param start 开始
     * @param end 结束
     * @return
     */
    public List<Channel> getMineChannelsIn(int start,int end) {
        List<Channel> channels = template.queryForList((cursor, index) -> {
            Channel channel = new Channel();
            channel.setChannelId(cursor.getString(cursor.getColumnIndex("_ID")));
            channel.setChannelName(cursor.getString(cursor.getColumnIndex("CHANNEL_NAME")));
            channel.setChannelType(cursor.getString(cursor.getColumnIndex("CHANNEL_TYPE")));
            channel.setChannelIndex(cursor.getInt(cursor.getColumnIndex("CHANNEL_INDEX")));
            channel.setChannelSelect((cursor.getInt(cursor.getColumnIndex("CHANNEL_SELECT")) == 0 ? true : false));
            channel.setChannelFixed((cursor.getInt(cursor.getColumnIndex("CHANNEL_FIXED")) == 0 ? true : false));
            return channel;
        }, "SELECT * FROM CHANNEL WHERE CHANNEL_SELECT=0  and channel_index>? and channel_index<? order by channel_index asc", new String[]{start+"",end+""});

        return channels;
    }
}
