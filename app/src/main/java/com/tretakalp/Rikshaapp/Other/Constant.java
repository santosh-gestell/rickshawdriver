package com.tretakalp.Rikshaapp.Other;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mac on 03/09/18.
 */
public class Constant {

    public static final String FIREBASE_SHARED_PREF_NAME ="firebase_shared_Name" ;
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    // id to handle the notification in the notification tray
    //public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;


    public static final String SHARED_PREF_NAME ="TretakalpRiksha" ;

    public static final String LANGUAGE ="language" ;
    //public static final String LOCAL_HOST ="http://tretakalp.com/rickshaw/index.php/Api/" ;
    //public static final String LOCAL_HOST ="http://tretakalp.com/driver_test/index.php/Api/" ;
    public static final String LOCAL_HOST ="https://tretakalp.com/driver_test/index.php/Api/" ; // test
    //public static final String LOCAL_HOST ="https://tretakalp.com/rickshaw/index.php/Api5.0/";   // online
    //public static final String LOCAL_HOST = "https://tretakalp.com/rickshaw/index.php/Api4.0/"; // new

    //public static final String LOCAL_HOST ="http://tretakalp.com/rickshaw/index.php/Api1.3/" ;//new link
    //public static final String LOCAL_HOST ="http://tretakalp.com/rickshaw/index.php/Api1.4/" ;//new new link
    //public static final String LOCAL_HOST ="http://tretakalp.com/rickshaw/index.php/Api1.7/" ;//new new link
    //public static final String LOCAL_HOST ="http://tretakalp.com/rickshaw/index.php/Api1.8/" ;//new new link
    //public static final String LOCAL_HOST ="http://tretakalp.com/rickshaw/index.php/Api1.9/" ;//new new link
    //public static final String LOCAL_HOST ="http://tretakalp.com/rickshaw/index.php/Api2.0/" ;//new new link
    //public static final String LOCAL_HOST ="https://tretakalp.com/rickshaw/index.php/Api2.1/" ;//new new link
    //public static final String LOCAL_HOST ="https://tretakalp.com/rickshaw/index.php/Api2.2/" ;//new new link

    public static final String GET_OTP_URL =LOCAL_HOST+"Login/signupOTP" ;//1
    public static final String DRIVER_CURRENT_STATUS_URL =LOCAL_HOST+"login/driverCurrentStatus" ;//1
    public static final String EARNING_DRIVER_URL =LOCAL_HOST+"static_calculation/earningDriver" ;//1
    public static final String TRIP_HISTORY_URL =LOCAL_HOST+"static_calculation/trip_history" ;//1
    public static final String GET_DRIVER_URL =LOCAL_HOST+"Login/profile" ;//1
    public static final String STATIC_CALC_URL =LOCAL_HOST+"static_calculation/getStaticFare" ;//1
    public static final String ADD_QUEUE =LOCAL_HOST+"static_calculation/driver_queue" ;//1
    public static final String DE_QUEUE =LOCAL_HOST+"static_calculation/driver_dequeue" ;//1
    public static final String GETDOCUMENT_URL =LOCAL_HOST+"document/getDocument" ;//1
    public static final String TRIP_ACCEPT_URL =LOCAL_HOST+"static_calculation/tripAccept" ;//1
    public static final String TRIP_ACCEPT_URL_PREPAID =LOCAL_HOST+"static_calculation/prepaid_notification_second" ;//1 prepaid
    public static final String CANCEL_REASON_URL =LOCAL_HOST+"document/getCancelledReason";//1
    public static final String GET_INCENTIVE_URL =LOCAL_HOST+"document/getIncentive";//1
    public static final String GET_PAYMENT_DETAIL_URL =LOCAL_HOST+"document/paymentDetails";//1

    public static final String SEND_OTP_VERIFIED_URL =LOCAL_HOST+"Login/otpVarification" ;//2
    public static final String ADD_DOCUMENT_URL =LOCAL_HOST+"document/add_document" ;//2
    public static final String SIGN_UP_URL =LOCAL_HOST+"Login/signUp" ;
    public static final String LOGIN_URL =LOCAL_HOST+"Login/user_verify" ;

    public static final String UPDATE_PROFILE_URL =LOCAL_HOST+"login/profileUpdate" ;//2

    public static final String STARTRIDE_URL =LOCAL_HOST+"static_calculation/startRide" ;//2
    public static final String ISPREPAID_URL =LOCAL_HOST+"static_calculation/prepaid_notification" ;//2
    public static final String ENDTRIP_URL =LOCAL_HOST+"static_calculation/endTrip" ;//12


