package com.tretakalp.Rikshaapp.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import java.util.Locale;

/**
 * Created by Mac on 08/10/18.
 */
public class TopicTwo extends AppCompatActivity {


    SharedPreferences pref;
    View v;
    private WebView wv1;
    String url;
    String toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legal_consent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.signIn);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        Bundle b=getIntent().getExtras();
        url=b.getString("url");
        toolbarTitle=b.getString("title");
        toolbar.setTitle(toolbarTitle);




        if(DetectConnection.checkInternetConnection(TopicTwo.this)) {
            wv1 = (WebView) findViewById(R.id.webView);
            wv1.setWebViewClient(new MyBrowser());
            wv1.getSettings().setLoadsImagesAutomatically(true);
            wv1.getSettings().setJavaScriptEnabled(true);
            wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wv1.loadUrl(url+"");
        }else {
            Toast.makeText(TopicTwo.this,R.string.noInternet,Toast.LENGTH_SHORT).show();
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url+"");
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String language = pref.getString(Constant.Locale_KeyValue, "");
        changeLocale(language);
    }

    public void changeLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale  =myLocale = new Locale(lang);//Set Selected Locale
        //  saveLocale(lang);//Save the selected locale
        Locale.setDefault(myLocale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = myLocale;//set config locale as selected locale
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }

}
