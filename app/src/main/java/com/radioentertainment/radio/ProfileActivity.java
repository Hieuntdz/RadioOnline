package com.radioentertainment.radio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.radioentertainment.asyncTasks.LoadProfile;
import com.radioentertainment.interfaces.SuccessListener;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.StatusBarView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    Methods methods;
    Toolbar toolbar;
    TextView textView_name, textView_email, textView_mobile, textView_notlog;
    LinearLayout ll_mobile;
    View view_phone;
    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        StatusBarView statusBarView = findViewById(R.id.statusBar);
        statusBarView.setBackground(methods.getGradientDrawableToolbar());

        toolbar = findViewById(R.id.toolbar_pro);
        toolbar.setBackground(methods.getGradientDrawableToolbar());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        textView_name = findViewById(R.id.tv_prof_fname);
        textView_email = findViewById(R.id.tv_prof_email);
        textView_mobile = findViewById(R.id.tv_prof_mobile);
        textView_notlog = findViewById(R.id.textView_notlog);

        ll_mobile = findViewById(R.id.ll_prof_phone);

        view_phone = findViewById(R.id.view_prof_phone);

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        methods.showBannerAd(ll_adView);

        if (Constants.itemUser != null && !Constants.itemUser.getId().equals("")) {
            if (methods.isConnectingToInternet()) {
                loadUserProfile();
            } else {
                setEmpty(true, getString(R.string.internet_not_connected));
            }
        } else {
            setEmpty(true, getString(R.string.not_log));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        if (Constants.itemUser != null && !Constants.itemUser.getId().equals("")) {
            menu.findItem(R.id.item_profile_edit).setVisible(true);
        } else {
            menu.findItem(R.id.item_profile_edit).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_profile_edit:
                if (Constants.itemUser != null && !Constants.itemUser.getId().equals("")) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                    startActivity(intent);
                } else {
                    methods.showToast(getString(R.string.not_log));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfile() {
        LoadProfile loadProfile = new LoadProfile(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    if (registerSuccess.equals("1")) {
                        setVariables();
                    } else {
                        setEmpty(false, message);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_PROFILE, 0, "", "", "", "", "", "", "", "", "", "", Constants.itemUser.getId(), "", null));
        loadProfile.execute();
    }

    public void setVariables() {
        textView_name.setText(Constants.itemUser.getName());
        textView_mobile.setText(Constants.itemUser.getMobile());
        textView_email.setText(Constants.itemUser.getEmail());

        if (!Constants.itemUser.getMobile().trim().isEmpty()) {
            ll_mobile.setVisibility(View.VISIBLE);
            view_phone.setVisibility(View.VISIBLE);
        }

        textView_notlog.setVisibility(View.GONE);
    }

    public void setEmpty(Boolean flag, String message) {
        if (flag) {
            textView_notlog.setText(message);
            textView_notlog.setVisibility(View.VISIBLE);
        } else {
            textView_notlog.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        if (Constants.isUpdate) {
            Constants.isUpdate = false;
            setVariables();
        }
        super.onResume();
    }
}
