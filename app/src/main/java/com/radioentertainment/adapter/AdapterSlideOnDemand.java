package com.radioentertainment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Methods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSlideOnDemand extends RecyclerView.Adapter<AdapterSlideOnDemand.OndemandViewHolder> {
    private ArrayList<ItemOnDemandCat> arrayList;
    private Methods methods;
    private static int selected_index= 0;

    public AdapterSlideOnDemand(ArrayList<ItemOnDemandCat> arrayList, Methods methods) {
        this.arrayList = arrayList;
        this.methods = methods;
    }

    @NonNull
    @Override
    public OndemandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout_slide_ondemand_item, parent, false);
        return new OndemandViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OndemandViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class OndemandViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_ondemand_slide_item;
        private ImageView imageView_ondemand2_slide_item, imageView_transparency_slide_item;
        private ConstraintLayout layout_item_slide;

        public OndemandViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_ondemand_slide_item = (TextView) itemView.findViewById(R.id.txt_ondemand_slide_item);
            imageView_ondemand2_slide_item = (ImageView) itemView.findViewById(R.id.imageView_ondemand2_slide_item);
            imageView_transparency_slide_item = (ImageView) itemView.findViewById(R.id.imageView_transparency_slide_item);
            layout_item_slide = (ConstraintLayout) itemView.findViewById(R.id.layout_item_slide);
        }

        public void bindView(int position){
            txt_ondemand_slide_item.setText(arrayList.get(position).getName());
            Picasso.get().load(arrayList.get(position).getImage()).into(imageView_ondemand2_slide_item);
            if(position == selected_index){
                //itemView.setScaleX(1.3f);
                imageView_transparency_slide_item.setVisibility(View.GONE);
            } else {

                //itemView.setScaleX(1.0f);
                imageView_transparency_slide_item.setVisibility(View.VISIBLE);
            }
            layout_item_slide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methods.showInter(position, "");
                }
            });
        }
    }

    public static void setSelected_index(int selected_index) {
        AdapterSlideOnDemand.selected_index = selected_index;
    }
}
