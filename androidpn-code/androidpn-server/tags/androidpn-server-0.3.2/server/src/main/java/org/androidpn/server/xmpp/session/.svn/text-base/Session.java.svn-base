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

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.androidpn.server.xmpp.net.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public abstract class Session {

    public static final int MAJOR_VERSION = 1;

    public static final int MINOR_VERSION = 0;

    public static final int STATUS_CLOSED = -1;

    public static final int STATUS_CONNECTED = 1;

    public static final int STATUS_AUTHENTICATED = 3;

    protected static final String CHARSET = "UTF-8";

    private static final Log log = LogFactory.getLog(Session.class);

    private JID address;

    private String streamID;

    protected int status = STATUS_CONNECTED;

    protected Connection conn;

    protected SessionManager sessionManager;

    private String serverName;

    private long startDate = System.currentTimeMillis();

    private long lastActiveDate;

    private long clientPacketCount = 0;

    private long serverPacketCount = 0;

    private final Map<String, Object> sessionData = new HashMap<String, Object>();

    public Session(String serverName, Connection connection, String streamID) {
        conn = connection;
        this.streamID = streamID;
        this.serverName = serverName;
        String id = streamID;
        this.address = new JID(null, serverName, id, true);
        this.sessionManager = SessionManager.getInstance();
    }

    public JID getAddress() {
        return address;
    }

    public void setAddress(JID address) {
        this.address = address;
    }

    public Connection getConnection() {
        return conn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStreamID() {
        return streamID;
    }

    public String getServerName() {
        return serverName;
    }

    public Date getCreationDate() {
        return new Date(startDate);
    }

    public Date getLastActiveDate() {
        return new Date(lastActiveDate);
    }

    public void incrementClientPacketCount() {
        clientPacketCount++;
        lastActiveDate = System.currentTimeMillis();
    }

    public void incrementServerPacketCount() {
        serverPacketCount++;
        lastActiveDate = System.currentTimeMillis();
    }

    public long getNumClientPackets() {
        return clientPacketCount;
    }

    public long getNumServerPackets() {
        return serverPacketCount;
    }

    public void setSessionData(String key, Object value) {
        synchronized (sessionData) {
            sessionData.put(key, value);
        }
    }

    public Object getSessionData(String key) {
        synchronized (sessionData) {
            return sessionData.get(key);
        }
    }

    public void removeSessionData(String key) {
        synchronized (sessionData) {
            sessionData.remove(key);
        }
    }

    public void process(Packet packet) {
        try {
            deliver(packet);
        } catch (Exception e) {
            log.error("Internal server error", e);
        }
    }

    public void deliver(Packet packet) {
        if (conn != null && !conn.isClosed()) {
            conn.deliver(packet);
        }
    }

    public void deliverRawText(String text) {
        if (conn != null) {
            conn.deliverRawText(text);
        }
    }

    public void close() {
        if (conn != null) {
            conn.close();
        }
    }

    public boolean isClosed() {
        return conn.isClosed();
    }

    public boolean isSecure() {
        return conn.isSecure();
    }

    public boolean validate() {
        return conn.validate();
    }

    public String getHostAddress() throws UnknownHostException {
        return conn.getHostAddress();
    }

    public String getHostName() throws UnknownHostException {
        return conn.getHostName();
    }

    public String toString() {
        return super.toString() + " status: " + status + " address: " + address
                + " id: " + streamID;
    }

    public abstract String getAvailableStreamFeatures();

}
