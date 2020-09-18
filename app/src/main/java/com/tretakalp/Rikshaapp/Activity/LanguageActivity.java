package com.tretakalp.Rikshaapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Mac on 05/09/18.
 */
public class LanguageActivity extends AppCompatActivity {

    Spinner spinner_lang;

    ArrayAdapter<String> dataAdapter;
    ArrayList<String> list=new ArrayList<>();
    TextView txtLang;
    SharedPreferences pref;
    Button btnSave,btnCancel;
    int selectedIndex;
    String path="";
    private static Locale myLocale;
    SharedPreferences  sharedPreferences;
    String driverId;
    String lang;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lang_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Language Settings");
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        sharedPreferences = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        driverId=sharedPreferences.getString(Constant.DRIVER_ID,"");

        Bundle b=getIntent().getExtras();
        path=b.getString("path");
        if(path.equals("0")){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        spinner_lang=findViewById(R.id.spinner_lang);
        txtLang=findViewById(R.id.txtlang);
        btnSave=findViewById(R.id.btn_save);
        btnCancel=findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        list.add("Select Language");
        list.add("English");
        //list.add("Hindi");
        list.add("Marathi");

        addLangaugeSpinner();
        selectedIndex=pref.getInt(Constant.LANGUAGE,1);

            txtLang.setText(list.get(selectedIndex));
            spinner_lang.setSelection(selectedIndex);
        loadLocale();


        spinner_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //txtLang.setText(list.get(i));
                selectedIndex=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //    if(path.equals("1")) {
                    if(DetectConnection.checkInternetConnection(LanguageActivity.this)) {

                        if (selectedIndex == 0) {

                            Toast.makeText(LanguageActivity.this, R.string.selectLang, Toast.LENGTH_SHORT).show();

                        } else {
                             if(driverId.equals("")){

                                    changeLocalLang();

                        }else {
                                 if (selectedIndex == 1) {
                                     lang="en";
                                 } else {
                                     lang="mr";
                                 }

                                 changeLanguage(lang);//synctoserver

                             }


                        }


                    }else {
                        Toast.makeText(LanguageActivity.this,R.string.noInternet,Toast.LENGTH_LONG).show();
                    }

                /*}else if(path.equals("0")) {

                    startActivity(new Intent(LanguageActivity.this,LoginActivity.class));

                }*/

            }
        });

    }

    private void changeLocalLang() {

        if (selectedIndex == 1) {
            changeLocale("en");
        } else {
            changeLocale("mr");
        }

        Toast.makeText(LanguageActivity.this, R.string.selectedLang, Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor edt = pref.edit();
        edt.putInt(Constant.LANGUAGE, selectedIndex);
        if (!pref.getBoolean(Constant.IS_SET_LANGUAGE, false)) {
            edt.putBoolean(Constant.IS_SET_LANGUAGE, true);
        }
        edt.commit();
        txtLang.setText(list.get(selectedIndex));

        if (path.equals("0")) {

            startActivity(new Intent(LanguageActivity.this, LoginActivity.class));
            finish();


        } else {
            Intent ss = new Intent(LanguageActivity.this, MapActivity.class);
            /*ss.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            ss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ss.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ss.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);*/
            startActivity(ss);
            finish();
        }

    }


    public void addLangaugeSpinner() {

        dataAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_lang.setAdapter(dataAdapter);
    }

    //Change Locale
    public void changeLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);//Set Selected Locale
        saveLocale(lang);//Save the selected locale
        Locale.setDefault(myLocale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = myLocale;//set config locale as selected locale
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());//Update the config
       // updateTexts();//Update texts according to locale
    }

    //Save locale method in preferences
    public void saveLocale(String lang) {

        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString(Constant.Locale_KeyValue, lang);
        editor.commit();
    }

    //Get locale method in preferences
    public void loadLocale() {
        String language = sharedPreferences.getString(Constant.Locale_KeyValue, "");
        changeLocale(language);
    }


    private void changeLanguage(String langauge) {

        final ProgressDialog pDialog = new ProgressDialog(LanguageActivity.this);
        pDialog.setMessage(getResources().getString(R.string.changingLang));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", pref.getString(Constant.DRIVER_ID,""));
            jsonObject1.put("lang", langauge);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d("LanguageActivity.this", "changeLanguage_URL" + jsonObject1.toString());
        Log.d("LanguageActivity.this", "changeLanguage_URl" + Constant.CHANGE_LANG_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.CHANGE_LANG_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LanguageActivity", response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d("LanguageActivity", "changeLanguage_Submit_result" + result);
                            if (result.trim().equals("200")) {
                                changeLocalLang();

                            }else {
                                Toast.makeText(LanguageActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                        // Toast.makeText(MapActivity.this,msg,Toast.LENGTH_SHORT).show();

                        JSONObject jsonObject= obj.getJSONObject("data");

                        String status = jsonObject.getString("status");
                        if(status.equals("0")){
                            Toast.makeText(LanguageActivity.this,msg,Toast.LENGTH_LONG).show();

                        }

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
                    Toast.makeText(LanguageActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }

                VolleyLog.d("LanguageActivity.this", "Error: " + error.getMessage());
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


}
