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
package org.androidpn.server.xmpp.session;

import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.xmpp.auth.AuthToken;
import org.androidpn.server.xmpp.net.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class ClientSession extends Session {

    private static final Log log = LogFactory.getLog(Session.class);

    private static final String ETHERX_NAMESPACE = "http://etherx.jabber.org/streams";

    protected AuthToken authToken;

    private boolean initialized;

    private boolean wasAvailable = false;

    private Presence presence = null;

    private int conflictCount = 0;

    //    private String activeList;
    //
    //    private String defaultList;

    public ClientSession(String serverName, Connection connection,
            String streamID) {
        super(serverName, connection, streamID);
        presence = new Presence();
        presence.setType(Presence.Type.unavailable);
    }

    public static ClientSession createSession(String serverName,
            XmlPullParser xpp, Connection connection)
            throws XmlPullParserException {
        if (!xpp.getName().equals("stream")) {
            throw new XmlPullParserException("Bad opening tag (not stream)");
        }

        if (!xpp.getNamespace(xpp.getPrefix()).equals(ETHERX_NAMESPACE)) {
            throw new XmlPullParserException("Stream not in correct namespace");
        }

        String language = "en";
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            if ("lang".equals(xpp.getAttributeName(i))) {
                language = xpp.getAttributeValue(i);
            }
        }

        // Store language and version information in the connection.
        connection.setLanaguage(language);
        connection.setXMPPVersion(MAJOR_VERSION, MINOR_VERSION);

        // Create a ClientSession for this user.
        ClientSession session = SessionManager.getInstance()
                .createClientSession(connection);

        // Build the start packet response
        StringBuilder sb = new StringBuilder(200);
        sb.append("<?xml version='1.0' encoding='");
        sb.append(CHARSET);
        sb.append("'?>");
        sb.append("<stream:stream ");
        sb
                .append("xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns=\"jabber:client\" from=\"");
        sb.append(serverName);
        sb.append("\" id=\"");
        sb.append(session.getStreamID().toString());
        sb.append("\" xml:lang=\"");
        sb.append(language);
        sb.append("\" version=\"");
        sb.append(MAJOR_VERSION).append(".").append(MINOR_VERSION);
        sb.append("\">");
        connection.deliverRawText(sb.toString());

        // XMPP 1.0 so we need to announce stream features.
        sb = new StringBuilder(490);
        sb.append("<stream:features>");
        //        // Include available SASL Mechanisms
        //        sb.append(SASLAuthentication.getSASLMechanisms(session));
        // Include Stream features
        String specificFeatures = session.getAvailableStreamFeatures();
        if (specificFeatures != null) {
            sb.append(specificFeatures);
        }
        sb.append("</stream:features>");

        connection.deliverRawText(sb.toString());
        return session;
    }

    public String getUsername() throws UserNotFoundException {
        if (authToken == null) {
            throw new UserNotFoundException();
        }
        return getAddress().getNode();
    }

    public void setAuthToken(AuthToken auth) {
        authToken = auth;
    }

    public void setAuthToken(AuthToken auth, String resource) {
        setAddress(new JID(auth.getUsername(), getServerName(), resource));
        authToken = auth;
        setStatus(Session.STATUS_AUTHENTICATED);
        // Add session to the session manager.
        sessionManager.addSession(this);
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public boolean isAnonymousUser() {
        return authToken == null || authToken.isAnonymous();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean isInit) {
        initialized = isInit;
    }

    public boolean wasAvailable() {
        return wasAvailable;
    }

    public Presence getPresence() {
        return presence;
    }

    public String getAvailableStreamFeatures() {
        StringBuilder sb = new StringBuilder(200);
        if (getAuthToken() == null) {
            // Advertise that the server supports Non-SASL Authentication
            sb.append("<auth xmlns=\"http://jabber.org/features/iq-auth\"/>");
            sb
                    .append("<register xmlns=\"http://jabber.org/features/iq-register\"/>");
        } else {
            // If the session has been authenticated then offer resource binding
            // and session establishment
            sb.append("<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>");
            sb
                    .append("<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/>");
        }
        return sb.toString();
    }

    public int incrementConflictCount() {
        conflictCount++;
        return conflictCount;
    }

    public String toString() {
        return super.toString() + " presence: " + presence;
    }
}
