package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.utils.du.DUActionUtils;

import com.android.systemui.qs.QSTile;
import com.android.systemui.R;

/** Quick settings tile: Hardware button control **/
public class HardwareButtonTile extends QSTile<QSTile.BooleanState> {

    public HardwareButtonTile(Host host) {
        super(host);
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    public boolean isAvailable() {
        return !DUActionUtils.hasNavbarByDefault(mContext);
    }

    @Override
    protected void handleDestroy() {
        super.handleDestroy();
    }

    @Override
    public void handleClick() {
        switchButtonState();
        refreshState();
    }

    @Override
    public Intent getLongClickIntent() {
        return new Intent().setComponent(new ComponentName(
            "com.android.settings", "com.android.settings.Settings$ButtonSettingsActivity"));
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_hardware_button_label);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.QUICK_SETTINGS;
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        state.value = getButtonState();
        state.label = mContext.getString(R.string.quick_settings_hardware_button_label);
        if (state.value) {
            state.icon = ResourceIcon.get(R.drawable.ic_qs_hardware_button_on);
            state.contentDescription =  mContext.getString(
                    R.string.accessibility_quick_settings_hardware_button_on);
        } else {
            state.icon = ResourceIcon.get(R.drawable.ic_qs_hardware_button_off);
            state.contentDescription =  mContext.getString(
                    R.string.accessibility_quick_settings_hardware_button_off);
        }
    }

    @Override
    public void setListening(boolean listening) {
        // Do nothing
    }

    private boolean getButtonState() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.HARDWARE_KEYS_DISABLE,
                0) != 0;
    }

    private void switchButtonState() {
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.HARDWARE_KEYS_DISABLE,
                getButtonState() ? 0 : 1);
    }
}
