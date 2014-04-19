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
package org.androidpn.server.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class ConfigManager {

    private static final Log log = LogFactory.getLog(ConfigManager.class);

    private static Configuration config;

    private static ConfigManager instance;

    private ConfigManager() {
        loadConfig();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                instance = new ConfigManager();
            }
        }
        return instance;
    }

    public void loadConfig() {
        loadConfig("config.xml");
    }

    public void loadConfig(String configFileName) {
        try {
            ConfigurationFactory factory = new ConfigurationFactory(
                    configFileName);
            config = factory.getConfiguration();
            log.info("Configuration loaded: " + configFileName);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("Configuration loading error: "
                    + configFileName, ex);
        }
    }

    public Configuration getConfig() {
        return config;
    }

}
