package leesiongchan.reactnativeescpos;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import io.github.escposjava.print.NetworkPrinter;
import io.github.escposjava.print.Printer;
import io.github.escposjava.print.exceptions.BarcodeSizeError;
import io.github.escposjava.print.exceptions.QRCodeException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import static io.github.escposjava.print.Commands.*;

public class EscPosModule extends ReactContextBaseJavaModule {
    public static final String PRINTING_SIZE_58_MM = "PRINTING_SIZE_58_MM";
    public static final String PRINTING_SIZE_80_MM = "PRINTING_SIZE_80_MM";
    private final ReactApplicationContext reactContext;
    private PrinterService printerService;
    private ReadableMap config;

    public EscPosModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(PRINTING_SIZE_58_MM, PRINTING_SIZE_58_MM);
        constants.put(PRINTING_SIZE_80_MM, PRINTING_SIZE_80_MM);
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
        try {
            printerService.print(text);
            promise.resolve(true);
        } catch (UnsupportedEncodingException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void printLn(String text, Promise promise) {
        try {
            printerService.printLn(text);
            promise.resolve(true);
        } catch (UnsupportedEncodingException e) {
            promise.reject(e);
        }
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
    public void connectBluetoothPrinter(String address, Promise promise) {
        if (!"bluetooth".equals(config.getString("type"))) {
            promise.reject("config.type is not a bluetooth type");
        }
        Printer printer = new BluetoothPrinter(address);
        printerService = new PrinterService(printer);
        promise.resolve(true);
    }

    @ReactMethod
    public void connectNetworkPrinter(String address, int port, Promise promise) {
        if (!"network".equals(config.getString("type"))) {
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
}
