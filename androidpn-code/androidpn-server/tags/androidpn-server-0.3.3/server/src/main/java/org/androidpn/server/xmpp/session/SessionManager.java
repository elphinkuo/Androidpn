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
package org.androidpn.server.xmpp.session;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.androidpn.server.XmppServer;
import org.androidpn.server.xmpp.net.Connection;
import org.androidpn.server.xmpp.net.ConnectionCloseListener;
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

    private ClientSessionListener clientSessionListener = new ClientSessionListener();

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
        // Register to receive close notification on this session
        conn.registerCloseListener(clientSessionListener, session);
        // Add to pre-authenticated sessions
        preAuthSessions.put(session.getAddress().getResource(), session);
        // Increment the counter of user sessions
        connectionsCounter.incrementAndGet();

        log.debug("ClientSession created.");
        return session;
    }

    public void addSession(ClientSession session) {
        // Remove the pre-Authenticated session but remember to use the temporary ID as the key
        preAuthSessions.remove(session.getStreamID().toString());

        clientSessions.put(session.getAddress().toString(), session);
    }

    public ClientSession getSession(String username) {
        // return getSession(new JID(username, serverName, null, true));
        return getSession(new JID(username, serverName, "AndroidpnClient", true));
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
        if (session == null || serverName == null) {
            return false;
        }

        return removeSession(session, session.getAddress(), false);
    }

    public boolean removeSession(ClientSession session, JID fullJID,
            boolean forceUnavailable) {
        if (serverName == null) {
            return false;
        }

        if (session == null) {
            session = getSession(fullJID);
        }

        boolean removed = clientSessions.remove(fullJID.toString()) != null;

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

    public void closeAllSessions() {
        try {
            // Send the close stream header to all connections
            Set<ClientSession> sessions = new HashSet<ClientSession>();
            sessions.addAll(preAuthSessions.values());
            sessions.addAll(clientSessions.values());

            for (ClientSession session : sessions) {
                try {
                    session.getConnection().systemShutdown();
                } catch (Throwable t) {
                }
            }
        } catch (Exception e) {
        }
    }

    private class ClientSessionListener implements ConnectionCloseListener {

        public void onConnectionClose(Object handback) {
            try {
                ClientSession session = (ClientSession) handback;
                try {
                    if ((session.getPresence().isAvailable() || !session
                            .wasAvailable())) {
                        // Send an unavailable presence to the user's subscribers
                        Presence presence = new Presence();
                        presence.setType(Presence.Type.unavailable);
                        presence.setFrom(session.getAddress());
                        // router.route(presence);
                    }
                } finally {
                    removeSession(session);
                }
            } catch (Exception e) {
                // Can't do anything about this problem...
                log.error("Could not close socket", e);
            }
        }
    }

}
