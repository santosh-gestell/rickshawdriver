package com.tretakalp.Rikshaapp.Receiver;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tretakalp.Rikshaapp.Activity.LoginActivity;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

//import com.tretakalp.Rikshaapp.Activity.MainActivity;


/**
 * Created by Mac on 12/06/17.
 */
public class NetworkReceiver extends BroadcastReceiver {


    double battery;
    private static final String TAG = "NetworkReceiver";
    boolean internetflag;
    SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "ScreenLock");


        battery=getBatteryLevel(context);
        //getting value of user id
         pref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the String user_id value form sharedpreferences

         boolean isLogin=pref.getBoolean(Constant.IS_LOGIN,false);


        if(!isLogin) {

            //Log.d(TAG,"hasInternetConnection "+hasInternetConnectionM(context)+"");
            String driverId=pref.getString(Constant.DRIVER_ID,"");


            if (intent.getAction().equals("android.intent.action.AIRPLANE_MODE")) {


            }

            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")||intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {



                if(DetectConnection.checkInternetConnection(context)) {

                    checkDriverCurrentStatus(context);

                }else{


                }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static double getBatteryLevel(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR) {
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
            return (level * 100.0) / scale;
        } else {
            return 0;
        }
    }


    //this check driver status .. if he submit data or not.. if user using app in offline mode then it will not call
    //when he turn on internet then it will invoke and if deactivate it
    private void checkDriverCurrentStatus(final Context context) {

        JSONObject jsonObject1 = null;

        try {


            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", pref.getString(Constant.DRIVER_ID,""));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "checkDriverCurrentStatus_URL" + jsonObject1.toString());
        Log.d(TAG, "checkDriverCurrentStatus_URl" + Constant.DRIVER_CURRENT_STATUS_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.DRIVER_CURRENT_STATUS_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        //pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getcheckDriverCurrentStatus_Submit_result" + result);
                            if (result.trim().equals("200")) {


                                JSONObject jsonObject= response.getJSONObject("data");

                                String status = jsonObject.getString("status");
                                if(status.equals("1")){
                                    // Toast.makeText(MapActivity.this,msg,Toast.LENGTH_SHORT).show();
                                    if(!TextUtils.isEmpty(msg)) {
                                        addNotification(msg,context);
                                    }
                                }

                            }else {
                                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
                        // Toast.makeText(MapActivity.this,msg,Toast.LENGTH_SHORT).show();

                        JSONObject jsonObject= obj.getJSONObject("data");

                        String status = jsonObject.getString("status");
                        if(status.equals("1")){
                            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        }

                        /*if(status.equals("0")){
                            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                            if(pref.getInt(Constant.ISRIDE_START,0)!=1&&!pref.getString(Constant.ACCEPT_RIDE,"0").equals("1")) {
                                logOut(context);
                            }
                        }*/

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
                    Toast.makeText(context, R.string.networkproblem, Toast.LENGTH_SHORT).show();
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



    private void addNotification(String msg,Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_arrow)
                        .setContentTitle("Rickshaw Application")
                        .setContentText(msg);

        Intent notificationIntent = new Intent(context, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


    /**Logout */
    private void logOut(Context context) {

        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean(Constant.IS_LOGIN,false);
        edt.putInt(Constant.ISRIDE_START,0);
        edt.putInt( Constant.DAYOFWEEK,0);
        edt.putString( Constant.RIDETYPE,"");
        edt.putString( Constant.ACCEPT_RIDE,"0");
        edt.putString( Constant.ACCEPT_RIDE_SECOND,"0");


        // edt.clear();
        edt.commit();
        context.startActivity(new Intent(context, LoginActivity.class));
        //context.finish();

    }



}