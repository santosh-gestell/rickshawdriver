package com.tretakalp.Rikshaapp.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.tretakalp.Rikshaapp.Adpter.CustomAdapter;
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
import java.util.regex.Pattern;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Mac on 31/08/18.
 */
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    Button btnNext;
    RelativeLayout rlNext;
    EditText edtName, edtLoc, edtMob, edtPass, edtConfrPass, edtRefCod, edtvehicle, edt_cityNumber;
    String otpNo = "", otpId = "";
    Dialog dialog1;
    SharedPreferences pref;
    ArrayList<Bean> cityList = new ArrayList<>();
    Spinner spinner_loc;
    CustomAdapter adapter;
    int spinnerIndex;
    LinearLayout lspinner;
    String cityId;
    CheckBox checkTerms;
    TextView txtTerms;
    SharedPreferences pref_firebase;
    String firebaseId;
    TelephonyManager telephonyManager;
    String imeiId = "";
    public static final int RequestPermissionCode = 1;
    public static final int REQUEST_LOCATION_CODE = 99;
    final static int REQUEST_LOCATION = 199;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 69;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String deviceName;
    String deviceBrand = "";
    String osVersion = "";
    String language = "";
    String referalDriverId = "";

    private boolean isValidVehicleNumber(String vehicleNumber) {
        if(!Pattern.matches("^[A-Z]{2}[-]{0,1}[0-9]{1,4}$", vehicleNumber)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.signUp);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        edtName = findViewById(R.id.edt_firstname);
        edtMob = findViewById(R.id.edt_mob);
        // edtLoc=findViewById(R.id.edt_loc);
        spinner_loc = findViewById(R.id.spinner_loc);
        edtPass = findViewById(R.id.edt_password);
        edtConfrPass = findViewById(R.id.edt_confirmedpassword);
        edtRefCod = findViewById(R.id.edt_referalCode);
        edtvehicle = findViewById(R.id.edt_vehicleNumber);
        edt_cityNumber = findViewById(R.id.edt_cityNumber);
        lspinner = findViewById(R.id.lSpinner);
        checkTerms = findViewById(R.id.checkTerms);
        txtTerms = findViewById(R.id.txtTerms);
        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LegalActivity.class));
            }
        });



        //btnNext=findViewById(R.id.btnNext);
        rlNext = findViewById(R.id.rlNext);

        if (!checkPermission()) {
            requestPermission();
        } else {

            if (DetectConnection.checkInternetConnection(SignUpActivity.this)) {
                getCityList();
            } else {

                Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_LONG).show();
            }

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


        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtName.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtMob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtMob.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtvehicle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtvehicle.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtConfrPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtConfrPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                proceedAfterPermission();

                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));

                if (edtName.getText().toString().equals("") || edtMob.getText().toString().equals("") || edtvehicle.getText().toString().equals("")
                        || edtPass.getText().toString().equals("") || edtConfrPass.getText().toString().equals("") || spinnerIndex == 0
                ) {

                    if (edtName.getText().toString().equals("")) {
                        edtName.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                    } else {
                        edtName.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                    }

                    if (edtMob.getText().toString().equals("")) {
                        edtMob.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                    } else {
                        edtMob.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                    }

                    if (!edtvehicle.getText().toString().matches("^[A-Z]{2}[-]{0,1}[0-9]{1,4}$")) {
                        edtvehicle.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                    } else {
                        edtvehicle.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                    }

                    if (edtPass.getText().toString().equals("")) {
                        edtPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                    } else {
                        edtPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                    }


                    if (edtConfrPass.getText().toString().equals("")) {
                        edtConfrPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                    } else {
                        edtConfrPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                    }

                    if (spinnerIndex == 0) {
                        lspinner.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                    } else {
                        lspinner.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                    }

                } else {

                    if (edtName.getText().length() > 3) {
                        if (edtPass.getText().length() > 7) {

                            if (edtMob.getText().toString().length() > 9) {

                                if (!edtvehicle.getText().toString().matches("^[A-Z]{2}[-]{0,1}[0-9]{1,4}$")) {
                                    edtvehicle.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                                } else {
                                    if (validate()) {

                                        edtName.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                                        edtMob.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                                        edtConfrPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                                        edtPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));
                                        edtvehicle.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));

                                        if (spinnerIndex != 0) {

                                            if (checkTerms.isChecked()) {

                                                if (DetectConnection.checkInternetConnection(SignUpActivity.this)) {

                                                    getOtpVerification();

                                                } else {

                                                    Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.pleaseaccepttermsandcondition, Toast.LENGTH_LONG).show();
                                                checkTerms.requestFocus();
                                            }

                                        } else {
                                            Toast.makeText(getApplicationContext(), R.string.pleaseselectcity, Toast.LENGTH_LONG).show();
                                        }
                                        // showDialogBox();

                                    } else {

                                        // Toast.makeText(getApplicationContext(),"Please, Enter",Toast.LENGTH_LONG).show();
                                    }
                                }


                            } else {

                                Toast.makeText(getApplicationContext(), R.string.mobilenumbershouldbe, Toast.LENGTH_LONG).show();
                                edtMob.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                                edtMob.requestFocus();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.enterpassshouldbe, Toast.LENGTH_LONG).show();
                            edtPass.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.enternameshouldbe, Toast.LENGTH_LONG).show();
                        edtName.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error_rounded_edittext));
                    }
                }


            }
        });


        spinner_loc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i != 0) {
                    edt_cityNumber.setText(cityList.get(i).getRtoCode());
                    spinnerIndex = i;
                    cityId = cityList.get(i).getCityId();
                    lspinner.setBackground(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.rounded_edittext));

                } else {
                    spinnerIndex = i;
                    edt_cityNumber.setText(cityList.get(i).getRtoCode());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    private void storeRegIdInPref(String refreshedToken) {
        SharedPreferences pref = getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", refreshedToken);
        editor.commit();

        firebaseId = refreshedToken;
    }

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
        String deviceBrand = android.os.Build.MANUFACTURER;
        String osVersion = android.os.Build.VERSION.RELEASE;
        Log.d("LoginActivity","DeviceName "+deviceName+" ss "+deviceBrand+" ss "+osVersion);
    }


    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED
                && SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(SignUpActivity.this, new String[]
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setTitle("Need Permission!");
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


    private boolean validate() {
        boolean temp=true;
       // String checkemail = edtPass.getText().toString();
        String pass=edtPass.getText().toString();
        String cpass=edtConfrPass.getText().toString();

         if(!pass.equals(cpass)){
            Toast.makeText(SignUpActivity.this,R.string.passowrdisnotmatching,Toast.LENGTH_SHORT).show();
            temp=false;
        }
        return temp;
    }

    private void addCitySpinner() {
        Log.d("Customer_Detail","branchDetails_size "+cityList.size()+"");
        /** Create custom adapter object ( see below CustomAdapter.java )*/
        adapter = new CustomAdapter(SignUpActivity.this, R.layout.spinner_row,cityList,"city");
        // Set adapter to spinner
        spinner_loc.setAdapter(adapter);

        /*if(cityList.size()>1){
            spinner_loc.setEnabled(false);
            spinner_loc.setSelection(1);
        }*/


    }

    private void showDialogBox() {


        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.otp);

        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) dialog1.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.verifyotp);


        Button btn_submit = dialog1.findViewById(R.id.btn_submit);
        Button btn_cancel =  dialog1.findViewById(R.id.btn_cancel);
        final EditText edtOtp=dialog1.findViewById(R.id.edtotp);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog1.dismiss();
                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
               // sendOtpVerification();

                if (DetectConnection.checkInternetConnection(SignUpActivity.this)) {
                    if(edtOtp.getText().toString().trim()!="") {
                        if(edtOtp.length()>3) {
                            sendOtpVerification(edtOtp.getText().toString().trim());
                        }else {
                            Toast.makeText(getApplicationContext(), R.string.pleaseentervalidotp, Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.enterotpcode, Toast.LENGTH_LONG).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 dialog1.dismiss();
                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
                // sendOtpVerification();


            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog1.getWindow().setAttributes(lp);
        dialog1.setCancelable(false);
        dialog1.show();

    }

/**
 * when user sign up then first it will check mobile no is already refigester or not
 * and device id exist or not
 * vehicle id exist or not
 * referral id valid or not
 * then generate otp
 * if user send refferal id then in respone we get refferal by  id mean tht driver id
 * and resend it after verifying otp in signup function
 * */

    private void getOtpVerification() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.gettingOTP));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;


        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("mobile_no", edtMob.getText().toString().trim());
            jsonObject1.put("vehical_no",edt_cityNumber.getText().toString().trim()+" "+ edtvehicle.getText().toString().trim());
            jsonObject1.put("deviceId",imeiId+"");
            jsonObject1.put("referral_code",edtRefCod.getText().toString()+"");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // jsonObject1.put("filename",);
        Log.d(TAG, "Login_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.GET_OTP_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_OTP_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString() + "");
                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Submit_result" + result);
                            if (result.trim().equals("200")) {

                                //showDialogBox();
                              JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                                    otpNo=jsonObject.getString("otp_no");
                                    otpId=jsonObject.getString("otp_id");
                                   referalDriverId=jsonObject.getString("refer_by");
                                }

                               showDialogBox();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                           /* if (responseContents != null) {
                                networkResponse = new NetworkResponse(statusCode, responseContents,
                                        responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                                if (statusCode == HttpStatus.SC_UNAUTHORIZED ||
                                        statusCode == HttpStatus.SC_FORBIDDEN) {
                                    attemptRetryOnException("auth",
                                            request, new AuthFailureError(networkResponse));
                                } else {
                                    // TODO: Only throw ServerError for 5xx status codes.
                                    throw new ServerError(networkResponse);
                                }
                            } else {
                                throw new NetworkError(networkResponse);
                            }*/
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SignUpActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }

               // Toast.makeText(SignUpActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    /**
     * send otp number which we get by sms it will check it is valid or not
     * */

    private void sendOtpVerification(String otpNo) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.verifingotp_load));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;


        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("otp_id", otpId);
            jsonObject1.put("otp_no",otpNo);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // jsonObject1.put("filename",);
        Log.d(TAG, "SEND_OTP_VERIFIED_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.SEND_OTP_VERIFIED_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.SEND_OTP_VERIFIED_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "VeriFicationOTP_Submit_result" + result);
                            if (result.trim().equals("200")) {
                                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
                                //finish();
                               // showDialogBox();
                                if (DetectConnection.checkInternetConnection(SignUpActivity.this)) {
                                    signUpUrl();
                                } else {

                                    Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_LONG).show();
                                }


                                dialog1.dismiss();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                //Toast.makeText(SignUpActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();

                NetworkResponse response = error.networkResponse;

                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SignUpActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }


        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

