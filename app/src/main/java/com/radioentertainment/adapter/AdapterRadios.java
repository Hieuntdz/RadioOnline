package com.radioentertainment.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.radioentertainment.radio.BaseActivity;
import com.radioentertainment.radio.R;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.DBHelper;
import com.radioentertainment.utils.Methods;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class AdapterRadios extends RecyclerView.Adapter {

    private DBHelper dbHelper;
    private Context context;
    private ArrayList<Object> arraylist;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private Methods methods;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public AdapterRadios(Context context, ArrayList<Object> list) {
        this.context = context;
        this.arraylist = list;
        this.filteredArrayList = list;
        dbHelper = new DBHelper(context);


        methods = new Methods(context, interAdListener);
        Resources r = context.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GRID_PADDING, r.getDisplayMetrics());
        Constants.columnWidth = (int) ((methods.getScreenWidth() - ((Constants.NUM_OF_COLUMNS + 1) * padding)) / Constants.NUM_OF_COLUMNS);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_title, textView_views, textView_lang;
        private ImageView imageView_fav, imageView;
        private CardView cardView;

        private MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.row_layout1);
            textView_views = view.findViewById(R.id.textView_view1);
            textView_lang = view.findViewById(R.id.textView_list_lang1);
            textView_title = view.findViewById(R.id.textView_radio_name1);
            imageView = view.findViewById(R.id.row_logo1);
            imageView_fav = view.findViewById(R.id.imageView_fav);
        }
    }

    class BannerAdsViewHolder extends RecyclerView.ViewHolder{

        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static CircularProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_ITEM:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cityradio_list1, parent, false);
                return new MyViewHolder(itemView);
            case Constants.ITEM_BANNER_AD:
                View bannerAdView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_ad_containter, parent, false);
                return new BannerAdsViewHolder(bannerAdView);
            default:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
                return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) viewHolder;
                AdView adView = (AdView) arraylist.get(position);
                ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;


                if(adCardView.getChildCount()>0){
                    adCardView.removeAllViews();
                }
                if(adCardView.getParent() != null){
                    ((ViewGroup)adView.getParent()).removeView(adView);
                }
                adCardView.addView(adView);
                break;
            case VIEW_ITEM:
                final MyViewHolder holder = (MyViewHolder) viewHolder;
                Boolean isFav = checkFav(position);

//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constants.columnWidth, Constants.columnWidth);
//                params.setMargins(0, 0, 0, 20);
//                holder.cardView.setLayoutParams(params);
                if (isFav) {
                    holder.imageView_fav.setImageResource(R.drawable.fav);
                } else {
                    holder.imageView_fav.setImageResource(R.drawable.unfav);
                }

                ItemRadio item = (ItemRadio) arraylist.get(position);

                holder.textView_views.setText(Methods.format(Double.parseDouble(item.getViews())));
                holder.textView_title.setText(item.getRadioName());
                holder.textView_lang.setText(item.getLanguage());

                ItemRadio itemPosition = (ItemRadio) arraylist.get(holder.getAdapterPosition());

                Picasso.get()
                        .load(methods.getImageThumbSize(itemPosition.getRadioImageurl(), ""))
                        .into(holder.imageView);

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methods.showInter(holder.getAdapterPosition(), "");
                    }
                });

                holder.imageView_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dbHelper.addORremoveFav(itemPosition)) {
                            holder.imageView_fav.setImageResource(R.drawable.fav);
                            methods.showToast(context.getString(R.string.add_to_fav));
                        } else {
                            holder.imageView_fav.setImageResource(R.drawable.unfav);
                            methods.showToast(context.getString(R.string.remove_from_fav));
                        }
                    }
                });
                break;
            default:
                if (getItemCount() < 15) {
                    ProgressViewHolder.progressBar.setVisibility(View.GONE);
                }
                break;
        }
    }

    private Boolean checkFav(int pos) {
        return dbHelper.checkFav((ItemRadio) arraylist.get(pos));
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arraylist.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(arraylist.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else{
            if(position < arraylist.size()) {
                return VIEW_ITEM;
            } else {
                return VIEW_PROG;
            }
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
                        ItemRadio itemRadio = (ItemRadio) obj;
                        String nameList = itemRadio.getRadioName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(itemRadio);
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
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arraylist = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constants.arrayList_radio.clear();
            for (Object o : arraylist){
                if(o instanceof ItemRadio){
                    Constants.arrayList_radio.add((ItemRadio) o);
                }
            }
            ((BaseActivity) context).clickPlay(position, true);
        }
    };
}