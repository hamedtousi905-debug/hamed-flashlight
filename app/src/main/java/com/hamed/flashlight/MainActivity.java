package com.hamed.flashlight;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.camera2.CameraManager;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    CameraManager camera;
    String cameraId;
    boolean روشن = false;
    Button button;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        camera = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = camera.getCameraIdList()[0];
        } catch(Exception e) {}

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.rgb(12,12,25));

        TextView title = new TextView(this);
        title.setText("HAMED FLASHLIGHT");
        title.setTextColor(Color.WHITE);
        title.setTextSize(25);
        title.setGravity(Gravity.CENTER);

        button = new Button(this);
        button.setText("روشن کردن چراغ");

        button.setOnClickListener(v -> {
            روشن = !روشن;
            try {
                camera.setTorchMode(cameraId, روشن);
                button.setText(روشن ? "خاموش کردن چراغ" : "روشن کردن چراغ");
            } catch(Exception e) {}
        });

        layout.addView(title);
        layout.addView(button);

        setContentView(layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (روشن) camera.setTorchMode(cameraId, false);
        } catch(Exception e) {}
    }
}
