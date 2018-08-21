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
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.android.internal.util.darkkat.ThemeColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;
import net.darkkatrom.dkcolorpicker.util.ThemeInfo;

public class ColorPickerThemedActivity extends ColorPickerActivity {
    public static final String KEY_THEME_INFO = "theme_info";

    private ThemeInfo mThemeInfo = null;
    private int mThemeType = ThemeInfo.DEFAULT_THEME;
    private int mDefaultPrimaryColor = 0;
    private int mThemeResId = 0;
    private int mThemeOverlayAccentResId = 0;
    private boolean mLightStatusBar = false;
    private boolean mLightActionBar = false;
    private boolean mLightNavigationBar = false;
    private int mStatusBarColor = 0;
    private int mPrimaryColor = 0;
    private int mNavigationColor = 0;
    private boolean mCustomizeColors = false;
    private boolean mIsBlackoutTheme = false;
    private boolean mIsWhiteoutTheme = false;
    private boolean mColorizeNavigationBar = false;

    @Override
    protected void updateDayNightTheme(Bundle extras) {
        if (extras != null) {
            mThemeInfo = (ThemeInfo) extras.getParcelable(KEY_THEME_INFO);
            mThemeType = mThemeInfo.getThemeType();

            if (mThemeType != ThemeInfo.DEFAULT_THEME) {
                if (mThemeType == ThemeInfo.DARKKAT_DAY_NIGHT_THEME) {
                    mDefaultPrimaryColor = getColor(com.android.internal.R.color.primary_color_darkkat);
                    mThemeResId = ThemeHelper.getDKThemeResId(this);
                } else {
                    mDefaultPrimaryColor = getDefaultPrimaryColor();
                    mThemeResId = getDayNightThemeResId();
                }

                mThemeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
                mLightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
                mLightActionBar = ThemeColorHelper.lightActionBar(this, mDefaultPrimaryColor);
                mLightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
                mStatusBarColor = ThemeColorHelper.getStatusBarBackgroundColor(this, mDefaultPrimaryColor);
                mPrimaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
                mNavigationColor = ThemeColorHelper.getNavigationBarBackgroundColor(this, mDefaultPrimaryColor);
                mCustomizeColors = ThemeColorHelper.customizeColors(this);
                mIsBlackoutTheme = ThemeHelper.isBlackoutTheme(this);
                mIsWhiteoutTheme = ThemeHelper.isWhiteoutTheme(this);
                mColorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);

                setDayNightTheme();
            }
        }
    }

    private int getDefaultPrimaryColor() {
        int color = 0;
        TypedValue tv = new TypedValue();
        ContextThemeWrapper themedContext = new ContextThemeWrapper(this, mThemeInfo.getThemeResId());
        themedContext.getTheme().resolveAttribute(android.R.attr.colorPrimary, tv, true);
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            color = tv.data;
        } else {
            color = themedContext.getColor(tv.resourceId);
        }
        return color;
    }

    private int getDayNightThemeResId() {
        int resId = 0;
        if (mThemeType == ThemeInfo.MATERIAL_DAY_NIGHT_THEME) {
            if (mLightActionBar && mLightNavigationBar) {
                resId = mLightStatusBar
                        ? android.R.style.Theme_Material_DayNight_LightStatusBar_LightNavigationBar
                        : android.R.style.Theme_Material_DayNight_LightActionBar_LightNavigationBar;
            } else if (mLightActionBar) {
                resId = mLightStatusBar
                        ? android.R.style.Theme_Material_DayNight_LightStatusBar
                        : android.R.style.Theme_Material_DayNight_LightActionBar;
            } else if (mLightNavigationBar) {
                resId = android.R.style.Theme_Material_DayNight_LightNavigationBar;
            } else {
                resId = mThemeInfo.getThemeResId();
            }
        } else {
            if (mLightActionBar && mLightNavigationBar) {
                resId = mLightStatusBar
                        ? mThemeInfo.getThemeLightStatusBarLightNavigationBarResId()
                        : mThemeInfo.getThemeLightActionBarLightNavigationBarResId();
            } else if (mLightActionBar) {
                resId = mLightStatusBar
                        ? mThemeInfo.getThemeLightStatusBarResId()
                        : mThemeInfo.getThemeLightActionBarResId();
            } else if (mLightNavigationBar) {
                resId = mThemeInfo.getThemeLightNavigationBarResId();
            } else {
                resId = mThemeInfo.getThemeResId();
            }
        }
        return resId;
    }

    private void setDayNightTheme() {
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
            int themeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
            boolean lightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
            boolean lightActionBar = ThemeColorHelper.lightActionBar(this, mDefaultPrimaryColor);
            boolean lightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
            int primaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
            boolean customizeColors = ThemeColorHelper.customizeColors(this);
            boolean colorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);

            if (mThemeOverlayAccentResId != themeOverlayAccentResId
                    || mLightStatusBar != lightStatusBar
                    || mLightActionBar != lightActionBar
                    || mLightNavigationBar != lightNavigationBar
                    || mPrimaryColor != primaryColor
                    || mCustomizeColors != customizeColors
                    || mColorizeNavigationBar != colorizeNavigationBar) {
                recreate();
            }
        }
    }
}
