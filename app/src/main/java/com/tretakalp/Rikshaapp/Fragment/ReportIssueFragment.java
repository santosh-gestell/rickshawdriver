package com.tretakalp.Rikshaapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.snackbar.Snackbar;
import com.tretakalp.Rikshaapp.Activity.ChatActivity;
import com.tretakalp.Rikshaapp.Activity.MapActivity;
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
 * Created by Mac on 07/09/18.
 */
public class ReportIssueFragment extends Fragment implements ReportIssueAdapter.ItemClickListener{

    private static final String TAG ="ReportIssueFragment" ;
    View v;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ReportIssueAdapter adapter;
    ArrayList<Bean> queryList=new ArrayList<>();
    Button btnCancel,btnSave;
    EditText edit_query;
    ReportIssueAdapter.ItemClickListener itemClickListener;
    ProgressBar progressBar;
    String driverId;
    SharedPreferences pref;
    LinearLayout linear;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.report_issue, container, false);
        btnSave=v.findViewById(R.id.btnSave);
        btnCancel=v.findViewById(R.id.btnCancel);
        edit_query=v.findViewById(R.id.edt_query);
        progressBar=v.findViewById(R.id.progress);
        linear=v.findViewById(R.id.linear);

        pref=getContext().getSharedPreferences(Constant.SHARED_PREF_NAME,Context.MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");

        if(DetectConnection.checkInternetConnection(getContext())) {
            getHeaderQueryData();
        }else {

            showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
        }

        //setRecyclerView();
        itemClickListener= (ReportIssueAdapter.ItemClickListener) getActivity();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edit_query.getText().toString().trim().equals("")){
                    /*Bean b=new Bean();
                    b.setQuery(edit_query.getText().toString());
                    queryList.add(b);

                    adapter.notifyDataSetChanged();*/

                    if(DetectConnection.checkInternetConnection(getContext())) {
                        Constant.hideKeyboardFrom1(getActivity(),edit_query);
                        sendHeaderQueryData();
                    }else {

                        showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
                    }

                }else {
                    Toast.makeText(getContext(),R.string.pleaseenterquery,Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit_query.setText("");

            }
        });

        return v;


    }

    public void showSnackBarForNoInternet(String message) {

        Snackbar snackbar = Snackbar
                .make(linear, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(DetectConnection.checkInternetConnection(getContext())) {

                            getHeaderQueryData();

                        }else {

                            showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
                        }

                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_report));
        ((MapActivity)getActivity()).loadLocale();
    }

    private void setRecyclerView() {

        // set up the RecyclerView
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        //int numberOfColumns = 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportIssueAdapter(getContext(),queryList,itemClickListener);
        //adapter.setClickListener(getContext());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

               if(DetectConnection.checkInternetConnection(getContext())) {
                   Intent i = new Intent(getActivity(), ChatActivity.class);
                   i.putExtra("queryId", queryList.get(position).getQueryId());
                   i.putExtra("viewId", queryList.get(position).getViewId());
                   i.putExtra("status", queryList.get(position).getStatus());
                   startActivity(i);
               }else {
                   showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
               }


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));



    }




    @Override
    public void onItemClick1(View view, int position) {

         Intent i=new Intent(getActivity(), ChatActivity.class);
        i.putExtra("queryId",queryList.get(position).getQueryId());
        startActivity(i);

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



/** Get main chat menu with queryId **/
    private void getHeaderQueryData() {



        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();

            jsonObject1.put("driver_id",driverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "params: " + jsonObject1);
        Log.d(TAG, "URl: " + Constant.GET_HEADER_QUERY_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_HEADER_QUERY_URL, jsonObject1,
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
                                queryList.clear();

                                JSONArray jsonArray=response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);

                                    Bean b=new Bean();
                                    b.setQueryId(jobj.getString("query_id"));
                                    b.setQuery(jobj.getString("query"));//basic id
                                    b.setViewId(jobj.getString("view_id"));//to show QueryId
                                    b.setStatus(jobj.getString("status"));//if status is 0 then user cant reply to admin
                                    queryList.add(b);

                                }
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
                7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        /**time = time + (time * Back Off Multiplier);
         *time = 7000 + 7000 = 14000ms
         *Socket Timeout = time;
         * Request dispatched with Socket Timeout of 9 Secs
         * **/

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }



    private void sendHeaderQueryData() {

        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject1 = null;


        try {
            jsonObject1=new JSONObject();
            jsonObject1.put("driver_id",driverId);
            jsonObject1.put("query",edit_query.getText().toString().trim());
            jsonObject1.put("query_id","");
            jsonObject1.put("user_type","driver");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "params: " + jsonObject1);
        Log.d(TAG, "URl: " + Constant.SEND_HEADER_QUERY_URL);
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.SEND_HEADER_QUERY_URL, jsonObject1,
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


                                edit_query.setText("");
                                getHeaderQueryData();



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


}
