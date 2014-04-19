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
package org.androidpn.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.androidpn.server.container.AdminConsole;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.util.TaskEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XmppServer {

    private static final Log log = LogFactory.getLog(XmppServer.class);

    private static XmppServer instance;

    private ApplicationContext context;

    private String serverName;

    private String serverHomeDir;

    private boolean shuttingDown;

    private SSLContext sslContext;

    public static XmppServer getInstance() {
        return instance;
    }

    public XmppServer() {
        if (instance != null) {
            throw new IllegalStateException("A server is already running");
        }
        instance = this;
        start();
    }

    public void start() {
        try {
            if (isStandAlone()) {
                Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
            }

            locateServer();
            serverName = Config.getString("xmpp.domain", "127.0.0.1")
                    .toLowerCase();
            context = new ClassPathXmlApplicationContext("spring-config.xml");
            log.info("Spring Configuration loaded.");

            AdminConsole adminConsole = new AdminConsole(serverHomeDir);
            adminConsole.startup();
            if (adminConsole.isHttpStarted()) {
                log.info("Admin console listening at: http://"
                        + adminConsole.getAdminHost() + ":"
                        + adminConsole.getAdminPort());
            }
            log.info("XmppServer started: " + serverName);

        } catch (Exception e) {
            e.printStackTrace();
            shutdownServer();
        }
    }

    public void stop() {
        shutdownServer();
        Thread shutdownThread = new ShutdownThread();
        shutdownThread.setDaemon(true);
        shutdownThread.start();
    }

    public Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public String getServerName() {
        return serverName;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    private void locateServer() throws FileNotFoundException {
        String baseDir = System.getProperty("base.dir", "..");

        if (serverHomeDir == null) {
            try {
                File confDir = new File(baseDir, "conf");
                if (confDir.exists()) {
                    serverHomeDir = confDir.getParentFile().getCanonicalPath();
                }
            } catch (FileNotFoundException fe) {
                // Ignore
            } catch (IOException ie) {
                // Ignore
            }
        }

        if (serverHomeDir == null) {
            System.err.println("Could not locate home");
            throw new FileNotFoundException();
        } else {
            Config.setProperty("server.home.dir", serverHomeDir);
            log.debug("server.home.dir=" + serverHomeDir);
        }
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    private void shutdownServer() {
        shuttingDown = true;
        // Shutdown the task engine
        TaskEngine.getInstance().shutdown();
        // Close all connections
        SessionManager.getInstance().closeAllSessions();
        log.info("XmppServer stopped");
    }

    public boolean isStandAlone() {
        boolean standalone;
        try {
            standalone = Class.forName("org.androidpn.starter.ServerStarter") != null;
        } catch (ClassNotFoundException e) {
            standalone = false;
        }
        return standalone;
    }

    private class ShutdownHookThread extends Thread {
        public void run() {
            shutdownServer();
            log.info("Server halted");
            System.err.println("Server halted");
        }
    }

    private class ShutdownThread extends Thread {
        public void run() {
            try {
                Thread.sleep(5000);
                System.exit(0);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

}
