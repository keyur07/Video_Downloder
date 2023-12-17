package com.cd.statussaver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cd.statussaver.R;
import com.cd.statussaver.adapter.Howtouse_Adapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.io.File;
import java.text.BreakIterator;
import java.text.DecimalFormat;

public class Setting extends AppCompatActivity {

    private RelativeLayout HowToUse,Clear_cache,Rate_app,Share_App,Privacy_policy,Feedback;
    private TextView cache;
    private ImageView back;
    private Dialog mDialog;
    public static ViewPager viewPager;
    public static Dialog download_completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.background_for_instagram);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);


        HowToUse = findViewById(R.id.how_to_use);
        Clear_cache = findViewById(R.id.cache_clear);
        back = findViewById(R.id.back);
        Share_App = findViewById(R.id.shareapp);
        Privacy_policy = findViewById(R.id.privacy);
        cache = findViewById(R.id.cache);
        NativeAds(SplashScreen.NativeId);

        try {
            ((TextView) findViewById(R.id.version)).setText(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
        File cacheDir = getCacheDir();
        cache.setText(String.valueOf(cacheDir.length()));


        this.cache.setText(cache+readableFileSize(getDirSize(this.getExternalCacheDir()) + getDirSize(this.getCacheDir()) + cacheDir.length()));


    /*    Rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(Setting.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.rate_us_dailog);
                dialog.setCancelable(false);
                final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.rating_bar);
                dialog.findViewById(R.id.txtnotnow).setOnClickListener(new View.OnClickListener() { // from class: m.a.a.b.w
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        Dialog dialog2 = dialog;
                        dialog2.dismiss();
                    }
                });
                dialog.findViewById(R.id.txtsubmit).setOnClickListener(new View.OnClickListener() { // from class: m.a.a.b.c0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        Intent intent;
                        dialog.dismiss();

                        intent = new Intent("android.intent.action.VIEW").setData(Uri.parse("https://play.google.com/store/apps"));
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        Share_App.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = "Best video and status downloader app....DOWNLOAD NOW....";
                String sub= "http://play.google.com/store/apps/details?id="+getPackageName();;
                intent.putExtra(Intent.EXTRA_TEXT,body);
                intent.putExtra(Intent.EXTRA_TEXT,sub);
                startActivity(Intent.createChooser(intent,"Share Using"));
            }
        });

        Privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.intent.action.VIEW").setData(Uri.parse("https://sites.google.com/view/videodownloderpolicy/home"));
                startActivity(intent);
            }
        });

        Clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trimCache(Setting.this);
                initializeCache();
            }
        });

        HowToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                howtouse();
            }
        });







    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeCache();
    }

    public static void trimCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
                Toast.makeText(context, "Cache Cleared...", 0).show();
            }
        } catch (Exception unused) {
        }
    }

    public static boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            for (String str : file.list()) {
                if (!deleteDir(new File(file, str))) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    private void initializeCache() {
        this.cache.setText(readableFileSize(getDirSize(this.getExternalCacheDir()) + getDirSize(this.getCacheDir()) + 0));
    }

    public static String readableFileSize(long j2) {
        double d2 = 0;
        if (j2 <= 0) {
            return "0 Bytes";
        }
        int log10 = (int) (Math.log10(j2) / Math.log10(1024.0d));
        return new DecimalFormat("#,##0.#").format(d2 / Math.pow(1024.0d, log10)) + " " + new String[]{"Bytes", "kB", "MB", "GB", "TB"}[log10];
    }


    public long getDirSize(File file) {
        File[] listFiles;
        long length = 0;
        long j2 = 0;
        if (!(file == null || !file.exists() || file.listFiles().length == 0)) {
            for (File file2 : file.listFiles()) {
                if (file2 == null || !file2.isDirectory()) {
                    if (file2 != null && file2.isFile()) {
                        length = file2.length();
                    }
                } else {
                    length = getDirSize(file2);
                }
                j2 += length;
            }
        }
        return j2;
    }


    private void howtouse() {
       download_completed = new Dialog(this);
        download_completed.setCancelable(false);
        download_completed.setContentView(R.layout.slide_dialog);
        download_completed.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT ));
        download_completed.show();
        viewPager=download_completed.findViewById(R.id.viewpagerone);
        Howtouse_Adapter adapter=new Howtouse_Adapter(this);
        viewPager.setAdapter(adapter);

    }



    public void NativeAds(String nativeId) {

        AdLoader.Builder builder = new AdLoader.Builder(this, nativeId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        FrameLayout frameLayout = findViewById(R.id.nativeads);
                        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.nativelayout, null);
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

        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

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
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }
        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }
        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

    }




}