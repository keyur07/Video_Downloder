package com.cd.statussaver.activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.cd.statussaver.util.Utils.RootDirectoryInsta;
import static com.cd.statussaver.util.Utils.createFileFolder;
import static com.cd.statussaver.util.Utils.startDownload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cd.statussaver.R;
import com.cd.statussaver.adapter.StoriesListAdapter;
import com.cd.statussaver.adapter.UserListAdapter;
import com.cd.statussaver.api.CommonClassForAPI;
import com.cd.statussaver.interfaces.UserListInterface;
import com.cd.statussaver.model.Edge;
import com.cd.statussaver.model.EdgeSidecarToChildren;
import com.cd.statussaver.model.FBStoryModel.NodeModel;
import com.cd.statussaver.model.ResponseModel;
import com.cd.statussaver.model.story.FullDetailModel;
import com.cd.statussaver.model.story.StoryModel;
import com.cd.statussaver.model.story.TrayModel;
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
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import pl.droidsonroids.gif.GifImageView;

public class InstagramActivity extends AppCompatActivity implements UserListInterface {
    private InstagramActivity activity;
    Activity context;
    private ClipboardManager clipBoard;
    CommonClassForAPI commonClassForAPI;
    private String PhotoUrl;
    private String VideoUrl;

    private ProgressBar prLoadingBar;

    private RecyclerView RVStories;

    private RecyclerView RVUserList;

    private RewardedAd rewardedAd;
    private ImageView imBack;
    private GifImageView imInfo;
    private TextView loginBtn1, tvPaste, tvLogin, tvViewStories;
    private EditText etText;

    private Switch SwitchLogin;

    private RelativeLayout LLOpenInstagram, RLLoginInstagram, smallnative;

