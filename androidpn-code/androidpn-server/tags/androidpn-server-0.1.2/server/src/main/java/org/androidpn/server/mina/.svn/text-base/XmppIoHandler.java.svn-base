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
package org.androidpn.server.mina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.androidpn.server.XmppServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.net.MXParser;
import org.jivesoftware.openfire.nio.XMLLightweightParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Class desciption here.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XmppIoHandler implements IoHandler {

	private static final Log log = LogFactory.getLog(XmppIoHandler.class);

	public static final String CHARSET = "UTF-8";

	public static final String XML_PARSER = "XML-PARSER";

	// private static final String CONNECTION = "CONNECTION";
	//
	// private static final String STANZA_HANDLER = "STANZA_HANDLER";

	protected String serverName;

	private static Map<Integer, XMPPPacketReader> parsers = new ConcurrentHashMap<Integer, XMPPPacketReader>();

	/**
	 * Reuse the same factory for all the connections.
	 */
	private static XmlPullParserFactory factory = null;

	static {
		try {
			factory = XmlPullParserFactory.newInstance(
					MXParser.class.getName(), null);
			factory.setNamespaceAware(true);
		} catch (XmlPullParserException e) {
			log.error("Error creating a parser factory", e);
		}
	}

	protected XmppIoHandler() {
		// serverName = Config.getString("xmpp.domain",
		// "127.0.0.1").toLowerCase();
		serverName = XmppServer.getInstance().getServerName();
	}

	protected XmppIoHandler(String serverName) {
		this.serverName = serverName;
	}

	public void sessionCreated(IoSession session) throws Exception {
		log.debug("sessionCreated()...");
	}

	public void sessionOpened(IoSession session) throws Exception {
		log.debug("sessionOpened()...");
		// Create a new XML parser for the new connection. The parser will be
		// used by the XMPPDecoder filter.
		XMLLightweightParser parser = new XMLLightweightParser(CHARSET);
		session.setAttribute(XML_PARSER, parser);
		// // Create a new Connection for the new session
		// Connection connection = new Connection(session);
		// session.setAttribute(CONNECTION, connection);
		// session.setAttribute(STANZA_HANDLER, new ClientStanzaHandler(
		// serverName, connection));
	}

	public void sessionClosed(IoSession session) throws Exception {
		log.debug("sessionClosed()...");
	}

	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug("sessionIdle()...");
	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.debug("sessionCaught()...");
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		log.debug("messageReceived()...");

		if (log.isDebugEnabled()) {
			log.debug("remoteAddress=" + session.getRemoteAddress());
			log.debug("message=" + message.toString());
		}

		// // Get the stanza handler for this session
		// StanzaHandler handler = (StanzaHandler) session
		// .getAttribute(STANZA_HANDLER);

		int hashCode = Thread.currentThread().hashCode();
		XMPPPacketReader parser = parsers.get(hashCode);
		if (parser == null) {
			parser = new XMPPPacketReader();
			parser.setXPPFactory(factory);
			parsers.put(hashCode, parser);
		}

		// // Let the stanza handler process the received stanza
		// try {
		// handler.process((String) message, parser);
		// } catch (Exception e) {
		// log.error(
		// "Closing connection due to error while processing message: "
		// + message, e);
		// Connection connection = (Connection) session
		// .getAttribute(CONNECTION);
		// connection.close();
		// }
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		log.debug("messageSent()...");
	}

}