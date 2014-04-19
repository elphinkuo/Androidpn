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

import java.util.HashMap;
import java.util.Iterator;

import org.jivesoftware.smack.packet.IQ;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class RegistrationIQ extends IQ {

    private String apiKey;

    private String userKey;

    private String deviceId;

    private boolean registrationSuccessful;

    private int errorCode;

    private String errorMessage;

    public RegistrationIQ() {
    }

    public RegistrationIQ(String apiKey, String userKey, String deviceId,
            boolean serviceRelocation) {
        this.apiKey = apiKey;
        this.userKey = userKey;
        this.deviceId = deviceId;
        //        this.serviceRelocation = serviceRelocation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        HashMap map = new HashMap();
        map.put("apiKey", apiKey);
        map.put("userKey", userKey);
        map.put("deviceId", deviceId);
        buf.append("<registration xmlns=\"androidpn:iq:registration\">");
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            String value = (String) map.get(name);
            if (value == null) {
                buf.append("<").append(name).append("/>");
            } else {
                buf.append("<").append(name).append(">");
                buf.append(value);
                buf.append("</").append(name).append(">");
            }
        }

        buf.append("</registration>");
        return buf.toString();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }

    public void setRegistrationSuccessful(boolean registrationSuccessful) {
        this.registrationSuccessful = registrationSuccessful;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
