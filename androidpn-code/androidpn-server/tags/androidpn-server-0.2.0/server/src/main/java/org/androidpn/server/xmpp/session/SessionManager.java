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

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.androidpn.server.XmppServer;
import org.androidpn.server.xmpp.auth.AuthToken;
import org.androidpn.server.xmpp.net.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class SessionManager {

    private static final Log log = LogFactory.getLog(SessionManager.class);

    private static SessionManager instance;

    private String serverName;

    private Map<String, ClientSession> preAuthSessions = new ConcurrentHashMap<String, ClientSession>();

    private Map<String, ClientSession> clientSessions = new ConcurrentHashMap<String, ClientSession>();

    private final AtomicInteger connectionsCounter = new AtomicInteger(0);

    private SessionManager() {
        serverName = XmppServer.getInstance().getServerName();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                instance = new SessionManager();
            }
        }
        return instance;
    }

    public ClientSession createClientSession(Connection conn) {
        Random random = new Random();
        String streamId = Integer.toHexString(random.nextInt());
        return createClientSession(conn, streamId);
    }

    public ClientSession createClientSession(Connection conn, String streamId) {
        if (serverName == null) {
            throw new IllegalStateException("Server not initialized");
        }
        ClientSession session = new ClientSession(serverName, conn, streamId);
        conn.init(session);

        // Add to pre-authenticated sessions
        preAuthSessions.put(session.getAddress().getResource(), session);

        // Increment the counter of user sessions
        connectionsCounter.incrementAndGet();

        return session;
    }

    public void addSession(ClientSession session) {
        // Remove the pre-Authenticated session but remember to use the temporary ID as the key
        preAuthSessions.remove(session.getStreamID().toString());

        clientSessions.put(session.getAddress().toString(), session);
    }

    public ClientSession getSession(JID from) {
        if (from == null || serverName == null
                || !serverName.equals(from.getDomain())) {
            return null;
        }

        // Check pre-authenticated sessions
        if (from.getResource() != null) {
            ClientSession session = preAuthSessions.get(from.getResource());
            if (session != null) {
                return session;
            }
        }

        if (from.getResource() == null || from.getNode() == null) {
            return null;
        }

        return clientSessions.get(from.toString());
    }

    public Collection<ClientSession> getSessions() {
        return clientSessions.values();
    }

    public boolean removeSession(ClientSession session) {
        // Do nothing if session is null or if the server is shutting down.
        // Note: When the server is shutting down the serverName will be null.
        if (session == null || serverName == null) {
            return false;
        }

        AuthToken authToken = session.getAuthToken();
        // Consider session anonymous (for this matter) if we are closing
        // a session that never authenticated
        boolean anonymous = authToken == null || authToken.isAnonymous();
        return removeSession(session, session.getAddress(), anonymous, false);
    }

    public boolean removeSession(ClientSession session, JID fullJID,
            boolean anonymous, boolean forceUnavailable) {
        // Do nothing if server is shutting down. Note: When the server
        // is shutting down the serverName will be null.
        if (serverName == null) {
            return false;
        }

        if (session == null) {
            session = getSession(fullJID);
        }

        // Remove route to the removed session (anonymous or not)
        boolean removed = clientSessions.remove(fullJID) != null;

        // Remove the session from the pre-Authenticated sessions list (if present)
        boolean preauthRemoved = preAuthSessions.remove(fullJID.getResource()) != null;

        // If the user is still available then send an unavailable presence
        if (forceUnavailable || session.getPresence().isAvailable()) {
            Presence offline = new Presence();
            offline.setFrom(fullJID);
            offline.setTo(new JID(null, serverName, null, true));
            offline.setType(Presence.Type.unavailable);
            // router.route(offline);
        }

        if (removed || preauthRemoved) {
            // Decrement the counter of user sessions
            connectionsCounter.decrementAndGet();
            return true;
        }
        return false;
    }

}
