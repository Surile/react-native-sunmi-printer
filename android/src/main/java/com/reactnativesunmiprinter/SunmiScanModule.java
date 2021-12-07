package com.reactnativesunmiprinter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SunmiScanModule extends ReactContextBaseJavaModule {
   private static ReactApplicationContext reactContext;
    private static final int START_SCAN = 0x0000;
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_FAILED_TO_SHOW_SCAN = "E_FAILED_TO_SHOW_SCAN";
    private Promise mPickerPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                ArrayList<HashMap<String, String>> result = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
                Iterator<HashMap<String, String>> it = result.iterator();
                while (it.hasNext()) {
                    HashMap hashMap = it.next();
                    sendEvent(hashMap.get("VALUE").toString());
                }
            }
        }
    };

    public(ReactApplicationContext context) {
     super(context);
     reactContext = context;
     reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "SunmiScanModule";
    }

    @ReactMethod
    public void scan(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }
        mPickerPromise = promise;
        try {
            Intent intent = new Intent("com.sunmi.scan");
            intent.setPackage("com.sunmi.sunmiqrcodescanner");
            intent.putExtra("PLAY_SOUND", true);
            currentActivity.startActivityForResult(intent, START_SCAN);
        } catch (Exception e) {
            mPickerPromise.reject("E_FAILED_TO_SHOW_SCAN", e);
            mPickerPromise = null;
        }
    }

    private static void sendEvent(String msg) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onScanSuccess", msg);
    }
}
