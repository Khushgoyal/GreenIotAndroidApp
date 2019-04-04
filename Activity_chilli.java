package com.example.urban_computing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_chilli extends AppCompatActivity
{
    private Button button1;
    private Button button2;
    private Button button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chilli);
        button1= findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openlight_se_chilli();
            }
        });

        button2= findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhumid_se_chilli();
            }
        });

        button3= findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weather();
            }
        });

    }
    public void openlight_se_chilli()
    {
        Intent intent = new Intent(this, light_se_chilli.class);
        startActivity(intent);
    }
    public void openhumid_se_chilli()
    {
        Intent intent = new Intent(this, moisture_se_chilli.class);
        startActivity(intent);
    }

    public void weather()
    {
        Intent intent = new Intent(this, weather.class);
        startActivity(intent);
    }
}
