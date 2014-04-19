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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public final class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        XLog.info("MainReceiver.onReceive()...");

        String action = intent.getAction();
        XLog.info("action=" + action);

        //        Log.e(getClass().getSimpleName(), "action=" + action);
        //        System.out.println("action=" + action);

        if ("org.androidpn.sdk.SHOW_NOTIFICATION".equals(action)) {
            String id = intent.getStringExtra("NOTIFICATION_ID");
            String appKey = intent.getStringExtra("NOTIFICATION_APP_KEY");
            String from = intent.getStringExtra("NOTIFICATION_FROM");
            String message = intent.getStringExtra("NOTIFICATION_MESSAGE");
            String ticker = intent.getStringExtra("NOTIFICATION_TICKER");
            String url = intent.getStringExtra("NOTIFICATION_URL");

            XLog.debug("id=" + id);
            XLog.debug("appKey=" + appKey);
            XLog.debug("title=" + from);
            XLog.debug("details=" + message);
            XLog.debug("ticker=" + ticker);
            XLog.debug("url=" + url);

            Notifier notifier = new Notifier(context);
            notifier.notify(id, appKey, from, message, ticker, url);
            XLog.info("notifier.notify()...done!");
            
        }
        else if ("org.androidpn.sdk.NOTIFICATION_CLICKED".equals(action)) {

        }
        else if ("org.androidpn.sdk.NOTIFICATION_CLEARED".equals(action)) {

        }

    }

}
