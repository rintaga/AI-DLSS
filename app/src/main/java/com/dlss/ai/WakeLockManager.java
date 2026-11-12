package com.dlss.ai;

import android.content.Context;
import android.os.PowerManager;

public final class WakeLockManager {

    private static PowerManager.WakeLock wakeLock;

    private WakeLockManager() {}

    public static synchronized void acquire(Context context) {
        if (wakeLock != null && wakeLock.isHeld()) {
            return;
        }

        PowerManager manager =
            (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        wakeLock = manager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            context.getPackageName() + ":DLSS"
        );

        wakeLock.setReferenceCounted(false);
        wakeLock.acquire();
    }

    public static synchronized void release() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        wakeLock = null;
    }
}
