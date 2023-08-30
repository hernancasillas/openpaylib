import Foundation
import OpenpayKit

@objc(Openpaylib)
class Openpaylib: NSObject {
    var openpay : Openpay!
    
    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b+3+5)
    }
    
    @objc(initialize:withPublicApiKey:withProductionMode:)
    func initialize(merchantId: String, publicApiKey: String,productionMode:Bool) -> Void {
        print("initialize called :: "+merchantId+"   :: "+publicApiKey+"   ... ")
        openpay = Openpay(withMerchantId: merchantId, andApiKey: publicApiKey, isProductionMode: productionMode, isDebug: false,countryCode: "MX")
        
    }
    
    @objc(getDeviceId:)
    func getDeviceId(callback:RCTResponseSenderBlock?){
        DispatchQueue.main.async {
            self.openpay.createDeviceSessionId(successFunction:  {sessionID in
                print("sessionID :: \(sessionID)")
                callback?([sessionID])
            }, failureFunction: {error in
                print("\(error.code) - \(error.localizedDescription)")
            })
        }
    }
    
    @objc(createToken:withResolver:withRejecter:)
    func createToken(dict: NSDictionary, resolve:@escaping RCTPromiseResolveBlock, reject:@escaping RCTPromiseRejectBlock) -> Void {
        
        let cardNumber = dict["cardNumber"] as! String
        let holderName = dict["holderName"] as! String
        let expirationYear = "\(dict["expirationYear"])"
        let expirationMonth = "\(dict["expirationMonth"])"
        let cvv2 = dict["cvv2"] as! String
        
        var card = TokenizeCardRequest(cardNumber: cardNumber,holderName:holderName, expirationYear: expirationYear, expirationMonth: expirationMonth, cvv2: cvv2)
        
        if let addressJson = dict["address"] as? String {
            let jsonData = addressJson.data(using: .utf8)!
            let address = try! JSONDecoder().decode(Address.self, from: jsonData)
            
            card.address = address
        }
        
        
        // request token by card model
        DispatchQueue.main.async {
            self.openpay.tokenizeCard(card: card) { (OPToken) in
                resolve(OPToken.card.asDictionary)
                print(OPToken.id)
            } failureFunction: { (error) in
                print(error)
                var desc : String = "";
                let a : Int = error.code;
                switch (a)
                {
                case 3001:
                    desc = "declined";
                    break;
                case 3002:
                    desc = "expired";
                    break;
                case 3003:
                    desc = "insufficient_funds";
                    break;
                case 3004:
                    desc = "stolen_card";
                    break;
                case 3005:
                    desc = "suspected_fraud";
                    break;
                case 2002:
                    desc = "already_exists";
                    break;
                default:
                    desc = "error_creating_card";
                }
                reject("error-" + String(a), desc, error)
            }
        }
    }
}
