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
package org.androidpn.server.container;

import java.io.File;

import org.androidpn.server.util.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.webapp.WebAppContext;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class AdminConsole {

    private static final Log log = LogFactory.getLog(AdminConsole.class);

    private String adminHost;

    private int adminPort;

    private Server adminServer;

    private ContextHandlerCollection contexts;

    private boolean httpStarted = false;

    public AdminConsole(String homeDir) {
        contexts = new ContextHandlerCollection();
        Context context = new WebAppContext(contexts, homeDir + File.separator
                + "console", "/");
        context.setWelcomeFiles(new String[] { "index.jsp" });

        adminHost = Config.getString("admin.console.host", "127.0.0.1");
        adminPort = Config.getInt("admin.console.port", 8080);
        adminServer = new Server();
        adminServer.setSendServerVersion(false);
    }

    public void startup() {
        if (adminPort > 0) {
            Connector httpConnector = new SelectChannelConnector();
            httpConnector.setHost(adminHost);
            httpConnector.setPort(adminPort);
            adminServer.addConnector(httpConnector);
        }

        if (adminServer.getConnectors() == null
                || adminServer.getConnectors().length == 0) {
            adminServer = null;
            log.warn("Admin console not started due to configuration error.");
            return;
        }

        adminServer
                .setHandlers(new Handler[] { contexts, new DefaultHandler() });

        try {
            adminServer.start();
            httpStarted = true;
            log.debug("Admin console started.");
        } catch (Exception e) {
            log.error("Could not start admin conosle server", e);
        }
    }

    public void shutdown() {
        try {
            if (adminServer != null && adminServer.isRunning()) {
                adminServer.stop();
            }
        } catch (Exception e) {
            log.error("Error stopping admin console server", e);
        }
        adminServer = null;
    }

    public ContextHandlerCollection getContexts() {
        return contexts;
    }

    public void restart() {
        try {
            adminServer.stop();
            adminServer.start();
        } catch (Exception e) {
            log.error(e);
        }
    }

    public String getAdminHost() {
        return adminHost;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public boolean isHttpStarted() {
        return httpStarted;
    }

}
