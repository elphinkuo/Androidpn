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
package org.androidpn.sdk;

import java.util.UUID;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Class desciption here.
 * 
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class XmppManager {

    private static final String LOGTAG = "XmppManager";

    private Context context;

    private SharedPreferences sdkPreferences;

    private Editor editor;

    private String xmppHost;

    private int xmppPort;

    private XMPPConnection connection;

    private String username;

    private String password;

    // private boolean running;

    public XmppManager(Context context) {
        this.context = context;
        this.sdkPreferences = context.getSharedPreferences(
                ServiceManager.SDK_PREFERENCES, Context.MODE_PRIVATE);
        this.editor = sdkPreferences.edit();

        this.xmppHost = sdkPreferences.getString(Constants.XMPP_HOST,
                "localhost");
        this.xmppPort = sdkPreferences.getInt(Constants.XMPP_PORT, 5222);
        this.username = sdkPreferences.getString(Constants.XMPP_USERNAME,
                username);
        this.password = sdkPreferences.getString(Constants.XMPP_PASSWORD,
                password);
    }

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }

    private boolean isRegistered() {
        return sdkPreferences.contains(Constants.XMPP_USERNAME)
                && sdkPreferences.contains(Constants.XMPP_PASSWORD);
    }

    private void removeRegistration() {
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }

    //	public void connectToXmpp() {
    //		connect();
    //		login();
    //		//register();
    //	}

    public void connect() {
        Log.d(LOGTAG, "connect().....");
        if (!isConnected()) {
            // Create the configuration for this new connection
            ConnectionConfiguration config = new ConnectionConfiguration(
                    xmppHost, xmppPort, "AndroidpnClient");
            //            config.setCompressionEnabled(true);
            //            config.setSASLAuthenticationEnabled(true);

            connection = new XMPPConnection(config);

            try {
                // Connect to the server
                connection.connect();

                ProviderManager.getInstance()
                        .addIQProvider("registration",
                                "androidpn:iq:registration",
                                new RegistrationProvider());
                ProviderManager.getInstance()
                        .addIQProvider("notification",
                                "androidpn:iq:notification",
                                new RegistrationProvider());
                
                connection.addPacketListener(new NotificationListener(this), null);

            } catch (XMPPException xe) {
                Log.e(LOGTAG, "connect()..... failed.", xe);
            }
        }
        Log.d(LOGTAG, "connect()..... done!");
    }

    public void login() {
        Log.d(LOGTAG, "login().....");

        //        Log.e(LOGTAG, "isConnected()=" + connection.isConnected());
        //        Log.e(LOGTAG, "isAuthenticated()=" + connection.isAuthenticated());
        //        Log.e(LOGTAG, "isRegistered()=" + isRegistered());
        //        Log.e(LOGTAG, "!isAuthenticated() && isRegistered()="
        //                + (!isAuthenticated() && isRegistered()));

        if (isRegistered()) {
            if (!isAuthenticated()) {
                try {

                    //                    PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                    //                            registration.getPacketID()));
                    //
                    //                    PacketListener packetListener = new PacketListener() {

                    Log.d(LOGTAG, "username=" + username);
                    Log.d(LOGTAG, "password=" + password);

                    //connection.login(username, password, "AndroidpnClient");
                    connection.login(username, password);
                    Log.i(LOGTAG, "login()..... logged in successfully.");

                } catch (XMPPException xe) {
                    Log
                            .e(LOGTAG,
                                    "login()..... failed by a typical error.",
                                    xe);
                    removeRegistration();
                    register();
                    login();

                } catch (Exception e) {
                    Log.e(LOGTAG,
                            "login()..... failed by an unexpected error.", e);
                }
            } else {
                Log.i(LOGTAG, "login()..... logged in already.");
            }
        } else {
            Log.i(LOGTAG, "login()..... not yet registered.");
        }
        Log.d(LOGTAG, "login()..... done!");
    }

    public void register() {
        Log.d(LOGTAG, "register().....");

        if (!isRegistered()) {

            // final XmppManager xmppManager = XmppManager.this;
            final String rUsername = newRandomUUID();
            final String rPassword = newRandomUUID();

            Registration registration = new Registration();
            //            PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
            //                    registration.getPacketID()), new PacketTypeFilter(
            //                    Message.class));
            PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                    registration.getPacketID()));

            PacketListener packetListener = new PacketListener() {

                public void processPacket(Packet packet) {

                    Log.d("PacketListener", "processPacket().....");
                    Log.e("PacketListener", "packet=" + packet.toXML());

                    if (packet instanceof IQ) {
                        IQ response = (IQ) packet;
                        if (response.getType() == IQ.Type.ERROR) {
                            if (!response.getError().toString().contains("409")) {
                                Log.e(LOGTAG,
                                        "Unknown error while registering XMPP account! "
                                                + response.getError()
                                                        .getCondition());
                            }
                        } else if (response.getType() == IQ.Type.RESULT) {
                            username = rUsername;
                            password = rPassword;
                            Log.d(LOGTAG, "username=" + username);
                            Log.d(LOGTAG, "password=" + password);

                            editor.putString(Constants.XMPP_USERNAME,
                                    username);
                            editor.putString(Constants.XMPP_PASSWORD,
                                    password);
                            editor.commit();

                            Log.i(LOGTAG, "XMPP account registered.");
                            //login();
                        }
                    }
                }
            };

            connection.addPacketListener(packetListener, packetFilter);

            registration.setType(IQ.Type.SET);
            //registration.setTo(xmppHost);
            //            Map<String, String> attributes = new HashMap<String, String>();
            //            attributes.put("username", rUsername);
            //            attributes.put("password", rPassword);
            //            registration.setAttributes(attributes);
            registration.addAttribute("username", rUsername);
            registration.addAttribute("password", rPassword);
            connection.sendPacket(registration);

        } else {
            Log.i(LOGTAG, "register()..... account registered already.");
        }
        Log.d(LOGTAG, "register()..... done!");
    }

    //    public static String getUsername(XmppManager xmppManager) {
    //        return xmppManager.username;
    //    }
    //
    //    public static String getPassword(XmppManager xmppManager) {
    //        return xmppManager.password;
    //    }
    //
    //    public static void setUsername(XmppManager xmppManager, String username) {
    //        xmppManager.username = username;
    //    }
    //
    //    public static void setPassword(XmppManager xmppManager, String password) {
    //        xmppManager.password = password;
    //    }
    //
    //    public static SharedPreferences getSdkPreferences(XmppManager xmppManager) {
    //        return xmppManager.sdkPreferences;
    //    }

    public static Context getContext(XmppManager xmppManager) {
        return xmppManager.context;
    }

    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }

}
