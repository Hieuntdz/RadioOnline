package com.radioentertainment.radio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.radioentertainment.asyncTasks.LoadAbout;
import com.radioentertainment.asyncTasks.LoadLogin;
import com.radioentertainment.interfaces.AboutListener;
import com.radioentertainment.interfaces.LoginListener;
import com.radioentertainment.item.ItemUser;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.DBHelper;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryPurchasesParams;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    SharedPref sharedPref;
    Methods methods;
    DBHelper dbHelper;
    AppOpenManager appOpenManager;
    BillingClient billingClient;
    ProgressBar progressBar;
    Sprite doubleBounce;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appOpenManager = new AppOpenManager(this);
        hideStatusBar();
        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);
        progressBar = findViewById(R.id.progressbar_login);
        doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(new Wave());

        checkUserSubscription();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getIsFirst()) {
                    loadAboutData();
                } else {
                    if(sharedPref.getIsAutoLogin()) {
                        loadLogin();
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LoadAppOpenAd();
                            }
                        }, 2000);
                    }
                }
            }
        }, 500);
    }

    private void checkUserSubscription() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {}).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                            (billingResult1, purchases) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){

                                    boolean isPremium = false;

                                    for(Purchase purchase : purchases){
                                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged()) {
                                            isPremium = true;
                                        }
                                    }

                                    sharedPref.setIsPremium(isPremium);

                                }else{
                                    sharedPref.setIsPremium(false);
                                }
                            });

                }

            }
        });
    }

    public void LoadAppOpenAd(){
        appOpenManager.showAdIfAvailable();
    }

    public void loadAboutData() {
        if (methods.isConnectingToInternet()) {
            LoadAbout loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if(!isFinishing()) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                dbHelper.addtoAbout();
                                sharedPref.setIsFirst(false);
                                LoadAppOpenAd();
                            } else {
                                errorDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            errorDialog(getString(R.string.server_error), getString(R.string.error_server));
                        }
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.internet_not_connected), getString(R.string.error_connect_net_tryagain));
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.internet_not_connected)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void loadLogin() {
        LoadLogin loadLogin = new LoadLogin(new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {
                if (!isFinishing()) {
                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Constants.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), "");

                            Constants.isLogged = true;
                        }
                        LoadAppOpenAd();
                    } else {
                        LoadAppOpenAd();
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_LOGIN, 0, "", "", "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", "", "", null));
        loadLogin.execute();
    }

    public void openMainActivity() {
        Intent intent = new Intent(SplashActivity.this, BaseActivity.class);
        intent.putExtra("from", "");
        startActivity(intent);
        finish();
    }

    void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}