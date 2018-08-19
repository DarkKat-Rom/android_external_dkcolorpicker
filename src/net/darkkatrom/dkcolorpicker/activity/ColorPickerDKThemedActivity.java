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

import com.android.internal.util.darkkat.ThemeColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

public class ColorPickerDKThemedActivity extends ColorPickerThemedActivity {

    @Override
    public void updateDayNightTheme(Bundle extras) {
        mDefaultPrimaryColor = getColor(com.android.internal.R.color.primary_color_darkkat);
        mThemeResId = ThemeHelper.getDKThemeResId(this);
        mThemeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
        mLightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
        mLightActionBar = ThemeColorHelper.lightActionBar(this, mDefaultPrimaryColor);
        mLightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
        mStatusBarColor = ThemeColorHelper.getStatusBarBackgroundColor(this, mDefaultPrimaryColor);
        mPrimaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
        mCustomizeColors = ThemeColorHelper.customizeColors(this);
        mIsBlackoutTheme = ThemeHelper.isBlackoutTheme(this);
        mIsWhiteoutTheme = ThemeHelper.isWhiteoutTheme(this);
        mNavigationColor = ThemeColorHelper.getNavigationBarBackgroundColor(this, mDefaultPrimaryColor);
        mColorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);
        setDayNightTheme();
    }
}
