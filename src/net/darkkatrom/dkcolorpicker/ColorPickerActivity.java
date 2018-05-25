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

package net.darkkatrom.dkcolorpicker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class ColorPickerActivity extends Activity {
    public static final String KEY_THEME_RES_ID         = "theme_res_id";
    public static final String KEY_IS_WHITEOUT_THEME    = "is_whiteout_theme";
    public static final String KEY_LIGHT_STATUS_BAR     = "light_status_bar";
    public static final String KEY_LIGHT_NAVIGATION_BAR = "light_navigation_bar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Fragment f = new ColorPickerFragment();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int themeResId = extras.getInt(KEY_THEME_RES_ID, 0);
                f.setArguments(extras);
                if (themeResId > 0) {
                    updateTheme(extras);
                }
            }
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, f)
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateTheme(Bundle extras) {
        int themeResId = extras.getInt(KEY_THEME_RES_ID, 0);
        boolean isWhiteoutTheme = extras.getBoolean(KEY_IS_WHITEOUT_THEME, false);
        boolean lightStatusBar = extras.getBoolean(KEY_LIGHT_STATUS_BAR, false);
        boolean lightNavigationBar = extras.getBoolean(KEY_LIGHT_NAVIGATION_BAR,
                false);

        setTheme(themeResId);

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!lightStatusBar) {
            // Possibly we are using the Whiteout theme
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set,
            // and we are not using the Whiteout theme,
            // (Whiteout theme should always use a light status bar).
            if (isLightStatusBar && !isWhiteoutTheme) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (lightNavigationBar) {
            newFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            // Check if light navigation bar flag was set
            boolean isLightNavigationBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            if (isLightNavigationBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        if (oldFlags != newFlags) {
            getWindow().getDecorView().setSystemUiVisibility(newFlags);
        }
    }
}
