package com.tretakalp.Rikshaapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Mac on 31/08/18.
 */
public class DocumentActivity extends AppCompatActivity {

    CardView cv1,cv2,cv3,cv4,cv5,cv6;
    RelativeLayout rdl,rProfile,rVehiclePermit,rBadge,rAadhar,rVehicleInsurnce,rBank;
    ImageView img_dl,img_ac,img_vp,img_pp,img_vi,img_badge,img_bank;
    Button btnContinue,btnCancel;
    SharedPreferences pref;
    String driverId;
    String key;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.documents);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");
        try {
            key = getIntent().getExtras().getString("key");
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("driverId",driverId+"");
        setUpView();
        if(key.equals("frmSignup")){
            btnCancel.setText(R.string.reset);
        }

        if(!pref.getString(Constant.BANK_ACCOUNT+driverId,"").equals("")) {
            img_bank.setImageDrawable(getResources().getDrawable(R.drawable.tick));
        }

        if(!pref.getString(Constant.AADHAR_CARD+driverId,"").equals("")) {
            img_ac.setImageDrawable(getResources().getDrawable(R.drawable.tick));
        }

        if(!pref.getString(Constant.V_INSURANCE+driverId,"").equals("")) {
            img_vi.setImageDrawable(getResources().getDrawable(R.drawable.tick));
        }

        if(!pref.getString(Constant.V_PERMIT+driverId,"").equals("")) {
            img_vp.setImageDrawable(getResources().getDrawable(R.drawable.tick));
        }

        if(!pref.getString(Constant.DRIVING_LICENSE+driverId,"").equals("")) {
            img_dl.setImageDrawable(getResources().getDrawable(R.drawable.tick));
        }

        if(!pref.getString(Constant.PROFILE_IMAGE+driverId,"").equals("")) {
            img_pp.setImageDrawable(getResources().getDrawable(R.drawable.tick));
        }


        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DocumentActivity.this,CameraActivity.class),2);

            }
        });

        rdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivity.this,CameraActivity.class);
                i.putExtra("type","dl");
                startActivityForResult(i,2);

            }
        });

        rProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DocumentActivity.this,ProfilePicActivity.class);
                i.putExtra("type","pp");
                startActivityForResult(i,2);

            }
        });

        rAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivity.this,CameraActivity.class);
                i.putExtra("type","ac");
                startActivityForResult(i,2);

            }
        });

        rBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivity.this,BankDetailActivity.class);
                i.putExtra("type","bank");
                startActivityForResult(i,2);

            }
        });

        /*rBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivity.this,BadgeNumberActivity.class);
                i.putExtra("type","bn");
                startActivityForResult(i,2);

            }
        });*/

        rVehiclePermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivity.this,CameraActivity.class);
                i.putExtra("type","vp");
                startActivityForResult(i,2);

            }
        });

        rVehicleInsurnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivity.this,CameraActivity.class);
                i.putExtra("type","vi");
                startActivityForResult(i,2);

            }
        });

       /* rBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivity.this,BadgeNumberActivity.class);
                i.putExtra("type","bn");
                startActivityForResult(i,2);

            }
        });*/

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int dlStatus=pref.getInt(Constant.IS_DRIVING_LICENSE+driverId,0);

                //dring status 2 means.. driving licencens uploaded
                Log.d("dlStatus",driverId+" " +dlStatus+"");
                if(dlStatus==2) {

                    SharedPreferences.Editor edit=pref.edit();
                    edit.putInt(Constant.SIGNUP_PROCESS,0);
                    edit.putBoolean(Constant.IS_LOGIN,true);
                    edit.putString(Constant.DRIVER_ID,driverId);

                    edit.commit();

                    Intent i = new Intent(DocumentActivity.this, MapActivity.class);
                    startActivity(i);
                }else {
                    Toast.makeText(DocumentActivity.this,R.string.pleaseuploaddrivinglicence,Toast.LENGTH_LONG).show();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * if user came here from signup screen and cancel the button..
                // then it will reset .. so new sign up screen will be open on click event of signup btn instead of
                //redirecting to document Screen
                // or if user came here from Login screen and cancel the button..
                //it will just finish without reseting the signup process..
                //so who left signup process without filling document it will redirect on click signup btn to document
                //signup process =0 means reset/complete.. 1 means half complete ,document not uploaded */

                if(key.equals("frmSignup")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                    builder.setTitle(getResources().getString(R.string.reset));
                    builder.setMessage(getResources().getString(R.string.thisresetsignupprocess));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putInt(Constant.SIGNUP_PROCESS, 0);
                            edit.commit();
                            finish();


                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();


                }else if(key.equals("frmLogin")) {

                    finish();
                }

            }
        });



    }

    private void setUpView() {

        cv1=findViewById(R.id.card_view1);
        cv2=findViewById(R.id.card_view2);
        cv3=findViewById(R.id.card_view3);
        cv4=findViewById(R.id.card_view4);
        cv5=findViewById(R.id.card_view5);
        cv6=findViewById(R.id.card_view6);

        rdl=findViewById(R.id.rdl);
        rAadhar=findViewById(R.id.rAadhar);
        rProfile=findViewById(R.id.rProfile);
        rVehiclePermit=findViewById(R.id.rVehiclePermit);
        rBadge=findViewById(R.id.rBadge);
        rVehicleInsurnce=findViewById(R.id.rVehicleInsurnce);
        rBank=findViewById(R.id.rbank);


        img_dl=findViewById(R.id.img_dl);
        img_ac=findViewById(R.id.img_adharNo);
        img_vp=findViewById(R.id.img_vehiclepermit);
        img_vi=findViewById(R.id.img_vehicleInsurunce);
        img_badge=findViewById(R.id.img_badgeNumber);
        img_pp=findViewById(R.id.img_profilepic);
        img_bank=findViewById(R.id.img_bankpic);

        btnContinue=findViewById(R.id.btnContinue);
        btnCancel=findViewById(R.id.btnCancel);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }else if(requestCode==2){

            if(resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String type=bundle.getString("type");
                if(type.equals("dl")) {
                    img_dl.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                }else  if(type.equals("ac")) {
                    img_ac.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                }else  if(type.equals("vp")) {
                    img_vp.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                }if(type.equals("vi")) {
                    img_vi.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                }if(type.equals("bn")) {
                    img_badge.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                }if(type.equals("bank")) {
                    img_bank.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                }if(type.equals("pp")) {
                    img_pp.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                }


                //Toast.makeText(PartyDetails.this,"Success ",Toast.LENGTH_LONG).show();
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
               // Toast.makeText(DocumentActivity.this,"Result Cancelled ",Toast.LENGTH_LONG).show();
                //Bundle bundle = data.getExtras();

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
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
