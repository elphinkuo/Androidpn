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
package org.androidpn.server.xmpp.handler;

import gnu.inet.encoding.Stringprep;
import gnu.inet.encoding.StringprepException;

import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.xmpp.UnauthenticatedException;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.auth.AuthManager;
import org.androidpn.server.xmpp.auth.AuthToken;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class IQAuthHandler extends IQHandler {

    private IQHandlerInfo info;

    private SessionManager sessionManager;

    private Element probeResponse;

    public IQAuthHandler() {
        info = new IQHandlerInfo("query", "jabber:iq:auth");

        sessionManager = SessionManager.getInstance();
        probeResponse = DocumentHelper.createElement(QName.get("query",
                "jabber:iq:auth"));
        probeResponse.addElement("username");
        if (AuthManager.isPlainSupported()) {
            probeResponse.addElement("password");
        }
        if (AuthManager.isDigestSupported()) {
            probeResponse.addElement("digest");
        }
        probeResponse.addElement("resource");
    }

    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        JID from = packet.getFrom();
        ClientSession session = (ClientSession) sessionManager.getSession(from);

        // If no session was found then answer an error (if possible)
        if (session == null) {
            log.error("Error during authentication. Session not found for key "
                    + from);
            IQ reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }
        
        IQ reply = null;
        //        boolean resourceBound = false;
        try {
            Element iq = packet.getElement();
            Element query = iq.element("query");
            Element queryResponse = probeResponse.createCopy();
            if (IQ.Type.get == packet.getType()) {
                String username = query.elementText("username");
                if (username != null) {
                    queryResponse.element("username").setText(username);
                }
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(queryResponse);
                if (session.getStatus() != Session.STATUS_AUTHENTICATED) {
                    reply.setTo((JID) null);
                }
            } else { // Otherwise set query
                String username = query.elementText("username");
                String password = query.elementText("password");
                String digest = null;
                if (query.element("digest") != null) {
                    digest = query.elementText("digest").toLowerCase();
                }

                reply = login(query, packet, username, password, session,
                        digest);
                //                resourceBound = (session.getStatus() == Session.STATUS_AUTHENTICATED);
            }
        } catch (UserNotFoundException e) {
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.not_authorized);
        } catch (UnauthorizedException e) {
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.not_authorized);
        } catch (UnauthenticatedException e) {
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
        }

        // Send the response directly to the correct session.
        if (reply != null) {
            session.process(reply);
        }

        //        if (resourceBound) {
        //            // After the client has been informed, inform all listeners as well.
        //            SessionEventDispatcher.dispatchEvent(session,
        //                    SessionEventDispatcher.EventType.resource_bound);
        //        }
        
        return null;
    }

    private IQ login(Element iq, IQ packet, String username, String password,
            ClientSession session, String digest) throws UnauthorizedException,
            UserNotFoundException, UnauthenticatedException {
        // Verify the validity of the username
        if (username == null || username.trim().length() == 0) {
            throw new UnauthorizedException("Invalid username (empty or null).");
        }
        try {
            Stringprep.nodeprep(username);
        } catch (StringprepException e) {
            throw new UnauthorizedException("Invalid username: " + username, e);
        }

        // Verify that specified resource is not violating any string prep rule
        String resource = iq.elementText("resource");
        if (resource != null) {
            try {
                resource = JID.resourceprep(resource);
            } catch (StringprepException e) {
                throw new UnauthorizedException(
                        "Invalid resource: " + resource, e);
            }
        } else {
            // Answer a not_acceptable error since a resource was not supplied
            IQ response = IQ.createResultIQ(packet);
            response.setChildElement(packet.getChildElement().createCopy());
            response.setError(PacketError.Condition.not_acceptable);
            return response;
        }

        username = username.toLowerCase();
        // Verify that supplied username and password are correct (i.e. user authentication was successful)
        AuthToken token = null;
        if (password != null && AuthManager.isPlainSupported()) {
            token = AuthManager.authenticate(username, password);
        } else if (digest != null && AuthManager.isDigestSupported()) {
            token = AuthManager.authenticate(username, session.getStreamID()
                    .toString(), digest);
        }

        if (token == null) {
            throw new UnauthorizedException();
        }

        // Set that the new session has been authenticated successfully
        session.setAuthToken(token, resource);
        packet.setFrom(session.getAddress());
        return IQ.createResultIQ(packet);
    }

    public IQHandlerInfo getInfo() {
        return info;
    }

}
