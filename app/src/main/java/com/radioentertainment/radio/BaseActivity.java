package com.radioentertainment.radio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.radioentertainment.adapter.AdapterSuggest;
import com.radioentertainment.interfaces.AdConsentListener;
import com.radioentertainment.interfaces.BackInterAdListener;
import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.utils.RecyclerItemClickListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.makeramen.roundedimageview.RoundedImageView;
import com.radioentertainment.asyncTasks.LoadAbout;
import com.radioentertainment.asyncTasks.LoadRadioViewed;
import com.radioentertainment.asyncTasks.LoadReport;
import com.radioentertainment.fragments.FragmentCity;
import com.radioentertainment.fragments.FragmentFavourite;
import com.radioentertainment.fragments.FragmentFeaturedRadio;
import com.radioentertainment.fragments.FragmentHome;
import com.radioentertainment.fragments.FragmentLanguage;
import com.radioentertainment.fragments.FragmentLanguageDetails;
import com.radioentertainment.fragments.FragmentMain;
import com.radioentertainment.fragments.FragmentOnDemandCat;
import com.radioentertainment.fragments.FragmentOnDemandDetails;
import com.radioentertainment.fragments.FragmentSearch;
import com.radioentertainment.fragments.FragmentSuggestion;
import com.radioentertainment.interfaces.AboutListener;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.interfaces.RadioViewListener;
import com.radioentertainment.interfaces.SuccessListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.utils.AdConsent;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.DBHelper;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;
import com.radioentertainment.utils.SleepTimeReceiver;
import com.radioentertainment.utils.StatusBarView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    StatusBarView statusBarView;
    FragmentManager fm;
    NavigationView navigationView;
    SlidingUpPanelLayout slidingPanel, sliding_layout_main;
    BottomSheetDialog dialog_desc;
    DBHelper dbHelper;
    AdConsent adConsent;
    ProgressDialog progressDialog;
    CircularProgressBar circularProgressBar, circularProgressBar_collapse;
    SeekBar seekbar_song;
    LinearLayout ll_ad, ll_collapse_color, ll_player_expand, ll_play_collapse, ll_top_collapse;
    RelativeLayout rl_expand, rl_collapse, rl_song_seekbar, btn_previous_expand, btn_sleep, btn_next_expand, btn_volume;
    ImageView imageView_player;
    RoundedImageView imageView_radio;
    ImageView imageView_play, imageView_share, imageView_fav;
