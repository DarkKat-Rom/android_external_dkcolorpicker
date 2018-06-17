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
import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;

public class ColorPickerActivity extends Activity {
    public static final String KEY_SUPPORTS_THEMING         = "supports_theming";
    public static final String KEY_THEME_RES_ID             = "theme_res_id";
    public static final String KEY_THEME_LIGHT_AB_RES_ID    = "theme_light_ab_res_id";
    public static final String KEY_THEME_LIGHT_SB_RES_ID    = "theme_light_sb_res_id";
    public static final String KEY_THEME_LIGHT_NB_RES_ID    = "theme_light_nb_res_id";
    public static final String KEY_THEME_LIGHT_AB_NB_RES_ID = "theme_light_ab_nb_res_id";
    public static final String KEY_THEME_LIGHT_SB_NB_RES_ID = "theme_light_sb_nb_res_id";

    private boolean mSupportsTheming = false;
    private int mThemeResId = 0;
    private boolean mCustomizeColors = false;
    private int mStatusBarColor = 0;
    private int mDefaultPrimaryColor = 0;
    private int mPrimaryColor = 0;
    private int mNavigationColor = 0;
    private boolean mColorizeNavigationBar = false;
    private boolean mLightStatusBar = false;
    private boolean mLightActionBar = false;
    private boolean mLightNavigationBar = false;
    private boolean mIsBlackoutTheme = false;
    private boolean mIsWhiteoutTheme = false;
    private int mThemeOverlayAccentResId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSupportsTheming = extras.getBoolean(KEY_SUPPORTS_THEMING, false);
            if (mSupportsTheming) {
                updateTheme(extras);
            }
        }

        if (savedInstanceState == null) {
            Fragment f = new ColorPickerFragment();
            if (extras != null) {
                f.setArguments(extras);
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
        mCustomizeColors = ThemeColorHelper.customizeColors(this);
        mDefaultPrimaryColor = 0xff2196f3;
        mStatusBarColor = ThemeColorHelper.getStatusBarBackgroundColor(this, mDefaultPrimaryColor);
        mPrimaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
        mNavigationColor = ThemeColorHelper.getNavigationBarBackgroundColor(this, mDefaultPrimaryColor);
        mColorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);
        mLightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
        mLightActionBar = ThemeColorHelper.lightActionBar(this, mDefaultPrimaryColor);
        mLightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
        mIsBlackoutTheme = ThemeHelper.isBlackoutTheme(this);
        mIsWhiteoutTheme = ThemeHelper.isWhiteoutTheme(this);

        if (mLightActionBar && mLightNavigationBar) {
            mThemeResId = mLightStatusBar
                    ? extras.getInt(KEY_THEME_LIGHT_SB_NB_RES_ID, 0)
                    : extras.getInt(KEY_THEME_LIGHT_AB_NB_RES_ID, 0);
        } else if (mLightActionBar) {
            mThemeResId = mLightStatusBar
                    ? extras.getInt(KEY_THEME_LIGHT_SB_RES_ID, 0)
                    : extras.getInt(KEY_THEME_LIGHT_AB_RES_ID, 0);
        } else if (mLightNavigationBar) {
            mThemeResId = extras.getInt(KEY_THEME_LIGHT_NB_RES_ID, 0);
        } else {
            mThemeResId = extras.getInt(KEY_THEME_RES_ID, 0);
        }
        if (mThemeResId > 0) {
            setTheme(mThemeResId);
        }

        mThemeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
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

        if (mCustomizeColors && !mIsBlackoutTheme && !mIsWhiteoutTheme) {
            getWindow().setStatusBarColor(mStatusBarColor);
            getActionBar().setBackgroundDrawable(new ColorDrawable(mPrimaryColor));
        }
        if (mNavigationColor != 0) {
            getWindow().setNavigationBarColor(mNavigationColor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSupportsTheming) {
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
