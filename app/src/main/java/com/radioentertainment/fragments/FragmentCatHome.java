package com.radioentertainment.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radioentertainment.adapter.AdapterRadioList;
import com.radioentertainment.asyncTasks.LoadHome;
import com.radioentertainment.asyncTasks.LoadRadioList;
import com.radioentertainment.interfaces.HomeListener;
import com.radioentertainment.interfaces.RadioListListener;
import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.radio.BaseActivity;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentCatHome extends Fragment {

    private ArrayList<Object> arrayList;
    private AdapterRadioList adapterRadioList;
    private RecyclerView recyclerView;
    private LinearLayout ll_empty;
    private SearchView searchView;
    private Methods methods;
    private CircularProgressBar progressBar;
    private TextView textView_empty;
    public static AppCompatButton button_try;
    private SharedPref sharedPref;
    private String type;

    private View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            type = bundle.getString("type");
        }

        sharedPref = new SharedPref(getActivity());
        methods = new Methods(getActivity());
        progressBar = rootView.findViewById(R.id.progressBar_cat);
        button_try = rootView.findViewById(R.id.button_empty_try);
        ll_empty = rootView.findViewById(R.id.ll_empty);
        ViewCompat.setBackgroundTintList(button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));

        setHasOptionsMenu(true);

        arrayList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(arrayList.get(position) instanceof AdView){
                    return 2;
                }else{
                    return 1;
                }
            }
        });
        recyclerView = rootView.findViewById(R.id.recyclerView_cat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        switch (type){
            case "featured":
                loadFeature("featured");

                button_try.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadFeature("featured");
                    }
                });
                break;
            case "trending":
                loadFeature("trending");

                button_try.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadFeature("trending");
                    }
                });
                break;
            case "latest":
                loadLatest();

                button_try.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadLatest();
                    }
                });
                break;
        }
        return rootView;
    }

    private void getBannerAds(){
        for (int i = Constants.ITEM_PER_AD_GRID; i < arrayList.size(); i += Constants.ITEM_PER_AD_GRID+1){
            if(Constants.adBannerShow++ < Constants.BANNER_SHOW_LIMIT){
                final AdView adView = new AdView(getContext());
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                adView.loadAd(new AdRequest.Builder().build());
                arrayList.add(i, adView);
            }
        }
    }

    private void loadLatest() {
        if (methods.isConnectingToInternet()) {
            LoadRadioList loadRadioList = new LoadRadioList(new RadioListListener() {
                @Override
                public void onStart() {
                    if (getActivity() != null) {
                        arrayList.clear();
                        ll_empty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListRadio);
                                getBannerAds();
                                if (arrayList.size() > 0) {
                                    adapterRadioList = new AdapterRadioList(getActivity(), arrayList, "grid");
                                    recyclerView.setAdapter(adapterRadioList);
                                    if (Constants.arrayList_radio.size() == 0) {
                                        for (Object o : arrayList){
                                            if(o instanceof ItemRadio){
                                                Constants.arrayList_radio.add((ItemRadio) o);
                                            }
                                        }
                                        ((BaseActivity) getActivity()).changeText(Constants.arrayList_radio.get(0));
                                    }
                                }
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        setEmpty();
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_LATEST, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadRadioList.execute();
        } else {
            methods.showToast(getString(R.string.internet_not_connected));
        }
    }

    private void loadFeature(final String type) {
        LoadHome loadHome = new LoadHome(new HomeListener() {
            @Override
            public void onStart() {
                if (getActivity() != null) {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd(String success, ArrayList<ItemRadio> arrayList_featured, ArrayList<ItemRadio> arrayList_mostviewed, ArrayList<ItemOnDemandCat> arrayList_ondemand_cat) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        switch (type){
                            case "featured":
                                arrayList.addAll(arrayList_featured);
                                break;
                            case "trending":
                                arrayList.addAll(arrayList_mostviewed);
                                break;
                        }
                        getBannerAds();
                        if (arrayList.size() > 0) {
                            adapterRadioList = new AdapterRadioList(getActivity(), arrayList, "grid");
                            recyclerView.setAdapter(adapterRadioList);
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                    }
                    progressBar.setVisibility(View.GONE);
                    setEmpty();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_HOME, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadHome.execute();
    }

    private void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText("No Item Found");
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        //MenuItem item = menu.findItem(R.id.search);
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (arrayList.size() > 0) {
                if (!searchView.isIconified()) {
                    adapterRadioList.getFilter().filter(s);
                    adapterRadioList.notifyDataSetChanged();
                }
            }
            return true;
        }
    };
}
