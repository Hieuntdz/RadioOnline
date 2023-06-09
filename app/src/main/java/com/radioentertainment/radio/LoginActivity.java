package com.radioentertainment.radio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radioentertainment.asyncTasks.LoadLogin;
import com.radioentertainment.interfaces.LoginListener;
import com.radioentertainment.item.ItemUser;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;
import com.radioentertainment.utils.StatusBarView;

import cn.refactor.library.SmoothCheckBox;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private String from = "";

    SharedPref sharedPref;
    EditText editText_email, editText_password;
    Button button_login, button_skip;
    TextView textView_register, textView_forgotpass;
    Methods methods;
    ProgressDialog progressDialog;
    LinearLayout ll_checkbox;
    SmoothCheckBox cb_rememberme;
    StatusBarView statusBarView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        from = getIntent().getStringExtra("from");

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        statusBarView = findViewById(R.id.statusBar);
        statusBarView.setBackground(methods.getGradientDrawableToolbar());

        ll_checkbox = findViewById(R.id.ll_checkbox);
        cb_rememberme = findViewById(R.id.cb_rememberme);
        editText_email = findViewById(R.id.et_login_email);
        editText_password = findViewById(R.id.et_login_password);
        button_login = findViewById(R.id.button_login);
        button_skip = findViewById(R.id.button_skip);
        textView_register = findViewById(R.id.tv_login_signup);
        textView_forgotpass = findViewById(R.id.tv_forgotpass);

        View view_login = findViewById(R.id.view_login);
        view_login.setBackgroundColor(sharedPref.getFirstColor());
        textView_register.setTextColor(sharedPref.getFirstColor());

        button_login.setBackground(methods.getRoundDrawable(sharedPref.getFirstColor()));
        button_skip.setBackground(methods.getRoundDrawable(Color.WHITE));
        button_skip.setTextColor(sharedPref.getFirstColor());

        TextView tv_welcome = findViewById(R.id.tv);

        tv_welcome.setTypeface(tv_welcome.getTypeface(), Typeface.BOLD);
        textView_forgotpass.setTypeface(textView_forgotpass.getTypeface(), Typeface.BOLD);
        textView_register.setTypeface(textView_register.getTypeface(), Typeface.BOLD);
        button_login.setTypeface(button_login.getTypeface(), Typeface.BOLD);
        button_skip.setTypeface(button_skip.getTypeface(), Typeface.BOLD);

        ll_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_rememberme.setChecked(!cb_rememberme.isChecked());
            }
        });

        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });

        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        textView_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.isConnectingToInternet()) {
                    attemptLogin();
                } else {
                    methods.showToast(getString(R.string.internet_not_connected));
                }
            }
        });

        if (sharedPref.getIsRemember()) {
            editText_email.setText(sharedPref.getEmail());
            editText_password.setText(sharedPref.getPassword());
        }
    }

    private void attemptLogin() {
        editText_email.setError(null);
        editText_password.setError(null);

        // Store values at the time of the login attempt.
        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            editText_password.setError(getString(R.string.error_password_sort));
            focusView = editText_password;
            cancel = true;
        }
        if (editText_password.getText().toString().endsWith(" ")) {
            editText_password.setError(getString(R.string.pass_end_space));
            focusView = editText_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            editText_email.setError(getString(R.string.cannot_empty));
            focusView = editText_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            editText_email.setError(getString(R.string.error_invalid_email));
            focusView = editText_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loadLogin();
        }
    }

    private void loadLogin() {
        LoadLogin loadLogin = new LoadLogin(new LoginListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    switch (loginSuccess) {
                        case "1":
                            Constants.itemUser = new ItemUser(user_id, user_name, editText_email.getText().toString(), "");
                            if (cb_rememberme.isChecked()) {
                                sharedPref.setLoginDetails(Constants.itemUser, true, editText_password.getText().toString());
                            } else {
                                sharedPref.setRemeber(false);
                            }
                            sharedPref.setIsAutoLogin(true);
                            Constants.isLogged = true;
                            methods.showToast(getString(R.string.login_success));

                            if (from.equals("app")) {
                                finish();
                            } else {
                                openMainActivity();
                            }
                            break;
                        case "-1":
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            break;
                        default:
                            methods.showToast(message);
                            break;
                    }
                } else {
                    methods.showToast(getString(R.string.error_server));
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_LOGIN, 0, "", "", "", "", "", "", editText_email.getText().toString(), editText_password.getText().toString(), "", "", "", "", null));
        loadLogin.execute();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        if (button_login != null && Constants.isThemeChanged) {
            button_login.setBackground(methods.getRoundDrawable(sharedPref.getFirstColor()));
            button_skip.setTextColor(sharedPref.getFirstColor());
        }
        super.onResume();
    }
}