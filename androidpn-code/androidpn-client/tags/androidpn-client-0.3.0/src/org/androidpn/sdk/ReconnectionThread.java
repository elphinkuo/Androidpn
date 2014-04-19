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

import org.jivesoftware.smack.ConnectionListener;

import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class ReconnectionThread extends Thread {

    private static final String LOGTAG = ReconnectionThread.class.getName();

    private final XmppManager xmppManager;

    private ConnectionListener connectionListener;

    private int waiting;

    ReconnectionThread(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
        this.connectionListener = xmppManager.getConnectionListener();
        this.waiting = 0;
    }

    private int a() {
        if (waiting > 20) {
            return 600;
        }
        if (waiting > 13) {
            return 300;
        }
        return waiting <= 7 ? 10 : 60;
    }

    public void run() {
        try {
            while (!isInterrupted()) {
                Log.d(LOGTAG, "Trying to reconnect in " + waiting + " seconds");
                Thread.sleep((long) a() * 1000L);
                XmppManager.reconnect(xmppManager, connectionListener);
                waiting++;
            }
        } catch (final InterruptedException e) {

            XmppManager.getHandler(xmppManager).post(new Runnable() {
                public void run() {
                    xmppManager.getConnectionListener().reconnectionFailed(e);
                }
            });
        }
    }
}
