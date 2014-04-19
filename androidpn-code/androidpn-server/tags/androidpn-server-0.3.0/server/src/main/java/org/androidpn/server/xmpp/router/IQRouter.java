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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.androidpn.server.XmppServer;
import org.androidpn.server.xmpp.handler.IQAuthHandler;
import org.androidpn.server.xmpp.handler.IQHandler;
import org.androidpn.server.xmpp.handler.IQHandlerInfo;
import org.androidpn.server.xmpp.handler.IQRegisterHandler;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class IQRouter {

    private final Log log = LogFactory.getLog(getClass());

    private SessionManager sessionManager;

    private String serverName;

    private List<IQHandler> iqHandlers = new ArrayList<IQHandler>();

    private Map<String, IQHandler> namespace2Handlers = new ConcurrentHashMap<String, IQHandler>();

    public IQRouter() {
        sessionManager = SessionManager.getInstance();
        serverName = XmppServer.getInstance().getServerName();
        iqHandlers.add(new IQAuthHandler());
        iqHandlers.add(new IQRegisterHandler());
    }

    public void route(IQ packet) {
        if (packet == null) {
            throw new NullPointerException();
        }
        JID sender = packet.getFrom();
        ClientSession session = sessionManager.getSession(sender);

        if (session == null
                || session.getStatus() == Session.STATUS_AUTHENTICATED
                || ("jabber:iq:auth".equals(packet.getChildElement()
                        .getNamespaceURI())
                        || "jabber:iq:register".equals(packet.getChildElement()
                                .getNamespaceURI()) || "urn:ietf:params:xml:ns:xmpp-bind"
                        .equals(packet.getChildElement().getNamespaceURI()))) {
            handle(packet);
        } else {
            IQ reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.not_authorized);
            session.process(reply);
        }
    }

    private void handle(IQ packet) {
        JID recipientJID = packet.getTo();
        try {
            Element childElement = packet.getChildElement();
            String namespace = null;
            if (childElement != null) {
                namespace = childElement.getNamespaceURI();
            }
            if (namespace == null) {
                if (packet.getType() != IQ.Type.result
                        && packet.getType() != IQ.Type.error) {
                    // Do nothing. We can't handle queries outside of a valid namespace
                    log.warn("Unknown packet " + packet);
                }
            } else {
                IQHandler handler = getHandler(namespace);
                if (handler == null) {
                    if (recipientJID == null) {
                        // Answer an error since the server can't handle the requested namespace
                        sendErrorPacket(packet,
                                PacketError.Condition.service_unavailable);
                    } else if (recipientJID.getNode() == null
                            || "".equals(recipientJID.getNode())) {
                        // Answer an error if JID is of the form <domain>
                        sendErrorPacket(packet,
                                PacketError.Condition.feature_not_implemented);
                    } else {
                        // Answer an error since the server can't handle packets sent to a node
                        sendErrorPacket(packet,
                                PacketError.Condition.service_unavailable);
                    }
                } else {
                    handler.process(packet);
                }
            }

        } catch (Exception e) {
            log.error("Could not route packet", e);
            Session session = sessionManager.getSession(packet.getFrom());
            if (session != null) {
                IQ reply = IQ.createResultIQ(packet);
                reply.setError(PacketError.Condition.internal_server_error);
                session.process(reply);
            }
        }
    }

    private void sendErrorPacket(IQ originalPacket,
            PacketError.Condition condition) {
        if (IQ.Type.error == originalPacket.getType()) {
            log.error("Cannot reply an IQ error to another IQ error: "
                    + originalPacket);
            return;
        }
        IQ reply = IQ.createResultIQ(originalPacket);
        reply.setChildElement(originalPacket.getChildElement().createCopy());
        reply.setError(condition);
        // Check if the server was the sender of the IQ
        if (serverName.equals(originalPacket.getFrom().toString())) {
            // Just let the IQ router process the IQ error reply
            handle(reply);
            return;
        }
        try {
            // Route the error packet to the original sender of the IQ.
            PacketDeliverer.deliver(reply);
        } catch (Exception e) {
            // Ignore
        }
    }

    public void addHandler(IQHandler handler) {
        if (iqHandlers.contains(handler)) {
            throw new IllegalArgumentException(
                    "IQHandler already provided by the server");
        }
        namespace2Handlers.put(handler.getInfo().getNamespace(), handler);
    }

    public void removeHandler(IQHandler handler) {
        if (iqHandlers.contains(handler)) {
            throw new IllegalArgumentException(
                    "Cannot remove an IQHandler provided by the server");
        }
        namespace2Handlers.remove(handler.getInfo().getNamespace());
    }

    private IQHandler getHandler(String namespace) {
        IQHandler handler = namespace2Handlers.get(namespace);
        if (handler == null) {
            for (IQHandler handlerCandidate : iqHandlers) {
                IQHandlerInfo handlerInfo = handlerCandidate.getInfo();
                if (handlerInfo != null
                        && namespace.equalsIgnoreCase(handlerInfo
                                .getNamespace())) {
                    handler = handlerCandidate;
                    namespace2Handlers.put(namespace, handler);
                    break;
                }
            }
        }
        return handler;
    }

}
