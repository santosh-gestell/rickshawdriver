package com.tretakalp.Rikshaapp.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tretakalp.Rikshaapp.Activity.TripNotification;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingServiceNew extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingServiceNew.class.getSimpleName();

    //private NotificationUtils notificationUtils;

    DatabaseHelper db;
    SharedPreferences pref;
    int notificationNumber;
    String driverId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From:" + remoteMessage.getFrom());
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,0);
        driverId=pref.getString(Constant.DRIVER_ID,"");
        db=DatabaseHelper.getInstance(this);
        if (remoteMessage == null)
            return;


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data_Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                boolean isLogin=pref.getBoolean(Constant.IS_LOGIN,false);
                if(isLogin) {

                        handleDataMessage(json);

                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }



    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "pushjson:->>>>>" + json.toString());
        db=new DatabaseHelper(this);
        String language = pref.getString(Constant.Locale_KeyValue, "");
        try {
            JSONObject data = json.getJSONObject("data");
            //check status , if status is 1 mean its new trip.. and if status 0 then someone accept
            //tht trip so if u have that trip in your listview it should be removed..
            //by using trip id it  will find which trip should be remove
            //If status is 2 then notification is of incentive type
            String status=data.getString("status").trim();
            Log.e(TAG, "DataStatus: " +data.getString("status"));
           if( status.equals("1")) {


                       String title = data.getString("title");
                       String message = data.getString("message");
                       // String name = data.getString("name");
                       String phone = data.getString("contact");
                       String drop = data.getString("drop");
                       String pickup = data.getString("pickup");
                       String triptype = data.getString("trip_type");
                       String trip_id = data.getString("trip_id");
                       String amount = data.getString("amount");
                       String distance = data.getString("distance");
                       String time = data.getString("timestamp");
                       String duration = data.getString("duration");
                       String seesionId = data.getString("session_code");
                       Log.d("mytag","TripType: "+triptype);
                        ///if
                        if(!data.getString("pickupmr").equals("") && language.equals("mr")) {
                            pickup = data.getString("pickupmr");
                            drop = data.getString("dropmr");
                        }

                       Bean bb = new Bean();
                       bb.setCustMobileNo(phone);
                       bb.setDriverId(driverId);
                       bb.setPickupLocName(pickup);
                       bb.setDropLocName(drop);
                       bb.setBookingId(trip_id);
                       bb.setNetAmount(amount);
                       bb.setDuration(duration);
                       bb.setIsPrepaid(triptype);
                       bb.setDistance(distance);
                       bb.setCheck_tripType(triptype);
                       bb.setSessionId(seesionId);
                       bb.setNotificationTitle(title);
                       bb.setMessage(message);
                       db.insertTripNotificationData(bb);

                       notificationNumber = pref.getInt(Constant.NOTIFICATION_ID, 0);

                       Log.d(TAG, "NotificationNumber: " + notificationNumber);
                       incrementNotificationNumber();
                       showNotificationMessage(pickup,amount, drop,message,distance, title, triptype);



                   //check tripnotificaition booking screen is visible to user if not the start tripnotificaition
                   if (!pref.getBoolean(Constant.IS_TRIP_NOTIFICATION_ACTIVE, false)) {

                       Log.d(TAG, "Class_IsNotVisible Notification");
                       Intent yesIntent = new Intent(this, TripNotification.class);
                       yesIntent.putExtra(Constant.NOTIFICATION_ID, notificationNumber + "");

                       yesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(yesIntent);

                   } else {
                       //if yes then call broadcast and refresh

                       Log.d(TAG, "Class_BroadCast Notification");

                       Intent pushNotification = new Intent(Constant.PUSH_NOTIFICATION);
                       LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                   }



           }else if( status.equals("0")){
          /**NOTE:  status 0 means if notification with specific id visible on the screen and some other driver
            accepted that notification then.. new response will come and it contains status 0 and tripid
           accoundring to tht trip id delete exsiting notification from screen

           */

               Log.d(TAG,"Class_RejectedNotification");
               String trip_id = data.getString("trip_id");
               db.deleteNotification(trip_id);

              /* Intent pushNotification = new Intent(Constant.PUSH_NOTIFICATION);
               LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);*/


               /** if user */
              // if(db.getTripNotification(driverId).size()>0) {

                   sendToTripNotificationScreen();
               //}




           }else if( data.getString("status").trim().equals("2")){

               notificationNumber = pref.getInt(Constant.NOTIFICATION_ID, 0);
               Log.d(TAG, "NotificationNumber: " + notificationNumber);
               incrementNotificationNumber();

               String title = data.getString("title");
               String message = data.getString("message");
               String imageUrl = data.getString("image");
               String notificationId = data.getString("notification_id");
               String dateTo = data.getString("date_to");
               String date_from = data.getString("date_from");

               Bean b=new Bean();
               b.setNotificationTitle(title);
               b.setMessage(message);
               b.setNotifyId(notificationId);
               b.setDate_To(dateTo);
               b.setDate_From(date_from);
               b.setImage(imageUrl);
               db.insertIncentiveNotiData(b);



               showNotificationMessageWithBigImage(this,title,message,imageUrl);

               /*if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                   Bitmap bitmap = getBitmapFromURL(imageUrl);

                   if (bitmap != null) {
                       showNotificationMessageWithBigImage(bitmap,this,title,message,imageUrl);
                   }
               }*/

           }else if( status.equals("3")){

               SharedPreferences.Editor edit = pref.edit();
               Log.d(TAG,"Class_RejectedNotification");
               String trip_id = data.getString("trip_id");
                if(pref.getString(Constant.TRIP_ID,"").equals(trip_id)) {

                    //edit.putInt(Constant.ISRIDE_START, 0);
                    // edit.commit();
                    //if user accepted seond ride then cancel first and accept second ride will be asssign to first. and scond which now first will be started
                    if (pref.getString(Constant.ACCEPT_RIDE_SECOND, "0").equals("1")) {


                        edit.putString(Constant.CUSTOMER_CONTACT, pref.getString(Constant.CUSTOMER_CONTACT_SECOND, ""));
                        edit.putString(Constant.START_LOCATION_NAME, pref.getString(Constant.START_LOCATION_NAME_SECOND, ""));
                        edit.putString(Constant.END_LOCATION_NAME, pref.getString(Constant.END_LOCATION_NAME_SECOND, ""));
                        edit.putString(Constant.TRIP_TYPE, pref.getString(Constant.TRIP_TYPE,""));
                        edit.putString(Constant.TRIP_ID, pref.getString(Constant.TRIP_ID_SECOND, ""));
                        edit.putString(Constant.NETPAY, pref.getString(Constant.NETPAY_SECOND, ""));
                        edit.putString(Constant.DISTANCE, pref.getString(Constant.DISTANCE_SECOND, ""));
                        edit.putString(Constant.TIME, pref.getString(Constant.TIME_SECOND, ""));
                        edit.putString(Constant.RIDETYPE, "online");
                        edit.putString(Constant.ACCEPT_RIDE, "1");
                        edit.putString(Constant.SESSION_ID, pref.getString(Constant.SESSION_ID_SECOND, ""));
                        edit.putString(Constant.IS_PREPAID_RIDE, pref.getString(Constant.IS_PREPAID_RIDE, ""));
                        edit.putString(Constant.END_LAT, pref.getString(Constant.END_LAT_SECOND, ""));
                        edit.putString(Constant.END_LONG, pref.getString(Constant.END_LONG_SECOND, ""));
                        edit.putString(Constant.CUST_LAT, pref.getString(Constant.CUST_LAT_SECOND, ""));
                        edit.putString(Constant.CUST_LONG, pref.getString(Constant.CUST_LONG_SECOND, ""));
                        edit.putString(Constant.ACCEPT_RIDE_SECOND, "0");//reset it

                    } else {
                        edit.putString(Constant.RIDETYPE, "");
                        edit.putString(Constant.ACCEPT_RIDE, "0");
                    }


                }else if(pref.getString(Constant.TRIP_ID_SECOND,"").equals(trip_id)){

                    /** if user accepted two notification and one ride is already ongoing process.. and noti of cancel of second accepted ride which (is in background)
                     *  comes
                    tht time it will get cancel .. and driver when end frist ride he will not see second accepted ride on mapactivity bcoz it is cancel
                    */
                    edit.putString(Constant.CUSTOMER_CONTACT_SECOND, "");
                    edit.putString(Constant.START_LOCATION_NAME_SECOND,"");
                    edit.putString(Constant.END_LOCATION_NAME_SECOND, "");
                    edit.putString(Constant.TRIP_ID_SECOND,  "");
                    edit.putString(Constant.NETPAY_SECOND,  "");
                    edit.putString(Constant.DISTANCE_SECOND,  "");
                    edit.putString(Constant.TIME_SECOND, "");
                    edit.putString(Constant.RIDETYPE_SECOND, "");
                    edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");
                    edit.putString(Constant.SESSION_ID_SECOND, "");

                }

               edit.commit();
               Intent yesIntent = new Intent(this, TripNotification.class);
               yesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(yesIntent);
           }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void sendToTripNotificationScreen() {

        if (!pref.getBoolean(Constant.IS_TRIP_NOTIFICATION_ACTIVE,false)) {
            Log.d(TAG, "Class_IsNotVisible Notification");
           // if(db.getTripNotification(driverId).size()>0) {

                Log.d(TAG, "Class_IsNotVisible Notification1");
                Intent yesIntent = new Intent(this, TripNotification.class);
                yesIntent.putExtra(Constant.NOTIFICATION_ID, notificationNumber + "");

                yesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(yesIntent);


        } else {
            //if yes then call broadcast and refresh

            Log.d(TAG, "Class_BroadCast Notification");

            Intent pushNotification = new Intent(Constant.PUSH_NOTIFICATION);
            pushNotification.putExtra(Constant.NOTIFICATION_ID, notificationNumber + "");
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }

    }

    private void showNotificationMessage(String pickup,String amount, String drop,String message,String distance,String title, String tripType) {


        // play notification sound
        //  NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        // notificationUtils.playNotificationSound();

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {1000,1000, 1000};
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(getResources().getString(R.string.source) + ":" + pickup + "\n" + getResources().getString(R.string.destination) + ":" + drop + "\n" + getResources().getString(R.string.kilometer) + ":" + distance + "\t\t"
                + getResources().getString(R.string.fare) + ": " + "\u20B9" + amount);

        String channelId = "channel-" + notificationNumber;
       // String channelName = "Trip Ride" + notificationNumber;
        String channelName = "Trip Ride" ;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            //builder.setChannelId(channelId);
            manager.createNotificationChannel(mChannel);
        }/*else {*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification));
        builder.setContentTitle(message);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setColor(getResources().getColor(R.color.colorPrimary));
        builder.setSound(uri);
        builder.setVibrate(v);
        builder.setAutoCancel(true);
        //builder.addAction(android.R.drawable.ic_menu_close_clear_cancel,getResources().getString( R.string.reject), rejectbtnIntent);
        //builder.addAction(R.mipmap.ic_tick2, getResources().getString(R.string.acceptRide), yesbtnIntent);
        builder.setStyle(bigText);
        builder.setContentText(title + getResources().getString(R.string.source) + ": " + pickup);
        builder.build();
                       /* }*/

        //}
        Intent notificationIntent = new Intent(this, TripNotification.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, notificationNumber, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(contentIntent);
        manager.notify(notificationNumber, builder.build());


    }

    private void incrementNotificationNumber() {

        SharedPreferences.Editor editor = pref.edit();
        notificationNumber++;
        editor.putInt(Constant.NOTIFICATION_ID, notificationNumber);
        editor.commit();
    }

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }



    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String imageUrl) {

       /* NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(message);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);*/


        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {1000,1000, 1000};
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);

        String channelId = "channel-" + notificationNumber;
        String channelName = "Channel Name" + notificationNumber;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            //builder.setChannelId(channelId);
            manager.createNotificationChannel(mChannel);
        }/*else {*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification));
        builder.setContentTitle(message);
        //builder.setStyle(bigPictureStyle);
        builder.setStyle(bigText);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setColor(getResources().getColor(R.color.colorPrimary));
        builder.setSound(uri);
        builder.setVibrate(v);
        builder.setAutoCancel(true);
        //builder.setStyle(bigText);
        builder.setContentText(title);
        builder.build();
        Intent notificationIntent = new Intent(this, TripNotification.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, notificationNumber, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(contentIntent);
        manager.notify(notificationNumber, builder.build());

    }



    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
