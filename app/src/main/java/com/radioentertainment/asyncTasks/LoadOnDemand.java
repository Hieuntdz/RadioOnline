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

public class LoadOnDemand extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private RadioListListener radioListListener;
    private ArrayList<ItemRadio> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadOnDemand(RadioListListener radioListListener, RequestBody requestBody) {
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
                    String name = objJson.getString(Constants.TAG_ONDEMAND_TITLE);
                    String url = objJson.getString(Constants.TAG_ONDEMAND_URL);
                    String image = objJson.getString(Constants.TAG_ONDEMAND_IMAGE);
                    String thumb = objJson.getString(Constants.TAG_ONDEMAND_IMAGE_THUMB);
                    String duration = objJson.getString(Constants.TAG_ONDEMAND_DURATION);
                    String desc = objJson.getString(Constants.TAG_ONDEMAND_DESCRIPTION);
                    String totalviews = objJson.getString(Constants.TAG_ONDEMAND_TOTAL_VIEWS);
                    ItemRadio objItem = new ItemRadio(id, name, url, image, thumb, duration, totalviews, desc, "ondemand");
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