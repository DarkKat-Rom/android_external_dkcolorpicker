/*
 * Copyright (C) 2011 Sergey Margaritov
 * Copyright (C) 2013 Slimroms
 * Copyright (C) 2016 DarkKat
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

package net.darkkatrom.dkcolorpicker.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import net.darkkatrom.dkcolorpicker.activity.ColorPickerThemedActivity;
import net.darkkatrom.dkcolorpicker.R;
import net.darkkatrom.dkcolorpicker.drawable.ColorViewCircleDrawable;
import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkcolorpicker.util.ThemeInfo;
import net.darkkatrom.dkcolorpicker.widget.ColorViewButton;

/**
 * A preference type that allows a user to choose a color
 * 
 * @author Sergey Margaritov
 */
public class ColorPickerPreference extends Preference {
    public static final String TAG = "ColorPickerPreference";

    private static final String ANDROID_NS      = "http://schemas.android.com/apk/res/android";
    private static final String DEFAULT_VALUE   = "defaultValue";

    public static final int RESULT_REQUEST_CODE = 5432;
    public static final String PREFERENCE_KEY   = "preference_key";

    private final Resources mResources;

    private TargetFragment mTargetFragment = null;
    private OwnerActivity mOwnerActivity = null;

    private String mPickerTitle = null;
    private String mPickerSubtitle = null;
    private String mPickerAdditionalSubtitle = null;
    private int mDefaultValue = Color.BLACK;
    private int mValue;
    private int mResetColor1 = Color.TRANSPARENT;
    private int mResetColor2 = Color.TRANSPARENT;
    private String mResetColor1Title = null;
    private String mResetColor2Title = null;
    private boolean mAlphaSliderVisible = false;
    private boolean mShowHelpScreen;

    private int mThemeType = ThemeInfo.DEFAULT_THEME;

    public interface TargetFragment {
        public void pickColor(Bundle extras, int requestCode, int theme);
    }

    public interface OwnerActivity {
        public ThemeInfo getThemeInfo();
    }

    public ColorPickerPreference(Context context) {
        this(context, null);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mResources = context.getResources();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.ColorPickerPreference, defStyleAttr, defStyleRes);
            mPickerTitle = a.getString(R.styleable.ColorPickerPreference_pickerTitle);
            mPickerSubtitle = a.getString(R.styleable.ColorPickerPreference_pickerSubtitle);
            mPickerAdditionalSubtitle =
                    a.getString(R.styleable.ColorPickerPreference_pickerAdditionalSubtitle);
            mDefaultValue = a.getColor(R.styleable.ColorPickerPreference_defaultColor,
                    Color.TRANSPARENT);
            mResetColor1 = a.getColor(R.styleable.ColorPickerPreference_resetColor1,
                    Color.TRANSPARENT);
            mResetColor2 = a.getColor(R.styleable.ColorPickerPreference_resetColor2,
                    Color.TRANSPARENT);
            mResetColor1Title = a.getString(R.styleable.ColorPickerPreference_resetColor1Title);
            mResetColor2Title = a.getString(R.styleable.ColorPickerPreference_resetColor2Title);
            mAlphaSliderVisible = a.getBoolean(
                    R.styleable.ColorPickerPreference_alphaSliderVisible, false);
            a.recycle();

            if (mDefaultValue == Color.TRANSPARENT) {
                String defaultValue = attrs.getAttributeValue(ANDROID_NS, DEFAULT_VALUE);
                if (defaultValue != null) {
                    if (defaultValue.startsWith("#")) {
                        try {
                            mDefaultValue = convertToColorInt(defaultValue);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Wrong color: " + defaultValue);
                        }
                    } else {
                        int resourceId = attrs.getAttributeResourceValue(ANDROID_NS, DEFAULT_VALUE,
                                Color.TRANSPARENT);
                        if (resourceId != 0) {
                            mDefaultValue = mResources.getInteger(resourceId);
                        }
                    }
                }
            }
            if (mDefaultValue == Color.TRANSPARENT) {
                mDefaultValue = Color.BLACK;
            }

