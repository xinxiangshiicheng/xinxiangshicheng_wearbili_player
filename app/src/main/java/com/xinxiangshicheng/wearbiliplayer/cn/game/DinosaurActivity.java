package com.xinxiangshicheng.wearbiliplayer.cn.game;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.R;

public class DinosaurActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinosaur);
        ImmersionBar.with(this).transparentBar().init();

    }
}