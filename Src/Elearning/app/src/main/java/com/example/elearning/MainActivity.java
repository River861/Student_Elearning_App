package com.example.elearning;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.elearning.util.MyToast;
import com.example.elearning.fragment.CalendarFragment;
import com.example.elearning.fragment.DashboardFragment;
import com.example.elearning.fragment.HomeFragment;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private DashboardFragment dashboard_fg;
    private CalendarFragment calendar_fg;
    private HomeFragment home_fg;
    private FragmentManager fManager;
    private TextView topbar_txt;
    private JSONObject mainData;
    ZLoadingDialog dialog;
    MyToast toast_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取各种数据
        mainData = JSON.parseObject(getIntent().getExtras().getString("data"));
        ElearningApi.setUserId(mainData.getString("user_id"));

        fManager = getSupportFragmentManager();
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        RadioGroup menu_bar = (RadioGroup) findViewById(R.id.menu_bar);
        menu_bar.setOnCheckedChangeListener(this);

        dialog = new ZLoadingDialog(MainActivity.this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING) // 设置类型
                .setLoadingColor(Color.YELLOW) // 颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(getResources().getColor(R.color.loading_text)) // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(getResources().getColor(R.color.loading_background)) // 设置背景色，默认白色
                .setCanceledOnTouchOutside(false);
        toast_error = new MyToast(MainActivity.this, "Disconnect.", Toast.LENGTH_SHORT);

        RadioButton dashboard_btn = (RadioButton) findViewById(R.id.dashboard_btn);
        dashboard_btn.setChecked(true); // 开始选中dashboard
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (checkedId){
            case R.id.dashboard_btn:
                topbar_txt.setText(R.string.dash_board);
                if(dashboard_fg == null){
                    dashboard_fg = new DashboardFragment();
                    Bundle bd = new Bundle();
                    bd.putString("data", mainData.getString("dash_data"));
                    dashboard_fg.setArguments(bd);
                    fTransaction.add(R.id.fragment_content, dashboard_fg);
                }else{
                    fTransaction.show(dashboard_fg);
                }
                break;
            case R.id.calendar_btn:
                topbar_txt.setText(R.string.calender);
                if(calendar_fg == null){
                    calendar_fg = new CalendarFragment();
                    Bundle bd = new Bundle();
                    bd.putString("data", mainData.getString("calendar_data"));
                    calendar_fg.setArguments(bd);
                    fTransaction.add(R.id.fragment_content, calendar_fg);
                }else{
                    fTransaction.show(calendar_fg);
                }
                break;
            case R.id.home_btn:
                topbar_txt.setText(R.string.my_home);
                if(home_fg == null){
                    home_fg = new HomeFragment();
                    Bundle bd = new Bundle();
                    bd.putString("data", mainData.getString("profile"));
                    home_fg.setArguments(bd);
                    fTransaction.add(R.id.fragment_content, home_fg);
                }else{
                    fTransaction.show(home_fg);
                }
                break;
        }
        fTransaction.commit();
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(dashboard_fg != null) fragmentTransaction.hide(dashboard_fg);
        if(calendar_fg != null) fragmentTransaction.hide(calendar_fg);
        if(home_fg != null) fragmentTransaction.hide(home_fg);
    }

}