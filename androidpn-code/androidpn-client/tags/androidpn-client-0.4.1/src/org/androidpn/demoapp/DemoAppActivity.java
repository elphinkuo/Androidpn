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
package org.androidpn.demoapp;

import org.androidpn.client.ServiceManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * This is an androidpn client demo application.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class DemoAppActivity extends Activity {

    private ServiceManager serviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        serviceManager = new ServiceManager(this);
        // serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();

        Log.d("DemoAppActivity", "onCreate()...");
    }

}