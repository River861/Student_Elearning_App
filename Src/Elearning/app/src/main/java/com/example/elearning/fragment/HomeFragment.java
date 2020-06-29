package com.example.elearning.fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.elearning.ElearningApi;
import com.example.elearning.ListViewActivity;
import com.example.elearning.LoginActivity;
import com.example.elearning.R;
import com.example.elearning.entity.IconText_Item;
import com.example.elearning.util.MyToast;
import com.example.elearning.util.SuperAdapter;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class HomeFragment extends Fragment {

    private final int ALL_TO_DO = 4;
    private final int FILES = 1;

    private String avatar_url;
    private String name;
    private String student_id;
    private String user_id;
    private int color;

    private View mView;
    private Context mContent;
    private ArrayList<IconText_Item> mData = null;

    private ZLoadingDialog dialog;
    private MyToast toast_error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.home_fragment,container,false);
        mContent = getActivity();
        // 解析数据
        Bundle bd = getArguments();
        assert bd != null;
        JSONObject profile_date = JSON.parseObject(bd.getString("data"));
        avatar_url = profile_date.getString("avatar");
        name = profile_date.getString("name");
        student_id = profile_date.getString("student_id");
        user_id = profile_date.getString("user_id");
        color = Color.parseColor(profile_date.getString("color"));
        // 初始化部件
        init();
        GridView personal_info_grid = (GridView) mView.findViewById(R.id.personal_info_grid);
        mData = new ArrayList<IconText_Item>();
        mData.add(new IconText_Item(R.drawable.homework, "All-to-do"));
        mData.add(new IconText_Item(R.drawable.files, "Files"));
        SuperAdapter mAdapter = new SuperAdapter<IconText_Item>(mData, R.layout.icon_text_item) {
            @Override
            public void bindView(ViewHolder holder, IconText_Item obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };

        personal_info_grid.setAdapter(mAdapter);
        personal_info_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0: // 第一个位置是ALL_TO_DO
                        goAllTasks();
                        break;
                    case FILES:
                        goFiles();
                }
            }
        });
        return mView;
    }

    private void init(){
        // 加载文字、颜色、头像
        ((TextView) mView.findViewById(R.id.my_name)).setText(name);
        ((TextView) mView.findViewById(R.id.my_id)).setText(student_id);
        mView.findViewById(R.id.home_topbar).setBackgroundColor(color);
        ImageView avatar = mView.findViewById(R.id.my_avatar);
        Glide.with(mContent).load(avatar_url)
                .asBitmap()
                .override(160, 160)
                .into(avatar);

        // 预先构造好loading窗口、Toast
        dialog = new ZLoadingDialog(mContent);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING) // 设置类型
                .setLoadingColor(color) // 颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(getResources().getColor(R.color.text_black)) // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(getResources().getColor(R.color.dirty_white)) // 设置背景色，默认白色
                .setCanceledOnTouchOutside(false);
        toast_error = new MyToast(mContent, "Internal error.", Toast.LENGTH_SHORT);
        // 改变颜色按钮
        ((Button)mView.findViewById(R.id.color_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(mContent)
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
        // logout按钮
        ((Button)mView.findViewById(R.id.logout_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        // clear按钮
        ((Button)mView.findViewById(R.id.clear_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file_path = mContent.getExternalFilesDir("download").toString();
                String cache_size = clearDir(file_path);
                new MyToast(mContent, "Clear done! "+cache_size+" M cache is released.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String clearDir(String path) {
        File dirFile = new File(path);
        File[] files = dirFile.listFiles();
        assert files != null;
        double res = 0;
        for (File file : files) { //遍历删除文件夹下的所有文件(无子目录)
            try{
                FileInputStream fis = new FileInputStream(file);
                int file_size = fis.available();
                if (file.isFile() && file.delete()) res += (double)file_size / (1024*1024);
            }
            catch (Exception ignored){}
        }
        String res_str = new DecimalFormat("#.0").format(res);
        if (res < 1) return "0" + res_str;
        return res_str;
    }

    private void changeColor(String newColor, final int color_int){
        dialog.show();
        try {
            ElearningApi.changeColor("user_"+user_id, newColor, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200 && data.equals("0")) {
                                color = color_int;
                                mView.findViewById(R.id.home_topbar).setBackgroundColor(color_int);
                                dialog.dismiss();
                                dialog.setLoadingColor(color_int);
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

    private void goFiles(){
        dialog.show();
        try {
            ElearningApi.openRootFolder(user_id, true, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200) {
                                Intent intent = new Intent(mContent, ListViewActivity.class);
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

    private void goAllTasks(){
        dialog.show();
        try {
            ElearningApi.getAllTasks(new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200) {
                                Intent intent = new Intent(mContent, ListViewActivity.class);
                                Bundle bd = new Bundle();
                                bd.putInt("type", ALL_TO_DO);
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

