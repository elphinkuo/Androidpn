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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Service that continues to run in background and respond to the push 
 * notification events from the server. This should be registered as service
 * in AndroidManifest.xml. 
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationService extends Service {

    public static final String SERVICE_NAME = "org.androidpn.client.NotificationService";

    private static final String LOGTAG = LogUtil.makeLogTag(NotificationService.class);

    private TelephonyManager telephonyManager;

    private WifiManager wifiManager;

    private ConnectivityManager connectivityManager;

    private final PhoneStateListener phoneStateListener;

    private BroadcastReceiver phoneStateReceiver;

    private ExecutorService executorService;

    private TaskSubmitter taskSubmitter;

    private TaskTracker taskTracker;

    private XmppManager xmppManager;

    //    private BroadcastReceiver notificationReceiver = new NotificationReceiver();

    //    private SharedPreferences clientPrefs;

    //    private String deviceId;

    public NotificationService() {
        executorService = Executors.newSingleThreadExecutor();
        taskSubmitter = new TaskSubmitter(this);
        taskTracker = new TaskTracker(this);
        phoneStateListener = new PhoneStateChangeListener(this);
        phoneStateReceiver = new PhoneStateReceiver(this);
    }

    @Override
    public void onCreate() {
        Log.d(LOGTAG, "onCreate()...");

        //        clientPrefs = getSharedPreferences(Constants.CLIENT_PREFERENCES,
        //                Context.MODE_PRIVATE);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //        deviceId = telephonyManager.getDeviceId();
        //        Log.d(LOGTAG, "deviceId=" + deviceId);
        //
        //        Editor editor = clientPrefs.edit();
        //        editor.putString(Constants.DEVICE_ID, deviceId);
        //        editor.commit();
        //
        //        if (deviceId == null || deviceId.trim().length() == 0
        //                || deviceId.matches("0+")) {
        //            if (clientPrefs.contains("EMULATOR_DEVICE_ID")) {
        //                deviceId = clientPrefs.getString(
        //                        Constants.EMULATOR_DEVICE_ID, "");
        //            } else {
        //                deviceId = (new StringBuilder("EMU")).append(
        //                        (new Random(System.currentTimeMillis())).nextLong())
        //                        .toString();
        //                editor.putString(Constants.EMULATOR_DEVICE_ID, deviceId);
        //                editor.commit();
        //            }
        //        }

        xmppManager = new XmppManager(this, taskSubmitter, taskTracker);

        taskSubmitter.submit(new Runnable() {

            private final NotificationService notificationService = NotificationService.this;

            @Override
            public void run() {
                NotificationService.start(notificationService);
            }

        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(LOGTAG, "onStart()...");
    }

    @Override
    public void onDestroy() {
        Log.d(LOGTAG, "onDestroy()...");
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, "onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOGTAG, "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOGTAG, "onUnbind()...");
        return true;
    }

    // ================

    private void registerPhoneStateReceiver() {
        Log.d(LOGTAG, "registerPhoneStateReceiver()...");
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(phoneStateReceiver, intentfilter);
    }

    private void unregisterPhoneStateReceiver() {
        Log.d(LOGTAG, "unregisterPhoneStateReceiver()...");
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(phoneStateReceiver);
    }

    //    private void registerNotificationReceiver() {
    //        IntentFilter filter = new IntentFilter();
    //        filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
    //        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
    //        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
    //        registerReceiver(this.notificationReceiver, filter);
    //    }
    //    
    //    private void unregisterNotificationReceiver() {
    //        unregisterReceiver(this.notificationReceiver);
    //    }

    private void start() {
        Log.d(LOGTAG, "start()...");
        // registerNotificationReceiver();
        registerPhoneStateReceiver();
        Intent intent = getIntent();
        startService(intent);
        xmppManager.connect();
    }

    private void stop() {
        Log.d(LOGTAG, "stop()...");
        // unregisterNotificationReceiver();
        unregisterPhoneStateReceiver();
        xmppManager.disconnect();
        executorService.shutdown();
    }

    private void restart() {
        Log.d(LOGTAG, "restart()...");

        taskSubmitter.submit(new Runnable() {

            final NotificationService notificationService = NotificationService.this;

            public void run() {
                // NotificationService.getXmppManager(notificationService).disconnect();
                NotificationService.getXmppManager(notificationService).connect();
            }
        });

    }

    // ================

    public static Intent getIntent() {
        return new Intent(SERVICE_NAME);
    }

    public static ExecutorService getExecutorService(NotificationService notificationService) {
        return notificationService.executorService;
    }

    public static TaskTracker getTaskTracker(NotificationService notificationService) {
        return notificationService.taskTracker;
    }

    public static void start(NotificationService notificationService) {
        notificationService.start();
    }

    public static void restart(NotificationService notificationService) {
        notificationService.restart();
    }

    public static TelephonyManager getTelephonyManager(NotificationService notificationService) {
        return notificationService.telephonyManager;
    }

    public static WifiManager getWifiManager(NotificationService notificationService) {
        return notificationService.wifiManager;
    }

    public static ConnectivityManager getConnectivityManager(
            NotificationService notificationService) {
        return notificationService.connectivityManager;
    }

    public static XmppManager getXmppManager(NotificationService notificationService) {
        return notificationService.xmppManager;
    }

    public static String getState(int state) {
        switch (state) {
        case 0: // '\0'
            return "DATA_DISCONNECTED";
        case 1: // '\001'
            return "DATA_CONNECTING";
        case 2: // '\002'
            return "DATA_CONNECTED";
        case 3: // '\003'
            return "DATA_SUSPENDED";
        }
        return "DATA_<UNKNOWN>";
    }

    //===============

    public class TaskSubmitter {

        final NotificationService notificationService;

        TaskSubmitter(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        @SuppressWarnings("unchecked")
        public Future submit(Runnable task) {
            Future result = null;
            if (!NotificationService.getExecutorService(notificationService).isTerminated()
                    && !NotificationService.getExecutorService(notificationService)
                            .isShutdown() && task != null) {
                result = NotificationService.getExecutorService(notificationService).submit(
                        task);
            }
            return result;
        }

    }

    public class TaskTracker {

        final NotificationService notificationService;

        public int count;

        public TaskTracker(NotificationService notificationService) {
            this.notificationService = notificationService;
            this.count = 0;
        }

        public void increase() {
            synchronized (NotificationService.getTaskTracker(notificationService)) {
                NotificationService.getTaskTracker(notificationService).count++;
                Log.d(LOGTAG, "Incremented task count to " + count);
            }
        }

        public void decrease() {
            synchronized (NotificationService.getTaskTracker(notificationService)) {
                NotificationService.getTaskTracker(notificationService).count--;
                Log.d(LOGTAG, "Decremented task count to " + count);
            }
        }

    }

}
