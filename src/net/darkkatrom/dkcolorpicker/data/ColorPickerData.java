/*
* Copyright (C) 2017 DarkKat
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

package net.darkkatrom.dkcolorpicker.data;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ColorPickerData implements Parcelable {
    public static final String TAG = "ColorPickerData";

    private String mPreferenceKey;
    private String mPickerSubtitle;
    private String mPickerTitle;
    private int mInitialColor;
    private int mNewColor;
    private int mOldColor;
    private int mResetColor1;
    private int mResetColor2;
    private String mResetColor1Title;
    private String mResetColor2Title;
    private boolean mAlphaSliderVisible;
    private int mHelpScreenVisibility = 0;

    private boolean mHideResetColor1 = true;
    private boolean mShowResetSubMenu = false;

    public ColorPickerData(String preferenceKey, String pickerTitle, String pickerSubtitle,
            int initialColor, int resetColor1, int resetColor2, String resetColor1Title,
            String resetColor2Title, boolean alphaSliderVisible) {
        mPreferenceKey = preferenceKey;
        mPickerTitle = pickerTitle;
        mPickerSubtitle = pickerSubtitle;
        mInitialColor = initialColor;
        mNewColor = initialColor;
        mOldColor = initialColor;
        mResetColor1 = resetColor1;
        mResetColor2 = resetColor2;
        mResetColor1Title = resetColor1Title;
        mResetColor2Title = resetColor2Title;
        mAlphaSliderVisible = alphaSliderVisible;
    }

    public void setUpResetMenuAppearience() {
        if (mResetColor1 == Color.TRANSPARENT) {
            if (mResetColor2 != Color.TRANSPARENT) {
                mResetColor2 = Color.TRANSPARENT;
                Log.w(TAG, "Reset color 1 has not been set, ignore reset color 2 value");
            }
            if (mResetColor1Title != null) {
                mResetColor1Title = null;
                Log.w(TAG, "Reset color 1 has not been set, ignore reset color 1 title");
            }
            if (mResetColor2Title != null) {
                mResetColor2Title = null;
                Log.w(TAG, "Reset color 1 has not been set, ignore reset color 2 title");
            }
        } else if (mResetColor2 == Color.TRANSPARENT) {
            if (mResetColor2Title != null) {
                mResetColor2Title = null;
                Log.w(TAG, "Reset color 2 has not been set, ignore reset color 2 title");
            }
        }

        if (mResetColor1 != Color.TRANSPARENT) {
            mHideResetColor1 = false;
            if (mResetColor2 != Color.TRANSPARENT) {
                mShowResetSubMenu = true;
            }
        }
    }

    public void setNewColor(int color) {
        mNewColor = color;
    }

    public void setOldColor(int color) {
        mOldColor = color;
    }

    public void setHelpScreenVisibility(int visibility) {
        mHelpScreenVisibility = visibility;
    }

    public String getPreferenceKey() {
        return mPreferenceKey;
    }

    public String getPickerTitle() {
        return mPickerTitle;
    }

    public String getPickerSubtitle() {
        return mPickerSubtitle;
    }

    public int getInitialColor() {
        return mInitialColor;
    }

    public int getNewColor() {
        return mNewColor;
    }

    public int getOldColor() {
        return mOldColor;
    }

    public int getResetColor1() {
        return mResetColor1;
    }

    public int getResetColor2() {
        return mResetColor2;
    }

    public String getResetColor1Title() {
        return mResetColor1Title;
    }

    public String getResetColor2Title() {
        return mResetColor2Title;
    }

    public boolean getAlphaSliderVisible() {
        return mAlphaSliderVisible;
    }

    public boolean getHideResetColor1() {
        return mHideResetColor1;
    }

    public boolean getShowResetSubMenu() {
        return mShowResetSubMenu;
    }

    public int getHelpScreenVisibility() {
        return mHelpScreenVisibility;
    }

    protected ColorPickerData(Parcel in) {
        mPreferenceKey = in.readString();
        mPickerTitle = in.readString();
        mPickerSubtitle = in.readString();
        mInitialColor = in.readInt();
        mNewColor = in.readInt();
        mOldColor = in.readInt();
        mResetColor1 = in.readInt();
        mResetColor2 = in.readInt();
        mResetColor1Title = in.readString();
        mResetColor2Title = in.readString();
        mAlphaSliderVisible = in.readInt() != 0;
        mHideResetColor1 = in.readInt() != 0;
        mShowResetSubMenu = in.readInt() != 0;
        mHelpScreenVisibility = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPreferenceKey);
        dest.writeString(mPickerTitle);
        dest.writeString(mPickerSubtitle);
        dest.writeInt(mInitialColor);
        dest.writeInt(mNewColor);
        dest.writeInt(mOldColor);
        dest.writeInt(mResetColor1);
        dest.writeInt(mResetColor2);
        dest.writeString(mResetColor1Title);
        dest.writeString(mResetColor2Title);
        dest.writeInt(mAlphaSliderVisible ? 1 : 0);
        dest.writeInt(mHideResetColor1 ? 1 : 0);
        dest.writeInt(mShowResetSubMenu ? 1 : 0);
        dest.writeInt(mHelpScreenVisibility);
    }

    public static final Parcelable.Creator<ColorPickerData> CREATOR = new Parcelable.Creator<ColorPickerData>() {
        @Override
        public ColorPickerData createFromParcel(Parcel in) {
            return new ColorPickerData(in);
        }

        @Override
        public ColorPickerData[] newArray(int size) {
            return new ColorPickerData[size];
        }
    };
}
