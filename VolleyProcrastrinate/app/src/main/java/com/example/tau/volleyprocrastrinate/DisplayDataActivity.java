package com.example.tau.volleyprocrastrinate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String message2 = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.txtResult);
        textView.setText(message);

        TextView textView2 = (TextView) findViewById(R.id.txtResult2);
        textView2.setText(message2);
    }

    public void clearData(View view)
    {
        TextView textView = (TextView) findViewById(R.id.txtResult);
        textView.setText("");
    }
}
