package com.ldx.weather.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ldx.weather.db.bean.DailyResponse;
import com.ldx.weather.databinding.ItemDailyRvBinding;
import com.ldx.weather.utils.EasyDate;
import com.ldx.weather.utils.WeatherUtil;

import java.util.List;

/**
 * 天气预报 数据适配器
 */
public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {

    private final List<DailyResponse.DailyBean> dailyBeans;

    private OnClickItemCallback onClickItemCallback;

    public void setOnClickItemCallback(OnClickItemCallback onClickItemCallback) {
        this.onClickItemCallback = onClickItemCallback;
    }

    public DailyAdapter(List<DailyResponse.DailyBean> dailyBeans) {
        this.dailyBeans = dailyBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDailyRvBinding binding = ItemDailyRvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);
        //添加点击回调
        binding.getRoot().setOnClickListener(v -> {
            if (onClickItemCallback != null) {
                onClickItemCallback.onItemClick(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyResponse.DailyBean dailyBean = dailyBeans.get(position);
        holder.binding.tvDate.setText(EasyDate.dateSplit(dailyBean.getFxDate()) + EasyDate.getDayInfo(dailyBean.getFxDate()));
        WeatherUtil.changeIcon(holder.binding.ivStatus, Integer.parseInt(dailyBean.getIconDay()));
        holder.binding.tvHeight.setText(dailyBean.getTempMax() + "℃");
        holder.binding.tvLow.setText(" / " + dailyBean.getTempMin() + "℃");
    }

    @Override
    public int getItemCount() {
        return dailyBeans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ItemDailyRvBinding binding;

        public ViewHolder(@NonNull ItemDailyRvBinding itemTextRvBinding) {
            super(itemTextRvBinding.getRoot());
            binding = itemTextRvBinding;
        }
    }
}
