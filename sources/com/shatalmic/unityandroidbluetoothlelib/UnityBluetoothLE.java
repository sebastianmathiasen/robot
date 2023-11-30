package com.shatalmic.unityandroidbluetoothlelib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class UnityBluetoothLE {
    protected static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final String TAG = "UnityBluetoothLE";
    public static UnityBluetoothLE _instance;
    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.UUID".equals(intent.getAction())) {
                UnityBluetoothLE.this.androidBluetoothLog("got action_uuid");
                if (UnityBluetoothLE.this.uuidList != null) {
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    Parcelable[] uuidExtra = intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
                    if (device == null || uuidExtra == null) {
                        if (device != null) {
                            UnityBluetoothLE.this.androidBluetoothLog("device: " + device.getAddress());
                        } else {
                            UnityBluetoothLE.this.androidBluetoothLog("device is null");
                        }
                        if (uuidExtra != null) {
                            UnityBluetoothLE.this.androidBluetoothLog("uuid count: " + uuidExtra.length);
                        } else {
                            UnityBluetoothLE.this.androidBluetoothLog("uuidExtra is null");
                        }
                    } else {
                        for (Parcelable uuidParcel : uuidExtra) {
                            String uuid = uuidParcel.toString();
                            UnityBluetoothLE.this.androidBluetoothLog("checking uuid " + uuid);
                            if (UnityBluetoothLE.this.uuidList.contains(UnityBluetoothLE.this.getFullBluetoothLEUUID(uuid))) {
                                UnityBluetoothLE.this.sendDiscoveredDevice(device, 0, (byte[]) null);
                            }
                        }
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public Map<String, BluetoothGatt> deviceGattMap = null;
    private Map<String, BluetoothDevice> deviceMap = null;
    /* access modifiers changed from: private */
    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    /* access modifiers changed from: private */
    public Context mContext = null;
    /* access modifiers changed from: private */
    public final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            UnityBluetoothLE.this.androidBluetoothLog("onConnectionStateChange");
            String deviceAddress = gatt.getDevice().getAddress();
            if (newState == 2) {
                if (UnityBluetoothLE.this.deviceGattMap == null) {
                    UnityBluetoothLE.this.deviceGattMap = new HashMap();
                }
                UnityBluetoothLE.this.deviceGattMap.put(deviceAddress, gatt);
                UnityBluetoothLE.UnitySend("ConnectedPeripheral~" + deviceAddress);
                gatt.discoverServices();
            } else if (newState == 0) {
                gatt.close();
                UnityBluetoothLE.UnitySend("DisconnectedPeripheral~" + deviceAddress);
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == 0) {
                UnityBluetoothLE.this.androidBluetoothLog("Services Discovered");
                List<BluetoothGattService> services = gatt.getServices();
                if (services != null) {
                    String deviceAddress = gatt.getDevice().getAddress();
                    for (BluetoothGattService service : services) {
                        UnityBluetoothLE.UnitySend("DiscoveredService~" + deviceAddress + "~" + service.getUuid());
                        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            UnityBluetoothLE.UnitySend("DiscoveredCharacteristic~" + deviceAddress + "~" + service.getUuid() + "~" + characteristic.getUuid());
                        }
                    }
                    return;
                }
                return;
            }
            UnityBluetoothLE.this.androidBluetoothLog("Error~Service Discovery " + status);
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte[] data;
            if (status == 0 && (data = characteristic.getValue()) != null) {
                String deviceAddress = gatt.getDevice().getAddress();
                UnityBluetoothLE.UnitySend("DidUpdateValueForCharacteristic~" + deviceAddress + "~" + characteristic.getUuid() + "~" + Base64.encodeToString(data, 0));
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            if (data != null) {
                String deviceAddress = gatt.getDevice().getAddress();
                UnityBluetoothLE.UnitySend("DidUpdateValueForCharacteristic~" + deviceAddress + "~" + characteristic.getUuid() + "~" + Base64.encodeToString(data, 0));
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                UnityBluetoothLE.UnitySend("DidWriteCharacteristic~" + characteristic.getUuid());
            } else {
                UnityBluetoothLE.UnitySend("Error~Response - failed to write characteristic: " + status);
            }
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == 0) {
                UnityBluetoothLE.this.androidBluetoothLog("onDescriptorWrite Success");
                if (descriptor == null || descriptor.getCharacteristic() == null || descriptor.getCharacteristic().getUuid() == null) {
                    UnityBluetoothLE.UnitySend("Error~Descriptor Write Failed: characterstic or uuid blank");
                    return;
                }
                UnityBluetoothLE.UnitySend("DidUpdateNotificationStateForCharacteristic~" + gatt.getDevice().getAddress() + "~" + descriptor.getCharacteristic().getUuid());
                return;
            }
            UnityBluetoothLE.UnitySend("Error~Descriptor Write Failed: " + status);
        }
    };
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            UnityBluetoothLE.this.sendDiscoveredDevice(device, rssi, scanRecord);
        }
    };
    public AtomicBoolean mRunning = new AtomicBoolean();
    /* access modifiers changed from: private */
    public ArrayList<UUID> uuidList = null;

    public static UnityBluetoothLE getInstance() {
        if (_instance == null) {
            _instance = new UnityBluetoothLE();
        }
        return _instance;
    }

    public static void UnitySend(String message) {
        UnityPlayer.UnitySendMessage("BluetoothLEReceiver", "OnBluetoothMessage", message);
    }

    public static void UnitySend(byte[] data, int length) {
        UnityPlayer.UnitySendMessage("BluetoothLEReceiver", "OnBluetoothData", Base64.encodeToString(Arrays.copyOfRange(data, 0, length), 0));
    }

    /* access modifiers changed from: private */
    public UUID getFullBluetoothLEUUID(String uuidString) {
        if (uuidString.length() == 4) {
            return UUID.fromString("0000" + uuidString + "-0000-1000-8000-00805F9B34FB");
        }
        return UUID.fromString(uuidString);
    }

    public void androidBluetoothInitialize(boolean asCentral, boolean asPeripheral) {
        Log.d(TAG, "androidBluetoothInitialize");
        this.mContext = UnityPlayer.currentActivity.getApplicationContext();
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            UnitySend("Error~Bluetooth Low Energy Not Available");
            return;
        }
        this.uuidList = null;
        this.deviceMap = null;
        this.deviceGattMap = null;
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.device.action.UUID");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.mContext.registerReceiver(this.ActionFoundReceiver, filter);
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
            if (bluetoothManager != null) {
                this.mBluetoothAdapter = bluetoothManager.getAdapter();
                if (this.mBluetoothAdapter == null) {
                    onDestroy();
                } else if (this.mBluetoothAdapter.isEnabled()) {
                    UnitySend("Initialized");
                } else {
                    UnitySend("Error~Bluetooth LE Not Enabled");
                }
            } else {
                onDestroy();
            }
        } else {
            UnitySend("Error~Bluetooth LE Not Available");
        }
    }

    public void androidBluetoothLog(String message) {
        Log.i(TAG, message);
    }

    public void androidBluetoothDeInitialize() {
        Log.d(TAG, "androidBluetoothDeInitialize");
        onDestroy();
        UnitySend("DeInitialized");
    }

    public void onDestroy() {
        if (this.mContext != null) {
            this.mContext.unregisterReceiver(this.ActionFoundReceiver);
            this.uuidList = null;
            this.deviceMap = null;
            this.deviceGattMap = null;
        }
    }

    public class AdRecord {
        public byte[] Data;
        public int Length;
        public int Type;

        public AdRecord(int length, int type, byte[] data) {
            this.Length = length;
            this.Type = type;
            this.Data = data;
        }

        public String decodeRecord() {
            try {
                return new String(this.Data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public class AdRecordList {
        List<AdRecord> Records = new ArrayList();

        public AdRecordList() {
        }

        public List<AdRecord> parseScanRecord(byte[] scanRecord) {
            int index = 0;
            while (true) {
                if (index >= scanRecord.length) {
                    break;
                }
                int index2 = index + 1;
                byte length = scanRecord[index];
                if (length == 0) {
                    int i = index2;
                    break;
                }
                byte type = scanRecord[index2];
                if (type == 0) {
                    int i2 = index2;
                    break;
                }
                this.Records.add(new AdRecord(length, type, Arrays.copyOfRange(scanRecord, index2 + 1, index2 + length)));
                index = index2 + length;
            }
            return this.Records;
        }

        public AdRecord getRecord(int type) {
            for (AdRecord record : this.Records) {
                if (record.Type == type) {
                    return record;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void sendDiscoveredDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String name = "No Name";
        if (device.getName() != null) {
            name = device.getName();
        }
        if (this.deviceMap == null) {
            this.deviceMap = new HashMap();
        }
        this.deviceMap.put(device.getAddress(), device);
        if (scanRecord != null) {
            AdRecordList recordList = new AdRecordList();
            recordList.parseScanRecord(scanRecord);
            AdRecord record = recordList.getRecord(-1);
            if (record != null) {
                UnitySend("DiscoveredPeripheral~" + device.getAddress() + "~" + name + "~" + Integer.valueOf(rssi) + "~" + Base64.encodeToString(record.Data, 0));
            } else {
                UnitySend("DiscoveredPeripheral~" + device.getAddress() + "~" + name);
            }
        } else {
            UnitySend("DiscoveredPeripheral~" + device.getAddress() + "~" + name);
        }
    }

    public void androidBluetoothPause(boolean paused) {
    }

    public void androidBluetoothScanForPeripheralsWithServices(String serviceUUIDsString) {
        if (this.mBluetoothAdapter != null) {
            ArrayList<UUID> uuidList2 = new ArrayList<>();
            if (serviceUUIDsString != null) {
                if (serviceUUIDsString.contains("|")) {
                    String[] serviceUUIDs = serviceUUIDsString.split("|");
                    if (serviceUUIDs != null && serviceUUIDs.length > 0) {
                        for (String fullBluetoothLEUUID : serviceUUIDs) {
                            uuidList2.add(getFullBluetoothLEUUID(fullBluetoothLEUUID));
                        }
                    }
                } else if (serviceUUIDsString.length() > 0) {
                    uuidList2.add(getFullBluetoothLEUUID(serviceUUIDsString));
                }
            }
            if (uuidList2.size() > 0) {
                UUID[] uuids = (UUID[]) uuidList2.toArray(new UUID[uuidList2.size()]);
                for (int i = 0; i < uuids.length; i++) {
                    androidBluetoothLog("Scanning for: " + uuids[i].toString());
                }
                this.mBluetoothAdapter.startLeScan(uuids, this.mLeScanCallback);
                return;
            }
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
        }
    }

    public void androidBluetoothStopScan() {
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }
    }

    public void androidBluetoothRetrieveListOfPeripheralsWithServices(String serviceUUIDsString) {
        if (this.mBluetoothAdapter != null) {
            this.uuidList = new ArrayList<>();
            if (serviceUUIDsString.contains("|")) {
                String[] serviceUUIDs = serviceUUIDsString.split("|");
                if (serviceUUIDs != null && serviceUUIDs.length > 0) {
                    for (String fullBluetoothLEUUID : serviceUUIDs) {
                        this.uuidList.add(getFullBluetoothLEUUID(fullBluetoothLEUUID));
                    }
                }
            } else if (serviceUUIDsString.length() > 0) {
                this.uuidList.add(getFullBluetoothLEUUID(serviceUUIDsString));
            }
            androidBluetoothLog("getting bonded devices");
            UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Set<BluetoothDevice> deviceSet = UnityBluetoothLE.this.mBluetoothAdapter.getBondedDevices();
                    if (deviceSet != null && deviceSet.size() > 0) {
                        for (BluetoothDevice device : deviceSet) {
                            if (device != null) {
                                UnityBluetoothLE.this.androidBluetoothLog("got device " + device.getAddress());
                                if (UnityBluetoothLE.this.uuidList.size() > 0) {
                                    device.fetchUuidsWithSdp();
                                } else {
                                    UnityBluetoothLE.this.sendDiscoveredDevice(device, 0, (byte[]) null);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000c, code lost:
        r0 = r3.deviceMap.get(r4);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void androidBluetoothConnectToPeripheral(java.lang.String r4) {
        /*
            r3 = this;
            android.bluetooth.BluetoothAdapter r1 = r3.mBluetoothAdapter
            if (r1 == 0) goto L_0x0020
            java.util.Map<java.lang.String, android.bluetooth.BluetoothDevice> r1 = r3.deviceMap
            if (r1 == 0) goto L_0x0020
            android.content.Context r1 = r3.mContext
            if (r1 == 0) goto L_0x0020
            java.util.Map<java.lang.String, android.bluetooth.BluetoothDevice> r1 = r3.deviceMap
            java.lang.Object r0 = r1.get(r4)
            android.bluetooth.BluetoothDevice r0 = (android.bluetooth.BluetoothDevice) r0
            if (r0 == 0) goto L_0x0020
            android.app.Activity r1 = com.unity3d.player.UnityPlayer.currentActivity
            com.shatalmic.unityandroidbluetoothlelib.UnityBluetoothLE$5 r2 = new com.shatalmic.unityandroidbluetoothlelib.UnityBluetoothLE$5
            r2.<init>(r0)
            r1.runOnUiThread(r2)
        L_0x0020:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shatalmic.unityandroidbluetoothlelib.UnityBluetoothLE.androidBluetoothConnectToPeripheral(java.lang.String):void");
    }

    public void androidBluetoothDisconnectPeripheral(String name) {
        BluetoothGatt gatt;
        if (this.mBluetoothAdapter != null && this.deviceGattMap != null && this.mContext != null && (gatt = this.deviceGattMap.get(name)) != null) {
            gatt.disconnect();
        }
    }

    /* access modifiers changed from: protected */
    public UUID getUUID(String uuidString) {
        if (uuidString.length() == 36) {
            return UUID.fromString(uuidString);
        }
        if (uuidString.length() > 8) {
            return null;
        }
        StringBuilder newString = new StringBuilder();
        newString.append("00000000", 0, 8 - uuidString.length());
        newString.append(uuidString);
        newString.append("-0000-1000-8000-00805f9b34fb");
        return UUID.fromString(newString.toString());
    }

    public void androidReadCharacteristic(String name, String serviceString, String characteristicString) {
        BluetoothGatt gatt;
        if (this.mBluetoothAdapter != null && this.deviceGattMap != null && this.mContext != null && (gatt = this.deviceGattMap.get(name)) != null) {
            UUID serviceUUID = getUUID(serviceString);
            if (serviceUUID != null) {
                BluetoothGattService service = gatt.getService(serviceUUID);
                if (service != null) {
                    UUID characteristicUUID = getUUID(characteristicString);
                    if (characteristicUUID != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                        if (characteristic == null) {
                            UnitySend("Error~Characteristic not found for Read");
                        } else if (!gatt.readCharacteristic(characteristic)) {
                            UnitySend("Error~Failed to read characteristic");
                        }
                    } else {
                        UnitySend("Error~Not a Valid Characteristic UUID for Read");
                    }
                } else {
                    UnitySend("Error~Service not found for Read");
                }
            } else {
                UnitySend("Error~Not a Valid Service UUID for Read");
            }
        }
    }

    public void androidWriteCharacteristic(String name, String serviceString, String characteristicString, byte[] data, int length, boolean withResponse) {
        BluetoothGatt gatt;
        if (this.mBluetoothAdapter != null && this.deviceGattMap != null && this.mContext != null && (gatt = this.deviceGattMap.get(name)) != null) {
            UUID serviceUUID = getUUID(serviceString);
            if (serviceUUID != null) {
                BluetoothGattService service = gatt.getService(serviceUUID);
                if (service != null) {
                    UUID characteristicUUID = getUUID(characteristicString);
                    if (characteristicUUID != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                        if (characteristic != null) {
                            androidBluetoothLog("write characteristic");
                            characteristic.setValue(data);
                            characteristic.setWriteType(withResponse ? 2 : 1);
                            if (!gatt.writeCharacteristic(characteristic)) {
                                UnitySend("Error~Failed to write characteristic");
                                return;
                            }
                            return;
                        }
                        UnitySend("Error~Characteristic not found for Write");
                        return;
                    }
                    UnitySend("Error~Not a Valid Characteristic UUID for Write");
                    return;
                }
                UnitySend("Error~Service not found for Write");
                return;
            }
            UnitySend("Error~Not a Valid Service UUID for Write");
        }
    }

    /* access modifiers changed from: protected */
    public void androidSetCharacteristicNotification(String name, String serviceString, String characteristicString, boolean enabled) {
        BluetoothGatt gatt;
        if (this.mBluetoothAdapter != null && this.deviceGattMap != null && this.mContext != null && (gatt = this.deviceGattMap.get(name)) != null) {
            UUID serviceUUID = getUUID(serviceString);
            if (serviceUUID != null) {
                BluetoothGattService service = gatt.getService(serviceUUID);
                if (service != null) {
                    UUID characteristicUUID = getUUID(characteristicString);
                    if (characteristicUUID != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                        if (characteristic == null) {
                            UnitySend("Error~Characteristic not found for Subscribe");
                        } else if (gatt.setCharacteristicNotification(characteristic, true)) {
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                            if (descriptor != null) {
                                int characteristicProperties = characteristic.getProperties();
                                byte[] valueToSend = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                                if (enabled) {
                                    if ((characteristicProperties & 16) == 16) {
                                        valueToSend = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                                    } else if ((characteristicProperties & 32) == 32) {
                                        valueToSend = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
                                    }
                                }
                                if (valueToSend.length > 0) {
                                    androidBluetoothLog("Notification Description: " + valueToSend[0]);
                                    descriptor.setValue(valueToSend);
                                    if (!gatt.writeDescriptor(descriptor)) {
                                        UnitySend("Error~Failed to write characteristic descriptor");
                                    } else {
                                        androidBluetoothLog("Notification setup succeeded");
                                    }
                                } else {
                                    UnitySend("Error~Failed to set notification type");
                                }
                            } else {
                                UnitySend("Error~Failed to get notification descriptor");
                            }
                        } else {
                            UnitySend("Error~Failed to set characteristic notification");
                        }
                    } else {
                        UnitySend("Error~Not a Valid Characteristic UUID for Subscribe");
                    }
                } else {
                    UnitySend("Error~Service not found for Subscribe");
                }
            } else {
                UnitySend("Error~Not a Valid Service UUID for Subscribe");
            }
        }
    }

    public void androidSubscribeCharacteristic(String name, String serviceString, String characteristicString) {
        androidBluetoothLog("subscribe characteristic");
        androidSetCharacteristicNotification(name, serviceString, characteristicString, true);
    }

    public void androidUnsubscribeCharacteristic(String name, String serviceString, String characteristicString) {
        androidBluetoothLog("unsubscribe characteristic");
        androidSetCharacteristicNotification(name, serviceString, characteristicString, false);
    }
}
