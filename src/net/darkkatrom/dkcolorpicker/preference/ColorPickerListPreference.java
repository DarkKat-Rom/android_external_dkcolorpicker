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

package net.darkkatrom.dkcolorpicker.preference;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkcolorpicker.R;
import net.darkkatrom.dkcolorpicker.util.ColorPickerHelper;
import net.darkkatrom.dkcolorpicker.widget.ColorViewButton;

public class ColorPickerListPreference extends ListPreference implements
        ColorPickerListAdapter.OnItemClickedListener {

    boolean mNeedEntryColors;
    private CharSequence[] mEntryColors;

    private ColorPickerListAdapter mAdapter;
    private int mClickedDialogItem;

    public ColorPickerListPreference(Context context) {
        this(context, null);
    }

    public ColorPickerListPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    public ColorPickerListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorPickerListPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.ColorPickerListPreference, defStyleAttr, defStyleRes);
            mNeedEntryColors = a.getBoolean(R.styleable.ColorPickerListPreference_needEntryColors, true);
            if (mNeedEntryColors) {
                mEntryColors = a.getTextArray(R.styleable.ColorPickerListPreference_entryColors);
            } else {
                mEntryColors = getEntryValues();
            }
            a.recycle();
        }
        setPositiveButtonText(R.string.dialog_ok);
        setNegativeButtonText(R.string.dialog_cancel);
        setLayoutResource(R.layout.preference_color_picker);
        setWidgetLayoutResource(R.layout.preference_widget_color_picker_list);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        if (view != null) {
            int entryColor = getEntryColor() != null
                    ? convertToColorInt(getEntryColor().toString()) : Color.TRANSPARENT;
            ImageView icon = (ImageView) view.findViewById(R.id.color_picker_list_widget_icon);
            TextView hex = (TextView) view.findViewById(R.id.color_picker_list_widget_hex);
            if (entryColor != Color.TRANSPARENT) {
                icon.setImageTintList(ColorStateList.valueOf(entryColor));
            }
            hex.setText(getEntryColor());
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }

        View view = LayoutInflater.from(builder.getContext()).inflate(
                R.layout.color_picker_dialog_list, null, false);
        View dividerTop = view.findViewById(R.id.color_picker_dialog_list_divider_top);
        View dividerBottom = view.findViewById(R.id.color_picker_dialog_list_divider_bottom);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.color_picker_dialog_list);
        mAdapter = new ColorPickerListAdapter(getContext(), dividerTop, dividerBottom, getEntries(),
                getEntryColors(), findIndexOfValue(getValue()));
        mAdapter.setOnItemClickedListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);
        llm.scrollToPosition(findIndexOfValue(getValue()));
        builder.setView(view);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        ((AlertDialog) getDialog()).getButton(
                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onItemClicked(int position) {
        mClickedDialogItem = position;
        ((AlertDialog) getDialog()).getButton(
                AlertDialog.BUTTON_POSITIVE).setEnabled(findIndexOfValue(getValue()) != position);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        mAdapter.setOnItemClickedListener(null);
        if (positiveResult && mClickedDialogItem >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[mClickedDialogItem].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    public void setNeedEntryColors(boolean needEntryColors) {
        mNeedEntryColors = needEntryColors;
        notifyChanged();
    }

    public void setEntryColors(CharSequence[] entryColors) {
        mEntryColors = entryColors;
        notifyChanged();
    }

    public void setEntryColors(int entryColorsResId) {
        setEntryColors(getContext().getResources().getTextArray(entryColorsResId));
    }

    public CharSequence[] getEntryColors() {
        return mEntryColors;
    }

    public CharSequence getEntryColor() {
        int index = findIndexOfValue(getValue());
        return index >= 0 && mEntryColors != null ? mEntryColors[index] : null;
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
