/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use mHost file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.NetworkUtils;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.systemui.R;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.systemui.Dependency;
import com.android.systemui.SysUIToast;
import com.android.systemui.qs.QSHost;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;

import java.net.InetAddress;
import javax.inject.Inject;

public class AdbOverNetworkTile extends QSTileImpl<BooleanState> {

    private final NetworkController mController;
    private final WifiSignalCallback mSignalCallback = new WifiSignalCallback();
    private final ActivityStarter mActivityStarter;
    private final KeyguardMonitor mKeyguard;

    private CharSequence mAddressLabel;
    private CharSequence mForcedTcpWarning;
    private int mForcedTcp = -1;

    @Inject
    public AdbOverNetworkTile(QSHost host, ActivityStarter activityStarter, KeyguardMonitor keyguardMonitor) {
        super(host);
        mController = Dependency.get(NetworkController.class);
        mActivityStarter = activityStarter;
        mKeyguard = keyguardMonitor;
        final String persistTcpProp =  SystemProperties.get("persist.adb.tcp.port");
        if (!TextUtils.isEmpty(persistTcpProp)) {
            mForcedTcp = Integer.parseInt(persistTcpProp);
            mForcedTcpWarning = mContext.getString(R.string.adb_network_forced_tcp_warn, persistTcpProp);
        }
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    protected void handleClick() {
        if (mForcedTcp > 0) {
            showForcedTcpWarning();
            return;
        }
        if (mKeyguard.isSecure() && mKeyguard.isShowing()) {
            mActivityStarter.postQSRunnableDismissingKeyguard(() -> {
                setAdbNetwork(getState().value);
            });
            return;
        }
        setAdbNetwork(getState().value);
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    protected void handleLongClick() {
        if (mForcedTcp > 0) {
            showForcedTcpWarning();
            return;
        }
        if (mAddressLabel != null) {
            SysUIToast.makeText(mContext, mAddressLabel,
                Toast.LENGTH_LONG).show();
        }
    }

    private void showForcedTcpWarning() {
        SysUIToast.makeText(mContext, mForcedTcpWarning,
            Toast.LENGTH_LONG).show();
    }

    @Override
    protected void handleUserSwitch(int newUserId) {
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_adb_network);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CUSTOM_QUICK_TILES;
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        CallbackInfo cb = (CallbackInfo) arg;
        if (cb == null) {
            cb = mSignalCallback.mInfo;
        }
        boolean wifiConnected = cb.enabled && (cb.wifiSignalIconId > 0) && (cb.enabledDesc != null);
        state.value = isAdbNetworkEnabled();
        state.icon = ResourceIcon.get(R.drawable.ic_qs_network_adb_on);
        if (state.value) {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (wifiManager.isWifiEnabled() && wifiInfo != null && wifiConnected) {
                // if wifiInfo is not null and wifi is connected, set the label to "hostAddress"
                InetAddress address = NetworkUtils.intToInetAddress(wifiInfo.getIpAddress());
                state.label = address.getHostAddress() + ":5555";
                mAddressLabel = state.label;
            } else {
                // if wifiInfo is null, set the label without host address
                state.label = mContext.getString(R.string.quick_settings_network_adb_enabled_label);
                mAddressLabel = null;
            }
            state.state = Tile.STATE_ACTIVE;
        } else {
            // Otherwise set the label and disabled icon
            state.label = mContext.getString(R.string.quick_settings_network_adb_disabled_label);
            mAddressLabel = null;
            state.state = Tile.STATE_INACTIVE;
        }
    }

    private boolean isAdbEnabled() {
        return Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.ADB_ENABLED, 0) > 0;
    }

    private boolean isAdbNetworkEnabled() {
        // ADB_PORT already gets persist.adb.tcp.port value, if set, from SystemServer.startOtherServices()
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.ADB_PORT, 0) > 0;
    }

    private void setAdbNetwork(boolean enabledTile) {
        Settings.Secure.putIntForUser(mContext.getContentResolver(),
                Settings.Secure.ADB_PORT, enabledTile ? -1 : 5555,
                UserHandle.USER_CURRENT);
    }

    private ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            refreshState();
        }
    };

    @Override
    public void handleSetListening(boolean listening) {
        if (listening) {
            mContext.getContentResolver().registerContentObserver(
                    Settings.Secure.getUriFor(Settings.Secure.ADB_PORT),
                    false, mObserver);
            mContext.getContentResolver().registerContentObserver(
                    Settings.Global.getUriFor(Settings.Global.ADB_ENABLED),
                    false, mObserver);
            mController.addCallback(mSignalCallback);
        } else {
            mContext.getContentResolver().unregisterContentObserver(mObserver);
            mController.addCallback(mSignalCallback);
        }
    }

    protected static final class CallbackInfo {
        boolean enabled;
        boolean connected;
        int wifiSignalIconId;
        String enabledDesc;

        @Override
        public String toString() {
            return new StringBuilder("CallbackInfo[")
                    .append("enabled=").append(enabled)
                    .append(",connected=").append(connected)
                    .append(",wifiSignalIconId=").append(wifiSignalIconId)
                    .append(",enabledDesc=").append(enabledDesc)
                    .append(']').toString();
        }
    }

    protected final class WifiSignalCallback implements SignalCallback {
        final CallbackInfo mInfo = new CallbackInfo();

        @Override
        public void setWifiIndicators(boolean enabled, IconState statusIcon, IconState qsIcon,
                boolean activityIn, boolean activityOut, String description, boolean isTransient,
                String statusLabel) {
            mInfo.enabled = enabled;
            mInfo.connected = qsIcon.visible;
            mInfo.wifiSignalIconId = qsIcon.icon;
            mInfo.enabledDesc = description;
            refreshState(mInfo);
        }
    }
}
