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

import java.util.List;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class Config {

    public static String getString(String key) {
        return ConfigManager.getInstance().getConfig().getString(key);
    }

    public static String getString(String key, String def) {
        return ConfigManager.getInstance().getConfig().getString(key, def);
    }

    public static int getInt(String key) {
        return ConfigManager.getInstance().getConfig().getInt(key);
    }

    public static int getInt(String key, int def) {
        return ConfigManager.getInstance().getConfig().getInt(key, def);
    }

    public static long getLong(String key) {
        return ConfigManager.getInstance().getConfig().getLong(key);
    }

    public static long getLong(String key, long def) {
        return ConfigManager.getInstance().getConfig().getLong(key, def);
    }

    public static float getFloat(String key) {
        return ConfigManager.getInstance().getConfig().getFloat(key);
    }

    public static float getFloat(String key, float def) {
        return ConfigManager.getInstance().getConfig().getFloat(key, def);
    }

    public static double getDouble(String key) {
        return ConfigManager.getInstance().getConfig().getDouble(key);
    }

    public static double getDouble(String key, double def) {
        return ConfigManager.getInstance().getConfig().getDouble(key, def);
    }

    public static boolean getBoolean(String key) {
        return ConfigManager.getInstance().getConfig().getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean def) {
        return ConfigManager.getInstance().getConfig().getBoolean(key, def);
    }

    public static String[] getStringArray(String key) {
        return ConfigManager.getInstance().getConfig().getStringArray(key);
    }

    @SuppressWarnings("unchecked")
    public static List getList(String key) {
        return ConfigManager.getInstance().getConfig().getList(key);
    }

    @SuppressWarnings("unchecked")
    public static List getList(String key, List def) {
        return ConfigManager.getInstance().getConfig().getList(key, def);
    }

    public static void setProperty(String key, Object value) {
        ConfigManager.getInstance().getConfig().setProperty(key, value);
    }

}
