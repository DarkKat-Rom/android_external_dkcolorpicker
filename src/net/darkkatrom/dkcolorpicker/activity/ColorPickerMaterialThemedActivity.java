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

import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import com.android.internal.util.darkkat.ThemeColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dkcolorpicker.util.ThemeInfo;

public class ColorPickerMaterialThemedActivity extends ColorPickerThemedActivity {

    @Override
    public void updateDayNightTheme(Bundle extras) {
        if (extras == null) {
            return;
        } else {
            mThemeInfo = (ThemeInfo) extras.getParcelable(KEY_THEME_INFO);
        }

        mDefaultPrimaryColor = getDefaultPrimaryColor();
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
        mThemeResId = getDayNightThemeResId();
        mThemeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
        setDayNightTheme();
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
        return resId;
    }
}
