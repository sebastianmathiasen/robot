package com.unity3d.player;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import java.util.ArrayList;
import java.util.List;

final class a {
    Camera a;
    Camera.Parameters b;
    Camera.Size c;
    int d;
    int[] e;
    b f;
    /* access modifiers changed from: private */
    public final Object[] g = new Object[0];
    private final int h;
    private final int i;
    private final int j;
    private final int k;

    /* renamed from: com.unity3d.player.a$a  reason: collision with other inner class name */
    interface C0000a {
        void onCameraFrame(a aVar, byte[] bArr);
    }

    public a(int i2, int i3, int i4, int i5) {
        this.h = i2;
        this.i = a(i3, 640);
        this.j = a(i4, 480);
        this.k = a(i5, 24);
    }

    private static final int a(int i2, int i3) {
        return i2 != 0 ? i2 : i3;
    }

    private static void a(Camera.Parameters parameters) {
        if (parameters.getSupportedColorEffects() != null) {
            parameters.setColorEffect("none");
        }
        if (parameters.getSupportedFocusModes().contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }
    }

    private void b(final C0000a aVar) {
        synchronized (this.g) {
            this.a = Camera.open(this.h);
            this.b = this.a.getParameters();
            this.c = f();
            this.e = e();
            this.d = d();
            a(this.b);
            this.b.setPreviewSize(this.c.width, this.c.height);
            this.b.setPreviewFpsRange(this.e[0], this.e[1]);
            this.a.setParameters(this.b);
            AnonymousClass1 r0 = new Camera.PreviewCallback() {
                long a = 0;

                public final void onPreviewFrame(byte[] bArr, Camera camera) {
                    if (a.this.a == camera) {
                        aVar.onCameraFrame(a.this, bArr);
                    }
                }
            };
            int i2 = (((this.c.width * this.c.height) * this.d) / 8) + 4096;
            this.a.addCallbackBuffer(new byte[i2]);
            this.a.addCallbackBuffer(new byte[i2]);
            this.a.setPreviewCallbackWithBuffer(r0);
        }
    }

    private final int d() {
        this.b.setPreviewFormat(17);
        return ImageFormat.getBitsPerPixel(17);
    }

    private final int[] e() {
        int[] iArr;
        double d2;
        double d3 = (double) (this.k * 1000);
        List<int[]> supportedPreviewFpsRange = this.b.getSupportedPreviewFpsRange();
        if (supportedPreviewFpsRange == null) {
            supportedPreviewFpsRange = new ArrayList<>();
        }
        int[] iArr2 = {this.k * 1000, this.k * 1000};
        double d4 = Double.MAX_VALUE;
        for (int[] iArr3 : supportedPreviewFpsRange) {
            double abs = Math.abs(Math.log(d3 / ((double) iArr3[0]))) + Math.abs(Math.log(d3 / ((double) iArr3[1])));
            if (abs < d4) {
                iArr = iArr3;
                d2 = abs;
            } else {
                double d5 = d4;
                iArr = iArr2;
                d2 = d5;
            }
            iArr2 = iArr;
            d4 = d2;
        }
        return iArr2;
    }

    private final Camera.Size f() {
        Camera.Size size;
        double d2;
        double d3 = (double) this.i;
        double d4 = (double) this.j;
        Camera.Size size2 = null;
        double d5 = Double.MAX_VALUE;
        for (Camera.Size next : this.b.getSupportedPreviewSizes()) {
            double abs = Math.abs(Math.log(d3 / ((double) next.width))) + Math.abs(Math.log(d4 / ((double) next.height)));
            if (abs < d5) {
                double d6 = abs;
                size = next;
                d2 = d6;
            } else {
                size = size2;
                d2 = d5;
            }
            d5 = d2;
            size2 = size;
        }
        return size2;
    }

    public final int a() {
        return this.h;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void a(com.unity3d.player.a.C0000a r4) {
        /*
            r3 = this;
            java.lang.Object[] r1 = r3.g
            monitor-enter(r1)
            android.hardware.Camera r0 = r3.a     // Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x000a
            r3.b(r4)     // Catch:{ all -> 0x0031 }
        L_0x000a:
            boolean r0 = com.unity3d.player.o.a     // Catch:{ all -> 0x0031 }
            if (r0 == 0) goto L_0x001f
            com.unity3d.player.f r0 = com.unity3d.player.o.h     // Catch:{ all -> 0x0031 }
            android.hardware.Camera r2 = r3.a     // Catch:{ all -> 0x0031 }
            boolean r0 = r0.a((android.hardware.Camera) r2)     // Catch:{ all -> 0x0031 }
            if (r0 == 0) goto L_0x001f
            android.hardware.Camera r0 = r3.a     // Catch:{ all -> 0x0031 }
            r0.startPreview()     // Catch:{ all -> 0x0031 }
            monitor-exit(r1)     // Catch:{ all -> 0x0031 }
        L_0x001e:
            return
        L_0x001f:
            com.unity3d.player.b r0 = r3.f     // Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x002f
            com.unity3d.player.a$2 r0 = new com.unity3d.player.a$2     // Catch:{ all -> 0x0031 }
            r0.<init>()     // Catch:{ all -> 0x0031 }
            r3.f = r0     // Catch:{ all -> 0x0031 }
            com.unity3d.player.b r0 = r3.f     // Catch:{ all -> 0x0031 }
            r0.a()     // Catch:{ all -> 0x0031 }
        L_0x002f:
            monitor-exit(r1)     // Catch:{ all -> 0x0031 }
            goto L_0x001e
        L_0x0031:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0031 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unity3d.player.a.a(com.unity3d.player.a$a):void");
    }

    public final void a(byte[] bArr) {
        synchronized (this.g) {
            if (this.a != null) {
                this.a.addCallbackBuffer(bArr);
            }
        }
    }

    public final Camera.Size b() {
        return this.c;
    }

    public final void c() {
        synchronized (this.g) {
            if (this.a != null) {
                this.a.setPreviewCallbackWithBuffer((Camera.PreviewCallback) null);
                this.a.stopPreview();
                this.a.release();
                this.a = null;
            }
            if (this.f != null) {
                this.f.b();
                this.f = null;
            }
        }
    }
}
