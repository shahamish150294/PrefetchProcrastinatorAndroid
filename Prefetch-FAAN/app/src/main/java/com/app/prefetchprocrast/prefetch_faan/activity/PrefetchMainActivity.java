package com.app.prefetchprocrast.prefetch_faan.activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.prefetchprocrast.prefetch_faan.R;

public class PrefetchMainActivity extends AppCompatActivity {


    private static final String CLASS_TAG = PrefetchContentActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefetch_main);
    }

    /**
     * Open Next Activity that prefetches content
     * @param view Current View is passed as argument
     */
    public void openNextActivity(View view) {
        Intent intent = new Intent(this, PrefetchContentActivity.class);
        Log.d(this.getLocalClassName(), "Starting new activity!");
        startActivity(intent);

    }

}


