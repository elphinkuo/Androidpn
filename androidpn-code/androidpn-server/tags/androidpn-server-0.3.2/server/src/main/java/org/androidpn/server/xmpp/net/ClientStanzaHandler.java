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
package org.androidpn.server.xmpp.net;

import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Presence;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class ClientStanzaHandler extends StanzaHandler {

    public ClientStanzaHandler(String serverName, Connection connection) {
        super(serverName, connection);
    }

    public String getNamespace() {
        return "jabber:client";
    }

    public boolean validateJIDs() {
        return true;
    }

    public boolean createSession(String namespace, String serverName,
            XmlPullParser xpp, Connection connection)
            throws XmlPullParserException {
        if ("jabber:client".equals(namespace)) {
            session = ClientSession.createSession(serverName, xpp, connection);
            return true;
        }
        return false;
    }

    protected void processIQ(IQ packet) throws UnauthorizedException {
        packet.setFrom(session.getAddress());
        super.processIQ(packet);
    }

    protected void processPresence(Presence packet)
            throws UnauthorizedException {
        packet.setFrom(session.getAddress());
        super.processPresence(packet);
    }

    protected void processMessage(Message packet) throws UnauthorizedException {
        packet.setFrom(session.getAddress());
        super.processMessage(packet);
    }

}
