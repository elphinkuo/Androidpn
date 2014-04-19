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

import java.util.Iterator;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceManager;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.openfire.forms.FormField;
import org.jivesoftware.openfire.forms.spi.XDataFormImpl;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class IQRegisterHandler extends IQHandler {

    private IQHandlerInfo info;

    private UserService userService;

    private Element probeResult;

    public IQRegisterHandler() {
        info = new IQHandlerInfo("query", "jabber:iq:register");
        userService = ServiceManager.getUserService();

        probeResult = DocumentHelper.createElement(QName.get("query",
                "jabber:iq:register"));
        probeResult.addElement("username");
        probeResult.addElement("password");
        probeResult.addElement("email");
        probeResult.addElement("name");
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        ClientSession session = sessionManager.getSession(packet.getFrom());
        IQ reply = null;

        // If no session was found
        if (session == null) {
            log.error("Error during registration. Session not found for key "
                    + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }

        if (IQ.Type.get.equals(packet.getType())) {
            reply = IQ.createResultIQ(packet);
            if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                try {
                    User user = userService.getUserByUsername(session
                            .getUsername());
                    Element currentRegistration = probeResult.createCopy();
                    currentRegistration.addElement("registered");
                    currentRegistration.element("username").setText(
                            user.getUsername());
                    currentRegistration.element("password").setText("");
                    currentRegistration.element("email").setText(
                            user.getEmail() == null ? "" : user.getEmail());
                    currentRegistration.element("name").setText(user.getName());

                    Element form = currentRegistration.element(QName.get("x",
                            "jabber:x:data"));
                    Iterator fields = form.elementIterator("field");
                    Element field;
                    while (fields.hasNext()) {
                        field = (Element) fields.next();
                        if ("username".equals(field.attributeValue("var"))) {
                            field.addElement("value").addText(
                                    user.getUsername());
                        } else if ("name".equals(field.attributeValue("var"))) {
                            field.addElement("value").addText(user.getName());
                        } else if ("email".equals(field.attributeValue("var"))) {
                            field.addElement("value").addText(
                                    user.getEmail() == null ? "" : user
                                            .getEmail());
                        }
                    }
                    reply.setChildElement(currentRegistration);
                } catch (UserNotFoundException e) {
                    reply.setChildElement(probeResult.createCopy());
                }
            } else {
                reply.setTo((JID) null);
                reply.setChildElement(probeResult.createCopy());
            }

        } else if (IQ.Type.set.equals(packet.getType())) {
            try {
                Element iqElement = packet.getChildElement();
                if (iqElement.element("remove") != null) {
                    if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                        // TODO
                    } else {
                        throw new UnauthorizedException();
                    }
                } else {
                    String username = null;
                    String password = null;
                    String email = null;
                    String name = null;
                    User newUser;
                    XDataFormImpl registrationForm;
                    FormField field;

                    Element formElement = iqElement.element("x");
                    // Check if a form was used to provide the registration info
                    if (formElement != null) {
                        // Get the sent form
                        registrationForm = new XDataFormImpl();
                        registrationForm.parse(formElement);
                        // Get the username sent in the form
                        Iterator<String> values = registrationForm.getField(
                                "username").getValues();
                        username = (values.hasNext() ? values.next() : " ");
                        // Get the password sent in the form
                        field = registrationForm.getField("password");
                        if (field != null) {
                            values = field.getValues();
                            password = (values.hasNext() ? values.next() : " ");
                        }
                        // Get the email sent in the form
                        field = registrationForm.getField("email");
                        if (field != null) {
                            values = field.getValues();
                            email = (values.hasNext() ? values.next() : " ");
                        }
                        // Get the name sent in the form
                        field = registrationForm.getField("name");
                        if (field != null) {
                            values = field.getValues();
                            name = (values.hasNext() ? values.next() : " ");
                        }
                    } else {
                        // Get the registration info from the query elements
                        username = iqElement.elementText("username");
                        password = iqElement.elementText("password");
                        email = iqElement.elementText("email");
                        name = iqElement.elementText("name");
                    }
                    if (email != null && email.matches("\\s*")) {
                        email = null;
                    }
                    if (name != null && name.matches("\\s*")) {
                        name = null;
                    }

                    // So that we can set a more informative error message back
                    if (username != null) {
                        Stringprep.nodeprep(username);
                    }

                    if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                        // Flag that indicates if the user is only changing his password
                        boolean onlyPassword = false;
                        if (iqElement.elements().size() == 2
                                && iqElement.element("username") != null
                                && iqElement.element("password") != null) {
                            onlyPassword = true;
                        }

                        User user = userService.getUser(session.getUsername());
                        if (user.getUsername().equalsIgnoreCase(username)) {
                            if (password != null
                                    && password.trim().length() > 0) {
                                user.setPassword(password);
                            }
                            if (!onlyPassword) {
                                user.setEmail(email);
                            }
                            newUser = user;
                        } else if (password != null
                                && password.trim().length() > 0) {
                            // An admin can create new accounts when logged in                               
                            newUser = new User();
                            newUser.setUsername(username);
                            newUser.setPassword(password);
                            newUser.setEmail(email);
                            newUser.setName(name);
                            newUser = userService.saveUser(newUser);
                        } else {
                            // Deny registration of users with no password
                            reply = IQ.createResultIQ(packet);
                            reply.setChildElement(packet.getChildElement()
                                    .createCopy());
                            reply
                                    .setError(PacketError.Condition.not_acceptable);
                            return reply;
                        }
                    } else {
                        // If some required information was not provided
                        if (password == null || password.trim().length() == 0) {
                            reply = IQ.createResultIQ(packet);
                            reply.setChildElement(packet.getChildElement()
                                    .createCopy());
                            reply
                                    .setError(PacketError.Condition.not_acceptable);
                            return reply;
                        } else {
                            newUser = new User();
                            newUser.setUsername(username);
                            newUser.setPassword(password);
                            newUser.setEmail(email);
                            newUser.setName(name);
                            newUser = userService.saveUser(newUser);
                        }
                    }

                    reply = IQ.createResultIQ(packet);
                }
            } catch (UserExistsException e) {
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.conflict);
            } catch (UserNotFoundException e) {
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.bad_request);
            } catch (StringprepException e) {
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.jid_malformed);
            } catch (IllegalArgumentException e) {
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.not_acceptable);
                log.warn(e);
            } catch (UnsupportedOperationException e) {
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.not_allowed);
            } catch (Exception e) {
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.internal_server_error);
                log.error(e);
            }
        }

        if (reply != null) {
            session.process(reply);
        }
        return null;
    }

    public IQHandlerInfo getInfo() {
        return info;
    }
}
