package com.xinxiangshicheng.wearbiliplayer.cn;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        ImmersionBar.with(this).transparentBar().init();
    }
    public void support_back(View view) {
        finish();
    }
}