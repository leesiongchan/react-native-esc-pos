# React Native ESC/POS module

## THIS PROJECT IS NOT MAINTAINED ANYMORE, YOU ARE WELCOME TO CREATE YOUR OWN FORK =)

A React Native ESC/POS module to help you connect to your ESC/POS printer easily.
It also has provide an intuitive way to design your layout, check below example to see how easy to get your layout ready!

**PS: iOS IS NOT IMPLEMENTED**

## Getting started

`$ yarn add @leesiongchan/react-native-esc-pos`

**PS: Skip below installation guide if you are using React Native >= 0.60**

### Mostly automatic installation

`$ react-native link @leesiongchan/react-native-esc-pos`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `@leesiongchan` ➜ `react-native-esc-pos` and add `EscPos.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libEscPos.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import leesiongchan.reactnativeescpos.EscPosPackage;` to the imports at the top of the file
- Add `new EscPosPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```
   include ':react-native-esc-pos'
   project(':react-native-esc-pos').projectDir = new File(rootProject.projectDir, 	'../node_modules/@leesiongchan/react-native-esc-pos/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
   ```
     compile project(':react-native-esc-pos')
   ```

## Android Bluetooth Permission

Add following permissions into `AndroidManifest.xml`

```
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

## Usage

```javascript
import EscPos from "@leesiongchan/react-native-esc-pos";

const design = `
D0004           {<>}           Table #: A1
------------------------------------------
[ ] Espresso
    - No sugar, Regular 9oz, Hot
                              {H3} {R} x 1
------------------------------------------
[ ] Blueberry Cheesecake
    - Slice
                              {H3} {R} x 1

{QR[Where are the aliens?]}
{BC[Your barcode here]}
{IMG[file://image/path/file.png]}
`;

async function testPrinter() {
  try {
    // Can be `network` or `bluetooth`
    EscPos.setConfig({ type: "network" });

    // Connects to your printer
    // If you use `bluetooth`, second parameter is not required.
    await EscPos.connect("10.10.10.10", 9100);

    // Once connected, you can setup your printing size, either `PRINTING_SIZE_58_MM`, `PRINTING_SIZE_76_MM` or `PRINTING_SIZE_80_MM`
    EscPos.setPrintingSize(EscPos.PRINTING_SIZE_80_MM);
    // 0 to 8 (0-3 = smaller, 4 = default, 5-8 = larger)
    EscPos.setTextDensity(8);
    // Test Print
    await EscPos.printSample();
    // Cut half!
    await EscPos.cutPart();
    // You can also print image! eg. "file:///longpath/xxx.jpg"
    await EscPos.printImage(file.uri);
    // You can also print image with a specific width offset (scale down image by offset pixels)! eg. "file:///longpath/xxx.jpg"
    await EscPos.printImageWithOffset(file.uri, offset);
    // Print your design!
    await EscPos.printDesign(design);
    // Print QR Code, you can specify the size
    await EscPos.printQRCode("Proxima b is the answer!", 200);
    // Print Barcode
    // printBarCode({code}, {type}, {width}, {height}, {font}, {fontPosition})
    // type: 65=UPC-A; 66=UPC-E; 67=EAN13; 68=EAN8; 69=CODE39; 70=ITF; 71=CODABAR; 72=CODE93; 73=CODE128}
    // width: 2-6
    // height: 0-255
    // font: 0=FontA; 1=FontB
    // fontPosition: 0=none; 1=top; 2=bottom; 3=top-bottom
    await EscPos.printBarcode("Your barcode here", 73, 3, 100, 0, 2);
    // Cut full!
    await EscPos.cutFull();
    // Beep!
    await EscPos.beep();
    // Kick the drawer! Can be either `kickCashDrawerPin2` or `kickCashDrawerPin5`
    await EscPos.kickCashDrawerPin2();
    // Disconnect
    await EscPos.disconnect();
  } catch (error) {
    console.error(error);
  }
}
```

### Scan for available bluetooth devices

```javascript
EscPos.scanDevices();
```

### Stop scanning

```javascript
EscPos.stopScan();
```

### Events

```javascript
EscPos.addListener("bluetoothDeviceFound", (event) => {
  if (event.state === EscPos.BLUETOOTH_DEVICE_FOUND) {
    console.log("Device Found!");
    console.log("Device Name : " + event.deviceInfo.name);
    console.log("Device MAC Address : " + event.deviceInfo.macAddress);
  }
});
```

To listen to bluetooth state changes:

```javascript
EscPos.addListener("bluetoothStateChanged", (event) => {
  if (event.state === EscPos.BLUETOOTH_CONNECTED) {
    console.log("Device Connected!");
    console.log("Device Name : " + event.deviceInfo.name);
    console.log("Device MAC Address : " + event.deviceInfo.macAddress);
  }
});
```

### Constants

- EscPos.PRINTING_SIZE_58_MM
- EscPos.PRINTING_SIZE_78_MM
- EscPos.PRINTING_SIZE_80_MM
- EscPos.BLUETOOTH_CONNECTED
- EscPos.BLUETOOTH_DISCONNECTED

## Design Tags

| Tag        | Description                                                                    |
| ---------- | :----------------------------------------------------------------------------- |
| {B}        | Bold.                                                                          |
| {U}        | Underline.                                                                     |
| {H1}       | Font Size. 2x2 / char                                                          |
| {H2}       | Font Size. 1x2 / char                                                          |
| {H3}       | Font Size. 2x1 / char                                                          |
| {LS:?}     | Linespace. M = 24LS, L = 30LS                                                  |
| {C}        | Align text to center.                                                          |
| {R}        | Align text to right.                                                           |
| {RP:?:?}   | Repeat text. Eg. {RP:5:a} will output "aaaaa".                                 |
| {BC[?]}    | Print barcode.                                                                 |
| {QR[?]}    | Print QR code.                                                                 |
| {IMG[?]}   | Print image from a path.                                                       |
| {IMG[?]:?} | Print image scaled down to make space for a width offset. Eg. {IMG[<path>]:32} |
| {<>}       | Left-right text separation.                                                    |
| {---}      | Create a "---" separator.                                                      |
| {===}      | Create a "===" separator.                                                      |

## TODO

- [x] Android support
- [ ] iOS support
- [x] Print barcode
- [ ] Add TypeScript support
- [x] Bluetooth support
- [ ] Serial port support
- [ ] Add test
- [x] Listen to the connection status
