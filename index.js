import { NativeModules } from 'react-native';

const { LayoutBuilder } = NativeModules;

const EscPos = {
  ...NativeModules.EscPos,
  // TODO: What is the best way to add optional arguments to @ReactMethod? overloading doesn seem to be working??
  connect(address, port) {
    if (!port) {
      return NativeModules.EscPos.connectBluetoothPrinter(address);
    } else {
      return NativeModules.EscPos.connectNetworkPrinter(address, port);
    }
  },
};

export { EscPos, LayoutBuilder };
export default EscPos;
