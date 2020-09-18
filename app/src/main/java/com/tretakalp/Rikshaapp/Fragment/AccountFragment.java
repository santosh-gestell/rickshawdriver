package com.tretakalp.Rikshaapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tretakalp.Rikshaapp.Activity.DocumentActivityNew;
import com.tretakalp.Rikshaapp.Activity.LanguageActivity;
import com.tretakalp.Rikshaapp.Activity.MapActivity;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import java.util.Locale;

/**
 * Created by Mac on 05/09/18.
 */
public class AccountFragment extends Fragment {

    View v;
    RelativeLayout rlang,rdoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.account_setting, container, false);
      //  ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        rlang=v.findViewById(R.id.rlang);
        rdoc=v.findViewById(R.id.rdoc);

        rlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), LanguageActivity.class);
                i.putExtra("path","1");
                startActivity(i);

                /*Fragment  fragment = new LanguageSetting();
                //args.putString("type","dl");
                //fragment.setArguments(args);
                if (fragment != null) {

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }*/

            }
        });


        rdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DetectConnection.checkInternetConnection(getContext())) {
                    startActivity(new Intent(getActivity(), DocumentActivityNew.class));
                }else {
                    Toast.makeText(getContext(),R.string.noInternet,Toast.LENGTH_SHORT).show();
                }

                /*Fragment  fragment = new LanguageSetting();
                //args.putString("type","dl");
                //fragment.setArguments(args);
                if (fragment != null) {

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }*/

            }
        });


        return v;
    }

   /* private void toolBarSetUp() {

       Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Demo");
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        *//*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onBackPressed();


            }
        });*//*
    }*/

    @Override
    public void onResume() {
        super.onResume();
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_AccountSetting));
        ((MapActivity)getActivity()).loadLocale();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
