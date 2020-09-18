package com.tretakalp.Rikshaapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Mac on 05/09/18.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private Menu menu;
    TextView txtDriverName;
    SharedPreferences pref;
    String driverName,profileUrl,driverId;
    ImageView imgView;
    DatabaseHelper db;
    int count;
    double sum;
    TextView txtTripCount,txtTotalEarning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               onBackPressed();

            }
        });

        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        db=DatabaseHelper.getInstance(this);


        driverName = pref.getString(Constant.DRIVER_NAME, "");
        driverId = pref.getString(Constant.DRIVER_ID, "");
        profileUrl = pref.getString(Constant.PROFILE_PATH, "");

        txtDriverName = findViewById(R.id.txtdriver_name);
        txtTripCount = findViewById(R.id.txtTripCount);
        txtTotalEarning = findViewById(R.id.txtTotalEarning);

        txtDriverName.setText(driverName + "");
        imgView = findViewById(R.id.imageView);


        if(DetectConnection.checkInternetConnection(ProfileActivity.this)) {
            //  Log.d(TAG,"LastTimeStamp: "+pref.getString(Constant.LAST_HISTORY_TIMESTAMP,""));
            //if(pref.getString(Constant.LAST_HISTORY_TIMESTAMP,"").equals("")) {
            //  db.deleteAllTripHistory(driverId);
            getAllTripEarnHistory();//Fetch all Trip History
            //}
        }


        count=db.getTotalTripCountNew(driverId);
        //sum=db.getTotalSum(driverId);
        sum=db.getTodayTotalSumNew(driverId);
        txtTotalEarning.setText("\u20B9 "+sum+"");

        txtTripCount.setText(count+"");

        Glide.with(this)
                .asBitmap()
                .load(profileUrl+"?ver=5436789")

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.profile)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imgView.setImageBitmap(resource);
                    }


                });




        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    //showOption(R.id.action_edit);
                } else if (isShow) {
                    isShow = false;
                  //  hideOption(R.id.action_edit);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.profile, menu);
        //hideOption(R.id.action_edit);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        } else */if (id == R.id.action_edit) {

            if(DetectConnection.checkInternetConnection(ProfileActivity.this)) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }else {
                Toast.makeText(ProfileActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
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


    private void getAllTripEarnHistory() {

       // getLastOneMonthDate();
        Calendar c = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);


        int  mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDayFr = c.get(Calendar.DAY_OF_MONTH);
        int mDayTo = c.get(Calendar.DAY_OF_MONTH);

        String fromDate=format.format(c.getTime());
        String toDate=format.format(c.getTime());

        // db.deleteAllTripHistory(driverId);
        ArrayList<Bean> tripIdList=new ArrayList<>();
        tripIdList= db.getTripIdData(Constant.formateDate2(fromDate),Constant.formateDate2(toDate),driverId);
        Log.d(TAG,"tripIdSize:"+tripIdList.size());

        final ProgressDialog pDialog = new ProgressDialog(ProfileActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {



            jsonObject1 = new JSONObject();
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("from",Constant.formateDate2(fromDate));
            jsonObject1.put("to", Constant.formateDate2(toDate));


            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<tripIdList.size();i++){
                jsonArray.put(tripIdList.get(i).getTripId());
            }
            jsonObject1.put("trip_id", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "EARNING_DRIVER_URL" + jsonObject1.toString());
        Log.d(TAG, "EARNING_DRIVER_URL" + Constant.TRIP_HISTORY_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.TRIP_HISTORY_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(ProfileActivity.this,msg,Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "getearningStatus_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);
                                    JSONArray jsonArray1=jobj.getJSONArray("details");
                                    for(int j=0;j<jsonArray1.length();j++){
                                        JSONObject jobj1=jsonArray1.getJSONObject(j);
                                        Bean b=new Bean();
                                        b.setTotalDistance(jobj1.getString("distance"));
                                        b.setDuration(jobj1.getString("duration"));
                                        b.setPickupLocName(jobj1.getString("pickup_location"));
                                        b.setDropLocName(jobj1.getString("drop_location"));
                                        b.setNetAmount(jobj1.getString("amount"));
                                        b.setRideCharge(jobj1.getString("ride_charge"));
                                        b.setFee(jobj1.getString("fee"));
                                        b.setTotalAmount(jobj1.getString("total_bill"));
                                        b.setTripNumber(jobj1.getString("trip_number"));
                                        b.setTripId(jobj1.getString("trip_id"));
                                        b.setPaymentMode(jobj1.getString("payment_mode"));
                                        b.setPayment(jobj1.getString("payment"));
                                        b.setTripDate(jobj1.getString("created_date"));
                                        b.setDriverId(driverId);

                                        db.insertTripHistoryDetails(b);

                                    }


                                }

                                /*SharedPreferences.Editor editor=pref.edit();
                                editor.putString(Constant.LAST_HISTORY_TIMESTAMP,Constant.getDateTime());
                                editor.commit();*/

                                count=db.getTotalTripCountNew(driverId);
                                //sum=db.getTotalSum(driverId);
                                sum=db.getTodayTotalSumNew(driverId);
                                txtTotalEarning.setText("\u20B9 "+sum+"");

                                txtTripCount.setText(count+"");


                            }else {
                                Toast.makeText(ProfileActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();


                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        if(!msg.equalsIgnoreCase("No data found")) {
                            Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

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
                    Toast.makeText(ProfileActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
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




}
