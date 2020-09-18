package com.tretakalp.Rikshaapp.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.android.material.snackbar.Snackbar;
import com.tretakalp.Rikshaapp.Adpter.ChatArrayAdapterNew;
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

/**
 * Created by Mac on 12/10/18.
 */
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private ChatArrayAdapterNew chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private ImageView buttonSend;
    private boolean side = false;
    ProgressBar progressBar;
    SharedPreferences pref;
    String driverId;
    LinearLayout linearLayout,linearChat;
    ArrayList<Bean> queryList=new ArrayList<>();
    String queryId,status,viewId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.queryNo);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        Bundle b=getIntent().getExtras();
        queryId= b.getString("queryId");
        viewId= b.getString("viewId");
        status= b.getString("status");
        toolbar.setTitle(getResources().getString(R.string.queryNo)+""+viewId);

        linearLayout=findViewById(R.id.linear);
        linearChat=findViewById(R.id.linearChat);
        progressBar=findViewById(R.id.progress);
        buttonSend = findViewById(R.id.imgSend);
        listView = (ListView) findViewById(R.id.messages_view);
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");

        if(status.trim().equals("0")){
            linearChat.setVisibility(View.GONE);
        }else{
            linearChat.setVisibility(View.VISIBLE);
        }





        setAdpterView();

        if(DetectConnection.checkInternetConnection(ChatActivity.this)) {
            getQueryData();
        }else {

            showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
        }

        chatText = (EditText) findViewById(R.id.editText);

        /*chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });*/


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!chatText.getText().toString().trim().equals("")){
                    Constant.hideKeyboardFrom1(ChatActivity.this,chatText);
                    sendChatMessage(chatText.getText().toString());
                }else {
                    Toast.makeText(ChatActivity.this,R.string.pleaseenterquery,Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void sendChatMessage(String s) {
        if(DetectConnection.checkInternetConnection(ChatActivity.this)) {

            sendReplyData();

        }else {

            showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
        }


    }

    private void setAdpterView() {

        chatArrayAdapter = new ChatArrayAdapterNew(getApplicationContext(), R.layout.right_chat_row,queryList);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);
        listView.setStackFromBottom(true);
        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

    }

    /*private boolean sendChatMessage() {


        chatArrayAdapter.add(new Bean(side, chatText.getText().toString()));
        chatText.setText("");
        side = !side;
        return true;
    }*/

    public void showSnackBarForNoInternet(String message) {

        Snackbar snackbar = Snackbar
                .make(linearLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(DetectConnection.checkInternetConnection(ChatActivity.this)) {



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

    private void getQueryData() {



        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();

            jsonObject1.put("driver_id",driverId);
            jsonObject1.put("query_id",queryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Json" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.GET_QUERIES_REPLY_QUERY_URL);


        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_QUERIES_REPLY_QUERY_URL, jsonObject1,
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
                                    if(!jobj.getString("answers").equals("null")) {
                                        Log.d(TAG, "answers->>>" + jobj.getString("answers"));
                                        Bean b = new Bean();
                                        b.setQueryId(jobj.getString("query_id"));
                                        b.setAnswer(jobj.getString("answers"));
                                        b.setUserType(jobj.getString("user_type"));
                                        queryList.add(b);

                                    }else {
                                        break;
                                    }
                                    //chatArrayAdapter.add(b);
                                    //chatText.setText("");
                                    //side = !side;
                                }
                                setAdpterView();



                            }else {
                                Toast.makeText(ChatActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ChatActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChatActivity.this,R.string.networkproblem, Toast.LENGTH_SHORT).show();

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





    private void sendReplyData() {

        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject1 = null;


        try {
            jsonObject1=new JSONObject();
            jsonObject1.put("driver_id",driverId);
            jsonObject1.put("query",chatText.getText().toString().trim());
            jsonObject1.put("query_id",queryId);
            jsonObject1.put("user_type","driver");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Json" + jsonObject1.toString());
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

                                chatText.setText("");

                                getQueryData();



                            }else {
                                Toast.makeText(ChatActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ChatActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChatActivity.this,R.string.networkproblem, Toast.LENGTH_SHORT).show();

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