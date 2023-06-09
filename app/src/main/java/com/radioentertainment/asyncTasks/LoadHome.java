package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.HomeListener;
import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadHome extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private HomeListener homeListener;
    private ArrayList<ItemRadio> arrayList_featured, arrayList_mostviewed;
    private ArrayList<ItemOnDemandCat> arraylist_ondemand_cat;

    public LoadHome(HomeListener homeListener, RequestBody requestBody) {
        this.homeListener = homeListener;
        this.requestBody = requestBody;
        arrayList_mostviewed = new ArrayList<>();
        arrayList_featured = new ArrayList<>();
        arraylist_ondemand_cat = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        homeListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject mainJson = new JSONObject(JSONParser.okhttpPost(Constants.SERVER_URL, requestBody));
            JSONObject jsonObject = mainJson.getJSONObject(Constants.TAG_ROOT);

            JSONArray jsonArray = jsonObject.getJSONArray(Constants.TAG_FEATURED);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                String id = objJson.getString(Constants.RADIO_ID);
                String name = objJson.getString(Constants.RADIO_NAME);
                String url = objJson.getString(Constants.RADIO_IMAGE);
                String freq = objJson.getString(Constants.RADIO_FREQ);
                String img = objJson.getString(Constants.RADIO_IMAGE_BIG);
                String views = objJson.getString(Constants.RADIO_VIEWS);
                String cid = objJson.getString(Constants.CITY_CID);
                String cityname = objJson.getString(Constants.CITY_NAME);
                String lang = objJson.getString(Constants.RADIO_LANG);
                String description = objJson.getString(Constants.RADIO_DESC);
                ItemRadio objItem = new ItemRadio(id, name, url, freq, img, views, cid, cityname, lang, description,"radioentertainment");
                arrayList_featured.add(objItem);
            }

            jsonArray = jsonObject.getJSONArray(Constants.TAG_MOST_VIEWED);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                String id = objJson.getString(Constants.RADIO_ID);
                String name = objJson.getString(Constants.RADIO_NAME);
                String url = objJson.getString(Constants.RADIO_IMAGE);
                String freq = objJson.getString(Constants.RADIO_FREQ);
                String img = objJson.getString(Constants.RADIO_IMAGE_BIG);
                String views = objJson.getString(Constants.RADIO_VIEWS);
                String cid = objJson.getString(Constants.CITY_CID);
                String cityname = objJson.getString(Constants.CITY_NAME);
                String lang = objJson.getString(Constants.RADIO_LANG);
                String description = objJson.getString(Constants.RADIO_DESC);
                ItemRadio objItem = new ItemRadio(id, name, url, freq, img, views, cid, cityname, lang, description,"radioentertainment");
                arrayList_mostviewed.add(objItem);
            }

            jsonArray = jsonObject.getJSONArray(Constants.TAG_ON_DEMAND_CAT_LIST);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                String id = objJson.getString(Constants.CAT_ID);
                String name = objJson.getString(Constants.CAT_NAME);
                String image = objJson.getString(Constants.CAT_IMAGE);
                String thumb = objJson.getString(Constants.CAT_THUMB);
                String total_items = objJson.getString(Constants.TOTAL_ITEMS);
                ItemOnDemandCat objItem = new ItemOnDemandCat(id, name,image,thumb,total_items);
                arraylist_ondemand_cat.add(objItem);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }

    }

    @Override
    protected void onPostExecute(String s) {
        homeListener.onEnd(s, arrayList_featured, arrayList_mostviewed, arraylist_ondemand_cat);
        super.onPostExecute(s);
    }
}
