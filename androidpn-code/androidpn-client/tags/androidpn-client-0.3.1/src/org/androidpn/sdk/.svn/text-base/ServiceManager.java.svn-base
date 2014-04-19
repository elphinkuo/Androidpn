/*
 * Copyright (C) 2010 The Androidpn Team
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public final class ServiceManager {

    public static final String ANDROIDPN_HOST = "ANDROIDPN_HOST";

    public static final String ANDROIDPN_PORT = "ANDROIDPN_PORT";

    public static final String ANDROIDPN_API_KEY = "ANDROIDPN_API_KEY";

    private static final String LOGTAG = ServiceManager.class.getName();

    private Context context;

    private SharedPreferences sdkPreferences;

    private Properties sdkProperties;
    
    private String sdkVersion;

    private String apiKey;

    private String xmppHost;

    private String xmppPort;

    private String callbackActivityPackageName;

    private String callbackActivityClassName;

    public ServiceManager(Context context) {
        this.context = context;
        this.sdkPreferences = context.getSharedPreferences(
                Constants.SDK_PREFERENCES, Context.MODE_PRIVATE);

        if (context instanceof Activity) {
            Log.i(LOGTAG, "Callback Activity...");
            Activity callbackActivity = (Activity) context;
            callbackActivityPackageName = callbackActivity.getPackageName();
            callbackActivityClassName = callbackActivity.getClass().getName();
        }

        Log.i(LOGTAG, "callbackActivityPackageName="
                + callbackActivityPackageName);
        Log.i(LOGTAG, "callbackActivityClassName=" + callbackActivityClassName);

        this.sdkProperties = loadSdkProperties();
        this.sdkVersion = sdkProperties.getProperty("sdkVersion");
        this.xmppHost = sdkProperties.getProperty("xmppHost", "127.0.0.1");
        this.xmppPort = sdkProperties.getProperty("xmppPort", "5222");
        //        xmppHost = getMetaDataValue(ANDROIDPN_HOST, "127.0.0.1");
        //        xmppPort = getMetaDataValue(ANDROIDPN_PORT, "5222");
        Log.i(LOGTAG, "xmppHost=" + xmppHost);
        Log.i(LOGTAG, "xmppPort=" + xmppPort);

        //        this.apiKey = getApiKey(context);
        apiKey = getMetaDataValue(ANDROIDPN_API_KEY);
        Log.i(LOGTAG, "apiKey=" + apiKey);

        //        if (apiKey == null) {
        //            Log.e(LOGTAG, "Please set the androidpn api key in the manifest file.");
        //            //throw new RuntimeException();
        //        }

        Editor editor = sdkPreferences.edit();
        editor.putString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME,
                callbackActivityPackageName);
        editor.putString(Constants.CALLBACK_ACTIVITY_CLASS_NAME,
                callbackActivityClassName);
        editor.putString(Constants.API_KEY, apiKey);
        editor.putString(Constants.XMPP_HOST, xmppHost);
        editor.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
        editor.commit();
        // Log.i(LOGTAG, "sdkPreferences=" + sdkPreferences.toString());
    }

    public void startService() {
        // Intent intent = new Intent(MainService.SERVICE_NAME);
        // context.startService(intent);

        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = MainService.getIntent();
                context.startService(intent);
            }
        });
        serviceThread.start();
    }

    public void stopService() {
        // Intent intent = new Intent(MainService.SERVICE_NAME);
        Intent intent = MainService.getIntent();
        context.stopService(intent);
    }

    //    private String getMetaDataValue(String name, String def) {
    //        String value = getMetaDataValue(name);
    //        return (value == null) ? def : value;
    //    }

    private String getMetaDataValue(String name) {
        Object value = null;
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context
                    .getPackageName(), 128);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }
        } catch (NameNotFoundException e) {
            throw new InvalidFormatException(
                    "Could not read the name in the manifest file.", e);
        }
        if (value == null) {
            throw new InvalidFormatException("The name '" + name
                    + "' is not defined in the manifest file's meta data.");
        }
        return value.toString();
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
    
    public String getSdkVersion() {
        return this.sdkVersion;
    }
    
    public String getApiKey() {
        return this.apiKey;
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
