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
package org.androidpn.starter;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class ServerClassLoader extends URLClassLoader {

    /**
     * Constructs the classloader.
     * 
     * @param parent
     *            the parent class loader (or null for none).
     * @param confDir
     *            the directory to load configration files from.
     * @param libDir
     *            the directory to load jar files from.
     * @throws java.net.MalformedURLException
     *             if the libDir path is not valid.
     */
    public ServerClassLoader(ClassLoader parent, File confDir, File libDir)
            throws MalformedURLException {
        super(new URL[] { confDir.toURI().toURL(), libDir.toURI().toURL() },
                parent);

        File[] jars = libDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean accept = false;
                String smallName = name.toLowerCase();
                if (smallName.endsWith(".jar")) {
                    accept = true;
                } else if (smallName.endsWith(".zip")) {
                    accept = true;
                }
                return accept;
            }
        });

        if (jars == null) {
            return;
        }

        for (int i = 0; i < jars.length; i++) {
            if (jars[i].isFile()) {
                addURL(jars[i].toURI().toURL());
            }
        }
    }

}
