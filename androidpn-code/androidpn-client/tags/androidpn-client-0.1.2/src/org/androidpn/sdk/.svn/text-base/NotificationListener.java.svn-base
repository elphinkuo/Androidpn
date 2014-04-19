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

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationListener implements PacketListener {

    private final XmppManager xmppManager;

    public NotificationListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processPacket(Packet packet) {

        XLog.debug("NotificationListener.processPacket()...");
        XLog.debug("packet.toXML()=" + packet.toXML());

        if (packet instanceof XmppNotification) {
            XmppNotification notification = (XmppNotification) packet;

            if (notification.getChildElementXML().contains(
                    "androidpn.iq.notification")) {

                String id = notification.getId();
                String appKey = notification.getAppKey();
                String from = notification.getFrom();
                String message = notification.getMessage();
                String ticker = notification.getTicker();
                String url = notification.getUrl();

                Intent intent = new Intent(
                        "org.androidpn.sdk.SHOW_NOTIFICATION");
                intent.putExtra("NOTIFICATION_ID", id);
                intent.putExtra("NOTIFICATION_APP_KEY", appKey);
                intent.putExtra("NOTIFICATION_FROM", from);
                intent.putExtra("NOTIFICATION_MESSAGE", message);
                intent.putExtra("NOTIFICATION_TICKER", ticker);
                intent.putExtra("NOTIFICATION_URL", url);

                XmppManager.getContext(xmppManager).sendBroadcast(intent);
            }

        }

        //        // TEST
        //        Intent intent = new Intent("org.androidpn.sdk.SHOW_NOTIFICATION");
        //        intent.putExtra("NOTIFICATION_ID", "12345");
        //        intent.putExtra("NOTIFICATION_APP_KEY", "1234567890");
        //        intent.putExtra("NOTIFICATION_FROM", "From Here");
        //        intent.putExtra("NOTIFICATION_MESSAGE", "This is a test message.");
        //        intent.putExtra("NOTIFICATION_TICKER", "Ticker");
        //        intent.putExtra("NOTIFICATION_URL", "");
        //
        //        XmppManager.getContext(xmppManager).sendBroadcast(intent);

    }

}
