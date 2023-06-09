package com.radioentertainment.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.radio.BaseActivity;
import com.radioentertainment.radio.PlayService;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.DBHelper;
import com.radioentertainment.utils.Methods;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class AdapterRadioList extends RecyclerView.Adapter {

    private DBHelper dbHelper;
    private ArrayList<Object> arrayList;
    private Context context;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private Methods methods;
    private String type = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView, imageView_fav;
        private TextView textView_radio, textView_freq;
        private CardView cv_home;

        private MyViewHolder(View view) {
            super(view);
            cv_home = view.findViewById(R.id.cv_home);
            textView_radio = view.findViewById(R.id.textView_radio_home);
            textView_freq = view.findViewById(R.id.textView_freq_home);
            imageView = view.findViewById(R.id.imageView_radio_home);
            imageView_fav = view.findViewById(R.id.imageView_fav_home);
        }
    }

    public class BannerAdsViewHolder extends RecyclerView.ViewHolder{

        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public AdapterRadioList(Context context, ArrayList<Object> arrayList, String type) {
        this.arrayList = arrayList;
        this.context = context;
        methods = new Methods(context, interAdListener);
        filteredArrayList = arrayList;
        dbHelper = new DBHelper(context);
        this.type = type;
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
                View itemView;

                if(type.equals("grid")){
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_radiolist_grid, parent, false);
                }else{
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_radiolist, parent, false);
                }
                return new MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) holder;
                AdView adView = (AdView) arrayList.get(position);
                ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;

                if(adCardView.getChildCount()>0){
                    adCardView.removeAllViews();
                }
                adCardView.addView(adView);
                break;
            default:
                ItemRadio objAllBean = (ItemRadio) arrayList.get(position);
                Boolean isFav = checkFav(position);

                MyViewHolder myViewHolder = (MyViewHolder) holder;

                try {
                    if (Constants.playTypeRadio && Constants.isPlaying && PlayService.getInstance().getPlayingRadioStation().getRadioId().equals(objAllBean.getRadioId())) {
                        myViewHolder.cv_home.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_playing));
                    } else {
                        myViewHolder.cv_home.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isFav) {
                    myViewHolder.imageView_fav.setImageResource(R.drawable.fav);
                } else {
                    myViewHolder.imageView_fav.setImageResource(R.drawable.unfav);
                }

                myViewHolder.textView_radio.setText(objAllBean.getRadioName());
                myViewHolder.textView_freq.setText(objAllBean.getRadioFreq());

                Picasso.get()
                        .load(methods.getImageThumbSize(objAllBean.getRadioImageurl().replace(" ", "%20"),context.getString(R.string.home)))
                        .placeholder(R.drawable.placeholder)
                        .into(myViewHolder.imageView);

                myViewHolder.imageView_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dbHelper.addORremoveFav((ItemRadio) arrayList.get(holder.getAdapterPosition()))) {
                            myViewHolder.imageView_fav.setImageResource(R.drawable.fav);
                            methods.showToast(context.getString(R.string.add_to_fav));
                        } else {
                            myViewHolder.imageView_fav.setImageResource(R.drawable.unfav);
                            methods.showToast(context.getString(R.string.remove_from_fav));
                        }
                    }
                });

                myViewHolder.cv_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methods.showInter(holder.getAdapterPosition(), "");
                        methods.showNativeAd();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else{
            return Constants.ITEM_RADIO_HOME;
        }
    }

    private Boolean checkFav(int pos) {
        return dbHelper.checkFav((ItemRadio) arrayList.get(pos));
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
                    Object obj = filteredArrayList.get(i);
                    if(obj instanceof ItemRadio){
                        ItemRadio item = (ItemRadio) obj;
                        String nameList = item.getRadioName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(item);
                    }

                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<ItemRadio> filteredItems = new ArrayList<>();
                    for (Object obj:
                            filteredArrayList) {
                        if(obj instanceof ItemRadio){
                            ItemRadio item = (ItemRadio) obj;
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
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constants.arrayList_radio.clear();
            for (Object o: arrayList){
                if(o instanceof ItemRadio){
                    Constants.arrayList_radio.add((ItemRadio) o);
                }
            }
            ((BaseActivity) context).clickPlay(position, true);
        }
    };
}