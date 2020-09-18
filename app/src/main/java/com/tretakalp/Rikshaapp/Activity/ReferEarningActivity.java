package com.tretakalp.Rikshaapp.Activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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

/**
 * Created by Mac on 08/10/18.
 */
public class ReferEarningActivity  extends AppCompatActivity {

    private static final String TAG ="ReferEarningActivity" ;
    ProgressBar progressBar;
    SharedPreferences pref;
    String driverId;
    TextView txtRefer,txtAmount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refer_earning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.refercount);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideKeyboardFrom(ReferEarningActivity.this);
                onBackPressed();

            }
        });

        progressBar=findViewById(R.id.progress);
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");
        txtRefer=findViewById(R.id.txtRefer);
        txtAmount=findViewById(R.id.txtAmount);

        if(DetectConnection.checkInternetConnection(this)){
            getReferData();
        }else {
            Toast.makeText(getApplicationContext(),R.string.noInternet,Toast.LENGTH_SHORT).show();
        }




    }

    private void getReferData() {

        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject1 = null;


        try {
            jsonObject1=new JSONObject();
            jsonObject1.put("driver_id",driverId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "URl" + Constant.GET_REFER_URL);
        Log.d(TAG, "json" + jsonObject1.toString());

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_REFER_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "Request_result" + result);
                            if (result.trim().equals("200")) {
                                JSONObject jsonObject=response.getJSONObject("data");

                                txtRefer.setText(getResources().getString(R.string.referalcount)+": "+jsonObject.getString("refer_count"));
                                txtAmount.setText(getResources().getString(R.string.amountearned)+": "+jsonObject.getString("earn_refer"));

                            }else {
                                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);


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
                    Toast.makeText(getApplicationContext(),R.string.networkproblem, Toast.LENGTH_SHORT).show();

                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();



            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }


}
