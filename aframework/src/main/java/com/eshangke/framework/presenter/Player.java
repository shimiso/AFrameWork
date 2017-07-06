package com.eshangke.framework.presenter;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Player implements OnBufferingUpdateListener,
        OnCompletionListener, MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback, MediaPlayer.OnInfoListener,MediaPlayer.OnErrorListener,
        MediaPlayer.OnVideoSizeChangedListener,AudioManager.OnAudioFocusChangeListener {
    private int videoWidth;
    private int videoHeight;
    public MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SeekBar skbProgress;
    private TextView playTime;
    private TextView totalTime;
    private int currentPosition=0;
    private Context context;
    private OnLoadListener onLoadListener;
    private OnCompleteListener onCompleteListener;
    private String videoUrl;
    private boolean isComplete;
    private int mDuration;

    private boolean mStartWhenPrepared;
    private boolean mIsPrepared;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mSeekWhenPrepared;



    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    int position = mediaPlayer.getCurrentPosition();
                    String currentTime = formatPlayTime(position);
                    if (mDuration > 0) {
                        long pos = skbProgress.getMax() * position / mDuration;
                        skbProgress.setProgress((int) pos);
                        playTime.setText(currentTime);
                    }
                    handleProgress.sendEmptyMessageDelayed(0,200);
                    break;

                case 1:
                    String durationTime  = (String)msg.obj;
                    totalTime.setText(durationTime);
                    break;
            }

        }


    };

    public Player(Context context,SurfaceView surfaceView, SeekBar skbProgress, TextView playTime, TextView totalTime) {
        this.context = context;
        this.surfaceView = surfaceView;
        this.skbProgress = skbProgress;
        this.playTime = playTime;
        this.totalTime = totalTime;
        initSurfaceView();
    }


    /**
     * 初始化surfaceview
     */
    private void initSurfaceView() {
        videoWidth = 0;
        videoHeight = 0;
        surfaceView.getHolder().addCallback(this);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 设置视频路径
     * @param path
     */
    public void setVideoPath(String path) {
        this.videoUrl = path;
        mStartWhenPrepared = false;
        mSeekWhenPrepared = 0;
//        openVideo();
    }



    @Override
    public void surfaceChanged(SurfaceHolder arg0, int format, int w, int h) {
        Log.e("mediaPlayer", "surface changed");
        mSurfaceWidth = w;
        mSurfaceHeight = h;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            surfaceHolder = holder;
            if(currentPosition == 0){

                initMediaPlayer();

            }else{
                if(mediaPlayer != null){
                    mediaPlayer.setDisplay(surfaceHolder);
                    mediaPlayer.seekTo(currentPosition);
                    mediaPlayer.start();
                    handleProgress.sendEmptyMessage(0);
                }
            }


        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }
        Log.e("mediaPlayer", "surface created");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.e("mediaPlayer", "surface destroyed");
        surfaceHolder = null;

    }

    /**
     * 初始化播放器
     */
    private void initMediaPlayer() {

        if (videoUrl == null || surfaceHolder == null) {
            return ;
        }

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

//                 AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//                 am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        try {
            mediaPlayer = new MediaPlayer();
//                     mediaPlayer = MediaPlayer.create(context, Uri.parse(videoUrl));
            if(mediaPlayer == null){
                Log.e("mediaPlayer", "初始化mediaplayer失败");
                return ;
            }
            mIsPrepared = false;
            mDuration = -1;
            mediaPlayer.setOnPreparedListener(Player.this);
            mediaPlayer.setOnVideoSizeChangedListener(Player.this);
            mediaPlayer.setOnCompletionListener(Player.this);
            mediaPlayer.setOnErrorListener(Player.this);
            mediaPlayer.setOnBufferingUpdateListener(Player.this);
            mediaPlayer.setOnInfoListener(Player.this);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    onLoadListener.closeloading();
                    // 暂停状态下拖拽进度条，需要记录拖拽位置，以便能继续播放
                    currentPosition = mp.getCurrentPosition();
                }

            });
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
//                     mediaPlayer.prepare();
            mediaPrepare();
        }
        catch (IOException ex) {
            Log.w("mediaPlayer", "Unable to open content: " + videoUrl, ex);
            return;
        }
        catch (IllegalArgumentException ex) {
            Log.w("mediaPlayer", "Unable to open content: " + videoUrl, ex);
            return ;
        }

    }


    /**
     * mediaplayer异步准备   准备完毕后会回调 onPrepared()方法，在那开启播放
     */
    private void mediaPrepare(){


        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                onLoadListener.closeloading();
            }
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    mediaPlayer.prepare(); //此方法是耗时操作，阻塞主线程
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("mediaPlayer", "prepare  IllegalStateException" );
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("mediaPlayer", "prepare  IOException" );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onLoadListener.closeloading();
            }
        }.execute();

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        handleProgress.sendEmptyMessage(0);
        videoWidth = mp.getVideoWidth();
        videoHeight = mp.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            mp.seekTo(currentPosition);
            mp.start();
        }
        mDuration = mediaPlayer.getDuration();

        String durationTime = formatPlayTime(mDuration);
        Message msg = handleProgress.obtainMessage();
        msg.what = 1;
        msg.obj = durationTime;
        handleProgress.sendMessage(msg);
        Log.e("mediaPlayer", "onPrepared");


        ///////////////////////////
        mIsPrepared = true;
        if (videoWidth != 0 && videoHeight != 0) {
            surfaceView.getHolder().setFixedSize(videoWidth, videoHeight);
            if (mSurfaceWidth == videoWidth && mSurfaceHeight == videoHeight) {
                if (mSeekWhenPrepared != 0) {
                    mediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                if (mStartWhenPrepared) {
                    mediaPlayer.start();
                    mStartWhenPrepared = false;
                    handleProgress.sendEmptyMessage(0);
                } else if (!isPlaying() &&
                        (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
                }
            }
        } else {
            if (mSeekWhenPrepared != 0) {
                mediaPlayer.seekTo(mSeekWhenPrepared);
                mSeekWhenPrepared = 0;
            }
            if (mStartWhenPrepared) {
                mediaPlayer.start();
                mStartWhenPrepared = false;
                handleProgress.sendEmptyMessage(0);
            }
        }
    }




    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                break;

            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                break;

            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                break;

            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;

            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                onLoadListener.showloading();
                Log.e("mediaPlayer", "loading begin");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                onLoadListener.closeloading();
                Log.e("mediaPlayer", "loading end");
                break;
        }
        return false;
    }




    @Override
    public void onCompletion(MediaPlayer arg0) {
        //
        Log.e("onCompletion", "onCompletion :" + formatPlayTime(mediaPlayer.getDuration()));
        isComplete = true;
        currentPosition = 0;
        // 播放完毕，取消播放时间的更新
        handleProgress.removeMessages(0);
        // 监听视频播放完成，更新控制按钮状态
        if(onCompleteListener != null){
            onCompleteListener.complete();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        skbProgress.setSecondaryProgress(bufferingProgress);
        int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", bufferingProgress + "% buffer");

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int w, int h) {
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();

        if (mVideoWidth != 0 && mVideoHeight != 0) {
            surfaceView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
        }
    }




    /**
     * 将播放进度跳到指定位置
     * @param progress
     */
    public void seekTo(int progress) {
        if (mediaPlayer != null && mIsPrepared) {
            if(surfaceHolder != null){

                mediaPlayer.seekTo(progress);
            }
        } else {
            mSeekWhenPrepared = progress;
        }
    }


    /**
     * 开始播放
     */
    public void start() {
        if (mediaPlayer != null && mIsPrepared) {
            if(surfaceHolder != null){

                mediaPlayer.start();
                mStartWhenPrepared = false;
                handleProgress.sendEmptyMessage(0);
                Log.e("mediaPlayer", "surfaceholder 不为null");
            }
        } else {
            mStartWhenPrepared = true;
        }
    }


    /**
     * 停止播放
     */
    public void stop() {
        handleProgress.removeMessages(0);

        mStartWhenPrepared = false;


        if (mediaPlayer != null && mIsPrepared) {
            if(mediaPlayer.isPlaying()){

                mediaPlayer.stop();

            }
            //Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

        }

    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mediaPlayer != null && mIsPrepared) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                currentPosition=mediaPlayer.getCurrentPosition();
                Log.e("mediaPlayer", "pause  currentPosition = "+ formatPlayTime(currentPosition));
                handleProgress.removeMessages(0);
            }
        }
        mStartWhenPrepared = false;
        //暂停的时候保存当前播放进度
    }


    /**
     * 获取视频时长
     * @return
     */
    public int getDuration() {
        if (mediaPlayer != null && mIsPrepared) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    /**
     * 获取当前播放进度
     * @return
     */
    public int getCurrentPosition() {
        if (mediaPlayer != null && mIsPrepared) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 是否正在播放视频
     * @return
     */
    public boolean isPlaying() {
        if (mediaPlayer != null && mIsPrepared) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 获取视频宽度
     * @return
     */
    public int getVideoWidth(){
        return mVideoWidth;
    }

    /**
     * 获取视频高度
     * @return
     */
    public int getVideoHeight(){
        return mVideoHeight;
    }


    /**
     * 释放播放器资源
     */
    private void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
//            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
        }
    }


    /**
     * 视频是否已经播放完毕
     * @return
     */
    public boolean isComplete() {
        return isComplete;
    }


    /**
     * 接着上次暂停的进度继续播放
     */
    public void keepPlay(){
        onLoadListener.showloading();
        mediaPlayer.seekTo(currentPosition);
        mediaPlayer.start();
        handleProgress.sendEmptyMessage(0);
    }


    /**
     * 设置缓冲监听
     * @param onLoadListener
     */
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        release();
        Log.d("mediaPlayer","what =  "+ what +"extra = "+ extra);
        return true;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // 长时间的失去音频焦点,停止播放，释放资源
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                break;


            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                break;
        }

    }

    /**
     * 缓冲监听接口
     * @author ArthurLin
     *
     */
    public interface OnLoadListener{
        public void  showloading();
        public void  closeloading();
    }

    /**
     * 设置播放完成监听器
     * @param onCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }


    /**
     * 播放完成监听接口
     * @author ArthurLin
     *
     */
    public interface OnCompleteListener{
        public void complete();
    }

    /**
     * 格式化时间为 HH:mm:ss
     * @param time 时间单位是毫秒
     * @return
     */
    public String formatPlayTime(int time){
        String times = "";
        //将毫秒换算成秒
        int totalSeconds = time / 1000;
        int hour = totalSeconds / 3600;
        int minute = totalSeconds % 3600 / 60;
        int second = totalSeconds % 60;
        times = String.format("%02d:%02d:%02d", hour, minute, second);
        return times;

    }

    /**
     * 下载视频到SD卡
     * @param savePath  保存路径
     */
    private void downloadVideo(String savePath) {

        new Thread(){
            @Override
            public void run() {
                super.run();

                File file = new File(Environment.getExternalStorageDirectory()+ "/videotemp");
                if(file.exists()){
//                     file.delete();
//                     file.mkdir();
                }else{
                    file.mkdir();
                }
                InputStream is= null;
                OutputStream os = null;
                try {
                    // 构造URL
                    URL url = new URL("http://mpv.videocc.net/8471b76eff/a/8471b76eff9b882b8fa12ba3f21bd29a_1.mp4");
                    // 打开连接
                    URLConnection con = url.openConnection();
                    //获得文件的长度
                    int contentLength = con.getContentLength();
                    Log.d("Player","长度 :"+contentLength);
                    // 输入流
                    is = con.getInputStream();
                    // 1K的数据缓冲
                    byte[] buffer = new byte[1024];
                    // 读取到的数据长度
                    int len;
                    // 输出的文件流
                    os = new FileOutputStream(file.getAbsolutePath()+"/test.mp4");
                    // 开始读取
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    // 完毕，关闭所有链接
                    try {

                        os.close();
                        is.close();
                    }catch (Exception e){

                    }
                }
            }
        }.start();

    }
}
