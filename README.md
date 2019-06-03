# react-native-esc-pos

## Getting started

`$ npm install react-native-esc-pos --save`

### Mostly automatic installation

`$ react-native link react-native-esc-pos`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-esc-pos` and add `EscPos.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libEscPos.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import leesiongchan.reactnativeescpos.EscPosPackage;` to the imports at the top of the file
- Add `new EscPosPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```
   include ':react-native-esc-pos'
   project(':react-native-esc-pos').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-esc-pos/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
   ```
     compile project(':react-native-esc-pos')
   ```

## Usage

```javascript
import EscPos from "react-native-esc-pos";

// TODO: What to do with the module?
EscPos;
```
