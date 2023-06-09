package com.radioentertainment.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.radioentertainment.adapter.AdapterCityDetails;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.DBHelper;

import java.util.ArrayList;

public class FragmentFavourite extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private AdapterCityDetails adapterFav;
    private TextView textView_empty;
    private SearchView searchView;
    private ArrayList<ItemRadio> arraylist;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_favorite, container, false);

        dbHelper = new DBHelper(getActivity());
        arraylist = dbHelper.getAllData();
        textView_empty = rootView.findViewById(R.id.textView_empty_fav);

        adapterFav = new AdapterCityDetails(getActivity(), arraylist);
        GridLayoutManager lLayout = new GridLayoutManager(getActivity(), 2);

        recyclerView = rootView.findViewById(R.id.recyclerView_fav);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);
        recyclerView.setAdapter(adapterFav);

        if (arraylist.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            textView_empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.search);
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
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
            if (arraylist.size() > 0) {
                if (searchView.isIconified()) {
                    recyclerView.setAdapter(adapterFav);
                    adapterFav.notifyDataSetChanged();
                } else {
                    adapterFav.getFilter().filter(s);
                    adapterFav.notifyDataSetChanged();
                }
            }
            return true;
        }
    };
}