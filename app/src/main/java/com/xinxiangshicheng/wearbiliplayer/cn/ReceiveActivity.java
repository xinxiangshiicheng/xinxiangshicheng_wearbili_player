package com.xinxiangshicheng.wearbiliplayer.cn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.xinxiangshicheng.wearbiliplayer.cn.player.PlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReceiveActivity extends AppCompatActivity {
    private String videourl, danmakuurl, title, bid;
    private int avid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        ImmersionBar.with(this).transparentBar().init();
        Uri uri = getIntent().getData();
        init(uri);
    }

    //https://api.bilibili.com/x/web-interface/view?bvid=1dF411P7V1
    private void init(Uri uri) {
        if (uri != null) {
            bid = uri.getQueryParameter("bvid");
//            bid=bid.replace("BV","");
            avid = Integer.parseInt(uri.getQueryParameter("aid"));
            Log.e("aid=" + avid, "bvid=" + bid);
            int cid = Integer.parseInt(uri.getQueryParameter("cid"));
            Log.e("cid", cid + "");
            danmakuurl = "https://comment.bilibili.com/" + cid + ".xml";
            //https://comment.bilibili.com/776707267.xml
            if (avid == 0) {
                Log.e("1", "1");
                request(0, 0, bid, cid);
//                requesttitle(0,0,bvid);
            } else {
                Log.e("else", "else");
                request(1, avid, null, cid);
//                requesttitle(1,aid,null);
            }
        }
    }

    private void requesttitle(int id, int aid, String bvid) {
        String url;
        if (id == 0) {
            url = "https://api.bilibili.com/x/web-interface/view?bvid=" + bvid;
        } else {
            url = "https://api.bilibili.com/x/web-interface/view?aid=" + aid;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("wearbili请求视频简介", "失败");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String body = response.body().string();
                try {
                    JSONObject body1 = new JSONObject(body);
                    JSONObject data = new JSONObject(body1.get("data").toString());
                    title = data.getString("title");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setClass(ReceiveActivity.this, PlayerActivity.class);
                intent.putExtra("url", videourl);
                intent.putExtra("danmaku", danmakuurl);
                intent.putExtra("title", title);
                startActivity(intent);
                finish();
            }
        });
    }

    private void request(int id, int aid, String bvid, int cid) {
        String url;
        if (id == 0) {
            url = "https://api.bilibili.com/x/player/playurl?bvid=" + bvid + "&cid=" + cid + "&qn=16&type=mp4&platform=html5";
        } else {
            url = "https://api.bilibili.com/x/player/playurl?avid=" + aid + "&cid=" + cid + "&qn=16&type=mp4&platform=html5";
        }
        Log.e("url", url);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("wearbili请求视频地址", "失败");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONObject body1 = new JSONObject(body);
                    JSONObject data = new JSONObject(body1.get("data").toString());
                    JSONArray durl = data.getJSONArray("durl");
                    JSONObject video_url = durl.getJSONObject(0);
                    videourl = video_url.getString("url");
                    Log.e("video_url", videourl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                String url_old = null;
//                Log.e("body", body);
//                String resStr = "(http)(.*?)(00\",\")";
//                Pattern pattern = Pattern.compile(resStr);
//                Matcher matcher = pattern.matcher(body);
//                while (matcher.find()) {
//                    url_old = matcher.group(0);
//                }
//                videourl = url_old.replace("\\u0026", "&");
//                Log.e("url", videourl);
                if (avid == 0) {
                    Log.e("1", "1");
                    requesttitle(0, 0, bid);
                } else {
                    Log.e("else", "else");
                    requesttitle(1, avid, null);
                }
            }
        });
    }
}