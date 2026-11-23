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
import android.os.Vibrator;
import android.os.Build;

public class MainActivity extends Activity {

    CameraManager camera;
    String cameraId;
    boolean روشن = false;
    boolean پلیسی = false;
    boolean قرمز = true;

    Button powerButton, policeButton, timerButton;
    TextView batteryText, statusText, policeLight;
    Handler handler = new Handler();
    Handler timerHandler = new Handler();

    Runnable policeRunnable = new Runnable() {
        @Override
        public void run() {
            if (!پلیسی) return;

            try {
                قرمز = !قرمز;
                policeLight.setBackgroundColor(
                    قرمز ? Color.RED : Color.BLUE
                );
                camera.setTorchMode(cameraId, قرمز);
            } catch (Exception e) {}

            handler.postDelayed(this, 400);
        }
    };

    Runnable batteryRunnable = new Runnable() {
        @Override
        public void run() {
            int battery = ((BatteryManager)getSystemService(BATTERY_SERVICE))
                    .getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            batteryText.setText("🔋 باتری گوشی: " + battery + "%");
            timerHandler.postDelayed(this, 10000);
        }
    };

    Runnable autoOffRunnable = new Runnable() {
        @Override
        public void run() {
            خاموش();
            statusText.setText("⏱ تایمر تمام شد؛ چراغ خاموش شد");
        }
    };

    void خاموش() {
        پلیسی = false;
        روشن = false;
        handler.removeCallbacks(policeRunnable);
        try {
            camera.setTorchMode(cameraId, false);
        } catch (Exception e) {}
        powerButton.setText("روشن کردن چراغ");
        policeButton.setText("🚨 حالت پلیسی");
        policeLight.setBackgroundColor(Color.DKGRAY);
    }

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
        title.setTextColor(Color.rgb(190,100,255));
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("نسخه ۳ | چراغ قوه هوشمند");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);

        statusText = new TextView(this);
        statusText.setText("وضعیت: خاموش");
        statusText.setTextColor(Color.WHITE);
        statusText.setTextSize(18);
        statusText.setGravity(Gravity.CENTER);

        policeLight = new TextView(this);
        policeLight.setText("🚨");
        policeLight.setTextSize(55);
        policeLight.setGravity(Gravity.CENTER);
        policeLight.setBackgroundColor(Color.DKGRAY);

        powerButton = new Button(this);
        powerButton.setText("روشن کردن چراغ");

        policeButton = new Button(this);
        policeButton.setText("🚨 حالت پلیسی");

        timerButton = new Button(this);
        timerButton.setText("⏱ خاموشی خودکار");

        batteryText = new TextView(this);
        batteryText.setTextColor(Color.GREEN);
        batteryText.setTextSize(20);
        batteryText.setGravity(Gravity.CENTER);

        powerButton.setOnClickListener(v -> {
            if (پلیسی) خاموش();

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
            if (پلیسی) {
                خاموش();
                statusText.setText("وضعیت: خاموش");
            } else {
                روشن = false;
                پلیسی = true;
                powerButton.setText("روشن کردن چراغ");
                policeButton.setText("توقف حالت پلیسی");
                statusText.setText("🚨 حالت پلیسی فعال");
                handler.post(policeRunnable);
            }
        });

        timerButton.setOnClickListener(v -> {
            String[] times = {"۱ دقیقه", "۵ دقیقه", "۱۰ دقیقه", "لغو تایمر"};

            new AlertDialog.Builder(this)
                .setTitle("زمان خاموشی")
                .setItems(times, (dialog, which) -> {
                    timerHandler.removeCallbacks(autoOffRunnable);

                    if (which == 3) {
                        statusText.setText("تایمر لغو شد");
                    } else {
                        int[] minutes = {1, 5, 10};
                        timerHandler.postDelayed(
                            autoOffRunnable,
                            minutes[which] * 60 * 1000L
                        );
                        statusText.setText("⏱ خاموشی در " + times[which]);
                    }
                }).show();
        });

        batteryRunnable.run();

        layout.addView(title);
        layout.addView(subtitle);
        layout.addView(statusText);
        layout.addView(policeLight);
        layout.addView(powerButton);
        layout.addView(policeButton);
        layout.addView(timerButton);
        layout.addView(batteryText);

        setContentView(layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        خاموش();
        timerHandler.removeCallbacks(autoOffRunnable);
        timerHandler.removeCallbacks(batteryRunnable);
    }
}
