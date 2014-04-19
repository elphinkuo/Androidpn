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
package org.androidpn.sdk;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class RegistrationProvider implements IQProvider {

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        XmppRegistration registration = new XmppRegistration();
        for (boolean done = false; !done;) {
            int eventType = parser.next();
            if (eventType == 2) {
                if ("registrationSuccesful".equals(parser.getName())) {
                    registration.setRegistrationSuccessful(Boolean
                            .parseBoolean(parser.nextText()));
                }
                if ("errorCode".equals(parser.getName())) {
                    registration.setErrorCode(Integer.parseInt(parser
                            .nextText()));
                }
                if ("errorMessage".equals(parser.getName())) {
                    String input = parser.nextText();
                    String errorMessage = null;
                    if (input != null) {
                        input = input.trim();
                        if (input.length() > 0)
                            errorMessage = input;
                    }
                    registration.setErrorMessage(errorMessage);
                }
            } else if (eventType == 3
                    && "registration".equals(parser.getName())) {
                done = true;
            }
        }

        return registration;
    }

}
