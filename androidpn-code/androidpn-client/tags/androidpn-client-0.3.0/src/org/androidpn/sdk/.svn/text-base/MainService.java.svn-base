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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
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

    private static final String LOGTAG = MainService.class.getName();

    //    private SharedPreferences sdkPreferences;

    private TelephonyManager telephonyManager;

    private WifiManager wifiManager;

    private ConnectivityManager connectivityManager;

    //    private final PhoneStateListener phoneStateListener;
    //
    //    private BroadcastReceiver phoneStateReceiver;

    private ExecutorService executorService;

    private TaskSubmitter taskSubmitter;

    private TaskTracker taskTracker;

    private XmppManager xmppManager;

    //    private String deviceId;

    public MainService() {
        executorService = Executors.newSingleThreadExecutor();
        taskSubmitter = new TaskSubmitter(this);
        taskTracker = new TaskTracker(this);
        //        phoneStateListener = new SdkPhoneStateListener(this);
    }

    @Override
    public void onCreate() {
        Log.d(LOGTAG, "onCreate()...");

        //        sdkPreferences = getSharedPreferences(Constants.SDK_PREFERENCES,
        //                Context.MODE_PRIVATE);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //        deviceId = telephonyManager.getDeviceId();
        //        Log.d(LOGTAG, "deviceId=" + deviceId);
        //
        //        Editor editor = sdkPreferences.edit();
        //        editor.putString(Constants.DEVICE_ID, deviceId);
        //        editor.commit();
        //
        //        if (deviceId == null || deviceId.trim().length() == 0
        //                || deviceId.matches("0+")) {
        //            if (sdkPreferences.contains("EMULATOR_DEVICE_ID")) {
        //                deviceId = sdkPreferences.getString(
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

            private final MainService mainService = MainService.this;

            @Override
            public void run() {
                MainService.start(mainService);
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

    //    private void registerPhoneStateReceiver() {
    //        Log.d(LOGTAG, "registerPhoneStateReceiver()...");
    //        telephonyManager.listen(phoneStateListener,
    //                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    //        IntentFilter intentfilter = new IntentFilter();
    //        intentfilter.addAction("android.net.wifi.STATE_CHANGE");
    //        registerReceiver(phoneStateReceiver, intentfilter);
    //    }
    //
    //    private void unregisterPhoneStateReceiver() {
    //        Log.d(LOGTAG, "unregisterPhoneStateReceiver()...");
    //        telephonyManager.listen(phoneStateListener,
    //                PhoneStateListener.LISTEN_NONE);
    //        unregisterReceiver(phoneStateReceiver);
    //    }

    private void start() {
        Log.d(LOGTAG, "start()...");
        // registerPhoneStateReceiver();
        Intent intent = getIntent();
        startService(intent);
        xmppManager.connect();
    }

    private void stop() {
        Log.d(LOGTAG, "stop()...");
        // unregisterPhoneStateReceiver();
        // mHandler.removeMessages(what);
        xmppManager.disconnect();
        executorService.shutdown();
    }

    private void restart() {
        Log.d(LOGTAG, "restart()...");

        taskSubmitter.submit(new Runnable() {

            final MainService mainService = MainService.this;

            public void run() {
                MainService.getXmppManager(mainService).connect();
            }
        });

    }

    // ================

    public static Intent getIntent() {
        return new Intent(SERVICE_NAME);
    }

    public static ExecutorService getExecutorService(MainService mainService) {
        return mainService.executorService;
    }

    public static TaskTracker getTaskTracker(MainService mainService) {
        return mainService.taskTracker;
    }

    public static void start(MainService mainService) {
        mainService.start();
    }

    public static void restart(MainService mainService) {
        mainService.restart();
    }

    public static TelephonyManager getTelephonyManager(MainService mainService) {
        return mainService.telephonyManager;
    }

    public static WifiManager getWifiManager(MainService mainService) {
        return mainService.wifiManager;
    }

    public static ConnectivityManager getConnectivityManager(
            MainService mainService) {
        return mainService.connectivityManager;
    }

    public static XmppManager getXmppManager(MainService mainService) {
        return mainService.xmppManager;
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

        final MainService mainService;

        TaskSubmitter(MainService mainService) {
            this.mainService = mainService;
        }

        @SuppressWarnings("unchecked")
        public Future submit(Runnable task) {
            Future result = null;
            if (!MainService.getExecutorService(mainService).isTerminated()
                    && !MainService.getExecutorService(mainService)
                            .isShutdown() && task != null) {
                result = MainService.getExecutorService(mainService).submit(
                        task);
            }
            return result;
        }

    }

    public class TaskTracker {

        final MainService mainService;

        public int count;

        public TaskTracker(MainService mainService) {
            this.mainService = mainService;
            this.count = 0;
        }

        public void increase() {
            synchronized (MainService.getTaskTracker(mainService)) {
                MainService.getTaskTracker(mainService).count++;
                Log.d(getClass().getName(), "Incremented task count to "
                        + count);
            }
        }

        public void decrease() {
            synchronized (MainService.getTaskTracker(mainService)) {
                MainService.getTaskTracker(mainService).count--;
                Log.d(getClass().getName(), "Decremented task count to "
                        + count);
//                if (MainService.getTaskTracker(mainService).count == 0) {
//                    // MainService.start(mainService);
//                    Intent intent = MainService.getIntent();
//                    mainService.startService(intent);
//                }
            }
        }

    }

}
