package com.example.urban_computing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity3 extends AppCompatActivity
{
    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stawberry);
        button1= findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openlight_se();
            }
        });

        button2= findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openhumid_se();
            }
        });

    }
    public void openlight_se()
    {
        Intent intent = new Intent(this, light_se.class);
        startActivity(intent);
    }
    public void openhumid_se()
    {
        Intent intent = new Intent(this, moisture_se.class);
        startActivity(intent);
    }
}
