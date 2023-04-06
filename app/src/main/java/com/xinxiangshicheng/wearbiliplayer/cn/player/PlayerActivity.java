package com.xinxiangshicheng.wearbiliplayer.cn.player;

import static android.media.AudioManager.STREAM_MUSIC;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.BatteryView;
import com.xinxiangshicheng.wearbiliplayer.cn.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, TextureView.SurfaceTextureListener {
    private OkHttpClient okHttpClient;
    private BaseDanmakuParser mParser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    private IDanmakuView mDanmakuView;
    private DanmakuContext mContext;
    private Timer progresstimer, timer2, sound, judge, timer;
    private String url, danmaku, title, ShowProgress, past_speed;
    private int showheight, showwidth, videoall, videonow, linshitime, rorate;
    private RelativeLayout relativeLayout;
    private MediaPlayer mediaPlayer;
    private TextureView textureView;
    private Surface mSurface;
//    private String basedirectory;//手机储存路径
//    private String videoname;//临时储存的视频路径
//    private String danmakuname;//临时储存的弹幕路径
//    private File delete0, delete1, video;//视频文件
    private float TotalFileSize;//视频文件总文件大小
    private float CompleteFileSize, partCompleteFileSize;//已经下完的文件大小
    private float DownProgress, partdownprogress;//下载进度
    private long Complete, partcompltet;
    private Button ControlButton;
    private SeekBar progressBar;
    private float Screenwidth, Screenheight;
    private FrameLayout downloadprogress;
    private LinearLayout linearLayouttop, linearLayoutbottom, showtime;
    private boolean ischanging, isdanmakushowing, part, one, two, three, four, five = false;
    private TextView timenow, alltime, showsound, Showtitle, partprogress0, partprogress1, partprogress2, partprogress3, partprogress4, local_time, speed;
    private AudioManager audioManager;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SharedPreferences preferences2;
    private ImageView showdanmaku, circle;
    private int totalvideosize, page;
    private ImageButton danmaku_btn;
    private BatteryView batteryView;
    private BatteryManager manager;
    private Date date;
    private SimpleDateFormat dateFormat;
    private boolean is_finish = false;
    private SharedPreferences preferences;
    private Handler handler;
    private Runnable mLongPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("加载", "加载");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ImmersionBar.with(this).transparentBar().init();
//        while (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
//        }

        findview();//找到所有控件
        wearbili();

        manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        batteryView.setPower(manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
        okHttpClient = new OkHttpClient();
        preferences2 = getSharedPreferences("videosetting", MODE_PRIVATE);
        preferences = getSharedPreferences("danmakusetting", MODE_PRIVATE);
//        SharedPreferences.Editor editor = rotate.edit();
//        editor.putBoolean("isnew",true);
//        editor.commit();
        Glide.with(this).load(R.mipmap.load).into(circle);
        date = new Date();
        dateFormat = new SimpleDateFormat("HH:mm");
        local_time.setText(dateFormat.format(date));
//        File basedir = Environment.getExternalStorageDirectory();
//        basedirectory = basedir.toString();
//        Log.e("内部储存", basedirectory);
//        File creat = new File(basedirectory + "/wearplayerdata");//创建视频临时存储文件夹
//        creat.mkdir();
//        File video = new File(basedirectory + "/wearplayerdata/Temporarypartvideo");
//        video.mkdir();
//        videoname = basedirectory + "/wearplayerdata/TemporaryVideo.mp4";
////        Log.e("videoname",videoname);
//        danmakuname = basedirectory + "/wearplayerdata/Danmaku.xml";
//        delete0 = new File(videoname);
//        delete1 = new File(danmakuname);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mContext = DanmakuContext.create();
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, preferences.getInt("maxline", 3));
        HashMap<Integer, Boolean> overlap = new HashMap<Integer, Boolean>();
        overlap.put(BaseDanmaku.TYPE_SCROLL_LR, preferences.getBoolean("allowoverlap", true));
        overlap.put(BaseDanmaku.TYPE_FIX_BOTTOM, preferences.getBoolean("allowoverlap", true));
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 1)
                .setDuplicateMergingEnabled(preferences.getBoolean("mergeduplicate", false))
                .setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f))
                .setScaleTextSize(preferences.getFloat("danmakusize", 1.0f))//缩放值
                .setMaximumLines(maxLinesPair)
                .setDanmakuTransparency(preferences.getFloat("transparency", 0.33f))
                .preventOverlapping(overlap);

        WindowManager windowManager = this.getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        Screenwidth = displayMetrics.widthPixels;//获取屏宽
        Screenheight = displayMetrics.heightPixels;//获取屏高
        handler = new Handler();
        mLongPressed = new Runnable() {
            public void run() {
                if (mediaPlayer != null && mDanmakuView.isPrepared() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Vibrator vv = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                    vv.vibrate(150l);
                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(3.0f));
                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 0.33f);
                    past_speed = speed.getText().toString();
                    speed.setText("x 3.0");
                    if (timer2 != null) timer2.cancel();
                    danmaku_btn.setVisibility(View.GONE);
                    showdanmaku.setVisibility(View.GONE);
                    linearLayouttop.setVisibility(View.GONE);
                    linearLayoutbottom.setVisibility(View.GONE);
                    showtime.setVisibility(View.GONE);
                    speed.setVisibility(View.GONE);
                }
                // 长按处理
            }
        };
        textureView.setSurfaceTextureListener(this);
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && ControlButton.getText().toString() == "| |" && event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.postDelayed(mLongPressed, 500);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if ((linearLayouttop.getVisibility()) == View.GONE) {
                        showdanmaku.setVisibility(View.VISIBLE);
                        linearLayouttop.setVisibility(View.VISIBLE);
                        linearLayoutbottom.setVisibility(View.VISIBLE);
                        showtime.setVisibility(View.VISIBLE);
                        batteryView.setPower(manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
                        danmaku_btn.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            speed.setVisibility(View.VISIBLE);
                        date = new Date();
                        dateFormat = new SimpleDateFormat("HH:mm");
                        local_time.setText(dateFormat.format(date));
                        autohide();
                    } else {
                        if (timer2 != null) timer2.cancel();
                        danmaku_btn.setVisibility(View.GONE);
                        showdanmaku.setVisibility(View.GONE);
                        linearLayouttop.setVisibility(View.GONE);
                        linearLayoutbottom.setVisibility(View.GONE);
                        showtime.setVisibility(View.GONE);
                        speed.setVisibility(View.GONE);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(mLongPressed);
                    if (mediaPlayer != null && mDanmakuView.isPrepared() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (speed.getText().toString() == "x 3.0") {
                            switch (past_speed) {
                                case "x 0.5":
                                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(0.5f));
                                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 2f);
                                    speed.setText("x 0.5");
                                    break;

                                case "x 1.0":
                                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.0f));
                                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f));
                                    speed.setText("x 1.0");
                                    break;

                                case "x1.25":
                                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.25f));
                                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 0.8f);
                                    speed.setText("x1.25");
                                    break;

                                case "x 1.5":
                                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.5f));
                                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 0.66f);
                                    speed.setText("x 1.5");
                                    break;

                                case "x 2.0":
                                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(2.0f));
                                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 0.5f);
                                    speed.setText("x 2.0");
                                    break;

                                default:
                                    break;
                            }
                        }
