package com.example.elearning;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.elearning.entity.IconText_Item;
import com.example.elearning.util.MyToast;
import com.example.elearning.util.SuperAdapter;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CourseActivity extends AppCompatActivity {

    private final int ANNOUNCEMENTS = 0;
    private final int FILES = 1;
    private final int HOMEWORK = 2;
    private final int MEMBERS = 3;

    private ArrayList<IconText_Item> mData = null;
    private int color;
    private String course_id;
    ZLoadingDialog dialog;
    MyToast toast_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_activity);

        init();

        GridView course_info_grid = (GridView) findViewById(R.id.course_info_grid);

        mData = new ArrayList<IconText_Item>();
        mData.add(new IconText_Item(R.drawable.announcement, "Announcements"));
        mData.add(new IconText_Item(R.drawable.files, "Files"));
        mData.add(new IconText_Item(R.drawable.homework, "Homework"));
        mData.add(new IconText_Item(R.drawable.people, "Members"));

        SuperAdapter mAdapter = new SuperAdapter<IconText_Item>(mData, R.layout.icon_text_item) {
            @Override
            public void bindView(ViewHolder holder, IconText_Item obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };

        course_info_grid.setAdapter(mAdapter);
        course_info_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){ // 抉择
                    case ANNOUNCEMENTS:
                        goAnnounce();
                        break;
                    case FILES:
                        goFiles();
                        break;
                    case HOMEWORK:
                        goHomework();
                        break;
                    case MEMBERS:
                        goMembers();
                }
            }
        });
    }

    private void init(){
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        assert bd != null;
        course_id = bd.getString("course_id");
        findViewById(R.id.course_topbar).setBackgroundColor(bd.getInt("color"));
        ((TextView)findViewById(R.id.topbar_txt)).setText(bd.getString("title"));
        ((TextView)findViewById(R.id.topbar_subtxt)).setText(bd.getString("subTitle"));
        // 回退按钮
        ((Button)findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 回退时，将页面2结束
                CourseActivity.this.finish();
            }
        });
        color = bd.getInt("color");
        // 预先构造好loading窗口、Toast
        dialog = new ZLoadingDialog(CourseActivity.this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING) // 设置类型
                .setLoadingColor(color) // 颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(ContextCompat.getColor(CourseActivity.this, R.color.text_black)) // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(ContextCompat.getColor(CourseActivity.this, R.color.dirty_white)) // 设置背景色，默认白色
                .setCanceledOnTouchOutside(false);
        toast_error = new MyToast(CourseActivity.this, "Internal error.", Toast.LENGTH_SHORT);
        // 改变颜色按钮
        ((Button)findViewById(R.id.color_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(CourseActivity.this)
                        .initialColor(color)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(6)
                        .lightnessSliderOnly()
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface this_dialog, int selectedColor, Integer[] allColors) {
                                changeColor("#"+Integer.toHexString(selectedColor).substring(2), selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", null)
                        .build()
                        .show();
            }
        });
    }

    private void changeColor(final String newColor, final int color_int){
        dialog.show();
        try {
            ElearningApi.changeColor("course_"+course_id, newColor, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200 && data.equals("0")) {
                                color = color_int;
                                findViewById(R.id.course_topbar).setBackgroundColor(color_int);
                                dialog.dismiss();
                                dialog.setLoadingColor(color_int);
                                GlobalFlag.setNew_color(course_id, newColor);
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

    public void goAnnounce(){
        dialog.show();
        try {
            ElearningApi.getCourseAnnounces(course_id, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200) {
                                Intent intent = new Intent(CourseActivity.this, ListViewActivity.class);
                                Bundle bd = new Bundle();
                                bd.putInt("type", ANNOUNCEMENTS);
                                bd.putInt("color", color);
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

    public void goFiles(){
        dialog.show();
        try {
            ElearningApi.openRootFolder(course_id, false, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200) {
                                Intent intent = new Intent(CourseActivity.this, ListViewActivity.class);
                                Bundle bd = new Bundle();
                                bd.putInt("type", FILES);
                                bd.putInt("color", color);
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

    public void goHomework(){
        dialog.show();
        try {
            ElearningApi.getCourseHomework(course_id, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200) {
                                Intent intent = new Intent(CourseActivity.this, ListViewActivity.class);
                                Bundle bd = new Bundle();
                                bd.putInt("type", HOMEWORK);
                                bd.putInt("color", color);
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

    public void goMembers(){
        dialog.show();
        try {
            ElearningApi.getCourseMember(course_id, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200) {
                                Intent intent = new Intent(CourseActivity.this, ListViewActivity.class);
                                Bundle bd = new Bundle();
                                bd.putInt("type", MEMBERS);
                                bd.putInt("color", color);
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
}

