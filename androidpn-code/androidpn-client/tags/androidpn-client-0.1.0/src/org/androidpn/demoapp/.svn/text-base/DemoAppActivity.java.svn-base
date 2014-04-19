package org.androidpn.demoapp;

import org.androidpn.sdk.ServiceManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class DemoAppActivity extends Activity {

    private ServiceManager serviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //        Intent service = new Intent("org.androidpn.sdk.MainService");
        //        //service.putExtra("update-rate", 5000);
        //        startService(service);

        final Context context = this;

        //        Thread serviceThread = new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                serviceManager = new ServiceManager(context);
        //                serviceManager.startService();
        //            }
        //        });
        //        serviceThread.start();

        serviceManager = new ServiceManager(context);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();

        Log.d(getClass().getSimpleName(), "onCreate()...");
    }

}