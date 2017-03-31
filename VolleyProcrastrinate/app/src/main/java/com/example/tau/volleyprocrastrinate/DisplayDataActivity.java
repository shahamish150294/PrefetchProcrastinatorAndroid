package com.example.tau.volleyprocrastrinate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class DisplayDataActivity extends AppCompatActivity {


    private enum UVRiskRating{
        Low, Moderate, High, Very_High, Extreme

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        try {

            Intent intent = getIntent();
            String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

            JSONObject jObject = new JSONObject(message);
            String prefetchResult = jObject.getString("data");


            float value = Float.valueOf(prefetchResult);
            String riskRating = getUVRiskValue(value);

            TextView textView = (TextView) findViewById(R.id.lblRiskIndex);
            textView.setText(prefetchResult);

            TextView textView2 = (TextView) findViewById(R.id.lblRiskLevel);
            textView2.setText(riskRating);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getUVRiskValue(float value)
    {
        if (value>=0 && value <=2)
        {
            return UVRiskRating.Low.toString();
        }

        else if (value>=3 && value <=5)
        {
            return UVRiskRating.Moderate.toString();
        }

        else if (value>=6 && value <=7)
        {
            return UVRiskRating.High.toString();
        }

        else if (value>=8 && value <=10)
        {
            return UVRiskRating.Very_High.toString();
        }

        else
        {
            return UVRiskRating.Extreme.toString();
        }
    }
}
