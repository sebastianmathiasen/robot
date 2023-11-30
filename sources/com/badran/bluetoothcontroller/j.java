package com.badran.bluetoothcontroller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

public final class j {
    private static j a = null;

    protected j() {
    }

    public static BluetoothSocket a(BluetoothDevice bluetoothDevice, UUID uuid) {
        try {
            return bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.v("PLUGIN", "Chinese connection failed");
            return null;
        }
    }

    public static j a() {
        if (a == null) {
            a = new j();
        }
        return a;
    }
}
