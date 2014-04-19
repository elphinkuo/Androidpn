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

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.session.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.dom4j.io.OutputFormat;
import org.jivesoftware.util.XMLWriter;
import org.xmpp.packet.Packet;

/**
 * Class desciption here.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class Connection {

    private static final Log log = LogFactory.getLog(Connection.class);

    public static final String CHARSET = "UTF-8";

    private IoSession ioSession;

    private Session session;

    private ConnectionCloseListener closeListener;

    private int majorVersion = 1;

    private int minorVersion = 0;

    private String language = null;

    private static ThreadLocal encoder = new ThreadLocalEncoder();

    private boolean closed;

    public Connection(IoSession ioSession) {
        this.ioSession = ioSession;
        this.closed = false;
    }

    public boolean validate() {
        if (isClosed()) {
            return false;
        }
        deliverRawText(" ");
        return !isClosed();
    }

    public void close() {
        boolean closedSuccessfully = false;
        synchronized (this) {
            if (!isClosed()) {
                try {
                    deliverRawText("</stream:stream>", false);
                } catch (Exception e) {
                    // Ignore
                }
                if (session != null) {
                    session.setStatus(Session.STATUS_CLOSED);
                }
                ioSession.close(false);
                closed = true;
                closedSuccessfully = true;
            }
        }
        if (closedSuccessfully) {
            notifyCloseListeners();
        }
    }

    public void systemShutdown() {
        deliverRawText("<stream:error><system-shutdown "
                + "xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></stream:error>");
        close();
    }

    public void init(Session owner) {
        session = owner;
    }

    public boolean isClosed() {
        if (session == null) {
            return closed;
        }
        return session.getStatus() == Session.STATUS_CLOSED;
    }

    public boolean isSecure() {
        return ioSession.getFilterChain().contains("tls");
    }

    public void registerCloseListener(ConnectionCloseListener listener,
            Object ignore) {
        if (closeListener != null) {
            throw new IllegalStateException("Close listener already configured");
        }
        if (isClosed()) {
            listener.onConnectionClose(session);
        } else {
            closeListener = listener;
        }
    }

    public void removeCloseListener(ConnectionCloseListener listener) {
        if (closeListener == listener) {
            closeListener = null;
        }
    }

    private void notifyCloseListeners() {
        if (closeListener != null) {
            try {
                closeListener.onConnectionClose(session);
            } catch (Exception e) {
                log.error("Error notifying listener: " + closeListener, e);
            }
        }
    }

    public void deliverRawText(String text) {
        // Deliver the packet in asynchronous mode
        deliverRawText(text, true);
    }

    private void deliverRawText(String text, boolean asynchronous) {
        log.debug("SENT: " + text);
        if (!isClosed()) {
            IoBuffer buffer = IoBuffer.allocate(text.length());
            buffer.setAutoExpand(true);

            boolean errorDelivering = false;
            try {
                buffer.put(text.getBytes(CHARSET));
                buffer.flip();
                if (asynchronous) {
                    ioSession.write(buffer);
                } else {
                    // Send stanza and wait for ACK
                    boolean ok = ioSession.write(buffer).awaitUninterruptibly(
                            Config.getInt("connection.ack.timeout", 2000));
                    if (!ok) {
                        log.warn("No ACK was received when sending stanza to: "
                                + this.toString());
                    }
                }
            } catch (Exception e) {
                log.debug("NIOConnection: Error delivering raw text" + "\n"
                        + this.toString(), e);
                errorDelivering = true;
            }
            // Close the connection if delivering text fails
            if (errorDelivering && asynchronous) {
                close();
            }
        }
    }

    public void deliver(Packet packet) {
        log.debug("SENT: " + packet.toXML());
        if (!isClosed()) {
            IoBuffer buffer = IoBuffer.allocate(4096);
            buffer.setAutoExpand(true);

            boolean errorDelivering = false;
            try {
                XMLWriter xmlSerializer = new XMLWriter(new IoBufferWriter(
                        buffer, (CharsetEncoder) encoder.get()),
                        new OutputFormat());
                xmlSerializer.write(packet.getElement());
                xmlSerializer.flush();
                buffer.flip();
                ioSession.write(buffer);
            } catch (Exception e) {
                log.debug("Connection: Error delivering packet" + "\n"
                        + this.toString(), e);
                errorDelivering = true;
            }
            if (errorDelivering) {
                close();
            } else {
                session.incrementServerPacketCount();
            }
        }
    }

    public byte[] getAddress() throws UnknownHostException {
        return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress()
                .getAddress();
    }

    public String getHostAddress() throws UnknownHostException {
        return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress()
                .getHostAddress();
    }

    public String getHostName() throws UnknownHostException {
        return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress()
                .getHostName();
    }

    public int getMajorXMPPVersion() {
        return majorVersion;
    }

    public int getMinorXMPPVersion() {
        return minorVersion;
    }

    public void setXMPPVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanaguage(String language) {
        this.language = language;
    }

    private static class ThreadLocalEncoder extends ThreadLocal {
        protected Object initialValue() {
            return Charset.forName(CHARSET).newEncoder();
        }
    }

}
