package com.example.urban_computing;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class moisture_se extends AppCompatActivity implements SensorEventListener
{

    TextView xValue;

    private static final String TAG = "moisture_se";
    private SensorManager sensorManager;
    private Sensor sensor_humid;
    private LineChart mChart;
    private Thread thread;
    private DatabaseReference database;
    private boolean plotData = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture_se);
        xValue = (TextView) findViewById(R.id.xValue);
        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // getting the values for sensor_humid
        Log.d(TAG, "onCreate: Registered sensor_humid listener");
        sensor_humid = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        if (sensor_humid != null)
        {
            // reading sensor without delay
            sensorManager.registerListener(moisture_se.this, sensor_humid, SensorManager.SENSOR_DELAY_NORMAL);

        }


        mChart = (LineChart) findViewById(R.id.mChart);
       // mChart.setDescription("light data graph");

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.GRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMaxValue(100f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
        feedMultiple();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this,"resumed",Toast.LENGTH_SHORT);
        sensorManager.registerListener(this, sensor_humid, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
       sensorManager.unregisterListener(this);
        thread.interrupt();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(thread!= null)
        {
            thread.interrupt();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {
    }

    @Override
    public void onSensorChanged(SensorEvent event)
        {
        float maxValue= sensor_humid.getMaximumRange();
        Log.d(TAG, "onSensorChanged" + event.values[0]);
        xValue.setText("Humid Intensity-" + event.values[0]);
        // mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String val = "" + Calendar.getInstance().getTime()+ "," + event.values[0];
        xValue.setText(val);

        float value = event.values[0];

        if(value > 50 && value < 90)
        {

            getWindow().getDecorView().setBackgroundColor(Color.rgb(0, 255, 0));

        }else
        {
            int newValue = (int) (255f * value / maxValue);
            getWindow().getDecorView().setBackgroundColor(Color.rgb(newValue, 0, 0));
        }

        writeToFile(val);

        if (plotData)
        {
            addEntry(event);
            plotData = false;

        }

    }


    public void addEntry(SensorEvent event)
    {
        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if(set == null)
            {
                set= createSet();
                data.addDataSet(set);
            }

            data.addEntry( new Entry(set.getEntryCount(), event.values[0] +5),0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setMaxVisibleValueCount(5);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    public LineDataSet createSet()
    {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        //set.setDrawCubic(true);
        set.setColor(Color.BLACK);
        set.setHighlightEnabled(false);
        set.setCubicIntensity(0.2f);
        set.setDrawValues(true);
        return set;


    }

    public void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread( new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }


    public void writeToFile(String val)
    {

        try
        {

            File sdcard= Environment.getExternalStorageDirectory();
            File dir = new File(sdcard + "/UrbanData");
            if(!dir.exists())
            {

                dir.mkdirs();
            }
            File csv = new File(dir, "record_stawberry_moisture.txt");

            if(!csv.exists())
            {

                csv.createNewFile();
                FileOutputStream fos = new FileOutputStream(csv,true);
                OutputStreamWriter wr= new OutputStreamWriter(fos);
                wr.write("humid value\n");
                wr.flush();
                wr.close();
            }
            else
                {

                    FileOutputStream fos = new FileOutputStream(csv, true);
                    OutputStreamWriter wr = new OutputStreamWriter(fos);
                    System.out.println("enter values ");
                    wr.write(val+"\n");

                    wr.flush();
                    wr.close();
            }
        }catch (IOException i)
        {
            i.printStackTrace();

    }

}

}