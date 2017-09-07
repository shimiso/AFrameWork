package com.shims.nicevideoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by shims on 2017/8/24.
 */
public class TxVideoPlayerController extends NiceVideoPlayerController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ChangeClarityDialog.OnClarityChangedListener {
    private Context mContext;
    private ImageView mImage;//播放前底图
    private ImageView mCenterStart; //中间开始播放按钮

    /** 顶部控制区 **/
    private LinearLayout mTop;
    private ImageView mBack;
    //标题
    private TextView mTitle;
    //电量
    private LinearLayout mBatteryTime;
    //电量图片
    private ImageView mBattery;
    //当前时间
    private TextView mTime;

    /** 底部控制区 **/
    private LinearLayout mBottom;
    //播放或暂停按钮
    private ImageView mRestartPause;
    //左下角-当前播放位置
    private TextView mPosition;
    //左下角-播放文件总时间
    private TextView mDuration;
    //进度条
    private SeekBar mSeek;
    //清晰度
    private TextView mClarity;
    //全屏
    private ImageView mFullScreen;

    //右下角视频总长
    private TextView mLength;

    /** 加载动画 **/
    private LinearLayout mLoading;
    //正在缓冲...
    private TextView mLoadText;

    /**锁定**/
    private ImageView mPlayerLockScreen;

    /** 改变播放位置 **/
    private LinearLayout mChangePositon;
    //播放位置 00:00
    private TextView mChangePositionCurrent;
    //进度位置
    private ProgressBar mChangePositionProgress;

    /** 改变亮度 **/
    private LinearLayout mChangeBrightness;
    //亮度位置
    private ProgressBar mChangeBrightnessProgress;

    /** 改变声音 **/
    private LinearLayout mChangeVolume;
    //音量位置
    private ProgressBar mChangeVolumeProgress;

    /** 播放错误 **/
    private LinearLayout mError;
    //重试
    private TextView mRetry;

    /** 播放完成 **/
    private LinearLayout mCompleted;
    //重播
    private TextView mReplay;
    //分享
    private TextView mShare;
    //顶部和底部控件是否显示
    private boolean topBottomVisible;

    private CountDownTimer mDismissTopBottomCountDownTimer;

    private int defaultClarityIndex;
    private List<Clarity> clarities;
    private ChangeClarityDialog mClarityDialog;

    // 是否已经注册了电池广播
    private boolean hasRegisterBatteryReceiver;




