/*
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

package net.darkkatrom.dkcolorpicker.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import net.darkkatrom.dkcolorpicker.R;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dkcolorpicker.widget.ApplyColorView;
import net.darkkatrom.dkcolorpicker.widget.ColorPickerView;
import net.darkkatrom.dkcolorpicker.widget.ColorViewButton;
import net.darkkatrom.dkcolorpicker.util.ColorPickerHelper;

public class ColorPickerFragment extends Fragment implements
        ColorPickerView.OnColorChangedListener, TextWatcher, View.OnClickListener,
                View.OnLongClickListener, View.OnFocusChangeListener {

    public static final String TAG = "ColorPickerFragment";

    public static final String KEY_TITLE                    = "picker_title";
    public static final String KEY_SUBTITLE                 = "picker_subtitle";
    public static final String KEY_INITIAL_COLOR            = "initial_color";
    public static final String KEY_NEW_COLOR                = "new_color";
    public static final String KEY_OLD_COLOR                = "old_color";
    public static final String KEY_RESET_COLOR_1            = "reset_color_1";
    public static final String KEY_RESET_COLOR_2            = "reset_color_2";
    public static final String KEY_RESET_COLOR_1_TITLE      = "reset_color_1_Title";
    public static final String KEY_RESET_COLOR_2_TITLE      = "reset_color_2_Title";
    public static final String KEY_ALPHA_SLIDER_VISIBLE     = "alpha_slider_visible";
    public static final String KEY_SHOW_HELP_SCREEN         = "show_help_screen";
    public static final String KEY_SHOW_FAVORITES           = "show_favorites";
    public static final String KEY_HELP_SCREEN_VISIBILITY   = "help_screen_Visibility";
    public static final String KEY_ID_FAVORITE_COLOR_BUTTON = "favorite_color_button_";

    private static final int HELP_SCREEN_VISIBILITY_DEFAULT = 0;
    private static final int HELP_SCREEN_VISIBILITY_VISIBLE = 1;
    private static final int HELP_SCREEN_VISIBILITY_GONE    = 2;

    private static final int PALETTE_MATERIAL = 1;
    private static final int PALETTE_RGB      = 2;

    private static final int ANIMATE_TO_SHOW = 0;
    private static final int ANIMATE_TO_HIDE = 1;
    private static final int NO_ANIMATION    = 2;

    private static final int ANIMATE_COLOR_TRANSITION       = 0;
    private static final int ANIMATE_FAVORITES_VISIBILITY   = 1;
    private static final int ANIMATE_HELP_SCREEN_VISIBILITY = 2;

    private SharedPreferences mPrefs;
    private Resources mResources;

    private ApplyColorView mApplyColorAction;
    private MenuItem mShowEditHexAction;
    private EditText mEditHexValue;

    private View mColorPickerView;
    private ColorPickerView mColorPicker;
    private LinearLayout mColorButtonsLayout;
    private LinearLayout mFavoritesLayout;

    private View mHelpScreen;
    private CheckedTextView mCheckShowHelpScreen;
    private Button mCloseHelpScreen;

    private CharSequence mSubtitle;
    private CharSequence mTitle;
    private String mPreferenceKey;
    private int mInitialColor;
    private int mNewColorValue;
    private int mOldColorValue;
    private int mResetColor1;
    private int mResetColor2;
    private CharSequence mResetColor1Title;
    private CharSequence mResetColor2Title;
    private int mBorderColor;
    private boolean mShowFavorites;
    private boolean mShowHelpScreen;
    private boolean mHelpScreenVisible;

    private boolean mHideResetColor1 = true;
    private boolean mHideResetColor2 = true;
    private boolean mShowSubMenu = false;

	private float mFullTranslationX;
    private int mFavoritesLayoutHeight = 0;
    private int mHelpScreenHeight = 0;

    private Animator mAnimator;
    private int mApplyColorIconAnimationType;
    private int mAnimationType;

    private OnColorChangedListener mListener;

    public interface OnColorChangedListener {
        public void onColorChanged(int color);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflateAndSetupView(inflater, container, savedInstanceState);
    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mResources = getActivity().getResources();
        TypedValue tv = new TypedValue();

        mColorPickerView = inflater.inflate(R.layout.color_picker_fragment, container, false);
        mColorPicker = (ColorPickerView) mColorPickerView.findViewById(R.id.color_picker_view);
        mColorButtonsLayout = (LinearLayout) mColorPickerView.findViewById(
                R.id.color_picker_color_buttons_layout);
        mFavoritesLayout = (LinearLayout) mColorPickerView.findViewById(R.id.favorite_buttons);
        mHelpScreen = mColorPickerView.findViewById(R.id.color_picker_help_screen);

        if (getArguments() != null) {
            mPreferenceKey = getArguments().getString(ColorPickerPreference.PREFERENCE_KEY);
            mTitle = getArguments().getCharSequence(KEY_TITLE);
            mSubtitle = getArguments().getCharSequence(KEY_SUBTITLE);
            mInitialColor = getArguments().getInt(KEY_INITIAL_COLOR);
            if (getArguments().getInt(KEY_NEW_COLOR) != 0) {
                mNewColorValue = getArguments().getInt(KEY_NEW_COLOR);
            } else {
                mNewColorValue = mInitialColor;
                getArguments().putInt(KEY_NEW_COLOR, mNewColorValue);
            }
            if (getArguments().getInt(KEY_OLD_COLOR) != 0) {
                mOldColorValue = getArguments().getInt(KEY_OLD_COLOR);
            } else {
                mOldColorValue = mInitialColor;
                getArguments().putInt(KEY_OLD_COLOR, mOldColorValue);
            }
            mResetColor1 = getArguments().getInt(KEY_RESET_COLOR_1);
            mResetColor2 = getArguments().getInt(KEY_RESET_COLOR_2);
            mResetColor1Title = getArguments().getCharSequence(KEY_RESET_COLOR_1_TITLE);
            mResetColor2Title = getArguments().getCharSequence(KEY_RESET_COLOR_2_TITLE);
            mShowHelpScreen = getShowHelpScreen();
            mHelpScreenVisible = resolveHelpScreenVisibility(
                    getArguments().getInt(KEY_HELP_SCREEN_VISIBILITY));

        }

        if (getActivity().getActionBar() != null) {
            if (mTitle != null) {
                getActivity().getActionBar().setTitle(mTitle);
            }
            if (mSubtitle != null) {
                getActivity().getActionBar().setSubtitle(mSubtitle);
            }
        }

        getActivity().getTheme().resolveAttribute(android.R.attr.colorControlHighlight, tv, true);
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            mBorderColor = tv.data;
        } else {
            mBorderColor = getActivity().getColor(tv.resourceId);
        }

		mFullTranslationX = mResources.getDimension(
                R.dimen.color_picker_action_apply_color_translation_x);

        mColorPicker.setOnColorChangedListener(this);
        mColorPicker.setColor(mNewColorValue);
        mColorPicker.setBorderColor(mBorderColor);

        if (getArguments() != null && getArguments().getBoolean(KEY_ALPHA_SLIDER_VISIBLE)) {
            mColorPicker.setAlphaSliderVisible(true);
        }

        if (mAnimator == null) {
            mAnimator = createAnimator();
        }

        setUpResetMenuAppearience();
        setUpFavoriteColorButtons();
        setUpPaletteColorButtons();
        setUpHelpScreen();

        return mColorPickerView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.color_picker_ab_more, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mHideResetColor1) {
            menu.removeItem(R.id.reset_colors);
            menu.removeItem(R.id.reset_color);
        } else {
            if (mShowSubMenu) {
                menu.removeItem(R.id.reset_color);
                if (mResetColor1Title != null) {
                    menu.findItem(R.id.reset_colors).getSubMenu()
                            .findItem(R.id.reset_color1).setTitle(mResetColor1Title);
                }
                if (mResetColor2Title != null) {
                    menu.findItem(R.id.reset_colors).getSubMenu()
                    .findItem(R.id.reset_color2).setTitle(mResetColor2Title);
                }
            } else {
                menu.removeItem(R.id.reset_colors);
                if (mResetColor1Title != null) {
                    menu.findItem(R.id.reset_color).setTitle(mResetColor1Title);
                }
            }
        }

        MenuItem applyColor = menu.findItem(R.id.apply_color);
        mApplyColorAction = (ApplyColorView) applyColor.getActionView();
        mShowEditHexAction = menu.findItem(R.id.edit_hex);
        LinearLayout editHexActionView = (LinearLayout) mShowEditHexAction.getActionView();
        mEditHexValue = (EditText) editHexActionView.findViewById(R.id.ab_edit_hex);
        ImageButton setHexValueButton = (ImageButton) editHexActionView.findViewById(R.id.ab_edit_hex_enter);
        MenuItem showHideFavorites = menu.findItem(R.id.show_hide_favorites);
        MenuItem showHideHelp = menu.findItem(R.id.show_hide_help);

        boolean newColor = mNewColorValue != mInitialColor;
        int favoritesTitleResId = mShowFavorites
                ? R.string.hide_favorites_title : R.string.show_favorites_title;
        int helpTitleResId = mHelpScreenVisible ? R.string.hide_help_title : R.string.show_help_title;

        mApplyColorAction.setColor(mNewColorValue);
        mApplyColorAction.setColorPreviewTranslationX(newColor ? 0f : mFullTranslationX);
        mApplyColorAction.showSetIcon(newColor ? true : false);
        mApplyColorAction.applySetIconAlpha(newColor ? 1f : 0f);
        mApplyColorAction.setOnClickListener(newColor ? this : null);

        mEditHexValue.setText(ColorPickerHelper.convertToARGB(mNewColorValue));
        mEditHexValue.setOnFocusChangeListener(this);
        setHexValueButton.setOnClickListener(this);

        showHideFavorites.setTitle(mResources.getString(favoritesTitleResId));
        showHideHelp.setTitle(mResources.getString(helpTitleResId));
    }

    private ValueAnimator createAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                if (mAnimationType == ANIMATE_COLOR_TRANSITION) {
                    int blended = ColorPickerHelper.getBlendColor(mOldColorValue, mNewColorValue, position);
                    mApplyColorAction.setColor(blended);
                    if (mApplyColorIconAnimationType != NO_ANIMATION) {
                        final boolean animateShow = mApplyColorIconAnimationType == ANIMATE_TO_SHOW;
                        float currentTranslationX = animateShow ? mFullTranslationX : 0f;
                        float alpha = animateShow ? 0f : 1f;
                        boolean applyAlpha = false;

                        if (animateShow) {
                            currentTranslationX = mFullTranslationX * (1f - position);
                            if (position > 0.5f) {
                                alpha = (position - 0.5f) * 2;
                                applyAlpha = true;
                            }
                        } else {
                            currentTranslationX = mFullTranslationX * position;
                            if (position <= 0.5f && position > 0f) {
                                alpha = 1f - position * 2;
                                applyAlpha = true;
                            }
                        }
                        mApplyColorAction.setColorPreviewTranslationX(currentTranslationX);
                        if (applyAlpha) {
                            mApplyColorAction.applySetIconAlpha(alpha);
                        }
                    }
                } else if (mAnimationType == ANIMATE_FAVORITES_VISIBILITY) {
                    int childCount = mColorButtonsLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        if (i == 0) {
                            mFavoritesLayout.setAlpha(mShowFavorites ? 1f - position : position);
                        } else {
                            View child = mColorButtonsLayout.getChildAt(i);
                            child.setTranslationY(0 - mFavoritesLayoutHeight * (
                                    mShowFavorites ? position : 1f - position));
                        }
                    }
                } else {
                    mHelpScreen.setTranslationY(
                            mHelpScreenHeight * (mHelpScreenVisible ? position  : 1f - position));
                    mHelpScreen.setAlpha(mHelpScreenVisible ? 1f - position : position);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimationType == ANIMATE_FAVORITES_VISIBILITY) {
                } else if (mAnimationType != ANIMATE_COLOR_TRANSITION) {
                    if (!mHelpScreenVisible) {
                        mHelpScreen.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimationType == ANIMATE_COLOR_TRANSITION) {
                    if (mApplyColorIconAnimationType != NO_ANIMATION) {
                        if (mApplyColorIconAnimationType != ANIMATE_TO_SHOW) {
                            mApplyColorAction.showSetIcon(false);
                        } else {
                            mApplyColorAction.setOnClickListener(getFragmentOnClickListener());
                        }
                    }
                    mOldColorValue = mNewColorValue;
                    getArguments().putInt(KEY_NEW_COLOR, mOldColorValue);
                } else if (mAnimationType == ANIMATE_FAVORITES_VISIBILITY) {
                    animation.setInterpolator(null);
                    mShowFavorites = !mShowFavorites;
                    writeShowFavorites(mShowFavorites);
                } else {
                    animation.setInterpolator(null);
                    if (mHelpScreenVisible) {
                        mHelpScreen.setVisibility(View.GONE);
                    }
                    mHelpScreenVisible = !mHelpScreenVisible;
                    getArguments().putInt(KEY_HELP_SCREEN_VISIBILITY, mHelpScreenVisible
                            ? HELP_SCREEN_VISIBILITY_VISIBLE : HELP_SCREEN_VISIBILITY_GONE);
                    getActivity().invalidateOptionsMenu();
                }
            }
        });
        return animator;
    }

    private void setUpResetMenuAppearience() {
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

        if (mResetColor1 != 0) {
            mHideResetColor1 = false;
            if (mResetColor2 != 0) {
                mHideResetColor2 = false;
                mShowSubMenu = true;
            }
        }
    }

    private void setUpFavoriteColorButtons() {
        mShowFavorites = getShowFavorites();
        TypedArray ta = mResources.obtainTypedArray(R.array.color_picker_favorite_color_buttons);

        for (int i=0; i<4; i++) {
            int resId = ta.getResourceId(i, 0);
            int buttonNumber = i + 1;
            String tag = String.valueOf(buttonNumber);
            ColorViewButton button = (ColorViewButton) mColorPickerView.findViewById(resId);
            button.setTag(tag);
            button.setBorderColor(mBorderColor);
            button.setOnLongClickListener(this);
            if (getFavoriteButtonValue(button) != 0) {
                button.setColor(getFavoriteButtonValue(button));
                button.setOnClickListener(this);
            } else {
                button.setShowFavoriteIcon(true);
            }
        }

        ta.recycle();

        mFavoritesLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFavoritesLayoutHeight = mFavoritesLayout.getHeight()
                        + mResources.getDimensionPixelSize(
                                R.dimen.color_picker_color_buttons_container_margin_bottom);
                mFavoritesLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (!mShowFavorites) {
                    int childCount = mColorButtonsLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        if (i == 0) {
                            mFavoritesLayout.setAlpha(0f);
                        } else {
                            View child = mColorButtonsLayout.getChildAt(i);
                            child.setTranslationY(0 - mFavoritesLayoutHeight);
                        }
                    }
                }
            }
        });

    }

    private void setUpPaletteColorButtons() {
        TypedArray layouts = mResources.obtainTypedArray(
                R.array.color_picker_palette_color_buttons_layouts);
        TypedArray buttons = mResources.obtainTypedArray(R.array.color_picker_palette_color_buttons);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_darkkat_palette);

        for (int i=0; i<3; i++) {
            int layoutResId = layouts.getResourceId(i, 0);
            LinearLayout layout = (LinearLayout) mColorPickerView.findViewById(layoutResId);
            TextView paletteTitle = (TextView) layout.findViewById(R.id.palette_color_buttons_title);
            int titleResId = R.string.palette_darkkat_title;
            if (i == PALETTE_MATERIAL) {
                titleResId = R.string.palette_material_title;
                colors = mResources.obtainTypedArray(R.array.color_picker_material_palette);
            } else if (i == PALETTE_RGB) {
                titleResId = R.string.palette_rgb_title;
                colors = mResources.obtainTypedArray(R.array.color_picker_rgb_palette);
            }
            paletteTitle.setText(titleResId);

            for (int j=0; j<8; j++) {
                int buttonResId = buttons.getResourceId(j, 0);
                ColorViewButton button = (ColorViewButton) layout.findViewById(buttonResId);
                button.setColor(getActivity().getColor(colors.getResourceId(j, 0)));
                button.setBorderColor(mBorderColor);
                button.setOnClickListener(this);
            }
        }

        layouts.recycle();
        buttons.recycle();
        colors.recycle();
    }

    private void setUpHelpScreen() {
        mCheckShowHelpScreen = (CheckedTextView) mColorPickerView.findViewById(
                R.id.color_picker_check_show_help_screen);
        mCheckShowHelpScreen.setChecked(!mShowHelpScreen);
        mCheckShowHelpScreen.setOnClickListener(this);
        mCloseHelpScreen = (Button) mColorPickerView.findViewById(
                R.id.color_picker_help_button_ok);
        mCloseHelpScreen.setOnClickListener(this);

        mHelpScreen.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHelpScreenHeight = mHelpScreen.getHeight();
                mHelpScreen.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (!mHelpScreenVisible) {
                    mHelpScreen.setTranslationY(mFavoritesLayoutHeight);
                    mHelpScreen.setAlpha(0f);
                    mHelpScreen.setVisibility(View.GONE);
                }
            }
        });
        mHelpScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reset_color || item.getItemId() == R.id.reset_color1) {
            mColorPicker.setColor(mResetColor1, true);
            return true;
        } else if (item.getItemId() == R.id.reset_color2) {
            mColorPicker.setColor(mResetColor2, true);
            return true;
        } else if (item.getItemId() == R.id.edit_hex) {
            mEditHexValue.setText(ColorPickerHelper.convertToARGB(mNewColorValue));
            return true;
        } else if (item.getItemId() == R.id.show_hide_favorites) {
            mAnimationType = ANIMATE_FAVORITES_VISIBILITY;
            mAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mAnimator.setDuration(300);
            mAnimator.start();
            return true;
        } else if (item.getItemId() == R.id.show_hide_help) {
            mAnimationType = ANIMATE_HELP_SCREEN_VISIBILITY;
            mAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mAnimator.setDuration(mShowFavorites ? 195 : 225);
            mAnimator.start();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.color_picker_apply_color_action_layout) {
            Intent data = new Intent();
            data.putExtra(KEY_NEW_COLOR, mApplyColorAction.getColor());
            data.putExtra(ColorPickerPreference.PREFERENCE_KEY, mPreferenceKey);
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        } else if (v.getId() == R.id.ab_edit_hex_enter) {
			String text = mEditHexValue.getText().toString();
			mShowEditHexAction.collapseActionView();
            try {
                int newColor = ColorPickerHelper.convertToColorInt(text);
                if (newColor != mOldColorValue) {
                    mNewColorValue = newColor;
                    mOldColorValue = mNewColorValue;
                    mColorPicker.setColor(mNewColorValue);
                    getArguments().putInt(KEY_NEW_COLOR, mNewColorValue);
                    getArguments().putInt(KEY_OLD_COLOR, mOldColorValue);
                    if (mNewColorValue != mInitialColor) {
                        mApplyColorAction.setColor(mNewColorValue);
                        mApplyColorAction.setColorPreviewTranslationX(0f);
                        mApplyColorAction.showSetIcon(true);
                        mApplyColorAction.applySetIconAlpha(1f);
                        mApplyColorAction.setOnClickListener(getFragmentOnClickListener());
                    } else {
                        mApplyColorAction.setColor(mNewColorValue);
                        mApplyColorAction.setColorPreviewTranslationX(mFullTranslationX);
                        mApplyColorAction.showSetIcon(false);
                        mApplyColorAction.applySetIconAlpha(0f);
                        mApplyColorAction.setOnClickListener(null);
                    }
                }
            } catch (Exception e) {}
        } else if (v.getId() == R.id.color_picker_check_show_help_screen) {
            mCheckShowHelpScreen.toggle();
            writeShowHelpScreen(!mCheckShowHelpScreen.isChecked());
        } else if (v.getId() == R.id.color_picker_help_button_ok) {
            mAnimationType = ANIMATE_HELP_SCREEN_VISIBILITY;
            mAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mAnimator.setDuration(195);
            mAnimator.start();
        } else if (v instanceof ColorViewButton) {
            try {
                int newColor = ((ColorViewButton) v).getColor();
                if (newColor != mOldColorValue) {
                    mColorPicker.setColor(newColor, true);
                }
            } catch (Exception e) {}
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ColorViewButton button = (ColorViewButton) v;
        if (!v.hasOnClickListeners()) {
            button.setOnClickListener(this);
        }
        button.setShowFavoriteIcon(false);
        button.setColor(mApplyColorAction.getColor());
        writeFavoriteButtonValue(button);
        return true;
    }

    @Override
    public void onColorChanged(int color) {
        mApplyColorIconAnimationType = NO_ANIMATION;
        if (color != mOldColorValue) {
            mNewColorValue = color;
            getArguments().putInt(KEY_NEW_COLOR, mNewColorValue);
            if (mNewColorValue == mInitialColor) {
                if (mOldColorValue != mInitialColor) {
                    mApplyColorIconAnimationType = ANIMATE_TO_HIDE;
                    mApplyColorAction.setOnClickListener(null);
                    mApplyColorAction.setClickable(false);
                }
            } else if (mOldColorValue == mInitialColor) {
                mApplyColorIconAnimationType = ANIMATE_TO_SHOW;
                mApplyColorAction.showSetIcon(true);
            }
            mAnimationType = ANIMATE_COLOR_TRANSITION;
            mAnimator.setDuration(300);
            mAnimator.start();

            try {
                if (mEditHexValue != null) {
                    mEditHexValue.setText(ColorPickerHelper.convertToARGB(color));
                }
            } catch (Exception e) {}
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            mEditHexValue.removeTextChangedListener(this);
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            mEditHexValue.addTextChangedListener(this);
        }
    }

    private int getColor() {
        return mColorPicker.getColor();
    }

    private View.OnClickListener getFragmentOnClickListener() {
        return this;
    }

    private void writeShowFavorites(boolean show) {
        mPrefs.edit().putBoolean(KEY_SHOW_FAVORITES, show).commit();
        getActivity().invalidateOptionsMenu();
    }

    private boolean getShowFavorites() {
        return mPrefs.getBoolean(KEY_SHOW_FAVORITES, true);
    }

    private void writeFavoriteButtonValue(ColorViewButton button) {
        mPrefs.edit().putInt(KEY_ID_FAVORITE_COLOR_BUTTON + (String) button.getTag(),
                button.getColor()).commit();
    }

    private int getFavoriteButtonValue(ColorViewButton button) {
        return mPrefs.getInt(KEY_ID_FAVORITE_COLOR_BUTTON + (String) button.getTag(), 0);
    }

    private void writeShowHelpScreen(boolean show) {
        mPrefs.edit().putBoolean(KEY_SHOW_HELP_SCREEN, show).commit();
    }

    private boolean getShowHelpScreen() {
        return mPrefs.getBoolean(KEY_SHOW_HELP_SCREEN, true);
    }

    private boolean resolveHelpScreenVisibility(int visibility) {
        if (visibility == HELP_SCREEN_VISIBILITY_DEFAULT) {
            getArguments().putInt(KEY_HELP_SCREEN_VISIBILITY,
                    mShowHelpScreen ? HELP_SCREEN_VISIBILITY_VISIBLE : HELP_SCREEN_VISIBILITY_GONE);
            return mShowHelpScreen;
        } else {
            return visibility == HELP_SCREEN_VISIBILITY_VISIBLE ? true : false;
        }
    }
}
