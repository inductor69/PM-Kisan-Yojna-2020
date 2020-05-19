package com.pm.kisanindia;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;
    DrawerLayout drawerLayout;
    WebView mWebView;
    private Menu optionsMenu;
    Toolbar toolbar;
    NavigationView navigationView;
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    ProgressBar progressBar;

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;
    NestedScrollView nestedScrollView;
    final String url="https://pmkisan.gov.in/Home.aspx#";
    final String url1="https://pmkisan.gov.in/RegistrationForm.aspx";
    final String url2="https://pmkisan.gov.in/UpdateAadharNoByFarmer.aspx";
    final String url3="https://pmkisan.gov.in/BeneficiaryStatus.aspx";
    final String url4="https://pmkisan.gov.in/Rpt_BeneficiaryStatus_pub.aspx";
    final String url5="https://pmkisan.gov.in/FarmerStatus.aspx";


    final String admob_app_id = "ca-app-pub-3780736957915970~5957798562";
    final String admob_banner_id = "ca-app-pub-3780736957915970/5383083491";
    final String admob_inter_id = "ca-app-pub-3780736957915970/4453145204";
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewInit();

        setTitle("PM Kisan Yojna 2020");

        mWebView.loadUrl(url);

        setMySwipeRefreshLayout();

        setSupportActionBar(toolbar);


        floatingActionButton();

        setActionBarToogle();

        //setLocationPermission();

        oneSignalInit();

        //checkPermission();//storage

        webSettings();

        setAdmob();

        //setRTL();
    }

    void setRTL(){
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }


    final void setAdmob(){
        MobileAds.initialize(this, admob_app_id);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(admob_banner_id);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(admob_inter_id);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //  requestNewInterstitial(); //add test device
    }


    final void setMySwipeRefreshLayout(){
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    final public void onRefresh() {
                        mWebView.reload();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }


   final void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("B9C840C4E9AD8EC5D1497C9A62C56374")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    final void setLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);
    }

    final void setActionBarToogle(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    final void floatingActionButton(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            final public void onClick(View view) {
                share();
            }
        });
    }




    @Override
    final public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebViewFragment inside Fragment
            // Use RESULT_OK only if you're implementing WebViewFragment inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }


    final void viewInit(){
        drawerLayout = findViewById(R.id.drawer_layout);
        mWebView = findViewById(R.id.mWebView);
        navigationView =  findViewById(R.id.nav_view);
        toolbar =  findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.content_frame);
        progressBar = findViewById(R.id.progressBar);
        nestedScrollView = findViewById(R.id.nested);
    }

     void webSettings(){
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDescription,
                                        String mimetype, long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                String fileName = URLUtil.guessFileName(url,contentDescription,mimetype);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);

                DownloadManager dManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                dManager.enqueue(request);

                Toasty.info(getApplicationContext(), R.string.download_info, Toast.LENGTH_SHORT, true).show();
            }
        });

        mWebView.setWebViewClient(new WebViewClient()
        {

            public void onReceivedError(WebView mWebView, int i, String s, String d1)
            {
                Toasty.error(getApplicationContext(),"No Internet Connection!").show();
                mWebView.loadUrl("file:///android_asset/net_error.html");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                boolean d = false;
                nestedScrollView.scrollTo(0, 0);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //opening link external browser
                /*if(!url.contains("android_asset")){
                    view.setWebViewClient(null);
                } else {
                    view.setWebViewClient(new WebViewClient());
                }*/

                if(url.contains("youtube.com") || url.contains("play.google.com") || url.contains("google.com/maps") || url.contains("facebook.com") || url.contains("twitter.com") || url.contains("instagram.com")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }

                else if(url.startsWith("tel:")){
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    return true;
                }

                else if(url.contains("geo:")) {
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                    return true;
                }

                view.loadUrl(url);
                return true;
            }

        });


        mWebView.setWebChromeClient(new WebChromeClient(){

            public Bitmap getDefaultVideoPoster()
            {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(mCustomView);
                mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
                setRequestedOrientation(mOriginalOrientation);
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }
                mCustomView = paramView;
                mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getRequestedOrientation();
                mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getWindow().getDecorView()).addView(mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
            }


            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor



            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext() , "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }


            public void onProgressChanged(WebView view, int newProgress){
                progressBar.setProgress(newProgress);
                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        WebSettings webSettings = mWebView.getSettings();

        webSettings.setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.getSaveFormData();
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(true);
       // webSettings.setSupportMultipleWindows(true); //?a href problem
        webSettings.getJavaScriptEnabled();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
       // mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
      //  webSettings.setJavaScriptCanOpenWindowsAutomatically(false); //(popup)
    }





    final void oneSignalInit() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    @Override
    public void onBackPressed(){
        if(getSupportActionBar().getTitle().equals("Local Page")){
            setTitle("PM Kisan Yojna 2020");
            FrameLayout frameLayout = findViewById(R.id.content_frame);
            frameLayout.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(url);
        }
        else if(mWebView.canGoBack())
            mWebView.goBack();
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit the application?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        final public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_back) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }
            return true;

        }
        else if(id == R.id.action_refresh){
            mWebView.reload();
        }
        else if(id == R.id.action_share){
            share();
        }
        else if(id == R.id.action_copy){
            copyToPanel(getApplicationContext(),mWebView.getUrl());
            Snackbar snackbar = Snackbar.make(drawerLayout, "Link Copied.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        return super.onOptionsItemSelected(item);
    }


    final public void copyToPanel(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied.", text);
        clipboard.setPrimaryClip(clip);
    }

    final void share(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, " Posted By ... : "+mWebView.getUrl());
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_actions, menu);
        getMenuInflater().inflate(R.menu.main, optionsMenu);
        return super.onCreateOptionsMenu(menu);
    }

    final void setFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contact) {
            fragment = null;
            setTitle("PM Kisan Yojna 2020");
            mWebView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            mWebView.loadUrl(url2);
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }
        } else if (id == R.id.nav_home) {
            fragment = null;
            setTitle("PM Kisan Yojna 2020");
            mWebView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            mWebView.loadUrl(url1);
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }

        }

        else if (id == R.id.nav_info) {
            fragment = null;
            setTitle("PM Kisan Yojna 2020");
            mWebView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            mWebView.loadUrl(url3);
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }
        } else if (id == R.id.nav_ll) {
            fragment = null;
            setTitle("PM Kisan Yojna 2020");
            mWebView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            mWebView.loadUrl(url4);
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }
        } else if (id == R.id.nav_ll1 ){
            fragment = null;
            setTitle("PM Kisan Yojna 2020");
            mWebView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            mWebView.loadUrl(url4);
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }
        }

        if (fragment != null) {
           setFragment();
           mWebView.setVisibility(View.GONE);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
