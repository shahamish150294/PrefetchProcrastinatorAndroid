package com.example.tau.volleyprocrastrinate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mcomputing.procrastinate.PrefetchCorrelationMap;
import com.mcomputing.procrastinate.ViewPrefetchCorrelation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


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

    private void delegate3(String requestUrl){
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //HttpURLConnection conn = null;
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

        delegate3("google.com");

        String activityName = DisplayDataActivity.class.getSimpleName();
        PrefetchCorrelationMap.getInstance().incrementPrefetchCount(activityName);

        /*
        delegate();
        delegate2();*/
        //AsyncCalls a = new AsyncCalls();
        //a.execute(new String[]{"asdasd"});

        Intent intent = new Intent(this, DisplayDataActivity.class);
        intent.putExtra(EXTRA_MESSAGE, result);
        intent.putExtra(EXTRA_MESSAGE2, result2);
        startActivity(intent);
    }

    class AsyncCalls extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... urls) {
            //delegate2("http://www.bing.com");
            delegate3("http://www.google.com");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {
                Log.d(this.getClass().getSimpleName(),"Delegates done!");
                Intent intent = new Intent(MainActivity.this, DisplayDataActivity.class);
                intent.putExtra(EXTRA_MESSAGE, result);
                intent.putExtra(EXTRA_MESSAGE2, result2);
                Log.d(this.getClass().getSimpleName(),"Starting activity!"+result+"**********"+result2);
                startActivity(intent);
            }
        }
    }
}

