package com.example.com.videoplayerlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.com.videoplayerlib.utils.Utils;
import com.example.com.videoplayerlib.widget.MediaController;
import com.pili.pldroid.player.PLOnAudioFrameListener;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnVideoFrameListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

/**
 * This is a demo activity of PLVideoView
 */
public abstract class PLVideoViewActivity extends AppCompatActivity {

    public static final String TAG = PLVideoViewActivity.class.getSimpleName();

    private PLVideoView mVideoView;
    private int mDisplayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT;
    //    private TextView mStatInfoTextView;
    private MediaController mMediaController;

//    public abstract PLVideoView setVideoView();

    public abstract String setVideoPath();

    /**
     * @return [ MEDIA_CONTROLLER_ID,IC_MEDIA_PAUSE_ID,IC_MEDIA_PLAY_ID, PRV_BUTTON_ID,
     * FFWD_BUTTON_ID, NEXT_BUTTON_ID, REW_BUTTON_ID,PAUSE_BUTTON_ID,MEDIACONTROLLER_PROGRESS_ID,
     * END_TIME_ID, CURRENT_TIME_ID ]
     */
    public abstract int[] setVideoControllerRes();

    public abstract Toolbar setToolBar();

    public abstract View setLoadingView();

    public String setCoverImg() {
        return null;
    }

    public int setCoverImgRes() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_video_view);

        String videoPath = setVideoPath();

        mVideoView = findViewById(R.id.VideoView);
//        mVideoView = setVideoView();

//        View loadingView = findViewById(R.id.LoadingView);
        View loadingView = setLoadingView();
        FrameLayout flLoading = findViewById(R.id.fl_container_loading);
        flLoading.addView(loadingView);
        mVideoView.setBufferingIndicator(loadingView);

        ImageView mCoverView = findViewById(R.id.CoverView);
        mVideoView.setCoverView(mCoverView);
        if (setCoverImgRes() > 0) {
            mCoverView.setImageResource(setCoverImgRes());
        } else if (!TextUtils.isEmpty(setCoverImg())) {
            Picasso.get().load(setCoverImg()).into(mCoverView);
        }

//        mStatInfoTextView = findViewById(R.id.StatInfoTextView);

