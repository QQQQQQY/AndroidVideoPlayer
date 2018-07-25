package com.example.com.androidvideoplayer;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.com.videoplayerlib.PLVideoViewActivity;

public class MainActivity extends PLVideoViewActivity {
    private Toolbar toolbar;
    // TODO: 2018/7/25 0025 设置 video url 
    private String urlVideo = "";
    // TODO: 2018/7/25 0025 设置 cover image url. 
    private String urlCoverImg = "";
    // TODO: 2018/7/25 0025 设置 video title.
    private String titleVideo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {//View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 隐藏虚拟按键(导航栏)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.argb(66, 0, 0, 0));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置屏幕为横屏

        toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.view_video_tool_bar, null);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title_video);
        tvTitle.setText(titleVideo);

        ImageView back = toolbar.findViewById(R.id.iv_back_video_player);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        super.onCreate(savedInstanceState);
    }

    @Override
    public String setVideoPath() {
        return urlVideo;
    }

    @Override
    public int[] setVideoControllerRes() {
        return new int[]{
                R.layout.view_video_controller,
                0,
                0,
                R.id.prev,
                R.id.ffwd,
                R.id.next,
                R.id.rew,
                R.id.pause,
                R.id.mediacontroller_progress,
                R.id.time,
                R.id.time_current
        };
    }

    @Override
    public Toolbar setToolBar() {
        return toolbar;
    }

    @Override
    public String setCoverImg() {
        return urlCoverImg;
    }

    @Override
    public View setLoadingView() {
        FrameLayout frameLayout = new FrameLayout(this);
        ProgressBar progressBar = new ProgressBar(this);
        frameLayout.addView(progressBar);
        return frameLayout;
    }
}