    public TxVideoPlayerController(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.video_palyer_controller, this, true);

        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);

        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mBatteryTime = (LinearLayout) findViewById(R.id.battery_time);
        mBattery = (ImageView) findViewById(R.id.battery);
        mTime = (TextView) findViewById(R.id.time);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);
        mClarity = (TextView) findViewById(R.id.clarity);
        mLength = (TextView) findViewById(R.id.length);

        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);

        mPlayerLockScreen = (ImageView) findViewById(R.id.player_lock_screen);

        mChangePositon = (LinearLayout) findViewById(R.id.change_position);
        mChangePositionCurrent = (TextView) findViewById(R.id.change_position_current);
        mChangePositionProgress = (ProgressBar) findViewById(R.id.change_position_progress);

        mChangeBrightness = (LinearLayout) findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = (ProgressBar) findViewById(R.id.change_brightness_progress);

        mChangeVolume = (LinearLayout) findViewById(R.id.change_volume);
        mChangeVolumeProgress = (ProgressBar) findViewById(R.id.change_volume_progress);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);
        mShare = (TextView) findViewById(R.id.share);

        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mPlayerLockScreen.setOnClickListener(this);
        mClarity.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {//中间播放
            if (mNiceVideoPlayer.isIdle()) {
                mNiceVideoPlayer.start();
            }
        } else if (v == mRestartPause) {//左下角暂停和继续播放
            if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
                mNiceVideoPlayer.pause();
            } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
                mNiceVideoPlayer.restart();
            }
        } else if (v == mRetry) {//重试
            mNiceVideoPlayer.restart();
        } else if (v == mReplay) {//重新播放
            mRetry.performClick();
        } else if (v == mShare) {
            Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();
        } else if (v == this) {//点击屏幕
            if (mNiceVideoPlayer.isPlaying()
                    || mNiceVideoPlayer.isPaused()
                    || mNiceVideoPlayer.isBufferingPlaying()
                    || mNiceVideoPlayer.isBufferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }
        }else if(v==mPlayerLockScreen){//锁屏
            setLock(!isLock);
        }else if (v == mFullScreen) {//全屏
            if (mNiceVideoPlayer.isNormal() || mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.enterFullScreen();
            } else if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            }
        }else if (v == mBack) {
            if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            } else if (mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.exitTinyWindow();
            }
        }else if (v == mClarity) {
            setTopBottomVisible(false); // 隐藏top、bottom
            mClarityDialog.show();     // 显示清晰度对话框
        }
    }

    /**
     * 设置锁屏控件的显示和隐藏
     *
     * @param lock true锁屏，false没锁屏.
     */
    private void setLock(boolean lock) {
        mTop.setVisibility(lock ? View.GONE : View.VISIBLE);
        mBottom.setVisibility(lock ? View.GONE : View.VISIBLE);
        isLock = lock;
        if (lock) {
            if (!mNiceVideoPlayer.isPaused() && !mNiceVideoPlayer.isBufferingPaused()) {
                //只要不是暂停或者缓冲状态,top和bottom在8秒后自动消失
                startDismissTopBottomTimer();
            }
            mPlayerLockScreen.setImageResource(R.drawable.video_locked);
        } else {
            cancelDismissTopBottomTimer();
            mPlayerLockScreen.setImageResource(R.drawable.video_unlock);
        }
    }

    /**
     * 当播放器的播放状态发生变化，在此方法中更新不同的播放状态的UI
     */
    @Override
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case NiceVideoPlayer.STATE_IDLE:
                break;
            /**播放准备中**/
            case NiceVideoPlayer.STATE_PREPARING:
                mImage.setVisibility(View.GONE);//隐藏底图
                mLoading.setVisibility(View.VISIBLE);//显示加载动画
                mLoadText.setText("正在准备...");//显示加载文字
                mError.setVisibility(View.GONE);//隐藏错误
                mCompleted.setVisibility(View.GONE);//隐藏播放完成画面
                mTop.setVisibility(View.GONE);//隐藏顶部栏
                mBottom.setVisibility(View.GONE);//隐藏底部栏
                mCenterStart.setVisibility(View.GONE);//隐藏中间播放按钮
                mLength.setVisibility(View.GONE);//隐藏右下角时长
                break;
            /**播放准备就绪**/
            case NiceVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();//启动更新进度定时器
                break;
            /**正在播放**/
            case NiceVideoPlayer.STATE_PLAYING:
                mLoading.setVisibility(View.GONE);//隐藏加载动画
                mRestartPause.setImageResource(R.drawable.ic_player_pause);//左下角按钮变成暂停键
                startDismissTopBottomTimer();//8秒后自动隐藏头部和底部栏
                break;
            /**暂停播放**/
            case NiceVideoPlayer.STATE_PAUSED:
                mLoading.setVisibility(View.GONE);//隐藏加载动画
                mRestartPause.setImageResource(R.drawable.ic_player_start);//左下角按钮变成播放键
                cancelDismissTopBottomTimer();
                break;
            /**正在缓冲 缓冲区足够播放**/
            case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                break;
            /**正在缓冲 缓冲区不够播放**/
            case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mLoadText.setText("正在缓冲...");
                cancelDismissTopBottomTimer();
                break;
            /**播放错误**/
            case NiceVideoPlayer.STATE_ERROR:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                break;
            /**播放完成**/
            case NiceVideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(View.VISIBLE);
                mCompleted.setVisibility(View.VISIBLE);
                break;
        }
    }
    /**
     * 播放器的播放模式发生变化，在此方法中更新不同模式下的控制器界面。
     */
    @Override
    protected void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case NiceVideoPlayer.MODE_NORMAL:
                mBack.setVisibility(View.GONE);//隐藏返回按钮
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);//显示放大全屏按钮
//                mFullScreen.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                mBatteryTime.setVisibility(View.GONE);
                mPlayerLockScreen.setVisibility(View.GONE);
                if (hasRegisterBatteryReceiver) {
                    mContext.unregisterReceiver(mBatterReceiver);
                    hasRegisterBatteryReceiver = false;
                }
                break;
            case NiceVideoPlayer.MODE_FULL_SCREEN:
                mBack.setVisibility(View.VISIBLE);//隐藏返回按钮
                mPlayerLockScreen.setVisibility(View.VISIBLE);
