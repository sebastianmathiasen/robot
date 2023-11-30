package com.badran.bluetoothcontroller;

import java.io.IOException;

final class i extends Thread {
    private /* synthetic */ h b;

    private i(h hVar) {
        this.b = hVar;
    }

    /* synthetic */ i(h hVar, byte b2) {
        this(hVar);
    }

    public final void run() {
        this.b.e = true;
        while (!this.b.l && this.b.f26a.size() > 0) {
            try {
                switch (((Integer) ((Object[]) this.b.f26a.get(0))[0]).intValue()) {
                    case 0:
                        this.b.a.write(((Integer) ((Object[]) this.b.f26a.get(0))[1]).intValue());
                        this.b.a.flush();
                        break;
                    case 1:
                        this.b.f25a.print((char[]) ((Object[]) this.b.f26a.get(0))[1]);
                        if (this.b.f25a.checkError()) {
                            Bridge.controlMessage(-5);
                            break;
                        }
                        break;
                    case 2:
                        this.b.a.write((byte[]) ((Object[]) this.b.f26a.get(0))[1]);
                        this.b.a.flush();
                        break;
                }
                if (this.b.f26a.size() > 0) {
                    this.b.f26a.remove(0);
                }
            } catch (IOException e) {
                if (this.b.f26a.size() > 0) {
                    this.b.f26a.remove(0);
                }
                Bridge.controlMessage(-5);
            }
        }
        this.b.e = false;
    }
}
