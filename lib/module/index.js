import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR = `The package 'openpaylib' doesn't seem to be linked. Make sure: \n\n` + Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const Openpaylib = NativeModules.Openpaylib ? NativeModules.Openpaylib : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
export function multiply(a, b) {
  return Openpaylib.multiply(a, b);
}
export function initialize(merchantId, publicApiKey, productionMode) {
  Openpaylib.initialize(merchantId, publicApiKey, productionMode);
}
export function getDeviceId(callback) {
  Openpaylib.getDeviceId(callback);
}
export function createToken(cardObj) {
  return Openpaylib.createToken(cardObj);
}
//# sourceMappingURL=index.js.map