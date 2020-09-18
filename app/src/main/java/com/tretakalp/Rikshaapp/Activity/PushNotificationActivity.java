package com.tretakalp.Rikshaapp.Activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Mac on 09/10/18.
 */
public class PushNotificationActivity extends AppCompatActivity {
    private static final String TAG ="PushNotificationActivity" ;
    SharedPreferences pref;
    int notifyId;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean isLogin=pref.getBoolean(Constant.IS_LOGIN,false);
        Bundle intent=getIntent().getExtras();
        notifyId=Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID));

        if(DetectConnection.checkInternetConnection(this)) {
            if (isLogin) {
                // if two online notification is accepted and want to accept third ride notitification then dont allow
                //driver can accept two ride at a time
                if(pref.getString(Constant.ACCEPT_RIDE_SECOND,"0").equals("1")){

                    Toast.makeText(PushNotificationActivity.this,R.string.pleaseCompleteyourpendingRide,Toast.LENGTH_LONG).show();
                }else
                if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("1")){
                    // if one online notification is accepted and want to accept second ride then save it for later use
                    //driver can accept two ride at a time


                    //for second Notification
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(Constant.CUSTOMER_CONTACT_SECOND, intent.getString("phone"));
                    edit.putString(Constant.START_LOCATION_NAME_SECOND, intent.getString("pickup"));
                    edit.putString(Constant.END_LOCATION_NAME_SECOND, intent.getString("drop"));
                    edit.putString(Constant.TRIP_ID_SECOND, intent.getString("trip_id"));
                    edit.putString(Constant.NETPAY_SECOND, intent.getString("amount"));
                    edit.putString(Constant.DISTANCE_SECOND, intent.getString("distance"));
                    edit.putString(Constant.TIME_SECOND, intent.getString("time"));
                    edit.putString(Constant.RIDETYPE_SECOND, "online");
                    edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");
                    edit.putString(Constant.TRIP_TYPE,"triptype");
                    edit.putString(Constant.SESSION_ID_SECOND,intent.getString("session_id")+"");
                    edit.putInt(Constant.ACCEPTED_NOTIFICATION_ID, Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID)));
                    edit.putString(Constant.IS_PREPAID_RIDE, intent.getString(""));
                    edit.commit();
                    Log.d(TAG,"SecondTripData "+"tripId"+intent.getString("trip_id")+" Contact"+intent.getString("phone")+" Notifi:"+Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID)));

                    sendOnlineAcceptDataToServer();

                }else  if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("0")){
                    // if no online notification is accepted and want to accept second first ride then save it

                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(Constant.CUSTOMER_CONTACT, intent.getString("phone"));
                    edit.putString(Constant.START_LOCATION_NAME, intent.getString("pickup"));
                    edit.putString(Constant.END_LOCATION_NAME, intent.getString("drop"));
                    edit.putString(Constant.TRIP_ID, intent.getString("trip_id"));
                    edit.putString(Constant.NETPAY, intent.getString("amount"));
                    edit.putString(Constant.DISTANCE, intent.getString("distance"));
                    edit.putString(Constant.TIME, intent.getString("time"));
                    edit.putString(Constant.RIDETYPE, "online");
                    edit.putString(Constant.ACCEPT_RIDE, "0");
                    edit.putString(Constant.TRIP_TYPE, intent.getString(""));
                    edit.putString(Constant.SESSION_ID,intent.getString("session_id")+"");
                    edit.putInt(Constant.ACCEPTED_NOTIFICATION_ID, Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID)));
                    edit.commit();
                    Log.d(TAG,"FirstTripData "+"tripId:"+intent.getString("trip_id")+" Contact:"+intent.getString("phone")+" Notifi:"+Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID)));


                }
                //pref.getString(Constant.ACCEPT_RIDE,"0")
                //disableNotification();

                Intent intent1 = new Intent(PushNotificationActivity.this, TripNotification.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra(Constant.NOTIFICATION_ID,Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID))+"");
                intent1.putExtra("RideType", "online");
                startActivity(intent1);
                finish();

                int notificationId=pref.getInt(Constant.ACCEPTED_NOTIFICATION_ID,0);
                Log.d(TAG,"Notificationid:"+notificationId);

            }else {
                Toast.makeText(PushNotificationActivity.this, R.string.pleasLogin,Toast.LENGTH_LONG).show();
            }
        }else {

            Toast.makeText(PushNotificationActivity.this, R.string.noInternet,Toast.LENGTH_LONG).show();
        }

    }


    private void sendOnlineAcceptDataToServer() {

        /*pDialog = new ProgressDialog(PushNotificationActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();*/
        String driverId=pref.getString(Constant.DRIVER_ID,"");
        String isPrepaid=pref.getString(Constant.IS_PREPAID_RIDE,"");

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("pickup", pref.getString(Constant.START_LOCATION_NAME_SECOND,""));
            jsonObject1.put("drop",  pref.getString(Constant.END_LOCATION_NAME_SECOND,""));
            jsonObject1.put("trip_id",  pref.getString(Constant.TRIP_ID,""));
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

        String URL = "";
        if (isPrepaid.equals("prepaid")){
           URL =  Constant.TRIP_ACCEPT_URL_PREPAID;
        }else{
            URL =  Constant.TRIP_ACCEPT_URL;
        }

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");


                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "TRIP_ACCEPT_URL_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                Toast.makeText(PushNotificationActivity.this,msg,Toast.LENGTH_SHORT).show();


                                String slat = "",slong="",dlat="",dlong="";
                                String distance = "",time="",netPay="",session="",trip_type="", is_ride_type = "";
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
                                disableNotification();

                                Intent intent1 = new Intent(PushNotificationActivity.this, TripNotification.class);
                                startActivity(intent1);
                                finish();

                            }else {
                                //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                       // pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                //pDialog.dismiss();

                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(PushNotificationActivity.this, msg, Toast.LENGTH_SHORT).show();

                        JSONObject jobj=obj.getJSONObject("data");
                        for(int i=0;i<jobj.length();i++){
                            String status=jobj.getString("status");
                            Log.i("Result: ","Status: " + status);
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
                                edit.putString(Constant.IS_PREPAID_RIDE,"");
                                edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");
                                edit.commit();
                                Intent intent1 = new Intent(PushNotificationActivity.this, TripNotification.class);
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
                                edit.putString(Constant.IS_PREPAID_RIDE,"");
                                edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");
                                edit.commit();

                                disableNotification();
                                Intent intent1 = new Intent(PushNotificationActivity.this, TripNotification.class);
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
                    Toast.makeText(PushNotificationActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
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

    private void disableNotification() {
       // int notificationId=pref.getInt(Constant.ACCEPTED_NOTIFICATION_ID,0);
        //Log.d(TAG,"Notificationid:"+notificationId);

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //notificationmanager.cancel(notifyId);
        notificationmanager.cancelAll();
    }


    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}
