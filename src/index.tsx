import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'openpaylib' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Openpaylib = NativeModules.Openpaylib
  ? NativeModules.Openpaylib
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return Openpaylib.multiply(a, b);
}

export function initialize(merchantId:string,publicApiKey:string,productionMode: boolean){
  Openpaylib.initialize(merchantId,publicApiKey,productionMode);
}

export function getDeviceId(callback: (result: string) => void) {
  Openpaylib.getDeviceId((deviceId) => {
    console.log("deviceId ==> " + deviceId);
    callback(deviceId);
  });
}

export function createToken(cardObj: Object): Promise<string> {
  return Openpaylib.createToken(cardObj);
}