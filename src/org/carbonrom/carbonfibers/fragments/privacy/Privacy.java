/*
 * Copyright (C) 2016 CarbonROM
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

package org.carbonrom.carbonfibers.fragments.privacy;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

public class Privacy extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, DialogInterface.OnClickListener {
    private static final String TAG = "Privacy";
    private SwitchPreference mUntrustedOverlay;
    private boolean mIsTrustedOverlayValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.privacy);

        ContentResolver resolver = getActivity().getContentResolver();

        mUntrustedOverlay = (SwitchPreference) findPreference("untrusted_overlay_toggle");
        mIsTrustedOverlayValue = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.UNTRUSTED_OVERLAY_TOGGLE, 0, UserHandle.USER_CURRENT) == 1;
        mUntrustedOverlay.setOnPreferenceChangeListener(this);
        mUntrustedOverlay.setChecked(mIsTrustedOverlayValue);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CARBONFIBERS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void alertReboot() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.untrusted_overlay_alert_title)
                .setMessage(R.string.untrusted_overlay_alert_message)
                .setPositiveButton(R.string.reboot, this)
                .setNegativeButton(android.R.string.cancel, this)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_NEGATIVE:
                mUntrustedOverlay.setChecked(mIsTrustedOverlayValue);
                dialog.dismiss();
                break;
            case BUTTON_POSITIVE:
                mIsTrustedOverlayValue = Settings.Secure.getIntForUser(getContentResolver(),
                        Settings.Secure.UNTRUSTED_OVERLAY_TOGGLE, 0, UserHandle.USER_CURRENT) == 1;
                Settings.Secure.putIntForUser(getContentResolver(),
                        Settings.Secure.UNTRUSTED_OVERLAY_TOGGLE,
                        mIsTrustedOverlayValue ? 0 : 1, UserHandle.USER_CURRENT);
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                pm.reboot(null);
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference.equals(mUntrustedOverlay))
            alertReboot();
        return true;
    }

}
