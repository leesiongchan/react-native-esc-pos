import { NativeModules } from "react-native";

const { EscPos, LayoutBuilder } = NativeModules;

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
  
export { EscPos, LayoutBuilder };
export default EscPos;