    public static final String MAKE_PAYMENT_URL =LOCAL_HOST+"static_calculation/makePayment" ;//
    public static final String CHANGE_PASS_URL =LOCAL_HOST+"Login/changePass" ;//2
    public static final String RESEND_OTP_URL =LOCAL_HOST+"static_calculation/resendOTP" ;//2
    public static final String INSERT_TRACKING_URL =LOCAL_HOST+"tracking/insertTracking" ;//2



    public static final String TRIP_CANCEL_URL =LOCAL_HOST+"static_calculation/cancelTrip" ;//2
    public static final String TRIP_CANCEL_URL_PREPAID =LOCAL_HOST+"static_calculation/cancelPrepaidTrip" ;//12 prepaid

    public static final String CHANGE_LANG_URL =LOCAL_HOST+"login/changeLanguage";//2
    public static final String FORGOT_PASSWORD_URL =LOCAL_HOST+"login/forgotPass";//2
    public static final String CHANGE_FIREBASE_URL =LOCAL_HOST+"login/changeFirebase";//2

    public static final String SIGN_IN_URL =LOCAL_HOST+"/sign_up" ;//1
    public static final String TOPIC_ONE_URL =LOCAL_HOST+"/Document_link" ;//1
    public static final String TOPIC_TWO_URL =LOCAL_HOST+"/video_app" ;//1

    public static final String GET_HEADER_QUERY_URL =LOCAL_HOST+"/query/getQueriesHeader" ;//1
    public static final String SEND_HEADER_QUERY_URL =LOCAL_HOST+"/query/addQuery" ;//2
    public static final String GET_QUERIES_REPLY_QUERY_URL =LOCAL_HOST+"/query/getQueries" ;//1
    public static final String GET_ISSUE_URL =LOCAL_HOST+"query/getIssues" ;//1
    public static final String GET_REFER_URL =LOCAL_HOST+"login/getReferEarn" ;//1

    public static final String FIND_CITY_URL =LOCAL_HOST+"Login/findCity" ;
    public static final String DRIVER_ID ="driverId" ;
    public static final String IS_LOGIN ="islogin" ;
    public static final String IS_DRIVING_LICENSE ="isDrivingLicense" ;
    public static final String IS_VEHICLE_INSURANCE ="" ;
    public static final String IS_VEHICLE_NUMBER ="" ;
    public static final String IS_ADHAR_CARD ="" ;
    public static final String SIGNUP_PROCESS="signupprocess" ;
    public static final String PROFILE_PATH ="Profile_img" ;
    public static final String MOBILE_NO ="mobile_no" ;
    public static final String DRIVER_NAME ="name" ;
    public static final String SESSION_ID = "session_id";
    public static final String NETPAY ="netpay" ;
    public static final String TIME ="time" ;
    public static final String DISTANCE ="distance" ;
    public static final String ISRIDE_START = "isridestarted";
    public static final String START_LAT ="startLat" ;
    public static final String START_LONG ="startLong" ;
    public static final String END_LAT ="endLat" ;
    public static final String END_LONG = "endLong";
    public static final String TOTAL_AMOUNT ="totalAmount" ;
    public static final String TOTAL_DISTANCE ="totalDistance" ;
    public static final String START_LOCATION_NAME ="startLocation" ;
    public static final String END_LOCATION_NAME="endLocation" ;
    public static final String IS_SET_LANGUAGE ="isSetLang" ;
    public static final String DAYOFWEEK ="dayOfWeek" ;
    public static final String BANK_ACCOUNT ="BankAccount" ;
    public static final String BANK_IFSC ="bankIfsc" ;
    public static final String BANK_IMAGE ="bankImage" ;
    public static final String DRIVING_LICENSE ="drivinglicence" ;
    public static final String DRIVING_LICENSE_EXPDATE ="drivinglicenceexpdate" ;

    public static final String DRIVING_LICENSE_IMAGE ="drivinglicence_img" ;
    public static final String V_INSURANCE ="vehicle_insurance" ;
    public static final String V_INSURANCE_EXPDATE ="vehicle_insurance_expdate" ;

    public static final String V_INSURANCE_IMAGE ="vehicle_insurance_img" ;
    public static final String V_PERMIT ="vehicle_permit" ;
    public static final String V_PERMIT_IMAGE ="vehicle_permit_img" ;
    public static final String AADHAR_CARD ="aadhar_card" ;
    public static final String AADHAR_CARD_EXPDATE ="aadhar_card_expdate" ;

