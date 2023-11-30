package com.unity3d.player;

import android.view.Choreographer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class k implements h {
    /* access modifiers changed from: private */
    public Choreographer a = null;
    private long b = 0;
    /* access modifiers changed from: private */
    public Choreographer.FrameCallback c;
    /* access modifiers changed from: private */
    public Lock d = new ReentrantLock();

    public final void a() {
        this.d.lock();
        if (this.a != null) {
            this.a.removeFrameCallback(this.c);
        }
        this.a = null;
        this.d.unlock();
    }

    public final void a(final UnityPlayer unityPlayer) {
        this.d.lock();
        if (this.a == null) {
            this.a = Choreographer.getInstance();
            if (this.a != null) {
                l.Log(4, "Choreographer available: Enabling VSYNC timing");
                this.c = new Choreographer.FrameCallback() {
                    public final void doFrame(long j) {
                        UnityPlayer.lockNativeAccess();
                        if (t.c()) {
                            unityPlayer.nativeAddVSyncTime(j);
                        }
                        UnityPlayer.unlockNativeAccess();
                        k.this.d.lock();
                        if (k.this.a != null) {
                            k.this.a.postFrameCallback(k.this.c);
                        }
                        k.this.d.unlock();
                    }
                };
                this.a.postFrameCallback(this.c);
            }
        }
        this.d.unlock();
    }
}
