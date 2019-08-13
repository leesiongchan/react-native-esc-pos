package leesiongchan.reactnativeescpos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScanManager {

    private static final String LOG_TAG = ScanManager.class.getSimpleName();
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private OnBluetoothScanListener onBluetoothScanListener;

    public interface OnBluetoothScanListener {
        void deviceFound(BluetoothDevice bluetoothDevice);
    }

    public ScanManager(Context context, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    /**
     * Start Scanning for discoverable devices
     */
    public void startScan() {
        Log.d(LOG_TAG, "Start Scan.");

        if (onBluetoothScanListener == null) {
            Log.e(LOG_TAG, "You must call registerCallback(...) first!");
        }

        bluetoothAdapter.startDiscovery();
    }

    /**
     * To Stop Scanning process
     */
    public void stopScan() {
        Log.d(LOG_TAG, "Stop Scan.");
        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * Register Broadcast Receiver that will listen to ACTION_FOUND
     *
     * @param onBluetoothScanListener user's callback implementation
     */
    public void registerCallback(OnBluetoothScanListener onBluetoothScanListener) {
        Log.d(LOG_TAG, "Register Callback");

        this.onBluetoothScanListener = onBluetoothScanListener;
        IntentFilter intentFilterConnectionState = new IntentFilter();
        intentFilterConnectionState.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilterConnectionState.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilterConnectionState.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(broadcastReceiver, intentFilterConnectionState);
    }

    /**
     * You must call this in OnDestroy() to unregister broadcast receiver
     */
    public void unregisterCallback() {
        Log.d(LOG_TAG, "Unregister Callback");
        context.unregisterReceiver(broadcastReceiver);
    }

    /**
     * Broadcast Receiver that will receive ACTiON_FOUND and returned with Found
     * Bluetooth Devices
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            // Action must not be null
            // Action must equals to ACTION_FOUND
            if (intent.getAction() != null && BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                // Extract BluetoothDevice found
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Check if bluetoothDevice is null
                if (bluetoothDevice != null) {
                    // Callback with device found
                    onBluetoothScanListener.deviceFound(bluetoothDevice);
                }
            }
        }
    };
}
