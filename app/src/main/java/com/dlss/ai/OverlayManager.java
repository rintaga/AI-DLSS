package com.dlss.ai;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.opengl.GLSurfaceView;

public final class OverlayManager {

    private OverlayManager() {}

    public static FrameLayout create(
        Context context,
        Runnable closeAction
    ) {
        FrameLayout root = new FrameLayout(context);

        root.setBackgroundColor(Color.argb(245, 8, 12, 20));

        GLSurfaceView surface = new GLSurfaceView(context);

        surface.setEGLContextClientVersion(3);
        surface.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surface.setZOrderMediaOverlay(true);
        DLSSRenderer renderer = new DLSSRenderer(context);
        surface.setRenderer(renderer);
        surface.setOnTouchListener((v, event) ->
            DLSSRenderer.handleTouch(event, surface));
        surface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        root.addView(
            surface,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        );

        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(Color.rgb(20, 35, 55));

        TextView title = new TextView(context);
        title.setText("DLSS  ↕");
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(14, 8, 14, 8);

        TextView close = new TextView(context);
        close.setText("×");
        close.setTextSize(25);
        close.setTextColor(Color.WHITE);
        close.setGravity(Gravity.CENTER);
        close.setPadding(18, 0, 18, 0);
        close.setOnClickListener(v -> closeAction.run());

        header.addView(
            title,
            new LinearLayout.LayoutParams(
                0,
                42,
                1
            )
        );

        header.addView(
            close,
            new LinearLayout.LayoutParams(
                48,
                42
            )
        );

        root.addView(
            header,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                42,
                Gravity.TOP
            )
        );

        title.setOnTouchListener(new View.OnTouchListener() {

            private float startX;
            private float startY;
            private int initialX;
            private int initialY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                WindowManager.LayoutParams params =
                    (WindowManager.LayoutParams) root.getLayoutParams();

                if (params == null) return false;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        initialX = params.x;
                        initialY = params.y;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX +
                            (int) (event.getRawX() - startX);

                        params.y = initialY +
                            (int) (event.getRawY() - startY);

                        try {
                            WindowManager manager =
                                (WindowManager) context.getSystemService(
                                    Context.WINDOW_SERVICE
                                );

                            manager.updateViewLayout(root, params);
                        } catch (Exception ignored) {}

                        return true;
                }

                return true;
            }
        });

        return root;
    }
}
