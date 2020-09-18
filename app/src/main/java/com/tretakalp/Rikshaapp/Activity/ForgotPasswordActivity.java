package com.tretakalp.Rikshaapp.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Mac on 11/04/18.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    SharedPreferences pref;
    //String userId;
    EditText edtEmail;
    Button btnSubmit;
    Button btnCancel;
    Toolbar toolbar;
    public static final int RequestPermissionCode = 1;
    public static final int REQUEST_LOCATION_CODE = 99;
    final static int REQUEST_LOCATION = 199;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 69;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    TelephonyManager telephonyManager;
    String imeiId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        // userId=pref.getString(Constant.DRIVER_ID,"");

        toolbarSetup();

        if (!checkPermission()) {
            requestPermission();
        }

        edtEmail = (EditText) findViewById(R.id.editText2);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        edtEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proceedAfterPermission();
                if (!edtEmail.getText().toString().equals("")) {
                    if (edtEmail.getText().length() > 9) {


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        alertDialogBuilder.setMessage(R.string.areuyousurechangepass);
                        alertDialogBuilder.setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        Constant.hideKeyboardFrom1(ForgotPasswordActivity.this, edtEmail);

                                        forgotPasswordVolleyTask();

                                    }
                                });

                        alertDialogBuilder.setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });

                        //Showing the alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, R.string.mobilenumbershouldbe, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, R.string.entermobileno, Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        //int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        //int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        // int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        //int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED;
               /* && SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;*/
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(ForgotPasswordActivity.this, new String[]
                {

                        /// WRITE_EXTERNAL_STORAGE,
                        ///ACCESS_FINE_LOCATION,
                        READ_PHONE_STATE
                        //CAMERA

                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    // boolean ExternalPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    //boolean GPSPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean READPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    // boolean ExternalPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    // boolean ReadPhoneStatePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (READPermission) {
                        proceedAfterPermission();

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        builder.setTitle(R.string.needphonecallpermission);
                        builder.setMessage(R.string.thisappneedphonecallpermisssion);
                        builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();

                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getBaseContext(), R.string.gotopermissionsetting, Toast.LENGTH_LONG).show();

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
        imeiId = telephonyManager.getDeviceId();
       /* deviceName = android.os.Build.MODEL;
        deviceBrand = android.os.Build.MANUFACTURER;
        osVersion = android.os.Build.VERSION.RELEASE;
        Log.d("LoginActivity","DeviceName "+deviceName+" ss "+deviceBrand+" ss "+osVersion);*/

    }


    private void forgotPasswordVolleyTask() {


        final ProgressDialog pDialog = new ProgressDialog(ForgotPasswordActivity.this);
        pDialog.setMessage(getResources().getString(R.string.changingpassword));
        pDialog.setCancelable(false);
        pDialog.show();




        JSONObject jsonObject1=new JSONObject();
        try {
            //jsonObject1.put("id",userId);
            jsonObject1.put("mobile_no",edtEmail.getText());
            jsonObject1.put("device_id",imeiId+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Json "+jsonObject1.toString() + "");
        Log.d(TAG, "Url "+ Constant.FORGOT_PASSWORD_URL+ "");

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.FORGOT_PASSWORD_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "FORGOT_PASS_URL_Submit_result" + result);
                            if (result.trim().equals("200")) {
                                edtEmail.setText("");
                                Toast.makeText(ForgotPasswordActivity.this,msg,Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                alertDialogBuilder.setMessage(R.string.newpasswordissendonyourmobileno);
                                alertDialogBuilder.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                                            }
                                        });
                                //Showing the alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                               /*TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                                Typeface face=Typeface.createFromAsset(getAssets(),"Abel-Regular.ttf");
                                textView.setTextSize(18);
                                textView.setTypeface(face);*/


                            }else  if (result.trim().equals("100")){
                                Toast.makeText(getApplicationContext(), "Mobile No doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        pDialog.dismiss();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

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
                        Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();


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
                    Toast.makeText(ForgotPasswordActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                    // ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.networkproblem));
                    finish();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

               // Toast.makeText(ForgotPasswordActivity.this,"Network Error, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void toolbarSetup() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextAppearance(this, R.style.RobotoBoldTextAppearance);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Forgot Password");
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
    }
}
