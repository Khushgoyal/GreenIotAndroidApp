package com.example.urban_computing;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

public class Activity2 extends AppCompatActivity
{
    private Button button1;
    private Button button2;
    private Button button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        button1= findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitystawberry();
            }
        });

        button2= findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity_tomato();
            }
        });

        button3= findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity_chilli();
            }
        });

    }
    //Green House
    public void openActivitystawberry()
    {
        Intent intent = new Intent(this, Activity3.class);
        startActivity(intent);
    }

    public void openActivity_tomato()
    {
        Intent intent = new Intent(this, Activity_tomato.class);
        startActivity(intent);
    }

    public void openActivity_chilli()
    {
        Intent intent = new Intent(this, Activity_chilli.class);
        startActivity(intent);
    }
}
