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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class ServerStarter {

	private static Logger logger = Logger.getLogger("ServerStarter");

	private static final String DEFAULT_CONF_DIR = "conf";

	private static final String DEFAULT_LIB_DIR = "lib";

	public static void main(String[] args) {
		try {
			// FileHandler fh = new FileHandler("../logs/starter.log");
			// fh.setFormatter(new SimpleFormatter());
			// logger.addHandler(fh);
			StreamHandler sh = new StreamHandler(System.out,
					new SimpleFormatter());
			logger.addHandler(sh);
			logger.setLevel(Level.ALL);
			new ServerStarter().start();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	@SuppressWarnings("unchecked")
	private void start() {
		try {
			final ClassLoader parent = findParentClassLoader();

			String baseDirString = System.getProperty("base.dir", "..");

			File confDir = new File(baseDirString + File.separator
					+ DEFAULT_CONF_DIR);
			if (!confDir.exists()) {
				throw new RuntimeException("Conf directory "
						+ confDir.getAbsolutePath() + " does not exist.");
			}

			File libDir = new File(baseDirString + File.separator
					+ DEFAULT_LIB_DIR);
			if (!libDir.exists()) {
				throw new RuntimeException("Lib directory "
						+ libDir.getAbsolutePath() + " does not exist.");
			}

			ClassLoader loader = new ServerClassLoader(parent, confDir, libDir);

			Thread.currentThread().setContextClassLoader(loader);

			Class containerClass = loader
					.loadClass("org.androidpn.server.XpnServer");
			containerClass.newInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ClassLoader findParentClassLoader() {
		ClassLoader parent = Thread.currentThread().getContextClassLoader();
		if (parent == null) {
			parent = this.getClass().getClassLoader();
			if (parent == null) {
				parent = ClassLoader.getSystemClassLoader();
			}
		}
		return parent;
	}

}