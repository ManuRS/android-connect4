/*
 * SensorActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import es.uam.eps.dadm.connect4.R;

/**
 * Actividad que juega con los sensores
 */
public class SensorsActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sm;
    Sensor acc;

    /**
     * Ciclo de vida de la actividad - crea la actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        //Broadcast
        registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // Recuperamos el gestor de sensores y consultamos con que cuenta el dispositivo
        sm = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        String str = "";
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            str += "Accelerometer, ";
        }
        if (sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            str += "Gyroscope, ";
        }
        if (sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            str += "Light, ";
        }
        if (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {

            str += "Magnetic field, ";
        }
        if (sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            acc = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            str += "Proximity, ";
        }else{
            TextView tv = (TextView) findViewById(R.id.sensorData);
            tv.setText("Not proximity sensor.");
        }
        if (sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            str += "Rotation vector, ";
        }
        TextView tv = (TextView) findViewById(R.id.sensorsList);
        tv.setText(str);

        /************/
    }

    /**
     * Metodo del cliclo de vida
     */
    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Metodo del cliclo de vida
     */
    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    /**
     * Escuchador de cambios en sensores
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView tv = (TextView) findViewById(R.id.sensorData);
        if(event.values[0]==0)
            tv.setText("True");
        else
            tv.setText("False");

        /*
        String str = "";
        for (Float f: event.values) {
            str += f+", ";
        }
        tv.setText(str);
        */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Prueba de broadcast receiver
     */
    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            TextView textView = (TextView) findViewById(R.id.br);
            textView.setText("BATTERY LEVEL = " + level +"%");
        }
    };

}