    UserListAdapter userListAdapter;
    StoriesListAdapter storiesListAdapter;
    private TextView download;
    private InterstitialAd interstitialAd;
    public static Context context_app;
    private RelativeLayout adContainerView;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_instagram);
        RequestInterstitial(SplashScreen.InterstitialID3);

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
        smallnative = findViewById(R.id.frmad);
        download = findViewById(R.id.download);
        context = activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();

        BannerAd(SplashScreen.bannerId2);


        Window window = activity.getWindow();
        Drawable background = activity.getResources().getDrawable(R.drawable.background_for_instagram);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);


        NativeAds(SplashScreen.NativeId2);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(InstagramActivity.this, GalleryActivity.class));
            }
        });
        imInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent("android.intent.action.VIEW").setData(Uri.parse(SplashScreen.Qureka_Id));
               startActivity(intent);

            }
        });
        context_app = InstagramActivity.this;




  /*      AdsUtils.showGoogleBannerAd(InstagramActivity.this, binding.adView);
        rewardedVideoAdsINIT();*/

        /*ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardListener() {
            @Override
            public void onPrimaryClipChanged() {

                ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                CharSequence pasteData = "";
                ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                pasteData = item.getText();
                Toast.makeText(getApplicationContext(), "copied val=" + pasteData,
                        Toast.LENGTH_SHORT).show();
            }
        });*/


        initViews();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvPaste.performClick();
            }
        }, 500);


    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        context = activity = this;
        context_app = context;
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
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        loginBtn1.setOnClickListener(v -> {
            String LL = etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, "Enter Url");
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, "Enter Valid Url");
            } else {
                GetInstagramData();
                // showRewardedVideoAds();
            }
        });

        tvPaste.setOnClickListener(v -> {
            PasteText();
        });
        LLOpenInstagram.setOnClickListener(v -> {
            Utils.OpenApp(activity, "com.instagram.android");
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        RVUserList.setLayoutManager(mLayoutManager);
        RVUserList.setNestedScrollingEnabled(false);
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
            layoutCondition();
            callStoriesApi();
            SwitchLogin.setChecked(true);
        } else {
            SwitchLogin.setChecked(false);
        }

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,
                        LoginActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        RLLoginInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                    Intent intent = new Intent(activity,
                            LoginActivity.class);
                    startActivityForResult(intent, 100);
                } else {
                    AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                    ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISINSTALOGIN, false);
                            SharePrefs.getInstance(activity).putString(SharePrefs.COOKIES, "");
                            SharePrefs.getInstance(activity).putString(SharePrefs.CSRF, "");
                            SharePrefs.getInstance(activity).putString(SharePrefs.SESSIONID, "");
                            SharePrefs.getInstance(activity).putString(SharePrefs.USERID, "");

                            if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                                SwitchLogin.setChecked(true);
                            } else {
                                SwitchLogin.setChecked(false);
                                RVUserList.setVisibility(View.GONE);
                                RVStories.setVisibility(View.GONE);
                                tvViewStories.setText(activity.getResources().getText(R.string.view_stories));
                                tvLogin.setVisibility(View.VISIBLE);
                            }
                            dialog.cancel();

                        }
                    });
                    ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = ab.create();
                    alert.setTitle("Don't Want to Download Media from Private Account?");
                    alert.show();
                }

            }
        });
        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 3);
        RVStories.setLayoutManager(mLayoutManager1);
        RVStories.setNestedScrollingEnabled(false);
        mLayoutManager1.setOrientation(RecyclerView.VERTICAL);

    }

    public void layoutCondition() {
        tvViewStories.setText(activity.getResources().getString(R.string.stories));
        tvLogin.setVisibility(View.GONE);

    }

    private void GetInstagramData() {

        createFileFolder();
        try {

            URL url = new URL(etText.getText().toString());
            String host = url.getHost();
            Log.e("initViews: ", host);
            if (host.equals("www.instagram.com")) {
                callDownload(etText.getText().toString());
                // showRewardedVideoAds();
            } else {
                Utils.setToast(activity, "Enter Valid Url");
            }

        } catch (Exception e) {
            Log.e("errrorr", e.toString());
        }
    }

    private void PasteText() {
        try {
            etText.setText("");
            String CopyIntent = "";
            if (CopyIntent.equals("")) {
                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("instagram.com")) {
                        etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("instagram.com")) {
                        etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("instagram.com")) {
                    etText.setText(CopyIntent);
                    callDownload(etText.getText().toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUrlWithoutParameters(String url) {
        try {
            URI uri = new URI(url);
            return new URI(uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null, // Ignore the query part of the input url
                    uri.getFragment()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.setToast(activity, "Enter Valid Url");
            return "";
        }
    }


    private void callDownload(String Url) {
        String UrlWithoutQP = getUrlWithoutParameters(Url);
        UrlWithoutQP = UrlWithoutQP + "?__a=1";
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    Utils.showProgressDialog(activity);
                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
                            "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                                    + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                ShowNoInternetDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void ShowNoInternetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(InstagramActivity.this, R.style.BottomSheetDialogTheme);
        View bottom = LayoutInflater.from(InstagramActivity.this).inflate(R.layout.no_internet, null);
        bottomSheetDialog.setContentView(bottom);
        bottomSheetDialog.show();
        TextView refresh = bottom.findViewById(R.id.tryagain);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstagramActivity.this, InstagramActivity.class));
                bottomSheetDialog.dismiss();
                finish();
            }
        });


    }


    private DisposableObserver<JsonObject> instaObserver = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(JsonObject versionList) {
            Utils.hideProgressDialog(activity);
            try {
                Log.e("onNext: ", versionList.toString());
                Type listType = new TypeToken<ResponseModel>() {
                }.getType();
                ResponseModel responseModel = new Gson().fromJson(versionList.toString(), listType);
                EdgeSidecarToChildren edgeSidecarToChildren = responseModel.getGraphql().getShortcode_media().getEdge_sidecar_to_children();
                if (edgeSidecarToChildren != null) {
                    List<Edge> edgeArrayList = edgeSidecarToChildren.getEdges();
                    for (int i = 0; i < edgeArrayList.size(); i++) {
                        if (edgeArrayList.get(i).getNode().isIs_video()) {
                            VideoUrl = edgeArrayList.get(i).getNode().getVideo_url();
                            startDownload(VideoUrl, RootDirectoryInsta, activity, getVideoFilenameFromURL(VideoUrl));
                            etText.setText("");
                            VideoUrl = "";

                        } else {
                            PhotoUrl = edgeArrayList.get(i).getNode().getDisplay_resources().get(edgeArrayList.get(i).getNode().getDisplay_resources().size() - 1).getSrc();
                            startDownload(PhotoUrl, RootDirectoryInsta, activity, getImageFilenameFromURL(PhotoUrl));
                            PhotoUrl = "";
                            etText.setText("");
                        }
                    }
                } else {
                    boolean isVideo = responseModel.getGraphql().getShortcode_media().isIs_video();
                    if (isVideo) {
                        VideoUrl = responseModel.getGraphql().getShortcode_media().getVideo_url();
                        // new DownloadFileFromURL().execute(VideoUrl,getFilenameFromURL(VideoUrl));
                        startDownload(VideoUrl, RootDirectoryInsta, activity, getVideoFilenameFromURL(VideoUrl));
                        VideoUrl = "";
                        etText.setText("");
                    } else {
                        PhotoUrl = responseModel.getGraphql().getShortcode_media().getDisplay_resources()
                                .get(responseModel.getGraphql().getShortcode_media().getDisplay_resources().size() - 1).getSrc();

                        startDownload(PhotoUrl, RootDirectoryInsta, activity, getImageFilenameFromURL(PhotoUrl));
                        PhotoUrl = "";
                        etText.setText("");


                        // new DownloadFileFromURL().execute(PhotoUrl,getFilenameFromURL(PhotoUrl));
                    }
                }


            } catch (Exception e) {
                Dialog downloading = new Dialog(InstagramActivity.this);
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
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog(activity);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog(activity);
        }
    };

    public String getImageFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".png";
        }
    }

    public String getVideoFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instaObserver.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 100 && resultCode == RESULT_OK) {
                String requiredValue = data.getStringExtra("key");
                if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                    SwitchLogin.setChecked(true);
                    layoutCondition();
                    callStoriesApi();
                } else {
                    SwitchLogin.setChecked(false);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private void callStoriesApi() {
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    prLoadingBar.setVisibility(View.VISIBLE);
                    commonClassForAPI.getStories(storyObserver, "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(activity, "No Internet Connection.");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private DisposableObserver<StoryModel> storyObserver = new
            DisposableObserver<StoryModel>() {
                @Override
                public void onNext(StoryModel response) {
                    RVUserList.setVisibility(View.VISIBLE);
                    prLoadingBar.setVisibility(View.GONE);
                    try {
                        ArrayList<TrayModel> arrayListTrayModel = new ArrayList<>();
                        for (int i = 0; i < response.getTray().size(); i++) {
                            try {
                                if (response.getTray().get(i).getUser().getFull_name() != null) {
                                    arrayListTrayModel.add(response.getTray().get(i));
                                }
                            } catch (Exception ex) {
                            }
                        }
                        userListAdapter = new UserListAdapter(activity, arrayListTrayModel,
                                activity);
                        RVUserList.setAdapter(userListAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    prLoadingBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    prLoadingBar.setVisibility(View.GONE);
                }
            };

    @Override
    public void userListClick(int position, TrayModel trayModel) {
        callStoriesDetailApi(String.valueOf(trayModel.getUser().getPk()));
    }

    @Override
    public void fbUserListClick(int position, NodeModel trayModel) {
        //
    }

    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    prLoadingBar.setVisibility(View.VISIBLE);
                    commonClassForAPI.getFullDetailFeed(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(activity,
                        "No Internet Connection.");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<FullDetailModel> storyDetailObserver = new DisposableObserver<FullDetailModel>() {
        @Override
        public void onNext(FullDetailModel response) {
            RVUserList.setVisibility(View.VISIBLE);
            prLoadingBar.setVisibility(View.GONE);
            try {
                storiesListAdapter = new StoriesListAdapter(activity, response.getReel_feed().getItems());
                RVStories.setAdapter(storiesListAdapter);

                storiesListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            prLoadingBar.setVisibility(View.GONE);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            prLoadingBar.setVisibility(View.GONE);
        }
    };


    public void NativeAds(String nativeId) {

        AdLoader.Builder builder = new AdLoader.Builder(this, nativeId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        RelativeLayout frameLayout = findViewById(R.id.frmad);
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
                        InstagramActivity.this.interstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @RequiresApi(api = Build.VERSION_CODES.Q)
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        InstagramActivity.this.interstitialAd = null;

                                    }


                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        InstagramActivity.this.interstitialAd = null;

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
        super.onBackPressed();
        showInterstitial();
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
