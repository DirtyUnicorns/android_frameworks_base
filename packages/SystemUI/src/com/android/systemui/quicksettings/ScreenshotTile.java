/*
 * Copyright (C) 2012 CyanogenMod Project
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

package com.android.systemui.quicksettings;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.widget.Toast;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;

public class ScreenshotTile extends QuickSettingsTile {

    public Handler mHandler = new Handler();

    public ScreenshotTile(Context context,
            QuickSettingsController qsc, Handler handler) {
        super(context, qsc);

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enough to delay for statusbar collapsing
                mHandler.postDelayed(mRunnable, 500);
            }
        };
        mOnLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int delay = Settings.System.getInt(mContext.getContentResolver(),
                        Settings.System.SCREENSHOT_TOGGLE_DELAY, 5000);
                final Toast toast = Toast.makeText(mContext,
                        String.format(mContext.getResources().getString(R.string.screenshot_toast),
                                delay / 1000), Toast.LENGTH_SHORT);
                toast.show();
                // toast duration is not customizable, hack to show it only for 1 sec
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);
                mHandler.postDelayed(mRunnable, delay);
                return true;
            }
        };
    }

    @Override
    void onPostCreate() {
        updateTile();
        super.onPostCreate();
    }

    @Override
    public void updateResources() {
        updateTile();
        super.updateResources();
    }

    private synchronized void updateTile() {
        mDrawable = R.drawable.ic_qs_screenshot;
        mLabel = mContext.getString(R.string.quick_settings_screenshot_label);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        updateResources();
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            Intent intent = new Intent(Intent.ACTION_SCREENSHOT);
            mContext.sendBroadcast(intent);
        }
    };
}
