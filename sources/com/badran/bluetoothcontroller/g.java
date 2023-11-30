package com.badran.bluetoothcontroller;

import com.unity3d.player.UnityPlayer;
import java.io.IOException;
import java.util.Arrays;

final class g extends Thread {
    private /* synthetic */ e b;

    private g(e eVar) {
        this.b = eVar;
    }

    /* synthetic */ g(e eVar, byte b2) {
        this(eVar);
    }

    public final void run() {
        boolean z;
        this.b.f24d = true;
        int i = 0;
        while (this.b.a != null && Bridge.f9b && !this.b.k) {
            try {
                if (this.b.f22a.available() > 0) {
                    switch (f.a[Bridge.f4a.ordinal()]) {
                        case 1:
                            if (!this.b.h) {
                                this.b.c = this.b.f21a.readLine();
                                UnityPlayer.UnitySendMessage("BtConnector", "receiver", this.b.c);
                                this.b.h = true;
                                this.b.i = true;
                                break;
                            } else {
                                continue;
                            }
                        case 2:
                        case 3:
                            if (!this.b.j) {
                                int length = this.b.f23a.length - i;
                                int i2 = i;
                                while (true) {
                                    if (i2 >= length) {
                                        z = true;
                                    } else {
                                        int read = this.b.f22a.read();
                                        if (read >= 0) {
                                            if (read == Bridge.a && Bridge.f4a != c.mode2) {
                                                try {
                                                    this.b.buffer = Arrays.copyOf(this.b.f23a, i2);
                                                    UnityPlayer.UnitySendMessage("BtConnector", "startReading", "");
                                                    this.b.j = true;
                                                    this.b.i = true;
                                                    z = false;
                                                    i = 0;
                                                } catch (IOException e) {
                                                    i = 0;
                                                    break;
                                                }
                                            } else {
                                                this.b.f23a[i2] = (byte) read;
                                                i2++;
                                            }
                                        } else {
                                            i = i2;
                                            z = false;
                                        }
                                    }
                                }
                                if (!z) {
                                    break;
                                } else {
                                    this.b.buffer = Arrays.copyOf(this.b.f23a, this.b.f23a.length);
                                    UnityPlayer.UnitySendMessage("BtConnector", "startReading", "");
                                    this.b.j = true;
                                    this.b.i = true;
                                    i = 0;
                                    break;
                                }
                            } else {
                                continue;
                            }
                        case 4:
                            if (!this.b.j) {
                                int length2 = this.b.f23a.length - this.b.d;
                                int i3 = 0;
                                boolean z2 = false;
                                while (true) {
                                    if (i3 < length2) {
                                        if (this.b.f22a.available() > 0) {
                                            int read2 = this.b.f22a.read();
                                            if (read2 >= 0) {
                                                this.b.f23a[i3] = (byte) read2;
                                                i3++;
                                                z2 = true;
                                            }
                                        } else {
                                            this.b.d = i3;
                                        }
                                    }
                                }
                                if (!z2) {
                                    break;
                                } else {
                                    this.b.buffer = Arrays.copyOf(this.b.f23a, i3);
                                    UnityPlayer.UnitySendMessage("BtConnector", "startReading", "");
                                    this.b.j = true;
                                    this.b.i = true;
                                    break;
                                }
                            } else {
                                continue;
                            }
                        default:
                            continue;
                    }
                    this.b.f24d = false;
                    Bridge.controlMessage(-6);
                }
            } catch (IOException e2) {
            }
        }
        this.b.f24d = false;
    }
}
