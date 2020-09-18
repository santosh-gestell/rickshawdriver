package com.tretakalp.Rikshaapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.tretakalp.Rikshaapp.Activity.MapActivity;
import com.tretakalp.Rikshaapp.Adpter.NotificationAdapter;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Model.Bean;
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
 * Created by Mac on 08/10/18.
 */
public class IncentiveAndNotification extends Fragment implements NotificationAdapter.ItemClickListener{

    private static final String TAG ="IncentiveAndNotification" ;
    View v;
    Toolbar toolbar;
    RecyclerView recyclerView;
    NotificationAdapter adapter;
    ArrayList<Bean> notificationList=new ArrayList<>();
    NotificationAdapter.ItemClickListener itemClickListener;
    DatabaseHelper db;
    ProgressBar progressbar;
    TextView txtNoData;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.incentive_noti, container, false);
        db=DatabaseHelper.getInstance(getActivity());
        progressbar=v.findViewById(R.id.progress);
        txtNoData=v.findViewById(R.id.txtmsg);

       if(DetectConnection.checkInternetConnection(getActivity())){

           getIncentiveData();

       }else {

           notificationList = db.getIncenetiveNotiData();
           if(notificationList.isEmpty()){
               txtNoData.setVisibility(View.VISIBLE);
           }else {
               txtNoData.setVisibility(View.GONE);
               setRecyclerView();
               ((MapActivity) getActivity()).showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
           }
       }
        /*for(int i=0;i<6;i++) {
           Bean b = new Bean();
           b.setNotificationTitle("Title");
           b.setMessage("Hello ABC, You have received this message as you have registerd successfully");
           notificationList.add(b);
            adapter.notifyDataSetChanged();
       }*/


       /* Bean b=new Bean();
        b.setNotificationTitle("Title");
        b.setMessage("Hello ABC, You have received this message as you have registerd successfully");
        notificationList.add(b);*/


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_incentiveAndNoti));
        ((MapActivity)getActivity()).loadLocale();
    }

    private void setRecyclerView() {

        // set up the RecyclerView
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        //int numberOfColumns = 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(),notificationList,itemClickListener);
        //adapter.setClickListener(getContext());
        recyclerView.setAdapter(adapter);

    }




    private void getIncentiveData() {



        progressbar.setVisibility(View.VISIBLE);

        JSONObject jsonObject1 = null;


        // jsonObject1.put("filename",);

        Log.d(TAG, "URl" + Constant.GET_INCENTIVE_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_INCENTIVE_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        progressbar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "Request_result" + result);
                            if (result.trim().equals("200")) {
                                txtNoData.setVisibility(View.GONE);

                                notificationList.clear();
                                JSONObject jsonObject=response.getJSONObject("data");

                               // for(int i=0;i<jsonArray.length();i++){

                                  //  JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    Bean b=new Bean();
                                    b.setNotificationTitle(jsonObject.getString("title"));
                                    b.setMessage(jsonObject.getString("message"));
                                    b.setNotifyId(jsonObject.getString("notification_id"));
                                    b.setDate_To(jsonObject.getString("date_to"));
                                    b.setDate_From(jsonObject.getString("date_from"));
                                    b.setImage(jsonObject.getString("image"));
                                    db.insertIncentiveNotiData(b);
                                    notificationList.add(b);

                                //}

                                setRecyclerView();



                            }else {
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressbar.setVisibility(View.GONE);


                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        txtNoData.setVisibility(View.VISIBLE);
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(),R.string.networkproblem, Toast.LENGTH_SHORT).show();

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



    @Override
    public void onItemClick1(View view, int position) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }
}