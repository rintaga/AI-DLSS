package com.dlss.ai;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class DLSSService extends Service {

    private static final String CHANNEL = "dlss_background";
    private static final int NOTIFICATION_ID = 1001;

    private WindowManager windowManager;
    private FrameLayout overlay;
    private boolean wakeStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        windowManager = (WindowManager)
            getSystemService(WINDOW_SERVICE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL,
                "DLSS Background Engine",
                NotificationManager.IMPORTANCE_LOW
            );

            channel.setDescription("DLSS background service");
            channel.enableLights(false);
            channel.enableVibration(false);

            NotificationManager manager =
                getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    private void startNotification() {
        Intent stopIntent = new Intent(this, DLSSService.class);
        stopIntent.setAction("STOP_DLSS");

        PendingIntent stopPending = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT |
            PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {
            builder = new Notification.Builder(this, CHANNEL);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification notification = builder
            .setContentTitle("AI DLSS")
            .setContentText("Background engine is running")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_media_pause,
                "STOP",
                stopPending
            )
            .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void showOverlay() {
        if (overlay != null) return;

        if (Build.VERSION.SDK_INT >= 23 &&
            !Settings.canDrawOverlays(this)) {
            return;
        }

        overlay = OverlayManager.create(this, this::stopSelf);

        int type;

        if (Build.VERSION.SDK_INT >= 26) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(
                (int) (360 * getResources().getDisplayMetrics().density),
                (int) (560 * getResources().getDisplayMetrics().density),
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 20;
        params.y = 100;

        try {
            windowManager.addView(overlay, params);
        } catch (Exception e) {
            overlay = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null &&
            "STOP_DLSS".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        startNotification();

        if (!wakeStarted) {
            WakeLockManager.acquire(this);
            wakeStarted = true;
        }

        showOverlay();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (overlay != null) {
            try {
                windowManager.removeView(overlay);
            } catch (Exception ignored) {}

            overlay = null;
        }

        WakeLockManager.release();
        wakeStarted = false;

        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
