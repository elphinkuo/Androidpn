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

    public MainReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "MainReceiver.onReceive()...");

        String action = intent.getAction();
        Log.d(LOGTAG, "action=" + action);

        if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {

            String notificationId = intent
                    .getStringExtra(Constants.NOTIFICATION_ID);
            String notificationApiKey = intent
                    .getStringExtra(Constants.NOTIFICATION_API_KEY);
            String notificationTitle = intent
                    .getStringExtra(Constants.NOTIFICATION_TITLE);
            String notificationMessage = intent
                    .getStringExtra(Constants.NOTIFICATION_MESSAGE);
            String notificationTicker = intent
                    .getStringExtra(Constants.NOTIFICATION_TICKER);
            String notificationUrl = intent
                    .getStringExtra(Constants.NOTIFICATION_URL);

            Log.w(LOGTAG, "notificationId=" + notificationId);
            Log.w(LOGTAG, "notificationApiKey=" + notificationApiKey);
            Log.w(LOGTAG, "notificationTitle=" + notificationTitle);
            Log.w(LOGTAG, "notificationMessage=" + notificationMessage);
            Log.w(LOGTAG, "notificationTicker=" + notificationTicker);
            Log.w(LOGTAG, "notificationUrl=" + notificationUrl);

            Notifier notifier = new Notifier(context);
            notifier.notify(notificationId, notificationApiKey,
                    notificationTitle, notificationMessage, notificationTicker,
                    notificationUrl);
            
            intent.removeExtra(Constants.NOTIFICATION_ID);
            intent.removeExtra(Constants.NOTIFICATION_API_KEY);
            intent.removeExtra(Constants.NOTIFICATION_TITLE);
            intent.removeExtra(Constants.NOTIFICATION_MESSAGE);
            intent.removeExtra(Constants.NOTIFICATION_TICKER);
            intent.removeExtra(Constants.NOTIFICATION_URL);
            
            Log.d(LOGTAG, "notifier.notify()... done!");

        } else if (Constants.ACTION_NOTIFICATION_CLICKED.equals(action)) {

            String notificationId = intent
                    .getStringExtra(Constants.NOTIFICATION_ID);
            String notificationApiKey = intent
                    .getStringExtra(Constants.NOTIFICATION_API_KEY);
            String notificationTitle = intent
                    .getStringExtra(Constants.NOTIFICATION_TITLE);
            String notificationMessage = intent
                    .getStringExtra(Constants.NOTIFICATION_MESSAGE);
            String notificationTicker = intent
                    .getStringExtra(Constants.NOTIFICATION_TICKER);
            String notificationUrl = intent
                    .getStringExtra(Constants.NOTIFICATION_URL);

            Log.e(LOGTAG, "notificationId=" + notificationId);
            Log.e(LOGTAG, "notificationApiKey=" + notificationApiKey);
            Log.e(LOGTAG, "notificationTitle=" + notificationTitle);
            Log.e(LOGTAG, "notificationMessage=" + notificationMessage);
            Log.e(LOGTAG, "notificationTicker=" + notificationTicker);
            Log.e(LOGTAG, "notificationUrl=" + notificationUrl);

            Intent detailsIntent = new Intent();
            detailsIntent.setClassName(context.getPackageName(),
                    NotificationDetailsActivity.class.getName());
            detailsIntent.putExtras(intent.getExtras());
            //            detailsIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            //            detailsIntent.putExtra(Constants.NOTIFICATION_API_KEY, notificationApiKey);
            //            detailsIntent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
            //            detailsIntent.putExtra(Constants.NOTIFICATION_MESSAGE, notificationMessage);
            //            detailsIntent.putExtra(Constants.NOTIFICATION_TICKER, notificationTicker);
            //            detailsIntent.putExtra(Constants.NOTIFICATION_URL, notificationUrl);
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 0x10000000

            try {
                context.startActivity(detailsIntent);
            } catch (ActivityNotFoundException e) {
                Toast toast = Toast.makeText(context,
                        "No app found to handle this request",
                        Toast.LENGTH_LONG);
                toast.show();
            }

            Log.d(LOGTAG, "detailsActivity... started!");            

        } else if (Constants.ACTION_NOTIFICATION_CLEARED.equals(action)) {
            //
        }

    }

}
