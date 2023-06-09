package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.RadioViewListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadRadioViewed extends AsyncTask<String, String, String> {

    private RadioViewListener radioViewListener;
    private RequestBody requestBody;

    public LoadRadioViewed(RadioViewListener radioViewListener, RequestBody requestBody) {
        this.radioViewListener = radioViewListener;
        this.requestBody = requestBody;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json_string = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);

            JSONObject mainJson = new JSONObject(json_string);
            JSONArray jsonArray = mainJson.getJSONArray(Constants.TAG_ROOT);
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
                Constants.itemRadio = new ItemRadio(id,name,url,freq,img,views,cid,cityname,lang, description,"radioentertainment");
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }

    }

    @Override
    protected void onPostExecute(String s) {
        radioViewListener.onEnd(s);
        super.onPostExecute(s);
    }
}