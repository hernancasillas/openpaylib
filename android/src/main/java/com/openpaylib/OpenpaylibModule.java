package com.openpaylib;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import org.json.JSONException;
import org.json.JSONObject;

import mx.openpay.android.Openpay;
import mx.openpay.android.OperationCallBack;
import mx.openpay.android.OperationResult;
import mx.openpay.android.exceptions.OpenpayServiceException;
import mx.openpay.android.exceptions.ServiceUnavailableException;
import mx.openpay.android.model.Address;
import mx.openpay.android.model.Card;
import mx.openpay.android.model.Token;

@ReactModule(name = OpenpaylibModule.NAME)
public class OpenpaylibModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Openpaylib";
 private Openpay openpay;
  private ReactApplicationContext reactApplicationContext;

  public OpenpaylibModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactApplicationContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

   @ReactMethod
  public void initialize(String merchantId, String publicApiKey, boolean productionMode) {
    openpay = new Openpay(merchantId, publicApiKey, productionMode);
  }

   @ReactMethod
    public void getDeviceId(Callback callback) {
        String s = openpay.getDeviceCollectorDefaultImpl().setup(reactApplicationContext.getCurrentActivity());
        Log.d("OpenpaylibModule", "getDeviceId: called :: " + s);
        callback.invoke(s);
    }

  @ReactMethod
  public void createToken(ReadableMap map, Promise promise) {
//    JSONObject obj = null;
    Log.e(NAME, "createToken: " + map.toHashMap());

    try {
      JSONObject obj = ReactNativeJSON.convertMapToJson(map);
      Card card = new Card();
      card.holderName(obj.getString("holderName"));
      card.cardNumber(obj.getString("cardNumber"));
      card.expirationMonth(obj.getInt("expirationMonth"));
      card.expirationYear(obj.getInt("expirationYear"));
      card.cvv2(obj.getString("cvv2"));

      if (obj.has("allows_charges")) {
        card.setAllowsCharges(obj.getBoolean("allows_charges"));
      }
      if (obj.has("bank_code")) {
        card.setBankCode(obj.optString("bank_code"));
      }
      if (obj.has("type")) {
        card.setType(obj.optString("type"));
      }
      if (obj.has("address")) {
        try {
          JSONObject addressObj = obj.getJSONObject("address");
          Address address = new Address();
          address.city(addressObj.optString("city"));
          address.state(addressObj.optString("state"));
          address.countryCode(addressObj.optString("countryCode"));
          address.postalCode(addressObj.optString("postalCode"));
          address.line1(addressObj.optString("line1"));
          address.line2(addressObj.optString("line2"));
          address.line3(addressObj.optString("line3"));

          card.address(address);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      openpay.createToken(card, new OperationCallBack<Token>() {

        @Override
        public void onSuccess(OperationResult<Token> arg0) {
          //Handlo in success
          Token token = arg0.getResult();
          JSONObject object = new JSONObject();
          try {
            JSONObject cardObj = new JSONObject();
            cardObj.put("holderName", card.getHolderName());
            cardObj.put("cardNumber", card.getCardNumber());
            cardObj.put("expirationMonth", card.getExpirationMonth());
            cardObj.put("expirationYear", card.getExpirationYear());
            cardObj.put("cvv2", card.getCvv2());
            cardObj.put("allows_charges", card.getAllowsCharges());
            cardObj.put("bank_code", card.getBankCode());
            cardObj.put("type", card.getType());

            JSONObject addressObj = new JSONObject();
            Address address = card.getAddress();
            if (address != null) {
              addressObj.put("city", address.getCity());
              addressObj.put("state", address.getState());
              addressObj.put("countryCode", address.getCountryCode());
              addressObj.put("postalCode", address.getPostalCode());
              addressObj.put("line1", address.getLine1());
              addressObj.put("line2", address.getLine2());
              addressObj.put("line3", address.getLine3());
            }

            cardObj.put("address", addressObj);

            object.put("id", token.getId());
            object.put("card", cardObj);
            promise.resolve(ReactNativeJSON.convertJsonToMap(object));
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onError(OpenpayServiceException error) {
          //Handle Error
          String desc = "";
          switch (error.getErrorCode()) {
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
          promise.reject("error-" + error.getErrorCode(), desc, error);

        }

        @Override
        public void onCommunicationError(ServiceUnavailableException arg0) {
          //Handle communication error
          promise.reject("communicationError", arg0.getMessage(), arg0);
        }
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