    public static final String AADHAR_CARD_IMAGE ="aadhar_card_img" ;
    public static final String PROFILE_IMAGE ="profilepicimg" ;


    public static final String CUSTOMER_CONTACT ="customerContact" ;
    public static final String TRIP_ID ="tripId" ;
    public static final String RIDETYPE ="RideType" ;
    public static final String ACCEPT_RIDE = "AcceptRide";
    public static final String CUST_LAT ="customerLat" ;
    public static final String CUST_LONG ="customerLong" ;
    public static final String NOTIFICATION_ID ="notificationId" ;
    public static final String ACCEPTED_NOTIFICATION_ID ="acceptedNotiId" ;
    public static final int NOTIFICATION_IDD =100 ;
    public static final String CUSTOMER_CONTACT_SECOND ="customer_contact_second" ;
    public static final String START_LOCATION_NAME_SECOND ="startlocationnameSecond" ;
    public static final String END_LOCATION_NAME_SECOND ="endlocationNameSecond" ;
    public static final String TRIP_ID_SECOND ="tripIdSecond" ;
    public static final String NETPAY_SECOND ="netPaySecond" ;
    public static final String DISTANCE_SECOND ="distanceSecond" ;
    public static final String TIME_SECOND ="timeSecond" ;
    public static final String RIDETYPE_SECOND ="RideTypeSecond" ;
    public static final String ACCEPT_RIDE_SECOND ="acceptedRideSecond" ;
    public static final String SESSION_ID_SECOND = "sessionIdSecond";
    public static final String END_LAT_SECOND ="endLatSecond" ;
    public static final String END_LONG_SECOND ="endLongSecond" ;
    public static final String CUST_LAT_SECOND ="custLatSecond" ;
    public static final String CUST_LONG_SECOND ="custLongSecond" ;

    public static final String IS_TRIP_NOTIFICATION_ACTIVE ="isTripNotification_active" ;
    public static final String LAST_HISTORY_TIMESTAMP ="lastHistoryTime" ;
    public static final String DYANAMIC_DISTANCE ="dynamicDistance" ;
    public static final String TRIP_TYPE ="tripType" ;
    public static final String FIREBASELAST_TIMESTAMP ="firebaseTime" ;
    public static final String QUEUE_STATUS = "1";
    public static final String IS_PREPAID_RIDE = "IS_PREPAID_RIDE";
    public static final String QUE_BUTTON_STATUS = "QUE_BUTTON_STATUS";
    public static final String DEQUEUE_STATUS = "1";
    public static final String ON_RIDE_STATUS = "ON_RIDE_STATUS";
    public static final String LOCATIONID = "LOCATIONID";

    public static String tag_json_obj="json_obj_req";
    public static String Locale_KeyValue="local_lang";

    public static String formateDate1(String date){

        String finalString="";
        try{
            String start_dt =date;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date1 = (Date)formatter.parse(start_dt);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            finalString = newFormat.format(date1);
        }catch (Exception e){
            e.printStackTrace();
        }

        return finalString;

    }

    public static String formateDate3(String date){

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date d1=null;

        try{
            d1=timeFormat.parse(date);
            // d2=timeFormat.parse(toDate1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return timeFormat.format(d1);

    }

    public static String formateDate2(String date){
        String finalString="";
        try{
            String start_dt =date;
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date1 = (Date)formatter.parse(start_dt);
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            finalString = newFormat.format(date1);
        }catch (Exception e){
            e.printStackTrace();
        }


        return finalString;

    }

    public static String getDateTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static   boolean  isgpsAvailable(Context c){

        LocationManager manager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d("statusOfGPS: ",statusOfGPS+"");
        return statusOfGPS;
    }

    public static void hideKeyboardFrom(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static void hideKeyboardFrom1(Context context, View view) {
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static double getBatteryLevel(Context context) {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR) {
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
            return (level * 100.0) / scale;
        } else {
            return 0;
        }
    }



    public static int getLocationMode(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }


        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (TextUtils.isEmpty(locationProviders)) {
                locationMode = Settings.Secure.LOCATION_MODE_OFF;
            }
            else if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                locationMode = Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
            }
            else if (locationProviders.contains(LocationManager.GPS_PROVIDER)) {
                locationMode = Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
            }
            else if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                locationMode = Settings.Secure.LOCATION_MODE_BATTERY_SAVING;
            }

        }

        return locationMode;
    }

}


// web link
// geo-fencing
//https://www.zoftino.com/android-location-proximity-alert-using-google-maps-geofencing-example
