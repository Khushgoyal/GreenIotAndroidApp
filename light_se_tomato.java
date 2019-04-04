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

public class light_se_tomato extends AppCompatActivity implements SensorEventListener , View.OnClickListener
{

    TextView xValue;

    private static final String TAG = "light_se";
    private SensorManager sensorManager;
    private Sensor sensor_light;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private Button uploadbutton, chooseButton;
    private StorageReference mStorageRef;
    private DatabaseReference database;
    private Uri filePath;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_light_se_tomato);
        // return an object

        database = FirebaseDatabase.getInstance().getReference();
        // declaring button
        uploadbutton = (Button) findViewById(R.id.uploadButton);
        uploadbutton.setOnClickListener(this);
        chooseButton = (Button) findViewById(R.id.chooseButton);
        chooseButton.setOnClickListener(this);

        xValue = (TextView) findViewById(R.id.xValue);
        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // getting the values for sensor_light
        Log.d(TAG, "onCreate: Registered sensor_light listener");
        sensor_light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (sensor_light != null)
        {
            sensorManager.registerListener(light_se_tomato.this, sensor_light, SensorManager.SENSOR_DELAY_NORMAL);// reading sensor without delay

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
        leftAxis.setAxisMaxValue(50000f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
        feedMultiple();

        chooseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)

            {
            if(ContextCompat.checkSelfPermission(light_se_tomato.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                    chooseFile();
                }else
                ActivityCompat.requestPermissions(light_se_tomato.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            }
        });


        uploadbutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
            if(filePath!= null)
                uploadFile(filePath);
         }
});



    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this,"resumed",Toast.LENGTH_SHORT);
        sensorManager.registerListener(this, sensor_light, SensorManager.SENSOR_DELAY_NORMAL);
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
        float maxValue= sensor_light.getMaximumRange();
        Log.d(TAG, "onSensorChanged" + event.values[0]);
        xValue.setText("Light Intensity-" + event.values[0]);
        // mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String val = "" + Calendar.getInstance().getTime()+ "," + event.values[0];
        xValue.setText(val);

        float value = event.values[0];

        if(value > 4000 && value < 8000)
        {
            //green
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
                        Thread.sleep(100);
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
            File csv = new File(dir, "record.txt");

            if(!csv.exists())
            {

                csv.createNewFile();
                FileOutputStream fos = new FileOutputStream(csv,true);
                OutputStreamWriter wr= new OutputStreamWriter(fos);
                wr.write("light value\n");
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


   public void uploadFile(Uri filePath)
   {
pd= new ProgressDialog(this);
pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
pd.setTitle("Uploading");
pd.setProgress(0);
pd.show();
       String fileName = System.currentTimeMillis() + "record";
       mStorageRef = FirebaseStorage.getInstance().getReference();
       mStorageRef.child("Uploads").child(fileName).putFile(filePath)

               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception exception) {

                   }
               })

       .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 *taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                pd.setMessage(((int) progress) + "% Uploaded....");
           }
       })


       ;
   }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode==9 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        chooseFile();
else
    {
        Toast.makeText(this, "please provide permission", Toast.LENGTH_SHORT).show();
    }

    }

    public void chooseFile()
    {
        Intent intent = new Intent();
        intent.setType("text/*");
        // to fetch file
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select txt"),86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 86 && resultCode == RESULT_OK && data != null && data.getData() != null)
            filePath = data.getData();
        else
        {
            Toast.makeText(this, "please select the file", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {

    }
}