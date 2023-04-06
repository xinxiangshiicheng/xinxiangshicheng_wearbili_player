package com.xinxiangshicheng.wearbiliplayer.cn.about;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.R;

public class VideoSetting extends AppCompatActivity {
    private Switch showfps,loop,round,full,online;
    boolean fps,videoloop,videoround,videofull,onlinemode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_setting);
        ImmersionBar.with(this).transparentBar().init();
        showfps = findViewById(R.id.showfps);
        loop = findViewById(R.id.loop);
        online = findViewById(R.id.online);
        round=findViewById(R.id.round);
        full=findViewById(R.id.full);
        SharedPreferences sharedPreferences = getSharedPreferences("videosetting",MODE_PRIVATE);
        online.setChecked(sharedPreferences.getBoolean("online",true));
        showfps.setChecked(sharedPreferences.getBoolean("showfps",false));
        loop.setChecked(sharedPreferences.getBoolean("videoloop",true));
        round.setChecked(sharedPreferences.getBoolean("round",false));
        full.setChecked(sharedPreferences.getBoolean("videofull",false));
    }
    public void videosettingtitle(View view){
        finish();
    }
    public void details(View view){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("选项详讯")
                .setMessage("显示FPS：启用后视频左下角会实时显示视频帧率等信息(不是特别的准)\n" +
                        "在线播放：将使用在线播放服务，稳定性不算非常高\n" +
                        "洗脑循环：启用后视频会一直重复播放\n" +
                        "填充播放：视频将会填充屏幕播放\n" +
                        "圆屏适配：启用后会通过勾股定理计算圆内视频尺寸，但可能存在一定误差哦:)")
                .create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showfps.isChecked())fps=true;else fps=false;
        if (online.isChecked())onlinemode=true;else onlinemode=false;
        if (loop.isChecked())videoloop=true;else videoloop=false;
        if (round.isChecked())videoround=true;else videoround=false;
        if (full.isChecked())videofull=true;else videofull=false;
        SharedPreferences sharedPreferences = getSharedPreferences("videosetting",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("showfps",fps);
        editor.putBoolean("online",onlinemode);
        editor.putBoolean("videoloop",videoloop);
        editor.putBoolean("round",videoround);
        editor.putBoolean("videofull",videofull);
        editor.commit();
        Toast.makeText(VideoSetting.this,"视频设置已保存(*≧ω≦)",Toast.LENGTH_LONG).show();
    }
}