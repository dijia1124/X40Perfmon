package com.runhui.x40perfmon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView batTempTextView;
    private WindowManager windowManager;
    private View floatingView;
    private TextView floatingTempTextView;
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 100;

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // Permission granted, proceed with adding the floating view
                // Call the code to add the floating view here
            } else {
                // Permission not granted, handle accordingly (e.g., show a message)
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestOverlayPermission();
        // Initialize the windowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Inflate the floating view layout
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_layout, null);

        // Set initial position and layout parameters for the floating view
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;

        // Add the floating view to the window manager
        windowManager.addView(floatingView, params);

        batTempTextView = findViewById(R.id.batTempTextView);
        floatingTempTextView = floatingView.findViewById(R.id.floatingTempTextView);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the floating view from the window manager when the activity is destroyed
        if (windowManager != null && floatingView != null) {
            windowManager.removeView(floatingView);
        }

        unregisterReceiver(batteryReceiver);
    }
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                float batTemp = temperature / 10f;
                batTempTextView.setText("Battery Temperature: " + batTemp + " °C");
                floatingTempTextView.setText("BatTemp: " + batTemp);
            }
        }
    };
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