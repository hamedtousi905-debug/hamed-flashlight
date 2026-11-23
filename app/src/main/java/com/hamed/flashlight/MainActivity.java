package com.hamed.flashlight;

import android.app.Activity;
import android.os.Bundle;
import android.os.BatteryManager;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import android.hardware.camera2.CameraManager;
import android.os.Handler;

public class MainActivity extends Activity {

    CameraManager camera;
    String cameraId;
    boolean روشن = false;
    boolean پلیسی = false;

    Button powerButton, policeButton;
    TextView batteryText, statusText;
    Handler handler = new Handler();

    Runnable policeRunnable = new Runnable() {
        boolean state = false;

        @Override
        public void run() {
            if (!پلیسی) return;

            try {
                state = !state;
                camera.setTorchMode(cameraId, state);
            } catch (Exception e) {}

            handler.postDelayed(this, 300);
        }
    };

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        camera = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = camera.getCameraIdList()[0];
        } catch (Exception e) {}

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30,30,30,30);
        layout.setBackgroundColor(Color.rgb(9,9,18));

        TextView title = new TextView(this);
        title.setText("HAMED FLASHLIGHT");
        title.setTextColor(Color.rgb(180,100,255));
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("نسخه ۲ | چراغ قوه هوشمند");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);

        statusText = new TextView(this);
        statusText.setText("وضعیت: خاموش");
        statusText.setTextColor(Color.WHITE);
        statusText.setTextSize(18);
        statusText.setGravity(Gravity.CENTER);

        powerButton = new Button(this);
        powerButton.setText("روشن کردن چراغ");

        policeButton = new Button(this);
        policeButton.setText("🚨 حالت پلیسی");

        batteryText = new TextView(this);
        batteryText.setTextColor(Color.GREEN);
        batteryText.setTextSize(20);
        batteryText.setGravity(Gravity.CENTER);

        powerButton.setOnClickListener(v -> {
            پلیسی = false;
            handler.removeCallbacks(policeRunnable);
            روشن = !روشن;

            try {
                camera.setTorchMode(cameraId, روشن);
                powerButton.setText(روشن ? "خاموش کردن چراغ" : "روشن کردن چراغ");
                statusText.setText(روشن ? "وضعیت: روشن 🟢" : "وضعیت: خاموش");
            } catch (Exception e) {
                statusText.setText("خطا در روشن کردن فلش");
            }
        });

        policeButton.setOnClickListener(v -> {
            پلیسی = !پلیسی;
            روشن = false;

            if (پلیسی) {
                روشن = false;
                powerButton.setText("روشن کردن چراغ");
                statusText.setText("🚨 حالت پلیسی فعال");
                handler.post(policeRunnable);
            } else {
                handler.removeCallbacks(policeRunnable);
                try {
                    camera.setTorchMode(cameraId, false);
                } catch (Exception e) {}
                statusText.setText("وضعیت: خاموش");
            }
        });

        int battery = ((BatteryManager)getSystemService(BATTERY_SERVICE))
                .getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        batteryText.setText("🔋 باتری گوشی: " + battery + "%");

        layout.addView(title);
        layout.addView(subtitle);
        layout.addView(statusText);
        layout.addView(powerButton);
        layout.addView(policeButton);
        layout.addView(batteryText);

        setContentView(layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        پلیسی = false;
        handler.removeCallbacks(policeRunnable);

        try {
            camera.setTorchMode(cameraId, false);
        } catch (Exception e) {}
    }
}
