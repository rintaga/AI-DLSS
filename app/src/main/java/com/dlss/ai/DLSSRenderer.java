package com.dlss.ai;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DLSSRenderer implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("dlss");
    }

    private static native void nativeInit();
    private static native void nativeResize(int width, int height);
    private static native void nativeRender();
    private static native void nativeTouch(
        int action,
        float x,
        float y
    );

    public DLSSRenderer(Context context) {}

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        nativeInit();
    }

    @Override
    public void onSurfaceChanged(
        GL10 gl,
        int width,
        int height
    ) {
        nativeResize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        nativeRender();
    }

    public static boolean handleTouch(
        MotionEvent event,
        GLSurfaceView surface
    ) {
        final int action = event.getActionMasked();
        final float x = event.getX();
        final float y = event.getY();

        surface.queueEvent(() ->
            nativeTouch(action, x, y)
        );

        return true;
    }
}
