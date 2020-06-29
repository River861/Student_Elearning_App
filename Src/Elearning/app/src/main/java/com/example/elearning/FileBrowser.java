package com.example.elearning;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elearning.util.MyToast;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class FileBrowser extends AppCompatActivity implements TbsReaderView.ReaderCallback {

    private TbsReaderView mTbsReaderView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_browser_activity);

//        // 测试x5内核
//        WebView webView = new WebView(this);
//        webView.setWebViewClient(new WebViewClient() {
//            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
//        try{
//            webView.loadUrl("http://debugtbs.qq.com");          //调用loadUrl方法为WebView加入链接
//            setContentView(webView);                           //调用Activity提供的setContentView将webView显示出来
//        }catch (Exception e){
//            Log.d("APP-ENVIR", e.toString());
//        }

        // 回退按钮
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileBrowser.this.finish();
            }
        });
        // 接收数据
        Bundle bd = getIntent().getExtras();
        assert bd != null;
        findViewById(R.id.top_bar).setBackgroundColor(bd.getInt("color"));
        ((TextView)findViewById(R.id.top_bar_txt)).setText("File Browser");
        LinearLayout tbsView = findViewById(R.id.file_view);
        mTbsReaderView = new TbsReaderView(this, this);
        tbsView.addView(mTbsReaderView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        Bundle bundle = new Bundle();
        bundle.putString("filePath", bd.getString("path"));
        bundle.putString("tempPath", getExternalCacheDir().toString());
        boolean result;
        if(bd.containsKey("file_name")) result = mTbsReaderView.preOpen(getFileType(bd.getString("file_name")), false);
        else result = false;
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
        else{
            new MyToast(FileBrowser.this, "Unsupported system. ", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileType(String filename) {
        String str = "";
        int idx = filename.lastIndexOf('.');
        if (idx <= -1) return str;
        str = filename.substring(idx + 1);
        return str;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTbsReaderView.onStop();
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
    }
}
