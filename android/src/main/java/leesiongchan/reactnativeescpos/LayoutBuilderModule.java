package leesiongchan.reactnativeescpos;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LayoutBuilderModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private LayoutBuilder layoutBuilder = new LayoutBuilder();

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
    public void createAccent(String text, String accent, Promise promise) {
        promise.resolve(layoutBuilder.createAccent(text, accent.charAt(0)));
    }

    @ReactMethod
    public void createFromDesign(String text, Promise promise) throws IOException {
        try {
            promise.resolve(layoutBuilder.createFromDesign(text));
        } catch (IOException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void createDivider(Promise promise) {
        promise.resolve(layoutBuilder.createDivider());
    }

    @ReactMethod
    public void createDivider(String symbol, Promise promise) {
        promise.resolve(layoutBuilder.createDivider(symbol.charAt(0)));
    }

    @ReactMethod
    public void createMenuItem(String key, String value, String space, Promise promise) {
        promise.resolve(layoutBuilder.createMenuItem(key, value, space.charAt(0)));
    }

    @ReactMethod
    public void createTextOnLine(String text, String space, String alignment, Promise promise) {
        promise.resolve(layoutBuilder.createTextOnLine(text, space.charAt(0), alignment));
    }

    @ReactMethod
    public void setPrintingSize(String printingSize) {
        int charsOnLine;

        switch (printingSize) {
        case EscPosModule.PRINTING_SIZE_80_MM:
            charsOnLine = LayoutBuilder.CHARS_ON_LINE_80_MM;
            break;

        case EscPosModule.PRINTING_SIZE_58_MM:
        default:
            charsOnLine = LayoutBuilder.CHARS_ON_LINE_58_MM;
        }

        layoutBuilder.setCharsOnLine(charsOnLine);
    }
}
