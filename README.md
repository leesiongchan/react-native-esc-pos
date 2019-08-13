# React Native ESC/POS module

A React Native ESC/POS module to help you connect to your ESC/POS printer easily.
It also has provide an intuitive way to design your layout, check below example to see how easy to get your layout ready!

## Getting started

`$ yarn add @leesiongchan/react-native-esc-pos`

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
    // Test Print
    await EscPos.printSample();
    // Cut half!
    await EscPos.cutPart();
    // You can also print image!
    await EscPos.printImage(file.uri);
    // Print your design!
    await EscPos.printDesign(design);
    // Print QR Code
    await EscPos.printQRCOde("Proxima b is the answer!");
    // Cut full!
    await EscPos.cutFull();
    // Beep!
    await EscPos.beep();
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

Register callback events to receive device found:-
```javascript
EscPos.addListener("bluetoothDeviceFound", (event: any) => {
  if (event.state === EscPos.BLUETOOTH_DEVICE_FOUND) {
    console.log("Device Found!");
    console.log("Device Name : " + event.deviceInfo.name);
    console.log("Device MAC Address : " + event.deviceInfo.macAddress);
  }
```


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

## TODO

- [x] Android support
- [ ] iOS support
- [ ] Print barcode
- [ ] Add TypeScript support
- [x] Bluetooth support
- [ ] Serial port support
- [ ] Add test
- [x] Listen to the connection status
