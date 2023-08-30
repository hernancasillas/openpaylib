"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.createToken = createToken;
exports.getDeviceId = getDeviceId;
exports.initialize = initialize;
exports.multiply = multiply;
var _reactNative = require("react-native");
const LINKING_ERROR = `The package 'openpaylib' doesn't seem to be linked. Make sure: \n\n` + _reactNative.Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const Openpaylib = _reactNative.NativeModules.Openpaylib ? _reactNative.NativeModules.Openpaylib : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
function multiply(a, b) {
  return Openpaylib.multiply(a, b);
}
function initialize(merchantId, publicApiKey, productionMode) {
  Openpaylib.initialize(merchantId, publicApiKey, productionMode);
}
function getDeviceId(callback) {
  Openpaylib.getDeviceId(callback);
}
function createToken(cardObj) {
  return Openpaylib.createToken(cardObj);
}
//# sourceMappingURL=index.js.map