//    FloatingActionButton fab_play_expand;
    TextView textView_name, textView_song, textView_freq_expand, textView_radio_expand, textView_song_expand, textView_song_duration, textView_total_duration, tv_views;
    Methods methods, methodsBack;
    LoadAbout loadAbout;
    DrawerLayout drawer;
    Boolean isExpand = false, isLoaded = false;
    SharedPref sharedPref;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    private Handler seekHandler = new Handler();
    View view_lollipop;
    Handler handler = new Handler();
    MenuItem menu_login, menu_profile;
    BottomSheetDialog dialog_report;
    RecyclerView rv_suggestion;
    RelativeLayout btn_play_music, btn_previous_scene2, btn_next_scene2, btn_play_scene2;
    ImageView iv_play_music, iv_thumb_scene2, iv_play_scene2;
    CardView btn_report;
    LinearLayout control_dragview, ll_suggest, ll_player_scene2, ll_player_content;
    TextView tv_songname_scene2;


    double current_offset = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_base);

        Constants.isAppOpen = true;

        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        sharedPref = new SharedPref(this);

        sharedPref.setCheckSleepTime();

        view_lollipop = findViewById(R.id.view_lollipop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view_lollipop.setVisibility(View.VISIBLE);
        }

        drawer = findViewById(R.id.drawer_layout);

        statusBarView = findViewById(R.id.statusBar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);
        methods = new Methods(this, interAdListener);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());
        fm = getSupportFragmentManager();

        methodsBack = new Methods(this, backInterAdListener);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        slidingPanel = findViewById(R.id.sliding_layout);
        sliding_layout_main = findViewById(R.id.sliding_layout_main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        menu_login = menu.findItem(R.id.nav_login);
        menu_profile = menu.findItem(R.id.nav_profile);

        changeLoginName();

        control_dragview = findViewById(R.id.slp_control_dragview);
        tv_views = findViewById(R.id.tv_views);
        btn_report = findViewById(R.id.btn_report);
        rv_suggestion = findViewById(R.id.rv_suggestion);
        circularProgressBar = findViewById(R.id.loader);
        circularProgressBar_collapse = findViewById(R.id.loader_collapse);
        seekbar_song = findViewById(R.id.seekbar_song);
        btn_sleep = findViewById(R.id.btn_sleep_expand);
        imageView_share = findViewById(R.id.imageView_share);
        imageView_fav = findViewById(R.id.imageView_fav_expand);
        imageView_player = findViewById(R.id.imageView_player);
//        imageView_previous = findViewById(R.id.imageView_player_previous);
        btn_previous_expand = findViewById(R.id.btn_previous_expand);
//        imageView_next = findViewById(R.id.imageView_player_next);
        btn_next_expand = findViewById(R.id.btn_next_expand);
        imageView_play = findViewById(R.id.imageView_player_play);
        //imageView_desc = findViewById(R.id.imageView_desc_expand);
        btn_volume = findViewById(R.id.btn_volume);
        textView_name = findViewById(R.id.textView_player_name);
        textView_song = findViewById(R.id.textView_song_name);
        ll_suggest = findViewById(R.id.ll_suggest);
        ll_player_scene2 = findViewById(R.id.ll_player_scene2);
        //imageView_report = findViewById(R.id.imageView_report_expand);

        iv_play_scene2 = findViewById(R.id.iv_play_scene2);
        iv_thumb_scene2 = findViewById(R.id.iv_thumb_scene2);
        btn_play_scene2 = findViewById(R.id.btn_play_scene2);
        btn_previous_scene2 = findViewById(R.id.btn_previous_scene2);
        btn_next_scene2 = findViewById(R.id.btn_next_scene2);
        tv_songname_scene2 = findViewById(R.id.tv_songname_scene2);
        ll_player_content = findViewById(R.id.ll_player_content);


        iv_play_music = findViewById(R.id.iv_play_music);
        btn_play_music = findViewById(R.id.btn_play_music);
//        fab_play_expand = findViewById(R.id.fab_play);
        imageView_radio = findViewById(R.id.imageView_radio);
        textView_radio_expand = findViewById(R.id.textView_radio_name_expand);
        textView_freq_expand = findViewById(R.id.textView_freq_expand);
        textView_song_expand = findViewById(R.id.textView_song_expand);
        textView_song_duration = findViewById(R.id.textView_song_duration);
        textView_total_duration = findViewById(R.id.textView_total_duration);

        ll_player_expand = findViewById(R.id.ll_player_expand);
        ll_play_collapse = findViewById(R.id.ll_play_collapse);
        ll_top_collapse = findViewById(R.id.ll_top_collapse);
        rl_song_seekbar = findViewById(R.id.rl_song_seekbar);
        rl_collapse = findViewById(R.id.ll_collapse);
        ll_collapse_color = findViewById(R.id.ll_collapse_color);
        rl_expand = findViewById(R.id.ll_expand);
        rl_expand.setAlpha(0.0f);
        ll_ad = findViewById(R.id.ll_adView);

        setIfPlaying();

        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                methods.showBannerAd(ll_ad);

                setUpContentSlideMain();
            }
        });

        if (methods.isConnectingToInternet()) {
            loadAboutData();
            adConsent.checkForConsent();
        } else {

            setUpContentSlideMain();

            dbHelper.getAbout();
            methods.showToast(getString(R.string.internet_not_connected));
        }

        seekbar_song.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                try {
                    Constants.exoPlayer_Radio.seekTo((int) Methods.getSeekFromPercentage(progress, Methods.calculateTime(Constants.arrayList_radio.get(Constants.pos).getDuration())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_play_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rl_collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        rl_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ll_top_collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        sliding_layout_main.setDragView(control_dragview);

        slidingPanel.setDragView(rl_collapse);
        slidingPanel.setShadowHeight(0);
        slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 0.0f) {
                    isExpand = false;
                    rl_expand.setVisibility(View.GONE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    if (isExpand) {

                        rl_collapse.setVisibility(View.VISIBLE);
                        rl_expand.setAlpha(slideOffset);
                        rl_collapse.setAlpha(1.0f - slideOffset);
                    } else {

                        rl_expand.setVisibility(View.VISIBLE);
                        rl_expand.setAlpha(0.0f + slideOffset);
                        rl_collapse.setAlpha(1.0f - slideOffset);
                    }
                } else {
                    isExpand = true;
                    rl_collapse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    viewpager.setCurrentItem(Constants.playPos);
                }
            }
        });



        sliding_layout_main.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {


            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    Log.e("AAA", "expand");

                    ObjectAnimator suggestDown = ObjectAnimator.ofFloat(ll_suggest, "translationY", 220f);
                    suggestDown.setDuration(300);
                    suggestDown.start();


                    ll_player_expand.animate()
                            .alpha(0f)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ll_player_expand.setVisibility(View.GONE);
                                }
                            });


                    ll_player_scene2.animate()
                            .alpha(1f)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    ll_player_scene2.setVisibility(View.VISIBLE);
                                }
                            });


                }else{
                    Log.e("AAA", "collapse");

                    ObjectAnimator suggestUp = ObjectAnimator.ofFloat(ll_suggest, "translationY", 0f);
                    suggestUp.setDuration(300);
                    suggestUp.start();


                    ll_player_expand.animate()
                            .alpha(1f)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    ll_player_expand.setVisibility(View.VISIBLE);
                                }
                            });

                    ll_player_scene2.animate()
                            .alpha(0f)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ll_player_scene2.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });


        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showRateDialog();
                clickPlay(Constants.pos, Constants.playTypeRadio);
            }
        });

