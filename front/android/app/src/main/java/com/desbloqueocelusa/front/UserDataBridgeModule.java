package com.desbloqueocelusa.front;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.facebook.react.bridge.*;

public class UserDataBridgeModule extends ReactContextBaseJavaModule {
    public UserDataBridgeModule(ReactApplicationContext reactContext) { super(reactContext); }

    @Override
    public String getName() { return "UserDataBridge"; }

    @ReactMethod
    public void saveUserData(String name, String phone, String document, String email, Promise promise) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getReactApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("document", document);
        editor.putString("email", email);
        boolean ok = editor.commit();
        if (ok) promise.resolve(true); else promise.reject("error", "No se pudo guardar");
    }
}