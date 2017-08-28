package com.shims.nicevideoplayer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shims on 2017/8/24.
 */

public abstract class NiceVideoPlayerController  extends FrameLayout implements View.OnTouchListener {
    private Context mContext;
    protected INiceVideoPlayer mNiceVideoPlayer;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;


    public NiceVideoPlayerController(Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
    }

    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        mNiceVideoPlayer = niceVideoPlayer;
    }

    /**
     * 设置播放的视频的标题
     *
     * @param title 视频标题
     */
    public abstract void setTitle(String title);

    /**
     * 视频底图
     *
     * @param resId 视频底图资源
     */
    public abstract void setImage(@DrawableRes int resId);

    /**
     * 视频底图ImageView控件，提供给外部用图片加载工具来加载网络图片
     *
     * @return 底图ImageView
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长.
     */
    public abstract void setLenght(long length);

    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    protected abstract void reset();

    /**
     * 当播放器的播放状态发生变化，在此方法中国你更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     *                  <ul>
     *                  <li>{@link NiceVideoPlayer#STATE_IDLE}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PREPARING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PREPARED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PLAYING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PAUSED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_BUFFERING_PLAYING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_BUFFERING_PAUSED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_ERROR}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_COMPLETED}</li>
     *                  </ul>
     */
    protected abstract void onPlayStateChanged(int playState);
    /**
     * 当播放器的播放模式发生变化，在此方法中更新不同模式下的控制器界面。
     *
     * @param playMode 播放器的模式：
     *                 <ul>
     *                 <li>{@link NiceVideoPlayer#MODE_NORMAL}</li>
     *                 <li>{@link NiceVideoPlayer#MODE_FULL_SCREEN}</li>
     *                 <li>{@link NiceVideoPlayer#MODE_TINY_WINDOW}</li>
     *                 </ul>
     */
    protected abstract void onPlayModeChanged(int playMode);

    /**
     * 开启更新进度的计时器。
     */
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    NiceVideoPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 300);
    }

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
     */
    protected abstract void updateProgress();

    /**当前x轴滑动变量**/
    private float mDownX;
    /**当前y轴滑动变量**/
    private float mDownY;
    /****/
    private boolean mNeedChangePosition;
    /****/
    private boolean mNeedChangeVolume;
    /****/
    private boolean mNeedChangeBrightness;
    /****/
    private static final int THRESHOLD = 80;
    private long mGestureDownPosition;
    /****/
    private float mGestureDownBrightness;
    /****/
    private int mGestureDownVolume;
    /****/
    private long mNewPosition;


    private int startX = 0;
    private int startY = 0;
    private int mTop;
    private int mLeft;
    ViewGroup mViewGroup;
    /**
     * 动态设置控件的marginTop 和 marginLeft的值
     *
     * @param dX x轴的偏移量
     * @param dY y轴的偏移量
     */
    private void setTinyWindowMargin(int dX, int dY) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mViewGroup.getLayoutParams();
        int top = mTop + dY;
        int left = mLeft + dX;

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = ((Activity) getContext()).getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(dm);

        int l = NiceUtil.getScreenWidth(mContext) - (int) (NiceUtil.getScreenWidth(mContext) * 0.6f);
        int t = NiceUtil.getScreenHeight(mContext) - (int) (NiceUtil.getScreenWidth(mContext) * 0.6f * 9f / 16f);
        //设置left和top的边界值
        if (left < 0) {
            left = 0;
        } else if (left > l) {
            left = l;
        }
        if (top < getStatusBarHeight()) {
            top = getStatusBarHeight();
        } else if (top > t) {
            top = t;
        }

        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        mViewGroup.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //浮动小窗口拖动事件
        if(mNiceVideoPlayer.isTinyWindow()){
            mViewGroup =(ViewGroup)getParent();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //初始化开始位置
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    mTop = mViewGroup.getTop();
                    mLeft = mViewGroup.getLeft();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //手势移动的dX和dY为控件的marginLeft和marginTop
                    int moveX = (int) event.getRawX();
                    int moveY = (int) event.getRawY();
                    int dX = moveX - startX;
                    int dY = moveY - startY;
                    setTinyWindowMargin(dX, dY);
                    break;
                case MotionEvent.ACTION_UP:
                    //初始值重置
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    mTop = mViewGroup.getTop();
                    mLeft = mViewGroup.getLeft();
                    break;
            }

            return false;
        }

        // 只有全屏的时候才能拖动位置、亮度、声音
        if (!mNiceVideoPlayer.isFullScreen()) {
            return false;
        }
        // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
        if (mNiceVideoPlayer.isIdle()
                || mNiceVideoPlayer.isError()
                || mNiceVideoPlayer.isPreparing()
                || mNiceVideoPlayer.isPrepared()
                || mNiceVideoPlayer.isCompleted()) {
            hideChangePosition();
            hideChangeBrightness();
            hideChangeVolume();
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);//x轴偏移量
                float absDeltaY = Math.abs(deltaY);//y轴偏移量
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness) {
                    // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
                    if (absDeltaX >= THRESHOLD) {
                        cancelUpdateProgressTimer();//取消更新播放进度条
                        mNeedChangePosition = true;
                        mGestureDownPosition = mNiceVideoPlayer.getCurrentPosition();
                    } else if (absDeltaY >= THRESHOLD) {
                        if (mDownX < getWidth() * 0.5f) {
                            // 左侧改变亮度
                            mNeedChangeBrightness = true;
                            mGestureDownBrightness = NiceUtil.scanForActivity(mContext)
                                    .getWindow().getAttributes().screenBrightness;
                        } else {
                            // 右侧改变声音
                            mNeedChangeVolume = true;
                            mGestureDownVolume = mNiceVideoPlayer.getVolume();
                        }
                    }
                }
                if (mNeedChangePosition) {
                    long duration = mNiceVideoPlayer.getDuration();
                    long toPosition = (long) (mGestureDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    int newPositionProgress = (int) (100f * mNewPosition / duration);
                    showChangePosition(duration, newPositionProgress);
                }
                if (mNeedChangeBrightness) {
                    deltaY = -deltaY;
                    float deltaBrightness = deltaY * 3 / getHeight();
                    float newBrightness = mGestureDownBrightness + deltaBrightness;
                    newBrightness = Math.max(0, Math.min(newBrightness, 1));
                    float newBrightnessPercentage = newBrightness;
                    WindowManager.LayoutParams params = NiceUtil.scanForActivity(mContext)
                            .getWindow().getAttributes();
                    params.screenBrightness = newBrightnessPercentage;
                    NiceUtil.scanForActivity(mContext).getWindow().setAttributes(params);
                    int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                    showChangeBrightness(newBrightnessProgress);
                }
                if (mNeedChangeVolume) {
                    deltaY = -deltaY;
                    int maxVolume = mNiceVideoPlayer.getMaxVolume();
                    int deltaVolume = (int) (maxVolume * deltaY * 3 / getHeight());
                    int newVolume = mGestureDownVolume + deltaVolume;
                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                    mNiceVideoPlayer.setVolume(newVolume);
                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
                    showChangeVolume(newVolumeProgress);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mNeedChangePosition) {
                    mNiceVideoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
                if (mNeedChangeBrightness) {
                    hideChangeBrightness();
                    return true;
                }
                if (mNeedChangeVolume) {
                    hideChangeVolume();
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration            视频总时长ms
     * @param newPositionProgress 新的位置进度，取值0到100。
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangePosition();

    /**
     * 手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newVolumeProgress 新的音量进度，取值1到100。
     */
    protected abstract void showChangeVolume(int newVolumeProgress);

    /**
     * 手势在左侧上下滑动改变音量后，手势up或者cancel时，隐藏控制器中间的音量变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeVolume();

    /**
     * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newBrightnessProgress 新的亮度进度，取值1到100。
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 手势在左侧上下滑动改变亮度后，手势up或者cancel时，隐藏控制器中间的亮度变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeBrightness();
}
