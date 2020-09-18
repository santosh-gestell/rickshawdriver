package com.tretakalp.Rikshaapp.Other;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Mac on 09/03/17.
 */
public class DetectConnection {



    public static boolean checkInternetConnection(Context context) {

        if(context!=null) {
            ConnectivityManager con_manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (con_manager.getActiveNetworkInfo() != null
                    && con_manager.getActiveNetworkInfo().isAvailable()
                    && con_manager.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    /*public static boolean checkPowerSaverMode(Context context) {


            PowerManager powerManager = (PowerManager)
                    context.getSystemService(Context.POWER_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && powerManager.isPowerSaveMode())

            {

            }

    }*/


}
