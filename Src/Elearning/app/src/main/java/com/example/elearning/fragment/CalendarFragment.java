package com.example.elearning.fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.elearning.ElearningApi;
import com.example.elearning.R;
import com.example.elearning.util.MyToast;
import com.example.elearning.util.SuperAdapter;
import com.example.elearning.entity.TaskList_Item;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CalendarFragment extends Fragment {

    private Context mContext;
    private static final String[] Month = {"", "Jan  ", "Feb  ", "Mar  ", "Apr  ", "May  ", "Jun  ", "Jul  ", "Aug  ", "Sept  ", "Oct  ", "Nov  ", "Dec  "};
    private CalendarLayout calendarLayout;
    private CalendarView calendarView;
    private View mView;
    private ListView listView;
    private JSONObject dateColor;
    private int curDay;
    private int curMonth;
    private int curYear;
    private TextView selectMonth_view;
    private ZLoadingDialog dialog;
    private MyToast toast_error;
    private AlertDialog addTask_dialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.calendar_fragment, container,false);
        mContext = getActivity();
        calendarLayout = mView.findViewById(R.id.calendarLayout);
        calendarView = mView.findViewById(R.id.calendarView);
        listView = mView.findViewById(R.id.task_list);
        // 今天日期
        curDay = calendarView.getCurDay();
        curMonth = calendarView.getCurMonth();
        curYear = calendarView.getCurYear();
        // 初始化部件
        init_views();
        // 初始化对话框
        init_addTask_dialog();
        // 解析数据
        Bundle bundle = getArguments();
        assert bundle != null;
        String color_data = bundle.getString("data");
        dateColor = JSON.parseObject(color_data);
        // 渲染日历
        init_scheme();
        // 渲染任务列表
        goTask(calendarView.getSelectedCalendar().toString());
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {}
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                if(isClick){
                    goTask(calendar.toString());
                }
            }
        });

        return mView;
    }

    private void init_views(){
        // 年月显示（月份切换改变）
        selectMonth_view = mView.findViewById(R.id.curMonth_txt);
        selectMonth_view.setText(String.valueOf(curYear).concat("  ").concat(Month[curMonth]));
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                selectMonth_view.setText(String.valueOf(year).concat("  ").concat(Month[month]));
            }
        });
        // switch按钮
        Button switch_btn = mView.findViewById(R.id.switch_btn);
        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendarLayout.isExpand()) calendarLayout.shrink();
                else calendarLayout.expand();
            }
        });
        // add按钮
        Button add_btn = mView.findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(beforeToday(calendarView.getSelectedCalendar())) return; // 过去的日期不能新增任务
                addNewTask();
            }
        });
        // locate按钮
        Button locate_btn = mView.findViewById(R.id.locate_btn);
        locate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollToCalendar(curYear, curMonth, curDay); // 滚动到指定日期
                calendarLayout.shrink(); // 收缩视图
                goTask(calendarView.getSelectedCalendar().toString()); // 加载今天的任务列表
            }
        });
        // 预先构造好loading窗口、Toast、add_dialog
        dialog = new ZLoadingDialog(mContext);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING) // 设置类型
                .setLoadingColor(Color.YELLOW) // 颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(getResources().getColor(R.color.loading_text)) // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(getResources().getColor(R.color.loading_background)) // 设置背景色，默认白色
                .setCanceledOnTouchOutside(false);
        toast_error = new MyToast(mContext, "Disconnect.", Toast.LENGTH_SHORT);
    }

    private void init_addTask_dialog(){
        // 构造新增任务的对话框
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(mContext);
        final LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        @SuppressLint("InflateParams")
        final View view_custom = inflater.inflate(R.layout.text_edit_dialog, null,false);
        final EditText editText = view_custom.findViewById(R.id.editText);
        dialog_builder.setView(view_custom);
        dialog_builder.setCancelable(false);
        addTask_dialog = dialog_builder.create();

        view_custom.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask_dialog.dismiss();
            }
        });

        view_custom.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                String date = calendarView.getSelectedCalendar().toString();
                submitTask(content, date, editText);
            }
        });
    }

    private void init_scheme(){
        // 初始化标记信息到日历
        Map<String, Calendar> map = new HashMap<>();
        for (Map.Entry entry : dateColor.entrySet()) {
            String[] date = ((String) entry.getKey()).split("-", 3);
            String state = (String) entry.getValue();
            if(state.equals("today")) continue;
            Calendar calendar = getSchemeCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), state);
            map.put(calendar.toString(), calendar);
        }
        calendarView.setSchemeDate(map);
    }

    private void init_taskList(String data){
        List<TaskList_Item> mData = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(data);
        for (Object item : jsonArray) {
            String content = ((JSONObject)item).getString("content");
            boolean is_finished = ((JSONObject)item).getBoolean("is_finished");
            int task_id = ((JSONObject)item).getInteger("task_id");
            mData.add(new TaskList_Item(content, is_finished, task_id));
        }
        // 日历颜色刷新
        Calendar calendar = calendarView.getSelectedCalendar();
        if(mData.size() == 0) {
            // 设置默认项(空项) 避免calendarView中的bug出现
            calendarView.removeSchemeDate(calendar);
            mData.add(new TaskList_Item());
            SuperAdapter<TaskList_Item> myAdapter;
            myAdapter = new SuperAdapter<TaskList_Item>((ArrayList) mData, R.layout.no_task_item) {
                @Override
                public void bindView(ViewHolder holder, final TaskList_Item obj) {}
            };
            listView.setAdapter(myAdapter);
        }
        else {
            if (afterToday(calendar)) {
                calendar.setScheme("future");
                calendarView.addSchemeDate(calendar);
            }
            final boolean is_before = beforeToday(calendarView.getSelectedCalendar());
            // Adapter初始化
            SuperAdapter<TaskList_Item> myAdapter;
            myAdapter = new SuperAdapter<TaskList_Item>((ArrayList) mData, R.layout.task_list_item) {
                @Override
                public void bindView(ViewHolder holder, final TaskList_Item obj) {
                    holder.setText(R.id.task_content, obj.getContent());
                    holder.setImageResource(R.id.task_finished, obj.getEndImg());
                    if (is_before) {
                        holder.setVisibility(R.id.delete_btn, View.GONE);
                        holder.setVisibility(R.id.finish_btn, View.GONE);
                    } else {
                        holder.setOnClickListener(R.id.delete_btn, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteTask(obj.getTask_id());
                            }
                        });
                        if (obj.getHas_done()) holder.setVisibility(R.id.finish_btn, View.GONE);
                        else holder.setOnClickListener(R.id.finish_btn, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finishTask(obj.getTask_id());
                            }
                        });
                    }
                }
            };
            listView.setAdapter(myAdapter);
        }
    }

    private void addNewTask(){
        addTask_dialog.show();
    }

    private void goTask(String date){
        // 放入点击事件中 获取当天的任务
        date = parseDate(date);
        dialog.setHintText("Loading...");
        dialog.show();
        try {
            ElearningApi.getTaskList(date, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200) {
                                init_taskList(data);
                                dialog.dismiss();
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

    private void submitTask(String content, String date, final EditText editText){
        // 放入点击事件中 获取当天的任务
        date = parseDate(date);
        dialog.setHintText("Submitting...");
        dialog.show();
        try {
            ElearningApi.postNewTask(date, content, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200 && data.equals("0")){ // 成功添加任务
                                editText.setText("");
                                addTask_dialog.dismiss();
                                dialog.dismiss();
                                goTask(calendarView.getSelectedCalendar().toString());
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

    private void deleteTask(int task_id){
        // 放入任务条中 删除该任务
        dialog.setHintText("Deleting...");
        dialog.show();
        try {
            ElearningApi.deleteTask(task_id, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200 && data.equals("0")){ // 成功删除任务
                                dialog.dismiss();
                                goTask(calendarView.getSelectedCalendar().toString());
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

    private void finishTask(int task_id){
        // 放入任务条中 更新该任务为完成状态
        dialog.setHintText("Updating...");
        dialog.show();
        try {
            ElearningApi.finishTask(task_id, new Callback() {
                @Override
                public void onResponse(Call call, Response res) throws IOException {
                    final int res_code = res.code();
                    final String data = res.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res_code == 200 && data.equals("0")){ // 成功更新任务
                                dialog.dismiss();
                                goTask(calendarView.getSelectedCalendar().toString());
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

    private boolean beforeToday(Calendar s){
        int sYear = s.getYear(), sMonth = s.getMonth(), sDay = s.getDay();
        return (sYear < curYear
                || sYear == curYear && sMonth < curMonth
                || sYear == curYear && sMonth == curMonth && sDay < curDay);
    }

    private boolean afterToday(Calendar s){
        int sYear = s.getYear(), sMonth = s.getMonth(), sDay = s.getDay();
        return (sYear > curYear
                || sYear == curYear && sMonth > curMonth
                || sYear == curYear && sMonth == curMonth && sDay > curDay);
    }

    private String parseDate(String date){
        return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setScheme(text);
        return calendar;
    }
}