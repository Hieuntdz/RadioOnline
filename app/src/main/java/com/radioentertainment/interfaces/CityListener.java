package com.radioentertainment.interfaces;

import com.radioentertainment.item.ItemCity;

import java.util.ArrayList;

public interface CityListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCity> arrayListCity);
}