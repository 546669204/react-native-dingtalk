import { NativeModules , NativeEventEmitter , requireNativeComponent } from "react-native";
import React from "react";
const RNModule = NativeModules.RNDingTalkModule;

type ShareWebPageReq = {
  url:String,
  title:String,
  content:String,
  thumbUrl:String,
  isSendDing?:Boolean,
}
type ShareImageReq = {
  url:String,
  path:String,
  isSendDing?:Boolean,
}
var eventEmitter:NativeEventEmitter = null;
 
export default {
  init(appId:String):Promise<Boolean> {
    if(!appId)throw "appId 不能为空"
    return RNModule.init(appId)
  },

  isDDAppInstalled():Promise<Boolean> {
    return RNModule.isDDAppInstalled()
  },
  isDDSupportAPI():Promise<Boolean> {
    return RNModule.isDDSupportAPI()
  },
  isDDSupportDingAPI():Promise<Boolean> {
    return RNModule.isDDSupportDingAPI()
  },
  isDDSupportDingLogin():Promise<Boolean> {
    return RNModule.isDDSupportDingLogin()
  },


  sendAuth():Promise<Boolean> {
    return RNModule.sendAuth()
  },
  sendTextMessage(text:string,isSendDing:Boolean=false):Promise<Boolean> {
    if(!text)throw "text 不能为空"
    return RNModule.sendTextMessage(text,isSendDing)
  },
  sendWebPageMessage(data:ShareWebPageReq):Promise<Boolean>{
    data.isSendDing = data.isSendDing || false;
    return RNModule.sendWebPageMessage(data)
  },
  sendImageMessage(data:ShareImageReq):Promise<Boolean>{
    if(!data.url && !data.path)throw "必须要有一个图片";
    data.isSendDing = data.isSendDing || false;
    return RNModule.sendImageMessage(data)
  },
  addListener(eventName,handler) {
    if(!eventName||!handler)return;
    if(!eventEmitter) {eventEmitter = new NativeEventEmitter(RNModule)};
    return eventEmitter.addListener(eventName, handler)
  }
};

