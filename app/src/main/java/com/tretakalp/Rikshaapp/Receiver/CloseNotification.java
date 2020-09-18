package com.tretakalp.Rikshaapp.Receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.tretakalp.Rikshaapp.Other.Constant;

/**
 * Created by Mac on 09/10/18.
 */
public class CloseNotification extends BroadcastReceiver {
    SharedPreferences pref;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        // Create Notification Manager
        Log.d("CloseNotification","received");
        pref=context.getSharedPreferences(Constant.SHARED_PREF_NAME,Context.MODE_PRIVATE);

       // Toast.makeText(context,"CloseNotification",Toast.LENGTH_LONG).show();
       // int notificationId=pref.getInt(Constant.NOTIFICATION_ID,0);
        int notificationId=Integer.parseInt(intent.getStringExtra(Constant.NOTIFICATION_ID));
        Log.d("Closenotification","noooti"+notificationId+"");
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Dismiss Notificationc
        notificationmanager.cancel(notificationId);
        //notificationmanager.cancelAll();
        //notificationmanager.cancel(intent.getIntExtra("0", -1));

    }



}
