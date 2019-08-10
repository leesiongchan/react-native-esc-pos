package leesiongchan.reactnativeescpos;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import io.github.escposjava.print.NetworkPrinter;
import io.github.escposjava.print.Printer;
import io.github.escposjava.print.exceptions.BarcodeSizeError;
import io.github.escposjava.print.exceptions.QRCodeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import static io.github.escposjava.print.Commands.*;

public class EscPosModule extends ReactContextBaseJavaModule {
    public static final String PRINTING_SIZE_58_MM = "PRINTING_SIZE_58_MM";
    public static final String PRINTING_SIZE_80_MM = "PRINTING_SIZE_80_MM";
    public static final String BLUETOOTH_CONNECTED = "BLUETOOTH_CONNECTED";
    public static final String BLUETOOTH_DISCONNECTED = "BLUETOOTH_DISCONNECTED";
    private final ReactApplicationContext reactContext;
    private PrinterService printerService;
    private ReadableMap config;

    enum BluetoothEvent {
        CONNECTED, DISCONNECTED, NONE
    }

    public EscPosModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(PRINTING_SIZE_58_MM, PRINTING_SIZE_58_MM);
        constants.put(PRINTING_SIZE_80_MM, PRINTING_SIZE_80_MM);
        constants.put(BLUETOOTH_CONNECTED, BluetoothEvent.CONNECTED.name());
        constants.put(BLUETOOTH_DISCONNECTED, BluetoothEvent.DISCONNECTED.name());
        return constants;
    }

    @Override
    public String getName() {
        return "EscPos";
    }

    @ReactMethod
    public void cutPart(Promise promise) {
        printerService.cutPart();
        promise.resolve(true);
    }

    @ReactMethod
    public void cutFull(Promise promise) {
        printerService.cutFull();
        promise.resolve(true);
    }

    @ReactMethod
    public void lineBreak(Promise promise) {
        printerService.lineBreak();
        promise.resolve(true);
    }

    @ReactMethod
    public void print(String text, Promise promise) {
        printerService.print(text);
        promise.resolve(true);
    }

    @ReactMethod
    public void printLn(String text, Promise promise) {
        printerService.printLn(text);
        promise.resolve(true);
    }

    @ReactMethod
    public void printBarcode(String code, String bc, int width, int height, String pos, String font, Promise promise) {
        try {
            printerService.printBarcode(code, bc, width, height, pos, font);
            promise.resolve(true);
        } catch (BarcodeSizeError e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void printDesign(String text, Promise promise) {
        try {
            printerService.printDesign(text);
            promise.resolve(true);
        } catch (IOException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void printImage(String filePath, Promise promise) {
        try {
            printerService.printImage(filePath);
            promise.resolve(true);
        } catch (IOException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void printQRCode(String value, Promise promise) {
        try {
            printerService.printQRCode(value);
            promise.resolve(true);
        } catch (QRCodeException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void printSample(Promise promise) {
        try {
            printerService.printSample();
            promise.resolve(true);
        } catch (IOException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void write(byte[] command, Promise promise) {
        printerService.write(command);
        promise.resolve(true);
    }

    @ReactMethod
    public void setCharCode(String code) {
        printerService.setCharCode(code);
    }

    @ReactMethod
    public void setPrintingSize(String printingSize) {
        int charsOnLine;
        int printingWidth;

        switch (printingSize) {
        case PRINTING_SIZE_80_MM:
            charsOnLine = LayoutBuilder.CHARS_ON_LINE_80_MM;
            printingWidth = PrinterService.PRINTING_WIDTH_80_MM;
            break;

        case PRINTING_SIZE_58_MM:
        default:
            charsOnLine = LayoutBuilder.CHARS_ON_LINE_58_MM;
            printingWidth = PrinterService.PRINTING_WIDTH_58_MM;
        }

        printerService.setCharsOnLine(charsOnLine);
        printerService.setPrintingWidth(printingWidth);
    }

    @ReactMethod
    public void beep(Promise promise) {
        printerService.beep();
        promise.resolve(true);
    }

    @ReactMethod
    public void setConfig(ReadableMap config) {
        this.config = config;
    }

    @ReactMethod
    public void connect(String address, Promise promise) {
        if (config.getString("type") != "bluetooth") {
            promise.reject("config.type is not a bluetooth type");
        }
        Printer printer = new BluetoothPrinter(address);
        printerService = new PrinterService(printer);
        promise.resolve(true);
    }

    @ReactMethod
    public void connect(String address, int port, Promise promise) {
        if (config.getString("type") != "network") {
            promise.reject("config.type is not a network type");
        }
        Printer printer = new NetworkPrinter(address, port);
        printerService = new PrinterService(printer);
        promise.resolve(true);
    }

    @ReactMethod
    public void disconnect(Promise promise) {
        printerService.close();
        promise.resolve(true);
    }

    @ReactMethod
    public void initBluetoothConnectionListener() {
        // Add listener when bluetooth conencted
        reactContext.registerReceiver(bluetoothConnectionEventListener,
                new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));

        // Add listener when bluetooth disconnected
        reactContext.registerReceiver(bluetoothConnectionEventListener,
                new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    /**
     * Bluetooth Connection Event Listener
     */
    private BroadcastReceiver bluetoothConnectionEventListener = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String callbackAction = intent.getAction();
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // if action or bluetooth data is null
            if (callbackAction == null || bluetoothDevice == null) {
                // do not proceed
                return;
            }

            // hold value for bluetooth event
            BluetoothEvent bluetoothEvent;

            switch (callbackAction) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                bluetoothEvent = BluetoothEvent.CONNECTED;
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                bluetoothEvent = BluetoothEvent.DISCONNECTED;
                break;
                
            default:
                bluetoothEvent = BluetoothEvent.NONE;
            }

            // bluetooth event must not be null
            if (bluetoothEvent != BluetoothEvent.NONE) {
                // extract bluetooth device info and put in deviceInfoParams
                WritableMap deviceInfoParams = Arguments.createMap();
                deviceInfoParams.putString("name", bluetoothDevice.getName());
                deviceInfoParams.putString("macAddress", bluetoothDevice.getAddress());

                // put deviceInfoParams into callbackParams
                WritableMap callbackParams = Arguments.createMap();
                callbackParams.putMap("deviceInfo", deviceInfoParams);
                callbackParams.putString("state", bluetoothEvent.name());

                // emit callback to RN code
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("bluetoothStateChanged", callbackParams);
            }
        }
    };
}
