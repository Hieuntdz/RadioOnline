package com.radioentertainment.asyncTasks;

import android.os.AsyncTask;

import com.radioentertainment.interfaces.AboutListener;
import com.radioentertainment.item.ItemAbout;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadAbout extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private AboutListener aboutListener;
    private String message = "", verifyStatus = "0";

    public LoadAbout(AboutListener aboutListener, RequestBody requestBody) {
        this.aboutListener = aboutListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject mainJson = new JSONObject(JSONParser.okhttpPost(Constants.SERVER_URL, requestBody));

            if(mainJson.has(Constants.TAG_ROOT)) {
                JSONArray jsonArray = mainJson.getJSONArray(Constants.TAG_ROOT);
                JSONObject c;
                for (int i = 0; i < jsonArray.length(); i++) {
                    c = jsonArray.getJSONObject(i);

                    if (!c.has(Constants.TAG_SUCCESS)) {
                        String appname = c.getString("app_name");
                        String applogo = c.getString("app_logo");
                        String desc = c.getString("app_description");
                        String appversion = c.getString("app_version");
                        String appauthor = c.getString("app_author");
                        String appcontact = c.getString("app_contact");
                        String email = c.getString("app_email");
                        String website = c.getString("app_website");
                        String privacy = c.getString("app_privacy_policy");
                        String developedby = c.getString("app_developed_by");

                        Constants.ad_banner_id = c.getString("banner_ad_id");
                        Constants.ad_inter_id = c.getString("interstital_ad_id");
                        Constants.isBannerAd = Boolean.parseBoolean(c.getString("banner_ad"));
                        Constants.isInterAd = Boolean.parseBoolean(c.getString("interstital_ad"));
                        Constants.ad_publisher_id = c.getString("publisher_id");
                        Constants.adShow = Integer.parseInt(c.getString("interstital_ad_click"));
                        Constants.fb_url = c.getString("app_fb_url");
                        Constants.twitter_url = c.getString("app_twitter_url");
                        Constants.packageName = c.getString("package_name");

                        Constants.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
                    } else {
                        verifyStatus = c.getString(Constants.TAG_SUCCESS);
                        message = c.getString(Constants.TAG_MSG);
                    }
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
        aboutListener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}