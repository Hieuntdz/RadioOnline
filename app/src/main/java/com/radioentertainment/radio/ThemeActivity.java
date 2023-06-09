package com.radioentertainment.radio;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radioentertainment.adapter.AdapterTheme;
import com.radioentertainment.asyncTasks.LoadTheme;
import com.radioentertainment.interfaces.ThemeListener;
import com.radioentertainment.item.ItemTheme;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.RecyclerItemClickListener;
import com.radioentertainment.utils.SharedPref;
import com.radioentertainment.utils.StatusBarView;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ThemeActivity extends AppCompatActivity {

    Toolbar toolbar;
    StatusBarView statusBarView;
    CardView cardView;
    RecyclerView recyclerView;
    ArrayList<ItemTheme> arrayList;
    AdapterTheme adapterTheme;
    Methods methods;
    SharedPref sharedPref;
    CircularProgressBar progressBar;
    TextView textView_empty;
    LinearLayout ll_adView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        methods = new Methods(this);
        sharedPref = new SharedPref(this);

        statusBarView = findViewById(R.id.statusBar_theme);
        toolbar = this.findViewById(R.id.toolbar_theme);
        toolbar.setTitle(getString(R.string.themes));
        toolbar.setBackground(methods.getGradientDrawableToolbar());
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());
        statusBarView.setBackground(methods.getGradientDrawableToolbar());
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_adView = findViewById(R.id.ll_adView_theme);
        methods.showBannerAd(ll_adView);
        textView_empty = findViewById(R.id.textView_empty_theme);
        progressBar = findViewById(R.id.progressBar_theme);
        cardView = findViewById(R.id.cardView_theme);
        arrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView_theme);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ThemeActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ThemeActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Constants.isThemeChanged = true;
                sharedPref.setThemeColors(arrayList.get(position).getFirstColor(), arrayList.get(position).getSecondColor());
                toolbar.setBackground(methods.getGradientDrawableToolbar());
                statusBarView.setBackground(methods.getGradientDrawableToolbar());
            }
        }));

        arrayList.add(new ItemTheme("0", ContextCompat.getColor(ThemeActivity.this, R.color.colorPrimaryDark), ContextCompat.getColor(ThemeActivity.this, R.color.colorPrimary)));

        if (methods.isConnectingToInternet()) {
            LoadTheme loadTheme = new LoadTheme(new ThemeListener() {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemTheme> arrayListTheme) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayList.addAll(arrayListTheme);
                            setAdapter();
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            setAdapter();
                        }
                    } else {

                        setAdapter();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }, methods.getAPIRequest(Constants.METHOD_THEMES, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadTheme.execute();
        } else {
            methods.showToast(getString(R.string.internet_not_connected));
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

    public void setAdapter() {
        adapterTheme = new AdapterTheme(ThemeActivity.this, arrayList);
        recyclerView.setAdapter(adapterTheme);

        if (arrayList.size() > 0) {
            textView_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}