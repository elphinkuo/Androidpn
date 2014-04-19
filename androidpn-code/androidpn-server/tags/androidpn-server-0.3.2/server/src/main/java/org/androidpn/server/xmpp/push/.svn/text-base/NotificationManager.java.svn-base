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
