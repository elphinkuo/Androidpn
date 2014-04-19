/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.sdk;

import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Class desciption here.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class MainService extends Service {

    public static final String SERVICE_NAME = "org.androidpn.sdk.MainService";

    // private static final String LOGTAG = "MainService";

    private SharedPreferences sdkPreferences;

    private TelephonyManager telephonyManager;

    //    private WifiManager wifiManager;
    //
    //    private ConnectivityManager connectivityManager;

    private XmppManager xmppManager;

    private String deviceId;

    public MainService() {
    }

    @Override
    public void onCreate() {
        Log.d(getClass().getSimpleName(), "onCreate()...");

        sdkPreferences = getSharedPreferences(ServiceManager.SDK_PREFERENCES,
                Context.MODE_PRIVATE);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        deviceId = telephonyManager.getDeviceId();
        Log.d(getClass().getSimpleName(), "deviceId=" + deviceId);

        Editor editor = sdkPreferences.edit();
        editor.putString(Constants.DEVICE_ID, deviceId);
        editor.commit();

        if (deviceId == null || deviceId.trim().length() == 0
                || deviceId.matches("0+")) {
            if (sdkPreferences.contains("EMULATOR_DEVICE_ID")) {
                deviceId = sdkPreferences.getString("EMULATOR_DEVICE_ID", "");
            } else {
                deviceId = (new StringBuilder("EMU")).append(
                        (new Random(System.currentTimeMillis())).nextLong())
                        .toString();
                editor.putString(Constants.EMULATOR_DEVICE_ID, deviceId);
                editor.commit();
            }
        }

        // TODO
        xmppManager = new XmppManager(this);
        xmppManager.connect();
        xmppManager.register();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(getClass().getSimpleName(), "onStart()...");
        // TODO
        xmppManager.login();
    }

    @Override
    public void onDestroy() {
        Log.d(getClass().getSimpleName(), "onDestroy()...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onUnbind()...");
        return true;
    }

    // ================

    public static Intent getIntent() {
        return new Intent(SERVICE_NAME);
    }

}
