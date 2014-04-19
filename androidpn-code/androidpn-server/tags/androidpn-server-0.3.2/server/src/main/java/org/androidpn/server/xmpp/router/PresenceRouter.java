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

import org.androidpn.server.xmpp.handler.PresenceUpdateHandler;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class PresenceRouter {

    private final Log log = LogFactory.getLog(getClass());

    private SessionManager sessionManager;

    private PresenceUpdateHandler presenceUpdateHandler;

    public PresenceRouter() {
        sessionManager = SessionManager.getInstance();
        presenceUpdateHandler = new PresenceUpdateHandler();
    }

    public void route(Presence packet) {
        if (packet == null) {
            throw new NullPointerException();
        }
        ClientSession session = sessionManager.getSession(packet.getFrom());

        if (session == null || session.getStatus() != Session.STATUS_CONNECTED) {
            handle(packet);
        } else {
            packet.setTo(session.getAddress());
            packet.setFrom((JID) null);
            packet.setError(PacketError.Condition.not_authorized);
            session.process(packet);
        }
    }

    private void handle(Presence packet) {
        try {
            Presence.Type type = packet.getType();
            // Presence updates (null == 'available')
            if (type == null || Presence.Type.unavailable == type) {
                presenceUpdateHandler.process(packet);
            } else {
                log.warn("Unknown presence type");
            }

        } catch (Exception e) {
            log.error("Could not route packet", e);
            Session session = sessionManager.getSession(packet.getFrom());
            if (session != null) {
                session.close();
            }
        }
    }

}
