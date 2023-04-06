package com.xinxiangshicheng.wearbiliplayer.cn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.about.InformationsActivity;
import com.xinxiangshicheng.wearbiliplayer.cn.game.GamelistActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("加载","加载");
        setContentView(R.layout.activity_main);
        ImmersionBar.with(this).transparentBar().init();
//        if (    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},100);
//        }


    }


    public void playlocatvideo(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,PlayActivity.class);
        startActivity(intent);
    }
    public void game(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, GamelistActivity.class);
        startActivity(intent);
    }
    public void setting(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,SettingActivity.class);
        startActivity(intent);
    }
    public void support(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,SupportActivity.class);
        startActivity(intent);
    }
    public void about(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, InformationsActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("wearbiliplayer://receive:8080/play?aid=848505258&bvid=0&cid=424446654"));
//        startActivity(intent);
    }
}