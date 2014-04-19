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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.androidpn.server.XmppServer;
import org.androidpn.server.service.ServiceManager;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.util.StringUtils;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class AuthManager {

    private static final Log log = LogFactory.getLog(AuthManager.class);

    private static final Object DIGEST_LOCK = new Object();

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            log.error("Internal server error", e);
        }
    }

    public static String getPassword(String username)
            throws UserNotFoundException {
        return ServiceManager.getUserService().getUserByUsername(username)
                .getPassword();
    }

    public static AuthToken authenticate(String username, String password)
            throws UnauthorizedException {
        if (username == null || password == null) {
            throw new UnauthorizedException();
        }
        username = username.trim().toLowerCase();
        if (username.contains("@")) {
            // Check that the specified domain matches the server's domain
            int index = username.indexOf("@");
            String domain = username.substring(index + 1);
            if (domain.equals(XmppServer.getInstance().getServerName())) {
                username = username.substring(0, index);
            } else {
                throw new UnauthorizedException();
            }
        }
        try {
            if (!password.equals(getPassword(username))) {
                throw new UnauthorizedException();
            }
        } catch (UserNotFoundException unfe) {
            throw new UnauthorizedException();
        }
        return new AuthToken(username);
    }

    public static AuthToken authenticate(String username, String token,
            String digest) throws UnauthorizedException {
        if (username == null || token == null || digest == null) {
            throw new UnauthorizedException();
        }
        username = username.trim().toLowerCase();
        if (username.contains("@")) {
            // Check that the specified domain matches the server's domain
            int index = username.indexOf("@");
            String domain = username.substring(index + 1);
            if (domain.equals(XmppServer.getInstance().getServerName())) {
                username = username.substring(0, index);
            } else {
                throw new UnauthorizedException();
            }
        }
        try {
            String password = getPassword(username);
            String anticipatedDigest = createDigest(token, password);
            if (!digest.equalsIgnoreCase(anticipatedDigest)) {
                throw new UnauthorizedException();
            }
        } catch (UserNotFoundException unfe) {
            throw new UnauthorizedException();
        }

        return new AuthToken(username);
    }

    public static boolean authorize(String username, String principal) {
        if (log.isDebugEnabled()) {
            log.debug("Trying authorize(" + username + " , " + principal + ")");
        }
        try {
            ServiceManager.getUserService().getUserByUsername(username);
        } catch (UserNotFoundException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isPlainSupported() {
        return true;
    }

    public static boolean isDigestSupported() {
        return true;
    }

    private static String createDigest(String token, String password) {
        synchronized (DIGEST_LOCK) {
            digest.update(token.getBytes());
            return StringUtils.encodeHex(digest.digest(password.getBytes()));
        }
    }

}
