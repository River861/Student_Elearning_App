package com.example.elearning.fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.elearning.GlobalFlag;
import com.example.elearning.R;
import com.example.elearning.entity.Dashboard_Item;
import com.example.elearning.util.SuperAdapter;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Map;


public class DashboardFragment extends Fragment {

    private GridView grid_dashboard;
    private ArrayList<Dashboard_Item> mData = null;
    private JSONObject courses_data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container,false);
        grid_dashboard = view.findViewById(R.id.grid_dashboard);

        // 解析数据
        Bundle bundle = getArguments();
        assert bundle != null;
        String course_data = bundle.getString("data");
        mData = new ArrayList<>();
        courses_data = JSON.parseObject(course_data);
        initCourseCard();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GlobalFlag.getNew_color() != null) {
            String course_id = GlobalFlag.getLast_course_id();
            String newColor = GlobalFlag.getNew_color();
            JSONObject detail = (JSONObject) courses_data.get(course_id);
            assert detail != null;
            detail.put("course_color", newColor);
            courses_data.put(course_id, detail);
            GlobalFlag.setNew_color(null, null);
            initCourseCard();
        }
    }

    private void initCourseCard(){
        mData.clear();
        for (Map.Entry entry : courses_data.entrySet()) {
            String course_id = (String) entry.getKey();
            JSONObject detail = (JSONObject) entry.getValue();
            String course_name = detail.getString("course_name");
            String course_subname = detail.getString("course_subname");
            String course_color = detail.getString("course_color");
            mData.add(new Dashboard_Item(course_id, course_name, course_subname, course_color));
        }
        SuperAdapter mAdapter = new SuperAdapter<Dashboard_Item>(mData, R.layout.dashboard_item) {
            @Override
            public void bindView(ViewHolder holder, Dashboard_Item obj) {
                holder.setBtn(R.id.myBtn, obj.getTitle(), obj.getColor(), obj.getSubTitle(), obj.getCourse_id());
            }
        };
        grid_dashboard.setAdapter(mAdapter);
    }
}