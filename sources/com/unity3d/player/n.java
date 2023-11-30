package com.unity3d.player;

import android.app.Activity;
import android.content.ContextWrapper;
import android.view.MotionEvent;
import android.view.View;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class n implements i {
    /* access modifiers changed from: private */
    public final Queue a = new ConcurrentLinkedQueue();
    /* access modifiers changed from: private */
    public final Activity b;
    private Runnable c = new Runnable() {
        private static void a(View view, MotionEvent motionEvent) {
            if (o.b) {
                o.i.a(view, motionEvent);
            }
        }

        public final void run() {
            while (true) {
                MotionEvent motionEvent = (MotionEvent) n.this.a.poll();
                if (motionEvent != null) {
                    View decorView = n.this.b.getWindow().getDecorView();
                    int source = motionEvent.getSource();
                    if ((source & 2) != 0) {
                        switch (motionEvent.getAction() & 255) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                decorView.dispatchTouchEvent(motionEvent);
                                break;
                            default:
                                a(decorView, motionEvent);
                                break;
                        }
                    } else if ((source & 4) != 0) {
                        decorView.dispatchTrackballEvent(motionEvent);
                    } else {
                        a(decorView, motionEvent);
                    }
                } else {
                    return;
                }
            }
        }
    };

    public n(ContextWrapper contextWrapper) {
        this.b = (Activity) contextWrapper;
    }

    private static int a(MotionEvent.PointerCoords[] pointerCoordsArr, float[] fArr, int i) {
        for (int i2 = 0; i2 < pointerCoordsArr.length; i2++) {
            MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
            pointerCoordsArr[i2] = pointerCoords;
            int i3 = i + 1;
            pointerCoords.orientation = fArr[i];
            int i4 = i3 + 1;
            pointerCoords.pressure = fArr[i3];
            int i5 = i4 + 1;
            pointerCoords.size = fArr[i4];
            int i6 = i5 + 1;
            pointerCoords.toolMajor = fArr[i5];
            int i7 = i6 + 1;
            pointerCoords.toolMinor = fArr[i6];
            int i8 = i7 + 1;
            pointerCoords.touchMajor = fArr[i7];
            int i9 = i8 + 1;
            pointerCoords.touchMinor = fArr[i8];
            int i10 = i9 + 1;
            pointerCoords.x = fArr[i9];
            i = i10 + 1;
            pointerCoords.y = fArr[i10];
        }
        return i;
    }

    private static MotionEvent.PointerCoords[] a(int i, float[] fArr) {
        MotionEvent.PointerCoords[] pointerCoordsArr = new MotionEvent.PointerCoords[i];
        a(pointerCoordsArr, fArr, 0);
        return pointerCoordsArr;
    }

    public final void a(long j, long j2, int i, int i2, int[] iArr, float[] fArr, int i3, float f, float f2, int i4, int i5, int i6, int i7, int i8, long[] jArr, float[] fArr2) {
        if (this.b != null) {
            MotionEvent obtain = MotionEvent.obtain(j, j2, i, i2, iArr, a(i2, fArr), i3, f, f2, i4, i5, i6, i7);
            int i9 = 0;
            for (int i10 = 0; i10 < i8; i10++) {
                MotionEvent.PointerCoords[] pointerCoordsArr = new MotionEvent.PointerCoords[i2];
                i9 = a(pointerCoordsArr, fArr2, i9);
                obtain.addBatch(jArr[i10], pointerCoordsArr, i3);
            }
            this.a.add(obtain);
            this.b.runOnUiThread(this.c);
        }
    }
}
