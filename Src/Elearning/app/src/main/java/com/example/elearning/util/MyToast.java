package com.example.elearning.util;

import com.example.elearning.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MyToast {

    private Toast toast;

    public MyToast(Context context, String text, int duration){
        toast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mytoast, (ViewGroup) ((Activity)context).findViewById(R.id.my_toast));
        TextView toast_msg = view.findViewById(R.id.toast_msg);
        toast_msg.setText(text);
        toast.setDuration(duration);
        toast.setView(view);
    }

    public void show(){
        toast.show();
    }

}
