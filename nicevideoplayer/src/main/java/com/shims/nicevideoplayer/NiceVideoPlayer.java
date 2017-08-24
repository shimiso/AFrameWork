package com.shims.nicevideoplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class NiceVideoPlayer extends FrameLayout implements INiceVideoPlayer,TextureView.SurfaceTextureListener{

    private static final String TAG = "NiceVideoPlayer";

    private FrameLayout mContainer;

    private Context mContext;

    private IMediaPlayer mMediaPlayer;

    private AudioManager mAudioManager;

    /**视频地址，可以是本地，也可以是网络视频**/
    private String mUrl;
    /**请求header**/
    private Map<String, String> mHeaders;

    /** 当前播放状态 **/
    private int mCurrentState = STATE_IDLE;
    /** 播放错误 **/
    public static final int STATE_ERROR = -1;
    /** 播放未开始 **/
    public static final int STATE_IDLE = 0;
    /** 播放准备中 **/
    public static final int STATE_PREPARING = 1;
    /** 播放准备就绪 **/
    public static final int STATE_PREPARED = 2;
    /** 正在播放 **/
    public static final int STATE_PLAYING = 3;
    /** 暂停播放 **/
    public static final int STATE_PAUSED = 4;
    /** 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放) **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /** 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停 **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    /** 播放完成 **/
    public static final int STATE_COMPLETED = 7;

    /** 当前播放器的模式 **/
    private int mCurrentMode = MODE_NORMAL;
    /** 普通模式 **/
    public static final int MODE_NORMAL = 10;
    /** 全屏模式 **/
    public static final int MODE_FULL_SCREEN = 11;
    /** 小窗口模式 **/
    public static final int MODE_TINY_WINDOW = 12;

    private int mPlayerType = TYPE_IJK;
    /** IjkPlayer  **/
    public static final int TYPE_IJK = 111;
    /**  MediaPlayer **/
    public static final int TYPE_NATIVE = 222;

    private NiceVideoPlayerController mController;

    private NiceTextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    private int mBufferPercentage;

    //是否从上一次的位置继续播放
    private boolean continueFromLastPosition = true;
    private long skipToPosition;

    public NiceVideoPlayer(Context context) {
        this(context, null);
    }

    public NiceVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    /**
     * 设置播放器类型
     *
     * @param playerType IjkPlayer or MediaPlayer.
     */
    public void setPlayerType(int playerType) {
        mPlayerType = playerType;
    }

    public void setUp(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
    }

    public void setController(NiceVideoPlayerController controller) {
        mContainer.removeView(mController);
        mController = controller;
        mController.reset();
        mController.setNiceVideoPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);
    }

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            NiceVideoPlayerManager.instance().setCurrentNiceVideoPlayer(this);
            initAudioManager();
            initMediaPlayer();
            initTextureView();
            addTextureView();
        } else {
            Log.d(TAG,"NiceVideoPlayer只有在mCurrentState == STATE_IDLE时才能调用start方法.");
        }
    }

    private void initAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            switch (mPlayerType) {
                case TYPE_NATIVE:
                    mMediaPlayer = new AndroidMediaPlayer();
                    break;
                case TYPE_IJK:
                default:
                    mMediaPlayer = new IjkMediaPlayer();
                    break;
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new NiceTextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mContainer.addView(mTextureView, 0, params);
    }

    @Override
    public void start(long position) {
        skipToPosition = position;
        start();
    }

    @Override
    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG,"STATE_PLAYING");
        } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG,"STATE_BUFFERING_PLAYING");
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mMediaPlayer.reset();
            openMediaPlayer();
        } else {
            Log.d(TAG,"NiceVideoPlayer在mCurrentState == " + mCurrentState + "时不能调用restart()方法.");
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG,"STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG,"STATE_BUFFERING_PAUSED");
        }
    }

    @Override
    public void seekTo(long pos) {

    }

    @Override
    public void setVolume(int volume) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {
        this.continueFromLastPosition = continueFromLastPosition;
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mCurrentMode == MODE_FULL_SCREEN;
    }

    @Override
    public boolean isTinyWindow() {
        return mCurrentMode == MODE_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return mCurrentMode == MODE_NORMAL;
    }

    @Override
    public int getMaxVolume() {
        return 0;
    }

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public float getSpeed(float speed) {
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        return 0;
    }

    @Override
    public void enterFullScreen() {

    }

    @Override
    public boolean exitFullScreen() {
        return false;
    }

    @Override
    public void enterTinyWindow() {

    }

    @Override
    public boolean exitTinyWindow() {
        return false;
    }

    @Override
    public void releasePlayer() {

    }

    @Override
    public void release() {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openMediaPlayer();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void openMediaPlayer() {
        // 屏幕常量
        mContainer.setKeepScreenOn(true);
        // 设置监听
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        // 设置dataSource
        try {
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl), mHeaders);
            if (mSurface == null) {
                mSurface = new Surface(mSurfaceTexture);
            }
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG,"STATE_PREPARING");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"打开播放器发生错误", e);
        }
    }

    /**播放准备就绪监听**/
    private IMediaPlayer.OnPreparedListener mOnPreparedListener=new IMediaPlayer.OnPreparedListener(){

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            mCurrentState = STATE_PREPARED;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG,"onPrepared ——> STATE_PREPARED");
            iMediaPlayer.start();
            // 从上次的保存位置播放
            if (continueFromLastPosition) {
                long savedPlayPosition = NiceUtil.getSavedPlayPosition(mContext, mUrl);
                iMediaPlayer.seekTo(savedPlayPosition);
            }
            // 跳到指定位置播放
            if (skipToPosition != 0) {
                iMediaPlayer.seekTo(skipToPosition);
            }
        }
    };
    /**视频尺寸变化监听**/
    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener=new IMediaPlayer.OnVideoSizeChangedListener(){

        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sar_num, int sar_den) {
            mTextureView.adaptVideoSize(width, height);
            Log.d(TAG,"onVideoSizeChanged ——> width：" + width + "， height：" + height);
        }
    };
    /**播放完成监听**/
    private IMediaPlayer.OnCompletionListener mOnCompletionListener=new IMediaPlayer.OnCompletionListener(){

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            mCurrentState = STATE_COMPLETED;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG,"onCompletion ——> STATE_COMPLETED");
            // 清除屏幕常亮
            mContainer.setKeepScreenOn(false);
        }
    };
    /**播放错误监听**/
    private IMediaPlayer.OnErrorListener mOnErrorListener=new IMediaPlayer.OnErrorListener(){

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            mCurrentState = STATE_ERROR;
            mController.onPlayStateChanged(mCurrentState);
            return false;
        }
    };
    /**播放信息事件 在有警告或错误信息时调用。如：开始缓冲、缓冲结束、下载速度变化**/
    private IMediaPlayer.OnInfoListener mOnInfoListener =new IMediaPlayer.OnInfoListener(){

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                mCurrentState = STATE_PLAYING;
                mController.onPlayStateChanged(mCurrentState);
                Log.d(TAG,"onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    Log.d(TAG,"onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    Log.d(TAG,"onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                mController.onPlayStateChanged(mCurrentState);
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    mController.onPlayStateChanged(mCurrentState);
                    Log.d(TAG,"onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    mController.onPlayStateChanged(mCurrentState);
                    Log.d(TAG,"onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                // 视频旋转了extra度，需要恢复
                if (mTextureView != null) {
                    mTextureView.setRotation(extra);
                    Log.d(TAG,"视频旋转角度：" + extra);
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                Log.d(TAG,"视频不能seekTo，为直播视频");
            } else {
                Log.d(TAG,"onInfo ——> what：" + what);
            }
            return true;
        }
    };
    /**缓冲中监听**/
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener=new IMediaPlayer.OnBufferingUpdateListener(){

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            mBufferPercentage = percent;
        }
    };
}
