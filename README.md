# React Native ESC/POS module

A React Native ESC/POS module to help you connect to your ESC/POS printer easily.
It also has provide an intuitive way to design your layout, check below example to see how easy to get your layout ready!

## Getting started

`$ yarn add @leesiongchan/react-native-esc-pos`

** NOTE: Skip below installation guide if you are using React Native >= 0.60 **

### Mostly automatic installation

`$ react-native link @leesiongchan/react-native-esc-pos`

### Android Bluetooth Permission
Add following permission into AndroidManifest.xml
```
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

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
`;

async function testPrinter() {
  try {
    // Can be `network` or `bluetooth`
    EscPos.setConfig({ type: "network" });

    // Connects to your printer
    // If you use `bluetooth`, second parameter is not required.
    await EscPos.connect("10.10.10.10", 9100);

    // Once connected, you can setup your printing size, either `PRINTING_SIZE_58_MM` or `PRINTING_SIZE_80_MM`
    EscPos.setPrintingSize(EscPos.PRINTING_SIZE_80_MM);
    // 0 to 8 (0-3 = smaller, 4 = default, 5-8 = larger)
    EscPos.setTextDensity(8);
    // Test Print
    await EscPos.printSample();
    // Cut half!
    await EscPos.cutPart();
    // You can also print image!
    await EscPos.printImage(file.uri); // file.uri = "file:///longpath/xxx.jpg"
    // Print your design!
    await EscPos.printDesign(design);
    // Print QR Code, you can specify the size
    await EscPos.printQRCode("Proxima b is the answer!", 200);
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

## Scan For Bluetooth Devices

To scan for available bluetooth in range

```javascript
EscPos.scanDevices();
```

To stop scan

```javascript
EscPos.stopScan();
```

Register callback events to receive device found:-

```javascript
EscPos.addListener("bluetoothDeviceFound", (event: any) => {
  if (event.state === EscPos.BLUETOOTH_DEVICE_FOUND) {
    console.log("Device Found!");
    console.log("Device Name : " + event.deviceInfo.name);
    console.log("Device MAC Address : " + event.deviceInfo.macAddress);
  }
```

## Design Tags

| Tag      | Description                                   |
| -------- | :-------------------------------------------- |
| {B}      | Bold.                                         |
| {U}      | Underline.                                    |
| {H1}     | Font Size. 2x2 / char                         |
| {H2}     | Font Size. 1x2 / char                         |
| {H3}     | Font Size. 2x1 / char                         |
| {LS:<?>} | Linespace. M = 24LS, L = 30LS                 |
| {C}      | Align text to center.                         |
| {R}      | Align text to right.                          |
| {RP:?:?} | Repeat text. Eg. {RP:5:a} will output "aaaaa" |
| {QR[?]}  | Print QR code.                                |
| {<>}     | Left-right text separation.                   |
| {---}    | Create a "---" separator.                     |
| {===}    | Create a "===" separator.                     |

## Events

To listen to bluetooth state change

```javascript
EscPos.addListener("bluetoothStateChanged", (event: any) => {
  if (event.state === EscPos.BLUETOOTH_CONNECTED) {
    console.log("Device Connected!");
    console.log("Device Name : " + event.deviceInfo.name);
    console.log("Device MAC Address : " + event.deviceInfo.macAddress);
  }
});
```

To get Bluetooth Conenction State:

- EscPos.BLUETOOTH_CONNECTED
- EscPos.BLUETOOTH_DISCONNECTED

To get Connected / Disconnected Bluetooth Device Info
Device Name:

- event.deviceInfo.name

Device MAC Address:

- event.deviceInfo.macAddress

## New Features

- You can now easily duplicate a string or character and print onto your design.
- Introducing Repeat feature:
  > Main tag {RP: number of times to duplicate required: string or character to duplicate}
  > Example:
  > Input {RP:5:\*}
  > Output: **\***

> If you have few characters to duplicate in a line and some text within the line you wouldn't want to disturb, you can do it as per below:
> Example:
> Input: {RP:3:= }This is a test string{RP:2:@_@}
> Output: = = = This is a test string@_@@\_@

> Important note: this feature does not support repetitive printing of Closing Curly Bracket }.

## TODO

- [x] Android support
- [ ] iOS support
- [ ] Print barcode
- [ ] Add TypeScript support
- [x] Bluetooth support
- [ ] Serial port support
- [ ] Add test
- [x] Listen to the connection status
