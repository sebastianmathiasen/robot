package com.badran.bluetoothcontroller;

import android.bluetooth.BluetoothSocket;
import java.io.BufferedReader;
import java.io.InputStream;

final class e {
    BluetoothSocket a;

    /* renamed from: a  reason: collision with other field name */
    g f20a;

    /* renamed from: a  reason: collision with other field name */
    BufferedReader f21a = null;

    /* renamed from: a  reason: collision with other field name */
    InputStream f22a = null;

    /* renamed from: a  reason: collision with other field name */
    byte[] f23a = new byte[0];
    byte[] buffer = new byte[0];
    String c = "";
    volatile int d = 0;

    /* renamed from: d  reason: collision with other field name */
    public volatile boolean f24d = false;
    boolean h = false;
    boolean i = false;
    boolean j = false;
    boolean k = false;

    public e(BluetoothSocket bluetoothSocket, InputStream inputStream, BufferedReader bufferedReader) {
        this.a = bluetoothSocket;
        this.f22a = inputStream;
        this.f21a = bufferedReader;
        this.f23a = new byte[Bridge.b];
    }
}
