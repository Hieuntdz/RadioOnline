package com.radioentertainment.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.radioentertainment.item.ItemTheme;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Methods;

import java.util.ArrayList;


public class AdapterTheme extends RecyclerView.Adapter<AdapterTheme.MyViewHolder> {

    private ArrayList<ItemTheme> arraylist;
    private Methods methods;

    public AdapterTheme(Context context, ArrayList<ItemTheme> list) {
        this.arraylist = list;
        methods = new Methods(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View vieww;

        private MyViewHolder(View view) {
            super(view);
            vieww = view.findViewById(R.id.view_theme);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_theme, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.vieww.setBackground(methods.getGradientDrawable(arraylist.get(position).getFirstColor(), arraylist.get(position).getSecondColor()));
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}