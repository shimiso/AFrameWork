package com.eshangke.framework.ui.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eshangke.framework.R;
import com.eshangke.framework.presenter.Player;

public class MediaPlayerActivity extends BaseActivity implements View.OnClickListener{
    //音量进度条
    private SeekBar volumeSeekbar;
    //音量按钮
    private ImageView volumeBtn;
    //播放器
    private Player player;
    //视频区域
    private View surfaceLayout;
    //视频区域
    private SurfaceView surfaceView;
    //播放进度条
    private SeekBar skbProgress;
    //播放时间和总时间
    private TextView playedTime, totalTime;
    //播放按钮
    private ImageView playBtn;
    //标题
    private TextView title;
    //返回
    private ImageView back;
    //加在动画
    private View loading;
    //整个页面
    private View frame;

    private int currentPosition;
    // String videoUrl = "http://vd1.bdstatic.com/mda-hdert2fvnatqmj7g/mda-hdert2fvnatqmj7g.mp4";
    private RelativeLayout controlLayout;
    private AudioManager audioManager;
    protected static final int HIDEN_CONTROL_VIEW = 1000;
    private String videoUrl;
    final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HIDEN_CONTROL_VIEW) {
                controlLayout.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player);
        initView();
        initVolume();
        getVideoUrl();
        initData();

    }

    /**
     * 获取视频地址
     * @return  空字符串表示无效路径
     */
    private String getVideoUrl(){
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("videoUrl");
//        videoPath = Environment.getExternalStorageDirectory()+"/videotemp/test.mp4";
        videoPath = "http://pic.huaxuntouzi.com.cn/20170525  至尊解盘  郭振.mp4";
        if(!TextUtils.isEmpty(videoPath)){
            videoUrl = videoPath;
            return videoUrl;
        }
        return "";

    }

    /**
     * 初始化数据
     */
    public void initData() {
//        if (!androidUtil.hasInternetConnected()) {
//            return;
//        }

        player = new Player(this,surfaceView, skbProgress,playedTime,totalTime);
        Player.OnLoadListener onLoadListener = new Player.OnLoadListener() {
            @Override
            public void showloading() {
                showProgressBar();
            }

            @Override
            public void closeloading() {
                hideProgressBar();
            }
        };
        player.setOnLoadListener(onLoadListener);
        player.setOnCompleteListener(() -> playBtn.setImageResource(R.drawable.player_play_btn));
        player.setVideoPath(videoUrl);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:

                if (player.isPlaying()) {//暂停
                    player.pause();
                    playBtn.setImageResource(R.drawable.player_play_btn);
                } else {
                    player.keepPlay();//继续播放
                    playBtn.setImageResource(R.drawable.player_pause_btn);
                }
                break;
            case R.id.back://返回
                finish();
                break;
        }
    }

    /**
     * 初始化View
     */
    public void initView() {
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);
        surfaceLayout = findViewById(R.id.surface_layout);
        frame = findViewById(R.id.frame);
        playedTime = (TextView) findViewById(R.id.played_time);
        totalTime = (TextView) findViewById(R.id.total_time);
        skbProgress = (SeekBar) findViewById(R.id.seekbar);
        surfaceView = (SurfaceView) findViewById(R.id.player_sv);
        playBtn = (ImageView) this.findViewById(R.id.play_btn);
        loading = findViewById(R.id.loading_pb);
        controlLayout = (RelativeLayout)findViewById(R.id.control_layout);
        controlLayout.setVisibility(View.GONE);
        playBtn.setOnClickListener(this);
        back.setOnClickListener(this);

        //播放进度条拖拽事件
        skbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
                this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
                // 同步更新当前播放时间
                String currentTime = player.formatPlayTime(this.progress);
                playedTime.setText(currentTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showProgressBar();
                player.seekTo(this.progress);
                // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
//                player.mediaPlayer.seekTo(progress);
            }
        });
    }

    /**
     * 初始化音量
     */
    private void initVolume() {
        volumeSeekbar = (SeekBar) findViewById(R.id.volume_seekbar);
        volumeBtn = (ImageView) findViewById(R.id.volume_btn);
        volumeBtn.setOnClickListener(this);
        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        int volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        volumeSeekbar.setMax(volumeMax);//设置最大音量
        volumeSeekbar.setProgress(volumeMax / 2);//设置当前音量
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMax / 2, 0);//设置当前播放音量
        if (volumeMax == 0) {
            volumeBtn.setImageResource(R.drawable.volume_btn_mute);
        } else {
            volumeBtn.setImageResource(R.drawable.volume_btn);
        }
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                int currentVolume = progress;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                if (currentVolume <= 0) {
                    volumeBtn.setImageResource(R.drawable.volume_btn_mute);
                } else {
                    volumeBtn.setImageResource(R.drawable.volume_btn);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("volumeSeekbar","onStartTrackingTouch ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("volumeSeekbar","onStopTrackingTouch ");
            }

        });
    }

    private void hideProgressBar() {
        if (loading != null) {
            loading.setVisibility(View.INVISIBLE);
        }
    }

    private void showProgressBar() {
        if (loading != null) {
            loading.setVisibility(View.VISIBLE);
            loading.bringToFront();
            frame.requestLayout();
            frame.invalidate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MediaPlayerActivity","onStop()");
//        player.pause();
//        playBtn.setImageResource(R.drawable.player_play_btn);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        currentPosition = player.getCurrentPosition();
        player.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        player.seekTo(currentPosition);
        player.start();
        if(player.getVideoHeight()!=0){
            playBtn.setImageResource(R.drawable.player_pause_btn);
        }

        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        player.keepPlay();
        Log.d("MediaPlayerActivity","onRestart()");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(player != null){

            player.stop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                if(controlLayout.getVisibility() == View.VISIBLE){
                    mhandler.removeMessages(HIDEN_CONTROL_VIEW);
                    controlLayout.setVisibility(View.GONE);
                }else{
                    controlLayout.setVisibility(View.VISIBLE);
                    mhandler.removeMessages(HIDEN_CONTROL_VIEW);
                    mhandler.sendEmptyMessageDelayed(HIDEN_CONTROL_VIEW, 10000);
                }


                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }





}
