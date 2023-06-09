package com.radioentertainment.interfaces;

import com.radioentertainment.item.ItemLanguage;

import java.util.ArrayList;

public interface LanguageListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemLanguage> arrayListLanguage);
}