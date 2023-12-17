package com.cd.statussaver.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cd.statussaver.R;
import com.cd.statussaver.util.AppLangSessionManager;
import com.cd.statussaver.util.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.onesignal.OneSignal;

import java.util.Locale;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity {
    SplashScreen activity;
    Context context;
    AppUpdateManager appUpdateManager;
    AppLangSessionManager appLangSessionManager;


    public  static String bannerId,NativeId,InterstitialID,appOpen;
    public  static String bannerId2,NativeId2,InterstitialID2,appOpen2;
    public  static String bannerId3,NativeId3,InterstitialID3,appOpen3;
    public static String Qureka_Id,ONESIGNAL_APP_ID;
    private InterstitialAd interstitialAd;
    public  static  boolean IsIdLoded;

    public static AppOpenAd appOpenAd = null;
    public static boolean isLoadingAd = false;
    public static boolean isShowingAd = false;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
        if(sharedPreferences.getInt("policy",0)==0){
            setContentView(R.layout.app_intro);

            sharedPref = getSharedPreferences("Data", MODE_PRIVATE);
            TextView btn = findViewById(R.id.continue_btn);
            TextView privacy_policy = findViewById(R.id.policy);

            privacy_policy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("android.intent.action.VIEW").setData(Uri.parse("https://sites.google.com/view/videodownloderpolicy/home"));
                    startActivity(intent);
                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPrivacyDailog();
                }
            });

        }else {
            setContentView(R.layout.activity_splash_screen);
            CheckInternet();
        }




        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        IsIdLoded=false;
        context = activity = this;
        appUpdateManager = AppUpdateManagerFactory.create(context);
        appLangSessionManager = new AppLangSessionManager(activity);
        UpdateApp();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setLocale(appLangSessionManager.getLanguage());

    }

    public void CheckInternet(){


        if(isNetworkAvailable()){
             RequestAdsId();
        }else {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SplashScreen.this, R.style.CustomBottomSheetDialogTheme);
            View bottom = LayoutInflater.from(SplashScreen.this).inflate(R.layout.no_internet, null);
            bottomSheetDialog.setContentView(bottom);
            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);
            TextView refresh = bottom.findViewById(R.id.tryagain);

            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isNetworkAvailable()){
                        bottomSheetDialog.dismiss();
                        RequestAdsId();
                    }else {
                        Toast.makeText(activity, "Please Check Internet Connection Again..", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        appLangSessionManager = new AppLangSessionManager(activity);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, IMMEDIATE, activity, 101);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void HomeScreen() {

    }

    public void UpdateApp() {
        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, IMMEDIATE, activity, 101);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    HomeScreen();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                HomeScreen();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode != RESULT_OK) {
                HomeScreen();
            } else {
                HomeScreen();
            }
        }
    }


    public void setLocale(String lang) {
        if (lang.equals("")){
            lang="en";
        }
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);



    }

    public void RequestAdsId()
    {

        String url ="https://unisoftex.com/ads/testads.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    appOpen = jsonObject.getString("AppOpenID");
                    InterstitialID = jsonObject.getString("InterstitialID");
                    NativeId = jsonObject.getString("NativeID");
                    bannerId = jsonObject.getString("BannerID");

                    InterstitialID2 = jsonObject.getString("InterstitialID_one");
                    NativeId2 = jsonObject.getString("NativeID_one");
                    bannerId2 = jsonObject.getString("BannerID_one");
                    appOpen2 = jsonObject.getString("AppOpenID_one");



                    InterstitialID3 = jsonObject.getString("InterstitialID_two");
                    NativeId3 = jsonObject.getString("NativeID_two");
                    bannerId3 = jsonObject.getString("BannerID_two");
                    appOpen3 = jsonObject.getString("AppOpenID_two");
                    RequestInterstitial(SplashScreen.InterstitialID);

                    Qureka_Id = jsonObject.getString("Qureka_ID");
                    ONESIGNAL_APP_ID = jsonObject.getString("One_Signal_App_ID");

                    AppOpenManager.AD_UNIT_ID=SplashScreen.appOpen;


                    OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

                    // OneSignal Initialization
                    OneSignal.initWithContext(SplashScreen.this);
                    OneSignal.setAppId(ONESIGNAL_APP_ID);

                } catch (JSONException e) {

                    ShowtestAds();
                    e.printStackTrace();
                    Log.e("AdsId",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ShowtestAds();
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                Log.e("AdsId",error.toString());

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SplashScreen.this);
        requestQueue.add(stringRequest);



    }

    public void RequestInterstitial(String IntersId) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                IntersId,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                        SplashScreen.this.interstitialAd = interstitialAd;
                        showInterstitial();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @RequiresApi(api = Build.VERSION_CODES.Q)
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                        startActivity(intent);
                                        SplashScreen.this.interstitialAd = null;
                                        finish();
                                    }
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                        startActivity(intent);

                                        SplashScreen.this.interstitialAd = null;
                                        finish();
                                    }
                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitialAd = null;
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();


                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(SplashScreen.this);
        } else {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    public void ShowtestAds(){

        bannerId = "/6499/example/banner";
        NativeId = "/6499/example/native";
        InterstitialID ="/6499/example/interstitial";

        bannerId2 = "/6499/example/banner";
        NativeId2 = "/6499/example/native";
        InterstitialID2 ="/6499/example/interstitial";

        bannerId3 = "/6499/example/banner";
        NativeId3 = "/6499/example/native";
        InterstitialID3 ="/6499/example/interstitial";
    }



    public boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) SplashScreen.this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowPrivacyDailog() {

        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.reminder_dailog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT ));
        dialog.show();

        TextView textView = dialog.findViewById(R.id.des);

        Button refuse =dialog.findViewById(R.id.refuse);
        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        Button agree = dialog.findViewById(R.id.agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("policy",1);
                editor.apply();

                setContentView(R.layout.activity_splash_screen);
                CheckInternet();

            }
        });



    }
}
