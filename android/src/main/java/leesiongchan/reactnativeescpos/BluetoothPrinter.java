package io.github.escposjava.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import io.github.escposjava.Printer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothPrinter implements Printer {
    // TODO: should this be hardcoded?
    private static final UUID APP_UUID = UUID.fromString('16d478c4-1144-4428-80e3-26b34ff382f9');
    private BluetoothAdapter adapter;

    private OutputStream printer  = null;
    private final String address;

   public BluetoothPrinter(String address) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        this.address = address;
   }

    public void open() {
        try {
            BluetoothDevice device = adapter.getRemoteDevice(address);
            BluetoothSocket socket = createRfcommSocketToServiceRecord(APP_UUID);
            printer = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void write(byte[] command) {
        try {
            printer.write(command);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

   public void close(){
        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
}