            mValue = mDefaultValue;
        }
        setLayoutResource(R.layout.preference_color_picker);
        setWidgetLayoutResource(R.layout.preference_widget_color_picker);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setNewColorInternal(restoreValue ? getValue() : (Integer) defaultValue);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        ColorViewButton preview = null;
        if (view != null) {
            preview = (ColorViewButton) view.findViewById(R.id.color_picker_widget);
        }
        if (preview != null) {
            TypedValue tv = new TypedValue();
            int borderColor;
            getContext().getTheme().resolveAttribute(android.R.attr.colorControlHighlight, tv, true);
            if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                borderColor = tv.data;
            } else {
                borderColor = getContext().getColor(tv.resourceId);
            }
            preview.setColor(mValue);
            preview.setBorderColor(borderColor);
        }
    }

    @Override
    public Bundle getExtras() {
        Bundle extras = new Bundle();
        extras.putString(PREFERENCE_KEY, getKey());
        extras.putCharSequence(ColorPickerFragment.KEY_TITLE, mPickerTitle);
        extras.putCharSequence(ColorPickerFragment.KEY_SUBTITLE, mPickerSubtitle);
        extras.putCharSequence(ColorPickerFragment.KEY_ADDITIONAL_SUBTITLE, mPickerAdditionalSubtitle);
        extras.putInt(ColorPickerFragment.KEY_INITIAL_COLOR, mValue);
        extras.putInt(ColorPickerFragment.KEY_NEW_COLOR, mValue);
        extras.putInt(ColorPickerFragment.KEY_OLD_COLOR, mValue);
        extras.putInt(ColorPickerFragment.KEY_RESET_COLOR_1, mResetColor1);
        extras.putInt(ColorPickerFragment.KEY_RESET_COLOR_2, mResetColor2);
        extras.putCharSequence(ColorPickerFragment.KEY_RESET_COLOR_1_TITLE, mResetColor1Title);
        extras.putCharSequence(ColorPickerFragment.KEY_RESET_COLOR_2_TITLE, mResetColor2Title);
        extras.putBoolean(ColorPickerFragment.KEY_ALPHA_SLIDER_VISIBLE, mAlphaSliderVisible);
        setThemeExtras(extras);
        return extras;
    }

    @Override
    protected void onClick() {
        if (mTargetFragment instanceof TargetFragment) {
            Bundle extras = getExtras();
            mTargetFragment.pickColor(extras, RESULT_REQUEST_CODE, mThemeType);
        }
    }

    public void setThemeExtras(Bundle extras) {
        ThemeInfo themeInfo = null;
        if (getContext() instanceof OwnerActivity) {
            OwnerActivity owner = (OwnerActivity) getContext();
            themeInfo = owner.getThemeInfo();
            if (themeInfo == null) {
                themeInfo = new ThemeInfo(ThemeInfo.DARKKAT_DAY_NIGHT_THEME);
            }
        }
        if (themeInfo == null) {
            themeInfo = new ThemeInfo();
        }
        mThemeType = themeInfo.getThemeType();
        extras.putParcelable(ColorPickerThemedActivity.KEY_THEME_INFO, themeInfo);
    }

    public void setTargetFragment(TargetFragment fragment) {
        mTargetFragment = fragment;
    }

    private int getValue() {
        try {
            if (isPersistent()) {
                mValue = getPersistedInt(mDefaultValue);
            }
        } catch (ClassCastException e) {
            mValue = mDefaultValue;
        }

        return mValue;
    }

    private void setNewColorInternal(int color) {
        if (isPersistent()) {
            persistInt(color);
        }
        mValue = color;
        notifyChanged();

    }

    public void setNewColor(int color) {
        setNewColorInternal(color);
        try {
            getOnPreferenceChangeListener().onPreferenceChange(this, color);
        } catch (NullPointerException e) {
        }
    }

    public void setPickerTitle(int titleResId) {
        mPickerTitle = mResources.getString(titleResId);
    }

    public void setPickerTitle(String title) {
        mPickerTitle = title;
    }


    public void setPickerSubtitle(int titleResId) {
        mPickerSubtitle = mResources.getString(titleResId);
    }

    public void setPickerSubtitle(String title) {
        mPickerSubtitle = title;
    }

    public void setPickerAdditionalSubtitle(int titleResId) {
        mPickerAdditionalSubtitle = mResources.getString(titleResId);
    }

    public void setPickerAdditionalSubtitle(String title) {
        mPickerAdditionalSubtitle = title;
    }

    public void setResetColors(int resetColor1, int resetColor2) {
        mResetColor1 = resetColor1;
        mResetColor2 = resetColor2;
    }

    public void setResetColor(int color) {
        mResetColor1 = color;
    }

    public void setResetColorsTitle(int title1ResId, int title2ResId) {
        mResetColor1Title = mResources.getString(title1ResId);
        mResetColor2Title = mResources.getString(title2ResId);
    }

    public void setResetColorsTitle(String title1, String title2) {
        mResetColor1Title = title1;
        mResetColor2Title = title2;
    }

    public void setResetColorTitle(int titleResId) {
        mResetColor1Title = mResources.getString(titleResId);
    }

    public void setResetColorTitle(String title) {
        mResetColor1Title = title;
    }

    /**
     * Toggle Alpha Slider visibility (by default it's disabled)
     * 
     * @param enable
     */
    public void setAlphaSliderVisible(boolean visible) {
        mAlphaSliderVisible = visible;
    }

    /**
     * For custom purposes. Not used by ColorPickerPreferrence
     * 
     * @param color
     * @author Unknown
     */
    public static String convertToARGB(int color) {
        return ColorPickerHelper.convertToARGB(color);
    }

    /**
     * Converts a aarrggbb- or rrggbb color string to a color int
     * 
     * @param argb
     * @throws NumberFormatException
     * @author Unknown
     */
    public static int convertToColorInt(String argb) {
        return ColorPickerHelper.convertToColorInt(argb);
    }
}
