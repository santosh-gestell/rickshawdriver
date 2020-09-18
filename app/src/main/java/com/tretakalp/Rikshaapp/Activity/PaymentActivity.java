package com.tretakalp.Rikshaapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import java.util.Locale;

/**
 * Created by Mac on 13/09/18.
 */
public class PaymentActivity  extends AppCompatActivity {
    TextView txtAmount;
    Button btnDone;
    SharedPreferences pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitle(R.string.payment);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);

        txtAmount=findViewById(R.id.txtAmount);
        btnDone=findViewById(R.id.btnDone);



        Bundle b=getIntent().getExtras();


        txtAmount.setText("\u20B9 "+b.getString("cash"));

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit= pref.edit();
                edit.putInt(Constant.ISRIDE_START,0);
                //edit.putString( Constant.RIDETYPE,"");
                //edit.putString( Constant.ACCEPT_RIDE,"0");
                edit.commit();

                startActivity(new Intent(PaymentActivity.this,MapActivity.class));
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        SharedPreferences.Editor edit= pref.edit();
        edit.putInt(Constant.ISRIDE_START,0);
        //edit.putString( Constant.RIDETYPE,"");//if online ride then make it empty
        //edit.putString( Constant.ACCEPT_RIDE,"0");//if online ride accept then make it 0 from 1
        edit.commit();

        startActivity(new Intent(PaymentActivity.this,MapActivity.class));
        finish();
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
