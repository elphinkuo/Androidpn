/*
 * Copyright (C) 2010 The Androidpn Team
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

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class SdkPhoneStateListener extends PhoneStateListener {

    private static final String LOGTAG = LogUtil
            .makeLogTag(SdkPhoneStateListener.class);

    private final MainService mainService;

    public SdkPhoneStateListener(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        super.onDataConnectionStateChanged(state);
        Log.d(LOGTAG, "Cellular data state is " + MainService.getState(state));
        if (state == TelephonyManager.DATA_CONNECTED) { // CONNECTED
            MainService.restart(mainService);
        }
    }

}
