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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XpnServer {

    private static Log log = LogFactory.getLog(XpnServer.class);

    private static XpnServer instance;

    private ApplicationContext context;

    public Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static XpnServer getInstance() {
        return instance;
    }

    public XpnServer() {
        if (instance != null) {
            throw new IllegalStateException("A server is already running");
        }
        instance = this;
        start();
    }

    public void start() {
        try {
            context = new ClassPathXmlApplicationContext("spring-config.xml");
            log.info("XpnServer started.");

        } catch (Exception e) {
            e.printStackTrace();
            // shutdownServer();
        }
    }

    //    public static void main(String[] args) {
    //        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    //        log.info("XpnServer started.");
    //    }

}
