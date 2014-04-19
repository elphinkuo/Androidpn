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

import org.androidpn.server.xmpp.PacketException;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.router.PacketDeliverer;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public abstract class IQHandler {

    protected final Log log = LogFactory.getLog(getClass());

    protected SessionManager sessionManager;

    public IQHandler() {
        sessionManager = SessionManager.getInstance();
    }

    public void process(Packet packet) throws PacketException {
        IQ iq = (IQ) packet;
        try {
            IQ reply = handleIQ(iq);
            if (reply != null) {
                PacketDeliverer.deliver(reply);
            }
        } catch (UnauthorizedException e) {
            if (iq != null) {
                try {
                    IQ response = IQ.createResultIQ(iq);
                    response.setChildElement(iq.getChildElement().createCopy());
                    response.setError(PacketError.Condition.not_authorized);
                    sessionManager.getSession(iq.getFrom()).process(response);
                } catch (Exception de) {
                    log.error("Internal server error", de);
                    sessionManager.getSession(iq.getFrom()).close();
                }
            }
        } catch (Exception e) {
            log.error("Internal server error", e);
            try {
                IQ response = IQ.createResultIQ(iq);
                response.setChildElement(iq.getChildElement().createCopy());
                response.setError(PacketError.Condition.internal_server_error);
                sessionManager.getSession(iq.getFrom()).process(response);
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    public abstract IQ handleIQ(IQ packet) throws UnauthorizedException;

    public abstract IQHandlerInfo getInfo();
}
