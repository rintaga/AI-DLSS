package com.dlss.ai;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private void startDLSS() {
        Intent intent = new Intent(this, DLSSService.class);

        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 40, 30, 30);
        layout.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("AI DLSS");
        title.setTextSize(32);
        title.setGravity(Gravity.CENTER);

        TextView description = new TextView(this);
        description.setText(
            "Universal Neural Graphics Engine\n\n" +
            "Floating ImGui\n" +
            "Automatic Wake Lock\n" +
            "Background Engine\n" +
            "ARM32 + ARM64"
        );
        description.setGravity(Gravity.CENTER);
        description.setTextSize(15);

        Button open = new Button(this);
        open.setText("OPEN DLSS OVERLAY");

        open.setOnClickListener(v -> {
            startDLSS();

            if (Build.VERSION.SDK_INT >= 23 &&
                !Settings.canDrawOverlays(this)) {

                Intent permission = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );

                startActivity(permission);
            }
        });

        Button stop = new Button(this);
        stop.setText("STOP DLSS");

        stop.setOnClickListener(v -> {
            stopService(new Intent(this, DLSSService.class));
        });

        layout.addView(title);
        layout.addView(description);
        layout.addView(open);
        layout.addView(stop);

        setContentView(layout);

        startDLSS();

        if (Build.VERSION.SDK_INT >= 23 &&
            !Settings.canDrawOverlays(this)) {

            Intent permission = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );

            startActivity(permission);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23 &&
            Settings.canDrawOverlays(this)) {
            startDLSS();
        }
    }
}
