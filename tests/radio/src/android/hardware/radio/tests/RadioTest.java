/*
 * Copyright (C) 2017 The Android Open Source Project
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
package android.hardware.radio.tests;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioTuner;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * A test for broadcast radio API.
 */
@RunWith(AndroidJUnit4.class)
public class RadioTest {

    public final Context mContext = InstrumentationRegistry.getContext();

    private final int kConfigCallbacktimeoutNs = 10000;
    private final int kTuneCallbacktimeoutNs = 30000;

    private RadioManager mRadioManager;
    private RadioTuner mRadioTuner;
    private final List<RadioManager.ModuleProperties> mModules = new ArrayList<>();
    @Mock private RadioTuner.Callback mCallback;

    RadioManager.AmBandDescriptor mAmBandDescriptor;
    RadioManager.FmBandDescriptor mFmBandDescriptor;

    RadioManager.BandConfig mAmBandConfig;
    RadioManager.BandConfig mFmBandConfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // check if radio is supported and skip the test if it's not
        PackageManager packageManager = mContext.getPackageManager();
        boolean isRadioSupported = packageManager.hasSystemFeature(PackageManager.FEATURE_RADIO);
        assumeTrue(isRadioSupported);

        mRadioManager = (RadioManager)mContext.getSystemService(Context.RADIO_SERVICE);
        assertNotNull(mRadioManager);

        int status = mRadioManager.listModules(mModules);
        assertEquals(RadioManager.STATUS_OK, status);
        assertFalse(mModules.isEmpty());
    }

    @After
    public void tearDown() {
        mRadioManager = null;
        mModules.clear();
        if (mRadioTuner != null) {
            mRadioTuner.close();
            mRadioTuner = null;
        }
        verifyNoMoreInteractions(mCallback);
    }

    private void openTuner() {
        openTuner(true);
    }

    private void resetCallback() {
        verifyNoMoreInteractions(mCallback);
        Mockito.reset(mCallback);
    }

    private void openTuner(boolean withAudio) {
        assertNull(mRadioTuner);

        // find FM band and build its config
        RadioManager.ModuleProperties module = mModules.get(0);
        for (RadioManager.BandDescriptor band : module.getBands()) {
            if (band.getType() == RadioManager.BAND_AM) {
                mAmBandDescriptor = (RadioManager.AmBandDescriptor)band;
            }
            if (band.getType() == RadioManager.BAND_FM) {
                mFmBandDescriptor = (RadioManager.FmBandDescriptor)band;
            }
        }
        assertNotNull(mAmBandDescriptor);
        assertNotNull(mFmBandDescriptor);
        mAmBandConfig = new RadioManager.AmBandConfig.Builder(mAmBandDescriptor).build();
        mFmBandConfig = new RadioManager.FmBandConfig.Builder(mFmBandDescriptor).build();

        mRadioTuner = mRadioManager.openTuner(module.getId(),
                mFmBandConfig, withAudio, mCallback, null);
        assertNotNull(mRadioTuner);
        verify(mCallback, timeout(kConfigCallbacktimeoutNs).times(1)).onConfigurationChanged(any());
        resetCallback();
    }

    @Test
    public void testOpenTuner() {
        openTuner();
    }

    @Test
    public void testReopenTuner() throws Throwable {
        openTuner();
        mRadioTuner.close();
        mRadioTuner = null;
        Thread.sleep(100);  // TODO(b/36122635): force reopen
        openTuner();
    }

    @Test
    public void testSetAndGetConfiguration() {
        openTuner();

        // set
        int ret = mRadioTuner.setConfiguration(mAmBandConfig);
        assertEquals(RadioManager.STATUS_OK, ret);
        verify(mCallback, timeout(kConfigCallbacktimeoutNs).times(1)).onConfigurationChanged(any());

        // get
        RadioManager.BandConfig[] config = new RadioManager.BandConfig[1];
        ret = mRadioTuner.getConfiguration(config);
        assertEquals(RadioManager.STATUS_OK, ret);

        assertEquals(mAmBandConfig, config[0]);
    }

    @Test
    public void testSetBadConfiguration() throws Throwable {
        openTuner();

        // set bad config
        Constructor<RadioManager.AmBandConfig> configConstr =
                RadioManager.AmBandConfig.class.getDeclaredConstructor(
                        int.class, int.class, int.class, int.class, int.class, boolean.class);
        configConstr.setAccessible(true);
        RadioManager.AmBandConfig badConfig = configConstr.newInstance(
                0 /*region*/, RadioManager.BAND_AM /*type*/,
                10000 /*lowerLimit*/, 1 /*upperLimit*/, 100 /*spacing*/, false /*stereo*/);
        int ret = mRadioTuner.setConfiguration(badConfig);
        assertEquals(RadioManager.STATUS_BAD_VALUE, ret);
        verify(mCallback, never()).onConfigurationChanged(any());

        // set null config
        ret = mRadioTuner.setConfiguration(null);
        assertEquals(RadioManager.STATUS_BAD_VALUE, ret);
        verify(mCallback, never()).onConfigurationChanged(any());

        // setting good config should recover
        ret = mRadioTuner.setConfiguration(mAmBandConfig);
        assertEquals(RadioManager.STATUS_OK, ret);
        verify(mCallback, timeout(kConfigCallbacktimeoutNs).times(1)).onConfigurationChanged(any());
    }

    @Test
    public void testMute() {
        openTuner();

        boolean isMuted = mRadioTuner.getMute();
        assertFalse(isMuted);

        int ret = mRadioTuner.setMute(true);
        assertEquals(RadioManager.STATUS_OK, ret);
        isMuted = mRadioTuner.getMute();
        assertTrue(isMuted);

        ret = mRadioTuner.setMute(false);
        assertEquals(RadioManager.STATUS_OK, ret);
        isMuted = mRadioTuner.getMute();
        assertFalse(isMuted);
    }

    @Test
    public void testMuteNoAudio() {
        openTuner(false);

        int ret = mRadioTuner.setMute(false);
        assertEquals(RadioManager.STATUS_ERROR, ret);

        boolean isMuted = mRadioTuner.getMute();
        assertTrue(isMuted);
    }

    @Test
    public void testStep() {
        openTuner();

        int ret = mRadioTuner.step(RadioTuner.DIRECTION_DOWN, true);
        assertEquals(RadioManager.STATUS_OK, ret);
        verify(mCallback, timeout(kTuneCallbacktimeoutNs).times(1)).onProgramInfoChanged(any());

        resetCallback();

        ret = mRadioTuner.step(RadioTuner.DIRECTION_UP, false);
        assertEquals(RadioManager.STATUS_OK, ret);
        verify(mCallback, timeout(kTuneCallbacktimeoutNs).times(1)).onProgramInfoChanged(any());
    }
}
