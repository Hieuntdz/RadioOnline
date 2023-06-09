package com.radioentertainment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.radioentertainment.interfaces.CityClickListener;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.item.ItemLanguage;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;

import java.util.ArrayList;

public class AdapterHomeLanguage extends RecyclerView.Adapter<AdapterHomeLanguage.MyViewHolder> {

    private ArrayList<ItemLanguage> arraylist;
    private ArrayList<ItemLanguage> filteredArrayList;
    private NameFilter filter;
    private CityClickListener cityClickListener;
    private Methods methods;
    private SharedPref sharedPref;

    public AdapterHomeLanguage(Context context, ArrayList<ItemLanguage> list, CityClickListener cityClickListener) {
        this.arraylist = list;
        this.filteredArrayList = list;
        this.cityClickListener = cityClickListener;
        methods = new Methods(context, interAdListener);
        sharedPref = new SharedPref(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cityhome, parent, false);

        return new AdapterHomeLanguage.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.tv_title.setText(arraylist.get(position).getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInter(holder.getAdapterPosition(), "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private CardView cardView;

        private MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cv_cityhome);
            tv_title = view.findViewById(R.id.tv_cityhome_text);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private String getID(int pos) {
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
                ArrayList<ItemLanguage> filteredItems = new ArrayList<>();

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

            arraylist = (ArrayList<ItemLanguage>) results.values;
            notifyDataSetChanged();
        }
    }

    private void loadLangRadio(int pos) {
        int reali = getPosition(getID(pos));
        Constants.itemLanguage = arraylist.get(reali);
        cityClickListener.onClick();
    }

    private int getPosition(String id) {
        int count = 0;
        int rid = Integer.parseInt(id);
        for (int i = 0; i < arraylist.size(); i++) {
            try {
                if (rid == Integer.parseInt(arraylist.get(i).getId())) {
                    count = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            loadLangRadio(position);
        }
    };
}
