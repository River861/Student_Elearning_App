package com.example.elearning;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;


public class AppEnvir extends Application {
    private final String TAG = "APP-ENVIR";

    @Override
    public void onCreate() {
        super.onCreate();

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核

        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, "===onCoreInitFinished===");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e(TAG,  "x5初始化结果: " + b);
            }
        });

    }
}