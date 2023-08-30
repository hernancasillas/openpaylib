#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(Openpaylib, NSObject)

RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(initialize:(NSString *)merchantId withPublicApiKey:(NSString *)publicApiKey withProductionMode:(BOOL)productionMode)

RCT_EXTERN_METHOD(getDeviceId:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(createToken:(NSDictionary *)dict withResolver:(RCTPromiseResolveBlock)resolve
                  withRejecter:(RCTPromiseRejectBlock)reject)


+(BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
