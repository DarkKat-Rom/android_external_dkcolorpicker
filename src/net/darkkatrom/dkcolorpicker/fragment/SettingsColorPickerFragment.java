/*
 * Copyright (C) 2018 DarkKat
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

package net.darkkatrom.dkcolorpicker.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceCategory;

import net.darkkatrom.dkcolorpicker.activity.ColorPickerActivity;
import net.darkkatrom.dkcolorpicker.activity.ColorPickerThemedActivity;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dkcolorpicker.util.ThemeInfo;

public class SettingsColorPickerFragment extends PreferenceFragment implements
        ColorPickerPreference.TargetFragment {

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);

        int prefsCount = getPreferenceScreen().getPreferenceCount();
        for(int i = 0; i < prefsCount; i++) {
            Preference p = getPreferenceScreen().getPreference(i);
            if (p instanceof PreferenceCategory) {
                int prefsGroupCount = ((PreferenceCategory) p).getPreferenceCount();
                for(int j = 0; j < prefsGroupCount; j++) {
                    Preference gp = ((PreferenceCategory) p).getPreference(j);
                    if (gp instanceof ColorPickerPreference) {
                        ((ColorPickerPreference) gp).setTargetFragment(this);
                    }
                }
            } else if (p instanceof ColorPickerPreference) {
                ((ColorPickerPreference) p).setTargetFragment(this);
            }
        }
    }

    @Override
    public void pickColor(Bundle extras, int requestCode, int themeType) {
        Intent intent = new Intent(getActivity(), themeType == ThemeInfo.DEFAULT_THEME
                ? ColorPickerActivity.class : ColorPickerThemedActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ColorPickerPreference.RESULT_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            String extraNewColor = ColorPickerFragment.KEY_NEW_COLOR;
            if (extras != null && extras.getInt(extraNewColor) != 0) {
                String extraPrefKey = ColorPickerPreference.PREFERENCE_KEY;
                ((ColorPickerPreference) findPreference(extras.getString(extraPrefKey)))
                        .setNewColor(extras.getInt(extraNewColor));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getActionBar() != null && getSubtitleResId() > 0) {
            getActivity().getActionBar().setSubtitle(getSubtitleResId());
        }
    }

    protected int getSubtitleResId() {
        return 0;
    }

    protected void removeSubtitle() {
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setSubtitle(null);
        }
    }

    protected void removePreference(String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            getPreferenceScreen().removePreference(pref);
        }
    }

    protected ContentResolver getContentResolver() {
        return getActivity().getContentResolver();
    }

    protected Object getSystemService(final String name) {
        return getActivity().getSystemService(name);
    }

    protected PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }
}
