package com.example.elearning;

import android.content.Context;

import com.example.elearning.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ElearningApi {

    private static final String MyBackend = "http://www.river.ac.cn:5555";
    private static MediaType TYPE = MediaType.Companion.parse("application/json; charset=utf-8");
    private static String TOKEN;
    private static String USER_ID;
    private static OkHttpClient client, temp_client;

    static void init_client(){
        temp_client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .build();
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    // 检查本地是否有该用户TOKEN, 暂不检测TOKEN是否有效
    static Boolean hasLocalToken(Context context, String username, String password){
        if(SPUtils.get(context, "username").equals(username)
                && SPUtils.get(context, "password").equals(password)
                && SPUtils.contains(context, "token")){
            TOKEN = SPUtils.get(context, "token");
            return true;
        }
        return false;
    }

    static void saveToken(Context context, String TOKEN, String username, String password){
        SPUtils.put(context, "username", username);
        SPUtils.put(context, "password", password);
        SPUtils.put(context, "token", TOKEN);
    }

    static void setTOKEN(String token){
        TOKEN = token;
    }

    static void setUserId(String user_id){
        USER_ID = user_id;
    }

    /* 获取TOKEN */
    static void getToken(String username, String password, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("username", username)
                .put("password", password);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/getElearningToken")
                .post(body)
                .build();
        //异步请求
        temp_client.newCall(request).enqueue(callback);
    }

    static void getMainData(Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/getMainData")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }


    static void getCourseAnnounces(String course_id, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("course_id", course_id);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/getCourseAnnounces")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    public static void openRootFolder(String id, boolean is_user, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("is_root", true)
                .put("is_user", is_user)
                .put("id", id);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/openFolder")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    static void openFolder(String files_url, String folders_url, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("is_root", false)
                .put("files_url", files_url)
                .put("folders_url", folders_url);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/openFolder")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    static void downLoadFile(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .build();
        //异步请求
        temp_client.newCall(request).enqueue(callback);
    }

    static void getCourseHomework(String course_id, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("course_id", course_id);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/getCourseHomework")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    static void getCourseMember(String course_id, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("course_id", course_id);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/getCourseMember")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    public static void getTaskList(String date, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("date", date);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/getTaskList")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    public static void postNewTask(String date, String content, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("user_id", USER_ID)
                .put("date", date)
                .put("content", content);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/postNewTask")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    public static void deleteTask(int task_id, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("task_id", task_id);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/deleteTask")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    public static void finishTask(int task_id, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("task_id", task_id);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/finishTask")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    public static void changeColor(String course_id, String color, Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN)
                .put("course_id", course_id)
                .put("color", color);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/changeColor")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

    public static void getAllTasks(Callback callback) throws JSONException {
        JSONObject json = new JSONObject()
                .put("token", TOKEN);
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request request = new Request.Builder()
                .url(MyBackend + "/getAllTasks")
                .post(body)
                .build();
        //异步请求
        client.newCall(request).enqueue(callback);
    }

}

