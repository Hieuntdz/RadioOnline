package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.OnDemandCatListener;
import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadOnDemandCat extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private OnDemandCatListener onDemandCatListener;
    private ArrayList<ItemOnDemandCat> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadOnDemandCat(OnDemandCatListener onDemandCatListener, RequestBody requestBody) {
        this.onDemandCatListener = onDemandCatListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        onDemandCatListener.onStart();
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
                    String id = objJson.getString(Constants.CAT_ID);
                    String name = objJson.getString(Constants.CAT_NAME);
                    String image = objJson.getString(Constants.CAT_IMAGE);
                    String thumb = objJson.getString(Constants.CAT_THUMB);
                    String total_items = objJson.getString(Constants.TOTAL_ITEMS);
                    ItemOnDemandCat objItem = new ItemOnDemandCat(id, name, image, thumb, total_items);
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
        onDemandCatListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}