package com.tretakalp.Rikshaapp.Service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.tretakalp.Rikshaapp.Activity.LoginActivity;
import com.tretakalp.Rikshaapp.Activity.MapActivity;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;


/**
 * Created by Mac on 25/05/17.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<Status> {
    private static final String TAG = "LocationServiceClass";
    public static GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public Context context;

    private SharedPreferences pref;

    FusedLocationProviderClient mFusedLocationClient;
    private boolean currentlyProcessingLocation = false;
    public AlertDialog alertdialog;
    int locationMode;
    private WindowManager mWindowManager;
    private View mFloatingView;
    ImageView openButton;


    Location mlastLocation;
    public LocationService(Context trackerList) {
        this.context = trackerList;
    }

    public LocationService() {
    }

    IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        pref = this.getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        openButton = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv1);

        showWidget();
    }


    public void showWidget() {

      //  if(pref.getInt(Constant.ISRIDE_START,0)==1) {

            //Add the view to the window.
            final WindowManager.LayoutParams params;
            if (Build.VERSION.SDK_INT <= 23) {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            } else {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }

            //Specify the view position
            params.gravity = Gravity.CENTER | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;


        try {
            mWindowManager.addView(mFloatingView, params);
        } catch (Exception e) {

        }


        //Open the application on thi button click

            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Open the application  click.
                    Intent intent = new Intent(LocationService.this, MapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                   // disableFloatingWidget();

                    //close the service and remove view from the view hierarchy
                    //stopSelf();
                }
            });

            //Drag and move floating view using user's touch action.
            mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            int Xdiff = (int) (event.getRawX() - initialTouchX);
                            int Ydiff = (int) (event.getRawY() - initialTouchY);

                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                        /*if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }*/
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);

                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            });

      //  }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;

            //startBackGroundService();
             startTracking();

        }

        return START_REDELIVER_INTENT;
    }

    private void startBackGroundService() {

        Intent notificationIntent = new Intent(this, LocationService.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.mipmap.gt_new_logo)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Rickshaw")
                .setContentText("Tracking location...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);

    }

    private void startTracking() {
        Log.d(TAG, "startTracking");
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
            createLocationRequest();
            startLocationUpdate();
        }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

        pref = this.getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        boolean isLogin=pref.getBoolean(Constant.IS_LOGIN,false);
        /**imp line*/
        if(isLogin) {
            startupdate(location);
        }


    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
       // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    public void removeLocationUpdates() {
        Log.d(TAG,"removeLocationUpdates");
        //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        if(mFusedLocationClient!=null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public Location getLastLocation() {
        Log.d(TAG,"removeLocationUpdates");
        if(mlastLocation!=null){
            return mlastLocation;
        }
        return null;
    }

    private void startupdate(Location location) {

        if (location != null) {
            Log.e(TAG, "LocationUpdate: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy() + " provider: " + location.getProvider() + " altitude: " + location.getAltitude() + " bearing: " + location.getBearing());
            locationMode = Constant.getLocationMode(this);
            if (Constant.isgpsAvailable(this)&&locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {

                if(DetectConnection.checkInternetConnection(this)) {


                    calculateDistance(location);

                    insertCurrentLocationToServer(location.getLatitude() + "", location.getLongitude() + "", location.getAccuracy() + "");
                }
            }
        }

    }

    //if ride is start then it will calculate
    private void calculateDistance(Location location) {

        if(pref.getInt(Constant.ISRIDE_START,0)==1) {

            if(mFloatingView==null){
                Log.d(TAG,"ssdiss->>>>");
                if(isAppIsInBackground(this)) {
                    Log.d(TAG,"showWidget->>>>");
                    if (mFloatingView == null){
                        showWidget();
                    }
                }else {
                    Log.d(TAG,"sst->>>>");
                  //  disableFloatingWidget();
                }
            }else {
                Log.d(TAG,"dis->>>>");
                if(!isAppIsInBackground(this)) {
                    Log.d(TAG,"foreground->>>>");
                   // disableFloatingWidget();
                }
            }

            if (mlastLocation != null) {
               // double dist = distance(location.getLatitude(), location.getLongitude(), mlastLocation.getLatitude(), mlastLocation.getLongitude());

                double dist=(location.distanceTo(mlastLocation))/1000;//in meter

                if(dist<=0) {
                    dist=0;
                }

                storeTotalDistance(dist);

                /*if(dist<1) {
                        storeTotalDistance(dist);
                    }else {
                        Log.d(TAG,"distance is greater than 2 km");
                    }*/

            }
            mlastLocation=location;
        }else {
            mlastLocation=null;
            //disableFloatingWidget();
        }

    }

    private void storeTotalDistance(double dist) {

        double totalDist= Double.parseDouble(pref.getString(Constant.DYANAMIC_DISTANCE,"0"))+dist;
        SharedPreferences.Editor editor= pref.edit();
        editor.putString(Constant.DYANAMIC_DISTANCE,totalDist+"");
        editor.commit();
        Log.d(TAG,"TotalDynamicDistance: "+String.format("%.2f", totalDist));

    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(20000);// milliseconds
        locationRequest.setFastestInterval(20000);// the fastest rate in milliseconds at which your app can handle location updates
       //locationRequest.setSmallestDisplacement(1*1000);//1 km
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public void stopLocationUpdates() {

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }



    @Override
    public void onResult(@NonNull Status status) {

    }


    //get location using
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){

                   // Log.d(TAG,"mLocationCallback "+" fusedApiClient:"+location.getLatitude()+" long:"+location.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    startupdate(location);
                    //insertCurrentLocationToServer(location.getLatitude()+"",location.getLongitude()+"",location.getAccuracy()+"");

            }
        }
    };


    private void insertCurrentLocationToServer(String lat,String longitude,String accuracy) {


        double battery=Constant.getBatteryLevel(this);
        String currentdate =Constant.getDateTime();
        String driverId=pref.getString(Constant.DRIVER_ID,"");

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("lat", lat);
            jsonObject1.put("long", longitude);
            jsonObject1.put("accuracy", accuracy);
            jsonObject1.put("provider", "fused location");
            jsonObject1.put("date_time", currentdate);
            jsonObject1.put("battery", battery);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "INSERT_TRACKING_URL" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.INSERT_TRACKING_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.INSERT_TRACKING_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        //pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "INSERT_TRACKING_URL_Submit_result" + result);
                            if (result.trim().equals("200")) {


                            }else {
                                pref.edit().putString(Constant.QUEUE_STATUS,"1").commit();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // pDialog.dismiss();
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        // Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
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
                    //  Toast.makeText(getContext(), R.string.networkproblem, Toast.LENGTH_SHORT).show();
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



    private  void sendMessageToActivity(String msg) {
        Intent intent = new Intent("gestell.com.gtracker.Location");
        Log.d(TAG,"sendMessageToActivity: "+msg);
        // You can also include some extra data.
        Bundle b = new Bundle();
        intent.putExtra("Status", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //when service kill..this method will call
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Service Stopped");

        if(pref.getInt(Constant.ISRIDE_START,0)==1) {
            addNotification(getResources().getString(R.string.keeprickshawappopen));
            if (mFloatingView == null){
                showWidget();
            }
        }else {
           // if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
           // disableFloatingWidget();
        }

        //if Service stop by some problem before valid time then... check flag and start service
       // disableFloatingWidget();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG,"Service onTaskRemoved");
        showWidget();
        if(pref.getInt(Constant.ISRIDE_START,0)==1) {

            addNotification(getResources().getString(R.string.keeprickshawappopen));
                    if (mFloatingView == null){
                        showWidget();
                        }
        }else {
            //if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
            //disableFloatingWidget();
        }
       // startService(new Intent(this, LocationService.class));
       // disableFloatingWidget();

    }

    public void disableFloatingWidget() {
        try {
            if (mFloatingView != null) {

                if (mWindowManager != null) {
                    mWindowManager.removeView(mFloatingView);
                }
            }
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }


    private void addNotification(String msg) {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {2000,2000};
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        final int random = new Random().nextInt(61) + 20;
        String channelId = "channel-_" + random;
        String channelName = "Channel_Name" + random;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            //builder.setChannelId(channelId);
            manager.createNotificationChannel(mChannel);
        }/*else {*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification))
                .setContentTitle("Rickshaw Application")
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .setSound(uri)
                .setVibrate(v)
                .setStyle(bigText)
                .setAutoCancel(true)
                .setContentText(msg);

        Intent notificationIntent = new Intent(this,LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        // Add as notification
        manager.notify(0, builder.build());

    }

}
