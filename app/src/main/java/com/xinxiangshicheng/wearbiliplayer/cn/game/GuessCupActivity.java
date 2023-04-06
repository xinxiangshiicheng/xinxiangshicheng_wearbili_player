package com.xinxiangshicheng.wearbiliplayer.cn.game;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.R;

import java.util.Random;

public class GuessCupActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private ImageView pic0;
    private ImageView pic1;
    private ImageView pic2;
    private int num;
    private int[] pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_cup);
        ImmersionBar.with(this).transparentBar().init();
        title = (TextView) findViewById(R.id.cup_title);
        pic0 = (ImageView) findViewById(R.id.cup_0);
        pic1 = (ImageView) findViewById(R.id.cup_1);
        pic2 = (ImageView) findViewById(R.id.cup_2);

        pic0.setOnClickListener(this);
        pic1.setOnClickListener(this);
        pic2.setOnClickListener(this);
    }

    public void guess(View v) {
        pic0.setImageResource(R.mipmap.cup_blue);
        pic1.setImageResource(R.mipmap.cup_blue);
        pic2.setImageResource(R.mipmap.cup_blue);
    }

    public void guess_cup_title(View v) {
        finish();
    }

    @Override
    public void onClick(View view) {
        pic0.setImageResource(R.mipmap.cup);
        pic1.setImageResource(R.mipmap.cup);
        pic2.setImageResource(R.mipmap.cup);
        jisuan();
        int ball;
        switch (num) {
            case 0:
                pic0.setImageResource(R.mipmap.cup_ball);
                ball = R.id.cup_0;
                break;
            case 1:
                pic1.setImageResource(R.mipmap.cup_ball);
                ball = R.id.cup_1;
                break;
            default:
                pic2.setImageResource(R.mipmap.cup_ball);
                ball = R.id.cup_2;
                break;
        }
        if (view.getId() == ball) {
            title.setText("恭喜你赢了，点击下方按钮再来一次");
        } else {
            title.setText("很遗憾你输了，点击下方按钮再尝试一次吧");
        }
    }

    private void jisuan() {
        Random random = new Random();
        num = (int) (random.nextFloat() * 3);
        if (num == 3) num = 2;


    }
}