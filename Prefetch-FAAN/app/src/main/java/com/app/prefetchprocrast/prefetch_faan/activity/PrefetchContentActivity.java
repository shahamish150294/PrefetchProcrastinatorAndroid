package com.app.prefetchprocrast.prefetch_faan.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.app.prefetchprocrast.prefetch_faan.R;
import com.jacksonandroidnetworking.JacksonParserFactory;

import java.io.File;

import okhttp3.OkHttpClient;



public class PrefetchContentActivity extends AppCompatActivity {
    private static final String CLASS_TAG = PrefetchContentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefetch_content);
        Log.d(CLASS_TAG, "Creating View!");

        AndroidNetworking.initialize(getApplicationContext());
        Log.d(CLASS_TAG, "Initialization in progress!");

        OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                .addNetworkInterceptor(new HttpLoggingInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        Log.d(CLASS_TAG, " Prefetching content from server.");
        AndroidNetworking.get(Constants.URL+"{pageName}")
                .addPathParameter("pageName", "ContentForPrefetch")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(CLASS_TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(CLASS_TAG, " bytesSent : " + bytesSent);
                        Log.d(CLASS_TAG, " bytesReceived : " + bytesReceived);
                        Log.d(CLASS_TAG, " isFromCache : " + isFromCache);
                    }

                })
                .prefetch();
    }

    /**
     * To delete cache so that there is no need to uninstall or clear cache from device manually
     * @param view
     */
    public void deleteCache(View view) {
        try {
            File dir = getApplicationContext().getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            Log.e(CLASS_TAG,"Error while deleting directory", e);
        }
        Log.d(CLASS_TAG,"Cache Deleted Successfully!");
    }

    /**
     * Depth first search to delete all the existing directories in cache
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
