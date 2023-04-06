package com.xinxiangshicheng.wearbiliplayer.cn;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;

public class WearOsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_os);
        ImmersionBar.with(this).transparentBar().init();
    }
    public void quanxianback(View v){
        finish();
    }
}