package com.example.elearning;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.elearning.util.MyToast;
import com.example.elearning.util.SPUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends Activity {

    private EditText id_editText;
    private EditText psw_editText;
    ZLoadingDialog dialog;
    MyToast toast_wrong_psw, toast_error, toast_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ElearningApi.init_client(); // 初始化api
        Button login_btn = findViewById(R.id.login_btn);
        id_editText = findViewById(R.id.ID_editText);
        psw_editText = findViewById(R.id.password_editText);
        id_editText.setText(SPUtils.get(LoginActivity.this, "username"));
        psw_editText.setText(SPUtils.get(LoginActivity.this, "password"));
        psw_editText.requestFocus();

        // 预先构造好loading窗口、Toast
        dialog = new ZLoadingDialog(LoginActivity.this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING) // 设置类型
                .setLoadingColor(Color.YELLOW) // 颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(getResources().getColor(R.color.loading_text)) // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(getResources().getColor(R.color.loading_background)) // 设置背景色，默认白色
                .setCanceledOnTouchOutside(false);
        toast_empty = new MyToast(LoginActivity.this, "Empty is not allowed!", Toast.LENGTH_SHORT);
        toast_wrong_psw = new MyToast(LoginActivity.this, "Wrong ID or password.", Toast.LENGTH_SHORT);
        toast_error = new MyToast(LoginActivity.this, "Disconnect.", Toast.LENGTH_SHORT);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = id_editText.getText().toString();
                final String password = psw_editText.getText().toString();
                if (username.equals("") || password.equals("")) {
                    toast_empty.show();
                    return;
                }
                if (ElearningApi.hasLocalToken(LoginActivity.this, username, password)) goMain(username, password, true); // 本地由TOKEN 则直接用
                else getToken(username, password); // 没有本地TOKEN，现在爬取一个
            }
        });
    }

    public void goMain(final String username, final String password, final boolean topLevel){ // topLevel指第一次调用，用于避免死循环
        dialog.setHintText("Loading...");
        dialog.show();
        try {
            ElearningApi.getMainData(new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if(res_code == 200) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Bundle bd = new Bundle();
                                bd.putString("data", data);
                                intent.putExtras(bd);
                                startActivity(intent);
                                finish();
                            }
                            else if(topLevel) getToken(username, password);
                        }
                    });
                }
                @Override
                public void onFailure(Call call, IOException err) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast_error.show();
                            dialog.dismiss();
                            if(topLevel) getToken(username, password);
                        }
                    });
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getToken(final String username, final String password){
        dialog.setHintText("The first login takes a few time...");
        dialog.show();
        try {
            ElearningApi.getToken(username, password, new Callback() {
                @Override
                public void onResponse(Call call, final Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res_code == 200) {
                                JSONObject jsonObject = JSON.parseObject(data);
                                if (jsonObject.getString("result").equals("0")) { // 成功获取
                                    String token = jsonObject.getString("token");
                                    ElearningApi.setTOKEN(token); // 设置TOKEN值
                                    ElearningApi.saveToken(LoginActivity.this, token, username, password); // TOKEN保存到本地
                                    dialog.dismiss();
                                    goMain(username, password, false); // 现在已经有TOKEN了，可以goMain了
                                } else {
                                    toast_wrong_psw.show();
                                    dialog.dismiss();
                                }
                            } else {
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

