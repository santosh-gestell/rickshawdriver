package com.tretakalp.Rikshaapp.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tretakalp.Rikshaapp.Adpter.RecyclerItemClickListener;
import com.tretakalp.Rikshaapp.Adpter.ReportIssueAdapter;
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

/**
 * Created by Mac on 04/09/18.
 */
public class IssueActivity extends AppCompatActivity implements ReportIssueAdapter.ItemClickListener{

    private static final String TAG = "TopicOneActivity" ;
    ProgressBar progressBar;
    String language="";
    ArrayList<Bean> issueList=new ArrayList<>();
    RecyclerView recyclerView;
    ReportIssueAdapter adapter;
    ReportIssueAdapter.ItemClickListener itemClickListener;
    LinearLayout container_toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incentive_noti);
        container_toolbar=findViewById(R.id.container_toolbar);
        container_toolbar.setVisibility(View.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.Issues);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


            loadLocale();


        progressBar=findViewById(R.id.progress);

        if(DetectConnection.checkInternetConnection(this)) {
            getIssueData();

        }else {
            Toast.makeText(this,R.string.noInternet,Toast.LENGTH_SHORT).show();
        }

    }


    private void getIssueData() {


        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();

            //jsonObject1.put("driver_id",driverId);
            jsonObject1.put("language",language);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "json" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.GET_ISSUE_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_ISSUE_URL, jsonObject1,
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
                                issueList.clear();

                                JSONArray jsonArray=response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);

                                        Bean b = new Bean();
                                        b.setQueryId(jobj.getString("id"));
                                    Log.d(TAG, "language->>>" + language);
                                    if(language.trim().equals("en")) {

                                        b.setQuery(jobj.getString("issues_eng"));

                                    }else {
                                        b.setQuery(jobj.getString("issues_marathi"));
                                    }
                                        issueList.add(b);


                                    //chatArrayAdapter.add(b);
                                    //chatText.setText("");
                                    //side = !side;
                                }
                                setRecyclerView();



                            }else {
                                Toast.makeText(IssueActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(IssueActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(IssueActivity.this,R.string.networkproblem, Toast.LENGTH_SHORT).show();

                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
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


   /* private void setRecyclerView() {

        // set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //int numberOfColumns = 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new NotificationAdapter(getApplicationContext(),issueList,itemClickListener);
        recyclerView.setAdapter(adapter);

    }*/


    private void setRecyclerView() {

        // set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //int numberOfColumns = 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ReportIssueAdapter(getApplicationContext(),issueList,itemClickListener);
        //adapter.setClickListener(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {



            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));



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

    public void changeLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale  =myLocale = new Locale(lang);//Set Selected Locale
        //  saveLocale(lang);//Save the selected locale
        Locale.setDefault(myLocale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = myLocale;//set config locale as selected locale
       getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }


    @Override
    public void onItemClick1(View view, int position) {

    }
}
