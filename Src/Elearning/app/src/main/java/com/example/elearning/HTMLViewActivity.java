package com.example.elearning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


public class HTMLViewActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_view_activity);
        // 回退按钮
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HTMLViewActivity.this.finish();
            }
        });
        // 接收数据
        Bundle bd = getIntent().getExtras();
        assert bd != null;
        String html = bd.getString("html", "无内容");
        TextView html_view = findViewById(R.id.html_view);
        findViewById(R.id.top_bar).setBackgroundColor(bd.getInt("color"));
        ((TextView)findViewById(R.id.top_bar_txt)).setText("Detail");
        ((TextView)findViewById(R.id.title)).setText(bd.getString("title", ""));
        ((TextView)findViewById(R.id.author)).setText(bd.getString("author", ""));
        ((TextView)findViewById(R.id.time)).setText(bd.getString("time", ""));
        // 渲染html
        MyImageGetter imageGetter = new MyImageGetter(html_view, HTMLViewActivity.this);
        html_view.setText(Html.fromHtml(html, imageGetter, null));
    }
}

class MyImageGetter implements Html.ImageGetter {
    private Context context;
    private TextView container;

    MyImageGetter(TextView text, Context context) {
        this.context = context;
        this.container = text;
    }

    @Override
    public Drawable getDrawable(String source) {
        final LevelListDrawable drawable = new LevelListDrawable();
        Glide.with(context).load(source).asBitmap().into(new SimpleTarget<Bitmap>() {
            // 异步下载图片完成后的回调
            @Override
            public void onResourceReady(Bitmap resource,
                                        GlideAnimation<? super Bitmap> glideAnimation) {
                if(resource != null) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                    drawable.addLevel(1, 1, bitmapDrawable); // 下载完成后通过加一个图层的方式显示图片
                    drawable.setBounds(0, 0, resource.getWidth(),resource.getHeight());
                    drawable.setLevel(1); // 显示刚加的这个图层
                    container.invalidate(); // 重绘html_textview
                    container.setText(container.getText());
                }
            }
        });
        return drawable;
    }
}