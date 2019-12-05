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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import leesiongchan.reactnativeescpos.helpers.EscPosHelper;
import leesiongchan.reactnativeescpos.utils.BitMatrixUtils;
import static io.github.escposjava.print.Commands.*;

public class PrinterService {
    public static final int PRINTING_WIDTH_58_MM = 384;
    public static final int PRINTING_WIDTH_80_MM = 576;
    private static final String CARRIAGE_RETURN = System.getProperty("line.separator");
    private LayoutBuilder layoutBuilder = new LayoutBuilder();
    private final int DEFAULT_QR_CODE_SIZE = 200;
    private int printingWidth = PRINTING_WIDTH_58_MM;
    private io.github.escposjava.PrinterService basePrinterService;

    public PrinterService(Printer printer) throws IOException {
        basePrinterService = new io.github.escposjava.PrinterService(printer);
    }

    public PrinterService(Printer printer, int printingWidth) throws IOException {
        basePrinterService = new io.github.escposjava.PrinterService(printer);
        this.printingWidth = printingWidth;
    }

    public void cutPart() {
        basePrinterService.cutPart();
    }

    public void cutFull() {
        basePrinterService.cutFull();
    }

    public void print(String text) throws UnsupportedEncodingException {
        write(text.getBytes("GBK"));
    }

    public void printLn(String text) throws UnsupportedEncodingException {
        print(text + CARRIAGE_RETURN);
    }

    public void lineBreak() {
        basePrinterService.lineBreak();
    }

    public void lineBreak(int nbLine) {
        basePrinterService.lineBreak(nbLine);
    }

    // TODO: This isn't working correctly
    public void printBarcode(String code, String bc, int width, int height, String pos, String font)
            throws BarcodeSizeError {
        basePrinterService.printBarcode(code, bc, width, height, pos, font);
    }

    public void printSample() throws IOException {
        String design =
            "               ABC Inc. {C}               " + "\n" +
            "           1234 Main Street {C}           " + "\n" +
            "        Anytown, US 12345-6789 {C}        " + "\n" +
            "            (555) 123-4567 {C}            " + "\n" +
            "                                          " + "\n" +
            "          D0004 | Table #: A1 {C}         " + "\n" +
            "------------------------------------------" + "\n" +
            "Item            {<>}    Qty  Price  Amount" + "\n" +
            "Chicken Rice    {<>}      2  12.50   25.00" + "\n" +
            "Coke Zero       {<>}      5   3.00   15.00" + "\n" +
            "Fries           {<>}      3   3.00    9.00" + "\n" +
            "Fresh Oyster    {<>}      1   8.00    8.00" + "\n" +
            "Lobster Roll    {<>}      1  16.50   16.50" + "\n" +
            "------------------------------------------" + "\n" +
            "       {QR[Where are the aliens?]}        " + "\n";

        printDesign(design);
    }

    public void printDesign(String text) throws IOException {
        ByteArrayOutputStream baos = generateDesignByteArrayOutputStream(text);
        write(baos.toByteArray());
    }

    public void printImage(String filePath) throws IOException {
        Uri fileUri = Uri.parse(filePath);
        Bitmap image = BitmapFactory.decodeFile(fileUri.getPath());
        printImage(image);
    }

    public void printImage(Bitmap image) throws IOException {
        image = EscPosHelper.resizeImage(image, printingWidth);
        ByteArrayOutputStream baos = generateImageByteArrayOutputStream(image);
        write(baos.toByteArray());
    }

    public void printQRCode(String value, int size) throws QRCodeException {
        ByteArrayOutputStream baos = generateQRCodeByteArrayOutputStream(value, size);
        write(baos.toByteArray());
    }

    public void write(byte[] command) {
        basePrinterService.write(command);
    }

    public void setCharCode(String code) {
        basePrinterService.setCharCode(code);
    }

    public void setCharsOnLine(int charsOnLine) {
        layoutBuilder.setCharsOnLine(charsOnLine);
    }

    public void setPrintingWidth(int printingWidth) {
        this.printingWidth = printingWidth;
    }

    public void setTextDensity(int density) {
        basePrinterService.setTextDensity(density);
    }

    public void beep() {
        basePrinterService.beep();
    }

    public void open() throws IOException {
        basePrinterService.open();
    }

    public void close() throws IOException {
        basePrinterService.close();
    }

    public void kickCashDrawerPin2(){
        basePrinterService.write(CD_KICK_2);
    }

    public void kickCashDrawerPin5(){
        basePrinterService.write(CD_KICK_5);
    }

