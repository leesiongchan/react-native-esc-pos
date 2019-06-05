package leesiongchan.reactnativeescpos;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import io.github.escposjava.print.NetworkPrinter;
import io.github.escposjava.print.Printer;
import io.github.escposjava.print.exceptions.BarcodeSizeError;
import io.github.escposjava.print.exceptions.QRCodeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.github.escposjava.print.Commands.*;

public class EscPosModule extends ReactContextBaseJavaModule {
    public static final String PRINTING_SIZE_58_MM = "PRINTING_SIZE_58_MM";
    public static final String PRINTING_SIZE_80_MM = "PRINTING_SIZE_80_MM";
    private final ReactApplicationContext reactContext;
    private PrinterService printerService;

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
    public void cutPart() {
        printerService.cutPart();
    }

    @ReactMethod
    public void cutFull() {
        printerService.cutFull();
    }

    @ReactMethod
    public void lineBreak() {
        printerService.lineBreak();
    }

    @ReactMethod
    public void print(String text) {
        printerService.print(text);
    }

    @ReactMethod
    public void printLn(String text) {
        printerService.printLn(text);
    }

    @ReactMethod
    public void printBarcode(String code, String bc, int width, int height, String pos, String font)
            throws BarcodeSizeError {
        printerService.printBarcode(code, bc, width, height, pos, font);
    }
    
    @ReactMethod
    public void printDesign(String text) throws IOException {
        printerService.printDesign(text);
    }

    @ReactMethod
    public void printImage(String filePath) throws IOException {
        printerService.printImage(filePath);
    }

    @ReactMethod
    public void printQRCode(String value) throws QRCodeException {
        printerService.printQRCode(value);
    }

    @ReactMethod
    public void printSample() {
        printerService.printSample();
    }

    @ReactMethod
    public void write(byte[] command) {
        printerService.write(command);
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
    public void beep() {
        printerService.beep();
    }

    @ReactMethod
    public boolean connect(String address, int port) {
        Printer printer = new NetworkPrinter(address, port);
        printerService = new PrinterService(printer);

        return true;
    }

    @ReactMethod
    public boolean disconnect() {
        printerService.close();

        return true;
    }
}
