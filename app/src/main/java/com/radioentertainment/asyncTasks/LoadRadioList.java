package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.RadioListListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadRadioList extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private RadioListListener radioListListener;
    private ArrayList<ItemRadio> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadRadioList(RadioListListener radioListListener, RequestBody requestBody) {
        this.radioListListener = radioListListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        radioListListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject mainJson = new JSONObject(JSONParser.okhttpPost(Constants.SERVER_URL, requestBody));
            JSONArray jsonArray = mainJson.getJSONArray(Constants.TAG_ROOT);
            JSONObject objJson;
            for (int i = 0; i < jsonArray.length(); i++) {
                objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Constants.TAG_SUCCESS)) {
                    String id = objJson.getString(Constants.RADIO_ID);
                    String name = objJson.getString(Constants.RADIO_NAME);
                    String url = objJson.getString(Constants.RADIO_IMAGE);
                    String freq = objJson.getString(Constants.RADIO_FREQ);
                    String img = objJson.getString(Constants.RADIO_IMAGE_BIG);
                    String views = objJson.getString(Constants.RADIO_VIEWS);
                    String lang = objJson.getString(Constants.RADIO_LANG);
                    String description = objJson.getString(Constants.RADIO_DESC);
                    String cityname = "", cid = "";
                    if (objJson.has(Constants.CITY_CID)) {
                        cid = objJson.getString(Constants.CITY_CID);
                    }
                    if (objJson.has(Constants.CITY_NAME)) {
                        cityname = objJson.getString(Constants.CITY_NAME);
                    }
                    ItemRadio objItem = new ItemRadio(id, name, url, freq, img, views, cid, cityname, lang, description, "radioentertainment");
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
        radioListListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}