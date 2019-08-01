/*
 * Copyright (C) 2015 The Android Open Source Project
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
 * limitations under the License
 */
package com.hardkernel.odroid.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.TwoStatePreference;
import android.util.Log;

import com.hardkernel.odroid.settings.R;


public class MiscFragment extends LeanbackAddBackPreferenceFragment {
    private static final String TAG = "MiscFragment";

    private static final String BT_SINK_PROP = "persist.service.bt.a2dp.sink";
    private static final String GPS_PROP = "persist.disable_location";
    private static final String SHUT_PROP = "persist.pwbtn.shutdown";

	private TwoStatePreference pref_bt_sink;
	private TwoStatePreference pref_gps;
	private TwoStatePreference pref_shut;


    public static MiscFragment newInstance() {
        return new MiscFragment();
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

    @Override
        public void onResume() {
            super.onResume();
        }

    @Override
        public void onCreatePreferences(Bundle savedInstanceState, String s) {
            setPreferencesFromResource(R.xml.misc_settings, null);

			pref_bt_sink = (TwoStatePreference) findPreference(getString(R.string.pref_enable_bluetooth_sink));
			pref_gps = (TwoStatePreference) findPreference(getString(R.string.pref_enable_gps));
			pref_shut = (TwoStatePreference) findPreference(getString(R.string.pref_force_shutdown_without_dialog));
			pref_bt_sink.setChecked(SystemProperties.getBoolean(BT_SINK_PROP, false));
			pref_gps.setChecked(!SystemProperties.getBoolean(GPS_PROP, true));
			pref_shut.setChecked(SystemProperties.getBoolean(SHUT_PROP, false));

        }
    @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            pref_bt_sink.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    SystemProperties.set(BT_SINK_PROP, value ? "true" : "false");
                    return true;
                }
            });

        pref_gps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean value = (Boolean) newValue;
                SystemProperties.set(GPS_PROP, value ? "false" : "true");
                Log.v(TAG, "onPreferenceChange: pref_gps " + newValue);
                return true;
            }
        });

        pref_shut.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean value = (Boolean) newValue;
                SystemProperties.set(SHUT_PROP, value ? "true" : "false");
                Log.v(TAG, "onPreferenceChange: pref_shut " + newValue);
                return true;
            }
        });
        }

}
