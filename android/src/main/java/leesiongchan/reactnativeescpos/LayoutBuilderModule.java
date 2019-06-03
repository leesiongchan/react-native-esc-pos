package leesiongchan.reactnativeescpos;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

public class LayoutBuilderModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private LayoutBuilder layoutBuilder;

    public LayoutBuilderModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(LayoutBuilder.TEXT_ALIGNMENT_LEFT, LayoutBuilder.TEXT_ALIGNMENT_LEFT);
        constants.put(LayoutBuilder.TEXT_ALIGNMENT_CENTER, LayoutBuilder.TEXT_ALIGNMENT_CENTER);
        constants.put(LayoutBuilder.TEXT_ALIGNMENT_RIGHT, LayoutBuilder.TEXT_ALIGNMENT_RIGHT);
        return constants;
    }

    @Override
    public String getName() {
        return "LayoutBuilder";
    }

    @ReactMethod
    public String createAccent(String text, char accent) {
        return layoutBuilder.createAccent(text, accent);
    }

    @ReactMethod
    public String createDivider() {
        return layoutBuilder.createDivider();
    }

    @ReactMethod
    public String createDivider(char symbol) {
        return layoutBuilder.createDivider(symbol);
    }

    @ReactMethod
    public String createMenuItem(String key, String value, char space) {
        return layoutBuilder.createMenuItem(key, value, space);
    }

    @ReactMethod
    public String createTextOnLine(String text, char space, String alignment) {
        return layoutBuilder.createTextOnLine(text, space, alignment);
    }

    @ReactMethod
    public void setPrintingSize(String printingSize) {
        int charsOnLine;

        switch (printingSize) {
        case PRINTING_SIZE_80_MM:
            charsOnLine = LayoutBuilder.CHARS_ON_LINE_80_MM;
            break;

        case PRINTING_SIZE_58_MM:
        default:
            charsOnLine = LayoutBuilder.CHARS_ON_LINE_58_MM;
        }

        layoutBuilder.setCharsOnLine(charsOnLine);
    }
}
