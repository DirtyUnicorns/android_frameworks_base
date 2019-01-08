/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.internal.util.du;

import android.app.ActivityManagerNative;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.RemoteException;
import android.os.SystemProperties;

import com.android.internal.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

public class Utils {

    // Check to see if device is WiFi only
    public static boolean isWifiOnly(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return (cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE) == false);
    }

    // Check to see if a package is installed
    public static boolean isPackageInstalled(Context context, String pkg, boolean ignoreState) {
        if (pkg != null) {
            try {
                PackageInfo pi = context.getPackageManager().getPackageInfo(pkg, 0);
                if (!pi.applicationInfo.enabled && !ignoreState) {
                    return false;
                }
            } catch (NameNotFoundException e) {
                return false;
            }
        }

        return true;
    }

    public static boolean isPackageInstalled(Context context, String pkg) {
        return isPackageInstalled(context, pkg, true);
    }

    // Check to see if device supports the Fingerprint scanner
    public static boolean hasFingerprintSupport(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(
                Context.FINGERPRINT_SERVICE);
        return context.getApplicationContext().checkSelfPermission(
                Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                (fingerprintManager != null && fingerprintManager.isHardwareDetected());
    }

    // Check to see if device not only supports the Fingerprint scanner but also if is enrolled
    public static boolean hasFingerprintEnrolled(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(
                Context.FINGERPRINT_SERVICE);
        return context.getApplicationContext().checkSelfPermission(
                Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                (fingerprintManager != null && fingerprintManager.isHardwareDetected() &&
                        fingerprintManager.hasEnrolledFingerprints());
    }

    // Check to see if device has a camera
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    // Check to see if device supports NFC
    public static boolean hasNFC(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
    }

    // Check to see if device supports Wifi
    public static boolean hasWiFi(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
    }

    // Check to see if device supports Bluetooth
    public static boolean hasBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    // Check to see if device supports an alterative ambient display package
    public static boolean hasAltAmbientDisplay(Context context) {
        return context.getResources().getBoolean(
                com.android.internal.R.bool.config_alt_ambient_display);
    }

    // Check to see if device supports A/B (seamless) system updates
    public static boolean isABdevice(Context context) {
        return SystemProperties.getBoolean("ro.build.ab_update", false);
    }

    // Check for Chinese language
    public static boolean isChineseLanguage() {
        return Resources.getSystem().getConfiguration().locale.getLanguage().startsWith(
                Locale.CHINESE.getLanguage());
    }

    // Flashlight
    public static boolean deviceHasFlashlight(Context ctx) {
        return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    // Method to detect navigation bar is in use
    public static boolean hasNavigationBar() {
        boolean hasNavbar = false;
        try {
            Class<?> windowManagerGlobalClass = Class.forName(
                    "android.view.WindowManagerGlobal");
            Method getWmServiceMethod = windowManagerGlobalClass.getDeclaredMethod(
                    "getWindowManagerService");
            getWmServiceMethod.setAccessible(true);
            Object iWindowManager = getWmServiceMethod.invoke(null);
            Class<?> iWindowManagerClass = iWindowManager.getClass();
            Method hasNavBarMethod = iWindowManagerClass.getDeclaredMethod(
                    "hasNavigationBar");
            hasNavBarMethod.setAccessible(true);
            hasNavbar = (Boolean) hasNavBarMethod.invoke(iWindowManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavbar;
    }

    // Method to detect if device has dash charge
    public static boolean isDashCharger() {
        try {
            FileReader file = new FileReader(
                    "/sys/class/power_supply/battery/fastchg_status");
            BufferedReader br = new BufferedReader(file);
            String state = br.readLine();
            br.close();
            file.close();
            return "1".equals(state);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return false;
    }

    // Method to detect if device is plugged in (wired or wireless)
    public static boolean isPlugged(Context context) {
        boolean isPlugged;
        Intent intent = context.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged ==
                BatteryManager.BATTERY_PLUGGED_AC ||
                plugged == BatteryManager.BATTERY_PLUGGED_USB;
        isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        return isPlugged;
    }

    // Check if task is in lock task mode
    public static boolean isInLockTaskMode() {
        try {
            return ActivityManagerNative.getDefault().isInLockTaskMode();
        } catch (RemoteException e) {
            return false;
        }
    }
}
