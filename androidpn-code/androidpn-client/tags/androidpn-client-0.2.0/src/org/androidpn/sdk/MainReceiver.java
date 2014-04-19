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

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public final class MainReceiver extends BroadcastReceiver {

    private static final String LOGTAG = MainReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOGTAG, "MainReceiver.onReceive()...");

        String action = intent.getAction();
        Log.d(LOGTAG, "action=" + action);

        if ("org.androidpn.sdk.SHOW_NOTIFICATION".equals(action)) {

            String notificationId = intent.getStringExtra("NOTIFICATION_ID");
            String notificationApiKey = intent
                    .getStringExtra("NOTIFICATION_API_KEY");
            String notificationTitle = intent
                    .getStringExtra("NOTIFICATION_TITLE");
            String notificationMessage = intent
                    .getStringExtra("NOTIFICATION_MESSAGE");
            String notificationTicker = intent
                    .getStringExtra("NOTIFICATION_TICKER");
            String notificationUrl = intent.getStringExtra("NOTIFICATION_URL");

            Log.d(LOGTAG, "notificationId=" + notificationId);
            Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
            Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
            Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
            Log.d(LOGTAG, "notificationTicker=" + notificationTicker);
            Log.d(LOGTAG, "notificationUrl=" + notificationUrl);

            Notifier notifier = new Notifier(context);
            notifier.notify(notificationId, notificationApiKey,
                    notificationTitle, notificationMessage, notificationTicker,
                    notificationUrl);
            Log.d(LOGTAG, "notifier.notify()...done!");

        } else if ("org.androidpn.sdk.NOTIFICATION_CLICKED".equals(action)) {

            String notificationId = intent.getStringExtra("NOTIFICATION_ID");
            String notificationApiKey = intent
                    .getStringExtra("NOTIFICATION_API_KEY");
            String notificationTitle = intent
                    .getStringExtra("NOTIFICATION_TITLE");
            String notificationMessage = intent
                    .getStringExtra("NOTIFICATION_MESSAGE");
            String notificationTicker = intent
                    .getStringExtra("NOTIFICATION_TICKER");
            String notificationUrl = intent.getStringExtra("NOTIFICATION_URL");

            //            Log.d(LOGTAG, "notificationId=" + notificationId);
            //            Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
            //            Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
            //            Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
            //            Log.d(LOGTAG, "notificationTicker=" + notificationTicker);
            //            Log.d(LOGTAG, "notificationUrl=" + notificationUrl);

            Intent detailsIntent = new Intent();
            detailsIntent.setClassName(context.getPackageName(),
                    NotificationDetailsActivity.class.getName());
            detailsIntent.putExtra("NOTIFICATION_ID", notificationId);
            detailsIntent.putExtra("NOTIFICATION_API_KEY", notificationApiKey);
            detailsIntent.putExtra("NOTIFICATION_TITLE", notificationTitle);
            detailsIntent.putExtra("NOTIFICATION_MESSAGE", notificationMessage);
            detailsIntent.putExtra("NOTIFICATION_TICKER", notificationTicker);
            detailsIntent.putExtra("NOTIFICATION_URL", notificationUrl);
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 0x10000000

            try {
                context.startActivity(detailsIntent);
            } catch (ActivityNotFoundException e) {
                Toast toast = Toast.makeText(context,
                        "No app found to handle this request",
                        Toast.LENGTH_LONG);
                toast.show();
            }

        } else if ("org.androidpn.sdk.NOTIFICATION_CLEARED".equals(action)) {
            //
        }

    }

}
