package leesiongchan.reactnativeescpos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import io.github.escposjava.print.Printer;
import io.github.escposjava.print.exceptions.BarcodeSizeError;
import io.github.escposjava.print.exceptions.QRCodeException;

import leesiongchan.reactnativeescpos.helpers.EscPosHelper;
import leesiongchan.reactnativeescpos.utils.BitMatrixUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static io.github.escposjava.print.Commands.*;

public class PrinterService {
    public static final int PRINTING_WIDTH_58_MM = 384;
    public static final int PRINTING_WIDTH_80_MM = 576;
    private final int DEFAULT_QR_CODE_SIZE = 150;
    private int printingWidth = PRINTING_WIDTH_58_MM;
    private io.github.escposjava.PrinterService basePrinterService;

    public PrinterService(Printer printer) {
        basePrinterService = new io.github.escposjava.PrinterService(printer);
    }

    public PrinterService(Printer printer, int printingWidth) {
        basePrinterService = new io.github.escposjava.PrinterService(printer);
        this.printingWidth = printingWidth;
    }

    public void cutPart() {
        basePrinterService.cutPart();
    }

    public void cutFull() {
        basePrinterService.cutFull();
    }

    public void print(String text) {
        basePrinterService.print(text);
    }

    public void printLn(String text) {
        basePrinterService.printLn(text);
    }

    public void lineBreak() {
        basePrinterService.lineBreak();
    }

    public void lineBreak(int nbLine) {
        basePrinterService.lineBreak(nbLine);
    }

    // TODO This isn't working correctly
    public void printBarcode(String code, String bc, int width, int height, String pos, String font)
            throws BarcodeSizeError {
        basePrinterService.printBarcode(code, bc, width, height, pos, font);
    }

    public void printDemo() {

    }

    public void printImage(String filePath) throws IOException {
        Uri fileUri = Uri.parse(filePath);
        Bitmap image = BitmapFactory.decodeFile(fileUri.getPath());
        printImage(image);
    }

    public void printImage(Bitmap image) throws IOException {
        image = EscPosHelper.resizeImage(image, printingWidth);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(LINE_SPACE_24);
        for (int y = 0; y < image.getHeight(); y += 24) {
            baos.write(SELECT_BIT_IMAGE_MODE); // bit mode
            // width, low & high
            baos.write(new byte[] { (byte) (0x00ff & image.getWidth()), (byte) ((0xff00 & image.getWidth()) >> 8) });
            for (int x = 0; x < image.getWidth(); x++) {
                // For each vertical line/slice must collect 3 bytes (24 bytes)
                baos.write(EscPosHelper.collectImageSlice(y, x, image));
            }
            baos.write(CTL_LF);
        }

        basePrinterService.write(baos.toByteArray());
    }

    public void printQRCode(String value) throws QRCodeException {
        printQRCode(value, DEFAULT_QR_CODE_SIZE);
    }

    public void printQRCode(String value, int size) throws QRCodeException {
        BitMatrix result;

        try {
            result = new QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, size, size, null);
        } catch (IllegalArgumentException | WriterException e) {
            // Unsupported format
            throw new QRCodeException("QRCode generation error", e);
        }

        Bitmap qrcode = BitMatrixUtils.convertToBitmap(result);
        printImage(qrcode);
    }

    public void write(byte[] command) {
        basePrinterService.write(command);
    }

    public void setCharCode(String code) {
        basePrinterService.setCharCode(code);
    }

    public void setPrintingWidth(int printingWidth) {
        this.printingWidth = printingWidth;
    }

    public void beep() {
        basePrinterService.beep();
    }

    public void open() {
        basePrinterService.open();
    }

    public void close() {
        basePrinterService.close();
    }
}
