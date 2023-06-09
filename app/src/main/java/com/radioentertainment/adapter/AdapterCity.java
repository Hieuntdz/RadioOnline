package com.radioentertainment.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.radioentertainment.interfaces.CityClickListener;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.item.ItemCity;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class AdapterCity extends RecyclerView.Adapter{

    private ArrayList<Object> arraylist;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private CityClickListener cityClickListener;
    private Methods methods;
    private SharedPref sharedPref;

    public AdapterCity(Context context, ArrayList<Object> list, CityClickListener cityClickListener) {
        this.arraylist = list;
        this.filteredArrayList = list;
        this.cityClickListener = cityClickListener;
        methods = new Methods(context, interAdListener);
        sharedPref = new SharedPref(context);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_title;
        private CardView cardView;
        private ImageView imageView;

        private MyViewHolder(View view) {
            super(view);
            textView_title = view.findViewById(R.id.tv_city_title);
            cardView = view.findViewById(R.id.cv_city_item);
            imageView = view.findViewById(R.id.image_citylist_item);
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
                        .inflate(R.layout.layout_citylist, parent, false);
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
                MyViewHolder viewHolder = (MyViewHolder) holder;
                ItemCity item = (ItemCity) arraylist.get(position);
                viewHolder.textView_title.setText(item.getName());
                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methods.showInter(holder.getAdapterPosition(), "");
                    }
                });

                String url ="";

                switch (item.getTagLine()){
                    case "USA":
                        url = "https://cdn.countryflags.com/thumbs/united-states-of-america/flag-800.png";
                        break;
                    case "Germany":
                        url = "https://cdn.countryflags.com/thumbs/germany/flag-800.png";
                        break;
                    case "Italy":
                        url = "https://cdn.countryflags.com/thumbs/italy/flag-800.png";
                        break;
                    case "Australia":
                        url = "https://cdn.countryflags.com/thumbs/australia/flag-800.png";
                        break;
                    case "Austria":
                        url = "https://cdn.countryflags.com/thumbs/austria/flag-800.png";
                        break;
                    case "Sweden":
                        url = "https://cdn.countryflags.com/thumbs/sweden/flag-800.png";
                        break;
                    case "France":
                        url = "https://cdn.countryflags.com/thumbs/france/flag-800.png";
                        break;
                    case "Spain":
                        url = "https://cdn.countryflags.com/thumbs/spain/flag-800.png";
                        break;
                    case "Canada":
                        url = "https://cdn.countryflags.com/thumbs/canada/flag-800.png";
                        break;
                    case "Switzerland":
                        url = "https://cdn.countryflags.com/thumbs/switzerland/flag-800.png";
                        break;
                    case "Colombia":
                        url = "https://cdn.countryflags.com/thumbs/colombia/flag-800.png";
                        break;
                    case "Brazil":
                        url = "https://cdn.countryflags.com/thumbs/brazil/flag-800.png";
                        break;
                    case "Haiti":
                        url = "https://cdn.countryflags.com/thumbs/haiti/flag-800.png";
                        break;
                    case "United Kingdom":
                        url = "https://cdn.countryflags.com/thumbs/england/flag-800.png";
                        break;
                    case "Denmark":
                        url = "https://cdn.countryflags.com/thumbs/denmark/flag-800.png";
                        break;
                    case "Netherlands":
                        url = "https://cdn.countryflags.com/thumbs/netherlands/flag-800.png";
                        break;
                    case "Ireland":
                        url = "https://cdn.countryflags.com/thumbs/ireland/flag-800.png";
                        break;
                    case "Indonesia":
                        url = "https://cdn.countryflags.com/thumbs/indonesia/flag-800.png";
                        break;
                    case "Turkey":
                        url = "https://cdn.countryflags.com/thumbs/turkey/flag-800.png";
                        break;
                    case "Norway":
                        url = "https://cdn.countryflags.com/thumbs/norway/flag-800.png";
                        break;
                    case "Mexico":
                        url = "https://cdn.countryflags.com/thumbs/mexico/flag-800.png";
                        break;
                    case "Uruguay":
                        url = "https://cdn.countryflags.com/thumbs/uruguay/flag-800.png";
                        break;
                    case "Russia":
                        url = "https://cdn.countryflags.com/thumbs/russia/flag-800.png";
                        break;
                    case "Poland":
                        url = "https://cdn.countryflags.com/thumbs/poland/flag-800.png";
                        break;
                    case "Singapore":
                        url = "https://cdn.countryflags.com/thumbs/singapore/flag-800.png";
                        break;
                    case "Romania":
                        url = "https://cdn.countryflags.com/thumbs/romania/flag-800.png";
                        break;
                    case "Kenya":
                        url = "https://cdn.countryflags.com/thumbs/kenya/flag-800.png";
                        break;
                    case "Georgia":
                        url ="https://cdn.countryflags.com/thumbs/georgia/flag-800.png";
                        break;
                    default:
                        url = "https://w7.pngwing.com/pngs/931/997/png-transparent-computer-icons-global-miscellaneous-photography-logo.png";
                        break;
                }
                Picasso.get()
                        .load(url)
                        .into(viewHolder.imageView);
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
            return Constants.ITEM_CITY;
        }
    }

    private String getID(int pos) {
        ItemCity item = (ItemCity) arraylist.get(pos);
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
                ArrayList<ItemCity> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    Object obj = filteredArrayList.get(i);
                    if(obj instanceof ItemCity){
                        ItemCity item = (ItemCity) obj;
                        String nameList = item.getName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(item);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<ItemCity> filteredItems = new ArrayList<>();
                    for (Object obj:
                            filteredArrayList) {
                        if(obj instanceof ItemCity){
                            ItemCity item = (ItemCity) obj;
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

    private void loadCityRadio(int pos) {
        int reali = getPosition(getID(pos));
        Constants.itemCity = (ItemCity) arraylist.get(reali);
        cityClickListener.onClick();
    }

    private int getPosition(String id) {
        int count = 0;
        int rid = Integer.parseInt(id);
        for (int i = 0; i < arraylist.size(); i++) {
            try {
                ItemCity item = (ItemCity) arraylist.get(i);
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
            loadCityRadio(position);
        }
    };
}