//        // 1 -> hw codec enable, 0 -> disable [recommended]
//        int codec = getIntent().getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_SW_DECODE);
//        AVOptions options = new AVOptions();
//        // the unit of timeout is ms
//        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
//        // 1 -> hw codec enable, 0 -> disable [recommended]
//        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
//        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);
//        boolean disableLog = getIntent().getBooleanExtra("disable-log", false);
////        options.setString(AVOptions.KEY_DNS_SERVER, "127.0.0.1");
//        options.setInteger(AVOptions.KEY_LOG_LEVEL, disableLog ? 5 : 0);
//        boolean cache = getIntent().getBooleanExtra("cache", false);
//        boolean vcallback = getIntent().getBooleanExtra("video-data-callback", false);
//        if (vcallback) {
//            options.setInteger(AVOptions.KEY_VIDEO_DATA_CALLBACK, 1);
//        }
//        boolean acallback = getIntent().getBooleanExtra("audio-data-callback", false);
//        if (acallback) {
//            options.setInteger(AVOptions.KEY_AUDIO_DATA_CALLBACK, 1);
//        }
//        mVideoView.setAVOptions(options);

        // Set some listeners
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnVideoFrameListener(mOnVideoFrameListener);
        mVideoView.setOnAudioFrameListener(mOnAudioFrameListener);

        mVideoView.setVideoPath(videoPath);
        mVideoView.setLooping(getIntent().getBooleanExtra("loop", false));

        Toolbar toolbar = setToolBar();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbar.setLayoutParams(lp);
        FrameLayout flContainer = findViewById(R.id.fl_container);
        flContainer.addView(toolbar);
        setSupportActionBar(toolbar);

        // You can also use a custom `MediaController` widget
        mMediaController = new MediaController(this, true, false) {
            @Override
            public int[] setControllerRes() {
                return setVideoControllerRes();
            }
        };
        mMediaController.setOnClickSpeedAdjustListener(mOnClickSpeedAdjustListener);
        mMediaController.setSupportActionBar(getSupportActionBar());
        mVideoView.setMediaController(mMediaController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    public void onClickSwitchScreen(View v) {
        mDisplayAspectRatio = (mDisplayAspectRatio + 1) % 5;
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        switch (mVideoView.getDisplayAspectRatio()) {
            case PLVideoView.ASPECT_RATIO_ORIGIN:
                Utils.showToastTips(this, "Origin mode");
                break;
            case PLVideoView.ASPECT_RATIO_FIT_PARENT:
                Utils.showToastTips(this, "Fit parent !");
                break;
            case PLVideoView.ASPECT_RATIO_PAVED_PARENT:
                Utils.showToastTips(this, "Paved parent !");
                break;
            case PLVideoView.ASPECT_RATIO_16_9:
                Utils.showToastTips(this, "16 : 9 !");
                break;
            case PLVideoView.ASPECT_RATIO_4_3:
                Utils.showToastTips(this, "4 : 3 !");
                break;
            default:
                break;
        }
    }

    private PLOnInfoListener mOnInfoListener = new PLOnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {
            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
            switch (what) {
                case PLOnInfoListener.MEDIA_INFO_BUFFERING_START:
                    break;
                case PLOnInfoListener.MEDIA_INFO_BUFFERING_END:
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START:
//                    Utils.showToastTips(PLVideoViewActivity.this, "first video render time: " + extra + "ms");
                    break;
                case PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START:
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_FRAME_RENDERING:
                    Log.i(TAG, "video frame rendering, ts = " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_AUDIO_FRAME_RENDERING:
                    Log.i(TAG, "audio frame rendering, ts = " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_GOP_TIME:
                    Log.i(TAG, "Gop Time: " + extra);
                    break;
                case PLOnInfoListener.MEDIA_INFO_SWITCHING_SW_DECODE:
                    Log.i(TAG, "Hardware decoding failure, switching software decoding!");
                    break;
                case PLOnInfoListener.MEDIA_INFO_METADATA:
                    Log.i(TAG, mVideoView.getMetadata().toString());
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_BITRATE:
                case PLOnInfoListener.MEDIA_INFO_VIDEO_FPS:
//                    updateStatInfo();
                    break;
                case PLOnInfoListener.MEDIA_INFO_CONNECTED:
                    Log.i(TAG, "Connected !");
                    break;
                case PLOnInfoListener.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    Log.i(TAG, "Rotation changed: " + extra);
                default:
                    break;
            }
        }
    };

    private PLOnErrorListener mOnErrorListener = new PLOnErrorListener() {
        @Override
        public boolean onError(int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLOnErrorListener.ERROR_CODE_IO_ERROR:
                    /**
                     * SDK will do reconnecting automatically
                     */
                    Log.e(TAG, "IO Error!");
                    return false;
                case PLOnErrorListener.ERROR_CODE_OPEN_FAILED:
                    Utils.showToastTips(PLVideoViewActivity.this, "failed to open player !");
                    break;
                case PLOnErrorListener.ERROR_CODE_SEEK_FAILED:
//                    Utils.showToastTips(PLVideoViewActivity.this, "failed to seek !");
                    break;
                default:
                    Utils.showToastTips(PLVideoViewActivity.this, "unknown error !");
                    break;
            }
            finish();
            return true;
        }
    };

    private PLOnCompletionListener mOnCompletionListener = new PLOnCompletionListener() {
        @Override
        public void onCompletion() {
            Log.i(TAG, "Play Completed !");
            mMediaController.seekToEnd();
//            Utils.showToastTips(PLVideoViewActivity.this, "Play Completed !");
            //finish();
        }
    };

    private PLOnBufferingUpdateListener mOnBufferingUpdateListener = new PLOnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(int precent) {
            Log.i(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLOnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLOnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            Log.i(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height);
        }
    };

    private PLOnVideoFrameListener mOnVideoFrameListener = new PLOnVideoFrameListener() {
        @Override
        public void onVideoFrameAvailable(byte[] data, int size, int width, int height, int format, long ts) {
            Log.i(TAG, "onVideoFrameAvailable: " + size + ", " + width + " x " + height + ", " + format + ", " + ts);
            if (format == PLOnVideoFrameListener.VIDEO_FORMAT_SEI && bytesToHex(Arrays.copyOfRange(data, 19, 23)).equals("ts64")) {
                // If the RTMP stream is from Qiniu
                // Add &addtssei=true to the end of URL to enable SEI timestamp.
                // Format of the byte array:
                // 0:       SEI TYPE                    This is part of h.264 standard.
                // 1:       unregistered user data      This is part of h.264 standard.
                // 2:       payload length              This is part of h.264 standard.
                // 3-18:    uuid                        This is part of h.264 standard.
                // 19-22:   ts64                        Magic string to mark this stream is from Qiniu
                // 23-30:   timestamp                   The timestamp
                // 31:      0x80                        Magic hex in ffmpeg
                Log.i(TAG, " timestamp: " + Long.valueOf(bytesToHex(Arrays.copyOfRange(data, 23, 31)), 16));
            }
        }
    };

    private PLOnAudioFrameListener mOnAudioFrameListener = new PLOnAudioFrameListener() {
        @Override
        public void onAudioFrameAvailable(byte[] data, int size, int samplerate, int channels, int datawidth, long ts) {
            Log.i(TAG, "onAudioFrameAvailable: " + size + ", " + samplerate + ", " + channels + ", " + datawidth + ", " + ts);
        }
    };

    private MediaController.OnClickSpeedAdjustListener mOnClickSpeedAdjustListener = new MediaController.OnClickSpeedAdjustListener() {
        @Override
        public void onClickNormal() {
            // 0x0001/0x0001 = 2
            mVideoView.setPlaySpeed(0X00010001);
        }

        @Override
        public void onClickFaster() {
            // 0x0002/0x0001 = 2
            mVideoView.setPlaySpeed(0X00020001);
        }

        @Override
        public void onClickSlower() {
            // 0x0001/0x0002 = 0.5
            mVideoView.setPlaySpeed(0X00010002);
        }
    };

    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

//    private void updateStatInfo() {
//        long bitrate = mVideoView.getVideoBitrate() / 1024;
//        final String stat = bitrate + "kbps, " + mVideoView.getVideoFps() + "fps";
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mStatInfoTextView.setText(stat);
//            }
//        });
//    }
}
