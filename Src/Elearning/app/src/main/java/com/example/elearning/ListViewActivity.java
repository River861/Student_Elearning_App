package com.example.elearning;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.elearning.entity.AnnounceList_Item;
import com.example.elearning.entity.FileList_Item;
import com.example.elearning.entity.HomeworkList_Item;
import com.example.elearning.entity.MemberList_Item;
import com.example.elearning.util.MyToast;
import com.example.elearning.util.SuperAdapter;
import com.example.elearning.entity.TodoList_Item;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListViewActivity extends AppCompatActivity {

    private final int ANNOUNCEMENTS = 0;
    private final int FILES = 1;
    private final int HOMEWORK = 2;
    private final int MEMBERS = 3;
    private final int ALL_TO_DO = 4;
    private String file_save_path;

    private ListView listview;
    private TextView listview_topbar_txt;
    private int type;
    private int color;
    private String data;
    private String folder_name;
    ZLoadingDialog dialog;
    MyToast toast_error, toast_mkdir_error, toast_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_activity);

        // 回退按钮
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 回退时，将页面2结束
                ListViewActivity.this.finish();
            }
        });

        // 接收数据
        Bundle bd = getIntent().getExtras();
        assert bd != null;
        type = bd.getInt("type", 0);
        color = bd.getInt("color");
        data = bd.getString("data", "");
        folder_name = bd.getString("folder_name", "FILE");
        findViewById(R.id.top_bar).setBackgroundColor(color);

        // 预先构造好loading窗口、Toast
        dialog = new ZLoadingDialog(ListViewActivity.this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING) // 设置类型
                .setLoadingColor(color) // 颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(getResources().getColor(R.color.text_black)) // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(getResources().getColor(R.color.dirty_white)) // 设置背景色，默认白色
                .setCanceledOnTouchOutside(false);
        file_save_path = getExternalFilesDir("download").toString();
        toast_error = new MyToast(ListViewActivity.this, "DisConnect.", Toast.LENGTH_SHORT);
        toast_mkdir_error = new MyToast(ListViewActivity.this, "Mkdir error.", Toast.LENGTH_SHORT);
        toast_save = new MyToast(ListViewActivity.this, "文件保存路径: "+ file_save_path, Toast.LENGTH_LONG);
        init();
    }

    private void init() {
        listview = findViewById(R.id.listview);
        listview_topbar_txt = findViewById(R.id.top_bar_txt);

        switch(type){ // 抉择
            case ANNOUNCEMENTS:
                initAnnounces();
                break;
            case FILES:
                initFiles();
                break;
            case HOMEWORK:
                initHomework();
                break;
            case MEMBERS:
                initMembers();
                break;
            case ALL_TO_DO:
                initALLtodo();
        }
    }

    @SuppressLint("SetTextI18n")
    public void initAnnounces(){
        listview_topbar_txt.setText("Announcements");
        // 解析数据
        List<AnnounceList_Item> mData = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        for (Object item : jsonArray) {
            String title = ((JSONObject)item).getString("title");
            String time = ((JSONObject)item).getString("time");
            String author = ((JSONObject)item).getString("author");
            String content = ((JSONObject)item).getString("content");
            mData.add(new AnnounceList_Item(title,time, author, content));
        }
        //Adapter初始化
        SuperAdapter<AnnounceList_Item> myAdapter;
        myAdapter = new SuperAdapter<AnnounceList_Item>((ArrayList)mData, R.layout.announce_list_item) {
            @Override
            public void bindView(ViewHolder holder, AnnounceList_Item obj) {
                holder.setText(R.id.announce_title, obj.getTitle());
                holder.setText(R.id.announce_time, obj.getTime());
            }
        };
        listview.setAdapter(myAdapter);
        // 点进公告
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AnnounceList_Item item = (AnnounceList_Item)parent.getAdapter().getItem(position);
                Intent intent = new Intent(ListViewActivity.this, HTMLViewActivity.class); // 展示公告内容
                Bundle bd = new Bundle();
                bd.putInt("color", color);
                bd.putString("title", item.getTitle());
                bd.putString("author", item.getAuthor());
                bd.putString("time", item.getTime());
                bd.putString("html", item.getContent());
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
    }

    public void initFiles(){
        listview_topbar_txt.setText(folder_name);
        // 解析数据
        List<FileList_Item> mData = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        for (Object item : jsonArray) {
            String type = ((JSONObject)item).getString("type");
            if(type.equals("folder")){
                String name = ((JSONObject)item).getString("name");
                String files_url = ((JSONObject)item).getString("files_url");
                String folders_url = ((JSONObject)item).getString("folders_url");
                mData.add(new FileList_Item("folder", name, files_url, folders_url));
            }
            else{
                type = ((JSONObject)item).getString("type");
                String name = ((JSONObject)item).getString("name");
                int size = ((JSONObject)item).getInteger("size");
                String download_url = ((JSONObject)item).getString("download_url");
                mData.add(new FileList_Item(type, name, size, download_url));
            }
        }
        //Adapter初始化
        SuperAdapter<FileList_Item> myAdapter;
        myAdapter = new SuperAdapter<FileList_Item>((ArrayList)mData, R.layout.file_list_item) {
            @Override
            public void bindView(ViewHolder holder, FileList_Item obj) {
                holder.setImageResource(R.id.file_img, obj.getImg());
                holder.setText(R.id.file_name, obj.getName());
            }
        };
        listview.setAdapter(myAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileList_Item item = (FileList_Item)parent.getAdapter().getItem(position);
                if(item.getType().equals("folder")){
                    goIntoFolder(item.getName(), item.getFiles_url(), item.getFolders_url());
                }
                else{ // 点击文件 下载浏览
                    browseFile(item.getDownload_url(), item.getName());
                }
            }
        });
    }

    // 进入文件夹内部
    public void browseFile(String url, final String file_name){
        dialog.setHintText("Downloading...");
        dialog.show();
        toast_save.show();
        try {
            ElearningApi.downLoadFile(url, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    // 储存下载文件的目录
                    String savePath = file_save_path; // 保存路径：/storage/emulated/0/Android/data/[packageName]/files/download
                    File tempFile = new File(savePath);
                    if (!tempFile.exists()) {
                        boolean mkdir = tempFile.mkdirs();
                        if (!mkdir) toast_mkdir_error.show();
                    }
                    try {
                        is = res.body().byteStream();
                        File file = new File(savePath, file_name);
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                        dialog.dismiss();
                        // 新activity打开
                        Intent intent = new Intent(ListViewActivity.this, FileBrowser.class);
                        Bundle bd = new Bundle();
                        bd.putInt("color", color);
                        bd.putString("path", savePath + "/" + file_name);
                        bd.putString("file_name", file_name);
                        intent.putExtras(bd);
                        ListViewActivity.this.startActivity(intent);
                    } catch (Exception e) {
                        toast_error.show();
                        dialog.dismiss();
                        e.printStackTrace();
                    } finally {
                        try {
                            if (is != null) is.close();
                        } catch (IOException ignored) {}
                        try {
                            if (fos != null) fos.close();
                        } catch (IOException ignored) {}
                    }
                }
                @Override
                public void onFailure(Call call, IOException err) {
                    toast_error.show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 进入文件夹内部
    public void goIntoFolder(final String folder_name, String files_url, String folders_url){
        dialog.show();
        try {
            ElearningApi.openFolder(files_url, folders_url, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if(res_code == 200) {
                                Intent intent = new Intent(ListViewActivity.this, ListViewActivity.class); // 新开一个相同activity展示内部文件
                                Bundle bd = new Bundle();
                                bd.putInt("type", FILES);
                                bd.putInt("color", color);
                                bd.putString("folder_name", folder_name);
                                bd.putString("data", data);
                                intent.putExtras(bd);
                                dialog.dismiss();
                                startActivity(intent);
                            }
                            else{
                                toast_error.show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
                @Override
                public void onFailure(Call call, IOException err) {
                    toast_error.show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void initHomework(){
        listview_topbar_txt.setText("Homework");
        // 解析数据
        List<HomeworkList_Item> mData = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        for (Object item : jsonArray) {
            String name = ((JSONObject)item).getString("name");
            String startTime = ((JSONObject)item).getString("startTime");
            String deadline = ((JSONObject)item).getString("deadline");
            String description = ((JSONObject)item).getString("description");
            boolean finished = ((JSONObject)item).getBoolean("finished");
            boolean can_dup = ((JSONObject)item).getBoolean("can_dup");
            mData.add(new HomeworkList_Item(name, can_dup, startTime, deadline, description, finished));
        }
        //Adapter初始化
        SuperAdapter<HomeworkList_Item> myAdapter;
        myAdapter = new SuperAdapter<HomeworkList_Item>((ArrayList)mData, R.layout.homework_list_item) {
            @Override
            public void bindView(ViewHolder holder, HomeworkList_Item obj) {
                holder.setText(R.id.homework_title, obj.getName());
                holder.setText(R.id.homework_subtitle, obj.getDeadline());
                holder.setImageResource(R.id.finished, obj.getEndImg());
                holder.setImageResource(R.id.front_icon, R.drawable.homework);
            }
        };
        listview.setAdapter(myAdapter);
        // 点进作业
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeworkList_Item item = (HomeworkList_Item)parent.getAdapter().getItem(position);
                Intent intent = new Intent(ListViewActivity.this, HTMLViewActivity.class); // 展示作业内容
                Bundle bd = new Bundle();
                bd.putInt("color", color);
                bd.putString("title", item.getName());
                if(item.getCan_dup()) bd.putString("author", "Duplicate submissions: √");
                else bd.putString("author", "Duplicate submissions: ×");
                bd.putString("time", "Duration: " + item.getStartTime() + " ~ " + item.getDeadline());
                bd.putString("html", item.getContent());
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void initMembers(){
        listview_topbar_txt.setText("Members");
        // 解析数据
        List<MemberList_Item> mData = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        for (Object item : jsonArray) {
            String id = ((JSONObject)item).getString("id");
            String img_url = ((JSONObject)item).getString("img");
            String name = ((JSONObject)item).getString("name");
            String role = ((JSONObject)item).getString("role");
            String uis_id = ((JSONObject)item).getString("uis_id");
            mData.add(new MemberList_Item(id, img_url, name, role, uis_id));
        }
        //Adapter初始化
        SuperAdapter<MemberList_Item> myAdapter;
        myAdapter = new SuperAdapter<MemberList_Item>((ArrayList)mData, R.layout.member_list_item) {
            @Override
            public void bindView(ViewHolder holder, MemberList_Item obj) {
                holder.setText(R.id.people_name, obj.getName());
                if(!obj.getRole().equals("student")) holder.setText(R.id.people_role, "("+obj.getRole()+")");
                else holder.setText(R.id.people_role, "");
                holder.setText(R.id.uis_id, obj.getUis_id());
                holder.setUrlImage(R.id.avatar, obj.getAvatar_url(), ListViewActivity.this, 70, 70);
            }
        };
        listview.setAdapter(myAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void initALLtodo(){
        listview_topbar_txt.setText("To Do");
        // 解析数据
        List<TodoList_Item> mData = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        for (Object item : jsonArray) {
            String title = ((JSONObject)item).getString("title");
            String subtitle = ((JSONObject)item).getString("subtitle");
            String type = ((JSONObject)item).getString("type");
            String deadine = ((JSONObject)item).getString("deadline");
            mData.add(new TodoList_Item(title, subtitle + "   " + deadine, type));
        }
        //Adapter初始化
        SuperAdapter<TodoList_Item> myAdapter;
        myAdapter = new SuperAdapter<TodoList_Item>((ArrayList)mData, R.layout.homework_list_item) {
            @Override
            public void bindView(ViewHolder holder, TodoList_Item obj) {
                holder.setText(R.id.homework_title, obj.getTitle());
                holder.setText(R.id.homework_subtitle, obj.getSubtitle());
                holder.setImageResource(R.id.front_icon, obj.getFrontImg());
                holder.setImageResource(R.id.finished, obj.getEndImg());
            }
        };
        listview.setAdapter(myAdapter);
    }
}
