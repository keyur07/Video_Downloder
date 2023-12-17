package com.cd.statussaver.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cd.statussaver.R;
import com.cd.statussaver.adapter.FBStoriesListAdapter;
import com.cd.statussaver.adapter.FbStoryUserListAdapter;
import com.cd.statussaver.api.CommonClassForAPI;
import com.cd.statussaver.interfaces.UserListInterface;
import com.cd.statussaver.model.FBStoryModel.EdgesModel;
import com.cd.statussaver.model.FBStoryModel.MediaModel;
import com.cd.statussaver.model.FBStoryModel.NodeModel;
import com.cd.statussaver.model.story.TrayModel;
import com.cd.statussaver.util.AppLangSessionManager;
import com.cd.statussaver.util.SharePrefs;
import com.cd.statussaver.util.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.ContentValues.TAG;
import static com.cd.statussaver.util.Utils.RootDirectoryFacebook;
import static com.cd.statussaver.util.Utils.createFileFolder;
import static com.cd.statussaver.util.Utils.startDownload;


public class FacebookActivity extends AppCompatActivity implements UserListInterface {

    FacebookActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String videoUrl;
    private ClipboardManager clipBoard;
    ArrayList<NodeModel> edgeModelList;

    private ImageView imBack,imInfo;
    private TextView loginBtn1,tvPaste,tvLogin,tvViewStories,activityname,facebook,download;
    private EditText etText;
    private ProgressBar prLoadingBar;

    private RecyclerView RVStories;

    private RecyclerView RVUserList;

    private Switch SwitchLogin;

    private RelativeLayout LLOpenInstagram,RLLoginInstagram;


