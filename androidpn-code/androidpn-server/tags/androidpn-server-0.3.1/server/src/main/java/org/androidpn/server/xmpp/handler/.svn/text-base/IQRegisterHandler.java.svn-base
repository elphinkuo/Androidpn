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

import java.util.Iterator;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceManager;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.Config;
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

    private boolean registrationEnabled;

    public IQRegisterHandler() {
        info = new IQHandlerInfo("query", "jabber:iq:register");
        userService = ServiceManager.getUserService();

        probeResult = DocumentHelper.createElement(QName.get("query",
                "jabber:iq:register"));
        probeResult.addElement("username");
        probeResult.addElement("password");
        probeResult.addElement("email");
        probeResult.addElement("name");

        registrationEnabled = Config.getBoolean("register.inband", true);
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        ClientSession session = sessionManager.getSession(packet.getFrom());
        IQ reply = null;

        // If no session was found then answer an error (if possible)
        if (session == null) {
            log.error("Error during registration. Session not found for key "
                    + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }

        if (IQ.Type.get.equals(packet.getType())) {
            // If inband registration is not allowed, return an error.
            if (!registrationEnabled) {
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.forbidden);
            } else {
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
                        currentRegistration.element("name").setText(
                                user.getName());

                        Element form = currentRegistration.element(QName.get(
                                "x", "jabber:x:data"));
                        Iterator fields = form.elementIterator("field");
                        Element field;
                        while (fields.hasNext()) {
                            field = (Element) fields.next();
                            if ("username".equals(field.attributeValue("var"))) {
                                field.addElement("value").addText(
                                        user.getUsername());
                            } else if ("name".equals(field
                                    .attributeValue("var"))) {
                                field.addElement("value").addText(
                                        user.getName());
                            } else if ("email".equals(field
                                    .attributeValue("var"))) {
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

                    // So that we can set a more informative error message back,
                    // lets test this for stringprep validity now.
                    if (username != null) {
                        Stringprep.nodeprep(username);
                    }

                    if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
                        // Flag that indicates if the user is *only* changing his password
                        boolean onlyPassword = false;
                        if (iqElement.elements().size() == 2
                                && iqElement.element("username") != null
                                && iqElement.element("password") != null) {
                            onlyPassword = true;
                        }

                        // If inband registration is not allowed, return an error.
                        if (!onlyPassword && !registrationEnabled) {
                            reply = IQ.createResultIQ(packet);
                            reply.setChildElement(packet.getChildElement()
                                    .createCopy());
                            reply.setError(PacketError.Condition.forbidden);
                            return reply;
                        } else {
                            User user = userService.getUser(session
                                    .getUsername());
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
                                // An admin can create new accounts when logged in.                                
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
                        }
                    } else {
                        // Inform the entity of failed registration if some required
                        // information was not provided
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

                    //                    // Set and save the extra user info (e.g. full name, etc.)
                    //                    if (newUser != null && name != null
                    //                            && !name.equals(newUser.getName())) {
                    //                        newUser.setName(name);
                    //                    }

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
                // The specified username is not correct according to the stringprep specs
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.jid_malformed);
            } catch (IllegalArgumentException e) {
                // At least one of the fields passed in is not valid
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.not_acceptable);
                log.warn(e);
            } catch (UnsupportedOperationException e) {
                // The User provider is read-only so this operation is not allowed
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.not_allowed);
            } catch (Exception e) {
                // Some unexpected error happened so return an internal_server_error
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
