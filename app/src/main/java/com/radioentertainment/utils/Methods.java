package com.radioentertainment.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.radioentertainment.interfaces.BackInterAdListener;
import com.radioentertainment.radio.BaseActivity;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.item.ItemUser;
import com.radioentertainment.radio.BuildConfig;
import com.radioentertainment.radio.LoginActivity;
import com.radioentertainment.radio.R;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.Options;
import com.yakivmospan.scytale.Store;

import java.io.File;
import java.text.DecimalFormat;
import java.util.PrimitiveIterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import androidx.appcompat.app.AlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Methods {

    private Context context;
    private InterAdListener interAdListener;
    private BackInterAdListener backInterAdListener;
    private SharedPref sharedPref;
    private boolean isClicked = false;
    private SecretKey key;
    private TemplateView NativeAdsView;

    public Methods(Context context, Boolean flag) {
        this.context = context;

        Store store = new Store(context);
        if (!store.hasKey(BuildConfig.API_KEY)) {
            key = store.generateSymmetricKey(BuildConfig.API_KEY, null);
        } else {
            key = store.getSymmetricKey(BuildConfig.API_KEY, null);
        }
    }

    // constructor
    public Methods(Context context) {
        this.context = context;
        sharedPref = new SharedPref(context);
    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        sharedPref = new SharedPref(context);
        this.interAdListener = interAdListener;
    }

    public Methods(Context context, BackInterAdListener backInterAdListener) {
        this.context = context;
        sharedPref = new SharedPref(context);
        this.backInterAdListener = backInterAdListener;
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public void clickLogin() {
        if (Constants.isLogged) {
            logout((Activity) context);
            showToast(context.getString(R.string.logout_success));
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("from", "app");
            context.startActivity(intent);
        }
    }

    public void logout(Activity activity) {
        Constants.itemUser = new ItemUser("", "", "", "");
        SharedPref sharedPref = new SharedPref(context);
        sharedPref.setLoginDetails(Constants.itemUser, false, "");
        Constants.isLogged = false;
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        context.startActivity(intent1);
        activity.finish();
    }

    public static String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public String getImageThumbSize(String imagePath, String type) {
        if (type.equals(context.getString(R.string.player))) {
            imagePath = imagePath.replace("&size=300x300", "&size=200x200");
        } else if(type.equals(context.getString(R.string.on_demand))) {
            imagePath = imagePath.replace("&size=300x300", "&size=300x150");
        } else if(type.equals(context.getString(R.string.home))) {
            imagePath = imagePath.replace("&size=300x300", "&size=200x200");
        }
        return imagePath;
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    private void showPersonalizedAds(LinearLayout linearLayout) {
        if (Constants.isBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder().build();
            //adView.setAdUnitId(Constants.ad_banner_id);
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            adView.setAdSize(AdSize.SMART_BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        }
    }

    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        if (Constants.isBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            //adView.setAdUnitId(Constants.ad_banner_id);
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            adView.setAdSize(AdSize.SMART_BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        }
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isConnectingToInternet() && !sharedPref.getIsPremium()) {
            if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                showNonPersonalizedAds(linearLayout);
            } else {
                showPersonalizedAds(linearLayout);
            }
        }
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationBannerAdSizeWithWidth(context, adWidth);
    }

    public void showNativeAd(){

        if(sharedPref.getIsPremium()){
            return;
        }

        NativeAdsView = ((BaseActivity) context).findViewById(R.id.my_template_native_ads);

        if(Constants.adNativeCount++ % 3 == 0){
            if(Constants.nativeAdsRequestCount++ < 5){

                AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd nativeAd) {
                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder().withMainBackgroundColor(new ColorDrawable(Color.WHITE)).build();
                                NativeAdsView.setStyles(styles);
                                NativeAdsView.setNativeAd(nativeAd);
                                NativeAdsView.setVisibility(View.VISIBLE);
                            }
                        })
                        .withAdListener(new AdListener(){
                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                NativeAdsView.setVisibility(View.GONE);
                                super.onAdFailedToLoad(loadAdError);
                            }
                        })
                        .build();
                if(adLoader != null){
                    adLoader.loadAd(new AdRequest.Builder().build());
                }
            }else {
                NativeAdsView.setVisibility(View.GONE);
            }
        }else {
            NativeAdsView.setVisibility(View.GONE);
        }
    }

    public void showInter(final int pos, final String type) {
        if (Constants.isInterAd && !sharedPref.getIsPremium()) {
            Constants.adCount = Constants.adCount + 1;
            if (Constants.adCount % Constants.adShow == 0) {
                isClicked = false;

                final InterstitialAd interstitialAd = new InterstitialAd(context);
                AdRequest adRequest;
                if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.PERSONALIZED) {
                    adRequest = new AdRequest.Builder()
                            .build();
                } else {
                    Bundle extras = new Bundle();
                    extras.putString("npa", "1");
                    adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .build();
                }
                //interstitialAd.setAdUnitId(Constants.ad_inter_id);
                interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
                interstitialAd.loadAd(adRequest);
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (!isClicked) {
                                isClicked = true;
                                interstitialAd.show();
                            }
                        }

                    public void onAdClosed() {
                        if(type.equals("BackPress")){
                            backInterAdListener.onClick();
                        }else {
                            interAdListener.onClick(pos, type);
                        }
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        if (!isClicked) {
                            isClicked = true;

                            if(type.equals("BackPress")){
                                backInterAdListener.onClick();
                            }else {
                                interAdListener.onClick(pos, type);
                            }
                        }
                        super.onAdFailedToLoad(loadAdError);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isClicked) {
                            isClicked = true;

                            if(type.equals("BackPress")){
                                backInterAdListener.onClick();
                            }else {
                                interAdListener.onClick(pos, type);
                            }
                        }
                    }
                }, 2000);
            } else {
                showRateDialog();

                if(type.equals("BackPress")){
                    backInterAdListener.onClick();
                }else {
                    interAdListener.onClick(pos, type);
                }
            }
        } else {
            showRateDialog();

            if(type.equals("BackPress")){
                backInterAdListener.onClick();
            }else {
                interAdListener.onClick(pos, type);
            }
        }
    }

    public void showRateDialog() {
        Constants.dialogCount = Constants.dialogCount + 1;
        if (Constants.dialogCount % 4 == 0) {
            final SharedPref sharedPref = new SharedPref(context);

            if (sharedPref.getIsRate()) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme_RED);
                alertDialog.setTitle(context.getString(R.string.rate_this_app));
                alertDialog.setMessage(context.getString(R.string.rate_this_app_message));
                alertDialog.setPositiveButton(context.getString(R.string.rate_it_now), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.setIsRate(false);
                        final String appName = context.getPackageName();//your application package name i.e play store application url
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id="
                                            + appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id="
                                            + appName)));
                        }
                    }
                });
                alertDialog.setNegativeButton(context.getString(R.string.remind_me_later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        }
    }

    public GradientDrawable getRoundDrawable(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(10);
        return gd;
    }

    public GradientDrawable getRoundDrawableRadis(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(radius);
        return gd;
    }

    public GradientDrawable getGradientDrawable(int first, int second) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColors(new int[]{
                first,
                second
        });
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.BL_TR);
        gd.mutate();
        gd.setCornerRadius(10);
        return gd;
    }

    public GradientDrawable getGradientDrawableToolbar() {

        GradientDrawable gd = new GradientDrawable();
        gd.setColors(new int[]{
                sharedPref.getFirstColor(),
                sharedPref.getSecondColor()
        });
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.BL_TR);
        return gd;
    }


    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String hourString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        long temp_milli = (long) calculateTime(Constants.arrayList_radio.get(Constants.pos).getDuration());
        int temp_hour = (int) (temp_milli / (1000 * 60 * 60));
        if (temp_hour != 0) {
            hourString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        // Prepending 0 to minutes if it is one digit
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        finalTimerString = hourString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static String milliSecondsToTimerDownload(long milliseconds) {
        String finalTimerString = "";
        String hourString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there

        if (hours != 0) {
            hourString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        // Prepending 0 to minutes if it is one digit
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        finalTimerString = hourString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public static long getSeekFromPercentage(int percentage, long totalDuration) {

        long currentSeconds = 0;
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        currentSeconds = (percentage * totalSeconds) / 100;

        // return percentage
        return currentSeconds * 1000;
    }

    public static int calculateTime(String duration) {
        int time = 0, min, sec, hr = 0;
        try {
            StringTokenizer st = new StringTokenizer(duration, ".");
            if (st.countTokens() == 3) {
                hr = Integer.parseInt(st.nextToken());
            }
            min = Integer.parseInt(st.nextToken());
            sec = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
            StringTokenizer st = new StringTokenizer(duration, ":");
            if (st.countTokens() == 3) {
                hr = Integer.parseInt(st.nextToken());
            }
            min = Integer.parseInt(st.nextToken());
            sec = Integer.parseInt(st.nextToken());
        }
        time = ((hr * 3600) + (min * 60) + sec) * 1000;
        return time;
    }

    public long convertToMili(String s) {

        long ms = 0;
        Pattern p;
        if (s.contains(("\\:"))) {
            p = Pattern.compile("(\\d+):(\\d+)");
        } else {
            p = Pattern.compile("(\\d+).(\\d+)");
        }
        Matcher m = p.matcher(s);
        if (m.matches()) {
            int h = Integer.parseInt(m.group(1));
            int min = Integer.parseInt(m.group(2));
            // int sec = Integer.parseInt(m.group(2));
            ms = (long) h * 60 * 60 * 1000 + min * 60 * 1000;
        }
        return ms;
    }

    public void getPosition(Boolean isNext) {
        if (isNext) {
            if (Constants.pos != Constants.arrayList_radio.size() - 1) {
                Constants.pos = Constants.pos + 1;
            } else {
                Constants.pos = 0;
            }
        } else {
            if (Constants.pos != 0) {
                Constants.pos = Constants.pos - 1;
            } else {
                Constants.pos = Constants.arrayList_radio.size() - 1;
            }
        }
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getPathImage(Uri uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            } else {

                if (uri == null) {
                    return null;
                }
                // try to retrieve the image from the media store first
                // this will only work for images selected from gallery
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String retunn = cursor.getString(column_index);
                    cursor.close();
                    return retunn;
                }
                // this is our fallback here
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String returnn = cursor.getString(column_index);
                cursor.close();
                return returnn;
            }
            // this is our fallback here
            return uri.getPath();
        }
    }

    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }

    public String encrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.encrypt(value, key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String decrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.decrypt(value, key);
        } catch (Exception e) {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.encrypt("null", key);
        }
    }

    public RequestBody getAPIRequest(String method, int page, String deviceID, String radioID, String searchText, String like, String catID, String type, String email, String password, String name, String phone, String userID, String reportMessage, File file) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", "com.radioentertainment.radio");

        switch (method) {
            case Constants.METHOD_ALL_RADIO:
                jsObj.addProperty("page", page);
                break;
            case Constants.METHOD_PROFILE:
                jsObj.addProperty("id", userID);
                break;
            case Constants.METHOD_PROFILE_UPDATE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;
            case Constants.METHOD_LOGIN:
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                break;
            case Constants.METHOD_REGISTER:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;
            case Constants.METHOD_FORGOT_PASS:
                jsObj.addProperty("user_email", email);
                break;
            case Constants.METHOD_SUGGESTION:
                jsObj.addProperty("title", name);
                jsObj.addProperty("description", searchText);
                break;
            case Constants.METHOD_REPORT:
                jsObj.addProperty("report_user_id", userID);
                jsObj.addProperty("report_email", email);
                jsObj.addProperty("post_id", radioID);
                jsObj.addProperty("report_text", reportMessage);
                jsObj.addProperty("report_type", type);
                break;
            case Constants.METHOD_ONDEMAND:
                jsObj.addProperty("on_demand_cat_id", catID);
                break;
            case Constants.METHOD_SEARCH:
                jsObj.addProperty("search_text", searchText);
                break;
            case Constants.METHOD_RADIO_BY_CITY:
                jsObj.addProperty("city_id", catID);
                jsObj.addProperty("quantity", page);
                break;
            case Constants.METHOD_RADIO_BY_LANGUAGE:
                jsObj.addProperty("lang_id", catID);
                jsObj.addProperty("quantity", page);
                break;
            case Constants.METHOD_SINGLE_RADIO:
                jsObj.addProperty("radio_id", radioID);
                break;
            case Constants.METHOD_SINGLE_ONDEMAND:
                jsObj.addProperty("on_demand_single", radioID);
                break;
        }

        Log.e("aaa - url", API.toBase64(jsObj.toString()));
        if (file != null) {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        }
    }
}