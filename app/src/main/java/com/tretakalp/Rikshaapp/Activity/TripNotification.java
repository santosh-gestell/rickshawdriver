package com.tretakalp.Rikshaapp.Activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.tretakalp.Rikshaapp.Adpter.NotificationListAdapter;
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
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Mac on 15/10/18.
 */
public class TripNotification extends AppCompatActivity implements NotificationListAdapter.ItemClickListener, NotificationListAdapter.StopFuncListener {

    private static final String TAG ="TripNotification" ;
    RecyclerView recyclerView;
    NotificationListAdapter adapter;
    // NotificationListThreadAdapter adapter;
    ArrayList<Bean> notificationList=new ArrayList<>();
    //NotificationListAdapter.ItemClickListener itemClickListener;
    NotificationListAdapter.ItemClickListener itemClickListener;
    String phone,pickup,drop,bookingId,amount,duration,distance,seesionId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    DatabaseHelper db;
    SharedPreferences pref;
    String driverId;
    CoordinatorLayout coordinatorLayout;
    boolean isLogin=false;
    ProgressDialog pDialog;
    //Timer timer;
    //MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_notification);
        coordinatorLayout=findViewById(R.id.cordinate);
        setuoToolbar();
        clearNotification();
        //mp = MediaPlayer.create(this, R.raw.beep);
        db=DatabaseHelper.getInstance(this);
       // db.copyDatabase(this);
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");
        isLogin=pref.getBoolean(Constant.IS_LOGIN,false);
        // set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG,"TripBroadcast.......");
                // checking for type intent filter
                if (intent.getAction().equals(Constant.PUSH_NOTIFICATION)||intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    // new push notification is received
                    Log.d(TAG,"Broadcast.......");
                    if(notificationList.size()>0){
                        notificationList.clear();
                    }
                    Log.d("mytag","NotificatinoList Brodcast: "+notificationList);
                    //before refreshing new list plz stop countdown timer
                        if(adapter!=null) {
                            adapter.stopCountDown();
                            adapter.notifyDataSetChanged();
                        }
                        notificationList=db.getTripNotification(driverId);
                        setRecyclerView();

                    if(notificationList.isEmpty()){

                        db.deleteAllNotificationData();
                        startActivity(new Intent(TripNotification.this,MapActivity.class));
                        clearNotification();
                        finish();
                    }

                    clearNotification();
                }
            }
        };


       setTripListData();

        //startBeepMethod();

    }

    /*private void startBeepMethod() {

        if(timer!=null)
           timer.cancel();

        timer = new Timer();

        timer.schedule( new TimerTask()
        {
            public void run() {
                // do your work
                playBeepSound();
            }
        }, 0,1 *(1000*1));
    }

    private void playBeepSound() {

        //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        try {

            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(TripNotification.this, R.raw.beep);
            }
            //if(this.isPlayingBeep) {
            mp.start();
            *//*}else {
                cancelTimer(holder);
            }*//*
        } catch(Exception e) { e.printStackTrace(); }

    }

    public void stopBeep() {

        if (mp.isPlaying()) {
           mp.stop();
            mp.release();
            mp = MediaPlayer.create(TripNotification.this, R.raw.beep);
        }

    }*/


    private void setTripListData() {

        if(notificationList.size()>0){
            notificationList.clear();
        }
        //before refreshing new list plz stop countdown timer
        if(adapter!=null) {
            adapter.stopCountDown();
        }

        notificationList=db.getTripNotification(driverId);
        Log.d("TripNotification","listSize: "+notificationList.size());
        setRecyclerView();

    }

    private void clearNotification() {
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancelAll();
        Log.d(TAG,"NotificationCancel");
    }

    private void setuoToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.incomingtrips);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onBackPressed();
            }
        });
    }


    public void showSnackBar(String message) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void setRecyclerView() {


        //int numberOfColumns = 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationListAdapter(this,notificationList,itemClickListener);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TripNotification.this);
        builder.setTitle(R.string.clearNotification);
        builder.setMessage(R.string.yourallnotificationsgetclear);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // adapter.stopBeep();

                adapter.stopCountDown();
                adapter.setBooleanStopBeep();
                db.deleteAllNotificationData();

                dialog.cancel();
                startActivity(new Intent(TripNotification.this,LoginActivity.class));
                finish();

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // onBackPressed();
            }
        });
        builder.show();



        //super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Constant.PUSH_NOTIFICATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, filter);



        if(notificationList.isEmpty()){

            db.deleteAllNotificationData();
            startActivity(new Intent(TripNotification.this,MapActivity.class));
            clearNotification();
            finish();
        }


        clearNotification();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }

    @Override
    public void onItemClick1(View view, int position) {

        if(DetectConnection.checkInternetConnection(this)) {

            if (isLogin) {
                // if two online notification is accepted and want to accept third ride notitification then dont allow
                //driver can accept two ride at a time
               // if(pref.getInt(Constant.ISRIDE_START,0)==1){

                if(pref.getString(Constant.ACCEPT_RIDE_SECOND,"0").equals("1")){

                    Toast.makeText(TripNotification.this,R.string.pleaseCompleteyourpendingRide,Toast.LENGTH_LONG).show();

                }else if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("1")/*||pref.getInt(Constant.ISRIDE_START, 0) == 1*/){
                    // if one online notification is accepted and want to accept second ride then save it for later use
                    //driver can accept two ride at a time
                    //for second Notification
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(Constant.CUSTOMER_CONTACT_SECOND, notificationList.get(position).getCustMobileNo());
                    edit.putString(Constant.START_LOCATION_NAME_SECOND, notificationList.get(position).getPickupLocName());
                    edit.putString(Constant.END_LOCATION_NAME_SECOND,  notificationList.get(position).getDropLocName());
                    edit.putString(Constant.TRIP_ID_SECOND,  notificationList.get(position).getBookingId());
                    edit.putString(Constant.NETPAY_SECOND,  notificationList.get(position).getNetAmount());
                    edit.putString(Constant.DISTANCE_SECOND,  notificationList.get(position).getDistance());
                    edit.putString(Constant.TIME_SECOND,  notificationList.get(position).getDuration());
                    edit.putString(Constant.IS_PREPAID_RIDE,  notificationList.get(position).getIsPrepaid());
                    edit.putString(Constant.RIDETYPE_SECOND, "online");
                    edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");
                    edit.putString(Constant.SESSION_ID_SECOND, notificationList.get(position).getSessionId()+"");
                    //edit.putInt(Constant.ACCEPTED_NOTIFICATION_ID, Integer.parseInt( notificationList.get(position).getString(Constant.NOTIFICATION_ID)));
                    edit.commit();
                    //Log.d(TAG,"SecondTripData "+"tripId"+ notificationList.get(position).getString("trip_id")+" Contact"+i notificationList.get(position).getString("phone")+" Notifi:"+Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID)));

                    sendOnlineAcceptDataToServer();

                }else  if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("0")){
                    // if no online notification is accepted and want to accept second first ride then save it

                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(Constant.CUSTOMER_CONTACT,  notificationList.get(position).getCustMobileNo());
                    edit.putString(Constant.START_LOCATION_NAME,  notificationList.get(position).getPickupLocName());
                    edit.putString(Constant.END_LOCATION_NAME,  notificationList.get(position).getDropLocName());
                    edit.putString(Constant.TRIP_ID,  notificationList.get(position).getBookingId());
                    edit.putString(Constant.NETPAY,  notificationList.get(position).getNetAmount());
                    edit.putString(Constant.DISTANCE,  notificationList.get(position).getDistance());
                    edit.putString(Constant.TIME,  notificationList.get(position).getDuration());
                    edit.putString(Constant.IS_PREPAID_RIDE,  notificationList.get(position).getIsPrepaid());
                    edit.putString(Constant.RIDETYPE, "online");
                    edit.putString(Constant.ACCEPT_RIDE, "0");
                    edit.putString(Constant.SESSION_ID, notificationList.get(position).getSessionId()+"");
                    //edit.putInt(Constant.ACCEPTED_NOTIFICATION_ID, Integer.parseInt( notificationList.get(position).getString(Constant.NOTIFICATION_ID)));
                    edit.commit();

                }
                //pref.getString(Constant.ACCEPT_RIDE,"0")
                //disableNotification();
                db.deleteAllNotificationData();

                Intent intent1 = new Intent(TripNotification.this, MapActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent1.putExtra(Constant.NOTIFICATION_ID,Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID))+"");
                intent1.putExtra("RideType", "online");
                startActivity(intent1);
                finish();

                int notificationId=pref.getInt(Constant.ACCEPTED_NOTIFICATION_ID,0);
                Log.d(TAG,"Notificationid:"+notificationId);

                /*}else {
                    Toast.makeText(TripNotification.this, R.string.pleasLogin,Toast.LENGTH_LONG).show();
                }*/


            }else {
                Toast.makeText(TripNotification.this, R.string.pleasLogin,Toast.LENGTH_LONG).show();
            }
        }else {

            Toast.makeText(TripNotification.this, R.string.noInternet,Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onItemDelete(View view, int position,ArrayList<Bean> arrayList) {
        //adapter.notifyDataSetChanged();
        Log.d("arrlistPos",position+"");
        if(!arrayList.isEmpty()) {
            db.deleteNotiRecord(arrayList.get(position).getSrId());
            arrayList.remove(position);
            adapter.notifyDataSetChanged();
           // adapter.notifyItemRemoved(position);
        }

        if(arrayList.size()<=0){
            Log.d("arrlistPos->>>>",position+"");
           /* if(timer!=null){
                timer.cancel();
            }
            stopBeep();*/

            adapter.stopCountDown();
            db.deleteAllNotificationData();
            adapter.notifyDataSetChanged();
            startActivity(new Intent(TripNotification.this,LoginActivity.class));
            clearNotification();
            finish();
        }


    }


    private void sendOnlineAcceptDataToServer() {

        pDialog = new ProgressDialog(TripNotification.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();
        String driverId=pref.getString(Constant.DRIVER_ID,"");
        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("pickup", pref.getString(Constant.START_LOCATION_NAME_SECOND,""));
            jsonObject1.put("drop",  pref.getString(Constant.END_LOCATION_NAME_SECOND,""));
            jsonObject1.put("amount",  pref.getString(Constant.NETPAY_SECOND,""));
            jsonObject1.put("duration",  pref.getString(Constant.TIME_SECOND,""));
            jsonObject1.put("distance",  pref.getString(Constant.DISTANCE_SECOND,""));
            jsonObject1.put("booking_id",  pref.getString(Constant.TRIP_ID_SECOND,""));
            jsonObject1.put("trip_type",  "online");
            jsonObject1.put("contact",  pref.getString(Constant.CUSTOMER_CONTACT_SECOND,""));
            jsonObject1.put("session_code",  pref.getString(Constant.SESSION_ID_SECOND,"")+"");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "TRIP_ACCEPT_URL" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.TRIP_ACCEPT_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.TRIP_ACCEPT_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");
                        pDialog.dismiss();


                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "TRIP_ACCEPT_URL_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                Toast.makeText(TripNotification.this,msg,Toast.LENGTH_SHORT).show();


                                String slat = "",slong="",dlat="",dlong="";
                                String distance = "",time="",netPay="",session="",trip_type="";
                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int j=0;j<jsonArray.length();j++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                                    distance = jsonObject.getString("distance");
                                    time = jsonObject.getString("time");
                                    netPay = jsonObject.getString("netPay");
                                    session = jsonObject.getString("session_code");
                                    trip_type = jsonObject.getString("trip_type");
                                    slat = jsonObject.getString("slat");
                                    slong = jsonObject.getString("slong");
                                    dlat = jsonObject.getString("dlat");
                                    dlong = jsonObject.getString("dlong");


                                }

                                //show online button linear layou,call,startride,endride

                                SharedPreferences.Editor edit= pref.edit();
                                edit.putString(Constant.SESSION_ID_SECOND,session);
                                edit.putString(Constant.NETPAY_SECOND,netPay);
                                edit.putString(Constant.TIME_SECOND,time);
                                edit.putString(Constant.DISTANCE_SECOND,distance);
                                edit.putString(Constant.END_LAT_SECOND,dlat);
                                edit.putString(Constant.END_LONG_SECOND,dlong);
                                edit.putString(Constant.CUST_LAT_SECOND,slat);
                                edit.putString(Constant.CUST_LONG_SECOND,slong);
                                edit.putString(Constant.ACCEPT_RIDE_SECOND,"1");
                                edit.commit();

                                Log.d("SecondOnlineLatLog","Source:"+slat+","+slong+" dest:"+dlat+","+dlong);
                                //disableNotification();

                                Intent intent1 = new Intent(TripNotification.this, MapActivity.class);
                                startActivity(intent1);
                                finish();

                            }else {
                                //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                            finish();
                        }
                        // pDialog.dismiss();
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
                        Toast.makeText(TripNotification.this, msg, Toast.LENGTH_SHORT).show();

                        JSONObject jobj=obj.getJSONObject("data");
                        for(int i=0;i<jobj.length();i++){
                            String status=jobj.getString("status");
                            if(status.equals("1")){

                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString(Constant.CUSTOMER_CONTACT_SECOND, "");
                                edit.putString(Constant.START_LOCATION_NAME_SECOND, "");
                                edit.putString(Constant.END_LOCATION_NAME_SECOND, "");
                                edit.putString(Constant.TRIP_ID_SECOND, "");
                                edit.putString(Constant.NETPAY_SECOND, "");
                                edit.putString(Constant.DISTANCE_SECOND, "");
                                edit.putString(Constant.TIME_SECOND, "");
                                edit.putString(Constant.RIDETYPE_SECOND, "");
                                edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");
                                edit.commit();
                                Intent intent1 = new Intent(TripNotification.this, MapActivity.class);
                                startActivity(intent1);
                                finish();
                            }else  if(status.equals("0")){//

                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString(Constant.CUSTOMER_CONTACT_SECOND, "");
                                edit.putString(Constant.START_LOCATION_NAME_SECOND, "");
                                edit.putString(Constant.END_LOCATION_NAME_SECOND, "");
                                edit.putString(Constant.TRIP_ID_SECOND, "");
                                edit.putString(Constant.NETPAY_SECOND, "");
                                edit.putString(Constant.DISTANCE_SECOND, "");
                                edit.putString(Constant.TIME_SECOND, "");
                                edit.putString(Constant.RIDETYPE_SECOND, "");
                                edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");
                                edit.commit();

                                //disableNotification();
                                Intent intent1 = new Intent(TripNotification.this, MapActivity.class);
                                startActivity(intent1);
                                finish();
                            }

                            // resetMapView();

                        }

                        Log.d("Error----->>>>>",res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                        finish();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                        finish();
                    }
                }else {
                    Log.d("Error----->>>>>","elsePart");
                    Toast.makeText(TripNotification.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                    // ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.networkproblem));
                    finish();
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
    protected void onDestroy() {
        super.onDestroy();
        if(pDialog!=null){
            pDialog.dismiss();
        }

        Log.d(TAG,"destroy--->>>>");

        if(adapter!=null) {
            adapter.stopCountDown();
        }
        /*if(timer!=null){
            timer.cancel();
        }*/

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Constant.IS_TRIP_NOTIFICATION_ACTIVE, false);
        ed.commit();

    }

    @Override
    public void onstopListener(View view, int position) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Constant.IS_TRIP_NOTIFICATION_ACTIVE, true);
        ed.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Constant.IS_TRIP_NOTIFICATION_ACTIVE, false);
        ed.commit();

    }



}
