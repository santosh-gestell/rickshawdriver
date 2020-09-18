package com.tretakalp.Rikshaapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tretakalp.Rikshaapp.Activity.MapActivity;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import java.util.Locale;

/**
 * Created by Mac on 07/09/18.
 */
public class ReferEarnFragment extends Fragment {

    View v;
    Button btnShare;
    TextView txtCode;
    SharedPreferences pref;
    String code;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.refer_earn, container, false);

        btnShare=v.findViewById(R.id.btnshare);
        pref=getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME,Context.MODE_PRIVATE);
        code=pref.getString(Constant.MOBILE_NO,"");
        txtCode=v.findViewById(R.id.txt1);
        txtCode.setText(code);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareInviteCode();
            }
        });


        return v;


    }

    private void shareInviteCode() {

        String shareBody = getResources().getString(R.string.ihaveanrickshacoupon)+" "+code+" "+getResources().getString(R.string.toavailthecodeandride)+" https://play.google.com/store/apps/details?id=com.tretakalp.Rikshaapp";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rickshaw Application");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_ReferEarn));
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MapActivity)getActivity()).loadLocale();
        loadLocale();
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }
}
