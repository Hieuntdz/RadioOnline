package com.radioentertainment.radio;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.radioentertainment.asyncTasks.LoadAbout;
import com.radioentertainment.interfaces.AboutListener;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.DBHelper;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.StatusBarView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;
    StatusBarView statusBarView;
    WebView webView;
    DBHelper dbHelper;
    TextView textView_appname, textView_email, textView_website, textView_company, textView_contact, textView_version;
    ImageView imageView_logo;
    LinearLayout ll_email, ll_website, ll_company, ll_contact;
    String website, email, desc, applogo, appname, appversion, appauthor, appcontact, privacy, developedby;
    ProgressDialog pbar;
    LoadAbout loadAbout;
    Methods methods;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        dbHelper = new DBHelper(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        statusBarView = findViewById(R.id.statusBar_about);
        toolbar = findViewById(R.id.toolbar_about);
        toolbar.setTitle(getString(R.string.menu_about));
        toolbar.setBackground(methods.getGradientDrawableToolbar());
        statusBarView.setBackground(methods.getGradientDrawableToolbar());
        this.setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pbar = new ProgressDialog(this);
        pbar.setMessage(getString(R.string.app_name));
        pbar.setCancelable(false);

        webView = findViewById(R.id.webView);
        textView_appname = findViewById(R.id.textView_about_appname);
        textView_email = findViewById(R.id.textView_about_email);
        textView_website = findViewById(R.id.textView_about_site);
        textView_company = findViewById(R.id.textView_about_company);
        textView_contact = findViewById(R.id.textView_about_contact);
        textView_version = findViewById(R.id.textView_about_appversion);
        imageView_logo = findViewById(R.id.imageView_about_logo);

        ll_email = findViewById(R.id.ll_email);
        ll_website = findViewById(R.id.ll_website);
        ll_contact = findViewById(R.id.ll_contact);
        ll_company = findViewById(R.id.ll_company);
        dbHelper.getAbout();

        if (methods.isConnectingToInternet()) {
            loadAboutData();
        } else {
            if (dbHelper.getAbout()) {
                setVariables();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void loadAboutData() {
        loadAbout = new LoadAbout(new AboutListener() {
            @Override
            public void onStart() {
                pbar.show();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                if (pbar.isShowing()) {
                    pbar.dismiss();
                }

                if (success.equals("1")) {
                    if(!verifyStatus.equals("-1")) {
                        setVariables();
                        dbHelper.addtoAbout();
                    } else {
                        methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadAbout.execute();
    }

    public void setVariables() {
        appname = Constants.itemAbout.getAppName();
        applogo = Constants.itemAbout.getAppLogo();
        desc = Constants.itemAbout.getAppDesc();
        appversion = Constants.itemAbout.getAppVersion();
        appauthor = Constants.itemAbout.getAuthor();
        appcontact = Constants.itemAbout.getContact();
        email = Constants.itemAbout.getEmail();
        website = Constants.itemAbout.getWebsite();
        privacy = Constants.itemAbout.getPrivacy();
        developedby = Constants.itemAbout.getDevelopedby();

        textView_appname.setText(appname);
        if (!email.trim().isEmpty()) {
            ll_email.setVisibility(View.VISIBLE);
            textView_email.setText(email);
        }

        if (!website.trim().isEmpty()) {
            ll_website.setVisibility(View.VISIBLE);
            textView_website.setText(website);
        }

        if (!appauthor.trim().isEmpty()) {
            ll_company.setVisibility(View.VISIBLE);
            textView_company.setText(appauthor);
        }

        if (!appcontact.trim().isEmpty()) {
            ll_contact.setVisibility(View.VISIBLE);
            textView_contact.setText(appcontact);
        }

        if (!appversion.trim().isEmpty()) {
            textView_version.setText(appversion);
        }

        if (applogo.trim().isEmpty()) {
            imageView_logo.setVisibility(View.GONE);
        } else {
            Picasso
                    .get()
                    .load(Constants.URL_ABOUT_US_LOGO + applogo)
                    .into(imageView_logo);
        }

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String text = "<html><head>"
                + "<style> body{color:#000 !important;text-align:left}"
                + "</style></head>"
                + "<body>"
                + desc
                + "</body></html>";


        webView.setBackgroundColor(Color.TRANSPARENT);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            webView.loadData(text, mimeType, encoding);
        } else {
            webView.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
        }
    }
}
