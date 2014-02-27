package edu.sjsu.movemonitor.app;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {

    public static final String LOG_TAG = "edu.sjsu.MoveMonitor.log";
    private SensorManager sensorManager;
    private Integer stepCount = 0;
    private DatabaseAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);


        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2)
        {
            // Turn on camera torch

            stepCount++;
            updateScreenCount();

        }
    }

    public void stopSensor(View view) {

        sensorManager.unregisterListener(this);

    }

    public void resetCount(View view) {
        stepCount = 0;
        updateScreenCount();
    }

    public void updateScreenCount() {
        TextView view = (TextView)findViewById(R.id.stepCountText);
        view.setText(Integer.toString(stepCount));
    }

    public void saveCount(View view) {

        // Date
        Date date = new Date();
        // Count
        TextView countView = (TextView)findViewById(R.id.stepCountText);
        dbAdapter.addRecord(date.toString(), countView.getText().toString());

        // Reset
        resetCount(null);
        Toast.makeText(this, "Count Saved", Toast.LENGTH_LONG).show();
    }

    public void showHistory(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }


}
