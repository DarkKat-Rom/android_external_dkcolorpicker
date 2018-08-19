/*
 * Copyright (C) 2018 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.darkkatrom.dkcolorpicker.util;

import android.os.Parcel;
import android.os.Parcelable;

public class ThemeInfo implements Parcelable {

    // No theming, the default app or activity theme will be used
    public static final int DEFAULT_THEME            = 0;
    // The global DarkKat Day/Night theme will be used
    public static final int DARKKAT_DAY_NIGHT_THEME  = 1;
    // The global Material Day/Night theme will be used
    public static final int MATERIAL_DAY_NIGHT_THEME = 2;
    // The app internal Day/Night theme will be used
    public static final int APP_DAY_NIGHT_THEME      = 3;
    
    private int mThemeType = 0;
    private int mThemeResId = 0;
    private int mThemeOverlayAccentResId = 0;
    private int mThemeLightActionBarResId = 0;
    private int mThemeLightStatusBarResId = 0;
    private int mThemeLightNavigationBarResId = 0;
    private int mThemeLightActionBarLightNavigationBarResId = 0;
    private int mThemeLightStatusBarLightNavigationBarResId = 0;

    public ThemeInfo() {
    }

    public ThemeInfo(int themeType) {
        mThemeType = themeType;
    }

    public ThemeInfo(int themeType, int themeResId, int themeOverlayAccentResId,
            int themeLightActionBarResId, int themeLightStatusBarResId,
            int themeLightNavigationBarResId, int themeLightActionBarLightNavigationBarResId,
            int themeLightStatusBarLightNavigationBarResId) {
        mThemeType = themeType;
        mThemeResId = themeResId;
        mThemeOverlayAccentResId = themeOverlayAccentResId;
        mThemeLightActionBarResId = themeLightActionBarResId;
        mThemeLightStatusBarResId = themeLightStatusBarResId;
        mThemeLightNavigationBarResId = themeLightNavigationBarResId;
        mThemeLightActionBarLightNavigationBarResId = themeLightActionBarLightNavigationBarResId;
        mThemeLightStatusBarLightNavigationBarResId = themeLightStatusBarLightNavigationBarResId;
    }

    public void setThemeType(int themeType) {
        mThemeType = themeType;
    }

    public void setThemeResId(int resId) {
        mThemeResId = resId;
    }

    public void setThemeOverlayAccentResId(int resId) {
        mThemeOverlayAccentResId = resId;
    }

    public void setThemeLightActionBarResId(int resId) {
        mThemeLightActionBarResId = resId;
    }

    public void setThemeLightStatusBarResId(int resId) {
        mThemeLightStatusBarResId = resId;
    }

    public void setThemeLightNavigationBarResId(int resId) {
        mThemeLightNavigationBarResId = resId;
    }

    public void setThemeLightActionBarLightNavigationBarResId(int resId) {
        mThemeLightActionBarLightNavigationBarResId = resId;
    }

    public void setThemeLightStatusBarLightNavigationBarResId(int resId) {
        mThemeLightStatusBarLightNavigationBarResId = resId;
    }

    public int getThemeType() {
        return mThemeType;
    }

    public int getThemeResId() {
        return mThemeResId;
    }

    public int getThemeOverlayAccentResId() {
        return mThemeOverlayAccentResId;
    }

    public int getThemeLightActionBarResId() {
        return mThemeLightActionBarResId;
    }

    public int getThemeLightStatusBarResId() {
        return mThemeLightStatusBarResId;
    }

    public int getThemeLightNavigationBarResId() {
        return mThemeLightNavigationBarResId;
    }

    public int getThemeLightActionBarLightNavigationBarResId() {
        return mThemeLightActionBarLightNavigationBarResId;
    }

    public int getThemeLightStatusBarLightNavigationBarResId() {
        return mThemeLightStatusBarLightNavigationBarResId;
    }

    public ThemeInfo(Parcel in) {
        mThemeType = in.readInt();
        mThemeResId = in.readInt();
        mThemeOverlayAccentResId = in.readInt();
        mThemeLightActionBarResId = in.readInt();
        mThemeLightStatusBarResId = in.readInt();
        mThemeLightNavigationBarResId = in.readInt();
        mThemeLightActionBarLightNavigationBarResId = in.readInt();
        mThemeLightStatusBarLightNavigationBarResId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mThemeType);
        dest.writeInt(mThemeResId);
        dest.writeInt(mThemeOverlayAccentResId);
        dest.writeInt(mThemeLightActionBarResId);
        dest.writeInt(mThemeLightStatusBarResId);
        dest.writeInt(mThemeLightNavigationBarResId);
        dest.writeInt(mThemeLightActionBarLightNavigationBarResId);
        dest.writeInt(mThemeLightStatusBarLightNavigationBarResId);
    }

    public static final Parcelable.Creator<ThemeInfo> CREATOR = 
            new Parcelable.Creator<ThemeInfo>(){
        @Override
        public ThemeInfo createFromParcel(Parcel source) {
            return new ThemeInfo(source);
        }
        @Override
        public ThemeInfo[] newArray(int size) {
            return new ThemeInfo[size];
        }
    };
}
