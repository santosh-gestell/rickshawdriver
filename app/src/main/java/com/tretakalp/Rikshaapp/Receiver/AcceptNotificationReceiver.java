package com.tretakalp.Rikshaapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.tretakalp.Rikshaapp.Activity.MapActivity;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

/**
 * Created by Mac on 09/10/18.
 */
public class AcceptNotificationReceiver extends BroadcastReceiver {
    SharedPreferences pref;
    @Override
    public void onReceive(Context context, Intent intent) {
        pref=context.getSharedPreferences(Constant.SHARED_PREF_NAME,Context.MODE_PRIVATE);
        boolean isLogin=pref.getBoolean(Constant.IS_LOGIN,false);

        if(DetectConnection.checkInternetConnection(context)) {
            if (isLogin) {

                SharedPreferences.Editor edit = pref.edit();
                edit.putString(Constant.CUSTOMER_CONTACT, intent.getStringExtra("phone"));
                edit.putString(Constant.START_LOCATION_NAME, intent.getStringExtra("pickup"));
                edit.putString(Constant.END_LOCATION_NAME, intent.getStringExtra("drop"));
                edit.putString(Constant.TRIP_ID, intent.getStringExtra("trip_id"));
                edit.putString(Constant.NETPAY, intent.getStringExtra("amount"));
                edit.putString(Constant.DISTANCE, intent.getStringExtra("distance"));
                edit.putString(Constant.TIME, intent.getStringExtra("time"));
                edit.putString(Constant.RIDETYPE, "online");
                edit.putString(Constant.ACCEPT_RIDE,"0");
                edit.putInt(Constant.ACCEPTED_NOTIFICATION_ID, Integer.parseInt(intent.getStringExtra(Constant.NOTIFICATION_ID)));
                edit.commit();

                /*NotificationManager notificationmanager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                // Dismiss Notificationc
                notificationmanager.cancel(0);*/

                Intent intent1 = new Intent(context, MapActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("RideType", "online");
                context.startActivity(intent1);

            }else {
                Toast.makeText(context, R.string.pleasLogin,Toast.LENGTH_LONG).show();
            }
        }else {

            Toast.makeText(context, R.string.noInternet,Toast.LENGTH_LONG).show();
        }

    }

}
