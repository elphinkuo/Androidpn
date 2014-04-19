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
package org.androidpn.server.xmpp.auth;

import org.androidpn.server.util.Config;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class AuthToken {

    private String username;

    private String domain;

    private Boolean anonymous;

    public AuthToken(String jid) {
        if (jid == null) {
            this.domain = Config.getString("xmpp.domain");
            return;
        }
        int index = jid.indexOf("@");
        if (index > -1) {
            this.username = jid.substring(0, index);
            this.domain = jid.substring(index + 1);
        } else {
            this.username = jid;
            this.domain = Config.getString("xmpp.domain");
        }
    }

    public AuthToken(String jid, Boolean anonymous) {
        int index = jid.indexOf("@");
        if (index > -1) {
            this.username = jid.substring(0, index);
            this.domain = jid.substring(index + 1);
        } else {
            this.username = jid;
            this.domain = Config.getString("xmpp.domain");
        }
        this.anonymous = anonymous;
    }

    public String getUsername() {
        return username;
    }

    public String getDomain() {
        return domain;
    }

    public boolean isAnonymous() {
        if (anonymous == null) {
            anonymous = (username == null);
        }
        return anonymous;
    }
}