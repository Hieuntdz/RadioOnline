package com.radioentertainment.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.radio.BaseActivity;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;

import java.util.ArrayList;


public class AdapterOnDemand extends RecyclerView.Adapter<AdapterOnDemand.MyViewHolder> {

    private Context context;
    private ArrayList<ItemRadio> arraylist;
    private ArrayList<ItemRadio> filteredArrayList;
    private NameFilter filter;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_title, textView_views, textView_lang;
        private ImageView imageView_fav, imageView;
        private LinearLayout cardView;

        private MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.row_layout);
            textView_views = view.findViewById(R.id.textView_view);
            textView_lang = view.findViewById(R.id.textView_list_lang);
            textView_title = view.findViewById(R.id.textView_radio_name);
            imageView = view.findViewById(R.id.row_logo);
        }
    }

    public AdapterOnDemand(Context context, ArrayList<ItemRadio> list) {
        this.context = context;
        this.arraylist = list;
        this.filteredArrayList = list;

        methods = new Methods(context, interAdListener);
//        Resources r = context.getResources();
//        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GRID_PADDING, r.getDisplayMetrics());
//        Constants.columnWidth = (int) ((methods.getScreenWidth() - ((Constants.NUM_OF_COLUMNS + 1) * padding)) / Constants.NUM_OF_COLUMNS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cityradio_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constants.columnWidth, (int)(Constants.columnWidth/1.5));
//        params.setMargins(0, 0, 0, 20);
//        holder.cardView.setLayoutParams(params);

        holder.textView_views.setText(Methods.format(Double.parseDouble(arraylist.get(position).getViews())));
        holder.textView_title.setText(arraylist.get(position).getRadioName());
        holder.textView_lang.setText(arraylist.get(position).getDuration());

        Picasso.get()
                .load(methods.getImageThumbSize(arraylist.get(holder.getAdapterPosition()).getImageThumb(),context.getString(R.string.on_demand)))
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInter(holder.getAdapterPosition(),"");
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemRadio> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getRadioName();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arraylist = (ArrayList<ItemRadio>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constants.arrayList_radio.clear();
            Constants.arrayList_radio.addAll(arraylist);
            ((BaseActivity) context).clickPlay(position, false);
        }
    };
}