package org.wower.ad_5g_manage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.wower.ad_5g_manage.model.Video;
import org.wower.ad_5g_manage.utils.Post;
import org.wower.ad_5g_manage.utils.RealPathFromUriUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User_Panel extends AppCompatActivity {

    public static final int REQUEST_PICK_VIDEO = 1001;
    private String path;
    String res = "ss";
    TextView video_path;
    private List<Video> videoList = new ArrayList<>();
    private int numberOfVideos = 0;
    TextView playedNumber_textview;
    private ProgressBar progressBar;
    private int isUploadDown = 1;
    private EditText play_time;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_panel);


        videoList = (List<Video>) getIntent().getSerializableExtra("video_list");

        numberOfVideos = videoList.size(); // 视频总数量
        int playedOfVideos = 0; // 视频总播放量
        for (Video video : videoList) {
            playedOfVideos += video.getPlayedNumber();
        }

        TextView uname_textview = (TextView) findViewById(R.id.uname);
        playedNumber_textview = (TextView) findViewById(R.id.all_played_numbers);
        TextView videoNumbers_textview = (TextView) findViewById(R.id.video_numbers);
        Button adList = (Button) findViewById(R.id.btn_ad_list);
        video_path = (TextView) findViewById(R.id.video_path);

        Intent intent = getIntent();
        uname_textview.setText(intent.getStringExtra("uname"));
        playedNumber_textview.setText(String.valueOf(playedOfVideos));
        videoNumbers_textview.setText(String.valueOf(numberOfVideos));
        video_path.setText("请选择视频");

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_user);
        progressBar.setVisibility(View.GONE);


        if (intent.getBooleanExtra("isLogin", false)) {
            // 来自登录的
            Toast.makeText(this, "登录成功，欢迎您 " + intent.getStringExtra("uname"), Toast.LENGTH_SHORT).show();
        } else if (intent.getBooleanExtra("isRegister", false)) {
            Toast.makeText(this, "注册成功，欢迎您 " + intent.getStringExtra("uname"), Toast.LENGTH_SHORT).show();
        }

        adList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(User_Panel.this, AdList.class);
                intent1.putExtra("video_list", (Serializable) videoList);
                intent1.putExtra("uname", intent.getStringExtra("uname"));
                intent1.putExtra("password", intent.getStringExtra("password"));
                startActivity(intent1);
            }
        });

        // 点击输入框弹出选择播放的时间
        play_time = (EditText)findViewById(R.id.play_time);
        play_time.setInputType(InputType.TYPE_NULL); //不显示系统输入键盘
        play_time.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    Calendar c = Calendar.getInstance();
                    new TimePickerDialog(User_Panel.this, new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            play_time.setText("视频播放时间:" + hourOfDay + ":" + minute);
                        }
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();

                }
            }
        });

        play_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar c = Calendar.getInstance();
                new TimePickerDialog(User_Panel.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        play_time.setText("视频播放时间:" + hourOfDay + ":" + minute);
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();

            }
        });


        Button select = (Button) findViewById(R.id.select_video);
        Button upload = (Button) findViewById(R.id.upload);

        EditText filename = (EditText) findViewById(R.id.filename);
        EditText editText_level = (EditText) findViewById(R.id.level);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                startActivityForResult(intent, REQUEST_PICK_VIDEO);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = filename.getText().toString();
                String level = editText_level.getText().toString();
                String play_time_editvalue = play_time.getText().toString();
                String timeOfPlay[] = play_time_editvalue.split(":");
                if (fileName.equals("") || fileName == null) {
                    Toast.makeText(User_Panel.this, "您还未输入视频名称", Toast.LENGTH_SHORT).show();
                } else if (level.equals("") || level == null) {
                    Toast.makeText(User_Panel.this, "您还未输入视频的优先级", Toast.LENGTH_SHORT).show();
                } else if (play_time_editvalue.equals("") || play_time_editvalue == null) {
                    // 不选择播放时间时候
                    // 这里写上传的模块
                    progressBar.setVisibility(View.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                res = Post.upload(path, fileName, level, intent.getStringExtra("uname"));
                                path = null;
                                isUploadDown = 0;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    while (isUploadDown == 1);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(User_Panel.this, "上传完成", Toast.LENGTH_SHORT).show();
                    video_path.setText("上传完成");

                }else {
                    // 选择播放时间的时候
                    // 这里写上传的模块
                    progressBar.setVisibility(View.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                res = Post.upload(path, fileName, level, intent.getStringExtra("uname"), timeOfPlay[1], timeOfPlay[2]);
                                path = null;
                                isUploadDown = 0;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    while (isUploadDown == 1);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(User_Panel.this, "上传完成", Toast.LENGTH_SHORT).show();
                    video_path.setText("上传完成");

                }
            }
        });

    }

    //获取文件夹返回的uri，即data.getData()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_VIDEO:
                    System.out.println("data:"+data);
                    if (data != null) {

                        // 获取视频的path
                        path = RealPathFromUriUtils.getFileAbsolutePath(this, data.getData());
                        System.out.println("data.getData():"+data.getData());
                        System.out.println("视频路径："+path);
                        video_path.setText(path);
                    } else {
                        Toast.makeText(User_Panel.this, "视频损坏，请重新选择", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}