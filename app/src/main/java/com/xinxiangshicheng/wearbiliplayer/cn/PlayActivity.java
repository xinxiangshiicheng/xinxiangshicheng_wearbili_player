package com.xinxiangshicheng.wearbiliplayer.cn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.player.PlayerActivity;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText;
    private Button play, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ImmersionBar.with(this).transparentBar().init();
        //从链接播放
        editText = findViewById(R.id.input_url);
        play = findViewById(R.id.fromurl_play);
        delete = findViewById(R.id.fromurl_delete);
        play.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    public void tuichu1(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fromurl_play:
                //点击播放
                String url = editText.getText().toString();
                if (!url.equals("")){
                    //输入不为空
                    Intent intent = new Intent();
                    intent.putExtra("url",url);
                    intent.putExtra("danmaku","https://comment.bilibili.com/33095956.xml");
                    intent.putExtra("title","小电视播放器在线播放_作者心想事成_真的不给个关注吗？");
                    intent.setClass(PlayActivity.this,PlayerActivity.class);
                    startActivity(intent);
                }else {
                    //输入为空
                    Intent action = new Intent(Intent.ACTION_VIEW);
                    StringBuilder builder = new StringBuilder();
                    builder.append("wearbiliplayer://receive:8080/play?bvid=1tT411c7Dw&cid=809188104&aid=0");
                    action.setData(Uri.parse(builder.toString()));
                    startActivity(action);
                }
                break;
            default:
                //点击删除
                editText.setText("");
                break;
        }
    }
}