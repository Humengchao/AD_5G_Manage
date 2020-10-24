package org.wower.ad_5g_manage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.wower.ad_5g_manage.model.User;
import org.wower.ad_5g_manage.model.Video;
import org.wower.ad_5g_manage.utils.Post;

import java.io.Serializable;
import java.util.List;

public class Login extends AppCompatActivity {

    private String info = null; // 服务器返回的String

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button btn_login = (Button) findViewById(R.id.btn_login);
        EditText uname = (EditText) findViewById(R.id.account_input);
        EditText password = (EditText) findViewById(R.id.password_input);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_login);

        progressBar.setVisibility(View.GONE);




        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_uname = uname.getText().toString();
                String str_password = password.getText().toString();
                User user = new User(str_uname, str_password);

                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                         info = Post.login(user);

                        if (info == null) {
                            // info 为空时候，
                            Looper.prepare();
                            Toast.makeText(Login.this, "网络错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else if (info.equals("passworderror")) {
                            // 账号密码错误
                            Looper.prepare();
                            Toast.makeText(Login.this, "账号或密码错误，请重新登录", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else {
                            // 登录成功，把info这个服务器返回的用户的视频列表传送给用户主界面这个活动
                            Gson gson = new Gson();
                            List<Video> videoList = gson.fromJson(info, new TypeToken<List<Video>>(){}.getType());
                            Intent intent = new Intent(Login.this, User_Panel.class);
                            // 把list强制类型转换成Serializable类型
                            intent.putExtra("video_list", (Serializable) videoList);
                            intent.putExtra("uname", str_uname);
                            intent.putExtra("password", str_password);
                            // 传值给user_panel是登录的还是注册的，user_panel弹出个Toast提示用户登录成功或者注册成功
                            intent.putExtra("isLogin", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }).start();
                progressBar.setVisibility(View.GONE);
            }
        });


    }
}