package com.radioentertainment.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.radioentertainment.adapter.AdapterHomeCity;
import com.radioentertainment.adapter.AdapterHomeLanguage;
import com.radioentertainment.adapter.AdapterMoreApp;
import com.radioentertainment.adapter.AdapterSlideOnDemand;
import com.radioentertainment.asyncTasks.LoadCity;
import com.radioentertainment.asyncTasks.LoadLanguage;
import com.radioentertainment.interfaces.AdConsentListener;
import com.radioentertainment.interfaces.CityClickListener;
import com.radioentertainment.interfaces.CityListener;
import com.radioentertainment.interfaces.InterAdListener;
import com.radioentertainment.interfaces.LanguageListener;
import com.radioentertainment.item.ItemCity;
import com.radioentertainment.item.ItemLanguage;
import com.radioentertainment.item.ItemMoreApp;
import com.radioentertainment.utils.AdConsent;
import com.radioentertainment.utils.RecyclerItemClickListener;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.radioentertainment.adapter.AdapterRadioList;
import com.radioentertainment.asyncTasks.LoadHome;
import com.radioentertainment.asyncTasks.LoadRadioList;
import com.radioentertainment.interfaces.HomeListener;
import com.radioentertainment.interfaces.RadioListListener;
import com.radioentertainment.item.ItemOnDemandCat;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.radio.BaseActivity;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentHome extends Fragment implements BaseSliderView.OnSliderClickListener {

    private AdConsent adConsent;
    private Methods methods;
    private SliderLayout sliderLayout;
    private ArrayList<Object> arrayList_radio_latest, arrayList_radio_featured, arrayList_radio_mostviewed, arrayList_radio_all;
    private ArrayList<ItemCity> arrayList_radio_city;
    private ArrayList<ItemLanguage> arrayList_radio_language;
    private ArrayList<ItemOnDemandCat> arrayList_ondemandcat;
    private ArrayList<ItemMoreApp> arrayList_moreapp;
    public static AdapterRadioList adapterRadioList, adapterRadioList_mostview, adapterRadioList_featured, adapterRadioList_all;
    public  AdapterSlideOnDemand adapterSlideOnDemand;
    private CircularProgressBar progressBar;
    private NestedScrollView scrollView;
    private ViewPager2 viewpager_slide;
    private RecyclerView recyclerView, recyclerView_mostview, recyclerView_featured, recyclerView_city, recyclerView_language, recyclerView_all_radio, recyclerView_more_app;
    private LinearLayout ll_ad;
    private Boolean isLoaded = false, isVisible = false;
    private RelativeLayout rl_featured, rl_trending, rl_latest, rl_city, rl_language, rl_all_radio;
    private AdapterHomeCity adapterHomeCity;
    private AdapterHomeLanguage adapterHomeLanguage;
    private int page = 1;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = viewpager_slide.getCurrentItem();
            if(currentPosition == arrayList_ondemandcat.size() - 1){
                viewpager_slide.setCurrentItem(0);
            } else {
                viewpager_slide.setCurrentItem(currentPosition + 1);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        methods = new Methods(getActivity());

        arrayList_radio_latest = new ArrayList<>();
        arrayList_radio_mostviewed = new ArrayList<>();
        arrayList_radio_all = new ArrayList<>();
        arrayList_radio_featured = new ArrayList<>();
        arrayList_radio_city = new ArrayList<>();
        arrayList_radio_language = new ArrayList<>();
        arrayList_ondemandcat = new ArrayList<>();
        arrayList_moreapp = new ArrayList<>();
        arrayList_moreapp.add(new ItemMoreApp(0, "Telegram0", "https://play.google.com/store/apps/details?id=org.telegram.messenger", "https://play-lh.googleusercontent.com/ZU9cSsyIJZo6Oy7HTHiEPwZg0m2Crep-d5ZrfajqtsH-qgUXSqKpNA2FpPDTn-7qA5Q=s180-rw"));
        arrayList_moreapp.add(new ItemMoreApp(1, "Telegram1", "https://play.google.com/store/apps/details?id=org.telegram.messenger", "https://play-lh.googleusercontent.com/ZU9cSsyIJZo6Oy7HTHiEPwZg0m2Crep-d5ZrfajqtsH-qgUXSqKpNA2FpPDTn-7qA5Q=s180-rw"));
        arrayList_moreapp.add(new ItemMoreApp(2, "Telegram2", "https://play.google.com/store/apps/details?id=org.telegram.messenger", "https://play-lh.googleusercontent.com/ZU9cSsyIJZo6Oy7HTHiEPwZg0m2Crep-d5ZrfajqtsH-qgUXSqKpNA2FpPDTn-7qA5Q=s180-rw"));
        arrayList_moreapp.add(new ItemMoreApp(3, "Telegram3", "https://play.google.com/store/apps/details?id=org.telegram.messenger", "https://play-lh.googleusercontent.com/ZU9cSsyIJZo6Oy7HTHiEPwZg0m2Crep-d5ZrfajqtsH-qgUXSqKpNA2FpPDTn-7qA5Q=s180-rw"));
        arrayList_moreapp.add(new ItemMoreApp(4, "Telegram4", "https://play.google.com/store/apps/details?id=org.telegram.messenger", "https://play-lh.googleusercontent.com/ZU9cSsyIJZo6Oy7HTHiEPwZg0m2Crep-d5ZrfajqtsH-qgUXSqKpNA2FpPDTn-7qA5Q=s180-rw"));


        ll_ad = rootView.findViewById(R.id.ll_adView);

        adConsent = new AdConsent(getContext(), new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                methods.showBannerAd(ll_ad);
            }
        });

        rl_featured = rootView.findViewById(R.id.rl_featured);
        rl_featured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCatHome f1 = new FragmentCatHome();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                Bundle bundle = new Bundle();
                bundle.putString("type", "featured");
                f1.setArguments(bundle);

                ft.add(R.id.frame_content_home, f1, getString(R.string.all_featured));
                ft.addToBackStack(getString(R.string.all_featured));
                ft.commit();
                ((BaseActivity)getActivity()).getSupportActionBar().setTitle(R.string.all_featured);
            }
        });

        rl_trending = rootView.findViewById(R.id.rl_trending);
        rl_trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCatHome f1 = new FragmentCatHome();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));

                Bundle bundle = new Bundle();
                bundle.putString("type", "trending");
                f1.setArguments(bundle);

                ft.add(R.id.frame_content_home, f1, getString(R.string.all_trending));
                ft.addToBackStack(getString(R.string.all_trending));
                ft.commit();
                ((BaseActivity)getActivity()).getSupportActionBar().setTitle(R.string.all_trending);
            }
        });

        rl_latest = rootView.findViewById(R.id.rl_latestVideo);
        rl_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCatHome f1 = new FragmentCatHome();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));

                Bundle bundle = new Bundle();
                bundle.putString("type", "latest");
                f1.setArguments(bundle);

                ft.add(R.id.frame_content_home, f1, getString(R.string.all_latest));
                ft.addToBackStack(getString(R.string.all_latest));
                ft.commit();
                ((BaseActivity)getActivity()).getSupportActionBar().setTitle(R.string.all_latest);
            }
        });

        rl_city = (RelativeLayout) rootView.findViewById(R.id.rl_city);
        rl_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCity f1 = new FragmentCity();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.frame_content_home, f1, getString(R.string.all_city));
                ft.addToBackStack(getString(R.string.all_city));
                ft.commit();
                ((BaseActivity)getActivity()).getSupportActionBar().setTitle(R.string.all_city);
            }
        });

        rl_all_radio = rootView.findViewById(R.id.rl_all_radio);
        rl_all_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAllRadio f1 = new FragmentAllRadio();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.frame_content_home, f1, getString(R.string.all_radio));
                ft.addToBackStack(getString(R.string.all_radio));
                ft.commit();
                ((BaseActivity)getActivity()).getSupportActionBar().setTitle(R.string.all_radio);
            }
        });

        rl_language= (RelativeLayout)rootView.findViewById(R.id.rl_language);
        rl_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentLanguage f1 = new FragmentLanguage();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.frame_content_home, f1, getString(R.string.all_language));
                ft.addToBackStack(getString(R.string.all_language));
                ft.commit();
                ((BaseActivity)getActivity()).getSupportActionBar().setTitle(R.string.all_language);
            }
        });

        progressBar = rootView.findViewById(R.id.progressBar_home);
        scrollView = rootView.findViewById(R.id.scrollView_home);

        //sliderLayout = rootView.findViewById(R.id.sliderLayout);
        viewpager_slide = rootView.findViewById(R.id.viewpager_slide);

        recyclerView = rootView.findViewById(R.id.recyclerView_radiolist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView_mostview = rootView.findViewById(R.id.recyclerView_mostview);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView_mostview.setLayoutManager(llm);
        recyclerView_mostview.setHasFixedSize(true);

        recyclerView_featured = rootView.findViewById(R.id.recyclerView_featured);
        LinearLayoutManager llm_featured = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView_featured.setLayoutManager(llm_featured);
        recyclerView_featured.setHasFixedSize(true);

        recyclerView_city = rootView.findViewById(R.id.recyclerView_city);
        LinearLayoutManager llc = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView_city.setLayoutManager(llc);
        recyclerView_city.setHasFixedSize(true);

        recyclerView_all_radio = rootView.findViewById(R.id.recyclerView_all_radio);
        LinearLayoutManager lla = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView_all_radio.setLayoutManager(lla);
        recyclerView_all_radio.setHasFixedSize(true);

        recyclerView_language = rootView.findViewById(R.id.recyclerView_language);
        LinearLayoutManager ll_lan = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView_language.setLayoutManager(ll_lan);
        recyclerView_language.setHasFixedSize(true);

        recyclerView_more_app = rootView.findViewById(R.id.recyclerView_more_app);
        recyclerView_more_app.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView_more_app.setHasFixedSize(true);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView_mostview.setNestedScrollingEnabled(false);
        recyclerView_featured.setNestedScrollingEnabled(false);
        recyclerView_city.setNestedScrollingEnabled(false);
        recyclerView_more_app.setNestedScrollingEnabled(false);

//        if (isVisible && !isLoaded) {
//            loadList();
//            isLoaded = true;
//        }

        loadList();

        setHasOptionsMenu(true);
        return rootView;
    }

    private void loadList() {
        if (methods.isConnectingToInternet()) {
            LoadRadioList loadRadioList = new LoadRadioList(new RadioListListener() {
                @Override
                public void onStart() {
                    if (getActivity() != null) {
                        arrayList_radio_latest.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList_radio_latest.addAll(arrayListRadio);
                                if (arrayList_radio_latest.size() > 0) {
                                    if(arrayList_radio_latest.size() <= 6){
                                        adapterRadioList = new AdapterRadioList(getActivity(), arrayList_radio_latest, "linear");
                                        recyclerView.setAdapter(adapterRadioList);
                                    }else{
                                        ArrayList<Object> list = new ArrayList<>();
                                        for (int i = 0; i < 6; i++){
                                            list.add(arrayList_radio_latest.get(i));
                                        }
                                        adapterRadioList = new AdapterRadioList(getActivity(), list, "linear");
                                        recyclerView.setAdapter(adapterRadioList);
                                    }
                                    if (Constants.arrayList_radio.size() == 0) {
                                        for(Object o : arrayList_radio_latest){
                                            if(o instanceof ItemRadio){
                                                Constants.arrayList_radio.add((ItemRadio) o);
                                            }
                                        }
                                        ((BaseActivity) getActivity()).changeText(Constants.arrayList_radio.get(0));
                                    }
                                }
                                loadHome();
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        }
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_LATEST, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadRadioList.execute();
        } else {
            methods.showToast(getString(R.string.internet_not_connected));
        }
    }

    private void loadHome() {
        LoadHome loadHome = new LoadHome(new HomeListener() {
            @Override
            public void onStart() {
                if (getActivity() != null) {
                    arrayList_radio_mostviewed.clear();
                    arrayList_radio_featured.clear();
                }
            }

            @Override
            public void onEnd(String success, ArrayList<ItemRadio> arrayList_featured, ArrayList<ItemRadio> arrayList_mostviewed, ArrayList<ItemOnDemandCat> arrayList_ondemand_cat) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        arrayList_radio_featured.addAll(arrayList_featured);
                        arrayList_radio_mostviewed.addAll(arrayList_mostviewed);
                        arrayList_ondemandcat.addAll(arrayList_ondemand_cat);

                        if (arrayList_mostviewed.size() > 0) {
                            if(arrayList_radio_featured.size() <= 6){
                                adapterRadioList_featured = new AdapterRadioList(getActivity(), arrayList_radio_featured, "linear");
                                recyclerView_featured.setAdapter(adapterRadioList_featured);
                            }else{
                                ArrayList<Object> list = new ArrayList<>();
                                for (int i = 0; i < 6; i++){
                                    list.add(arrayList_radio_featured.get(i));
                                }
                                adapterRadioList_featured = new AdapterRadioList(getActivity(), list, "linear");
                                recyclerView_featured.setAdapter(adapterRadioList_featured);
                            }
                            if(arrayList_radio_mostviewed.size() <= 6){
                                adapterRadioList_mostview = new AdapterRadioList(getActivity(), arrayList_radio_mostviewed, "linear");
                                recyclerView_mostview.setAdapter(adapterRadioList_mostview);
                            }else{
                                ArrayList<Object> list = new ArrayList<>();
                                for (int i = 0; i < 6; i++){
                                    list.add(arrayList_radio_mostviewed.get(i));
                                }
                                adapterRadioList_mostview = new AdapterRadioList(getActivity(), list, "linear");
                                recyclerView_mostview.setAdapter(adapterRadioList_mostview);
                            }
//                            adapterRadioList_mostview = new AdapterRadioList(getActivity(), arrayList_radio_mostviewed, "linear");
//                            recyclerView_mostview.setAdapter(adapterRadioList_mostview);
//                            adapterRadioList_featured = new AdapterRadioList(getActivity(), arrayList_radio_featured, "linear");
//                            recyclerView_featured.setAdapter(adapterRadioList_featured);
                            adConsent.checkForConsent();

                            ((BaseActivity) getContext()).LoadDemandList(arrayList_ondemandcat);

                            recyclerView_more_app.setAdapter(new AdapterMoreApp(arrayList_moreapp));
                            recyclerView_more_app.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList_moreapp.get(position).getUrl())));
                                }
                            }));

                            loadCity();
                            loadAllRadio();
                            loadLanguage();
                            loadSlider();
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                    }
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_HOME, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadHome.execute();
    }



    private void loadCity() {
        LoadCity loadCity = new LoadCity(new CityListener() {
            @Override
            public void onStart() {
                if (getActivity() != null){
                    arrayList_radio_city.clear();
                }
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCity> arrayListCity) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayList_radio_city.addAll(arrayListCity);
                            setAdapterCityHome();
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            setEmptyCity();
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                        setEmptyCity();
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_CITY, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadCity.execute();
    }

    private void loadAllRadio() {
        LoadRadioList loadRadioList = new LoadRadioList(new RadioListListener() {
            @Override
            public void onStart() {
                if (getActivity() != null) {

                }
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            page = page + 1;
                            arrayList_radio_all.addAll(arrayListRadio);
                            setAdapterAllRadio();
                            setEmptyAllRadio();
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            setEmptyAllRadio();
                        }
                    } else {
                        setEmptyAllRadio();
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_ALL_RADIO, 1, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadRadioList.execute();
    }

    private void loadLanguage(){
        LoadLanguage loadLanguage = new LoadLanguage(new LanguageListener() {
            @Override
            public void onStart() {
                if(getActivity() != null){
                    arrayList_radio_language.clear();
                }
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemLanguage> arrayListLanguage) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayList_radio_language.addAll(arrayListLanguage);
                            setAdapterLanguageHome();
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            setEmptyLanguage();
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                        setEmptyLanguage();
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_LANGUAGE, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadLanguage.execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            if (!s.trim().equals("") && getActivity() != null) {
                Constants.search_text = s.replace(" ", "%20");

                FragmentManager fm = getFragmentManager();
                FragmentSearch f1 = new FragmentSearch();
                FragmentTransaction ft = fm.beginTransaction();

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.content_frame_activity, f1, getString(R.string.menu_search));
                ft.addToBackStack(getString(R.string.menu_search));
                ft.commit();
                ((BaseActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.menu_search));
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void loadSlider() {
        InterAdListener interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentOnDemandDetails f1 = new FragmentOnDemandDetails(false);
                FragmentTransaction ft = fm.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", arrayList_ondemandcat.get(position));
                f1.setArguments(bundle);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.add(R.id.content_frame_activity, f1, arrayList_ondemandcat.get(position).getName());
                ft.addToBackStack(arrayList_ondemandcat.get(position).getName());
                ft.commit();
            }
        };

        Methods methods = new Methods(getActivity(), interAdListener);


        //       for (int i = 0; i < arrayList_ondemandcat.size(); i++) {
//            TextSliderView textSliderView = new TextSliderView(getActivity());
//            textSliderView.frequency("(" + arrayList_ondemandcat.get(i).getTotalItems() + ")")
//                    .language("")
//                    .name(arrayList_ondemandcat.get(i).getName())
//                    .image(arrayList_ondemandcat.get(i).getImage())
//                    .setScaleType(BaseSliderView.ScaleType.Fit)
//                    .setOnSliderClickListener(this);
//
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle().putInt("pos", i);
//
//            sliderLayout.addSlider(textSliderView);
//        }
//        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
//        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
//        sliderLayout.setCustomAnimation(new DescriptionAnimation());
//        sliderLayout.setDuration(5000);

        Handler handler = new Handler(Looper.getMainLooper());
        if(runnable != null){
            runnable = null;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentPosition = viewpager_slide.getCurrentItem();
                if(currentPosition == arrayList_ondemandcat.size() - 1){
                    viewpager_slide.setCurrentItem(0);
                } else {
                    viewpager_slide.setCurrentItem(currentPosition + 1);
                }
            }
        };
    //setting ViewPager 2
        viewpager_slide.setOffscreenPageLimit(3);//3 item
        viewpager_slide.setClipToPadding(false);
        viewpager_slide.setClipChildren(false);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r*0.15f);
            }
        });
        viewpager_slide.setPageTransformer(compositePageTransformer);
        adapterSlideOnDemand = new AdapterSlideOnDemand(arrayList_ondemandcat, methods);
        AdapterSlideOnDemand.setSelected_index(0);
        viewpager_slide.setAdapter(adapterSlideOnDemand);
        viewpager_slide.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AdapterSlideOnDemand.setSelected_index(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2500);
                adapterSlideOnDemand.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
//        FragmentManager fm = FragmentHome.this.getParentFragment().getFragmentManager();
//        FragmentOnDemandDetails f1 = new FragmentOnDemandDetails();
//        FragmentTransaction ft = fm.beginTransaction();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("item", arrayList_ondemandcat.get(slider.getBundle().getInt("pos")));
//        f1.setArguments(bundle);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.hide(FragmentHome.this.getParentFragment());
//        ft.add(R.id.content_frame_activity, f1, arrayList_ondemandcat.get(slider.getBundle().getInt("pos")).getName());
//        ft.addToBackStack(arrayList_ondemandcat.get(slider.getBundle().getInt("pos")).getName());
//        ft.commit();
//        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(arrayList_ondemandcat.get(slider.getBundle().getInt("pos")).getName());
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        isVisible = isVisibleToUser;
//        if (isVisibleToUser && isAdded() && !isLoaded) {
//            loadList();
//            isLoaded = true;
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }

    private void setEmptyCity(){
        if(arrayList_radio_city.size() <= 0){
            recyclerView_city.setVisibility(View.GONE);
            rl_city.setVisibility(View.GONE);
        }
    }

    private void setEmptyLanguage(){
        if(arrayList_radio_language.size() <=0){
            recyclerView_language.setVisibility(View.GONE);
            rl_language.setVisibility(View.GONE);
        }
    }
    private void setAdapterCityHome(){
        if(arrayList_radio_city.size() <= 6){
            adapterHomeCity = new AdapterHomeCity(getContext(), arrayList_radio_city, new CityClickListener() {
                @Override
                public void onClick() {
                    FragmentManager fm = getFragmentManager();
                    FragmentCityDetails f1 = new FragmentCityDetails();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                    ft.add(R.id.content_frame_activity, f1, Constants.itemCity.getName());
                    ft.addToBackStack(Constants.itemCity.getName());
                    ft.commit();
                    ((BaseActivity) getActivity()).getSupportActionBar().setTitle(Constants.itemCity.getName());
                }
            });
            recyclerView_city.setAdapter(adapterHomeCity);
            setEmptyCity();
        }else{
            ArrayList<ItemCity> list = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                list.add(arrayList_radio_city.get(i));
            }
            adapterHomeCity = new AdapterHomeCity(getContext(), list, new CityClickListener() {
                @Override
                public void onClick() {
                    FragmentManager fm = getFragmentManager();
                    FragmentCityDetails f1 = new FragmentCityDetails();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    //ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                    ft.add(R.id.frame_content_home, f1, Constants.itemCity.getName());
                    ft.addToBackStack(Constants.itemCity.getName());
                    ft.commit();
                    ((BaseActivity) getActivity()).getSupportActionBar().setTitle(Constants.itemCity.getName());
                }
            });
            recyclerView_city.setAdapter(adapterHomeCity);
            setEmptyCity();
        }
    }

    private void setAdapterAllRadio(){
        if(arrayList_radio_all.size() <= 6){
            adapterRadioList_all = new AdapterRadioList(getActivity(), arrayList_radio_all, "linear");
            recyclerView_all_radio.setAdapter(adapterRadioList_all);
        }else{
            ArrayList<Object> list = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                list.add(arrayList_radio_all.get(i));
            }
            adapterRadioList_all = new AdapterRadioList(getActivity(), list, "linear");
            recyclerView_all_radio.setAdapter(adapterRadioList_all);
        }
    }

    private void setEmptyAllRadio(){
        if(arrayList_radio_all.size() <=0){
            recyclerView_all_radio.setVisibility(View.GONE);
            rl_all_radio.setVisibility(View.GONE);
        }
    }
    private void setAdapterLanguageHome(){
        if(arrayList_radio_language.size() <= 6){
            adapterHomeLanguage = new AdapterHomeLanguage(getActivity(), arrayList_radio_language, new CityClickListener() {
                @Override
                public void onClick() {
                    FragmentManager fm = getFragmentManager();
                    FragmentLanguageDetails f1 = new FragmentLanguageDetails();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                    ft.add(R.id.frame_content_home, f1, Constants.itemLanguage.getName());
                    ft.addToBackStack(Constants.itemLanguage.getName());
                    ft.commit();
                    ((BaseActivity) getActivity()).getSupportActionBar().setTitle(Constants.itemLanguage.getName());
                }
            });
            recyclerView_language.setAdapter(adapterHomeLanguage);
            setEmptyLanguage();
        }else{
            ArrayList<ItemLanguage> list = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                list.add(arrayList_radio_language.get(i));
            }
            adapterHomeLanguage = new AdapterHomeLanguage(getActivity(), list, new CityClickListener() {
                @Override
                public void onClick() {
                    FragmentManager fm = getFragmentManager();
                    FragmentLanguageDetails f1 = new FragmentLanguageDetails();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    //ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                    ft.add(R.id.content_frame_activity, f1, Constants.itemLanguage.getName());
                    ft.addToBackStack(Constants.itemLanguage.getName());
                    ft.commit();
                    ((BaseActivity) getActivity()).getSupportActionBar().setTitle(Constants.itemLanguage.getName());
                }
            });
            recyclerView_language.setAdapter(adapterHomeLanguage);
            setEmptyLanguage();
        }
    }

}
