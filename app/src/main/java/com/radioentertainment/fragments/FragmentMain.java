package com.radioentertainment.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;

public class FragmentMain extends Fragment {

    SharedPref sharedPref;
    public static AppBarLayout appBarLayout;
    SearchView searchView;
    Methods methods;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            FragmentHome f1 = new FragmentHome();
        ft.add(R.id.frame_content_home, f1, getString(R.string.home));
        ft.addToBackStack(getString(R.string.home));
        ft.commit();

        return rootView;
    }

}