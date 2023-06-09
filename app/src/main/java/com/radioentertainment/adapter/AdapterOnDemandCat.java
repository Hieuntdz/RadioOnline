package com.radioentertainment.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;

import java.util.ArrayList;


public class AdapterOnDemandCat extends RecyclerView.Adapter<AdapterOnDemandCat.MyViewHolder> {

    private Context context;
    private ArrayList<ItemOnDemandCat> arraylist;
    private ArrayList<ItemOnDemandCat> filteredArrayList;
    private NameFilter filter;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_title;
        private ImageView imageView;
        private CardView cardView;

        private MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.row_layout);
            textView_title = view.findViewById(R.id.textView_radio_name);
            imageView = view.findViewById(R.id.row_logo);
        }
    }

    public AdapterOnDemandCat(Context context, ArrayList<ItemOnDemandCat> list) {
        this.context = context;
        this.arraylist = list;
        this.filteredArrayList = list;

        methods = new Methods(context);
        Resources r = context.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GRID_PADDING, r.getDisplayMetrics());
        Constants.columnWidth = (int) ((methods.getScreenWidth() - ((Constants.NUM_OF_COLUMNS + 1) * padding)) / Constants.NUM_OF_COLUMNS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_ondemandcat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constants.columnWidth, (int) (Constants.columnWidth / 1.5));
//        params.setMargins(0, 0, 0, 20);
//        holder.cardView.setLayoutParams(params);

        holder.textView_title.setText(arraylist.get(position).getName());

        Picasso.get()
                .load(methods.getImageThumbSize(arraylist.get(holder.getAdapterPosition()).getImage(),context.getString(R.string.on_demand)))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getID(int pos) {
        return arraylist.get(pos).getId();
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
                ArrayList<ItemOnDemandCat> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
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

            arraylist = (ArrayList<ItemOnDemandCat>) results.values;
            notifyDataSetChanged();
        }
    }
}