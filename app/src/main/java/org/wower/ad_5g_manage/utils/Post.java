package org.wower.ad_5g_manage.utils;


import android.util.Log;

import com.google.gson.Gson;

import org.wower.ad_5g_manage.model.User;
import org.wower.ad_5g_manage.model.Video;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// 将数据通过json打包，然后post发送到服务器
public class Post {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // 服务器地址
//    private static final String URL = "http://ad.wqnmd.net:8080";

    private static final String URL="http://192.168.3.3:8080";

    public static Response sendPost(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建个请求对象
        Request request = new Request.Builder().url(url).post(body).build();
        // 发送请求获取响应
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 发送注册的信息给服务器，注册成功服务器返回该用户的视频列表（所有项都是空的），失败（已经有这个账号了）返回error
     * @param user
     * @return
     */
    public static String register(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        Response response = sendPost(URL + "/register", userJson);
        String res = null;
        if (response.isSuccessful()) {
            // 当注册的请求成功的发送给服务器
            try {
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 发送登录信息给服务器，成功服务器返回用户的视频list的json的string，失败返回passworderror字段
     * 具体的是否登录成功由activity自己判断
      * @param user
     */
    public static String login(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        Response response = sendPost(URL + "/login", userJson);
        String res = null;
        try {
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * 发送删除视频的请求给服务器，成功删除服务器返回“success”，删除失败返回“error”
     * @param video
     * @return
     */
    public static String deleteVideo(Video video) {
        Gson gson = new Gson();
        String videoJson = gson.toJson(video);
        Response response = sendPost(URL + "/deletevideo", videoJson);
        String isSuccess = null;
        try {
            isSuccess = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 上传视频给服务器
     * @param filePath 视频在本地的path
     * @param videoName 视频的名称，到时候存储到服务器端数据库中
     * @return
     */
    public static String upload(String filePath, String videoName, String level, String uname) throws IOException {
        String[] str = filePath.split("/");
        String filename = str[str.length - 1];
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uname", uname)
                .addFormDataPart("video_name", videoName)
                .addFormDataPart("level", level)
                .addFormDataPart("time", "false")   // 判断是否上传了时间
                .addFormDataPart("file", filename, RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();

        Request request = new Request.Builder().url(URL + "/videoupload").post(requestBody).build();
        Response response = null;
        String res = null;
        try {
            response = client.newCall(request).execute();
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 重载，多了个时和分的变量
     * @param filePath
     * @param videoName
     * @param level
     * @param uname
     * @param hour
     * @param minute
     * @return
     * @throws IOException
     */
    public static String upload(String filePath, String videoName, String level, String uname, String hour, String minute) throws IOException {
        String[] str = filePath.split("/");
        String filename = str[str.length - 1];
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uname", uname)
                .addFormDataPart("video_name", videoName)
                .addFormDataPart("level", level)
                .addFormDataPart("time", "true")    // 判断是否上传了时间
                .addFormDataPart("hour",hour)
                .addFormDataPart("minute", minute)
                .addFormDataPart("file", filename, RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();

        Request request = new Request.Builder().url(URL + "/videoupload").post(requestBody).build();
        Response response = null;
        String res = null;
        try {
            response = client.newCall(request).execute();
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

}
