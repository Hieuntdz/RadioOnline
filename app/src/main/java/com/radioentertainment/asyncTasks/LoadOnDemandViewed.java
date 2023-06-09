package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.RadioViewListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadOnDemandViewed extends AsyncTask<String, String, String> {

    private RadioViewListener radioViewListener;
    private RequestBody requestBody;

    public LoadOnDemandViewed(RadioViewListener radioViewListener, RequestBody requestBody) {
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
                String name = objJson.getString(Constants.TAG_ONDEMAND_TITLE);
                String url = objJson.getString(Constants.TAG_ONDEMAND_URL);
                String image = objJson.getString(Constants.TAG_ONDEMAND_IMAGE);
                String thumb = objJson.getString(Constants.TAG_ONDEMAND_IMAGE_THUMB);
                String duration = objJson.getString(Constants.TAG_ONDEMAND_DURATION);
                String desc = objJson.getString(Constants.TAG_ONDEMAND_DESCRIPTION);
                String totalviews = objJson.getString(Constants.TAG_ONDEMAND_TOTAL_VIEWS);
                Constants.itemRadio = new ItemRadio(id, name, url, image, thumb, duration, totalviews, desc, "ondemand");
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