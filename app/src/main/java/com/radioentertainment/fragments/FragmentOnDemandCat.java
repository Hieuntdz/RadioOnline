package com.radioentertainment.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radioentertainment.adapter.AdapterOnDemandCat;
import com.radioentertainment.asyncTasks.LoadOnDemandCat;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.interfaces.OnDemandCatListener;
import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.RecyclerItemClickListener;
import com.radioentertainment.utils.SharedPref;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentOnDemandCat extends Fragment {

    private RecyclerView recyclerView;
    private AdapterOnDemandCat adapter;
    private SearchView searchView;
    private ArrayList<ItemOnDemandCat> arraylist;
    private CircularProgressBar progressBar;
    private TextView textView_empty;
    public static AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    SharedPref sharedPref;
    Methods methods;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_ondemand_cat, container, false);

        methods = new Methods(getActivity(), interAdListener);
        sharedPref = new SharedPref(getActivity());

        arraylist = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.progressBar_on);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        ViewCompat.setBackgroundTintList(button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));

        GridLayoutManager lLayout = new GridLayoutManager(getActivity(),2, RecyclerView.VERTICAL, false);

        recyclerView = rootView.findViewById(R.id.recyclerView_on);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);

        loadCity();

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCity();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, "");
            }
        }));

        setHasOptionsMenu(true);
        return rootView;
    }

    private void loadCity() {
        if (methods.isConnectingToInternet()) {
            LoadOnDemandCat loadOnDemandCat = new LoadOnDemandCat(new OnDemandCatListener() {
                @Override
                public void onStart() {
                    arraylist.clear();
                    ll_empty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemOnDemandCat> arrayListOnDemandCat) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arraylist.addAll(arrayListOnDemandCat);
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
            }, methods.getAPIRequest(Constants.METHOD_ONDEMAND_CAT, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadOnDemandCat.execute();
        } else {
            errr_msg = getString(R.string.internet_not_connected);
            setEmpty();
        }
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
                if (!searchView.isIconified()) {
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();
                }
            }
            return true;
        }
    };

    public void setAdapter() {
        adapter = new AdapterOnDemandCat(getActivity(), arraylist);
        recyclerView.setAdapter(adapter);
        setEmpty();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arraylist.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            int pos = getPosition(adapter.getID(position));
            FragmentManager fm = getFragmentManager();
            FragmentOnDemandDetails f1 = new FragmentOnDemandDetails(false);
            FragmentTransaction ft = fm.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", arraylist.get(pos));
            f1.setArguments(bundle);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.hide(fm.findFragmentByTag(getString(R.string.on_demand)));
            ft.add(R.id.content_frame_activity, f1, arraylist.get(pos).getName());
            ft.addToBackStack(arraylist.get(pos).getName());
            ft.commit();
        }
    };

    private int getPosition(String id) {
        int count = 0;
        for (int i = 0; i < arraylist.size(); i++) {
            if (id.equals(arraylist.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }
}