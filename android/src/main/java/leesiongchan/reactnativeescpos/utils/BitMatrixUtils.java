package leesiongchan.reactnativeescpos.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.common.BitMatrix;

// @ref https://gist.github.com/adrianoluis/fa9374d7f2f8ca1115b00cc83cd7aacd
public class BitMatrixUtils {
    public static Bitmap convertToBitmap(BitMatrix data) {
        final int w = data.getWidth();
        final int h = data.getHeight();
        final int[] pixels = new int[w * h];

        for (int y = 0; y < h; y++) {
            final int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = data.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);

        return bitmap;
    }
}
