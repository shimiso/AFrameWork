package com.eshangke.framework.common;

/**
 * 
 * 系统全局配置.
 * @author 史明松
 */
public interface Constants {
	/**
	 * sampleRateInHz the sample rate expressed in Hertz. 44100Hz is currently the only
	 * rate that is guaranteed to work on all devices, but other rates such as 22050,
	 * 16000, and 11025 may work on some devices.
	 */
	public static final int DEFAULT_SAMPLING_RATE = 22050;

	/**brate compression ratio in KHz*/
	public static final int BIT_RATE = 16;


	public static final String NEWS_ID = "news_id";
	public static final String NEWS_TYPE = "news_type";
	public static final String CHANNEL_POSITION = "channel_position";

}