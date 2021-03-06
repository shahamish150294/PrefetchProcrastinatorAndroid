package com.example.tau.volleyprocrastrinate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mcomputing.Measurements.NetworkMeter;
import com.mcomputing.Measurements.PrefetchCorrelationMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;




import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    public static final String EXTRA_MESSAGE = "volley.MESSAGE";
    public static String result;
    public static String prefetchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void getWeatherData(){

        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?zip=47408,US&appid=9ebbefc881d4c7bf3ecc57074775e3d2");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(this.getClass().getSimpleName(),result+"****");
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = convertStreamToString(in);
        }catch (Exception e){

            Log.e(this.getClass().getSimpleName(),e.getMessage()+"****");
        }
    }

    private void getPrefetchData(){
        try {

            URL url = new URL("http://api.openweathermap.org/v3/uvi/39,-86/current.json?appid=9ebbefc881d4c7bf3ecc57074775e3d2");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(conn.getInputStream());
            prefetchResult = convertStreamToString(in);

            Log.d(this.getClass().getSimpleName(),prefetchResult+"****");
        }catch (Exception e){
            Log.e(this.getClass().getSimpleName(),e.getMessage()+"****");
        }
    }

    private void updateWeatherControls()  {
        try {

            Log.d("weather update: ", result + "****");
            JSONObject jObject = new JSONObject(result);
            JSONObject mainjObject = jObject.getJSONObject("main");
            String temperature = mainjObject.getString("temp");

            double temp = Double.valueOf(temperature) - 273.15;


            TextView textView = (TextView) findViewById(R.id.lblTempValue);
            textView.setText((int)temp +" degrees");

        } catch (Exception e){
            TextView textView = (TextView) findViewById(R.id.lblTempValue);
            textView.setText("25 degrees");
            Log.e(this.getClass().getSimpleName(),e.getMessage()+"** weather update exception **");
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

        Intent intent = new Intent(this, DisplayDataActivity.class);
        intent.putExtra(EXTRA_MESSAGE, prefetchResult);

        startActivity(intent);

    }

    @Override
    public void onStop(){
        super.onStop();

        // Measurements framework: flush the vpc data
        Context context = this.getApplicationContext();
        PrefetchCorrelationMap.getInstance(context).persistData();

        // Measurements framework: update the network stats and save the data
        NetworkMeter.getInstance(context).updateDailyStatsAndSave();
    }

    @Override
    public void onStart(){
        super.onStart();

        // Measurements framework: increment the prefetch count
        Context context = this.getApplicationContext();
        String activityName = DisplayDataActivity.class.getSimpleName();
        PrefetchCorrelationMap.getInstance(context).incrementPrefetchCount(activityName);
        Log.d(this.getClass().getSimpleName(), "Fetching weather data!");
        AsyncCallWeather asyncCallWeather = new AsyncCallWeather();
        asyncCallWeather.execute();
        Log.d(this.getClass().getSimpleName(), "Fetching next activity data!");
        AsyncCallPrefetch asyncCallPrefetch = new AsyncCallPrefetch();
        asyncCallPrefetch.execute();

    }

    // Display the network data until now
    public void displayStats(View view)
    {
        Context context = this.getApplicationContext();
        String stats = NetworkMeter.getInstance(context).getStatsInText();
        Log.d("Main", stats);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Network Statistics")
                .setMessage(stats)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close
                    }
                }).show();

    }

    // Display the VPC info until now
    public void displayVpcInfo(View view)
    {
        Context context = this.getApplicationContext();
        String vpcStats = PrefetchCorrelationMap.getInstance(context).getVpcInText();
        Log.d("Main", vpcStats);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("VPC Statistics")
                .setMessage(vpcStats)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close
                    }
                }).show();

     //   Toast.makeText(context, vpcStats, Toast.LENGTH_LONG).show();
    }

    class AsyncCallWeather extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... urls) {

            getWeatherData();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {
                updateWeatherControls();
            }
        }
    }

    class AsyncCallPrefetch extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... urls) {

            getPrefetchData();
            return true;
        }
    }
}

