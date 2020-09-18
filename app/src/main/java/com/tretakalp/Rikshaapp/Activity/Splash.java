package com.tretakalp.Rikshaapp.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Splash extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_splash);
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        if(!checkPermission()) {
            requestPermission();
        }else{
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    callNextActivity();
                }
            }, SPLASH_TIME_OUT);
        }

    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(Splash.this, new String[]
                {

                        WRITE_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION,
                        READ_PHONE_STATE,
                        CAMERA

                }, RequestPermissionCode);

    }
    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED
                && SecondPermissionResult == PackageManager.PERMISSION_GRANTED
                && ThirdPermissionResult == PackageManager.PERMISSION_GRANTED
                && FourthPermissionResult == PackageManager.PERMISSION_GRANTED ;

    }


    public void callNextActivity()
    {
        Log.d("StringValue",!pref.getBoolean(Constant.IS_SET_LANGUAGE,false)+"");
        if(!pref.getBoolean(Constant.IS_SET_LANGUAGE,false)) {

            Intent ss = new Intent(Splash.this, LanguageActivity.class);
            ss.putExtra("path", "0");
            ss.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            ss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ss.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ss.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(ss);
            finish();


        }else {

            Intent ss = new Intent(Splash.this, LoginActivity.class);
            //ss.putExtra("path", "0");
            ss.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            ss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ss.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ss.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(ss);
            finish();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RequestPermissionCode){
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    callNextActivity();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLocale();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }

    @SuppressWarnings("deprecation")
    public Locale getSystemLocaleLegacy(Configuration config){
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getSystemLocale(Configuration config){
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public void setSystemLocaleLegacy(Configuration config, Locale locale){
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setSystemLocale(Configuration config, Locale locale){
        config.setLocale(locale);
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String language = pref.getString(Constant.Locale_KeyValue, "");
        Log.d("language",language);
        changeLocale(language);
        //setLanguage(language);
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


    public void setLanguage( String languageCode){
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
        }else{
            setSystemLocaleLegacy(config, locale);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            getApplicationContext().getResources().updateConfiguration(config,
                   getResources().getDisplayMetrics());
    }

}
