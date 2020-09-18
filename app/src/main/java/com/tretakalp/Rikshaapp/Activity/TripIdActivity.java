package com.tretakalp.Rikshaapp.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mac on 10/09/18.
 */
public class TripIdActivity extends AppCompatActivity {

    String source,destination,time,tripId,amount,minTime,distance,fee,paymentMode,rideCharge;
    TextView txtSource,txtDest,txtTime,txtTripId,txtAmount,txtFee;
    TextView txtTotalBill,txtTotalAmount2,txtdistance,txtRideCharge,txtPaymentMode;
    String startTime,endTime,payment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.tripIdTitle);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        txtSource=findViewById(R.id.txtsource);
        txtSource.setSelected(true);
        txtDest=findViewById(R.id.txtDestination);
        txtDest.setSelected(true);
        txtTime=findViewById(R.id.txtTime);
        txtAmount=findViewById(R.id.txtAmount);
        txtRideCharge=findViewById(R.id.txtRideCharge);
        txtdistance=findViewById(R.id.txtDistance);
        txtTotalBill=findViewById(R.id.txtTotalBill);
        txtTotalAmount2=findViewById(R.id.txtTotalAmt2);
        txtPaymentMode=findViewById(R.id.txtPaymentMode);
        txtFee=findViewById(R.id.txtFee);

        Bundle b=getIntent().getExtras();
        amount=b.getString("amount");
        source=b.getString("source");
        destination=b.getString("destination");
        time=b.getString("time");
        minTime=b.getString("time");
        distance=b.getString("distance");
        paymentMode=b.getString("paymentMode");
        startTime=b.getString("startTime");
        endTime=b.getString("endTime");
        fee=b.getString("fee");
        rideCharge=b.getString("rideCharge");
        payment=b.getString("payment");

        toolbar.setTitle(getResources().getString(R.string.tripIdTitle)+"("+b.getString("tripId")+")");
       // fee=b.getString("fee");

        txtSource.setText(source);
        txtDest.setText(destination);
        txtAmount.setText(payment);
        txtdistance.setText(distance);
        txtRideCharge.setText("\u20B9 "+rideCharge);
        txtTotalBill.setText("\u20B9 "+amount);
        txtTotalAmount2.setText("\u20B9 "+payment);
        txtFee.setText(fee+"");

        if(paymentMode.equals("0")) {
            txtPaymentMode.setText(R.string.cash);
        }else {
            txtPaymentMode.setText(R.string.online);
        }
      //  getTotalTime( startTime,  endTime,  txtTime);
        txtTime.setText(endTime+"");

    }



    private void getTotalTime(String startTime, String endTime, TextView txtTime) {

        //DateUtils obj = new DateUtils();
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);

        try {
            Date date1 = simpleDateFormat.parse(startTime);
            Date date2 = simpleDateFormat.parse(endTime);

            printDifference(date1, date2,txtTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //1 minute = 60 seconds
        //1 hour = 60 x 60 = 3600
        //1 day = 3600 x 24 = 86400


    }


    public void printDifference(Date startDate, Date endDate, TextView txtTime) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        txtTime.setText(elapsedMinutes+" min "+ elapsedSeconds+"sec");

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
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
