package com.tretakalp.Rikshaapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by Mac on 04/09/18.
 */
public class RideSummary extends AppCompatActivity {

    private static final String TAG ="RideSummary" ;
    String totalDistance,totalAmount;
    TextView txtTotalAmt,txtPayAmt,txtDistance,txtDiscount;

    Dialog dialog1;
    RelativeLayout rCash,rOnline;
    SharedPreferences pref;
    String session_id;

    DatabaseHelper db;
    String driverId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitle(R.string.ridesummary_title);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        db=DatabaseHelper.getInstance(this);
        driverId=pref.getString(Constant.DRIVER_ID,"");

        Bundle b=getIntent().getExtras();
        totalDistance =b.getString("totalDistance");
        totalAmount= b.getString("totalAmount");

        rCash=findViewById(R.id.rlCash);
        rOnline=findViewById(R.id.rlOnline);

        txtTotalAmt=findViewById(R.id.txtTotalAmt);
        txtPayAmt=findViewById(R.id.txtPayAmt);
        txtDiscount=findViewById(R.id.txtDiscount);
        txtDistance=findViewById(R.id.txtTotalDist);

        txtTotalAmt.setText("\u20B9 "+totalAmount+"");
        txtPayAmt.setText("\u20B9 "+totalAmount+"");
        txtDistance.setText(totalDistance+" KM");

        session_id=pref.getString(Constant.SESSION_ID,"");


        rCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DetectConnection.checkInternetConnection(RideSummary.this)){

                    makePayment("0");


                }else {
                    Toast.makeText(getApplicationContext(),R.string.noInternet,Toast.LENGTH_SHORT).show();
                }

            }
        });

        rOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DetectConnection.checkInternetConnection(RideSummary.this)){

                    makePayment("1");


                }else {
                    Toast.makeText(getApplicationContext(),R.string.noInternet,Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RideSummary.this);
                builder.setTitle(R.string.paymentmode);
                builder.setMessage(R.string.pleaseselecetpaymentmode);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*Intent ii=new Intent(RideSummary.this,PaymentActivity.class);
                        ii.putExtra("cash",totalAmount);
                        startActivity(ii);*/
                        dialog.cancel();


                    }
                });
                /*builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });*/
                builder.show();

        /*startActivity(new Intent(RideSummary.this,MapActivity.class));
        finish();*/


    }






    private void makePayment(final String paymentMode) {

        final ProgressDialog pDialog = new ProgressDialog(RideSummary.this);
        pDialog.setMessage(getResources().getString(R.string.makingpaymentpleasewait));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;



        try {
            String isPrepaid=pref.getString(Constant.IS_PREPAID_RIDE,"");
            jsonObject1 = new JSONObject();

            if (isPrepaid.equals("app")){
                jsonObject1.put("session_code", session_id);
                jsonObject1.put("payment_mode", paymentMode);
                jsonObject1.put("contact",  pref.getString(Constant.CUSTOMER_CONTACT,""));
                jsonObject1.put("driverId", driverId);
                jsonObject1.put("trip_type", "app");
            }else{
                jsonObject1.put("session_code", session_id);
                jsonObject1.put("payment_mode", paymentMode);
                jsonObject1.put("contact",  pref.getString(Constant.CUSTOMER_CONTACT,""));
                jsonObject1.put("driverId", driverId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "makePayment_data: " + jsonObject1.toString());
        Log.d(TAG, "URl: " + Constant.MAKE_PAYMENT_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.MAKE_PAYMENT_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getCityList_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                                SharedPreferences.Editor edit= pref.edit();
                                edit.putInt(Constant.ISRIDE_START,0);
                               // edit.commit();
                                //if user accepted request of notification
                                  if(pref.getString(Constant.ACCEPT_RIDE_SECOND,"0").equals("1")) {
                                      Log.d(TAG,"setSecondNoti");
                                      //SharedPreferences.Editor edit= pref.edit();
                                      edit.putString(Constant.CUSTOMER_CONTACT,pref.getString(Constant.CUSTOMER_CONTACT_SECOND,"") );
                                      edit.putString(Constant.START_LOCATION_NAME, pref.getString(Constant.START_LOCATION_NAME_SECOND,""));
                                      edit.putString(Constant.END_LOCATION_NAME, pref.getString(Constant.END_LOCATION_NAME_SECOND,""));
                                      Log.d(TAG,"setSecondNoti");
                                      edit.putString(Constant.TRIP_ID, pref.getString(Constant.TRIP_ID_SECOND,""));
                                      edit.putString(Constant.NETPAY, pref.getString(Constant.NETPAY_SECOND,""));
                                      edit.putString(Constant.DISTANCE, pref.getString(Constant.DISTANCE_SECOND,""));
                                      edit.putString(Constant.TIME, pref.getString(Constant.TIME_SECOND,""));
                                      edit.putString(Constant.RIDETYPE, "online");
                                      edit.putString(Constant.ACCEPT_RIDE, "1");
                                      edit.putString(Constant.SESSION_ID,pref.getString(Constant.SESSION_ID_SECOND,"") );
                                      edit.putString(Constant.END_LAT,pref.getString(Constant.END_LAT_SECOND,""));
                                      edit.putString(Constant.END_LONG,pref.getString(Constant.END_LONG_SECOND,""));
                                      edit.putString(Constant.CUST_LAT,pref.getString(Constant.CUST_LAT_SECOND,""));
                                      edit.putString(Constant.CUST_LONG,pref.getString(Constant.CUST_LONG_SECOND,""));

                                      edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");//reset it
                                     // edit.putInt(Constant.ACCEPTED_NOTIFICATION_ID, Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID)));
                                      Log.d(TAG,"setSecondNoti");
                                  }else{
                                      edit.putString(Constant.RIDETYPE, "");
                                      edit.putString( Constant.ACCEPT_RIDE,"0");
                                  }
                                edit.putString(Constant.TRIP_TYPE,"static");
                                edit.commit();



                                db.updateTripTable_withPaymentMode(pref.getString(Constant.SESSION_ID,""),paymentMode);

                                Intent ii=new Intent(RideSummary.this,PaymentActivity.class);
                                ii.putExtra("cash",totalAmount);
                                startActivity(ii);
                                finish();

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int j=0;j<jsonArray.length();j++){

                                    JSONObject jsonObject=jsonArray.getJSONObject(j);

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
                //Toast.makeText(SignUpActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(RideSummary.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();



            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
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
