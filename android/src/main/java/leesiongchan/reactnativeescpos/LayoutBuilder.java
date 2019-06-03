package leesiongchan.reactnativeescpos;

import org.apache.commons.lang3.StringUtils;

// @ref https://github.com/LeeryBit/esc-pos-android/blob/master/library/src/main/java/com/leerybit/escpos/Ticket.java
public class LayoutBuilder {
    public static final String TEXT_ALIGNMENT_LEFT = "LEFT";
    public static final String TEXT_ALIGNMENT_CENTER = "CENTER";
    public static final String TEXT_ALIGNMENT_RIGHT = "RIGHT";
    public static final int CHARS_ON_LINE_58_MM = 32;
    public static final int CHARS_ON_LINE_80_MM = 48;
    private int charsOnLine = CHARS_ON_LINE_58_MM;

    LayoutBuilder(int charsOnLine) {
        this.charsOnLine = charsOnLine;
    }

    public String createAccent(String text, char accent) {
        if (text.length() - 4 > charsOnLine) {
            accent = ' ';
        }

        return createTextOnLine(' ' + text + ' ', accent, TEXT_ALIGNMENT_CENTER);
    }

    public String createDivider() {
        return createDivider('-');
    }

    public String createDivider(char symbol) {
        return StringUtils.repeat(symbol, charsOnLine) + "\n";
    }

    public String createMenuItem(String key, String value, char space) {
        if (key.length() + value.length() + 2 > charsOnLine) {
            return createTextOnLine(key + ": " + value, ' ', TEXT_ALIGNMENT_LEFT);
        }

        return StringUtils.rightPad(key, charsOnLine - value.length(), space) + value + "\n";
    }

    public String createTextOnLine(String text, char space, String alignment) {
        if (text.length() > charsOnLine) {
            StringBuilder out = new StringBuilder();
            int len = text.length();
            for (int i = 0; i <= len / charsOnLine; i++) {
                String str = text.substring(i * charsOnLine, Math.min((i + 1) * charsOnLine, len));
                if (!str.trim().isEmpty()) {
                    out.append(createTextOnLine(str, space, alignment));
                }
            }

            return out.toString();
        }

        switch (alignment) {
        case TEXT_ALIGNMENT_RIGHT:
            return StringUtils.leftPad(text, charsOnLine, space) + "\n";

        case TEXT_ALIGNMENT_CENTER:
            return StringUtils.center(text, charsOnLine, space) + "\n";

        default:
            return StringUtils.rightPad(text, charsOnLine, space) + "\n";
        }
    }

    public void setCharsOnLine(int charsOnLine) {
        this.charsOnLine = charsOnLine;
    }
}
