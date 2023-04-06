package com.xinxiangshicheng.wearbiliplayer.cn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.about.DanmukeSetting;
import com.xinxiangshicheng.wearbiliplayer.cn.about.OpenSourceActivity;
import com.xinxiangshicheng.wearbiliplayer.cn.about.VideoSetting;

public class SettingActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        textView = findViewById(R.id.text);
        ImmersionBar.with(this).transparentBar().init();
    }
    public void back(View view){
        finish();
    }
    public void updata(View view){
        if (textView.getVisibility()==View.GONE || textView.length()>0){
            textView.setVisibility(View.VISIBLE);
            String data = "更新日志：\n" +
                    "Beat0.1 小电视播放器推出，可以正常播放腕上哔哩的视频\n\n" +
                    "Beat0.2 修复了SurfaceView在退出重进后被销毁导致视频有声无画面bug，修复了进度条和标题重复点击后鬼畜的bug，新增音量控制功能\n\n" +
                    "Beat0.3 修复了一些bug，新增音量控制时实时显示当前音量功能，增大了音量控制按钮大小，修改了音量控制按钮图标\n\n" +
                    "v1.0 修复了一些bug，完成了主页及主页一部分功能的制作\n" +
                    "v1.1.1 修复了一些bug，新增了弹幕播放功能\n" +
                    "v1.2.1 修复了一些bug\n" +
                    "v1.3 完成了所有重要功能，系列基本完结，主页那几个没做到，emmmmm就当永远没填的坑吧\n" +
                    "v1.3.1 紧急修复了横屏bug(但横屏bug仍在)\n" +
                    "v1.4 修复横屏bug，修改了图标，基本完结\n" +
                    "v1.5 修复了部分bug，新增小游戏猜杯子，新增填充播放,全新适配了更加好用的wearbili\n" +
                    "v1.5.3 正式支持在线播放，无需文件权限，搭配小抬腕可播放腕上哔哩视频，支持wearbili视频播放\n" +
                    "*支持在线播放无需文件权限";
            textView.setText(data);
        }else {
            textView.setVisibility(View.GONE);
        }
    }
    public void authowords(View view){
        if (textView.getVisibility()==View.GONE || textView.length()>0){
            textView.setVisibility(View.VISIBLE);
            String words = "因为不想付费，于是研究腕上哔哩开源的代码并开发了这个小电视播放器，其实我感觉luern的代码写的挺好的，估计人家大学是计算机专业的，代码写的很整齐，不像我的代码一样乱成一片:(\n\n" +
                    "其实说实话我对Android开发几乎是没什么基础的，java会的也不多，了解比较多的还是H5开发，开发小电视播放器的时候我基本是边学边写的，踩了特备多的坑(这也就是为啥开发了这么久的原因)" +
                    "\n\n" +
                    "后来我适配了wearbili，并开发了小抬腕用于腕上哔哩接入";
            textView.setText(words);
        }else {
            textView.setVisibility(View.GONE);
        }
    }
    public void source(View view){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, OpenSourceActivity.class);
        startActivity(intent);
    }
    public void danmaku(View v){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, DanmukeSetting.class);
        startActivity(intent);
    }
    public void videosetting(View view){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, VideoSetting.class);
        startActivity(intent);
    }
}