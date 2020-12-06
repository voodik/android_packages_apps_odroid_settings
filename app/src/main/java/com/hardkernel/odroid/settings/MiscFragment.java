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
import androidx.leanback.preference.LeanbackPreferenceFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.TwoStatePreference;
import android.util.Log;

import com.hardkernel.odroid.settings.R;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.LinkMovementMethod;
import androidx.annotation.Keep;
import android.app.AlertDialog;
import android.text.TextUtils;

import android.provider.Settings;
@Keep
public class MiscFragment extends LeanbackAddBackPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "MiscFragment";
    private static final boolean GAPPS = SystemProperties.getBoolean("ro.opengapps_installed", false);

    private static final String BT_DISABLE_PROP = "persist.disable_bluetooth";
    private static final String BT_SINK_PROP = "persist.service.bt.a2dp.sink";
    private static final String GPS_PROP = "persist.disable_location";
    private static final String WLAN_PS_PROP = "persist.enable_wlan_ps";
    private static final String SHUT_PROP = "persist.pwbtn.shutdown";
    private static final String USB_PERM_DISABLE_PROP = "persist.disable_usb_perms";
    private static final String HDMI_CEC_PROP = "persist.hdmi_cec.enable";

    private Preference pref_gsf_id;
    private TwoStatePreference pref_disable_bt;
    private TwoStatePreference pref_bt_sink;
    private TwoStatePreference pref_gps;
    private TwoStatePreference pref_enable_wlan_ps;
    private TwoStatePreference pref_shut;
    private TwoStatePreference pref_disable_usb_perms;
    private TwoStatePreference pref_enable_hdmi_cec;

    private static final Uri sUri = Uri.parse("content://com.google.android.gsf.gservices");

    private View view_dialog;
    private AlertDialog mAlertDialog = null;

    private Context mContext;

    public static MiscFragment newInstance() {
        return new MiscFragment();
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = getActivity();
        }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
        public void onCreatePreferences(Bundle savedInstanceState, String s) {
            setPreferencesFromResource(R.xml.misc_settings, null);

            pref_gsf_id = (Preference) findPreference(getString(R.string.pref_gsf_id));
            pref_disable_bt = (TwoStatePreference) findPreference(getString(R.string.pref_disable_bluetooth));
            pref_bt_sink = (TwoStatePreference) findPreference(getString(R.string.pref_enable_bluetooth_sink));
            pref_gps = (TwoStatePreference) findPreference(getString(R.string.pref_enable_gps));
            pref_enable_wlan_ps = (TwoStatePreference) findPreference(getString(R.string.pref_enable_wlan_ps));
            pref_shut = (TwoStatePreference) findPreference(getString(R.string.pref_force_shutdown_without_dialog));
            pref_disable_usb_perms = (TwoStatePreference) findPreference(getString(R.string.pref_disable_usb_perms_dialog));
            pref_enable_hdmi_cec = (TwoStatePreference) findPreference(getString(R.string.pref_enable_hdmi_cec));

        }

    @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            pref_disable_bt.setChecked(SystemProperties.getBoolean(BT_DISABLE_PROP, true));
            pref_bt_sink.setChecked(SystemProperties.getBoolean(BT_SINK_PROP, false));
            pref_gps.setChecked(!SystemProperties.getBoolean(GPS_PROP, true));
            pref_enable_wlan_ps.setChecked(SystemProperties.getBoolean(WLAN_PS_PROP, false));
            pref_shut.setChecked(Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.POWER_BUTTON_LONG_PRESS, 1) == 3);
            pref_disable_usb_perms.setChecked(SystemProperties.getBoolean(USB_PERM_DISABLE_PROP, false));
            pref_enable_hdmi_cec.setChecked(SystemProperties.getBoolean(HDMI_CEC_PROP, false));


            pref_gsf_id.setVisible(GAPPS);

            pref_disable_bt.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    SystemProperties.set(BT_DISABLE_PROP, value ? "true" : "false");
                    return true;
                }
            });

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

            pref_enable_wlan_ps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    SystemProperties.set(WLAN_PS_PROP, value ? "true" : "false");
                    Log.v(TAG, "onPreferenceChange: pref_enable_wlan_ps " + newValue);
                    return true;
                }
            });

            pref_shut.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    int xz = Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.POWER_BUTTON_LONG_PRESS, 777);
                    Boolean value = (Boolean) newValue;
//                    SystemProperties.set(SHUT_PROP, value ? "true" : "false");
                    Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.POWER_BUTTON_LONG_PRESS, value ? 3 : 1);
                    Log.v(TAG, "onPreferenceChange: pref_shut " + newValue);
                    return true;
                }
            });

            pref_disable_usb_perms.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    SystemProperties.set(USB_PERM_DISABLE_PROP, value ? "true" : "false");
                    Log.v(TAG, "onPreferenceChange: pref_disable_usb_perms " + newValue);
                    return true;
                }
            });

            pref_enable_hdmi_cec.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean value = (Boolean) newValue;
                    SystemProperties.set(HDMI_CEC_PROP, value ? "true" : "false");
                    Log.v(TAG, "onPreferenceChange: pref_enable_hdmi_cec " + newValue);
                    return true;
                }
            });

        }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        if (preference.equals(pref_gsf_id)) {
            showDialog();
        }
        return super.onPreferenceTreeClick(preference);
    }

    public static String getGSFID(Context context) {
        try {
            Cursor query = context.getContentResolver().query(sUri, null, null, new String[] { "android_id" }, null);
            if (query == null) {
                return "Not found";
            }
            if (!query.moveToFirst() || query.getColumnCount() < 2) {
                query.close();
                return "Not found";
            }
            final String toHexString = Long.toHexString(Long.parseLong(query.getString(1)));
            query.close();
            return toHexString.toUpperCase().trim();
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showDialog () {
        if (mAlertDialog == null) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view_dialog = inflater.inflate(R.layout.dialog_gsfid, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            mAlertDialog = builder.create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        }

//        Activity activity = (Activity)getContext();
        final ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        TextView TVgsfid = view_dialog.findViewById(R.id.textView_gsfid);
        TextView GsfHelp = view_dialog.findViewById(R.id.textView_gsf_url);
        GsfHelp.setMovementMethod(LinkMovementMethod.getInstance());
        long gsfint = Long.parseLong(getGSFID(mContext), 16);
        final String gsfid = Long.toString(gsfint);
        TVgsfid.setText(gsfid);
        ImageView copyText = view_dialog.findViewById(R.id.copy);

        copyText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ClipData clip = ClipData.newPlainText("Copied Text", gsfid);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext,"Text copied to Clipboard",Toast.LENGTH_LONG).show();
            }
        });

        mAlertDialog.show();
        mAlertDialog.getWindow().setContentView(view_dialog);
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(true);
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

}
