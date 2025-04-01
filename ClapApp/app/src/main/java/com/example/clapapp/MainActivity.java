package com.example.clapapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.media.ToneGenerator;
import android.media.AudioManager;


public class MainActivity extends Activity implements SensorEventListener{
    // implements SensorEventListener
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private TextView clapMessage;
    private TextView clapCountText;
    private int clapCount = 0;  // Variable to count the number of claps
    private ToneGenerator toneGen;  // ToneGenerator instance for generating beep sounds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the UI elements
        clapMessage = findViewById(R.id.clapMessage);
        clapCountText = findViewById(R.id.clapCountText);

        // Initialize the ToneGenerator for beep sound
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);  // Generate tone at normal volume level

        // Get the SensorManager system service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get the proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            clapMessage.setText("Proximity sensor not available!");
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener for proximity sensor updates
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener when not in use to save battery
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        // Release the ToneGenerator when the activity is paused
        if (toneGen != null) {
            toneGen.release();
            toneGen = null;
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            // Get the proximity value from the sensor
            float proximityValue = event.values[0];

            // Check if the proximity value is within the range from 1 cm to 10 cm
            if (proximityValue >= 1 && proximityValue <= 5) {
                // Simulate a clap when the hand is in the desired proximity range
                clapMessage.setText("Clap detected");
                clapCount++;  // Increment the clap count
                clapCountText.setText("Total Claps: " + clapCount);  // Update the UI with the clap count

                // Play a beep sound using ToneGenerator
                if (toneGen != null) {
                    toneGen.startTone(ToneGenerator.TONE_CDMA_HIGH_SS, 200);  // Short beep tone for 200ms
                }
            } else {
                clapMessage.setText("Hand is not in range for clap detection");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this case, but it's required by SensorEventListener
    }
}