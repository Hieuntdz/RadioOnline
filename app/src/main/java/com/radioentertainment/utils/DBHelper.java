package com.radioentertainment.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.radioentertainment.item.ItemAbout;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.radio.BuildConfig;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private Methods methods;
    private static String DB_NAME = "radioapp.db";
    private SQLiteDatabase db;
    private static final String TABLE_QUOTES = "radio";
    private static final String TABLE_ABOUT = "about";

    // Table columns_quotes
    private static final String TAG_ID = "id";
    private static final String TAG_RADIO_ID = "rid";
    private static final String TAG_RADIO_NAME = "rname";
    private static final String TAG_RADIO_URL = "url";
    private static final String TAG_RADIO_FREQ = "freq";
    private static final String TAG_IMAGE_SMALL = "thumb";
    private static final String TAG_IMAGE_BIG = "image";
    private static final String TAG_VIEWS = "views";
    private static final String TAG_CID = "cid";
    private static final String TAG_CNAME = "cname";
    private static final String TAG_DESC = "desc";
    private static final String TAG_LANG = "lang";

    private static final String TAG_ABOUT_NAME = "name";
    private static final String TAG_ABOUT_LOGO = "logo";
    private static final String TAG_ABOUT_VERSION = "version";
    private static final String TAG_ABOUT_AUTHOR = "author";
    private static final String TAG_ABOUT_CONTACT = "contact";
    private static final String TAG_ABOUT_EMAIL = "email";
    private static final String TAG_ABOUT_WEBSITE = "website";
    private static final String TAG_ABOUT_DESC = "desc";
    private static final String TAG_ABOUT_DEVELOPED = "developed";
    private static final String TAG_ABOUT_PRIVACY = "privacy";
    private static final String TAG_ABOUT_PUB_ID = "ad_pub";
    private static final String TAG_ABOUT_BANNER_ID = "ad_banner";
    private static final String TAG_ABOUT_INTER_ID = "ad_inter";
    private static final String TAG_ABOUT_IS_BANNER = "isbanner";
    private static final String TAG_ABOUT_IS_INTER = "isinter";
    private static final String TAG_ABOUT_CLICK = "click";
    private static final String TAG_ABOUT_FB = "fb";
    private static final String TAG_ABOUT_TWITTER = "twt";

    // Creating table query
    private static final String CREATE_TABLE_RADIO = "create table " + TABLE_QUOTES + "(" + TAG_ID
            + " TEXT PRIMARY KEY, " + TAG_RADIO_ID + " TEXT, " + TAG_RADIO_NAME + " TEXT, " + TAG_RADIO_URL + " TEXT" +
            ", " + TAG_RADIO_FREQ + " TEXT, " + TAG_IMAGE_SMALL + " TEXT, " + TAG_IMAGE_BIG + " TEXT, " + TAG_VIEWS + " TEXT" +
            ", " + TAG_CID + " TEXT, " + TAG_CNAME + " TEXT, " + TAG_DESC + " TEXT, " + TAG_LANG + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_ABOUT = "create table " + TABLE_ABOUT + "(" + TAG_ABOUT_NAME
            + " TEXT, " + TAG_ABOUT_LOGO + " TEXT, " + TAG_ABOUT_VERSION + " TEXT, " + TAG_ABOUT_AUTHOR + " TEXT" +
            ", " + TAG_ABOUT_CONTACT + " TEXT, " + TAG_ABOUT_EMAIL + " TEXT, " + TAG_ABOUT_WEBSITE + " TEXT, " + TAG_ABOUT_DESC + " TEXT" +
            ", " + TAG_ABOUT_DEVELOPED + " TEXT, " + TAG_ABOUT_PRIVACY + " TEXT, " + TAG_ABOUT_PUB_ID + " TEXT, " + TAG_ABOUT_BANNER_ID + " TEXT" +
            ", " + TAG_ABOUT_INTER_ID + " TEXT, " + TAG_ABOUT_IS_BANNER + " TEXT, " + TAG_ABOUT_IS_INTER + " TEXT, " +  TAG_ABOUT_CLICK + " TEXT " +
            ", " + TAG_ABOUT_FB + " TEXT, " + TAG_ABOUT_TWITTER + " TEXT);";

    private String[] columns_about = new String[]{TAG_ABOUT_NAME, TAG_ABOUT_LOGO, TAG_ABOUT_VERSION, TAG_ABOUT_AUTHOR,
            TAG_ABOUT_CONTACT, TAG_ABOUT_EMAIL, TAG_ABOUT_WEBSITE, TAG_ABOUT_DESC, TAG_ABOUT_DEVELOPED, TAG_ABOUT_PRIVACY,
            TAG_ABOUT_PUB_ID, TAG_ABOUT_BANNER_ID, TAG_ABOUT_INTER_ID, TAG_ABOUT_IS_BANNER, TAG_ABOUT_IS_INTER, TAG_ABOUT_CLICK, TAG_ABOUT_FB, TAG_ABOUT_TWITTER};

    private String[] columns_quotes= new String[]{TAG_ID, TAG_RADIO_ID, TAG_RADIO_NAME, TAG_RADIO_URL, TAG_RADIO_FREQ, TAG_IMAGE_SMALL,
            TAG_IMAGE_BIG, TAG_VIEWS, TAG_CID, TAG_CNAME, TAG_DESC, TAG_LANG};

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        db = getWritableDatabase();
        methods = new Methods(context, false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_RADIO);
            db.execSQL(CREATE_TABLE_ABOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean checkFav(ItemRadio itemRadio) {
        Cursor cursor = db.query(TABLE_QUOTES, columns_quotes, TAG_RADIO_ID + "=" + itemRadio.getRadioId(), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    public Boolean addORremoveFav(ItemRadio itemRadio) {
        if(checkFav(itemRadio)) {
            db.delete(TABLE_QUOTES, TAG_RADIO_ID + "=" + itemRadio.getRadioId(), null);
            return false;
        } else {
            String imageBig = "", imageSmall = "";

            if (itemRadio.getRadioImageurl() != null) {
                imageBig = methods.encrypt(itemRadio.getRadioImageurl().replace(BuildConfig.SERVER_URL, ""));
                imageSmall = methods.encrypt(itemRadio.getImageThumb().replace(BuildConfig.SERVER_URL, ""));
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_RADIO_ID, itemRadio.getRadioId());
            contentValues.put(TAG_RADIO_NAME, itemRadio.getRadioName());
            contentValues.put(TAG_RADIO_URL, itemRadio.getRadiourl());
            contentValues.put(TAG_RADIO_FREQ, itemRadio.getRadioFreq());
            contentValues.put(TAG_IMAGE_SMALL, imageSmall);
            contentValues.put(TAG_IMAGE_BIG, imageBig);
            contentValues.put(TAG_VIEWS, itemRadio.getViews());
            contentValues.put(TAG_CID, itemRadio.getCityId());
            contentValues.put(TAG_CNAME, itemRadio.getCityName());
            contentValues.put(TAG_DESC, itemRadio.getDescription());
            contentValues.put(TAG_LANG, itemRadio.getLanguage());

            db.insert(TABLE_QUOTES, null, contentValues);
            return true;
        }
    }

    public ArrayList<ItemRadio> getAllData() {
        ArrayList<ItemRadio> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_QUOTES, columns_quotes, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    String rid = cursor.getString(cursor.getColumnIndex(TAG_RADIO_ID));
                    String rname = cursor.getString(cursor.getColumnIndex(TAG_RADIO_NAME));
                    String url = cursor.getString(cursor.getColumnIndex(TAG_RADIO_URL));
                    String freq = cursor.getString(cursor.getColumnIndex(TAG_RADIO_FREQ));
                    String image_big = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));
                    String image_small = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_SMALL)));
                    String views = cursor.getString(cursor.getColumnIndex(TAG_VIEWS));
                    String cid = cursor.getString(cursor.getColumnIndex(TAG_CID));
                    String cname = cursor.getString(cursor.getColumnIndex(TAG_CNAME));
                    String desc = cursor.getString(cursor.getColumnIndex(TAG_DESC));
                    String lang = cursor.getString(cursor.getColumnIndex(TAG_LANG));

                    ItemRadio itemRadio = new ItemRadio(rid, rname, url, freq, image_big, views, cid, cname, lang, desc, "radioentertainment");
                    arrayList.add(itemRadio);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public void addtoAbout() {
        try {
            db.delete(TABLE_ABOUT, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_ABOUT_NAME, Constants.itemAbout.getAppName());
            contentValues.put(TAG_ABOUT_LOGO, Constants.itemAbout.getAppLogo());
            contentValues.put(TAG_ABOUT_VERSION, Constants.itemAbout.getAppVersion());
            contentValues.put(TAG_ABOUT_AUTHOR, Constants.itemAbout.getAuthor());
            contentValues.put(TAG_ABOUT_CONTACT, Constants.itemAbout.getContact());
            contentValues.put(TAG_ABOUT_EMAIL, Constants.itemAbout.getEmail());
            contentValues.put(TAG_ABOUT_WEBSITE, Constants.itemAbout.getWebsite());
            contentValues.put(TAG_ABOUT_DESC, Constants.itemAbout.getAppDesc());
            contentValues.put(TAG_ABOUT_DEVELOPED, Constants.itemAbout.getDevelopedby());
            contentValues.put(TAG_ABOUT_PRIVACY, Constants.itemAbout.getPrivacy());
            contentValues.put(TAG_ABOUT_PUB_ID, Constants.ad_publisher_id);
            contentValues.put(TAG_ABOUT_BANNER_ID, Constants.ad_banner_id);
            contentValues.put(TAG_ABOUT_INTER_ID, Constants.ad_inter_id);
            contentValues.put(TAG_ABOUT_IS_BANNER, Constants.isBannerAd.toString());
            contentValues.put(TAG_ABOUT_IS_INTER, Constants.isInterAd.toString());
            contentValues.put(TAG_ABOUT_CLICK, Constants.adShow);
            contentValues.put(TAG_ABOUT_FB, Constants.fb_url);
            contentValues.put(TAG_ABOUT_WEBSITE, Constants.twitter_url);

            db.insert(TABLE_ABOUT, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getAbout() {
        try {
            Cursor c = db.query(TABLE_ABOUT, columns_about, null, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    String appname = c.getString(c.getColumnIndex(TAG_ABOUT_NAME));
                    String applogo = c.getString(c.getColumnIndex(TAG_ABOUT_LOGO));
                    String desc = c.getString(c.getColumnIndex(TAG_ABOUT_DESC));
                    String appversion = c.getString(c.getColumnIndex(TAG_ABOUT_VERSION));
                    String appauthor = c.getString(c.getColumnIndex(TAG_ABOUT_AUTHOR));
                    String appcontact = c.getString(c.getColumnIndex(TAG_ABOUT_CONTACT));
                    String email = c.getString(c.getColumnIndex(TAG_ABOUT_EMAIL));
                    String website = c.getString(c.getColumnIndex(TAG_ABOUT_WEBSITE));
                    String privacy = c.getString(c.getColumnIndex(TAG_ABOUT_PRIVACY));
                    String developedby = c.getString(c.getColumnIndex(TAG_ABOUT_DEVELOPED));

                    Constants.ad_banner_id = c.getString(c.getColumnIndex(TAG_ABOUT_BANNER_ID));
                    Constants.ad_inter_id = c.getString(c.getColumnIndex(TAG_ABOUT_INTER_ID));
                    Constants.isBannerAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_BANNER)));
                    Constants.isInterAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_INTER)));
                    Constants.ad_publisher_id = c.getString(c.getColumnIndex(TAG_ABOUT_PUB_ID));
                    Constants.adShow = Integer.parseInt(c.getString(c.getColumnIndex(TAG_ABOUT_CLICK)));
                    Constants.fb_url = c.getString(c.getColumnIndex(TAG_ABOUT_FB));
                    Constants.twitter_url = c.getString(c.getColumnIndex(TAG_ABOUT_TWITTER));

                    Constants.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
                }
                c.close();
                return true;
            } else {
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}  