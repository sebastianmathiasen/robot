package com.unity3d.player;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.view.View;

public final class d implements f {
    private static final SurfaceTexture a = new SurfaceTexture(-1);
    private static final int b = (o.f ? 5894 : 1);
    /* access modifiers changed from: private */
    public volatile boolean c;

    /* access modifiers changed from: private */
    public void a(final View view, int i) {
        Handler handler = view.getHandler();
        if (handler == null) {
            a(view, this.c);
        } else {
            handler.postDelayed(new Runnable() {
                public final void run() {
                    d.this.a(view, d.this.c);
                }
            }, 1000);
        }
    }

    public final void a(final View view) {
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            public final void onSystemUiVisibilityChange(int i) {
                d.this.a(view, 1000);
            }
        });
    }

    public final void a(View view, boolean z) {
        this.c = z;
        view.setSystemUiVisibility(this.c ? view.getSystemUiVisibility() | b : view.getSystemUiVisibility() & (b ^ -1));
    }

    public final boolean a(Camera camera) {
        try {
            camera.setPreviewTexture(a);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public final void b(View view) {
        if (!o.f && this.c) {
            a(view, false);
            this.c = true;
        }
        a(view, 1000);
    }
}
