package com.unity3d.player;

import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

abstract class b implements SurfaceHolder.Callback {
    private final Activity a = ((Activity) r.a.a());
    /* access modifiers changed from: private */
    public final int b = 3;
    /* access modifiers changed from: private */
    public SurfaceView c;

    b(int i) {
    }

    /* access modifiers changed from: package-private */
    public final void a() {
        this.a.runOnUiThread(new Runnable() {
            public final void run() {
                if (b.this.c == null) {
                    SurfaceView unused = b.this.c = new SurfaceView(r.a.a());
                    b.this.c.getHolder().setType(b.this.b);
                    b.this.c.getHolder().addCallback(b.this);
                    r.a.a(b.this.c);
                    b.this.c.setVisibility(0);
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public final void b() {
        this.a.runOnUiThread(new Runnable() {
            public final void run() {
                if (b.this.c != null) {
                    r.a.b(b.this.c);
                }
                SurfaceView unused = b.this.c = null;
            }
        });
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
}
