package org.wower.ad_5g_manage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
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

public class Register extends AppCompatActivity {

    ProgressBar progressBar;

    private String info = null;
    String str_uname = null;
    String str_password = null;
    String str_email = null;

    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EditText uname = (EditText) findViewById(R.id.account_input);
        EditText password = (EditText) findViewById(R.id.password_input);
        EditText email = (EditText) findViewById(R.id.email_input);
        Button btn_register = (Button) findViewById(R.id.btn_enroll);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_register);

        progressBar.setVisibility(View.GONE);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                str_uname = uname.getText().toString();
                str_password = password.getText().toString();
                str_email = email.getText().toString();
                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)||TextUtils.isEmpty(str_uname)){
                    Toast.makeText(Register.this, "null", Toast.LENGTH_SHORT).show();
                }

                User user = new User(str_uname, str_password, str_email);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        info = Post.register(user);
                        if (info == null) {
                            // info 为空时候，
                            Looper.prepare();
                            Toast.makeText(Register.this, "网络错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else if (info.equals("error")) {
                            Looper.prepare();
                            Toast.makeText(Register.this, "已经存在该账号，请更换账号注册", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else {
                            // 注册成功，把info这个服务器返回的用户的视频列表传送给用户主界面这个活动
                            Gson gson = new Gson();
                            List<Video> videoList = gson.fromJson(info, new TypeToken<List<Video>>() {
                            }.getType());
                            Intent intent = new Intent(Register.this, User_Panel.class);
                            // 把list强制类型转换成Serializable类型
                            intent.putExtra("video_list", (Serializable) videoList);
                            intent.putExtra("uname", str_uname);
                            // 传值给user_panel是登录的还是注册的，user_panel弹出个Toast提示用户登录成功或者注册成功
                            intent.putExtra("isRegister", true);
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