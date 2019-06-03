package leesiongchan.reactnativeescpos.helpers;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

// @ref https://gist.github.com/douglasjunior/dc3b41908514304f694f1b37cadf2df7
public class EscPosHelper {
    /**
     * Collect a slice of 3 bytes with 24 dots for image printing.
     *
     * @param y     row position of the pixel.
     * @param x     column position of the pixel.
     * @param image 2D array of pixels of the image (RGB, row major order).
     * @return 3 byte array with 24 dots (field set).
     */
    public static byte[] collectImageSlice(int y, int x, Bitmap image) {
        byte[] slices = new byte[] { 0, 0, 0 };
        for (int yy = y, i = 0; yy < y + 24 && i < 3; yy += 8, i++) { // repeat for 3 cycles
            byte slice = 0;
            for (int b = 0; b < 8; b++) {
                int yyy = yy + b;
                if (yyy >= image.getHeight()) {
                    continue;
                }
                int color = image.getPixel(x, yyy);
                boolean v = shouldPrintColor(color);
                slice |= (byte) ((v ? 1 : 0) << (7 - b));
            }
            slices[i] = slice;
        }

        return slices;
    }

    /**
     * Resizes a Bitmap image.
     * 
     * @param image
     * @param width
     * @return new Bitmap image.
     */
    public static Bitmap resizeImage(Bitmap image, int width) {
        int origWidth = image.getWidth();
        int origHeight = image.getHeight();

        final int destWidth = width;

        if (origWidth > destWidth) {
            // picture is wider than we want it, we calculate its target height
            int destHeight = origHeight / (origWidth / destWidth);
            // we create an scaled bitmap so it reduces the image, not just trim it
            Bitmap newImage = Bitmap.createScaledBitmap(image, destWidth, destHeight, false);

            return newImage;
        }

        return image;
    }

    /**
     * Defines if a color should be printed (burned).
     *
     * @param color RGB color.
     * @return true if should be printed/burned (black), false otherwise (white).
     */
    public static boolean shouldPrintColor(int color) {
        final int threshold = 127;
        int a, r, g, b, luminance;
        a = (color >> 24) & 0xff;
        if (a != 0xff) { // ignore pixels with alpha channel
            return false;
        }
        r = (color >> 16) & 0xff;
        g = (color >> 8) & 0xff;
        b = color & 0xff;

        luminance = (int) (0.299 * r + 0.587 * g + 0.114 * b);

        return luminance < threshold;
    }
}
