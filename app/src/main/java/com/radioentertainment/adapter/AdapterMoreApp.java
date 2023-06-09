package com.radioentertainment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.radioentertainment.item.ItemMoreApp;
import com.radioentertainment.radio.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMoreApp extends RecyclerView.Adapter<AdapterMoreApp.MyViewHolder> {


    private ArrayList<ItemMoreApp> itemMoreApps;

    public AdapterMoreApp(ArrayList<ItemMoreApp> itemMoreApps) {
        this.itemMoreApps = itemMoreApps;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_app, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemMoreApp item = itemMoreApps.get(position);

        holder.textView_name.setText(item.getName());

        Picasso.get()
                .load(item.getThumb())
                .into(holder.imageView_thumb);
    }

    @Override
    public int getItemCount() {
        return itemMoreApps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_name;
        private ImageView imageView_thumb;

        private MyViewHolder(View view) {
            super(view);
            imageView_thumb = view.findViewById(R.id.imageView_thumb);
            textView_name = view.findViewById(R.id.textView_name);
        }
    }
}