//        fab_play_expand.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(BaseActivity.this, R.color.colorPrimary)));
//
//        fab_play_expand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                methods.showRateDialog();
//                clickPlay(Constants.pos, Constants.playTypeRadio);
//            }
//        });

        btn_play_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showRateDialog();
                clickPlay(Constants.pos, Constants.playTypeRadio);
            }
        });

        btn_play_scene2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showRateDialog();
                clickPlay(Constants.pos, Constants.playTypeRadio);
            }
        });

//        imageView_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                methods.showRateDialog();
//                togglePlayPosition(true);
//            }
//        });

        btn_next_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showRateDialog();
                togglePlayPosition(true);
            }
        });

        btn_next_scene2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showRateDialog();
                togglePlayPosition(true);
            }
        });

//        imageView_previous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                methods.showRateDialog();
//                togglePlayPosition(false);
//            }
//        });

        btn_previous_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showRateDialog();
                togglePlayPosition(false);
            }
        });

        btn_previous_scene2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showRateDialog();
                togglePlayPosition(false);
            }
        });

        imageView_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.arrayList_radio.size() > 0) {
                    methods.showRateDialog();
                    if (dbHelper.addORremoveFav(Constants.arrayList_radio.get(Constants.pos))) {
                        imageView_fav.setImageResource(R.drawable.fav);
                        methods.showToast(getString(R.string.add_to_fav));
                    } else {
                        imageView_fav.setImageResource(R.drawable.unfav);
                        methods.showToast(getString(R.string.remove_from_fav));
                    }
                }
            }
        });

        imageView_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.arrayList_radio.size() > 0) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.listining_to) + " - " + Constants.arrayList_radio.get(Constants.pos).getRadioName() + "\n" + getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(share);
                }
            }
        });