    /**
     * DESIGN 1: Order List                       *
     *          D0004 | Table #: A1 {C} {H1}      *
     * ------------------------------------------ *
     * [Dine In] {U} {B}                          *
     * [ ] Espresso {H2}                          *
     *     - No sugar, Regular 9oz, Hot           *
     *                               {H3} {R} x 1 *
     * ------------------------------------------ *
     * [ ] Blueberry Cheesecake {H2}              *
     *     - Slice                                *
     *                               {H3} {R} x 1 *
     *                                            *
     * DESIGN 2: Menu Items                       *
     * ------------------------------------------ *
     * Item         {<>}       Qty  Price  Amount *
     * Pork Rice    {<>}         1  13.80   13.80 *
     *                                            *
     * DESIGN 3: Barcode                          *
     * {QR[Love me, hate me.]} {C}                *
     **/
    private ByteArrayOutputStream generateDesignByteArrayOutputStream(String text) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(text.trim()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.matches("\\{QR\\[(.+)\\]\\}")) {
                try {
                    baos.write(generateQRCodeByteArrayOutputStream(line.replaceAll("\\{QR\\[(.+)\\]\\}", "$1"),
                            DEFAULT_QR_CODE_SIZE).toByteArray());
                } catch (QRCodeException e) {
                    throw new IOException(e);
                }
                continue;
            }

            boolean bold = line.contains("{B}");
            boolean underline = line.contains("{U}");
            boolean h1 = line.contains("{H1}");
            boolean h2 = line.contains("{H2}");
            boolean h3 = line.contains("{H3}");
            boolean lsm = line.contains("{LS:M}");
            boolean lsl = line.contains("{LS:L}");
            boolean ct = line.contains("{C}");
            boolean rt = line.contains("{R}");
            int charsOnLine = layoutBuilder.getCharsOnLine();

            // TODO: Shouldn't put it here
            byte[] ESC_t = new byte[] { 0x1b, 't', 0x00 };
            byte[] ESC_M = new byte[] { 0x1b, 'M', 0x00 };
            byte[] FS_and = new byte[] { 0x1c, '&' };
            byte[] TXT_NORMAL_NEW = new byte[] { 0x1d, '!', 0x00 };
            byte[] TXT_4SQUARE_NEW = new byte[] { 0x1d, '!', 0x11 };
            byte[] TXT_2HEIGHT_NEW = new byte[] { 0x1d, '!', 0x01 };
            byte[] TXT_2WIDTH_NEW = new byte[] { 0x1d, '!', 0x10 };
            byte[] LINE_SPACE_68 = new byte[] { 0x1b, 0x33, 68 };
            byte[] LINE_SPACE_88 = new byte[] { 0x1b, 0x33, 120 };
            byte[] DEFAULT_LINE_SPACE = new byte[] { 0x1b, 50 };

            baos.write(ESC_t);
            baos.write(FS_and);
            baos.write(ESC_M);

            // Add tags
            if (bold) {
                baos.write(TXT_BOLD_ON);
                line = line.replace("{B}", "");
            }
            if (underline) {
                baos.write(TXT_UNDERL_ON);
                line = line.replace("{U}", "");
            }
            if (h1) {
                baos.write(TXT_4SQUARE_NEW);
                // baos.write(LINE_SPACE_88);
                line = line.replace("{H1}", "");
                charsOnLine = charsOnLine / 2;
            } else if (h2) {
                baos.write(TXT_2HEIGHT_NEW);
                // baos.write(LINE_SPACE_88);
                line = line.replace("{H2}", "");
            } else if (h3) {
                baos.write(TXT_2WIDTH_NEW);
                // baos.write(LINE_SPACE_68);
                line = line.replace("{H3}", "");
                charsOnLine = charsOnLine / 2;
            }
            if (lsm) {
                baos.write(LINE_SPACE_24);
                line = line.replace("{LS:M}", "");
            } else if (lsl) {
                baos.write(LINE_SPACE_30);
                line = line.replace("{LS:L}", "");
            }
            if (ct) {
                baos.write(TXT_ALIGN_CT);
                line = line.replace("{C}", "");
            }
            if (rt) {
                baos.write(TXT_ALIGN_RT);
                line = line.replace("{R}", "");
            }

            try {
                baos.write(layoutBuilder.createFromDesign(line, charsOnLine).getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // Do nothing?
            }

            // Remove tags
            if (bold) {
                baos.write(TXT_BOLD_OFF);
            }
            if (underline) {
                baos.write(TXT_UNDERL_OFF);
            }
            if (h1 || h2 || h3) {
                baos.write(DEFAULT_LINE_SPACE);
                baos.write(TXT_NORMAL_NEW);
            }
            if (lsm || lsl) {
                baos.write(LINE_SPACE_24);
            }
            if (ct || rt) {
                baos.write(TXT_ALIGN_LT);
            }
        }

        return baos;
    }

    private ByteArrayOutputStream generateImageByteArrayOutputStream(Bitmap image) throws IOException {
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

        return baos;
    }

    private ByteArrayOutputStream generateQRCodeByteArrayOutputStream(String value, int size) throws QRCodeException {
        try {
            BitMatrix result = new QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, size, size, null);
            Bitmap qrcode = BitMatrixUtils.convertToBitmap(result);
            return generateImageByteArrayOutputStream(qrcode);
        } catch (IllegalArgumentException | WriterException | IOException e) {
            // Unsupported format
            throw new QRCodeException("QRCode generation error", e);
        }
    }
}
