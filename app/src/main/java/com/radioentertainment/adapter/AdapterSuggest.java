package com.radioentertainment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.radio.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSuggest extends RecyclerView.Adapter<AdapterSuggest.MyViewHolder> {

    private ArrayList<ItemOnDemandCat> arrayList;

    public AdapterSuggest(ArrayList<ItemOnDemandCat> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_demand_control, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemOnDemandCat item = arrayList.get(position);

        holder.tv_name.setText(item.getName());
        holder.tv_count.setText(item.getTotalItems()+" songs");

        Picasso.get()
                .load(item.getThumb())
                .into(holder.iv_thumb);

    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumb;
        private TextView tv_name, tv_count;

        private MyViewHolder(View view) {
            super(view);
            iv_thumb = view.findViewById(R.id.iv_thumb);
            tv_name = view.findViewById(R.id.tv_name);
            tv_count = view.findViewById(R.id.tv_count);
        }
    }
}
