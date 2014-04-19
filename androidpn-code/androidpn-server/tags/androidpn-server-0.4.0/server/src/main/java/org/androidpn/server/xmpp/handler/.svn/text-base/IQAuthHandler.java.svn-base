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
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

/** 
 * This class is to handle the TYPE_IQ jabber:iq:auth protocol.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class IQAuthHandler extends IQHandler {

    private static final String NAMESPACE = "jabber:iq:auth";

    private Element probeResponse;

    /**
     * Constructor.
     */
    public IQAuthHandler() {
        probeResponse = DocumentHelper.createElement(QName.get("query",
                NAMESPACE));
        probeResponse.addElement("username");
        if (AuthManager.isPlainSupported()) {
            probeResponse.addElement("password");
        }
        if (AuthManager.isDigestSupported()) {
            probeResponse.addElement("digest");
        }
        probeResponse.addElement("resource");
    }

    /**
     * Handles the received IQ packet.
     * 
     * @param packet the packet
     * @return the response to send back
     * @throws UnauthorizedException if the user is not authorized
     */
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        JID from = packet.getFrom();
        ClientSession session = (ClientSession) sessionManager.getSession(from);

        // If no session was found
        if (session == null) {
            log.error("Error during authentication. Session not found for key "
                    + from);
            IQ reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }

        IQ reply = null;
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
            } else { // set query
                String username = query.elementText("username");
                String password = query.elementText("password");
                String digest = null;
                if (query.element("digest") != null) {
                    digest = query.elementText("digest").toLowerCase();
                }

                reply = login(query, packet, username, password, session,
                        digest);
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

        // Send the response directly to the session
        if (reply != null) {
            session.process(reply);
        }

        return null;
    }

    private IQ login(Element iq, IQ packet, String username, String password,
            ClientSession session, String digest) throws UnauthorizedException,
            UserNotFoundException, UnauthenticatedException {
        // Verify the username
        if (username == null || username.trim().length() == 0) {
            throw new UnauthorizedException("Invalid username (empty or null).");
        }
        try {
            Stringprep.nodeprep(username);
        } catch (StringprepException e) {
            throw new UnauthorizedException("Invalid username: " + username, e);
        }

        // Verify the resource
        String resource = iq.elementText("resource");
        if (resource != null) {
            try {
                resource = JID.resourceprep(resource);
            } catch (StringprepException e) {
                throw new UnauthorizedException(
                        "Invalid resource: " + resource, e);
            }
        } else {
            // Answer a not_acceptable error
            IQ response = IQ.createResultIQ(packet);
            response.setChildElement(packet.getChildElement().createCopy());
            response.setError(PacketError.Condition.not_acceptable);
            return response;
        }

        username = username.toLowerCase();
        // Verify that username and password are correct
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

        // Set the session authenticated successfully
        session.setAuthToken(token, resource);
        packet.setFrom(session.getAddress());
        return IQ.createResultIQ(packet);
    }

    /**
     * Returns the namespace of the handler.
     * 
     * @return the namespace
     */
    public String getNamespace() {
        return NAMESPACE;
    }

}
