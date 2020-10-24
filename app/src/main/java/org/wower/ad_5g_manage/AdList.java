package org.wower.ad_5g_manage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.wower.ad_5g_manage.model.User;
import org.wower.ad_5g_manage.model.Video;
import org.wower.ad_5g_manage.utils.AdAdapter;
import org.wower.ad_5g_manage.utils.Post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdList extends AppCompatActivity {
    private List<Video> videoList = new ArrayList<>();
    private String uname = null;
    private String password = null;
    private String info = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_list);

        List<Video> videoList = (List<Video>) getIntent().getSerializableExtra("video_list");
        uname = getIntent().getStringExtra("uname");
        password = getIntent().getStringExtra("password");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ad_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        AdAdapter adAdapter = new AdAdapter(videoList);
        recyclerView.setAdapter(adAdapter);
    }

    // 将返回键通过intent返回，并传递更新好的数据,
    // 删除视频的逻辑，先把视频从recyclerview的list中移出，然后更新数据库，然后返回键重写了成为了调用登录的post请求更新数据
    @Override
    public void onBackPressed() {
        User user = new User(uname, password);

        new Thread(new Runnable() {
            @Override
            public void run() {

                info = Post.login(user);
                Gson gson = new Gson();
                List<Video> videoList = gson.fromJson(info, new TypeToken<List<Video>>(){}.getType());
                Intent intent = new Intent(AdList.this, User_Panel.class);
                // 把list强制类型转换成Serializable类型
                intent.putExtra("video_list", (Serializable) videoList);
                intent.putExtra("uname", uname);
                intent.putExtra("password", password);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).start();
    }
}