/**
 * after verifyning mob number and otp.. now register new driver with this fucn
 * **/
    private void signUpUrl() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.signingUp));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;
      //  spinnerIndex-=1;

        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("name", edtName.getText().toString().trim());
            jsonObject1.put("mobile_no",edtMob.getText().toString().trim());
            jsonObject1.put("city_id",cityId+"");
            jsonObject1.put("password",edtPass.getText().toString().trim());
            jsonObject1.put("vehical_no",edt_cityNumber.getText().toString().trim()+" "+edtvehicle.getText().toString().trim());
            jsonObject1.put("rick_no",edtvehicle.getText().toString().trim());
            jsonObject1.put("referral_code",edtRefCod.getText().toString().trim());
            jsonObject1.put("deviceId",imeiId);
            jsonObject1.put("firebaseId",firebaseId);
            //jsonObject1.put("deviceInfo",deviceName);
            jsonObject1.put("deviceName", deviceName);
            jsonObject1.put("deviceCompany", deviceBrand);
            jsonObject1.put("deviceOs", osVersion);
            jsonObject1.put("lang",language);
            jsonObject1.put("refer_by",referalDriverId+"");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // jsonObject1.put("filename",);
        Log.d(TAG, "SignUp_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.SIGN_UP_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.SIGN_UP_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signup_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                String  driverId="";
                               // JSONArray jsonArray= response.getJSONArray("data");
                                JSONObject job=response.getJSONObject("data");
                                //for(int i=0;i<jsonArray.length();i++){

                                    //JSONObject jsonObject=jsonArray.getJSONObject(i);
                                   driverId= job.getString("driver_id");


                                //}
                                SharedPreferences.Editor edit=pref.edit();
                                edit.putString(Constant.DRIVER_ID,driverId);
                                edit.putInt(Constant.SIGNUP_PROCESS,1);
                                //edit.putString(Constant.PROFILE_PATH, edtMob.getText().toString().trim());
                                edit.putString(Constant.DRIVER_NAME, edtName.getText().toString().trim());
                                edit.putString(Constant.MOBILE_NO, edtMob.getText().toString().trim());
                                edit.commit();

                               // startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));

                                Intent i=new Intent(SignUpActivity.this, DocumentActivity.class);
                                i.putExtra("key","frmSignup");
                                startActivity(i);
                                finish();
                                // showDialogBox();
                                dialog1.dismiss();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SignUpActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }
               // Toast.makeText(SignUpActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }


    /**this api will call first time for getting city to add in spinner*/
    private void getCityList() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;



        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("ss", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "city_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.FIND_CITY_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.FIND_CITY_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                           // Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "getCityList_Submit_result" + result);
                            if (result.trim().equals("200")) {
                                Bean b1 = new Bean();
                                b1.setCityName("Select City");
                                b1.setCityId("");
                                b1.setRtoCode("");
                                cityList.add(b1);

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int j=0;j<jsonArray.length();j++){

                                   // JSONArray jsonArray1=jsonArray.getJSONArray(j);
                                    /*for(int i=0;i<jsonArray1.length();i++) {

                                        JSONObject jsonObject=jsonArray1.getJSONObject(i);*/
                                       JSONObject jsonObject=jsonArray.getJSONObject(j);
                                        Bean b = new Bean();
                                        b.setCityName(jsonObject.getString("city_name"));
                                        b.setCityId(jsonObject.getString("city_id"));
                                        b.setRtoCode(jsonObject.getString("rto_code"));
                                        cityList.add(b);

                                   // }


                                }

                                addCitySpinner();

                                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
                                //finish();
                                // showDialogBox();
                               // dialog1.dismiss();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                //Toast.makeText(SignUpActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();

                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SignUpActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();



            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
         language = pref.getString(Constant.Locale_KeyValue, "");
        changeLocale(language);
    }

    //change local language
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

}
