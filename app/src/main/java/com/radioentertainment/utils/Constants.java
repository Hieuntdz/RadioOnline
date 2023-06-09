package com.radioentertainment.utils;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.radioentertainment.item.ItemAbout;
import com.radioentertainment.item.ItemCity;
import com.radioentertainment.item.ItemLanguage;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.item.ItemUser;
import com.radioentertainment.radio.BuildConfig;

import java.io.Serializable;
import java.util.ArrayList;

public class Constants implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SERVER_URL = BuildConfig.SERVER_URL + "api.php";

    public static final String METHOD_HOME =  "get_home_radio";
    public static final String METHOD_ABOUT = "get_app_details";
    public static final String METHOD_LOGIN = "users_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_PROFILE_UPDATE = "user_profile_update";
    public static final String METHOD_FORGOT_PASS = "forgot_pass";
    public static final String METHOD_SUGGESTION = "add_user_suggest";
    public static final String METHOD_REPORT = "add_report";
    public static final String METHOD_THEMES = "get_themes";

    public static final String METHOD_SINGLE_RADIO = "get_single_radio";
    public static final String METHOD_LATEST = "get_latest_radio";
    public static final String METHOD_CITY = "city_list";
    public static final String METHOD_LANGUAGE = "lang_list";
    public static final String METHOD_ONDEMAND = "get_on_demand_cat_id";
    public static final String METHOD_ONDEMAND_CAT = "on_demand_cat";
    public static final String METHOD_SINGLE_ONDEMAND = "get_on_demand_single";
    public static final String METHOD_SEARCH = "get_search_radio";
    public static final String METHOD_ALL_RADIO = "get_all_radios";
    public static final String METHOD_FEATURED_RADIO = "get_featured_radio";
    public static final String METHOD_RADIO_BY_CITY = "get_radio_by_city_id";
    public static final String METHOD_RADIO_BY_LANGUAGE = "get_radio_by_lang_id";

    public static final String URL_ABOUT_US_LOGO = BuildConfig.SERVER_URL + "images/";

    public static final String TAG_ROOT = "ONLINE_RADIO";

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "msg";

    public static final String TAG_ID = "id";
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_USER_NAME = "name";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PHONE = "phone";

    public static final String TAG_FEATURED = "featured_list";
    public static final String TAG_MOST_VIEWED = "most_view_list";
    public static final String TAG_ON_DEMAND_CAT_LIST = "ondemand_cat_list";
    public static final String RADIO_ID = "id";
    public static final String RADIO_NAME = "radio_name";
    public static final String RADIO_IMAGE_BIG = "video_thumbnail_b";
    public static final String RADIO_IMAGE_SMALL = "video_thumbnail_s";
    public static final String RADIO_IMAGE = "radio_url";
    public static final String RADIO_FREQ = "radio_frequency";
    public static final String RADIO_VIEWS = "views";
    public static final String RADIO_LANG = "language_name";
    public static final String RADIO_DESC = "radio_description";

    public static final String TAG_ONDEMAND_TITLE = "mp3_title";
    public static final String TAG_ONDEMAND_URL = "mp3_url";
    public static final String TAG_ONDEMAND_IMAGE = "mp3_thumbnail_b";
    public static final String TAG_ONDEMAND_IMAGE_THUMB = "mp3_thumbnail_s";
    public static final String TAG_ONDEMAND_DURATION = "mp3_duration";
    public static final String TAG_ONDEMAND_DESCRIPTION = "mp3_description";
    public static final String TAG_ONDEMAND_TOTAL_VIEWS = "total_views";

    public static final String TAG_GRADIENT_1 = "gradient_color1";
    public static final String TAG_GRADIENT_2 = "gradient_color2";

    public static final String CITY_NAME = "city_name";
    public static final String CITY_CID = "cid";
    public static final String LANG_NAME = "language_name";
    public static final String LANG_ID = "lid";
    public static final String CITY_TAGLINE = "city_tagline";

    public static final String CAT_ID = "cid";
    public static final String CAT_NAME = "category_name";
    public static final String CAT_IMAGE = "category_image";
    public static final String CAT_THUMB = "category_image_thumb";
    public static final String TOTAL_ITEMS = "total_items";
    public static final int ITEM_PER_AD = 5;
    public static final int ITEM_PER_AD_GRID = 8;
    public static final int BANNER_SHOW_LIMIT = 99999;
    public static final int ITEM_LANGUAGE = 0;
    public static final int ITEM_BANNER_AD = 575;
    public static final int ITEM_CITY = 130;
    public static final int ITEM_RADIO_HOME = 774;

    public static ItemCity itemCity;
    public static ItemLanguage itemLanguage;
    public static String song_name;

    public static int columnWidth = 0;
    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 2;
    // Gridview image padding
    public static final int GRID_PADDING = 7; // in dp
    public static ItemAbout itemAbout;
    public static SimpleExoPlayer exoPlayer_Radio;
    public static String fragment = "Home", pushRID = "0";
    public static ItemUser itemUser;
    public static Boolean isPlaying = false;
    public static Boolean isLogged = false, isUpdate = false, isBannerAd = true, isInterAd = true, isThemeChanged = true, playTypeRadio = true, isAppOpen = false, isQuitDialog = false;

    public static ItemRadio itemRadio;
    public static ArrayList<ItemRadio> arrayList_radio = new ArrayList<>();
    public static int pos = 0;
    public static String search_text = "";

    public static String packageName = "";
    public static String fb_url = "";
    public static String twitter_url = "";
    public static String ad_publisher_id = "";
    public static String ad_banner_id = "";
    public static String ad_inter_id = "";

    public static int dialogCount = 0;
    public static int adCount = 0;
    public static int adShow = 1;
    public static int adNativeCount = 3;
    public static int adBannerShow = 0;
    public static int nativeAdsRequestCount = 0;
}