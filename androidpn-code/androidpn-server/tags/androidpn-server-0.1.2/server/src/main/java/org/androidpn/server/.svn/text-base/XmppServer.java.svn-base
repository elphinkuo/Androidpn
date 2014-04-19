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

import org.androidpn.server.util.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XmppServer {

    private static Log log = LogFactory.getLog(XmppServer.class);

    private static XmppServer instance;

    private ApplicationContext context;

    private String serverName;

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
            serverName = Config.getString("xmpp.domain", "127.0.0.1")
                    .toLowerCase();
            context = new ClassPathXmlApplicationContext("spring-config.xml");
            log.info("XmppServer started: " + serverName);

        } catch (Exception e) {
            e.printStackTrace();
            // shutdownServer();
        }
    }

    public Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public String getServerName() {
        return serverName;
    }

    //    public static void main(String[] args) {
    //        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    //        log.info("XmppServer started.");
    //    }

}
