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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public final class ServiceManager {

    // public static String APP_PREFERENCES = "AppPreferences";

    public static final String SDK_PREFERENCES = "SdkPreferences";

    private static final String LOGTAG = "ServiceManager";

    private Context context;

    private SharedPreferences sdkPreferences;

    private Properties sdkProperties;

    private String appKey;

    private String xmppHost;

    private String xmppPort;

    public ServiceManager(Context context) {
        this.context = context;
        this.sdkPreferences = context.getSharedPreferences(SDK_PREFERENCES,
                Context.MODE_PRIVATE);

        this.sdkProperties = loadSdkProperties();
        this.xmppHost = sdkProperties.getProperty("xmppHost", "localhost");
        this.xmppPort = sdkProperties.getProperty("xmppPort", "5222");
        Log.i(LOGTAG, "xmppHost=" + xmppHost);
        Log.i(LOGTAG, "xmppPort=" + xmppPort);

        this.appKey = getAppKey(context);
        Log.i(LOGTAG, "appKey=" + appKey);

        Editor editor = sdkPreferences.edit();
        editor.putString(Constants.ANDROIDPN_APP_KEY, appKey);
        editor.putString(Constants.XMPP_HOST, xmppHost);
        editor.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
        editor.commit();
        // Log.i(LOGTAG, "sdkPreferences=" + sdkPreferences.toString());
    }

    public void startService() {
        // Intent intent = new Intent(MainService.SERVICE_NAME);
        //        Intent intent = MainService.getIntent();
        //        context.startService(intent);

        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = MainService.getIntent();
                context.startService(intent);
            }
        });
        serviceThread.start();
    }

    //    public void stopService() {
    //        Intent intent = new Intent("org.androidpn.sdk.MainService");
    //        context.stopService(intent);
    //    }

    private String getAppKey(Context context) {
        if (appKey == null) {
            try {
                PackageManager packagemanager = context.getPackageManager();
                ApplicationInfo applicationInfo = packagemanager
                        .getApplicationInfo(context.getPackageName(), 128);
                if (applicationInfo == null || applicationInfo.metaData == null) {
                    throw new RuntimeException(
                            "Could not read the api key. No meta data found in the manifest file.");
                }
                appKey = applicationInfo.metaData
                        .getString(Constants.ANDROIDPN_APP_KEY);

            } catch (android.content.pm.PackageManager.NameNotFoundException ex) {
                throw new RuntimeException(
                        "Could not read the api key. No name found in the manifest file.");
            }
        }
        if (appKey == null) {
            throw new RuntimeException(
                    "Could not read the api key because of an unknown error.");
        } else {
            return appKey;
        }
    }

    private Properties loadSdkProperties() {
        InputStream in = null;
        Properties props = null;
        try {
            in = getClass().getResourceAsStream(
                    "/org/androidpn/sdk/sdk.properties");
            if (in != null) {
                props = new Properties();
                props.load(in);
            } else {
                Log.e(LOGTAG, "Could not find the sdkProperties file.");
            }
        } catch (IOException e) {
            // e.printStackTrace();
            Log.e(LOGTAG, "Could not find the sdkProperties file.", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Throwable ignore) {
                }
        }
        return props;
    }

    public void setNotificationIcon(int iconId) {
        Editor editor = sdkPreferences.edit();
        editor.putInt(Constants.NOTIFICATION_ICON, iconId);
        editor.commit();
    }

    public void setNotificationSound(String soundUri) {
        Editor editor = sdkPreferences.edit();
        editor.putString(Constants.NOTIFICATION_SOUND, soundUri);
        editor.commit();
    }

}
