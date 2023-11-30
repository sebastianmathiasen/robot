package com.badran.bluetoothcontroller;

import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

final class h {
    BufferedOutputStream a = null;

    /* renamed from: a  reason: collision with other field name */
    PrintWriter f25a = null;

    /* renamed from: a  reason: collision with other field name */
    volatile ArrayList f26a;
    public volatile boolean e = false;
    boolean l = false;

    public h(BufferedOutputStream bufferedOutputStream, PrintWriter printWriter) {
        this.a = bufferedOutputStream;
        this.f25a = printWriter;
        this.f26a = new ArrayList();
    }

    /* access modifiers changed from: package-private */
    public final void b() {
        if (!this.e) {
            this.e = true;
            new i(this, (byte) 0).start();
        }
    }
}
