package com.example.hcmus_filter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hcmus_filter.R;
import com.example.hcmus_filter.model.FilterData;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ListFilterAdapter extends RecyclerView.Adapter<ListFilterAdapter.FilterViewHolder> {

    List<FilterData> listFilter;
    int currentPosition = 0;

    public ListFilterAdapter(List<FilterData> listFilter) {
        this.listFilter = listFilter;
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, int position) {
        holder.setData(listFilter.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_filter)
        View llFilter;
        @Bind(R.id.iv_filter_image)
        ImageView ivFilterImage;
        @Bind(R.id.tv_filter_name)
        TextView tvFilterName;

        public FilterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(final FilterData filterData, final int position) {
            ivFilterImage.setImageResource(filterData.getImageId());
            tvFilterName.setText(filterData.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFilterSelect != null) {
                        int oldFocusPosition = currentPosition;
                        currentPosition = position;
                        notifyItemChanged(oldFocusPosition);
                        notifyItemChanged(position);
                        onFilterSelect.onSelect(filterData);
                    }
                }
            });
            if (position == currentPosition) {
                llFilter.setBackgroundResource(R.drawable.bg_item_filter_selected);
            } else {
                llFilter.setBackgroundResource(R.drawable.bg_item_filter_unselected);
            }
        }
    }

    public interface OnFilterSelect {
        void onSelect(FilterData filterData);
    }

    OnFilterSelect onFilterSelect;

    public OnFilterSelect getOnFilterSelect() {
        return onFilterSelect;
    }

    public void setOnFilterSelect(OnFilterSelect onFilterSelect) {
        this.onFilterSelect = onFilterSelect;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
