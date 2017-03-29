package com.example.tau.volleyprocrastrinate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;


import com.mcomputing.procrastinate.PrefetchCorrelationMap;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static final String EXTRA_MESSAGE = "volley.MESSAGE";
    public static final String EXTRA_MESSAGE2 = "volley.MESSAGE2";
    public String result, result2;

    public void Procrastinate(View view)
    {
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT);
    }

    private void delegate3(){
        try {

            URL url = new URL("http://www.google.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = convertStreamToString(in);
            Log.d(this.getClass().getSimpleName(),result+"****");
        }catch (Exception e){

        }

    }

    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public void sendMessage(View view) {

        String activityName = DisplayDataActivity.class.getSimpleName();
        PrefetchCorrelationMap.getInstance().incrementPrefetchCount(activityName);
        AsyncCalls a = new AsyncCalls();
        a.execute();
        Intent intent = new Intent(this, DisplayDataActivity.class);
        intent.putExtra(EXTRA_MESSAGE, result);
        intent.putExtra(EXTRA_MESSAGE2, result2);
        startActivity(intent);
    }

    class AsyncCalls extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... urls) {

            delegate3();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {

            }
        }
    }
}

