package com.hardkernel.odroid.settings.rotation;

import android.content.Context;
import android.os.Bundle;

import android.provider.MediaStore;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.leanback.preference.LeanbackPreferenceFragment;
import android.util.Log;
import android.view.Display;

import com.hardkernel.odroid.settings.R;
import com.hardkernel.odroid.settings.LeanbackAddBackPreferenceFragment;

import com.hardkernel.odroid.settings.RadioPreference;

public class RotationFragment extends LeanbackAddBackPreferenceFragment {
    private static final String TAG = "RotationFragment";

    public static RotationFragment newInstance() { return new RotationFragment(); }

    private static boolean isPortrait = false;
    private static int degree;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        degree = display.getRotation();
        if (degree != 0)
            isPortrait = true;
        updatePreferenceFragment();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof RadioPreference) {
            final RadioPreference radioPreference = (RadioPreference)preference;
            radioPreference.clearOtherRadioPreferences(getPreferenceScreen());

            String getDegree = radioPreference.getKey();

            Log.e(TAG, "set rotation : " + getDegree);

            switch (Integer.valueOf(getDegree)) {
                case 0:
                    isPortrait = false;
                    degree = 0;
                    break;
                case 90:
                    isPortrait = true;
                    degree = 1;
                    break;
                case 270:
                    isPortrait = true;
                    degree = 3;
                    break;
            }
            final Context context = getPreferenceManager().getContext();

            android.provider.Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            android.provider.Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, degree);
        } else {
            isPortrait = true;
        }

        updatePreferenceFragment();
        return super.onPreferenceTreeClick(preference);
    }

    private void updatePreferenceFragment() {
        final Context themedContext = getPreferenceManager().getContext();
        final PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(themedContext);

        screen.setTitle(R.string.rotation);
        setPreferenceScreen(screen);

        RadioPreference landscape;
        Preference portrait;
        RadioPreference portrait_90 = null;
        RadioPreference portrait_270 = null;

        landscape = new RadioPreference(themedContext);
        landscape.setKey("0");
        landscape.setPersistent(false);
        landscape.setTitle(R.string.landscape);
        landscape.setLayoutResource(R.layout.preference_reversed_widget);

        portrait = new Preference(themedContext);
        portrait.setKey("portrait");
        portrait.setPersistent(false);
        portrait.setTitle(R.string.portrait);
        portrait.setLayoutResource(R.layout.preference_reversed_widget);

        if (isPortrait) {
            portrait_90 = new RadioPreference(themedContext);
            portrait_90.setKey("90");
            portrait_90.setPersistent(false);
            portrait_90.setTitle(R.string.portrait_90);
            portrait_90.setLayoutResource(R.layout.preference_reversed_widget);

            portrait_270 = new RadioPreference(themedContext);
            portrait_270.setKey("270");
            portrait_270.setPersistent(false);
            portrait_270.setTitle(R.string.portrait_270);
            portrait_270.setLayoutResource(R.layout.preference_reversed_widget);
        }
        switch (degree) {
            case 0:
                landscape.setChecked(true);
                break;
            case 1:
                portrait_90.setChecked(true);
                break;
            case 3:
                portrait_270.setChecked(true);
                break;
        }

        screen.addPreference(landscape);
        screen.addPreference(portrait);
        if (isPortrait) {
            screen.addPreference(portrait_90);
            screen.addPreference(portrait_270);
        }
    }
}