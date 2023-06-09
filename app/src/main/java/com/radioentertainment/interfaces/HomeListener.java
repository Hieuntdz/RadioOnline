package com.radioentertainment.interfaces;

import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.item.ItemRadio;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemRadio> arrayListFeatured, ArrayList<ItemRadio> arrayListMostViewed, ArrayList<ItemOnDemandCat> arrayListOnDemandCat);
}