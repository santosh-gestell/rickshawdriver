package com.tretakalp.Rikshaapp.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Created by Mac on 31/08/18.
 */
public class DocumentActivityNew extends AppCompatActivity {

    private static final String TAG ="DocumentActivityNew" ;
    CardView cv1,cv2,cv3,cv4,cv5,cv6;
    RelativeLayout rdl,rProfile,rVehiclePermit,rBadge,rAadhar,rVehicleInsurnce,rBank;
    ImageView img_dl,img_ac,img_vp,img_pp,img_vi,img_badge,img_bank;
    Button btnContinue;
    SharedPreferences pref;
    String driverId;
     String  dlImageUrl="",viImageUrl="",baImageUrl="",acImageUrl="",vpImageUrl="",ppImageUrl="";
     String  dlNumber="", viNumber="", baNumber="", acNumber="", vpNumber="",bankIfsc="", expDateDl="", expDateAc="", expDateVi="";
     String acDate="";
    String dlDate="";
    String viDate="";
    String vpDate="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_new);


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

        Log.d("driverId",driverId+"");
        setUpView();

        getDocument();

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DocumentActivityNew.this,CameraActivity.class);

                startActivityForResult(i,2);

            }
        });

        rdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DocumentActivityNew.this,CameraActivity.class);
                i.putExtra("type","dl");
                i.putExtra("docNumber",dlNumber+"");
                i.putExtra("docImageUrl",dlImageUrl+"");
                i.putExtra("expDate",dlDate+"");
                startActivityForResult(i,2);
                Log.d("expDate", dlDate);
            }
        });

        rProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(DocumentActivityNew.this,ProfilePicActivity.class);
                i.putExtra("type","pp");
                i.putExtra("docImageUrl",ppImageUrl+"");

                startActivityForResult(i,2);

            }
        });

        rAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivityNew.this,CameraActivity.class);
                i.putExtra("type","ac");
                i.putExtra("docNumber",acNumber+"");
                i.putExtra("docImageUrl",acImageUrl+"");
                i.putExtra("expDate",acDate+"");
                startActivityForResult(i,2);
                Log.d("expDate", acDate);
            }
        });

        rBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivityNew.this,BankDetailActivity.class);
                i.putExtra("type","bank");
                i.putExtra("docNumber",baNumber+"");
                i.putExtra("docImageUrl",baImageUrl+"");
                i.putExtra("docIfsc",bankIfsc+"");
                startActivityForResult(i,2);

            }
        });

        //No use
        /*rBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivityNew.this,BadgeNumberActivity.class);
                i.putExtra("type","bn");

                startActivityForResult(i,2);

            }
        });*/

        rVehiclePermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivityNew.this,CameraActivity.class);
                i.putExtra("type","vp");
                i.putExtra("docNumber",vpNumber+"");
                i.putExtra("docImageUrl",vpImageUrl+"");
                i.putExtra("expDate",vpDate+"");
                startActivityForResult(i,2);
                Log.d("expDate", vpDate);

            }
        });

        rVehicleInsurnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivityNew.this,CameraActivity.class);
                i.putExtra("type","vi");
                i.putExtra("docNumber",viNumber+"");
                i.putExtra("docImageUrl",viImageUrl+"");
                i.putExtra("expDate",viDate+"");
                startActivityForResult(i,2);
                Log.d("expDate", viDate);

            }
        });

        /*rBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivityNew.this,BadgeNumberActivity.class);
                i.putExtra("type","bn");
                startActivityForResult(i,2);

            }
        });*/

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(DocumentActivityNew.this,LegalActivity.class);

                startActivity(i);

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


    private void getDocument() {

        final ProgressDialog pDialog = new ProgressDialog(DocumentActivityNew.this);
        pDialog.setMessage("Fetching Document Data...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();

            jsonObject1.put("driver_id", driverId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "Profile_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.GETDOCUMENT_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GETDOCUMENT_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Profile_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                JSONObject jobj = response.getJSONObject("data");
                                JSONObject jobjlicence=jobj.getJSONObject("licence");
                                JSONObject jobjpermit=jobj.getJSONObject("permit");
                                JSONObject jobjadharCard=jobj.getJSONObject("adhar_card");
                                JSONObject jobjinsurance=jobj.getJSONObject("insurance");
                                JSONObject jobjbankAccount=jobj.getJSONObject("bank_account");
                                JSONObject jobjprofileImg=jobj.getJSONObject("profile_img");
                                JSONObject jobjifsc=jobj.getJSONObject("ifsc");

                                if(!jobjlicence.getString("doc_number").equals("")){
                                    img_dl.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                                    dlImageUrl=jobjlicence.getString("doc_image");
                                    dlNumber=jobjlicence.getString("doc_number");
                                    expDateDl=jobjlicence.getString("expiry_date");
                                    dlDate=jobjlicence.getString("expiry_date");
                                    Log.d("expDate","dl: "+dlDate);

                                }

                                if(!jobjinsurance.getString("doc_number").equals("")){
                                    img_vi.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                                    viImageUrl=jobjinsurance.getString("doc_image");
                                    viNumber=jobjinsurance.getString("doc_number");
                                    expDateVi=jobjinsurance.getString("expiry_date");
                                    viDate=jobjinsurance.getString("expiry_date");
                                    Log.d("expDate","vi: "+viDate);
                                }

                                if(!jobjpermit.getString("doc_number").equals("")){
                                    img_vp.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                                    vpImageUrl=jobjpermit.getString("doc_image");
                                    vpNumber=jobjpermit.getString("doc_number");
                                    vpDate=jobjpermit.getString("expiry_date");
                                    Log.d("expDate","vi: "+vpDate);
                                }

                                if(!jobjadharCard.getString("doc_number").equals("")){
                                    img_ac.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                                    acImageUrl=jobjadharCard.getString("doc_image");
                                    acNumber=jobjadharCard.getString("doc_number");
                                    expDateAc=jobjadharCard.getString("expiry_date");
                                    acDate = jobjadharCard.getString("expiry_date");

                                    Log.d("expDate","ac: "+acDate);
                                }
                                if(!jobjbankAccount.getString("doc_number").equals("")){
                                    img_bank.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                                    baImageUrl=jobjbankAccount.getString("doc_image");
                                    baNumber=jobjbankAccount.getString("doc_number");
                                    bankIfsc=jobjifsc.getString("doc_number");
                                }

                                if(!jobjprofileImg.getString("doc_image").equals("")){
                                    img_pp.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                                    ppImageUrl=jobjprofileImg.getString("doc_image");

                                }

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                // Toast.makeText(CameraActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                        Log.d("Error----->>>>>",res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }else {
                    Log.d("Error----->>>>>","elsePart");
                    Toast.makeText(DocumentActivityNew.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDocument();
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
