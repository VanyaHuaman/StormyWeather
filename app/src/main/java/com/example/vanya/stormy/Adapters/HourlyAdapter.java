package com.example.vanya.stormy.Adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.vanya.stormy.R;
import com.example.vanya.stormy.Weather.Hour;
import com.example.vanya.stormy.databinding.HourlyListItemBinding;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private List<Hour> mHours;
    private Context mContext;

    public HourlyAdapter(List<Hour> hours, Context context) {
        mHours = hours;
        mContext = context;
    }

    @NonNull
    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HourlyListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                R.layout.hourly_list_item,
                parent,
                false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Hour hour = mHours.get(position);
        holder.hourlyListItemBinding.setHour(hour);
    }

    @Override
    public int getItemCount() {
        return mHours.size();
    }


    //VIEW HOLDER
    public class ViewHolder extends RecyclerView.ViewHolder{
        //Binding var
        public HourlyListItemBinding hourlyListItemBinding;

        //Constructor to do lookups for each subview
        public ViewHolder(HourlyListItemBinding hourlyLayoutBinding){
            super(hourlyLayoutBinding.getRoot());
            hourlyListItemBinding = hourlyLayoutBinding;
        }
    }


}
