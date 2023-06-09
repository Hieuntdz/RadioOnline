package com.radioentertainment.interfaces;

import com.radioentertainment.item.ItemRadio;

import java.util.ArrayList;

public interface RadioListListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio);
}