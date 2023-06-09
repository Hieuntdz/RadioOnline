package com.radioentertainment.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;

import com.radioentertainment.item.ItemUser;
import com.radioentertainment.radio.R;

public class SharedPref {

    private Methods methods;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static String TAG_IS_RATE = "israte", TAG_UID = "uid" ,TAG_USERNAME = "name", TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_REMEMBER = "rem",
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin", IS_PREMIUM = "is_premium";

    public SharedPref(Context context) {
        this.context = context;
        methods = new Methods(context, false);
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setIsRate(Boolean flag) {
        editor.putBoolean(TAG_IS_RATE, flag);
        editor.apply();
    }

    public Boolean getIsRate() {
        return sharedPreferences.getBoolean(TAG_IS_RATE, true);
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean("firstopen", flag);
        editor.apply();
    }

    public Boolean getIsPremium(){
        return sharedPreferences.getBoolean(IS_PREMIUM, false);
    }

    public void setIsPremium(boolean is_premium){
        editor.putBoolean(IS_PREMIUM, is_premium);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean("firstopen", true);
    }

    public void setLoginDetails(ItemUser itemUser, Boolean isRemember, String password) {
        editor.putString(TAG_UID, methods.encrypt(itemUser.getId()));
        editor.putString(TAG_USERNAME, methods.encrypt(itemUser.getName()));
        editor.putString(TAG_MOBILE, methods.encrypt(itemUser.getMobile()));
        editor.putString(TAG_EMAIL, methods.encrypt(itemUser.getEmail()));
        editor.putString(TAG_PASSWORD, methods.encrypt(password));
        editor.apply();
    }

    public void setRemeber(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public void getUserDetails() {
        Constants.itemUser = new ItemUser(methods.decrypt(sharedPreferences.getString(TAG_UID,"")), methods.decrypt(sharedPreferences.getString(TAG_USERNAME,"")), methods.decrypt(sharedPreferences.getString(TAG_EMAIL,"")), methods.decrypt(sharedPreferences.getString(TAG_MOBILE,"")));
    }

    public String getEmail() {
        return methods.decrypt(sharedPreferences.getString(TAG_EMAIL,""));
    }

    public String getPassword() {
        return methods.decrypt(sharedPreferences.getString(TAG_PASSWORD,""));
    }

    public Boolean getIsRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public void setThemeColors(int first, int second) {
        editor.putInt("first", first);
        editor.putInt("second", second);
        editor.apply();
    }

    public int getFirstColor () {
        return sharedPreferences.getInt("first", ContextCompat.getColor(context, R.color.colorPrimaryDark));
    }

    public int getSecondColor () {
        return sharedPreferences.getInt("second",ContextCompat.getColor(context, R.color.colorPrimary));
    }

    public void setCheckSleepTime() {
        if(getSleepTime() <= System.currentTimeMillis()) {
            setSleepTime(false, 0, 0);
        }
    }

    public void setSleepTime(Boolean isTimerOn, long sleepTime, int id) {
        editor.putBoolean("isTimerOn", isTimerOn);
        editor.putLong("sleepTime", sleepTime);
        editor.putInt("sleepTimeID", id);
        editor.apply();
    }

    public Boolean getIsSleepTimeOn () {
        return sharedPreferences.getBoolean("isTimerOn",false);
    }

    public long getSleepTime () {
        return sharedPreferences.getLong("sleepTime",0);
    }

    public int getSleepID () {
        return sharedPreferences.getInt("sleepTimeID",0);
    }

    public Boolean getIsAutoLogin() {
        return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false);
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }
}