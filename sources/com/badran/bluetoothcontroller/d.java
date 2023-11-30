package com.badran.bluetoothcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public final class d extends Thread {
    private BluetoothAdapter a = null;

    /* renamed from: a  reason: collision with other field name */
    private BluetoothSocket f10a;

    /* renamed from: a  reason: collision with other field name */
    private final a f11a = new a();

    /* renamed from: a  reason: collision with other field name */
    private e f12a = null;

    /* renamed from: a  reason: collision with other field name */
    private h f13a = null;

    /* renamed from: a  reason: collision with other field name */
    private BufferedOutputStream f14a = null;

    /* renamed from: a  reason: collision with other field name */
    private BufferedReader f15a = null;

    /* renamed from: a  reason: collision with other field name */
    private InputStream f16a = null;

    /* renamed from: a  reason: collision with other field name */
    private OutputStream f17a = null;

    /* renamed from: a  reason: collision with other field name */
    private PrintWriter f18a = null;
    private BluetoothDevice b = null;
    private int c = 2;

    /* renamed from: c  reason: collision with other field name */
    private boolean f19c = false;
    private boolean f = false;
    private boolean g = false;

    public d(BluetoothAdapter bluetoothAdapter) {
        this.a = bluetoothAdapter;
    }

    public d(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice) {
        this.a = bluetoothAdapter;
        this.b = bluetoothDevice;
        this.f19c = true;
    }

    private BluetoothSocket a() {
        boolean z = true;
        boolean z2 = false;
        UUID fromString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        if (!this.f19c) {
            Set<BluetoothDevice> bondedDevices = this.a.getBondedDevices();
            BluetoothDevice[] bluetoothDeviceArr = (BluetoothDevice[]) bondedDevices.toArray(new BluetoothDevice[bondedDevices.size()]);
            int length = bluetoothDeviceArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = z2;
                    break;
                }
                BluetoothDevice bluetoothDevice = bluetoothDeviceArr[i];
                z2 = Bridge.f7a ? bluetoothDevice.getAddress().equals(Bridge.f8b) : bluetoothDevice.getName().equals(Bridge.f6a);
                if (z2) {
                    this.b = bluetoothDevice;
                    z = z2;
                    break;
                }
                i++;
            }
        }
        if (z || this.f19c) {
            try {
                if (Build.VERSION.SDK_INT >= 10) {
                    j.a();
                    return j.a(this.b, fromString);
                } else if (!this.f) {
                    return this.b.createRfcommSocketToServiceRecord(fromString);
                } else {
                    try {
                        return (BluetoothSocket) this.b.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE}).invoke(this.b, new Object[]{1});
                    } catch (SecurityException e) {
                        Log.v("PLUGIN", e.getMessage());
                        return null;
                    } catch (NoSuchMethodException e2) {
                        Log.v("PLUGIN", e2.getMessage());
                        return null;
                    } catch (IllegalArgumentException e3) {
                        Log.v("PLUGIN", e3.getMessage());
                        return null;
                    } catch (IllegalAccessException e4) {
                        Log.v("PLUGIN", e4.getMessage());
                        return null;
                    } catch (InvocationTargetException e5) {
                        Log.v("PLUGIN", e5.getMessage());
                        return null;
                    }
                }
            } catch (IOException e6) {
                Log.v("PLUGIN", e6.getMessage());
                close();
                Bridge.controlMessage(-1);
                return null;
            }
        } else {
            Bridge.controlMessage(-2);
            return null;
        }
    }

    /* renamed from: a  reason: collision with other method in class */
    public final void m0a() {
        e eVar = this.f12a;
        if (Bridge.f9b) {
            if (!(Bridge.f4a == c.mode0) && eVar.f23a.length != Bridge.b) {
                eVar.f23a = new byte[Bridge.b];
            }
            if (!eVar.f24d) {
                eVar.f20a = new g(eVar, (byte) 0);
                eVar.f20a.start();
            }
        }
    }

    public final void a(int i) {
        this.c = i;
        this.f10a = a();
    }

    public final boolean available() {
        return this.f12a.i;
    }

    public final void close() {
        String str;
        String str2;
        String str3;
        this.f12a.k = true;
        h hVar = this.f13a;
        hVar.l = true;
        hVar.f26a.clear();
        if (this.f16a != null) {
            try {
                this.f16a.close();
            } catch (Exception e) {
                this.f16a = null;
            }
        }
        if (this.f15a != null) {
            try {
                this.f15a.close();
            } catch (Exception e2) {
                this.f15a = null;
            }
        }
        if (this.f17a != null) {
            try {
                this.f17a.flush();
                this.f17a.close();
            } catch (Exception e3) {
                this.f17a = null;
            }
        }
        if (this.f18a != null) {
            try {
                this.f18a.flush();
                this.f18a.close();
            } catch (Exception e4) {
                this.f18a = null;
            }
        }
        if (this.f14a != null) {
            try {
                this.f14a.flush();
                this.f14a.close();
            } catch (Exception e5) {
                this.f14a = null;
            }
        }
        try {
            if (this.f10a != null) {
                this.f10a.close();
                this.f10a = null;
            }
            this.g = false;
            Bridge.controlMessage(2);
        } catch (IOException e6) {
            Bridge.controlMessage(-4);
        } finally {
            str = "BtConnector";
            str2 = "connection";
            str3 = "";
            UnityPlayer.UnitySendMessage(str, str2, str3);
        }
    }

    public final void doneReading() {
        e eVar = this.f12a;
        eVar.i = false;
        eVar.h = false;
    }

    public final boolean isConnected() {
        return this.g;
    }

    public final boolean isListening() {
        a aVar = this.f11a;
        return false;
    }

    public final boolean isSending() {
        a aVar = this.f11a;
        return false;
    }

    public final byte[] readBuffer() {
        e eVar = this.f12a;
        eVar.i = false;
        if (eVar.buffer == null || !eVar.j) {
            return new byte[0];
        }
        if (Bridge.f4a == c.mode1) {
            eVar.d = 0;
        }
        eVar.j = false;
        return eVar.buffer;
    }

    public final String readLine() {
        e eVar = this.f12a;
        eVar.i = false;
        if (eVar.c.length() <= 0) {
            return "";
        }
        String str = eVar.c;
        eVar.c = "";
        eVar.h = false;
        return str;
    }

    public final void run() {
        boolean z;
        boolean z2;
        if (this.f10a != null) {
            int i = 1;
            do {
                this.a.cancelDiscovery();
                try {
                    this.f10a.connect();
                    z = false;
                    z2 = false;
                } catch (IOException e) {
                    Log.v("PLUGIN", "TRYING TO CONNECT AGAIN : " + String.valueOf(this.f) + " : " + e.getMessage());
                    if (i < this.c) {
                        if (this.f10a != null) {
                            try {
                                this.f10a.close();
                            } catch (IOException e2) {
                            }
                            this.f10a = null;
                        }
                        this.g = false;
                        this.f = !this.f;
                        this.f10a = null;
                        this.f10a = a();
                        i++;
                        z = false;
                        z2 = true;
                    } else {
                        close();
                        Bridge.controlMessage(-3);
                        z = true;
                        z2 = false;
                    }
                }
                if (z2) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e3) {
                        Log.v("PLUGIN", "Sleep Thread Interrupt Exception");
                    }
                }
                if (!z2 && !z) {
                    if (Bridge.f9b) {
                        try {
                            this.f16a = this.f10a.getInputStream();
                        } catch (IOException e4) {
                            Log.v("yahya", "inFailed" + e4.getMessage());
                        }
                        this.f15a = new BufferedReader(new InputStreamReader(this.f16a));
                        this.f12a = new e(this.f10a, this.f16a, this.f15a);
                        e eVar = this.f12a;
                        if (!eVar.f24d) {
                            eVar.f20a = new g(eVar, (byte) 0);
                            eVar.f20a.start();
                        }
                    }
                    try {
                        this.f17a = this.f10a.getOutputStream();
                    } catch (IOException e5) {
                        Log.v("yahya", "outFailed" + e5.getMessage());
                    }
                    this.f14a = new BufferedOutputStream(this.f17a);
                    this.f18a = new PrintWriter(this.f17a, true);
                    this.f13a = new h(this.f14a, this.f18a);
                    this.g = true;
                    UnityPlayer.UnitySendMessage("BtConnector", "connection", "1");
                    Bridge.controlMessage(1);
                    continue;
                }
            } while (z2);
        }
    }

    public final void sendBytes(byte[] bArr) {
        h hVar = this.f13a;
        hVar.f26a.add(new Object[]{2, bArr});
        hVar.b();
    }

    public final void sendChar(char c2) {
        h hVar = this.f13a;
        hVar.f26a.add(new Object[]{0, Integer.valueOf(c2)});
        hVar.b();
    }

    public final void sendString(String str) {
        h hVar = this.f13a;
        char[] charArray = str.toCharArray();
        char[] copyOf = Arrays.copyOf(charArray, charArray.length + 1);
        copyOf[charArray.length] = 10;
        hVar.f26a.add(new Object[]{1, copyOf});
        hVar.b();
    }
}
