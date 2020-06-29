package com.example.elearning;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException, JSONException, InterruptedException {
        //get_test1();
        //get_test2();
        //post_normal();
        //post_json();
        //post_file();
        //download_file();
        //Thread.sleep(5000);
        getToken();
    }

    private void getToken() throws IOException {
        OkHttpClient client = new OkHttpClient();

        //application/x-www-form-urlencoded 数据是个普通表单
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", "8a06df56f4b1c8453c6eb0366062a734272469bf4a026217a5957dc2512e428a342d25a7faa46973dfe5ca0ca18f436f59abbf2ef976a53d053cf71d65e4a477")
                .add("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
                .add("client_id", "001")
                .build();

        //header方法是覆盖，addHeader才是添加
        Request req = new Request.Builder()
                .url("https://elearning.fudan.edu.cn/login/oauth2/token")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
                //.addHeader("键", "值")
                .post(body)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        System.out.println("Auth: "+res.header("WWW-Authenticate"));
        System.out.println(res.body().string());
    }

    // 同步请求
    private void get_test1() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url("https://www.baidu.com/")
                .build();
        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());
        System.out.println("返回内容："+res.body().string());
    }

    // 异步请求
    private void get_test2(){
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url("https://www.baidu.com/")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
                //.......
                .build();
        //异步请求
        client.newCall(req).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                //请求成功会执行
                System.out.println("返回码："+arg1.code());
                System.out.println(arg1.body().string());
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                //请求失败或网络错误会执行这里
                System.out.println("请求失败");
            }
        });
    }

    private void post_normal() throws IOException {
        OkHttpClient client = new OkHttpClient();

        //application/x-www-form-urlencoded 数据是个普通表单
        //multipart/form-data 数据里有文件
        //application/json 数据是json
        RequestBody body = new FormBody.Builder()
                .add("键", "值")
                //例如 .add("user","123456")
                //     .add("passwd","abc1234")
                .build();

        //header方法是覆盖，addHeader才是添加
        Request req = new Request.Builder()
                .url("https://www.baidu.com/")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
                //.addHeader("键", "值")
                .post(body)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        System.out.println(res.body().toString());
    }

    private void post_json() throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();

        //String转RequestBody String、ByteArray、ByteString都可以用toRequestBody()
        MediaType TYPE = MediaType.Companion.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject()
                .put("user", "123456");
        RequestBody body = RequestBody.Companion.create(json.toString(), TYPE);
        Request req = new Request.Builder()
                .url("https://www.baidu.com/")
                .post(body)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        System.out.println(res.body().toString());
    }

    private void post_file() throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();

        File file = new File("C:\\Users\\River\\Pictures\\a.jpg");
        //MultipartBody也是继承了RequestBody
        /*源码可知它适用于这五种Content-Type:
          public static final MediaType MIXED = MediaType.parse("multipart/mixed");
          public static final MediaType ALTERNATIVE = MediaType.parse("multipart/alternative");
          public static final MediaType DIGEST = MediaType.parse("multipart/digest");
          public static final MediaType PARALLEL = MediaType.parse("multipart/parallel");
          public static final MediaType FORM = MediaType.parse("multipart/form-data");
         */
        MediaType TYPE = MediaType.Companion.parse("text/x-markdown; charset=utf-8");

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.Companion.create(file, TYPE))
                .build();

        Request req = new Request.Builder()
                .url("https://www.baidu.com/")
                .post(body)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        System.out.println(res.body().toString());
    }

    // 异步下载
    private void download_file() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513db777cf78376d55fbb3fbd9b3.jpg";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                System.out.println("大小:"+arg1.body().contentLength()/1024+"kb");
                InputStream is = arg1.body().byteStream();

                byte[] buf = new byte[2048];

                File file = new File("C:\\Users\\River\\Pictures\\z.jpg");
                if(file.exists()) file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);

                int len = 0;
                while((len = is.read(buf)) != -1) {
                    fo.write(buf,0, len);
                }
                fo.flush();
                if(is != null) is.close();
                if(fo != null) fo.close();
                System.out.println("下载完成");
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
            }

        });
    }

}