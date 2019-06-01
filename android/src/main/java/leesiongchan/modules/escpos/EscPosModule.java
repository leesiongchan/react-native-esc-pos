package leesiongchan.modules.escpos;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import io.github.escposjava.PrinterService;
import io.github.escposjava.print.NetworkPrinter;
import io.github.escposjava.print.exceptions.BarcodeSizeError;
import io.github.escposjava.print.exceptions.QRCodeException;

import java.awt.image.BufferedImage;

import static io.github.escposjava.print.Commands.*;

public class EscPosModule extends ReactContextBaseJavaModule {
    private static final String COMMAND_CTL_LF = "COMMAND_CTL_LF";
    private static final String COMMAND_BEEPER = "COMMAND_BEEPER";
    private final ReactApplicationContext reactContext;
    private PrinterService printerService;

    public EscPosModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(COMMAND_CTL_LF, CTL_LF);
        constants.put(COMMAND_BEEPER, BEEPER);
        constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }

    @Override
    public String getName() {
        return "EscPos";
    }

    @ReactMethod
    public boolean connect(String address, int port) {
        Printer printer = new NetworkPrinter(address, port);
        this.printerService = new PrinterService(printer);

        return true;
    }

    @ReactMethod
    public boolean disconnect() {
        this.printerService.close();

        return true;
    }

    @ReactMethod
    public void cutPart() {
        this.printerService.cutPart();
    }

    @ReactMethod
    public void cutFull() {
        this.printerService.cutFull();
    }

    @ReactMethod
    public void lineBreak() {
        this.printerService.lineBreak();
    }

    @ReactMethod
    public void print(String text) {
        this.printerService.print(text);
    }

    @ReactMethod
    public void printLn(String text) {
        this.printerService.printLn(text);
    }

    @ReactMethod
    public void printBarcode(String code, String bc, int width, int height, String pos, String font)
            throws BarcodeSizeError {
        this.printerService.printBarcode(code, bc, width, height, pos, font);
    }

    @ReactMethod
    public void printImage(BufferedImage image) {
        this.printerService.printImage(image);
    }

    @ReactMethod
    public void printQRCode(String value) throws QRCodeException {
        this.printerService.printQRCode(value);
    }

    @ReactMethod
    public void write(byte[] command) {
        this.printerService.write(command);
    }
}
