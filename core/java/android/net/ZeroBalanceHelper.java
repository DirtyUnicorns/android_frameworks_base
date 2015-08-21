/*
 ** Copyright (c) 2015, The Linux Foundation. All rights reserved.

 ** Redistribution and use in source and binary forms, with or without
 ** modification, are permitted provided that the following conditions are
 ** met:
 **     * Redistributions of source code must retain the above copyright
 **       notice, this list of conditions and the following disclaimer.
 **     * Redistributions in binary form must reproduce the above
 **       copyright notice, this list of conditions and the following
 **       disclaimer in the documentation and/or other materials provided
 **       with the distribution.
 **     * Neither the name of The Linux Foundation nor the names of its
 **       contributors may be used to endorse or promote products derived
 **       from this software without specific prior written permission.

 ** THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 ** ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 ** BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 ** CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 ** SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 ** BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 ** WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 ** OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 ** IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package android.net;

import android.app.ActivityThread;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

/** @hide */
public final class ZeroBalanceHelper {

    public static final String BACKGROUND_DATA_PROPERTY = "sys.background.data.disable";
    public static final String BACKGROUND_DATA_BROADCAST = "com.background.data.broadcast";
    public static final String TAG = "ZeroBalance";

    private Context mContext = null;

    public ZeroBalanceHelper() {
        mContext = ActivityThread.currentApplication();
    }

    public void setBgDataProperty(String enabled) {
        Intent intent = new Intent();
        intent.setAction(BACKGROUND_DATA_BROADCAST);
        intent.putExtra("enabled", enabled);
        mContext.sendBroadcast(intent);
    }

    public String getBgDataProperty() {
        return SystemProperties.get(BACKGROUND_DATA_PROPERTY, "false");
    }

    public String getConfiguredRedirectURL() {
        String redirectURL = mContext.getResources().getString(
                com.android.internal.R.string.operator_config_url);
        Log.d(TAG, "Returning the configured redirect URL   :   "
                + redirectURL);
        return redirectURL;
    }
}
