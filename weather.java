package com.example.urban_computing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;


public class weather extends AppCompatActivity implements SensorEventListener
{

    //adding variable
    public TextView t1_temp, t2_city, t3_desciption, t4_date, xValue;
    private static final String TAG = "weather";

    private SensorManager sensorManager;
    private Sensor sensor_humid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        //button code adding
        t1_temp = (TextView)findViewById(R.id.textView);
        t2_city = (TextView)findViewById(R.id.textView4);
        t3_desciption = (TextView)findViewById(R.id.textView7);
        t4_date = (TextView)findViewById(R.id.textView2);
        xValue = (TextView) findViewById(R.id.textView3);
        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // getting the values for sensor_humid
        Log.d(TAG, "onCreate: Registered sensor_humid listener");
        sensor_humid = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (sensor_humid != null)
        {
            // reading sensor without delay
            sensorManager.registerListener(weather.this, sensor_humid, SensorManager.SENSOR_DELAY_NORMAL);

        }
        find_weather();


    }


    public void find_weather()
    {

        System.out.println("enter in find weather");
        String url = "https://api.openweathermap.org/data/2.5/weather?q=dublin,ireland&APPID=0539e0c5d7d3e23313d635c3ceb0440a&units=Imperial";
        System.out.println("enter");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
        {

         @Override

         public void onResponse(JSONObject response){
             System.out.println("on response");

                try {

                    System.out.println("enter in try");
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");

                    t1_temp.setText(temp);
                    System.out.println(temp);
                    t2_city.setText(city);
                    t3_desciption.setText(description);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    String formatted_date = sdf.format(calendar.getTime());

                    t4_date.setText(formatted_date);

                    double temp_int = Double.parseDouble(temp);
                    double centi= (temp_int-32)/1.8000;
                    centi = Math.round(centi);
                    int i= (int)centi;
                    t1_temp.setText(String.valueOf(i));

                }catch (JSONException e)

                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener()
        {

            @Override

            public void onErrorResponse(VolleyError error) {

            }
        }

        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "value of temp-" +event.values[0]);

        xValue.setText("xvalue" + event.values[0]);
        // mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String val = "" + event.values[0];
        xValue.setText(val);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
