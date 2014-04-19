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
    
    private static final String LOGTAG = NotificationPacketListener.class.getName();

    private final XmppManager xmppManager;

    public NotificationPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processPacket(Packet packet) {

        Log.d(LOGTAG, "NotificationPacketListener.processPacket()...");
        Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());

        if (packet instanceof XmppNotification) {
            XmppNotification notification = (XmppNotification) packet;

            if (notification.getChildElementXML().contains(
                    "androidpn.iq.notification")) {

                String notificationId = notification.getId();
                String notificationAppKey = notification.getAppKey();
                String notificationFrom = notification.getFrom();
                String notificationMessage = notification.getMessage();
                String notificationTicker = notification.getTicker();
                String notificationUrl = notification.getUrl();

                Intent intent = new Intent(
                        "org.androidpn.sdk.SHOW_NOTIFICATION");
                intent.putExtra("NOTIFICATION_ID", notificationId);
                intent.putExtra("NOTIFICATION_APP_KEY", notificationAppKey);
                intent.putExtra("NOTIFICATION_FROM", notificationFrom);
                intent.putExtra("NOTIFICATION_MESSAGE", notificationMessage);
                intent.putExtra("NOTIFICATION_TICKER", notificationTicker);
                intent.putExtra("NOTIFICATION_URL", notificationUrl);

                XmppManager.getContext(xmppManager).sendBroadcast(intent);
            }

        }

        // TODO remove this later
        Intent intent = new Intent("org.androidpn.sdk.SHOW_NOTIFICATION");
        intent.putExtra("NOTIFICATION_ID", "12345");
        intent.putExtra("NOTIFICATION_APP_KEY", "1234567890");
        intent.putExtra("NOTIFICATION_FROM", "Demo App");
        intent.putExtra("NOTIFICATION_MESSAGE", "This is a test message.");
        intent.putExtra("NOTIFICATION_TICKER", "This is a test message.");
        intent.putExtra("NOTIFICATION_URL", "");

        XmppManager.getContext(xmppManager).sendBroadcast(intent);

    }

}
