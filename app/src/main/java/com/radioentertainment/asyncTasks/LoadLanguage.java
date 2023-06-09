package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.LanguageListener;
import com.radioentertainment.item.ItemLanguage;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadLanguage extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private LanguageListener languageListener;
    private ArrayList<ItemLanguage> arrayList;
    private String verifyStatus = "0", message = "";

    public LoadLanguage(LanguageListener languageListener, RequestBody requestBody) {
        this.languageListener = languageListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        languageListener.onStart();
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
                    String id = objJson.getString(Constants.LANG_ID);
                    String name = objJson.getString(Constants.LANG_NAME);
                    ItemLanguage objItem = new ItemLanguage(id, name, null);
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
        languageListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}