package com.example.tau.volleyprocrastrinate;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.tau.volleyprocrastrinate.MainActivity.*;


class Template extends AsyncTask<Void, Void, Boolean> {
    private Context context;

    public Template(Context context) {
        this.context = context;
    }
    public static final String EXTRA_MESSAGE = "volley.MESSAGE";
    @Override
    protected Boolean doInBackground(Void... args) {
        try {

            String stringurl ="http://www.bing.com";
            URL url = new URL(stringurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //HttpURLConnection conn = null;
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            MainActivity.result = convertStreamToString(in);
            Log.d(this.getClass().getSimpleName(),result+"****");
        }catch (Exception e) {
        }
            return true;
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

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        if (aBoolean) {
            Log.d(this.getClass().getSimpleName(),"Delegates done!");
            Intent intent = new Intent(context, DisplayDataActivity.class);
            intent.putExtra(EXTRA_MESSAGE, result);
            intent.putExtra(EXTRA_MESSAGE2, result2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(this.getClass().getSimpleName(),"Starting activity!"+result+"**********"+result2);
            context.startActivity(intent);
        }
    }
}