//                mFullScreen.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);//显示缩放按钮
                if (clarities != null && clarities.size() > 1) {
                    mClarity.setVisibility(View.VISIBLE);
                }
                mBatteryTime.setVisibility(View.VISIBLE);
                if (!hasRegisterBatteryReceiver) {
                    mContext.registerReceiver(mBatterReceiver,
                            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    hasRegisterBatteryReceiver = true;
                }
                break;
            case NiceVideoPlayer.MODE_TINY_WINDOW:
                mBack.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                mPlayerLockScreen.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 设置清晰度
     *
     * @param clarities 清晰度及链接
     */
    public void setClarity(List<Clarity> clarities, int defaultClarityIndex) {
        if (clarities != null && clarities.size() > 1) {
            this.clarities = clarities;
            this.defaultClarityIndex = defaultClarityIndex;

            List<String> clarityGrades = new ArrayList<>();
            for (Clarity clarity : clarities) {
                clarityGrades.add(clarity.grade + " " + clarity.p);
            }
            mClarity.setText(clarities.get(defaultClarityIndex).grade);
            // 初始化切换清晰度对话框
            mClarityDialog = new ChangeClarityDialog(mContext);
            mClarityDialog.setClarityGrade(clarityGrades, defaultClarityIndex);
            mClarityDialog.setOnClarityCheckedListener(this);
            // 给播放器配置视频链接地址
            if (mNiceVideoPlayer != null) {
                mNiceVideoPlayer.setUp(clarities.get(defaultClarityIndex).videoUrl, null);
            }
        }
    }

    /**
     * 电池状态即电量变化广播接收器
     */
    private BroadcastReceiver mBatterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                // 充电中
                mBattery.setImageResource(R.drawable.battery_charging);
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                // 充电完成
                mBattery.setImageResource(R.drawable.battery_full);
            } else {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int percentage = (int) (((float) level / scale) * 100);
                if (percentage <= 10) {
                    mBattery.setImageResource(R.drawable.battery_10);
                } else if (percentage <= 20) {
                    mBattery.setImageResource(R.drawable.battery_20);
                } else if (percentage <= 50) {
                    mBattery.setImageResource(R.drawable.battery_50);
                } else if (percentage <= 80) {
                    mBattery.setImageResource(R.drawable.battery_80);
                } else if (percentage <= 100) {
                    mBattery.setImageResource(R.drawable.battery_100);
                }
            }
        }
    };

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    @Override
    public void setLenght(long length) {
        mLength.setText(NiceUtil.formatTime(length));
    }

    @Override
    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        super.setNiceVideoPlayer(niceVideoPlayer);
        // 给播放器配置视频链接地址
        if (clarities != null && clarities.size() > 1) {
            mNiceVideoPlayer.setUp(clarities.get(defaultClarityIndex).videoUrl, null);
        }
    }

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

        mCenterStart.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.VISIBLE);

        mBottom.setVisibility(View.GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);

        mLength.setVisibility(View.VISIBLE);

        mTop.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.GONE);

        mLoading.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mCompleted.setVisibility(View.GONE);
    }

    @Override
    protected void updateProgress() {
        long position = mNiceVideoPlayer.getCurrentPosition();
        long duration = mNiceVideoPlayer.getDuration();
        int bufferPercentage = mNiceVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(NiceUtil.formatTime(position));
        mDuration.setText(NiceUtil.formatTime(duration));
        // 更新时间
        mTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        mChangePositon.setVisibility(View.VISIBLE);
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
        mChangePositionProgress.setProgress(newPositionProgress);
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(NiceUtil.formatTime(newPosition));
    }

    @Override
    protected void hideChangePosition() {
        mChangePositon.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeVolume(int newVolumeProgress) {
        mChangeVolume.setVisibility(View.VISIBLE);
        mChangeVolumeProgress.setProgress(newVolumeProgress);
    }

    @Override
    protected void hideChangeVolume() {
        mChangeVolume.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {
        mChangeBrightness.setVisibility(View.VISIBLE);
        mChangeBrightnessProgress.setProgress(newBrightnessProgress);
    }

    @Override
    protected void hideChangeBrightness() {
        mChangeBrightness.setVisibility(View.GONE);
    }

    /**
     * 设置top、bottom的显示和隐藏
     *
     * @param visible true显示，false隐藏.
     */
    private void setTopBottomVisible(boolean visible) {
        if(!isLock){
            mTop.setVisibility(visible ? View.VISIBLE : View.GONE);
            mBottom.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        if(mNiceVideoPlayer.isFullScreen())
            mPlayerLockScreen.setVisibility(visible ? View.VISIBLE : View.GONE);

        topBottomVisible = visible;
        if (visible) {
            if (!mNiceVideoPlayer.isPaused() && !mNiceVideoPlayer.isBufferingPaused()) {
                //只要不是暂停或者缓冲状态,top和bottom在8秒后自动消失
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    /**
     * 开启top、bottom自动消失的timer
     * 8秒钟后自动消失
     */
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    /**
     * 取消top、bottom自动消失的timer
     */
    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mNiceVideoPlayer.isBufferingPaused() || mNiceVideoPlayer.isPaused()) {
            mNiceVideoPlayer.restart();
        }
        long position = (long) (mNiceVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
        mNiceVideoPlayer.seekTo(position);
        startDismissTopBottomTimer();
    }

    @Override
    public void onClarityChanged(int clarityIndex) {
        // 根据切换后的清晰度索引值，设置对应的视频链接地址，并从当前播放位置接着播放
        Clarity clarity = clarities.get(clarityIndex);
        mClarity.setText(clarity.grade);
        long currentPosition = mNiceVideoPlayer.getCurrentPosition();
        mNiceVideoPlayer.releasePlayer();
        mNiceVideoPlayer.setUp(clarity.videoUrl, null);
        mNiceVideoPlayer.start(currentPosition);
    }

    @Override
    public void onClarityNotChanged() {
        // 清晰度没有变化，对话框消失后，需要重新显示出top、bottom
        setTopBottomVisible(true);
    }
}
