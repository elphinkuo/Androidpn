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
package org.androidpn.server.xmpp.router;

import org.androidpn.server.xmpp.PacketException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class PacketDeliverer {

    private static final Log log = LogFactory.getLog(PacketDeliverer.class);

    public PacketDeliverer() {
    }

    public static void deliver(Packet packet) throws PacketException {
        if (packet == null) {
            throw new PacketException("Packet was null");
        }

        try {
            JID recipient = packet.getTo();
            if (recipient != null) {
                ClientSession clientSession = SessionManager.getInstance()
                        .getSession(recipient);
                if (clientSession != null) {
                    clientSession.deliver(packet);
                }
            }
        } catch (Exception e) {
            log.error("Could not deliver packet\n" + packet.toString(), e);
        }
    }
}
