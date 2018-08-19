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

package net.darkkatrom.dkcolorpicker.activity;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.android.internal.util.darkkat.ThemeColorHelper;
import net.darkkatrom.dkcolorpicker.util.ThemeInfo;

public class ColorPickerThemedActivity extends ColorPickerBaseActivity {
    public static final String KEY_THEME_INFO = "theme_info";

    protected ThemeInfo mThemeInfo = null;
    protected boolean mSupportsTheming = false;
    protected int mThemeResId = 0;
    protected boolean mCustomizeColors = false;
    protected int mStatusBarColor = 0;
    protected int mDefaultPrimaryColor = 0;
    protected int mPrimaryColor = 0;
    protected int mNavigationColor = 0;
    protected boolean mColorizeNavigationBar = false;
    protected boolean mLightStatusBar = false;
    protected boolean mLightActionBar = false;
    protected boolean mLightNavigationBar = false;
    protected boolean mIsBlackoutTheme = false;
    protected boolean mIsWhiteoutTheme = false;
    protected int mThemeOverlayAccentResId = 0;

    @Override
    protected void updateDayNightTheme(Bundle extras) {
    }

    protected void setDayNightTheme() {
        if (mThemeResId > 0) {
            setTheme(mThemeResId);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeResId > 0) {
            boolean lightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
            boolean lightActionBar = ThemeColorHelper.lightActionBar(this, mDefaultPrimaryColor);
            boolean lightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
            int primaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
            boolean customizeColors = ThemeColorHelper.customizeColors(this);
            boolean colorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);
            int themeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);

            if (mLightStatusBar != lightStatusBar
                    || mLightActionBar != lightActionBar
                    || mLightNavigationBar != lightNavigationBar
                    || mPrimaryColor != primaryColor
                    || mCustomizeColors != customizeColors
                    || mColorizeNavigationBar != colorizeNavigationBar
                    || mThemeOverlayAccentResId != themeOverlayAccentResId) {
                recreate();
            }
        }
    }
}
