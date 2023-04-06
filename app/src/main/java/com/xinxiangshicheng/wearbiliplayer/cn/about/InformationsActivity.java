package com.xinxiangshicheng.wearbiliplayer.cn.about;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.R;

public class InformationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);
        ImmersionBar.with(this).transparentBar().init();
    }
    public void tuichu(View view){
        finish();
    }
}