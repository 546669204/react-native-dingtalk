package com.xiaoming.rn.dingtalk;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.ShareConstant;
import com.android.dingtalk.share.ddsharemodule.message.BaseReq;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.android.dingtalk.share.ddsharemodule.message.DDImageMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDMediaMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDTextMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDWebpageMessage;
import com.android.dingtalk.share.ddsharemodule.message.SendAuth;
import com.android.dingtalk.share.ddsharemodule.message.SendMessageToDD;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.lang.reflect.Field;

import static android.app.Activity.RESULT_OK;


public class RNDingTalkModule extends ReactContextBaseJavaModule  implements  LifecycleEventListener,IDDAPIEventHandler {

  private final ReactApplicationContext reactContext;
  private static String TAG = "xm.dingtalk";

  public static String appId = "";
  private static RNDingTalkModule mInstance;
  //self
  private static  IDDShareApi iddShareApi;

  public RNDingTalkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addLifecycleEventListener(this);

    mInstance = this;
  }


  @Override
  public void onHostResume() {
    // Activity `onResume`
  }

  @Override
  public void onHostPause() {
    // Activity `onPause`
  }

  @Override
  public void onHostDestroy() {
    // Activity `onDestroy`
  }

  @ReactMethod
  public void init(String appId , Promise promise) {
    try {
      iddShareApi = DDShareApiFactory.createDDShareApi(reactContext,appId,true);
      appId = appId;
      promise.resolve(true);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void isDDAppInstalled(Promise promise) {
    try {
      promise.resolve(iddShareApi.isDDAppInstalled());
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void isDDSupportAPI(Promise promise) {
    try {
      promise.resolve(iddShareApi.isDDSupportAPI());
    }catch (Exception e){
      promise.reject(e);
    }
  }
  @ReactMethod
  public void isDDSupportDingAPI(Promise promise) {
    try {
      promise.resolve(iddShareApi.isDDSupportDingAPI());
    }catch (Exception e){
      promise.reject(e);
    }
  }
  @ReactMethod
  public void isDDSupportDingLogin(Promise promise) {
    try {
      SendAuth.Req req = new SendAuth.Req();
      promise.resolve(req.getSupportVersion() <= iddShareApi.getDDSupportAPI());
    }catch (Exception e){
      promise.reject(e);
    }
  }
  @ReactMethod
  public void sendAuth(Promise promise) {
    try {
      SendAuth.Req req = new SendAuth.Req();
      req.scope = SendAuth.Req.SNS_LOGIN;
      req.state = "test";
      if (req.getSupportVersion() > iddShareApi.getDDSupportAPI()) {
        promise.reject(new Error("钉钉版本过低，不支持登录授权"));
        return;
      }
      iddShareApi.sendReq(req);
      promise.resolve(true);
    } catch (Exception e) {
      promise.reject(e);
    }
  }
  @ReactMethod
  public void sendTextMessage(String str,boolean isSendDing,Promise promise) {
    try {
      //初始化一个DDTextMessage对象
      DDTextMessage textObject = new DDTextMessage();
      textObject.mText = str;

      //用DDTextMessage对象初始化一个DDMediaMessage对象
      DDMediaMessage mediaMessage = new DDMediaMessage();
      mediaMessage.mMediaObject = textObject;

      //构造一个Req
      SendMessageToDD.Req req = new SendMessageToDD.Req();
      req.mMediaMessage = mediaMessage;

      //调用api接口发送消息到钉钉
      if(isSendDing){
        iddShareApi.sendReqToDing(req);
      } else {
        iddShareApi.sendReq(req);
      }

      promise.resolve(true);
    }catch (Exception e){
      promise.reject(e);
    }
  }
  @ReactMethod
  public void sendWebPageMessage(ReadableMap obj,Promise promise) {
    try {
      //初始化一个DDWebpageMessage并填充网页链接地址
      DDWebpageMessage webPageObject = new DDWebpageMessage();
      webPageObject.mUrl = obj.getString("url");

      //构造一个DDMediaMessage对象
      DDMediaMessage webMessage = new DDMediaMessage();
      webMessage.mMediaObject = webPageObject;

      //填充网页分享必需参数，开发者需按照自己的数据进行填充
      webMessage.mTitle = obj.getString("title");
      webMessage.mContent = obj.getString("content");
      webMessage.mThumbUrl = obj.getString("thumbUrl");
      // 网页分享的缩略图也可以使用bitmap形式传输
//         webMessage.setThumbImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

      //构造一个Req
      SendMessageToDD.Req webReq = new SendMessageToDD.Req();
      webReq.mMediaMessage = webMessage;
//        webReq.transaction = buildTransaction("webpage");

      //调用api接口发送消息到支付宝
      if(obj.getBoolean("isSendDing")){
        iddShareApi.sendReqToDing(webReq);
      } else {
        iddShareApi.sendReq(webReq);
      }
      promise.resolve(true);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void sendImageMessage(ReadableMap obj,Promise promise) {
    try {
      //初始化一个DDImageMessage
      DDImageMessage imageObject = new DDImageMessage();
      if (obj.hasKey("url")){
        imageObject.mImageUrl = obj.getString("url");
      }
      if (obj.hasKey("path")){
        imageObject.mImagePath = obj.getString("path");
      }

      //构造一个mMediaObject对象
      DDMediaMessage mediaMessage = new DDMediaMessage();
      mediaMessage.mMediaObject = imageObject;

      //构造一个Req
      SendMessageToDD.Req req = new SendMessageToDD.Req();
      req.mMediaMessage = mediaMessage;
//        req.transaction = buildTransaction("image");

      //调用api接口发送消息到支付宝
      if(obj.getBoolean("isSendDing")){
        iddShareApi.sendReqToDing(req);
      } else {

        iddShareApi.sendReq(req);
      }
      promise.resolve(true);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }


  @Override
  public String getName() {
    return "RNDingTalkModule";
  }


  @Override
  public void onReq(BaseReq baseReq) {
    Log.d(TAG, "onReq");
  }

  @Override
  public void onResp(BaseResp baseResp) {
    Log.d(TAG, ""+baseResp.mErrStr);
    int errCode = baseResp.mErrCode;
    if(baseResp.getType() == ShareConstant.COMMAND_SENDAUTH_V2 && (baseResp instanceof SendAuth.Resp)){
      SendAuth.Resp authResp = (SendAuth.Resp) baseResp;
      WritableMap params = Arguments.createMap();
      switch (errCode){
        case BaseResp.ErrCode.ERR_OK:
          params.putInt("code",0);
          params.putString("data",authResp.code);
          break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
          params.putInt("code",-1);
          params.putString("msg","授权取消");
          break;
        default:
          params.putInt("code",-1);
          params.putString("msg","授权异常");
          break;
      }
      sendEvent(reactContext,"callbackByLogin",params);
    }else{
      WritableMap params = Arguments.createMap();
      switch (errCode){
        case BaseResp.ErrCode.ERR_OK:
          params.putInt("code",0);
          break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
          params.putInt("code",-1);
          params.putString("msg","分享取消");
          break;
        default:
          params.putInt("code",-1);
          params.putString("msg","分享异常");
          break;
      }
      sendEvent(reactContext,"callbackByShare",params);
    }
  }
  public static void handleIntent(Intent intent) {
    if(mInstance != null){
      iddShareApi.handleIntent(intent, mInstance);
    }

  }
}
