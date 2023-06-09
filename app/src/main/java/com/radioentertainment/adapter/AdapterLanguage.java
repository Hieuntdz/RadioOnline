package com.radioentertainment.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radioentertainment.interfaces.CityClickListener;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.item.ItemLanguage;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;
import com.google.android.gms.ads.AdView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class AdapterLanguage extends RecyclerView.Adapter {

    private ArrayList<Object> arraylist;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private CityClickListener cityClickListener;
    private Methods methods;
    private SharedPref sharedPref;

    public AdapterLanguage(Context context, ArrayList<Object> list, CityClickListener cityClickListener) {
        this.arraylist = list;
        this.filteredArrayList = list;
        this.cityClickListener = cityClickListener;
        methods = new Methods(context, interAdListener);
        sharedPref = new SharedPref(context);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_title;
        private LinearLayout ll;
        private View vieww;

        private MyViewHolder(View view) {
            super(view);
            ll = view.findViewById(R.id.ll);
            textView_title = view.findViewById(R.id.textView_cityname);
            vieww = view.findViewById(R.id.view_city);
        }
    }

    public static class BannerAdsViewHolder extends RecyclerView.ViewHolder{

        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                View bannerAdView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_ad_containter, parent, false);
                return new BannerAdsViewHolder(bannerAdView);
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_languagelist, parent, false);
                return new MyViewHolder(itemView);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) holder;
                AdView adView = (AdView) arraylist.get(position);
                ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;

                if(adCardView.getChildCount()>0){
                    adCardView.removeAllViews();
                }
                adCardView.addView(adView);
                break;
            default:
                MyViewHolder myViewHolder = (MyViewHolder) holder;

                ItemLanguage item = (ItemLanguage) arraylist.get(position);

                myViewHolder.vieww.setBackgroundColor(sharedPref.getFirstColor());
                myViewHolder.textView_title.setText(item.getName());

                myViewHolder.ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methods.showInter(holder.getAdapterPosition(), "");
                    }
                });
                break;
        }


    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if(arraylist.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else{
            return Constants.ITEM_LANGUAGE;
        }
    }

    private String getID(int pos) {
        ItemLanguage item = (ItemLanguage) arraylist.get(pos);
        return item.getId();
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
                    Object obj = filteredArrayList.get(i);
                    if(obj instanceof ItemLanguage){
                        ItemLanguage item = (ItemLanguage) obj;
                        String nameList = item.getName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(item);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<ItemLanguage> filteredItems = new ArrayList<>();
                    for (Object obj:
                         filteredArrayList) {
                        if(obj instanceof ItemLanguage){
                            ItemLanguage item = (ItemLanguage) obj;
                            filteredItems.add(item);
                        }
                    }
                    result.values = filteredItems;
                    result.count = filteredItems.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arraylist = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }

    private void loadLangRadio(int pos) {
        int reali = getPosition(getID(pos));
        Constants.itemLanguage = (ItemLanguage) arraylist.get(reali);
        cityClickListener.onClick();
    }

    private int getPosition(String id) {
        int count = 0;
        int rid = Integer.parseInt(id);
        for (int i = 0; i < arraylist.size(); i++) {
            try {
                ItemLanguage item = (ItemLanguage) arraylist.get(i);
                if (rid == Integer.parseInt(item.getId())) {
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