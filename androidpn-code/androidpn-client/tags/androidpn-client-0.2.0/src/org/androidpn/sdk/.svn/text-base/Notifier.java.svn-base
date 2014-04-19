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

import java.util.Random;

import org.androidpn.demoapp.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class Notifier {

    private static final String LOGTAG = Notifier.class.getName();

    private static final Random random = new Random(System.currentTimeMillis());

    private Context context;

    private NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notify(String notificationId, String apiKey, String title,
            String message, String ticker, String url) {

        Log.d(LOGTAG, "notify()...");

        //        final Notification notification = new Notification();
        //        notification.number++;
        //        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //
        //        Intent toLaunch = new Intent(Notifications.this, Notifications.class);
        //        PendingIntent intentBack = PendingIntent.getActivity(
        //                Notifications.this, 0, toLaunch, 0);
        //
        //        notification.setLatestEventInfo(Notifications.this, "Hi there!",
        //                "This is even more text.", intentBack);

        Notification notification = new Notification();

        int icon = getNotificationIcon(context);
        Uri sound = getNotificationSound(context);

        notification.icon = icon;
        notification.sound = sound;
        // notification.number++;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent positiveIntent = new Intent(
                "org.androidpn.sdk.NOTIFICATION_CLICKED");
        positiveIntent.putExtra("NOTIFICATION_ID", notificationId);
        positiveIntent.putExtra("NOTIFICATION_API_KEY", apiKey);
        positiveIntent.putExtra("NOTIFICATION_TITLE", title);
        positiveIntent.putExtra("NOTIFICATION_MESSAGE", message);
        positiveIntent.putExtra("NOTIFICATION_TICKER", ticker);
        positiveIntent.putExtra("NOTIFICATION_URL", url);

        PendingIntent positivePendingIntent = PendingIntent.getBroadcast(
                context, 0, positiveIntent, 0);

        Intent negativeIntent = new Intent(
                "org.androidpn.sdk.NOTIFICATION_CLEARED");
        negativeIntent.putExtra("NOTIFICATION_ID", notificationId);
        negativeIntent.putExtra("NOTIFICATION_API_KEY", apiKey);

        PendingIntent negativePendingIntent = PendingIntent.getBroadcast(
                context, 0, negativeIntent, 0);

        notification.tickerText = ticker;
        notification.when = System.currentTimeMillis();
        notification.setLatestEventInfo(context, title, message,
                positivePendingIntent);
        notification.deleteIntent = negativePendingIntent;
        notificationManager.notify(random.nextInt(), notification);

    }

    public static int getNotificationIcon(Context context) {
        SharedPreferences sdkPreferences = context.getSharedPreferences(
                "SdkPreferences", 0);
        return sdkPreferences.getInt("NOTIFICATION_ICON",
                R.drawable.notification);
    }

    public static Uri getNotificationSound(Context context) {
        SharedPreferences sdkPreferences = context.getSharedPreferences(
                "SdkPreferences", 0);
        String sound = sdkPreferences.getString("NOTIFICATION_SOUND", null);
        return (sound != null) ? Uri.parse(sound) : null;
    }

    //    public static String stripTags(String text) {
    //        text = text.replaceAll("<b>", "");
    //        text = text.replaceAll("</b>", "");
    //        text = text.replaceAll("<i>", "");
    //        text = text.replaceAll("</i>", "");
    //        return text;
    //    }

}
