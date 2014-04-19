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

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;
import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationPacketListener implements PacketListener {

    private static final String LOGTAG = NotificationPacketListener.class
            .getName();

    private final XmppManager xmppManager;

    public NotificationPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processPacket(Packet packet) {

        Log.e(LOGTAG, "NotificationPacketListener.processPacket()...");
        Log.e(LOGTAG, "packet.toXML()=" + packet.toXML());

        if (packet instanceof NotificationIQ) {
            NotificationIQ notification = (NotificationIQ) packet;

            if (notification.getChildElementXML().contains(
                    "androidpn:iq:notification")) {
                String notificationId = notification.getId();
                String notificationApiKey = notification.getApiKey();
                String notificationTitle = notification.getTitle();
                String notificationMessage = notification.getMessage();
                String notificationTicker = notification.getTicker();
                String notificationUrl = notification.getUrl();

//                Log.d(LOGTAG, "notificationId=" + notificationId);
//                Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
//                Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
//                Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
//                Log.d(LOGTAG, "notificationTicker=" + notificationTicker);
//                Log.d(LOGTAG, "notificationUrl=" + notificationUrl);

                Intent intent = new Intent(
                        "org.androidpn.sdk.SHOW_NOTIFICATION");
                intent.putExtra("NOTIFICATION_ID", notificationId);
                intent.putExtra("NOTIFICATION_API_KEY", notificationApiKey);
                intent.putExtra("NOTIFICATION_TITLE", notificationTitle);
                intent.putExtra("NOTIFICATION_MESSAGE", notificationMessage);
                intent.putExtra("NOTIFICATION_TICKER", notificationTicker);
                intent.putExtra("NOTIFICATION_URL", notificationUrl);

                XmppManager.getContext(xmppManager).sendBroadcast(intent);
            }
        }

    }

}
