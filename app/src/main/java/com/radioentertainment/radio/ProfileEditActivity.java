package com.radioentertainment.radio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.radioentertainment.asyncTasks.LoadProfileEdit;
import com.radioentertainment.interfaces.SuccessListener;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;
import com.radioentertainment.utils.StatusBarView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText editText_name, editText_email, editText_phone, editText_pass, editText_cpass;
    Toolbar toolbar;
    Methods methods;
    SharedPref sharedPref;
    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        StatusBarView statusBarView = findViewById(R.id.statusBar);
        statusBarView.setBackground(methods.getGradientDrawableToolbar());

        toolbar = findViewById(R.id.toolbar_proedit);
        toolbar.setBackground(methods.getGradientDrawableToolbar());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppCompatButton button_update = findViewById(R.id.button_prof_update);
        editText_name = findViewById(R.id.editText_profedit_name);
        editText_email = findViewById(R.id.editText_profedit_email);
        editText_phone = findViewById(R.id.editText_profedit_phone);
        editText_pass = findViewById(R.id.editText_profedit_password);
        editText_cpass = findViewById(R.id.editText_profedit_cpassword);

        ViewCompat.setBackgroundTintList(editText_name, ColorStateList.valueOf(sharedPref.getFirstColor()));
        ViewCompat.setBackgroundTintList(editText_email, ColorStateList.valueOf(sharedPref.getFirstColor()));
        ViewCompat.setBackgroundTintList(editText_phone, ColorStateList.valueOf(sharedPref.getFirstColor()));
        ViewCompat.setBackgroundTintList(editText_pass, ColorStateList.valueOf(sharedPref.getFirstColor()));
        ViewCompat.setBackgroundTintList(editText_cpass, ColorStateList.valueOf(sharedPref.getFirstColor()));

        LinearLayout ll_adView = findViewById(R.id.ll_adView);
        methods.showBannerAd(ll_adView);

        setProfileVar();

        button_update.setBackground(methods.getRoundDrawable(sharedPref.getFirstColor()));
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (methods.isConnectingToInternet()) {
                        loadUpdateProfile();
                    } else {
                        Toast.makeText(ProfileEditActivity.this, getResources().getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean validate() {
        editText_name.setError(null);
        editText_email.setError(null);
        editText_cpass.setError(null);
        if (editText_name.getText().toString().trim().isEmpty()) {
            methods.showToast(getString(R.string.cannot_empty));
            editText_name.setError(getString(R.string.cannot_empty));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            methods.showToast(getString(R.string.email_empty));
            editText_email.setError(getString(R.string.cannot_empty));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().endsWith(" ")) {
            methods.showToast(getString(R.string.pass_end_space));
            editText_pass.setError(getString(R.string.pass_end_space));
            editText_pass.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().trim().equals(editText_cpass.getText().toString().trim())) {
            methods.showToast(getString(R.string.pass_nomatch));
            editText_cpass.setError(getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void updateArray() {
        Constants.itemUser.setName(editText_name.getText().toString());
        Constants.itemUser.setEmail(editText_email.getText().toString());
        Constants.itemUser.setMobile(editText_phone.getText().toString());

        if (!editText_pass.getText().toString().equals("")) {
            sharedPref.setRemeber(false);
        }
    }

    private void loadUpdateProfile() {
        LoadProfileEdit loadProfileEdit = new LoadProfileEdit(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    switch (registerSuccess) {
                        case "1":
                            updateArray();
                            Constants.isUpdate = true;
                            finish();
                            methods.showToast(message);
                            break;
                        case "-1":
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            break;
                        default:
                            if (message.contains("Email address already used")) {
                                editText_email.setError(message);
                                editText_email.requestFocus();
                            } else {
                                methods.showToast(message);
                            }
                            break;
                    }
                } else {
                    methods.showToast(getString(R.string.error_server));
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_PROFILE_UPDATE, 0, "", "", "", "", "", "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), Constants.itemUser.getId(), "", null));
        loadProfileEdit.execute();
    }

    public void setProfileVar() {
        editText_name.setText(Constants.itemUser.getName());
        editText_phone.setText(Constants.itemUser.getMobile());
        editText_email.setText(Constants.itemUser.getEmail());
    }
}
