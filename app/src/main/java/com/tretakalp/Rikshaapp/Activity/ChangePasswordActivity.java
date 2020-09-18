package com.tretakalp.Rikshaapp.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by Mac on 11/04/18.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG ="ChangePasswordActivity" ;
    EditText edt_old,edt_new,edt_confnew;
    Button submit,btn_cancel;
    TextInputLayout txt_input_old,txt_input_new,txt_input_confrm;
    String userId,oldPassword;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        toolbarSetup();
        initialiseView();
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.DRIVER_ID, "");
        //oldPassword = sharedPreferences.getString(Constant.USER_PASS, "");
        //Log.d("oldPassword",oldPassword);


        edt_old.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s) {
                String strPass1 = edt_old.getText().toString();
                txt_input_old.setError(null);
                /*if (strPass1.equals(oldPassword)) {
                    txt_input_old.setError(null);
                    txt_input_old.setErrorEnabled(false);
                } else {
                    txt_input_old.setError("Please, Enter Correct Old Password");
                }*/

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        edt_new.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s) {

                txt_input_new.setError(null);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        edt_old.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //ADD HERE ABOUT CUT COPY PASTE
                // TODO Auto-generated method stub
                return true;
            }
        });

        edt_confnew.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s) {
                String strPass1 = edt_new.getText().toString();
                String strPass2 = edt_confnew.getText().toString();

                if (strPass1.equals(strPass2)) {
                    txt_input_confrm.setError(null);
                    txt_input_confrm.setErrorEnabled(false);
                } else {

                    txt_input_confrm.setError(getResources().getString(R.string.passowrdisnotmatching));

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        edt_confnew.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //ADD HERE ABOUT CUT COPY PASTE
                // TODO Auto-generated method stub
                return true;
            }
        });

        edt_new.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //ADD HERE ABOUT CUT COPY PASTE
                // TODO Auto-generated method stub
                return true;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    changePassword();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void toolbarSetup() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // toolbar.setTitleTextAppearance(this, R.style.RobotoBoldTextAppearance);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //v.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)‌​;
                onBackPressed();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitle(R.string.changepassword_title);

    }


    private void initialiseView() {

        edt_old= (EditText) findViewById(R.id.editText1) ;
        edt_new= (EditText) findViewById(R.id.editText2) ;
        edt_confnew= (EditText)findViewById(R.id.editText3) ;

        submit=(Button) findViewById(R.id.btn_submit_pass);
        btn_cancel=(Button) findViewById(R.id.btn_cancel);

        txt_input_old= (TextInputLayout) findViewById(R.id.input_old_password);
        txt_input_new= (TextInputLayout) findViewById(R.id.input_new_password);
        txt_input_confrm= (TextInputLayout) findViewById(R.id.input_conf_password);




    }

    private void changePassword() {

        final String oldpassword= edt_old.getText().toString().trim();
        final String newpassword= edt_new.getText().toString().trim();
        final String confpassword= edt_confnew.getText().toString().trim();

        if(oldpassword.equals("")&&newpassword.equals("")&&confpassword.equals(""))
        {
            Toast.makeText(ChangePasswordActivity.this,R.string.pleasefillfield, Toast.LENGTH_SHORT).show();
            txt_input_old.setError(getResources().getString(R.string.pleaseenteroldpass));
            txt_input_new.setError(getResources().getString(R.string.pleaseenternewpass));
            txt_input_confrm.setError(getResources().getString(R.string.pleaseenterrenter));

        }else if(oldpassword.equals("")||newpassword.equals("")||confpassword.equals("")){

            if(oldpassword.equals(""))
            {
                txt_input_old.setError(getResources().getString(R.string.pleaseenteroldpass));
            }

            if(newpassword.equals(""))
            {
                txt_input_new.setError(getResources().getString(R.string.pleaseenternewpass));
            }

            if(confpassword.equals(""))
            {
                txt_input_confrm.setError(getResources().getString(R.string.pleaseenterrenter));
            }


        }else {


            if (!oldpassword.equals("") && !newpassword.equals("") && !confpassword.equals("")) {

                if (edt_old.getText().length() > 7 && edt_new.getText().length() > 7) {

                if (newpassword.equals(confpassword)) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePasswordActivity.this);
                        alertDialogBuilder.setMessage(R.string.areuyousurechangepass);
                        alertDialogBuilder.setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        //Starting login activity
                                        //if (newpassword.equals(confpassword)) {
                                       // new ChangePassAsync(getActivity(), oldpassword, newpassword, user_id).execute();
                                        changePasswordVolleyTask(newpassword);

                                    }
                                });

                        alertDialogBuilder.setNegativeButton(R.string.No,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });

                        //Showing the alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();


                    } else {

                        Toast.makeText(ChangePasswordActivity.this, R.string.newpassnotmatchreenterpass, Toast.LENGTH_SHORT).show();
                        txt_input_confrm.setError(getResources().getString(R.string.passowrdisnotmatching));
                    }


                } else {
                    Toast.makeText(getApplicationContext(), R.string.enterpasswordshould, Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(ChangePasswordActivity.this, R.string.pleasefillfield, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changePasswordVolleyTask(final String newpassword) {


           final ProgressDialog pDialog = new ProgressDialog(ChangePasswordActivity.this);
           pDialog.setMessage(getResources().getString(R.string.changingpassword));
           pDialog.show();



        JSONObject jsonObject1=new JSONObject();
        try {

            jsonObject1.put("driver_id",userId);
            jsonObject1.put("curr_pass",edt_old.getText().toString().trim());
            jsonObject1.put("new_pass",newpassword);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Json "+jsonObject1.toString() + "");
        Log.d(TAG, "Url "+ Constant.CHANGE_PASS_URL+ "");

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.CHANGE_PASS_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                            if (result.trim().equals("200")) {

                                //Toast.makeText(ChangePasswordActivity.this,"Password has been changed successfully", Toast.LENGTH_SHORT).show();
                               /* SharedPreferences.Editor edit=sharedPreferences.edit();
                                edit.putString(Constant.USER_PASS,newpassword);
                                edit.commit();*/

                                edt_old.setText("");
                                edt_new.setText("");
                                edt_confnew.setText("");
                                edt_old.setError(null);

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
                    Toast.makeText(ChangePasswordActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

               // Toast.makeText(ChangePasswordActivity.this,"Network Error, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Constant.hideKeyboardFrom(ChangePasswordActivity.this);
       // overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String language = pref.getString(Constant.Locale_KeyValue, "");
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



}