//                        else {
//                            if (mediaPlayer.isPlaying())
//                        }
//                        mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.0f));
//                        mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f));
//                        speed.setText("x 1.0");
                    }
                }
                return true;
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ischanging = true;
                if (timer2 != null) timer2.cancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    circle.setVisibility(View.VISIBLE);
                    mediaPlayer.seekTo(progressBar.getProgress());
                    circle.setVisibility(View.GONE);
                    ischanging = false;
                    if (mDanmakuView != null) {
                        mDanmakuView.seekTo((long) progressBar.getProgress());
                        if (ControlButton.getText().toString() == "‣") mDanmakuView.pause();
                    }
                }
                autohide();
            }
        });

//        Log.e("url",local_url);
        autohide();
//        playing();
        downdanmu();
//        if (url.indexOf("http") == 0) {
//            Log.e("播放提示","b站在线视频");
//            getvideosize();
//            downdanmu();
//        } else {
//            streamdanmaku(url.replace("video.mp4", "danmaku.xml"));
//            videodelay();
//        }
    }


//    private void videodelay(String video) {
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
////                playing(video);
//            }
//        };
//        timer.schedule(timerTask, 200);
//    }

    private void autohide() {
        if (timer2 != null) timer2.cancel();
        timer2 = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        danmaku_btn.setVisibility(View.GONE);
                        showdanmaku.setVisibility(View.GONE);
                        linearLayouttop.setVisibility(View.GONE);
                        linearLayoutbottom.setVisibility(View.GONE);
                        showtime.setVisibility(View.GONE);
                        speed.setVisibility(View.GONE);
                    }
                });
            }
        };
        timer2.schedule(timerTask, 4000);
    }


    private void wearbili() {
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");//功能暂不清楚
        url = intent.getStringExtra("url");//视频链接
        String url_backup = intent.getStringExtra("url_backup");//功能暂不清楚
        danmaku = intent.getStringExtra("danmaku");//弹幕链接
        title = intent.getStringExtra("title");//视频标题
        String identity_name = intent.getStringExtra("identity_name");//视频来源
        String time = intent.getStringExtra("time");//估计是历史记录的时间
        String headers = intent.getStringExtra("headers");//暂时不知道干嘛的
        Log.e("弹幕", danmaku);
        Log.e("视频", url);
        Log.e("标题", title);
        if (mode != null) Log.e("mode", mode);
        if (url_backup != null) Log.e("urlback", url_backup);
        if (identity_name != null) Log.e("ident", identity_name);
        if (headers != null) Log.e("header", headers);
        Showtitle.setText(title);
    }

    private void findview() {
        circle = findViewById(R.id.circle);
        timenow = findViewById(R.id.timenow);
        alltime = findViewById(R.id.alltime);
        showdanmaku = findViewById(R.id.danmaku);
        ControlButton = findViewById(R.id.control);//找到播放控制按钮
        progressBar = findViewById(R.id.videoprogress);//找到视频进度条
        danmaku_btn = findViewById(R.id.rotate_btn);
        local_time = findViewById(R.id.time);
        speed = findViewById(R.id.video_speed);
        partprogress0 = findViewById(R.id.partprogress0);
        partprogress1 = findViewById(R.id.partprogress1);
        partprogress2 = findViewById(R.id.partprogress2);
        partprogress3 = findViewById(R.id.partprogress3);
        partprogress4 = findViewById(R.id.partprogress4);
        Showtitle = findViewById(R.id.showtitle);//找到标题控件
        showsound = findViewById(R.id.showsound);
        textureView = findViewById(R.id.textureview);
        ControlButton = findViewById(R.id.control);
        downloadprogress = (FrameLayout) findViewById(R.id.downloadprogress);
        relativeLayout = (RelativeLayout) findViewById(R.id.realtive);
        mDanmakuView = (IDanmakuView) findViewById(R.id.sv_danmaku);
        linearLayouttop = (LinearLayout) findViewById(R.id.Topcontrol);
        linearLayoutbottom = (LinearLayout) findViewById(R.id.Bottomcontrol);
        showtime = findViewById(R.id.showtime);
        batteryView = (BatteryView) findViewById(R.id.power);
    }

    public void danmakucontrol(View view) {
        if (mediaPlayer != null && mDanmakuView != null) {
            if (isdanmakushowing == true) {
                mDanmakuView.setVisibility(View.GONE);
                showdanmaku.setImageResource(R.mipmap.danmakuoff);
                isdanmakushowing = false;
            } else {
                mDanmakuView.setVisibility(View.VISIBLE);
                showdanmaku.setImageResource(R.mipmap.danmakuon);
                isdanmakushowing = true;
            }
        }
    }

    private void adddanmaku() {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = "弹幕君准备完毕～(*≧ω≦)";
        danmaku.padding = 5;
        danmaku.priority = 1;
        danmaku.textColor = Color.WHITE;
        mDanmakuView.addDanmaku(danmaku);
    }

    private BaseDanmakuParser createParser(InputStream stream) {
        if (stream == null) {
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    public void showcon(View view) {
        if ((linearLayouttop.getVisibility()) == View.GONE) {
            showdanmaku.setVisibility(View.VISIBLE);
            linearLayouttop.setVisibility(View.VISIBLE);
            linearLayoutbottom.setVisibility(View.VISIBLE);
            showtime.setVisibility(View.VISIBLE);
            batteryView.setPower(manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
            danmaku_btn.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) speed.setVisibility(View.VISIBLE);
            date = new Date();
            dateFormat = new SimpleDateFormat("HH:mm");
            local_time.setText(dateFormat.format(date));
            autohide();
        } else {
            if (timer2 != null) timer2.cancel();
            danmaku_btn.setVisibility(View.GONE);
            showdanmaku.setVisibility(View.GONE);
            linearLayouttop.setVisibility(View.GONE);
            linearLayoutbottom.setVisibility(View.GONE);
            showtime.setVisibility(View.GONE);
            speed.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent();
        if (mediaPlayer != null) data.putExtra("time", mediaPlayer.getCurrentPosition());
        else data.putExtra("time", 0);
        data.putExtra("isfin", false);
        setResult(0, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.e("生命周期","销毁");
        if (timer2 != null) timer2.cancel();
        if (sound != null) sound.cancel();
        if (progresstimer != null) progresstimer.cancel();
        if (mediaPlayer != null) mediaPlayer.release();
        if (mDanmakuView != null) mDanmakuView.release();
//        delete0 = new File(videoname);
//        delete1 = new File(danmakuname);
//        delete0.delete();
//        delete1.delete();
//        int i = 0;
//        while (i <= 4) {
//            File file1 = new File(basedirectory + "/wearplayerdata/Temporarypartvideo/" + i + ".mp4");
//            file1.delete();
//            i++;
//        }
        try {
            if (inputStream != null) inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e("生命周期","pause");
        if (mediaPlayer != null) mediaPlayer.pause();
        if (mDanmakuView != null) mDanmakuView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("生命周期","恢复");
        if (mediaPlayer != null) {
            String con = ControlButton.getText().toString();
            if (con == "| |") {
                mediaPlayer.start();
                if (mDanmakuView != null) mDanmakuView.start(mediaPlayer.getCurrentPosition());
            }
        }
    }

//    private void downvideo() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                Log.e("开始下完整视频", "start");
//                Request request = new Request.Builder().url(url)
//                        .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36")
//                        .addHeader("Referer", "https://www.bilibili.com/")
//                        .addHeader("Range", "bytes=0-")
//                        .addHeader("Origin", "https://www.bilibili.com/")
//                        .build();
//                Call call = okHttpClient.newCall(request);
//                try {
//                    Response response = call.execute();
//                    InputStream inputStream = response.body().byteStream();
//                    final long lengh = response.body().contentLength();
//                    TotalFileSize = (float) lengh;
//                    //获取总文件大小
//                    FileOutputStream fileOutputStream = new FileOutputStream(videoname);
//                    Timer timer = new Timer();
//                    TimerTask timerTask = new TimerTask() {
//                        @Override
//                        public void run() {
//                            String show = ((float) delete0.length() / TotalFileSize) * 100 + "%";
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    partprogress2.setText(show);
//                                }
//                            });
//                        }
//                    };
//                    timer.schedule(timerTask, 1000, 100);
//                    int len = 0;
//                    byte[] bytes = new byte[8192];
//                    while ((len = inputStream.read(bytes)) != -1) {
//                        fileOutputStream.write(bytes, 0, len);
//                    }
//                    fileOutputStream.flush();
//                    inputStream.close();
//                    fileOutputStream.close();
//                    Log.e("完整下完", "ok");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            downloadprogress.removeAllViews();
//                        }
//                    });
////                    playing(videoname);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
//
//    private void getvideosize() {
//        Request request = new Request.Builder().url(url)
//                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36")
//                .addHeader("Referer", "https://www.bilibili.com/")
//                .addHeader("Origin", "https://www.bilibili.com/")
//                .build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                totalvideosize = (int) (response.body().contentLength());
//                Log.e("文件总大小", totalvideosize + "");
//                //2000000
//                if (totalvideosize <= 3000000) {
//                    Log.e("单线程", "start");
//                    downvideo();
//                } else {
//                    Log.e("启用多线程", "start");
//                    page = totalvideosize / 5;
////                    Log.e("page",page+"");
//                    partvideo0(0, page - 1);
//                    partvideo1(page, page * 2 - 1);
//                    partvideo2(page * 2, page * 3 - 1);
//                    partvideo3(page * 3, page * 4 - 1);
//                    partvideo4(page * 4);
////                    partvideo(page);
//                }
//            }
//        });
//    }
//
//    private void partvideo0(int start, int end) {
//
//        //1.1创建okHttpClient
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        //1.2创建Request对象
//        Request request = new Request.Builder().url(url)
//                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36")
//                .addHeader("Referer", "https://www.bilibili.com/")
//                .addHeader("Range", "bytes=" + start + "-" + end)
//                .build();
//
//        //2.把Request对象封装成call对象
//        Call call = okHttpClient.newCall(request);
//
//        //3.发起异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream inputStream = response.body().byteStream();
//                final File file = new File(basedirectory + "/wearplayerdata/Temporarypartvideo", "0.mp4");
//                FileOutputStream outputStream = new FileOutputStream(file);
//                Timer timer = new Timer();
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        String show = ((float) file.length() / (float) page) * 100 + "%";
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                partprogress0.setText(show);
//                            }
//                        });
//                    }
//                };
//                timer.schedule(timerTask, 1000, 100);
//                int len = 0;
//                byte[] bytes = new byte[1024];
//                while ((len = inputStream.read(bytes)) != -1) {
//                    outputStream.write(bytes, 0, len);
//                }
//                inputStream.close();
//                outputStream.close();
//                one = true;
//            }
//        });
//
//    }
//
//    private void partvideo1(int start, int end) {
//
//        //1.1创建okHttpClient
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        //1.2创建Request对象
//        Request request = new Request.Builder().url(url)
//                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36")
//                .addHeader("Referer", "https://www.bilibili.com/")
//                .addHeader("Range", "bytes=" + start + "-" + end)
//                .build();
//
//        //2.把Request对象封装成call对象
//        Call call = okHttpClient.newCall(request);
//
//        //3.发起异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream inputStream = response.body().byteStream();
//                final File file = new File(basedirectory + "/wearplayerdata/Temporarypartvideo", "1.mp4");
//                FileOutputStream outputStream = new FileOutputStream(file);
//                Timer timer = new Timer();
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        String show = ((float) file.length() / (float) page) * 100 + "%";
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                partprogress1.setVisibility(View.VISIBLE);
//                                partprogress1.setText(show);
//                            }
//                        });
//                    }
//                };
//                timer.schedule(timerTask, 1000, 100);
//                int len = 0;
//                byte[] bytes = new byte[1024];
//                while ((len = inputStream.read(bytes)) != -1) {
//                    outputStream.write(bytes, 0, len);
//                }
//                inputStream.close();
//                outputStream.close();
//                two = true;
//            }
//        });
//        judge();
//    }
//
//    private void partvideo2(int start, int end) {
//
//        //1.1创建okHttpClient
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        //1.2创建Request对象
//        Request request = new Request.Builder().url(url)
//                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36")
//                .addHeader("Referer", "https://www.bilibili.com/")
//                .addHeader("Range", "bytes=" + start + "-" + end)
//                .build();
//
//        //2.把Request对象封装成call对象
//        Call call = okHttpClient.newCall(request);
//
//        //3.发起异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream inputStream = response.body().byteStream();
//                final File file = new File(basedirectory + "/wearplayerdata/Temporarypartvideo", "2.mp4");
//                FileOutputStream outputStream = new FileOutputStream(file);
//                Timer timer = new Timer();
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        String show = ((float) file.length() / (float) page) * 100 + "%";
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                partprogress2.setVisibility(View.VISIBLE);
//                                partprogress2.setText(show);
//                            }
//                        });
//                    }
//                };
//                timer.schedule(timerTask, 1000, 100);
//                int len = 0;
//                byte[] bytes = new byte[1024 * 10];
//                while ((len = inputStream.read(bytes)) != -1) {
//                    outputStream.write(bytes, 0, len);
//                }
//                inputStream.close();
//                outputStream.close();
//                three = true;
//            }
//        });
//
//    }
//
//    private void partvideo3(int start, int end) {
////        Log.e("video3",start+"-"+end);
//        //1.1创建okHttpClient
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        //1.2创建Request对象
//        Request request = new Request.Builder().url(url)
//                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36")
//                .addHeader("Referer", "https://www.bilibili.com/")
//                .addHeader("Range", "bytes=" + start + "-" + end)
//                .build();
//
//        //2.把Request对象封装成call对象
//        Call call = okHttpClient.newCall(request);
//
//        //3.发起异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream inputStream = response.body().byteStream();
//                final File file = new File(basedirectory + "/wearplayerdata/Temporarypartvideo", "3.mp4");
//                FileOutputStream outputStream = new FileOutputStream(file);
//                Timer timer = new Timer();
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        String show = ((float) file.length() / (float) page) * 100 + "%";
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                partprogress3.setVisibility(View.VISIBLE);
//                                partprogress3.setText(show);
//                            }
//                        });
//                    }
//                };
//                timer.schedule(timerTask, 1000, 100);
//                int len = 0;
//                byte[] bytes = new byte[1024 * 10];
//                while ((len = inputStream.read(bytes)) != -1) {
//                    outputStream.write(bytes, 0, len);
//                }
//                inputStream.close();
//                outputStream.close();
//                four = true;
//            }
//        });
//
//    }
//
//    private void partvideo4(int start) {
////        Log.e("最后的page",start+"");
//        //1.1创建okHttpClient
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        //1.2创建Request对象
//        Request request = new Request.Builder().url(url)
//                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36")
//                .addHeader("Referer", "https://www.bilibili.com/")
//                .addHeader("Range", "bytes=" + start + "-")
//                .build();
//
//        //2.把Request对象封装成call对象
//        Call call = okHttpClient.newCall(request);
//
//        //3.发起异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream inputStream = response.body().byteStream();
//                final File file = new File(basedirectory + "/wearplayerdata/Temporarypartvideo", "4.mp4");
//                FileOutputStream outputStream = new FileOutputStream(file);
//                Timer timer = new Timer();
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        String show = ((float) file.length() / (float) page) * 100 + "%";
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                partprogress4.setVisibility(View.VISIBLE);
//                                partprogress4.setText(show);
//                            }
//                        });
//                    }
//                };
//                timer.schedule(timerTask, 1000, 100);
//                int len = 0;
//                byte[] bytes = new byte[1024 * 10];
//                while ((len = inputStream.read(bytes)) != -1) {
//                    outputStream.write(bytes, 0, len);
//                }
//                inputStream.close();
//                outputStream.close();
//                five = true;
////                Log.e("4.mp4","ok");
//            }
//        });
//    }
//
//    private void judge() {
//        judge = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (one == true && two == true && three == true && four == true && five == true) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            partprogress0.setText("合并中…");
//                            partprogress1.setText("(≧∇≦)");
//                            partprogress2.setVisibility(View.GONE);
//                            partprogress3.setVisibility(View.GONE);
//                            partprogress4.setVisibility(View.GONE);
//                        }
//                    });
//                    try {
//                        copy();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        judge.schedule(timerTask, 1000, 500);
//    }
//
//    private void copy() throws IOException {
//        judge.cancel();
//        video = new File(videoname);
//        int i = 0;
//        outputStream = new FileOutputStream(video);
//        while (i <= 4) {
//            File file = new File(basedirectory + "/wearplayerdata/Temporarypartvideo/" + i + ".mp4");
//            InputStream inputStream = new FileInputStream(file);
//            byte[] byt = new byte[262144];
//            int len;
//            while ((len = inputStream.read(byt)) != -1) {
//                outputStream.write(byt, 0, len);
//            }
//            inputStream.close();
//            i++;
//        }
////        playing(videoname);
//        outputStream.close();
//        int i1 = 0;
//        while (i1 <= 4) {
//            File file1 = new File(basedirectory + "/wearplayerdata/Temporarypartvideo/" + i + ".mp4");
//            file1.delete();
//            i1++;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                downloadprogress.removeAllViews();
//                circle.setVisibility(View.GONE);
//            }
//        });
//    }

    private void playing() {
//        Log.e("videourl",nowurl);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setSurface(mSurface);
            mediaPlayer.setLooping(preferences2.getBoolean("videoloop", false));//循环播放
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        mediaPlayer.setSurface(new Surface(textureView.getSurfaceTexture()));
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                if (preferences2.getBoolean("round", false) == true) changeRoundVideoSize(i, i1);
                else changeVideoSize(i, i1);

            }
        });

    }

    private void changeRoundVideoSize(int width, int height) {
        Log.e("wid", width + "");
        Log.e("hei", height + "");
        float video_mul = (float) height / (float) width;
        double sqrt = Math.sqrt(((double) Screenwidth * (double) Screenwidth) / ((((double) height * (double) height) / ((double) width * (double) width)) + 1));
        int show_height = (int) (sqrt * video_mul + 0.5);
//        Log.e("mul",video_mul+"");
//        Log.e("长高",sqrt+"||"+show_height);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams((int) (sqrt + 0.5), show_height));
        if (preferences2.getBoolean("videofull", false) != false) {
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams((int) Screenwidth, (int) Screenheight));
        }
    }

    private void changeVideoSize(int width, int height) {
        float multiplewidth = Screenwidth / width;
        float multipleheight = Screenheight / height;
        float endhi1 = height * multipleheight;
        float endwi1 = width * multipleheight;
        float endhi2 = height * multiplewidth;
        float endwi2 = width * multipleheight;
        if (endhi1 <= Screenheight && endwi1 <= Screenwidth) {
            showheight = (int) (endhi1 + 0.5);
            showwidth = (int) (endwi1 + 0.5);
        } else {
            showheight = (int) (endhi2 + 0.5);
            showwidth = (int) (endwi2 + 0.5);
        }
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(showwidth, showheight));
        if (preferences2.getBoolean("videofull", false) != false) {
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams((int) Screenwidth, (int) Screenheight));
        }
    }

    private void downdanmu() {
        Request request = new Request.Builder().url(danmaku).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //请求失败
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                Sink sink = null;
//                BufferedSink bufferedSink = null;
//                try {
//                    File dest = new File(basedirectory + "/wearplayerdata", "Danmaku.xml");
//                    if (!dest.exists()) dest.createNewFile();
//                    sink = Okio.sink(dest);
//                    byte[] decompressBytes = decompress(response.body().bytes());//调⽤解压函数进⾏解压，返回包含解压后数据的byte数组
////                    Log.d("length", String.valueOf(decompressBytes.length));
//                    bufferedSink = Okio.buffer(sink);
//                    bufferedSink.write(decompressBytes);//将解压后数据写⼊⽂件（sink）中
//                    bufferedSink.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (bufferedSink != null) {
//                        bufferedSink.close();
//                    }
//                }
                byte[] decompress = decompress(response.body().bytes());
                InputStream danmakuinputstream = new ByteArrayInputStream(decompress);
                streamdanmaku(danmakuinputstream);
            }
        });
    }

    private void streamdanmaku(InputStream danmakuurl) {
        if (danmakuurl != null) {
            mParser = createParser(danmakuurl);
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    adddanmaku();
                    Log.e("准备", "弹幕准备完毕");
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });
            mDanmakuView.prepare(mParser, mContext);
            mDanmakuView.showFPS(preferences2.getBoolean("showfps", false));
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    public static byte[] decompress(byte[] data) {
        byte[] output;
        Inflater decompresser = new Inflater(true);//这个true是关键
        decompresser.reset();
        decompresser.setInput(data);
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[2048];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        decompresser.end();
        return output;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        videoall = mediaPlayer.getDuration();
        isdanmakushowing = true;
        mDanmakuView.start();
        showdanmaku.setImageResource(R.mipmap.danmakuon);
        progressBar.setMax(videoall);
        int minutes = videoall / 60000;
        int seconds = videoall % 60000 / 1000;
        ControlButton.setText("| |");
        alltime.setText(minutes + ":" + seconds);
        progresschange();
    }

    private void progresschange() {
        progresstimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (ischanging == false && mediaPlayer != null) {
                    videonow = mediaPlayer.getCurrentPosition();
                    progressBar.setProgress(videonow);
                    int minute = videonow / 60000;
                    int second = videonow % 60000 / 1000;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timenow.setText(minute + ":" + second);
                        }
                    });
                }
            }
        };
        progresstimer.schedule(task, 0, 500);
    }

    public void controlvideo(View view) {
        if ((ControlButton.getText().toString()) == "| |") {
            mediaPlayer.pause();
            ControlButton.setText("‣");
            mDanmakuView.pause();
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                ControlButton.setText("| |");
                if (mDanmakuView != null) {
                    mDanmakuView.start(mediaPlayer.getCurrentPosition());
                }
            }
        }
        autohide();
    }

    public void addsound(View view) {
        timer2.cancel();
        int soundnow = audioManager.getStreamVolume(STREAM_MUSIC);
        int maxsound = audioManager.getStreamMaxVolume(STREAM_MUSIC);
        int added = maxsound;
        if (soundnow != maxsound) {
            added = soundnow + 1;
        }
        audioManager.setStreamVolume(STREAM_MUSIC, added, 0);
        float show = (float) (added) / (float) maxsound * 100;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showsound.setVisibility(View.VISIBLE);
                showsound.setText("音量：" + (int) show + "%");
            }
        });
        hidesound();
    }

    private void hidesound() {
        if (sound != null) sound.cancel();
        sound = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showsound.setVisibility(View.GONE);
                    }
                });
            }
        };
        sound.schedule(timerTask, 3000);
    }

    public void cutsound(View view) {
        timer2.cancel();
        int soundnow = audioManager.getStreamVolume(STREAM_MUSIC);
        int maxsound = audioManager.getStreamMaxVolume(STREAM_MUSIC);
        int added = 0;
        if (soundnow != 0) {
            added = soundnow - 1;
        }
        audioManager.setStreamVolume(STREAM_MUSIC, added, 0);
        float show = (float) added / (float) maxsound * 100;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showsound.setVisibility(View.VISIBLE);
                showsound.setText("音量：" + (int) show + "%");
            }
        });
        hidesound();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowManager windowManager = this.getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        Screenwidth = displayMetrics.widthPixels;//获取屏宽
        Screenheight = displayMetrics.heightPixels;//获取屏高
        if (mediaPlayer != null) {
            if (preferences2.getBoolean("round", false) == true) {
                changeRoundVideoSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
            } else changeVideoSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
        }
    }

    public void rotate(View v) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void asdfgh(View view) {
    }

    public void finish(View view) {
        Intent data = new Intent();
        if (mediaPlayer != null) data.putExtra("time", mediaPlayer.getCurrentPosition());
        else data.putExtra("time", 0);
        data.putExtra("isfin", false);
        setResult(0, data);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
    }

    public void videospeed(View v) {
        if (mediaPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (speed.getText().toString()) {
                case "x 0.5":
                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.0f));
                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f));
                    speed.setText("x 1.0");
                    break;
                case "x 1.0":
                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.25f));
                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 0.8f);
                    speed.setText("x1.25");
                    break;
                case "x1.25":
                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(1.5f));
                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 0.66f);
                    speed.setText("x 1.5");
                    break;
                case "x 1.5":
                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(2.0f));
                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 0.5f);
                    speed.setText("x 2.0");
                    break;
                case "x 2.0":
                    mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(0.5f));
                    mContext.setScrollSpeedFactor(preferences.getFloat("danmakuspeed", 1.0f) * 2f);
                    speed.setText("x 0.5");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        mSurface = new Surface(surfaceTexture);
        playing();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        surfaceTexture = null;
        mSurface = null;
        if (mediaPlayer != null) {
            mediaPlayer.release();
//            mediaPlayer.stop();
        }
        mSurface.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }
}