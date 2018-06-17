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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.android.internal.util.darkkat.ThemeColorHelper;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class ColorPickerActivity extends Activity {
    public static final String KEY_THEME_RES_ID                = "theme_res_id";
    public static final String KEY_CUSTOMIZE_COLORS            = "customize_colors";
    public static final String KEY_STATUS_BAR_COLOR            = "status_bar_color";
    public static final String KEY_PRIMARY_COLOR               = "primary_color";
    public static final String KEY_NAVIGATION_BAR_COLOR        = "navigation_bar_color";
    public static final String KEY_COLORIZE_NAVIGATION_BAR     = "colorize_navigation_bar";
    public static final String KEY_LIGHT_STATUS_BAR            = "light_status_bar";
    public static final String KEY_LIGHT_ACTION_BAR            = "light_action_bar";
    public static final String KEY_LIGHT_NAVIGATION_BAR        = "light_navigation_bar";
    public static final String KEY_IS_WHITEOUT_THEME           = "is_whiteout_theme";
    public static final String KEY_IS_BLACKOUT_THEME           = "is_blackout_theme";
    public static final String KEY_THEME_OVERLAY_ACCENT_RES_ID = "theme_res_id";

    private int mThemeResId = 0;
    private boolean mCustomizeColors = false;
    private int mPrimaryColor = 0;
    private boolean mColorizeNavigationBar = false;
    private boolean mLightStatusBar = false;
    private boolean mLightActionBar = false;
    private boolean mLightNavigationBar = false;
    private int mThemeOverlayAccentResId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Fragment f = new ColorPickerFragment();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mThemeResId = extras.getInt(KEY_THEME_RES_ID, 0);
                f.setArguments(extras);
                if (mThemeResId > 0) {
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
        mCustomizeColors = extras.getBoolean(KEY_CUSTOMIZE_COLORS, false);
        int statusBarColor = extras.getInt(KEY_STATUS_BAR_COLOR, 0);
        mPrimaryColor = extras.getInt(KEY_PRIMARY_COLOR, 0);
        int navigationColor = extras.getInt(KEY_NAVIGATION_BAR_COLOR, 0);
        mColorizeNavigationBar = extras.getBoolean(KEY_COLORIZE_NAVIGATION_BAR, false);
        mLightStatusBar = extras.getBoolean(KEY_LIGHT_STATUS_BAR, false);
        mLightActionBar = extras.getBoolean(KEY_LIGHT_ACTION_BAR, false);
        mLightNavigationBar = extras.getBoolean(KEY_LIGHT_NAVIGATION_BAR, false);
        boolean isWhiteoutTheme = extras.getBoolean(KEY_IS_WHITEOUT_THEME, false);
        boolean isBlackoutTheme = extras.getBoolean(KEY_IS_BLACKOUT_THEME, false);

        setTheme(mThemeResId);

        mThemeOverlayAccentResId = extras.getInt(KEY_THEME_OVERLAY_ACCENT_RES_ID, 0);
        if (mThemeOverlayAccentResId > 0) {
            getTheme().applyStyle(mThemeOverlayAccentResId, true);
        }

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!mLightStatusBar) {
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set.
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!mLightNavigationBar) {
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

        if (mCustomizeColors && !isBlackoutTheme && !isWhiteoutTheme) {
            getWindow().setStatusBarColor(statusBarColor);
            getActionBar().setBackgroundDrawable(new ColorDrawable(mPrimaryColor));
        }
        if (navigationColor != 0) {
            getWindow().setNavigationBarColor(navigationColor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeResId > 0) {
            boolean customizeColors = ThemeColorHelper.customizeColors(this);
            int primaryColor = ThemeColorHelper.getPrimaryColor(this, 0xff2196f3);
            boolean colorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);
            boolean lightStatusBar = ThemeColorHelper.lightStatusBar(this, 0xff2196f3);
            boolean lightActionBar = ThemeColorHelper.lightActionBar(this, 0xff2196f3);
            boolean lightNavigationBar = ThemeColorHelper.lightNavigationBar(this, 0xff2196f3);
            int themeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);

            if (mThemeOverlayAccentResId != themeOverlayAccentResId
                    || mCustomizeColors != customizeColors
                    || mPrimaryColor != primaryColor
                    || mColorizeNavigationBar != colorizeNavigationBar
                    || mLightStatusBar != lightStatusBar
                    || mLightActionBar != lightActionBar
                    || mLightNavigationBar != lightNavigationBar) {
                recreate();
            }
        }
    }
}
