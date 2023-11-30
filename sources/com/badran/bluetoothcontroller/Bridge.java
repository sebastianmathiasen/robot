package com.badran.bluetoothcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import com.unity3d.player.UnityPlayer;

public class Bridge {
    static byte a = 0;

    /* renamed from: a  reason: collision with other field name */
    private static int f0a = 0;

    /* renamed from: a  reason: collision with other field name */
    private static BluetoothAdapter f1a = BluetoothAdapter.getDefaultAdapter();

    /* renamed from: a  reason: collision with other field name */
    public static BluetoothDevice f2a;

    /* renamed from: a  reason: collision with other field name */
    private static b f3a = new b((byte) 0);

    /* renamed from: a  reason: collision with other field name */
    static c f4a = c.mode0;

    /* renamed from: a  reason: collision with other field name */
    private static d f5a = null;

    /* renamed from: a  reason: collision with other field name */
    static String f6a = "HC-05";

    /* renamed from: a  reason: collision with other field name */
    static boolean f7a = false;
    static int b = 0;

    /* renamed from: b  reason: collision with other field name */
    static String f8b = "";

    /* renamed from: b  reason: collision with other field name */
    static boolean f9b = true;
    private static boolean c = false;

    public static String BluetoothDeviceMac(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice.getAddress();
    }

    public static String BluetoothDeviceName(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice.getName();
    }

    public static void askEnableBluetooth() {
        UnityPlayer.currentActivity.startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
    }

    public static boolean available() {
        if (!isConnected()) {
            return false;
        }
        return f5a.available();
    }

    public static void close() {
        if (f5a != null) {
            f5a.interrupt();
            f5a.close();
            f5a = null;
        }
    }

    public static int connect(int i) {
        if (f1a == null) {
            return -2;
        }
        if (!f1a.isEnabled()) {
            return -1;
        }
        if (f5a != null) {
            f5a.interrupt();
            f5a.close();
            f5a = null;
        }
        if (c) {
            f5a = new d(f1a, f2a);
        } else {
            f5a = new d(f1a);
        }
        f5a.a(i);
        f5a.start();
        return 1;
    }

    public static int controlData() {
        return f0a;
    }

    public static synchronized void controlMessage(int i) {
        synchronized (Bridge.class) {
            f0a = i;
        }
    }

    public static boolean disableBluetooth() {
        if (f1a != null) {
            return f1a.disable();
        }
        return false;
    }

    public static void doneReading() {
        if (f5a != null) {
            f5a.doneReading();
        }
    }

    public static boolean enableBluetooth() {
        if (f1a != null) {
            return f1a.enable();
        }
        return false;
    }

    public static BluetoothDevice getPickedDevice() {
        if (f2a != null) {
            return f2a;
        }
        return null;
    }

    public static boolean isBluetoothEnabled() {
        if (f1a != null) {
            return f1a.isEnabled();
        }
        return false;
    }

    public static boolean isConnected() {
        if (f5a == null) {
            return false;
        }
        return f5a.isConnected();
    }

    public static boolean isListening() {
        if (f5a == null) {
            return false;
        }
        return f5a.isListening();
    }

    public static boolean isSending() {
        if (f5a == null) {
            return false;
        }
        return f5a.isSending();
    }

    public static void listen(boolean z) {
        if (f5a != null) {
            f5a.a();
        }
        f9b = z;
        f4a = c.mode0;
    }

    public static void listen(boolean z, int i, byte b2) {
        if (f5a != null) {
            f5a.a();
        }
        b = i;
        a = b2;
        f9b = z;
        f4a = c.mode1;
    }

    public static void listen(boolean z, int i, boolean z2) {
        if (f5a != null) {
            f5a.a();
        }
        b = i;
        f4a = z2 ? c.mode2 : c.mode3;
        f9b = z;
    }

    public static void moduleMac(String str) {
        f7a = true;
        f8b = str;
    }

    public static void moduleName(String str) {
        f7a = false;
        f6a = str;
    }

    public static String read() {
        listen(true);
        return !isConnected() ? "" : f5a.readLine();
    }

    public static byte[] readBuffer() {
        return !isConnected() ? new byte[0] : f5a.readBuffer();
    }

    public static byte[] readBuffer(int i) {
        listen(true, i, false);
        return !isConnected() ? new byte[0] : f5a.readBuffer();
    }

    public static byte[] readBuffer(int i, byte b2) {
        listen(true, i, b2);
        return !isConnected() ? new byte[0] : f5a.readBuffer();
    }

    public static void sendBytes(byte[] bArr) {
        if (isConnected()) {
            f5a.sendBytes(bArr);
        }
    }

    public static void sendChar(char c2) {
        if (isConnected()) {
            f5a.sendChar(c2);
        }
    }

    public static void sendString(String str) {
        if (isConnected()) {
            f5a.sendString(str);
        }
    }

    public static boolean setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null || !(bluetoothDevice instanceof BluetoothDevice)) {
            return false;
        }
        f2a = bluetoothDevice;
        c = true;
        return true;
    }

    public static void showDevices() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
        UnityPlayer.currentActivity.registerReceiver(f3a, intentFilter);
        UnityPlayer.currentActivity.startActivity(new Intent("android.bluetooth.devicepicker.action.LAUNCH").putExtra("android.bluetooth.devicepicker.extra.NEED_AUTH", false).putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 0).setFlags(8388608));
    }

    public static void stopListen() {
        f9b = false;
    }
}
