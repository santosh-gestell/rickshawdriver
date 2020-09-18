package com.tretakalp.Rikshaapp.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Mac on 31/08/18.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    TextView txtSignUp;
    Button btnLogin;
    EditText edtUserNm, edtPassword;


    public static final int RequestPermissionCode = 1;
    public static final int REQUEST_LOCATION_CODE = 99;
    final static int REQUEST_LOCATION = 199;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 69;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    GoogleApiClient mGoogleApiClient1;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient googleApiClient = null;
    PendingResult<LocationSettingsResult> result = null;
    TelephonyManager telephonyManager;
    String imeiId = "";
    SharedPreferences pref;
    SharedPreferences pref_firebase;
    String firebaseId;
    String deviceName;
    String deviceBrand = "";
    String osVersion = "";
    String language = "";
    LinearLayout linear_forgetPass;
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        txtSignUp = findViewById(R.id.linkSignup);
        btnLogin = findViewById(R.id.btnLogin);
        edtUserNm = findViewById(R.id.edtUser);
        edtPassword = findViewById(R.id.edtPass);
        linear_forgetPass = findViewById(R.id.linear_forgetPassword);
        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        txtSignUp.setText(" " + getResources().getString(R.string.signUp));

        //  NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));

        if (checkPlayServices()) {
            Log.i("playservice", checkPlayServices() + "");
            // Building the GoogleApi client
            buildGoogleApiClient();
        }


        if (!checkPermission()) {
            requestPermission();
        }

        pref_firebase = getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
        firebaseId = pref_firebase.getString("regId", "");
        Log.d(TAG, "firebaseId: " + firebaseId);

        if (firebaseId == null) {
            try {
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                storeRegIdInPref(refreshedToken);

                Log.d("Firbase id login1", "Refreshed token: " + refreshedToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * if user came here from signup screen and cancel the button..
                 // then it will reset .. so new sign up screen will be open on click event of signup btn instead of
                 //redirecting to document Screen
                 // or if user came here from Login screen and cancel the button..
                 //it will just finish without reseting the signup process..
                 //so who left signup process without filling document it will redirect on click signup btn to document
                 //signup process =0 means reset/complete.. 1 means half complete ,document not uploaded */

                Log.d(TAG, "SIGNUP_PROCESS " + pref.getInt(Constant.SIGNUP_PROCESS, 0));
                if (pref.getInt(Constant.SIGNUP_PROCESS, 0) == 0) {
                    Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(i);
                } else if (pref.getInt(Constant.SIGNUP_PROCESS, 0) == 1) {

                    Intent i = new Intent(LoginActivity.this, DocumentActivity.class);
                    i.putExtra("key", "frmSignup");
                    startActivity(i);

                }
                if (pref.getInt(Constant.SIGNUP_PROCESS, 0) == 2) {

                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                }

            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constant.isgpsAvailable(LoginActivity.this)) {

                    loginDriver();
                } else {
                    checkLocationSetting();
                }

            }
        });

        linear_forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

    }

    //check validation and call getLogin() to call login API
    private void loginDriver() {

        proceedAfterPermission();

        if (edtUserNm.getText().toString().equals("") || edtPassword.getText().toString().equals("")) {

            if (edtUserNm.getText().toString().equals("")) {
                edtUserNm.setError("Please, Enter Mobile Number");
            } else {
                // ed
            }

            if (edtUserNm.getText().toString().equals("")) {
                edtPassword.setError("Please, Enter Password");
            }

        } else {
            if (DetectConnection.checkInternetConnection(LoginActivity.this)) {
                getLogin();
            } else {

                Toast.makeText(LoginActivity.this, R.string.noInternet, Toast.LENGTH_LONG).show();
            }
        }

    }


    private void storeRegIdInPref(String refreshedToken) {
        SharedPreferences pref = getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", refreshedToken);
        editor.commit();

        firebaseId = refreshedToken;
    }


    //Call Login APi if userId valid then loggein
    private void getLogin() {

        final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loging_msg));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("username", edtUserNm.getText().toString().trim());
            jsonObject1.put("password", edtPassword.getText().toString().trim());
            jsonObject1.put("device_id", imeiId);
            jsonObject1.put("firebaseId", firebaseId);
            jsonObject1.put("deviceName", deviceName);
            jsonObject1.put("deviceCompany", deviceBrand);
            jsonObject1.put("deviceOs", osVersion);
            jsonObject1.put("lang", language);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "login_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.LOGIN_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.LOGIN_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "getCityList_Submit_result" + result);
                            if (result.trim().equals("200")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    if (!jobj.getString("status").equals("3")) {


                                        if (jobj.getString("status").equals("1") || jobj.getString("status").equals("2")) {

                                            SharedPreferences.Editor edit = pref.edit();
                                            edit.putBoolean(Constant.IS_LOGIN, true);
                                            edit.putString(Constant.DRIVER_ID, jobj.getString("driver_id"));
                                            edit.putString(Constant.PROFILE_PATH, jobj.getString("profile_img"));
                                            edit.putString(Constant.DRIVER_NAME, jobj.getString("name"));
                                            edit.putString(Constant.MOBILE_NO, jobj.getString("mobile_no"));
                                            edit.commit();


                                            //if status is 1  means user uploaded documents and 2 means only dlicense uploaded
                                            startActivity(new Intent(LoginActivity.this, MapActivity.class));
                                            finish();

                                        } else if (jobj.getString("status").equals("3")) {
                                            //if status is 3 means user need to upload document within 8 days
                                            startActivity(new Intent(LoginActivity.this, DocumentActivity.class));
                                            finish();
                                        } else if (jobj.getString("status").equals("0")) {
                                            // Toast.makeText(LoginActivity.this,"")
                                        }

                                    } else {
                                        SharedPreferences.Editor edit = pref.edit();
                                        // edit.putBoolean(Constant.IS_LOGIN, true);
                                        edit.putString(Constant.DRIVER_ID, jobj.getString("driver_id"));
                                        edit.putString(Constant.PROFILE_PATH, jobj.getString("profile_img"));
                                        edit.putString(Constant.DRIVER_NAME, jobj.getString("name"));
                                        edit.putString(Constant.MOBILE_NO, jobj.getString("mobile_no"));
                                        edit.commit();

                                        Intent ii = new Intent(LoginActivity.this, DocumentActivity.class);
                                        ii.putExtra("key", "frmLogin");
                                        startActivity(ii);
                                        finish();
                                    }

                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                //Toast.makeText(SignUpActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();

                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>", "networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        //Log.d("response-----",""+res);
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d("Error----->>>>>", res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                } else {
                    Log.d("Error----->>>>>", "elsePart");
                    Toast.makeText(LoginActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    //check permission before user login
    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        // int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        //int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED
                && SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    //request permission before login
    private void requestPermission() {

        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {

                        WRITE_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION,
                        READ_PHONE_STATE
                        //CAMERA

                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean ExternalPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean GPSPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean READPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    // boolean ExternalPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    // boolean ReadPhoneStatePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (GPSPermission && ExternalPermission && READPermission) {


                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Need Storage Permission");
                        builder.setMessage("This app need Storage permission.");
                        builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();

                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Storage", Toast.LENGTH_LONG).show();

                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();

                    }
                }

                break;
        }
    }


    //show dilog box for turn on GPS if GPS is off
    private void checkLocationSetting() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).
                        addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        /*if( mGoogleApiClient!=null){
            buildGoogleApiClient();
        }*/
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        // locationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {

                        loginDriver();

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(LoginActivity.this, "You can not login without gps", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    // to get details of device info
    private void proceedAfterPermission() {

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        imeiId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //imeiId = telephonyManager.getDeviceId();
        deviceName = android.os.Build.MODEL;
        deviceBrand = android.os.Build.MANUFACTURER;
         osVersion = android.os.Build.VERSION.RELEASE;
        Log.d("LoginActivity","DeviceName "+deviceName+" ss "+deviceBrand+" ss "+osVersion);

    }

    //initialize googleApiclient
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient1 = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //check google play service is exist
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "GoogleApiClient connected!");
        createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient1);
        Log.i(TAG, " Location: " + mLastLocation);
    }

    protected void createLocationRequest() {

        locationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if(mGoogleApiClient1!=null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient1, mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    Log.i("onLocationResult", locationResult + "");

                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    Log.i("onLocationAvailability", "onLocationAvailability: isLocationAvailable =  " + locationAvailability.isLocationAvailable());
                }
            }, null);
        }

    }

    //initilize location request
    private void locationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient1.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    protected void onStart() {

        if (mGoogleApiClient1 != null) {
            mGoogleApiClient1.connect();
            Log.i(TAG, "mGoogleApiClient.connect()");
        }

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadLocale();

        //if user is already log in then show mapActivity.Class
        boolean islogin=  pref.getBoolean(Constant.IS_LOGIN,false);
        if(islogin){
            startActivity(new Intent(LoginActivity.this,MapActivity.class));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"state "+ "Login onStop");

        if (mGoogleApiClient1 != null) {
            mGoogleApiClient1.disconnect();
            Log.i(TAG, "mGoogleApiClient.connect()");
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
         language = pref.getString(Constant.Locale_KeyValue, "");
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

    public static PendingIntent getDismissIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }
}
