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
package org.androidpn.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/** 
 * This class is to manage the notificatin service and to load the configuration.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public final class ServiceManager {

    private static final String LOGTAG = LogUtil
            .makeLogTag(ServiceManager.class);

    private Context context;

    private SharedPreferences clientPrefs;

    private Properties clientProps;

    private String version;

    private String apiKey;

    private String xmppHost;

    private String xmppPort;

    //    private String callbackActivityPackageName;
    //
    //    private String callbackActivityClassName;

    public ServiceManager(Context context) {
        this.context = context;        
        version = Constants.CLIENT_VERSION;
        
        apiKey = getMetaDataValue(Constants.ANDROIDPN_API_KEY);
        Log.i(LOGTAG, "apiKey=" + apiKey);
        //        if (apiKey == null) {
        //            Log.e(LOGTAG, "Please set the androidpn api key in the manifest file.");
        //            throw new RuntimeException();
        //        }

        clientProps = loadProperties();
        xmppHost = clientProps.getProperty("xmppHost", "127.0.0.1");
        xmppPort = clientProps.getProperty("xmppPort", "5222");
        Log.i(LOGTAG, "xmppHost=" + xmppHost);
        Log.i(LOGTAG, "xmppPort=" + xmppPort);

        clientPrefs = context.getSharedPreferences(
                Constants.CLIENT_PREFERENCES, Context.MODE_PRIVATE);
        Editor editor = clientPrefs.edit();
        editor.putString(Constants.ANDROIDPN_API_KEY, apiKey);
        editor.putString(Constants.VERSION, version);
        editor.putString(Constants.XMPP_HOST, xmppHost);
        editor.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
        editor.commit();
        // Log.i(LOGTAG, "clientPrefs=" + clientPrefs.toString());
    }

    public void startService() {
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NotificationService.getIntent();
                context.startService(intent);
            }
        });
        serviceThread.start();
    }

    public void stopService() {
        Intent intent = NotificationService.getIntent();
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

    private Properties loadProperties() {
        InputStream in = null;
        Properties props = null;
        try {
            in = getClass().getResourceAsStream(
                    "/org/androidpn/client/client.properties");
            if (in != null) {
                props = new Properties();
                props.load(in);
            } else {
                Log.e(LOGTAG, "Could not find the properties file.");
            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Could not find the properties file.", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Throwable ignore) {
                }
        }
        return props;
    }

    //    public String getVersion() {
    //        return version;
    //    }
    //
    //    public String getApiKey() {
    //        return apiKey;
    //    }

    public void setNotificationIcon(int iconId) {
        Editor editor = clientPrefs.edit();
        editor.putInt(Constants.NOTIFICATION_ICON, iconId);
        editor.commit();
    }

    public void setNotificationSound(String soundUri) {
        Editor editor = clientPrefs.edit();
        editor.putString(Constants.NOTIFICATION_SOUND, soundUri);
        editor.commit();
    }

}
