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

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import net.darkkatrom.dkcolorpicker.R;
import net.darkkatrom.dkcolorpicker.widget.ColorViewButton;
import net.darkkatrom.dkcolorpicker.util.ColorPickerHelper;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerListAdapter extends RecyclerView.Adapter<ColorPickerListAdapter.ViewHolder> {

    private Context mContext;
    private View mDividerTop;
    private View mDividerBottom;
    private boolean mDividerTopVisible = true;
    private boolean mDividerBottomVisible = false;
    private int mBorderColor;
    private List<ColorPickerListItem> mColorPickerListItems;
    private int mSelectedItem;

    private OnItemClickedListener mOnItemClickedListener;

    public interface OnItemClickedListener {
        public void onItemClicked(int position);
    }

    public ColorPickerListAdapter(Context context, View dividerTop, View dividerBottom,
            CharSequence[] entries, CharSequence[] entryColors, int selectedItem) {
        super();

        mContext = context;
        mDividerTop = dividerTop;
        mDividerBottom = dividerBottom;
        TypedValue tv = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.colorControlHighlight, tv, true);
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            mBorderColor = tv.data;
        } else {
            mBorderColor = mContext.getColor(tv.resourceId);
        }
        mColorPickerListItems = new ArrayList<ColorPickerListItem>();
        mSelectedItem = selectedItem;
        for (int i = 0; i < entries.length; i++) {
            ColorPickerListItem item = new ColorPickerListItem(entries[i], entryColors[i],
                    i == mSelectedItem);
            mColorPickerListItems.add(item);
        }
    }

    @Override
    public ColorPickerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.color_picker_dialog_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ColorPickerListItem item = mColorPickerListItems.get(position);
        if (position == 0) {
            int paddingStart = holder.mView.getPaddingStart();
            int paddingTop = mContext.getResources().getDimensionPixelOffset(
                    R.dimen.color_picker_dialog_list_first_item_padding_top);
            int paddingEnd = holder.mView.getPaddingEnd();
            holder.mView.setPaddingRelative(paddingStart, paddingTop, paddingEnd, 0);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPickerListItems.get(mSelectedItem).setChecked(false);
                mColorPickerListItems.get(position).setChecked(true);
                mSelectedItem = position;
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onItemClicked(mSelectedItem);
                }
                notifyDataSetChanged();
            }
        });
        if (holder.mTitle.isChecked() != item.isChecked()) {
            holder.mTitle.setChecked(item.isChecked());
        }
        holder.mTitle.setText(item.getTitle());
        holder.mPreview.setColor(item.getColor());
        holder.mPreview.setBorderColor(mBorderColor);
    }


    @Override
    public int getItemCount() {
        return mColorPickerListItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int firstVisibleItemPosition = llm.findFirstCompletelyVisibleItemPosition();
                int lastVisibleItemPosition = llm.findLastCompletelyVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (firstVisibleItemPosition == 0 && !mDividerTopVisible) {
                        mDividerTop.setVisibility(View.VISIBLE);
                        mDividerTopVisible = true;
                    }

                    if (lastVisibleItemPosition == mColorPickerListItems.size() - 1
                            && mDividerBottomVisible) {
                        mDividerBottom.setVisibility(View.INVISIBLE);
                        mDividerBottomVisible = false;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = llm.findFirstCompletelyVisibleItemPosition();
                int lastVisibleItemPosition = llm.findLastCompletelyVisibleItemPosition();
                if (firstVisibleItemPosition == 0 && mDividerTopVisible) {
                    mDividerTop.setVisibility(View.INVISIBLE);
                    mDividerTopVisible = false;
                } else if (firstVisibleItemPosition != 0 && !mDividerTopVisible) {
                    mDividerTop.setVisibility(View.VISIBLE);
                    mDividerTopVisible = true;
                }

                if (lastVisibleItemPosition == mColorPickerListItems.size() - 1
                            && mDividerBottomVisible) {
                    mDividerBottom.setVisibility(View.INVISIBLE);
                    mDividerBottomVisible = true;
                } else if (lastVisibleItemPosition != mColorPickerListItems.size() - 1
                            && !mDividerBottomVisible) {
                    mDividerBottom.setVisibility(View.VISIBLE);
                    mDividerBottomVisible = true;
                }
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.clearOnScrollListeners();
    }

    public class ColorPickerListItem {
        private CharSequence mTitle;
        private CharSequence mColor;
        private boolean mIsChecked;

        public ColorPickerListItem(CharSequence title, CharSequence color, boolean isChecked) {
            mTitle = title;
            mColor = color;
            mIsChecked = isChecked;
        }

        public CharSequence getTitle() {
            return mTitle;
        }

        public int getColor() {
            return ColorPickerHelper.convertToColorInt(mColor.toString());
        }

        public boolean isChecked() {
            return mIsChecked;
        }

        public void setChecked(boolean checked) {
            mIsChecked = checked;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CheckedTextView mTitle;
        public ColorViewButton mPreview;

        public ViewHolder(View v) {
            super(v);

            mView = v;
            mTitle = (CheckedTextView) v.findViewById(R.id.color_picker_dialog_list_item_title);
            mPreview = (ColorViewButton) v.findViewById(R.id.color_picker_dialog_list_item_preview);
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        mOnItemClickedListener = onItemClickedListener;
    }
}
