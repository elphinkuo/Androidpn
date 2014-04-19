/*
 * Copyright (C) 2010 The Androidpn Team
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.push;

import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationManager {

    protected final Log log = LogFactory.getLog(getClass());

    protected SessionManager sessionManager;

    public NotificationManager() {
        sessionManager = SessionManager.getInstance();
    }

    private IQ createNotificationIQ(String apiKey, String title,
            String message, String ticker, String url) {
        // TODO Create an unique id
        String id = String.valueOf(System.currentTimeMillis());

        Element notification = DocumentHelper.createElement(QName.get(
                "notification", "androidpn:iq:notification"));
        notification.addElement("id").setText(id);
        notification.addElement("apiKey").setText(apiKey);
        notification.addElement("title").setText(title);
        notification.addElement("message").setText(message);
        notification.addElement("ticker").setText(ticker);
        notification.addElement("url").setText(url);

        IQ iq = new IQ();
        iq.setType(IQ.Type.set);
        iq.setChildElement(notification);

        return iq;
    }

    public void sendBroadcast(String apiKey, String title, String message,
            String ticker, String url) {
        log.debug("sendBroadcast()...");
        IQ notificationIQ = createNotificationIQ(apiKey, title, message,
                ticker, url);
        for (ClientSession session : sessionManager.getSessions()) {
            if (session.getPresence().isAvailable()) {
                notificationIQ.setTo(session.getAddress());
                session.deliver(notificationIQ);
            }
        }
    }

    public void sendNotifcationToUser(String apiKey, String username,
            String title, String message, String ticker, String url) {
        log.debug("sendNotifcationToUser()...");
        IQ notificationIQ = createNotificationIQ(apiKey, title, message,
                ticker, url);
        ClientSession session = sessionManager.getSession(username);
        if (session != null) {
            if (session.getPresence().isAvailable()) {
                notificationIQ.setTo(session.getAddress());
                session.deliver(notificationIQ);
            }
        }
    }

}