//        imageView_desc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                methods.showRateDialog();
//                showBottomSheetDialog();
//            }
//        });

        btn_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showRateDialog();
                changeVolume();
            }
        });

        btn_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showRateDialog();
                if (sharedPref.getIsSleepTimeOn()) {
                    openTimeDialog();
                } else {
                    openTimeSelectDialog();
                }
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.arrayList_radio.size() > 0) {
                    showReportDialog();
                }
            }
        });

        if (!Constants.pushRID.equals("0")) {
            progressDialog.show();
            loadRadio();
        }
        isLoaded = true;
        navigationView.setCheckedItem(navigationView.getMenu().findItem(R.id.nav_home).getItemId());
        changeThemeColor();
        checkPer();

        FragmentMain f1 = new FragmentMain();
        loadFrag(f1, getResources().getString(R.string.home), fm);
        getSupportActionBar().setTitle(getString(R.string.home));
    }

    private void setUpContentSlideMain() {
        int height = sliding_layout_main.getPanelHeight();

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll_player_content.getLayoutParams();
        params.setMargins(0, 0, 0, height); //substitute parameters for left, top, right, bottom
        ll_player_content.setLayoutParams(params);
        
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        clickNav(item.getItemId());
        return true;
    }

    private void clickNav(int item) {
        drawer.closeDrawer(GravityCompat.START);
        switch (item) {
            case R.id.nav_home:
                FragmentMain f1 = new FragmentMain();
                loadFrag(f1, getResources().getString(R.string.home), fm);
                break;
            case R.id.nav_premium:
                startActivity(new Intent(BaseActivity.this, PurchaseActivity.class));
                break;
            case R.id.nav_ondemand:
                FragmentOnDemandCat f2 = new FragmentOnDemandCat();
                loadFrag(f2, getResources().getString(R.string.on_demand), fm);
                break;
            case R.id.nav_featured:
                FragmentFeaturedRadio ffeat = new FragmentFeaturedRadio();
                loadFrag(ffeat, getResources().getString(R.string.featured), fm);
                break;
            case R.id.nav_fav:
                FragmentFavourite ffav = new FragmentFavourite();
                loadFrag(ffav, getResources().getString(R.string.favourite), fm);
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_login:
                methods.clickLogin();
                break;
            case R.id.nav_settings:
                Intent intent_setting = new Intent(BaseActivity.this, SettingActivity.class);
                startActivity(intent_setting);
                break;
            case R.id.nav_sug:
                FragmentSuggestion fsug = new FragmentSuggestion();
                loadFrag(fsug, getResources().getString(R.string.suggestion), fm);
                break;
            case R.id.nav_shareapp:
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(ishare);
                break;
            case R.id.nav_fb:
                if (!Constants.fb_url.trim().isEmpty()) {
                    String url = Constants.fb_url;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                break;
            case R.id.nav_twitter:
                if (!Constants.twitter_url.trim().isEmpty()) {
                    String urlt = Constants.twitter_url;
                    Intent intent_t = new Intent(Intent.ACTION_VIEW);
                    intent_t.setData(Uri.parse(urlt));
                    startActivity(intent_t);
                }
                break;
        }
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {

        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame_activity, f1, name);
        ft.addToBackStack(name);
        ft.commit();
        getSupportActionBar().setTitle(name);
    }

    private void togglePlayPosition(Boolean isNext) {
        if (Constants.arrayList_radio.size() > 0) {
            if (PlayService.getInstance() == null) {
                PlayService.createInstance().initialize(BaseActivity.this, Constants.arrayList_radio.get(Constants.pos));
            }
            Intent intent;
            intent = new Intent(BaseActivity.this, PlayService.class);
            if (isNext) {
                intent.setAction(PlayService.ACTION_NEXT);
            } else {
                intent.setAction(PlayService.ACTION_PREVIOUS);
            }
            startService(intent);
        }
    }

    public void clickPlay(int position, Boolean isRadio) {
        if (Constants.pos < Constants.arrayList_radio.size()) {
            Constants.playTypeRadio = isRadio;
            Constants.pos = position;
            ItemRadio radio = Constants.arrayList_radio.get(Constants.pos);
            final Intent intent = new Intent(BaseActivity.this, PlayService.class);

            if (PlayService.getInstance() != null) {
                ItemRadio playerCurrrentRadio = PlayService.getInstance().getPlayingRadioStation();
                if (playerCurrrentRadio != null) {
                    if (!radio.getRadioId().equals(PlayService.getInstance().getPlayingRadioStation().getRadioId())) {
                        PlayService.getInstance().initialize(BaseActivity.this, radio);
                        intent.setAction(PlayService.ACTION_PLAY);
                    } else {
                        intent.setAction(PlayService.ACTION_TOGGLE);
                    }
                } else {
                    PlayService.getInstance().initialize(BaseActivity.this, radio);
                    intent.setAction(PlayService.ACTION_PLAY);
                }
            } else {
                PlayService.createInstance().initialize(BaseActivity.this, radio);
                intent.setAction(PlayService.ACTION_PLAY);
            }
            startService(intent);
            popUpSlidingPanel();
        }
    }

    private void startImageAnimation(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageView_radio.animate().rotationBy(360).withEndAction(this).setDuration(10000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };

        imageView_radio.animate().rotationBy(360).withEndAction(runnable).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopImageAnimation(){
        imageView_radio.animate().cancel();
    }


    public void LoadDemandList(ArrayList<ItemOnDemandCat> arrayList_demand){

        ArrayList<ItemOnDemandCat> arrayList_random = new ArrayList<>();

        for (int i = 0; i < 6; i++)
        {
            // generating the index using Math.random()
            int index = (int)(Math.random() * arrayList_demand.size());

            if(arrayList_random.contains(arrayList_demand.get(index))){
                i--;
            }else{
                arrayList_random.add(arrayList_demand.get(index));
            }

        }

        AdapterSuggest adapterSuggest = new AdapterSuggest(arrayList_random);
        InterAdListener interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int pos = getPosition(adapterSuggest.getID(position), arrayList_random);
                FragmentManager fm = getSupportFragmentManager();
                FragmentOnDemandDetails f1 = new FragmentOnDemandDetails(true);
                FragmentTransaction ft = fm.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", arrayList_random.get(pos));
                f1.setArguments(bundle);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //ft.hide(fm.findFragmentByTag(getString(R.string.on_demand)));
                ft.add(R.id.content_frame_activity, f1, arrayList_random.get(pos).getName());
                ft.addToBackStack(arrayList_random.get(pos).getName());
                ft.commit();

                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                sliding_layout_main.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        };

        Methods suggest_method = new Methods(this, interAdListener);

        rv_suggestion.setLayoutManager(new GridLayoutManager(this, 3));
        rv_suggestion.setAdapter(adapterSuggest);

        rv_suggestion.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                suggest_method.showInter(position, "");
            }
        }));


    }

    private int getPosition(String id, ArrayList<ItemOnDemandCat> arrayList_demand) {
        int count = 0;
        for (int i = 0; i < arrayList_demand.size(); i++) {
            if (id.equals(arrayList_demand.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }


    private void popUpSlidingPanel(){
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void changePlayPause(Boolean flag) {
        Constants.isPlaying = flag;

        if (flag) {
            ItemRadio itemRadio = PlayService.getInstance().getPlayingRadioStation();
            if (itemRadio != null) {
                changeText(itemRadio);
                imageView_play.setImageDrawable(getResources().getDrawable(R.drawable.pause_2));
//                fab_play_expand.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.mipmap.fab_pause));

                iv_play_music.setImageDrawable(getResources().getDrawable(R.drawable.pause_2));
                iv_play_scene2.setImageDrawable(getResources().getDrawable(R.drawable.pause_2));

                startImageAnimation();
            }
        } else {
            if (Constants.arrayList_radio.size() > 0) {
                changeText(Constants.arrayList_radio.get(Constants.pos));
            }
            imageView_play.setImageDrawable(getResources().getDrawable(R.drawable.play_2));
//            fab_play_expand.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.mipmap.fab_play));

            iv_play_music.setImageDrawable(getResources().getDrawable(R.drawable.play_2));
            iv_play_scene2.setImageDrawable(getResources().getDrawable(R.drawable.play_2));

            stopImageAnimation();
        }
    }

    public void changeText(ItemRadio itemRadio) {
        if (Constants.playTypeRadio) {
            textView_freq_expand.setText(itemRadio.getRadioFreq() + " " + getString(R.string.HZ));
            changeSongName(Constants.song_name);
            changeFav(itemRadio);

            textView_freq_expand.setVisibility(View.VISIBLE);
            textView_song_expand.setVisibility(View.VISIBLE);
            imageView_fav.setVisibility(View.VISIBLE);
            rl_song_seekbar.setVisibility(View.GONE);

            if (FragmentHome.adapterRadioList != null && FragmentHome.adapterRadioList_mostview != null) {
                FragmentHome.adapterRadioList.notifyDataSetChanged();
                FragmentHome.adapterRadioList_mostview.notifyDataSetChanged();
                FragmentHome.adapterRadioList_featured.notifyDataSetChanged();
            }
        } else {
            textView_total_duration.setText(itemRadio.getDuration());
            textView_song.setText(getString(R.string.on_demand));
            textView_song_expand.setText(itemRadio.getRadioName());

            textView_freq_expand.setText(getString(R.string.on_demand));;
            textView_song_expand.setVisibility(View.GONE);
            imageView_fav.setVisibility(View.GONE);
            rl_song_seekbar.setVisibility(View.VISIBLE);
        }
        tv_songname_scene2.setText(itemRadio.getRadioName());
        textView_name.setText(itemRadio.getRadioName());
        textView_radio_expand.setText(itemRadio.getRadioName());
        tv_views.setText(itemRadio.getViews());


        Picasso.get().load(itemRadio.getRadioImageurl())
                .placeholder(R.drawable.placeholder)
                .into(imageView_radio);
        Picasso.get().load(methods.getImageThumbSize(itemRadio.getRadioImageurl(), getString(R.string.home)))
                .placeholder(R.drawable.placeholder)
                .into(imageView_player);
        Picasso.get().load(methods.getImageThumbSize(itemRadio.getRadioImageurl(), getString(R.string.home)))
                .placeholder(R.drawable.placeholder)
                .into(iv_thumb_scene2);
    }

    public void changeFav(ItemRadio itemRadio) {
        if (dbHelper.checkFav(itemRadio)) {
            imageView_fav.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.mipmap.fav_hover_white));
        } else {
            imageView_fav.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.mipmap.fav_white));
        }
    }

    public void changeSongName(String songName) {
        Constants.song_name = songName;
        textView_song.setText(songName);
        textView_song_expand.setText(songName);
    }

    public void setIfPlaying() {
        if (PlayService.getInstance() != null) {
            PlayService.initNewContext(BaseActivity.this);
            changePlayPause(PlayService.getInstance().isPlaying());
            seekUpdation();
        } else {
            changePlayPause(false);
        }
    }

    public void loadAboutData() {
        loadAbout = new LoadAbout(new AboutListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                if (!verifyStatus.equals("-1")) {
                    //adConsent.checkForConsent();
                    dbHelper.addtoAbout();
                } else {
                    methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadAbout.execute();
    }

    public void openQuitDialog() {
        TextView txt_Exit_dialog, txtTitle_Exit_dialog;
        Button btnCancel_Exit_dialog, btnexit_Exit_dialog;
//        AlertDialog.Builder alert;
//        alert = new AlertDialog.Builder(BaseActivity.this, R.style.Widget_MaterialComponents_MaterialCalendar_Day);
//        alert.setTitle(R.string.app_name);
//        alert.setIcon(R.mipmap.app_icon);
//        alert.setMessage(getString(R.string.sure_quit));
//
//        alert.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                finish();
//            }
//        });
//
//        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//       alert.show();
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_exit_app);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        txt_Exit_dialog = (TextView) dialog.findViewById(R.id.txt_Exit_dialog);
        txtTitle_Exit_dialog = (TextView) dialog.findViewById(R.id.txtTitle_Exit_dialog);
        btnCancel_Exit_dialog = (Button) dialog.findViewById(R.id.btnCancel_Exit_dialog);
        btnexit_Exit_dialog = (Button) dialog.findViewById(R.id.btnexit_Exit_dialog);

        txtTitle_Exit_dialog.setText("Confirm");
        txt_Exit_dialog.setText(getString(R.string.sure_quit));
        btnCancel_Exit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        btnexit_Exit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            if (type.equals("nav")) {
                clickNav(position);
            }
        }
    };

    private BackInterAdListener backInterAdListener = new BackInterAdListener() {
        @Override
        public void onClick() {
            BaseActivity.super.onBackPressed();
        }
    };

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(BaseActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!Constants.pushRID.equals("0")) {
            progressDialog.show();
            loadRadio();
        }
        super.onNewIntent(intent);
    }

    private void loadRadio() {
        LoadRadioViewed loadRadioViewed = new LoadRadioViewed(new RadioViewListener() {
            @Override
            public void onEnd(String success) {
                Constants.pushRID = "0";
                progressDialog.dismiss();
                if (success.equals("1")) {
                    Constants.arrayList_radio.clear();
                    Constants.arrayList_radio.add(Constants.itemRadio);
                    clickPlay(0, true);
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_SINGLE_RADIO, 0, "", Constants.pushRID, "", "", "", "", "", "", "", "", "", "", null));
        loadRadioViewed.execute();
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_desc, null);

        dialog_desc = new BottomSheetDialog(this);
        dialog_desc.setContentView(view);
        dialog_desc.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_desc.show();

        AppCompatButton button = dialog_desc.findViewById(R.id.button_detail_close);
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(sharedPref.getFirstColor()));
        TextView textView = dialog_desc.findViewById(R.id.textView_detail_title);
        textView.setText(Constants.arrayList_radio.get(Constants.pos).getRadioName());

        textView.setBackground(methods.getGradientDrawableToolbar());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_desc.dismiss();
            }
        });

        WebView webview_song_desc = dialog_desc.findViewById(R.id.webView_bottom);
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";
        String text = "<html><head>"
                + "<style> body{color: #000 !important;text-align:left}"
                + "</style></head>"
                + "<body>"
                + Constants.arrayList_radio.get(Constants.pos).getDescription()
                + "</body></html>";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            webview_song_desc.loadData(text, mimeType, encoding);
        } else {
            webview_song_desc.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
        }
    }

    @SuppressLint("RestrictedApi")
    public void showReportDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_report, null);

        dialog_report = new BottomSheetDialog(this);
        dialog_report.setContentView(view);
        dialog_report.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_report.show();

        final AppCompatEditText editText = dialog_report.findViewById(R.id.editText_report);
        Button button_report = dialog_report.findViewById(R.id.button_report);
        button_report.setBackground(methods.getRoundDrawable(sharedPref.getFirstColor()));

        ViewCompat.setBackgroundTintList(editText, ColorStateList.valueOf(sharedPref.getFirstColor()));

        button_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.isLogged) {
                    if (!TextUtils.isEmpty(editText.getText())) {
                        String type = "";
                        if (Constants.playTypeRadio) {
                            type = "radioentertainment";
                        } else {
                            type = "song";
                        }
//                        Toast.makeText(BaseActivity.this, "Reporting is disabled in demo app", Toast.LENGTH_SHORT).show();
                        loadReport(type, editText.getText().toString());
                    } else {
                        methods.showToast(getString(R.string.write_report_details));
                    }
                } else {
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    intent.putExtra("from", "app");
                    startActivity(intent);
                }
            }
        });
    }

    private void loadReport(String radioType, String radioDesc) {
        if (methods.isConnectingToInternet()) {
            LoadReport loadReport = new LoadReport(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            dialog_report.dismiss();
                            methods.showToast(message);
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_REPORT, 0, "", Constants.arrayList_radio.get(Constants.pos).getRadioId(), "", "", "", radioType, Constants.itemUser.getEmail(), "", "", "", Constants.itemUser.getId(), radioDesc, null));
            loadReport.execute();
        } else {
            methods.showToast(getString(R.string.internet_not_connected));
        }
    }

    public void changeThemeColor() {
        Constants.isThemeChanged = false;
        statusBarView.setBackground(methods.getGradientDrawableToolbar());
        toolbar.setBackground(methods.getGradientDrawableToolbar());
//        fab_play_expand.setBackgroundTintList(ColorStateList.valueOf(sharedPref.getFirstColor()));
//        ll_collapse_color.setBackground(methods.getGradientDrawableToolbar());
        //navigationView.setBackground(methods.getGradientDrawableToolbar());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            seekbar_song.setThumbTintList(ColorStateList.valueOf(sharedPref.getFirstColor()));
//            seekbar_song.setProgressTintList(ColorStateList.valueOf(sharedPref.getFirstColor()));
//            seekbar_song.setSecondaryProgressTintList(ColorStateList.valueOf(sharedPref.getSecondColor()));
//        } else {
//            seekbar_song.getThumb().setColorFilter(sharedPref.getFirstColor(), PorterDuff.Mode.SRC_IN);
//            seekbar_song.getProgressDrawable().setColorFilter(sharedPref.getSecondColor(), PorterDuff.Mode.SRC_IN);
//        }

        int[][] state = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_pressed}

        };

        int[] color = new int[]{
                sharedPref.getFirstColor(),
                Color.WHITE,
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
        };

        ColorStateList ColorStateList1 = new ColorStateList(state, color);
        //navigationView.setItemTextColor(ColorStateList1);
        //navigationView.setItemIconTintList(ColorStateList1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (fm.getBackStackEntryCount() > 2) {
            getSupportActionBar().setTitle(fm.getFragments().get(fm.getBackStackEntryCount() - 2).getTag());
            methodsBack.showInter(999, "BackPress");
        } else {
            openQuitDialog();
        }
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            try {
                seekUpdation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void seekUpdation() {
        try {
            if (!Constants.playTypeRadio && Constants.isAppOpen) {
                seekbar_song.setProgress(Methods.getProgressPercentage(Constants.exoPlayer_Radio.getCurrentPosition(), Methods.calculateTime(Constants.arrayList_radio.get(Constants.pos).getDuration())));
                textView_song_duration.setText(Methods.milliSecondsToTimer(Constants.exoPlayer_Radio.getCurrentPosition()));
                Log.e("duration", "" + Methods.milliSecondsToTimer(Constants.exoPlayer_Radio.getCurrentPosition()));
                seekbar_song.setSecondaryProgress(Constants.exoPlayer_Radio.getBufferedPercentage());
                if (PlayService.getInstance().isPlaying()) {
                    seekHandler.removeCallbacks(run);
                    seekHandler.postDelayed(run, 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBuffer(Boolean flag) {
        if (flag) {
            circularProgressBar.setVisibility(View.VISIBLE);
            //ll_player_expand.setVisibility(View.INVISIBLE);
            circularProgressBar_collapse.setVisibility(View.VISIBLE);
            ll_play_collapse.setVisibility(View.INVISIBLE);
        } else {
            circularProgressBar.setVisibility(View.INVISIBLE);
            //ll_player_expand.setVisibility(View.VISIBLE);
            circularProgressBar_collapse.setVisibility(View.GONE);
            ll_play_collapse.setVisibility(View.VISIBLE);
        }
    }

    public void changeVolume() {
        final RelativePopupWindow popupWindow = new RelativePopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_volume, null);
        ImageView imageView1 = view.findViewById(R.id.iv1);
        ImageView imageView2 = view.findViewById(R.id.iv2);
        imageView1.setColorFilter(Color.BLACK);
        imageView2.setColorFilter(Color.BLACK);

        VerticalSeekBar seekBar = view.findViewById(R.id.seekbar_volume);
        seekBar.getThumb().setColorFilter(sharedPref.getFirstColor(), PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(sharedPref.getSecondColor(), PorterDuff.Mode.SRC_IN);

        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(volume_level);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setContentView(view);
        popupWindow.showOnAnchor(btn_volume, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER);
    }

    private void openTimeSelectDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        alt_bld.setTitle(getString(R.string.sleep_time));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dailog_selecttime, null);
        alt_bld.setView(dialogView);

        final TextView tv_min = dialogView.findViewById(R.id.tv_min);
        tv_min.setText("1 " + getString(R.string.min));
        FrameLayout frameLayout = dialogView.findViewById(R.id.fl);

        final IndicatorSeekBar seekbar = IndicatorSeekBar
                .with(BaseActivity.this)
                .min(1)
                .max(120)
                .progress(1)
                .thumbColor(sharedPref.getSecondColor())
                .indicatorColor(sharedPref.getFirstColor())
                .trackProgressColor(sharedPref.getFirstColor())
                .build();

        seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                tv_min.setText(seekParams.progress + " " + getString(R.string.min));
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        frameLayout.addView(seekbar);

        alt_bld.setPositiveButton(getString(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String hours = String.valueOf(seekbar.getProgress() / 60);
                String minute = String.valueOf(seekbar.getProgress() % 60);

                if (hours.length() == 1) {
                    hours = "0" + hours;
                }

                if (minute.length() == 1) {
                    minute = "0" + minute;
                }

                String totalTime = hours + ":" + minute;
                long total_timer = methods.convertToMili(totalTime) + System.currentTimeMillis();

                Random random = new Random();
                int id = random.nextInt(100);

                sharedPref.setSleepTime(true, total_timer, id);

                Intent intent = new Intent(BaseActivity.this, SleepTimeReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, total_timer, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, total_timer, pendingIntent);
                }
            }
        });
        alt_bld.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }



    private void openTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this, R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.sleep_time));
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dailog_time, null);
        builder.setView(dialogView);

        TextView textView = dialogView.findViewById(R.id.textView_time);

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton(getString(R.string.stop), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(BaseActivity.this, SleepTimeReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseActivity.this, sharedPref.getSleepID(), i, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
                sharedPref.setSleepTime(false, 0, 0);
            }
        });

        updateTimer(textView, sharedPref.getSleepTime());

        builder.show();
    }

    private void updateTimer(final TextView textView, long time) {
        long timeleft = time - System.currentTimeMillis();
        if (timeleft > 0) {
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeleft),
                    TimeUnit.MILLISECONDS.toMinutes(timeleft) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(timeleft) % TimeUnit.MINUTES.toSeconds(1));


            textView.setText(hms);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPref.getIsSleepTimeOn()) {
                        updateTimer(textView, sharedPref.getSleepTime());
                    }
                }
            }, 1000);
        }
    }

    private void changeLoginName() {
        if (menu_login != null) {
            if (Constants.isLogged) {
                menu_profile.setVisible(true);
                menu_login.setTitle(getResources().getString(R.string.logout));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.logout));
            } else {
                menu_profile.setVisible(false);
                menu_login.setTitle(getResources().getString(R.string.login));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.login));
            }
        }
    }

    @Override
    protected void onResume() {
        changeLoginName();

        Constants.isQuitDialog = true;
        setIfPlaying();

        if (isLoaded && Constants.isThemeChanged) {
            changeThemeColor();
            if (FragmentCity.adapterCity != null) {
                FragmentCity.adapterCity.notifyDataSetChanged();
                ViewCompat.setBackgroundTintList(FragmentCity.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }
            if (FragmentLanguage.adapterLanguage != null) {
                FragmentLanguage.adapterLanguage.notifyDataSetChanged();
                ViewCompat.setBackgroundTintList(FragmentLanguage.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }
            if (FragmentMain.appBarLayout != null) {
                FragmentMain.appBarLayout.setBackground(methods.getGradientDrawableToolbar());
            }

            if (FragmentOnDemandCat.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentOnDemandCat.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentOnDemandDetails.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentOnDemandDetails.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentSearch.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentSearch.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentFeaturedRadio.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentFeaturedRadio.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentLanguageDetails.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentLanguageDetails.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentSuggestion.button_submit != null) {
                ViewCompat.setBackgroundTintList(FragmentSuggestion.button_submit, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }
        }
        navigationView.setCheckedItem(navigationView.getMenu().findItem(R.id.nav_home).getItemId());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Constants.isAppOpen = false;
        try {
            if (Constants.exoPlayer_Radio != null && !Constants.exoPlayer_Radio.getPlayWhenReady()) {
                Intent intent = new Intent(getApplicationContext(), PlayService.class);
                intent.setAction(PlayService.ACTION_STOP);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}