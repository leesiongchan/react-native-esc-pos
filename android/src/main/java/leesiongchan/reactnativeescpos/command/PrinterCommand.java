package leesiongchan.reactnativeescpos.command;

import java.io.UnsupportedEncodingException;

public class PrinterCommand {
    /**
     * getBarCodeCommand
     *
     * @param str              code
     * @param nType            It is between 65 and 73. {65: UPC-A, 66: UPC-E, 67: EAN13, 68: EAN8, 69: CODE39, 70: ITF, 71: CODABAR, 72: CODE93, 73: CODE128}
     * @param nWidthX          width {2-6}
     * @param nHeight          height {0-255}
     * @param nHriFontType     FontA or FontB
     * @param nHriFontPosition code position (0: none, 1: top, 2: bottom, 3: top - bottom)
     * @return
     * @reference
     * http://www.sam4s.co.kr/_common/ac_downFile.asp?f_url=/files/DOWN/2019012393432_1.pdf&f_name=SAM4S%20Printer%20Control%20Command%20Manual%20REV1_8.pdf (Page 71)
     * https://github.com/januslo/react-native-bluetooth-escpos-printer/blob/master/android/src/main/java/cn/jystudio/bluetooth/escpos/command/sdk/PrinterCommand.java
     */
    public static byte[] getBarCodeCommand(String str, int nType, int nWidthX, int nHeight, int nHriFontType, int nHriFontPosition) {

        if (nType < 0x41 | nType > 0x49 | nWidthX < 2 | nWidthX > 6
                | nHeight < 1 | nHeight > 255 | str.length() == 0)
            return null;

        byte[] bCodeData = null;
        try {
            bCodeData = str.getBytes("GBK");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        byte[] command = new byte[bCodeData.length + 16];

        command[0] = 29;
        command[1] = 119;
        command[2] = ((byte) nWidthX);
        command[3] = 29;
        command[4] = 104;
        command[5] = ((byte) nHeight);
        command[6] = 29;
        command[7] = 102;
        command[8] = ((byte) (nHriFontType & 0x01));
        command[9] = 29;
        command[10] = 72;
        command[11] = ((byte) (nHriFontPosition & 0x03));
        command[12] = 29;
        command[13] = 107;
        command[14] = ((byte) nType);
        command[15] = (byte) (byte) bCodeData.length;
        System.arraycopy(bCodeData, 0, command, 16, bCodeData.length);


        return command;
    }
}
