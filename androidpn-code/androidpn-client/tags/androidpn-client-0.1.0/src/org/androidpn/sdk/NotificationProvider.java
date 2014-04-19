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

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationProvider implements IQProvider {

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {

        XmppNotification notification = new XmppNotification();
        for (boolean done = false; !done;) {
            int eventType = parser.next();
            if (eventType == 2) {
                if ("messageContent".equals(parser.getName())) {
                    String notificationData = parser.nextText();

                    Log.d("TODO", "Parsing notification data......"
                            + notificationData);

                }
            } else if (eventType == 3
                    && "notification".equals(parser.getName())) {
                done = true;
            }
        }

        return notification;

    }

}
