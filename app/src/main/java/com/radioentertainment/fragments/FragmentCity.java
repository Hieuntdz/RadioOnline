package com.radioentertainment.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radioentertainment.adapter.AdapterCity;
import com.radioentertainment.asyncTasks.LoadCity;
import com.radioentertainment.interfaces.CityClickListener;
import com.radioentertainment.interfaces.CityListener;
import com.radioentertainment.item.ItemCity;
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


public class FragmentCity extends Fragment {

    private ArrayList<Object> arrayList;
    private RecyclerView recyclerView;
    public static AdapterCity adapterCity;
    private SearchView searchView;
    private Methods methods;
    private CircularProgressBar progressBar;
    private Boolean isLoaded = false, isVisible = false;
    private TextView textView_empty;
    public static AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    SharedPref sharedPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        sharedPref = new SharedPref(getActivity());
        methods = new Methods(getActivity());
        progressBar = rootView.findViewById(R.id.progressBar_cat);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        ViewCompat.setBackgroundTintList(button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));

        setHasOptionsMenu(true);

        arrayList = new ArrayList<>();

        adapterCity = new AdapterCity(getActivity(), arrayList, new CityClickListener() {
            @Override
            public void onClick() {
                FragmentManager fm = getFragmentManager();
                FragmentCityDetails f1 = new FragmentCityDetails();
                FragmentTransaction ft = fm.beginTransaction();

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.content_frame_activity, f1, Constants.itemCity.getName());
                ft.addToBackStack(Constants.itemCity.getName());
                ft.commit();
                ((BaseActivity) getActivity()).getSupportActionBar().setTitle(Constants.itemCity.getName());
            }
        });
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

//        if (isVisible && !isLoaded) {
//            loadCity();
//        }

        loadCity();

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCity();
            }
        });

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

    private void loadCity() {
        if (methods.isConnectingToInternet()) {
            LoadCity loadCity = new LoadCity(new CityListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCity> arrayListCity) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListCity);
                                getBannerAds();
                                errr_msg = getString(R.string.items_not_found);
                                setAdapter();
                            } else {
                                errr_msg = message;
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                setEmpty();
                            }
                        } else {
                            errr_msg = getString(R.string.error_server);
                            setEmpty();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_CITY, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadCity.execute();
        } else {
            errr_msg = getString(R.string.internet_not_connected);
            setEmpty();
        }
        isLoaded = true;
    }

    public void setAdapter() {
        recyclerView.setAdapter(adapterCity);
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.search);
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
                    adapterCity.getFilter().filter(s);
                    adapterCity.notifyDataSetChanged();
                }
            }
            return true;
        }
    };

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        isVisible = isVisibleToUser;
//        if (isVisibleToUser && isAdded() && !isLoaded) {
//            loadCity();
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }
}
