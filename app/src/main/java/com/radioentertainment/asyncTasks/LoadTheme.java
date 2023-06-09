package com.radioentertainment.asyncTasks;

import android.graphics.Color;
import android.os.AsyncTask;

import com.radioentertainment.interfaces.ThemeListener;
import com.radioentertainment.item.ItemTheme;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadTheme extends AsyncTask <String, String, String> {

    private RequestBody requestBody;
    private ThemeListener themeListener;
    private ArrayList<ItemTheme> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadTheme(ThemeListener themeListener, RequestBody requestBody) {
        this.themeListener = themeListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        themeListener.onStart();
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
                    String id = objJson.getString(Constants.TAG_ID);
                    String first = objJson.getString(Constants.TAG_GRADIENT_1);
                    String second = objJson.getString(Constants.TAG_GRADIENT_2);
                    ItemTheme objItem = new ItemTheme(id, Color.parseColor(first), Color.parseColor(second));
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
        themeListener.onEnd(s,verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}