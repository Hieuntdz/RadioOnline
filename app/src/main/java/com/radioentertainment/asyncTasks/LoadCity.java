package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.CityListener;
import com.radioentertainment.item.ItemCity;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadCity extends AsyncTask <String, String, String> {

    private RequestBody requestBody;
    private CityListener cityListener;
    private ArrayList<ItemCity> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadCity(CityListener cityListener, RequestBody requestBody) {
        this.cityListener = cityListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        cityListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject mainJson = new JSONObject(JSONParser.okhttpPost(Constants.SERVER_URL, requestBody));
            JSONArray jsonArray = mainJson.getJSONArray(Constants.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Constants.TAG_SUCCESS)) {
                    String id = objJson.getString(Constants.CITY_CID);
                    String name = objJson.getString(Constants.CITY_NAME);
                    String tag = objJson.getString(Constants.CITY_TAGLINE);
                    ItemCity objItem = new ItemCity(id, name, tag);
                    arrayList.add(objItem);
                } else {
                    verifyStatus = objJson.getString(Constants.TAG_SUCCESS);
                    message = objJson.getString(Constants.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }

    }

    @Override
    protected void onPostExecute(String s) {
        cityListener.onEnd(s,verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}