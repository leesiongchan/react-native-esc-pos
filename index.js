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

EscPos.addListener = (eventName, cb) => {
    switch (eventName) {
      case 'bluetoothStateChanged':
        EscPos.initBluetoothConnectionListener();
        DeviceEventEmitter.addListener('bluetoothStateChanged', cb);
        break;
  
      default:
        throw new Error('Event name not exist!');
    }
  };
  
      default:
export { EscPos, LayoutBuilder };
export default EscPos;
