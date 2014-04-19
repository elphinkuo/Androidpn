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

import java.io.IOException;
import java.io.StringReader;

import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.router.PacketRouter;
import org.androidpn.server.xmpp.session.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.net.MXParser;
import org.jivesoftware.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;
import org.xmpp.packet.Roster;
import org.xmpp.packet.StreamError;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public abstract class StanzaHandler {

    private static final Log log = LogFactory.getLog(StanzaHandler.class);

    protected static String CHARSET = "UTF-8";

    protected Connection connection;

    protected Session session;

    protected String serverName;

    private boolean sessionCreated = false;

    private PacketRouter router;

    public StanzaHandler(String serverName, Connection connection) {
        this.serverName = serverName;
        this.connection = connection;
        this.router = new PacketRouter();
    }

    public void process(String stanza, XMPPPacketReader reader)
            throws Exception {

        boolean initialStream = stanza.startsWith("<stream:stream");
        if (!sessionCreated || initialStream) {
            if (!initialStream) {
                // Ignore <?xml version="1.0"?>
                return;
            }
            // Found an stream:stream tag...
            if (!sessionCreated) {
                sessionCreated = true;
                MXParser parser = reader.getXPPParser();
                parser.setInput(new StringReader(stanza));
                createSession(parser);
            }
            return;
        }

        // Verify if end of stream was requested
        if (stanza.equals("</stream:stream>")) {
            session.close();
            return;
        }
        // Ignore <?xml version="1.0"?> stanzas sent by clients
        if (stanza.startsWith("<?xml")) {
            return;
        }
        // Create DOM object from received stanza
        Element doc = reader.read(new StringReader(stanza)).getRootElement();
        if (doc == null) {
            return;
        }
        process(doc);
    }

    private void process(Element doc) throws UnauthorizedException {
        if (doc == null) {
            return;
        }

        String tag = doc.getName();
        if ("message".equals(tag)) {
            log.debug("message...");
            Message packet;
            try {
                packet = new Message(doc, !validateJIDs());
            } catch (IllegalArgumentException e) {
                log.debug("Rejecting packet. JID malformed", e);
                // The original packet contains a malformed JID so answer with an error.
                Message reply = new Message();
                reply.setID(doc.attributeValue("id"));
                reply.setTo(session.getAddress());
                reply.getElement().addAttribute("from",
                        doc.attributeValue("to"));
                reply.setError(PacketError.Condition.jid_malformed);
                session.process(reply);
                return;
            }
            processMessage(packet);
        } else if ("presence".equals(tag)) {
            log.debug("presence...");
            Presence packet;
            try {
                packet = new Presence(doc, !validateJIDs());
            } catch (IllegalArgumentException e) {
                log.debug("Rejecting packet. JID malformed", e);
                // The original packet contains a malformed JID so answer an error
                Presence reply = new Presence();
                reply.setID(doc.attributeValue("id"));
                reply.setTo(session.getAddress());
                reply.getElement().addAttribute("from",
                        doc.attributeValue("to"));
                reply.setError(PacketError.Condition.jid_malformed);
                session.process(reply);
                return;
            }
            // Check that the presence type is valid. If not then assume available type
            try {
                packet.getType();
            } catch (IllegalArgumentException e) {
                log.warn("Invalid presence type", e);
                packet.setType(null);
            }
            // Check that the presence show is valid. If not then assume available show value
            try {
                packet.getShow();
            } catch (IllegalArgumentException e) {
                log.warn("Invalid presence show for -" + packet.toXML(), e);
                packet.setShow(null);
            }
            if (session.getStatus() == Session.STATUS_CLOSED
                    && packet.isAvailable()) {
                log
                        .warn("Ignoring available presence packet of closed session: "
                                + packet);
                return;
            }
            processPresence(packet);
        } else if ("iq".equals(tag)) {
            log.debug("iq...");
            IQ packet;
            try {
                packet = getIQ(doc);
            } catch (IllegalArgumentException e) {
                log.debug("Rejecting packet. JID malformed", e);
                // The original packet contains a malformed JID so answer an error
                IQ reply = new IQ();
                if (!doc.elements().isEmpty()) {
                    reply.setChildElement(((Element) doc.elements().get(0))
                            .createCopy());
                }
                reply.setID(doc.attributeValue("id"));
                reply.setTo(session.getAddress());
                if (doc.attributeValue("to") != null) {
                    reply.getElement().addAttribute("from",
                            doc.attributeValue("to"));
                }
                reply.setError(PacketError.Condition.jid_malformed);
                session.process(reply);
                return;
            }
            if (packet.getID() == null
                    && Config.getBoolean("xmpp.server.validation.enabled",
                            false)) {
                // IQ packets MUST have an 'id' attribute so close the connection
                StreamError error = new StreamError(
                        StreamError.Condition.invalid_xml);
                session.deliverRawText(error.toXML());
                session.close();
                return;
            }
            processIQ(packet);
        } else {
            log.warn("Unexpected packet tag (not message,iq,presence)"
                    + doc.asXML());
            session.close();
        }
    }

    private IQ getIQ(Element doc) {
        Element query = doc.element("query");
        if (query != null && "jabber:iq:roster".equals(query.getNamespaceURI())) {
            return new Roster(doc);
        } else {
            return new IQ(doc, !validateJIDs());
        }
    }

    protected void processIQ(IQ packet) throws UnauthorizedException {
        router.route(packet);
        session.incrementClientPacketCount();
    }

    protected void processPresence(Presence packet)
            throws UnauthorizedException {
        router.route(packet);
        session.incrementClientPacketCount();
    }

    protected void processMessage(Message packet) throws UnauthorizedException {
        router.route(packet);
        session.incrementClientPacketCount();
    }

    protected void createSession(XmlPullParser xpp)
            throws XmlPullParserException, IOException {
        for (int eventType = xpp.getEventType(); eventType != XmlPullParser.START_TAG;) {
            eventType = xpp.next();
        }

        // Create the correct session based on the sent namespace     
        boolean ssCreated = createSession(xpp.getNamespace(null), serverName,
                xpp, connection);

        // No session was created because of an invalid namespace prefix
        if (!ssCreated) {
            StringBuilder sb = new StringBuilder(250);
            sb.append("<?xml version='1.0' encoding='");
            sb.append(CHARSET);
            sb.append("'?>");
            sb.append("<stream:stream ");
            sb.append("from=\"").append(serverName).append("\" ");
            sb.append("id=\"").append(StringUtils.randomString(5))
                    .append("\" ");
            sb.append("xmlns=\"").append(xpp.getNamespace(null)).append("\" ");
            sb.append("xmlns:stream=\"").append(xpp.getNamespace("stream"))
                    .append("\" ");
            sb.append("version=\"1.0\">");

            // Include the bad-namespace-prefix in the response
            StreamError error = new StreamError(
                    StreamError.Condition.bad_namespace_prefix);
            sb.append(error.toXML());
            connection.deliverRawText(sb.toString());
            connection.close();
            log
                    .warn("Closing session due to bad_namespace_prefix in stream header. Prefix: "
                            + xpp.getNamespace(null)
                            + ". Connection: "
                            + connection);
        }
    }

    abstract public String getNamespace();

    abstract public boolean validateJIDs();

    abstract public boolean createSession(String namespace, String serverName,
            XmlPullParser xpp, Connection connection)
            throws XmlPullParserException;

}
