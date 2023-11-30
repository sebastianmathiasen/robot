package com.badran.bluetoothcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.unity3d.player.UnityPlayer;

final class b extends BroadcastReceiver {
    private b() {
    }

    /* synthetic */ b(byte b) {
        this();
    }

    public final void onReceive(Context context, Intent intent) {
        if ("android.bluetooth.devicepicker.action.DEVICE_SELECTED".equals(intent.getAction())) {
            Bridge.f2a = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            UnityPlayer.UnitySendMessage("BtConnector", "devicePicked", "");
        }
    }
}
