package com.runhui.x40perfmon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView batTempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batTempTextView = findViewById(R.id.batTempTextView);
        float batTemp = getBatteryTemperature();

        // Set the battery temperature in the TextView
        batTempTextView.setText("Battery Temperature: " + batTemp + " °C");
    }

    private float getBatteryTemperature() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, intentFilter);

        if (batteryStatus != null) {
            int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            return temperature / 10f; // The temperature is returned in tenths of a degree Celsius
        }
        return 0f; // Return 0 if the battery temperature cannot be retrieved
    }
}