    AppLangSessionManager appLangSessionManager;
    private UnifiedNativeAd nativeAd;
    private String strName = "facebook";
    private String strNameSecond = "fb";
    FbStoryUserListAdapter fbStoryUserListAdapter;
    FBStoriesListAdapter fbStoriesListAdapter;
    private InterstitialAd interstitialAd;
    private RelativeLayout Rl1;
    private RelativeLayout adContainerView;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);


        BannerAd(SplashScreen.bannerId3);

        imBack = findViewById(R.id.imBack);
        loginBtn1 = findViewById(R.id.login_btn1);
        tvPaste = findViewById(R.id.tv_paste);
        tvLogin = findViewById(R.id.tvLogin);
        tvViewStories = findViewById(R.id.tvViewStories);
        etText = findViewById(R.id.et_text);
        SwitchLogin = findViewById(R.id.SwitchLogin);
        LLOpenInstagram = findViewById(R.id.LLOpenInstagram);
        RLLoginInstagram = findViewById(R.id.RLLoginInstagram);
        imInfo = findViewById(R.id.imInfo);
        prLoadingBar = findViewById(R.id.pr_loading_bar);
        RVStories = findViewById(R.id.RVStories);
        RVUserList = findViewById(R.id.RVUserList);
        activityname = findViewById(R.id.activityname);
        facebook = findViewById(R.id.insta);
        download = findViewById(R.id.download);
        
        Rl1 = findViewById(R.id.rl1);

        RLLoginInstagram.setBackground(getDrawable(R.drawable.background_for_facebook));
        Rl1.setBackground(getDrawable(R.drawable.background_for_facebook));
        etText.setBackground(getDrawable(R.drawable.ads_back_fb));

        loginBtn1.setBackground(getDrawable(R.drawable.fb_backcolor));
        tvPaste.setBackground(getDrawable(R.drawable.fb_backcolor));
        tvLogin.setBackground(getDrawable(R.drawable.fb_backcolor));
        download.setBackground(getDrawable(R.drawable.fb_backcolor));
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(FacebookActivity.this,GalleryActivity.class));
            }
        });

        RequestInterstitial(SplashScreen.InterstitialID);

        facebook.setBackground(getDrawable(R.drawable.fb_backcolor));
        facebook.setText("Facebook");
        activityname.setText("Facebook");

        NativeAds(SplashScreen.NativeId3);
        activity = this;

        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());

        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();



        Window window = activity.getWindow();
        Drawable background = activity.getResources().getDrawable(R.drawable.background_for_facebook);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvPaste.performClick();
            }
        }, 500);


    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvPaste.performClick();
            }
        }, 500);
    }



    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
       // tvLoginInstagram.setText(getResources().getString(R.string.download_stories));
        imBack.setOnClickListener(view -> onBackPressed());

        tvPaste.setOnClickListener(view -> {
            pasteText();
        });

      //  binding.tvAppName.setText(activity.getResources().getString(R.string.facebook_app_name));

      /*  binding.imInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);

            }
        });
*/
/*

        Glide.with(activity)
                .load(R.drawable.fb1)
                .into(binding.layoutHowTo.imHowto1);

        Glide.with(activity)
                .load(R.drawable.fb2)
                .into(binding.layoutHowTo.imHowto2);

        Glide.with(activity)
                .load(R.drawable.fb3)
                .into(binding.layoutHowTo.imHowto3);

        Glide.with(activity)
                .load(R.drawable.fb4)
                .into(binding.layoutHowTo.imHowto4);
*/


     /*   binding.layoutHowTo.tvHowTo1.setText(getResources().getString(R.string.opn_fb));
        binding.layoutHowTo.tvHowTo3.setText(getResources().getString(R.string.opn_fb));*/
      /*  if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISSHOWHOWTOFB)) {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISSHOWHOWTOFB,true);
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
        }else {
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.GONE);
        }*/

       loginBtn1.setOnClickListener(v -> {
            String ll = etText.getText().toString();
            if (ll.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(ll).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                getFacebookData();

            }


        });
     //   TVTitle.setOnClickListener(v -> Utils.OpenApp(activity, "com.facebook.katana"));

        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
       RVUserList.setLayoutManager(mLayoutManager);
        RVUserList.setNestedScrollingEnabled(false);
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);


        edgeModelList = new ArrayList<>();
        fbStoryUserListAdapter = new FbStoryUserListAdapter(activity, edgeModelList, FacebookActivity.this);
        RVUserList.setAdapter(fbStoryUserListAdapter);


        if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISFBLOGIN)) {
            layoutCondition();
            getFacebookUserData();
           SwitchLogin.setChecked(true);
        }else {
            SwitchLogin.setChecked(false);
        }

        RLLoginInstagram.setOnClickListener(v -> tvLogin.performClick());

       tvLogin.setOnClickListener(v -> {
            if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISFBLOGIN)) {
                Intent intent = new Intent(activity,
                        FBLoginActivity.class);
                startActivityForResult(intent, 100);
            }else {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISFBLOGIN, false);
                    SharePrefs.getInstance(activity).putString(SharePrefs.FBKEY, "");
                    SharePrefs.getInstance(activity).putString(SharePrefs.FBCOOKIES, "");
                    if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISFBLOGIN)){
                       SwitchLogin.setChecked(true);
                    }else {
                       SwitchLogin.setChecked(false);
                       RVUserList.setVisibility(View.GONE);
                      RVStories.setVisibility(View.GONE);
                       tvLogin.setVisibility(View.VISIBLE);
                       tvViewStories.setText(activity.getResources().getText(R.string.view_stories));
                    }
                    dialog.cancel();

                });
                ab.setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = ab.create();
                alert.setTitle(getResources().getString(R.string.do_u_want_to_download_media_from_pvt));
                alert.show();
            }

        });
        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 2);
       RVStories.setLayoutManager(mLayoutManager1);
        RVStories.setNestedScrollingEnabled(false);
        mLayoutManager1.setOrientation(RecyclerView.VERTICAL);
    }
    public void layoutCondition(){
       tvViewStories.setText(activity.getResources().getString(R.string.stories));
       tvLogin.setVisibility(View.GONE);

    }


    private void getFacebookData() {
        try {
            createFileFolder();
            URL url = new URL(etText.getText().toString());
            String host = url.getHost();
            if (host.contains(strName) || host.contains(strNameSecond)) {
                Utils.showProgressDialog(activity);
                new CallGetFacebookData().execute(etText.getText().toString());

            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pasteText() {
        try {
            etText.setText("");
            String copyIntent = getIntent().getStringExtra("CopyIntent");
            copyIntent=MainActivity.extractLinks(copyIntent);
            if (copyIntent== null || copyIntent.equals("")) {
                if (!(clipBoard.hasPrimaryClip())) {
                    Log.d(TAG, "PasteText");
                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains(strName)) {
                        etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }else if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains(strNameSecond)) {
                       etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains(strName)) {
                        etText.setText(item.getText().toString());
                    }
                   else if (item.getText().toString().contains(strNameSecond)) {
                       etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (copyIntent.contains(strName)) {
                    etText.setText(copyIntent);
                }
              else if (copyIntent.contains(strNameSecond)) {
                    etText.setText(copyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userListClick(int position, TrayModel trayModel) {

    }


    @Override
    public void fbUserListClick(int position, NodeModel trayModel) {
        getFacebookUserStories(trayModel.getNodeDataModel().getId());
    }

    class CallGetFacebookData extends AsyncTask<String, Void, Document> {
        Document facebookDoc;

        @Override
        protected Document doInBackground(String... urls) {
            try {
                facebookDoc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Private", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "doInBackground: Error");
            }
            return facebookDoc;
        }

        @Override
        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {
                videoUrl = result.select("meta[property=\"og:video\"]").last().attr("content");
                if (!videoUrl.equals("")) {
                    startDownload(videoUrl, RootDirectoryFacebook, activity, "facebook_"+ System.currentTimeMillis()+".mp4");
                    videoUrl = "";

                    etText.setText("");
                }
            } catch (NullPointerException e) {
               Dialog downloading = new Dialog(FacebookActivity.this);
                downloading.setCancelable(false);
                downloading.setContentView(R.layout._dialog);
                downloading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                downloading.show();
                e.printStackTrace();

                TextView ok = downloading.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloading.dismiss();
                    }
                });

            }
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 100 && resultCode == RESULT_OK) {
                if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISFBLOGIN)){
                   SwitchLogin.setChecked(true);
                    layoutCondition();
                    getFacebookUserData();
                } else {
                    SwitchLogin.setChecked(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void getFacebookUserData(){
        prLoadingBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post("https://www.facebook.com/api/graphql/")
                .addHeaders("accept-language", "en,en-US;q=0.9,fr;q=0.8,ar;q=0.7")
                .addHeaders("cookie", SharePrefs.getInstance(activity).getString(SharePrefs.FBCOOKIES))
                .addHeaders(
                        "user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"
                )
                .addHeaders("Content-Type", "application/json")
                .addBodyParameter("fb_dtsg", SharePrefs.getInstance(activity).getString(SharePrefs.FBKEY))
                .addBodyParameter(
                        "variables",
                        "{\"bucketsCount\":200,\"initialBucketID\":null,\"pinnedIDs\":[\"\"],\"scale\":3}"
                )
                .addBodyParameter("doc_id", "2893638314007950")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("JsonResp- "+response);

                        if(response!=null){
                            try {
                                JSONObject unified_stories_buckets = response.getJSONObject("data").getJSONObject("me").getJSONObject("unified_stories_buckets");

                                Gson gson = new Gson();
                                Type listType = new TypeToken<EdgesModel>() {
                                }.getType();
                                EdgesModel edgesModelNew = gson.fromJson(String.valueOf(unified_stories_buckets), listType);

                                if (edgesModelNew.getEdgeModel().size()>0){
                                    edgeModelList.clear();
                                    edgeModelList.addAll(edgesModelNew.getEdgeModel());
                                    fbStoryUserListAdapter.notifyDataSetChanged();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                       RVUserList.setVisibility(View.VISIBLE);
                       prLoadingBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(ANError anError) {
                        prLoadingBar.setVisibility(View.GONE);

                    }
                });
    }
    public void getFacebookUserStories(String userId){
       prLoadingBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post("https://www.facebook.com/api/graphql/")
                .addHeaders("accept-language", "en,en-US;q=0.9,fr;q=0.8,ar;q=0.7")
                .addHeaders("cookie", SharePrefs.getInstance(activity).getString(SharePrefs.FBCOOKIES))
                .addHeaders(
                        "user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"
                )
                .addHeaders("Content-Type", "application/json")
                .addBodyParameter("fb_dtsg", SharePrefs.getInstance(activity).getString(SharePrefs.FBKEY))
                .addBodyParameter(
                        "variables",
                        "{\"bucketID\":\""+userId+"\",\"initialBucketID\":\""+userId+"\",\"initialLoad\":false,\"scale\":5}"
                )
                .addBodyParameter("doc_id", "2558148157622405")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("JsonResp- "+response);
                       prLoadingBar.setVisibility(View.GONE);
                        if(response!=null){
                            try {
                                JSONObject edges = response.getJSONObject("data").getJSONObject("bucket").getJSONObject("unified_stories");
                                Gson gson = new Gson();
                                Type listType = new TypeToken<EdgesModel>() {
                                }.getType();
                                EdgesModel edgesModelNew = gson.fromJson(String.valueOf(edges), listType);
                                ArrayList<MediaModel> attachmentsList = edgesModelNew.getEdgeModel().get(0).getNodeDataModel().getAttachmentsList();
                                fbStoriesListAdapter = new FBStoriesListAdapter(activity, edgesModelNew.getEdgeModel());
                              RVStories.setAdapter(fbStoriesListAdapter);
                                fbStoriesListAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                       prLoadingBar.setVisibility(View.GONE);
                    }
                });
    }


    public void NativeAds(String nativeId) {

        AdLoader.Builder builder = new AdLoader.Builder(this, nativeId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        RelativeLayout frameLayout = findViewById(R.id.frmad);
                        frameLayout.setBackground(getDrawable(R.drawable.ads_back_fb));
                        frameLayout.setVisibility(View.VISIBLE);
                        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.small_native, null);
                        populateNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());





    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {


        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.findViewById(R.id.ad_call_to_action).setBackground(getDrawable(R.drawable.native_for_facebook));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

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
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        FacebookActivity.this.interstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @RequiresApi(api = Build.VERSION_CODES.Q)
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        FacebookActivity.this.interstitialAd = null;

                                    }


                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        FacebookActivity.this.interstitialAd = null;

                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                    }
                                });
                    }

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        interstitialAd = null;


                    }
                });
    }

    private void showInterstitial() {

        if (interstitialAd != null) {
            interstitialAd.show(this);

            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });

        } else {

        }

    }

    @Override
    public void onBackPressed() {
        showInterstitial();
        super.onBackPressed();

    }

    public void BannerAd(String bannerId) {
        adContainerView = findViewById(R.id.adView);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(bannerId);
        adContainerView.addView(adView);
        loadBanner();


    }

    private void loadBanner() {

        AdRequest adRequest =
                new AdRequest.Builder()
                        .build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }





}