package com.example.elearning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class TestApi{
    @Test
    public void main() throws IOException, JSONException {
        ElearningAPI api = new ElearningAPI("CLBXC06LAO7y82M3xDEsaIjjzlGCCYXdg8yx4oZVxDuZw8YjoNvhODyhw9tGonJf"); // "m4zH9PUh0oyt5B4bsd7qoizy76yBt7oZdIZt7kdRJ1F0LyBiWwqiPuD9BgkascGL"
        api.personal_info();

        //api.courses_info();
        //api.announces_info("22466", "2020-01-01", "2020-03-18");
        //api.homework_info("22466");
        //api.student_list("17559");

        //api.get_calendar();
        //api.add_calendar("DO PJ", "many many many many Pjs", "2020-03-20");
        //api.del_calendar("3525");
        //api.modify_calendar("3526", "Do PJs", "many many many many many many...");

        //api.activity_stream();
    }
}


class ElearningAPI {
    private final String ELEARNING = "https://elearning.fudan.edu.cn";
    private final String CLIENT_ID = "10000000000003";
    private final String CLIENT_SECRET = "vkt97LBOMSN3tiySgJdAaIfegYdyY6akKjKs9XnsSQYuiat35sSZ6tRARRmp9Wjn";
    private String TOKEN;
    private String user_id;
    OkHttpClient client;


    @NotNull
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }
        return sb.toString();
    }
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) sb.append('\t');
    }
    public static void printJson(String jsonStr) {
        System.out.println(formatJson(jsonStr));
    }

    ElearningAPI(String TOKEN) throws IOException, JSONException {
        this.TOKEN = TOKEN;
        client = new OkHttpClient();
        //get_token();
        personal_info();
    }

    protected void get_token() throws IOException {
        HttpUrl url = HttpUrl.parse(ELEARNING + "/login/oauth2/auth")
                .newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
                .build();

        System.out.println(url);

        Request req = new Request.Builder()
                .url(url)
                .build();

        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }


    /**
     * 个人相关
     */
    // 列出个人信息
    protected void personal_info() throws IOException, JSONException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/users/self")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());

        System.out.println(res.body().string());
    }

    // 所有课程流
    protected void activity_stream() throws IOException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/users/self/activity_stream")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // 即将发生的事件
    protected void up_coming() throws IOException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/users/self/upcoming_events")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // TODO 个人数据、头像

    // 个人空间 1G TODO 递归、新建、上传、下载、删除
    protected void personal_file() throws IOException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/users/self/folders/root")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }


    /**
     *  课程相关  */
    // 列出当前课程信息列表
    protected void courses_info() throws IOException {
        Request req = new Request.Builder()
                .url(HttpUrl.parse(ELEARNING + "/api/v1/courses")
                        .newBuilder()
                        .addQueryParameter("enrollment_state", "active") //active, invited_or_pending, completed
                        .addQueryParameter("include[]", "public_description")
                        .addQueryParameter("include[]", "total_scores")
                        .addQueryParameter("include[]", "favorites")
                        .addQueryParameter("include[]", "teachers")
                        .addQueryParameter("include[]", "term")
                        .addQueryParameter("state[]", "unpublished")
                        .addQueryParameter("state[]", "available")
                        .build())
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // 列出指定课程的公告
    protected void announces_info(String course, String start_day, String end_day) throws IOException {
        Request req = new Request.Builder()
                .url(HttpUrl.parse(ELEARNING + "/api/v1/announcements")
                        .newBuilder()
                        .addQueryParameter("context_codes" , "course_"+course)
                        .addQueryParameter("start_date", start_day)
                        .addQueryParameter("end_date", end_day)
                        //.addQueryParameter("active_only", "false")
                        .build())
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());
        printJson("返回内容："+res.body().string());
    }

    // 获取某课程文件夹根目录 TODO 从folders_url、files_url逐层树状获取文件
    protected void rool_folder(String course) throws IOException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/courses/"+course+"/folders/root")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // 列出某课程的所有作业
    protected void homework_info(String course) throws IOException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/courses/"+course+"/assignments")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // 列出某课程的学生
    protected  void student_list(String course) throws IOException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/courses/"+course+"/students")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // TODO 课程昵称、颜色


    /**
     *  日历相关  */
    // 获取日历 （个人） TODO 所有日历
    protected void get_calendar() throws IOException {
        Request req = new Request.Builder()
                .url(HttpUrl.parse(ELEARNING + "/api/v1/calendar_events")
                        .newBuilder()
                        //.addQueryParameter("type", "assignment")
                        .addQueryParameter("all_events", "true")
                        .addQueryParameter("context_codes[]", "user_"+user_id)
                        .build())
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();

        Response res = client.newCall(req).execute();

        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // 添加日历事件 （个人）
    protected void add_calendar(String title, String description, String end_at) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("calendar_event[context_code]", "user_"+user_id)
                .add("calendar_event[title]", title)
                .add("calendar_event[description]", description)
                .add("calendar_event[end_at]", end_at)
                .build();

        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/calendar_events.json")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .post(body)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // 删除日历事件 （个人）
    protected void del_calendar(String cid) throws IOException {
        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/calendar_events/"+cid+".json")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .delete()
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }

    // 修改日历事件 (个人)
    protected void modify_calendar(String cid, String title, String description, String end_at) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("calendar_event[context_code]", "user_"+user_id)
                .add("calendar_event[title]", title)
                .add("calendar_event[description]", description)
                .build();

        Request req = new Request.Builder()
                .url(ELEARNING + "/api/v1/calendar_events/"+cid+".json")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .put(body)
                .build();

        //同步请求
        Response res = client.newCall(req).execute();
        System.out.println("返回码："+res.code());
        printJson(res.body().string());
    }


    /**
     *  会话相关  */
//    // 发送消息 （个人）
//    protected void send_message(String receiver_id, String subject, String msg) throws IOException {
//        RequestBody body = new FormBody.Builder()
//                .add("recipients[]", receiver_id)
//                .add("subject", subject)
//                .add("body", msg)
//                .add("user_note", "false")
//                .build();
//
//        Request req = new Request.Builder()
//                .url(ELEARNING + "/api/v1/conversations")
//                .addHeader("Authorization", "Bearer " + TOKEN)
//                .post(body)
//                .build();
//
//        //同步请求
//        Response res = client.newCall(req).execute();
//        System.out.println("返回码："+res.code());
//        printJson(res.body().string());
//    }
//
//    // 查看消息 TODO 待测试
//    protected void get_message() throws IOException {
//        Request req = new Request.Builder()
//                .url(ELEARNING + "/api/v1/conversations")
//                .addHeader("Authorization", "Bearer " + TOKEN)
//                .build();
//
//        //同步请求
//        Response res = client.newCall(req).execute();
//        System.out.println("返回码："+res.code());
//        printJson(res.body().string());
//    }


}
