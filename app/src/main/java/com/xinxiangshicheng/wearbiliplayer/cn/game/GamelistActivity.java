package com.xinxiangshicheng.wearbiliplayer.cn.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.R;

public class GamelistActivity extends AppCompatActivity {
    private Button dinosaur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamelist);
        dinosaur = findViewById(R.id.dinosaur);
        ImmersionBar.with(this).transparentBar().init();
        dinosaur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(GamelistActivity.this,GuessCupActivity.class);
                startActivity(intent);
            }
        });
    }
    public void fanhui(View view){
        finish();
    }
}