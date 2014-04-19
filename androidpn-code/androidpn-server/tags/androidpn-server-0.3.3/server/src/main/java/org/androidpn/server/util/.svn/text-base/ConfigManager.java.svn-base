/*
 * Copyright (C) 2010 The Androidpn Team
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
