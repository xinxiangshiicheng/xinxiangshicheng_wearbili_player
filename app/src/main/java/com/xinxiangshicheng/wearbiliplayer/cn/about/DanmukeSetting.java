package com.xinxiangshicheng.wearbiliplayer.cn.about;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.R;

public class DanmukeSetting extends AppCompatActivity {
    private EditText maxline,danmakuspeed,danmakusize,transparency;
    private CheckBox allowoverlap,mergeduplicate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).transparentBar().init();
        setContentView(R.layout.activity_danmuke_setting);
        danmakusize = findViewById(R.id.danmakusize);
        maxline = findViewById(R.id.maxline);
        danmakuspeed = findViewById(R.id.danmakuspeed);
        allowoverlap = findViewById(R.id.allowoverlap);
        mergeduplicate = findViewById(R.id.mergeduplicate);
        transparency = findViewById(R.id.danmaku_transparency);
        SharedPreferences preferences = getSharedPreferences("danmakusetting",MODE_PRIVATE);
        maxline.setText(preferences.getInt("maxline",1)+"");
        danmakuspeed.setText(preferences.getFloat("danmakuspeed",1.0f)+"");
        allowoverlap.setChecked(preferences.getBoolean("allowoverlap",true));
        mergeduplicate.setChecked(preferences.getBoolean("mergeduplicate",false));
        danmakusize.setText((preferences.getFloat("danmakusize",1.0f))+"");
        String show_transparency = preferences.getFloat("transparency",0.5f)*100+"";
        transparency.setText(show_transparency);
    }
    public void back3(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean allow,merge;
        if (allowoverlap.isChecked()) allow = true;else allow = false;
        if (mergeduplicate.isChecked()) merge = true;else merge = false;
        String newline = maxline.getText().toString();
        String newspeed = danmakuspeed.getText().toString();
        String newtextsize = danmakusize.getText().toString();
        String newtransparency = this.transparency.getText().toString();
        if (newtextsize.length()<=0) newtextsize = "1.0";
        if (newline.length()<=0) newline = "0";
        if (newspeed.length()<=0) newspeed = "1.0";
        int line = Integer.parseInt(newline);
        float speed = Float.parseFloat(newspeed);
        float textsize = Float.parseFloat(newtextsize);
        float transparency = Float.parseFloat(newtransparency);
        SharedPreferences sharedPreferences = getSharedPreferences("danmakusetting",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("allowoverlap",allow);
        editor.putBoolean("mergeduplicate",merge);
        editor.putInt("maxline",line);
        editor.putFloat("danmakuspeed",speed);
        editor.putFloat("danmakusize",textsize);
        editor.putFloat("transparency",transparency/100f);
        editor.commit();
        Toast.makeText(DanmukeSetting.this,"设置已经保存好啦(*≧ω≦)，喵喵～",Toast.LENGTH_LONG).show();
    }
}