package leesiongchan.reactnativeescpos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import static io.github.escposjava.print.Commands.*;

// @ref https://github.com/LeeryBit/esc-pos-android/blob/master/library/src/main/java/com/leerybit/escpos/Ticket.java
public class LayoutBuilder {
    public static final String TEXT_ALIGNMENT_LEFT = "LEFT";
    public static final String TEXT_ALIGNMENT_CENTER = "CENTER";
    public static final String TEXT_ALIGNMENT_RIGHT = "RIGHT";
    public static final int CHARS_ON_LINE_58_MM = 32;
    public static final int CHARS_ON_LINE_80_MM = 48;
    private int charsOnLine = CHARS_ON_LINE_58_MM;

    LayoutBuilder() {
    }

    LayoutBuilder(int charsOnLine) {
        this.charsOnLine = charsOnLine;
    }

    public String createFromDesign(String text) throws IOException {
        return createFromDesign(text, charsOnLine);
    }

    /**
     * DESIGN 1: Order List                       *
     *          D0004 | Table #: A1 {C}           *
     * ------------------------------------------ *
     * [Dine In]                                  *
     * [ ] Espresso                               *
     *     - No sugar, Regular 9oz, Hot           *
     *                                    {R} x 1 *
     * ------------------------------------------ *
     * [ ] Blueberry Cheesecake                   *
     *     - Slice                                *
     *                                    {R} x 1 *
     *                                            *
     * DESIGN 2: Menu Items                       *
     * ------------------------------------------ *
     * Item         {<>}       Qty  Price  Amount *
     * Pork Rice    {<>}         1  13.80   13.80 *
     **/
    public String createFromDesign(String text, int charsOnLine) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(text));
        StringBuilder designText = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.matches("---.*")) {
                designText.append(createDivider(charsOnLine));
            } else if (line.matches("===.*")){
                designText.append(createDivider('=',charsOnLine));
            } else if (line.contains("{RP:")) {
                designText.append(createCustomNumberOfString(line));
            // } else if (line.contains("{C}")) {
            //     designText.append(
            //             createTextOnLine(line.trim().replace("{C}", ""), ' ', TEXT_ALIGNMENT_CENTER, charsOnLine));
            // } else if (line.contains("{R}")) {
            //     designText.append(
            //             createTextOnLine(line.trim().replace("{R}", ""), ' ', TEXT_ALIGNMENT_RIGHT, charsOnLine));
            } else if (line.contains("{<>}")) {
                String[] splitLine = line.split("\\{<>\\}");
                designText.append(createMenuItem(splitLine[0], splitLine[1], ' ', charsOnLine));
            } else {
                designText.append(line);
                designText.append("\n");
                // designText.append(createTextOnLine(line, ' ', TEXT_ALIGNMENT_LEFT, charsOnLine));
            }
        }

        return designText.toString();
    }

    public String createAccent(String text, char accent) {
        return createAccent(text, accent, charsOnLine);
    }

    public String createAccent(String text, char accent, int charsOnLine) {
        if (text.length() - 4 > charsOnLine) {
            accent = ' ';
        }

        return createTextOnLine(' ' + text + ' ', accent, TEXT_ALIGNMENT_CENTER, charsOnLine);
    }

    public String createCustomNumberOfString(String text) {
        String symbol = "", count = "0";

        if (text.contains("{RP:")) {
            symbol = text.substring(0,text.indexOf("{RP:"));
            count = text.substring(text.indexOf("{RP:") + 4, text.indexOf('}'));
        }

        if (text.contains("{C}")) {
            return createTextOnLine(createCustomNumberOfString(symbol, count), ' ', TEXT_ALIGNMENT_CENTER, charsOnLine);
        } else if (text.contains("{R}")) {
            return createTextOnLine(createCustomNumberOfString(symbol, count), ' ', TEXT_ALIGNMENT_RIGHT, charsOnLine);
        }

        return createCustomNumberOfString(symbol, count);
    }

    public String createCustomNumberOfString(String text, String count) {
        int intNum = 0;
        intNum = Integer.parseInt(count);
        return StringUtils.repeat(text, intNum);
    }

    public String createDivider() {
        return createDivider('-', charsOnLine);
    }

    public String createDivider(int charsOnLine) {
        return createDivider('-', charsOnLine);
    }

    public String createDivider(char symbol, int charsOnLine) {
        return StringUtils.repeat(symbol, charsOnLine) + "\n";
    }

    public String createMenuItem(String key, String value, char space) {
        return createMenuItem(key, value, space, charsOnLine);
    }

    public String createMenuItem(String key, String value, char space, int charsOnLine) {
        if (key.length() + value.length() + 2 > charsOnLine) {
            return createTextOnLine(key + ": " + value, ' ', TEXT_ALIGNMENT_LEFT, charsOnLine);
        }

        return StringUtils.rightPad(key, charsOnLine - value.length(), space) + value + "\n";
    }

    public String createTextOnLine(String text, char space, String alignment) {
        return createTextOnLine(text, space, alignment, charsOnLine);
    }

    public String createTextOnLine(String text, char space, String alignment, int charsOnLine) {
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

    public int getCharsOnLine() {
        return charsOnLine;
    }
}
