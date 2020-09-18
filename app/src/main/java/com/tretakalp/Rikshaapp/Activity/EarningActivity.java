package com.tretakalp.Rikshaapp.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mac on 04/09/18.
 */
public class EarningActivity extends AppCompatActivity {
    private static final String TAG ="EarningFragment" ;
    ArrayList<String> listLabel;
   // View v;
    Spinner spinner_period;
    private int mYear, mMonth, mDay, mHour, mMinute,mDayFr,mDayTo;
    //Button btnfrm,btnTo;
    EditText btnfrm,btnTo;
    LinearLayout linearDetails;
    Button btnGetDetails,btnAmount;

    int mMonthTo1,mYearTo1, mDayTo1;
    SharedPreferences pref;
    TextView txtOnlineEarning,txtCashEarning,txtFees,txtTotalEarning,txtTotalPayout,txtTripsNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.earning);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.nav_Earnings);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideKeyboardFrom(EarningActivity.this);
                onBackPressed();

            }
        });

        btnfrm= (EditText) findViewById(R.id.from);
        btnTo= (EditText) findViewById(R.id.to);
        linearDetails=findViewById(R.id.linearDetails);
        btnGetDetails=findViewById(R.id.btnGetDetails);
        //btnAmount=findViewById(R.id.btnAmount);

        txtCashEarning=findViewById(R.id.txtCashEarning);
        txtOnlineEarning=findViewById(R.id.txtOnlineEarning);
        txtFees=findViewById(R.id.txtFees);
        txtTotalEarning=findViewById(R.id.txtTotalEarning);
        txtTotalPayout=findViewById(R.id.txtTotalPayout);
        txtTripsNo=findViewById(R.id.txtTrips);

        setCurrentDate();
        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,Context.MODE_PRIVATE);



        addItemsOnSpinner();

        btnfrm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate();
            }
        });

        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate1();
            }
        });

        btnGetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DetectConnection.checkInternetConnection(getApplicationContext())) {
                    getEarningDriverStatus();

                }else {

                    Toast.makeText(getApplicationContext(),R.string.noInternet,Toast.LENGTH_LONG).show();

                }

            }
        });



        spinner_period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int arg2, long arg3) {
                selectDate(arg2);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    // add items into spinner dynamically
    public void addItemsOnSpinner() {

        spinner_period = (Spinner) findViewById(R.id.spinner_period);
        List<String> list = new ArrayList<String>();
        list.add("This day");
        list.add("This Week");
        list.add("This Month");
        list.add("Yesterday");
        list.add("Previous Week");
        list.add("Previous Month");
        list.add("Previous Year");
        list.add("Last 7 days");
        list.add("Last 30 days");
        list.add("Custom");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EarningActivity.this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_period.setAdapter(dataAdapter);
    }

    private void selectDate(int arg2) {

        Calendar c = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        String mfrom,mTo;
        switch (arg2) {

            case 0:
                //This Day

                disaleBtn();


                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDayFr = c.get(Calendar.DAY_OF_MONTH);
                mDayTo = c.get(Calendar.DAY_OF_MONTH);

                btnfrm.setText(format.format(c.getTime()));
                btnTo.setText(format.format(c.getTime()));


                break;

            case 1:
                //This Week
                disaleBtn();
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

                mYear =c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH) ;
                mDay = c.get(Calendar.DAY_OF_MONTH);

                btnfrm.setText(format.format(c.getTime()));

                c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                int lmDay = c.get(Calendar.DAY_OF_MONTH);

                btnTo.setText(format.format(c.getTime()));


                break;

            case 2:
                //This Month
                disaleBtn();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                //mDayFr = c.get(Calendar.DAY_OF_MONTH);
                mDayFr = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                mDayTo = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                mfrom=mYear + "-" + (mMonth + 1) + "-" + mDayFr;
                mTo=mYear + "-" + (mMonth + 1) + "-" + mDayTo;

                // btnfrm.setText(mYear + "-" + (mMonth + 1) + "-" + mDayFr);
                btnfrm.setText(Constant.formateDate1(mfrom));

                //btnTo.setText(mYear + "-" + (mMonth + 1) + "-" + mDayTo);
                btnTo.setText(Constant.formateDate1(mTo));


                break;

            case 3:
                disaleBtn();
                //This Yesterday
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                dateFormat.format(cal.getTime()); //your formatted date here

                mYear = cal.get(Calendar.YEAR);
                mMonth = cal.get(Calendar.MONTH);
                mDayFr = cal.get(Calendar.DAY_OF_MONTH);
                // mDayFr = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                mDayTo = cal.get(Calendar.DAY_OF_MONTH);

                mfrom=mYear + "-" + (mMonth + 1) + "-" + mDayFr;
                mTo=mYear + "-" + (mMonth + 1) + "-" + mDayTo;

                btnfrm.setText(Constant.formateDate1(mfrom));
                btnTo.setText(Constant.formateDate1(mTo));

                break;

            case 4:
                // Previous Week
                disaleBtn();
                int weekCount=c.get(Calendar.WEEK_OF_MONTH);
                Log.d("WeekCount",weekCount+"");
                c.set(Calendar.WEEK_OF_MONTH,weekCount-1);

                // c.add(Calendar.DATE, -7);
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                //c.setFirstDayOfWeek(Calendar.SUNDAY);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDayFr = c.get(Calendar.DAY_OF_MONTH);
                //btnfrm.setText(mYear + "-" + (mMonth + 1) + "-" + mDayFr);
                btnfrm.setText(format.format(c.getTime()));

                c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                // c.setFirstDayOfWeek(Calendar.SATURDAY);
                mDayTo = c.get(Calendar.DAY_OF_MONTH);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                //btnTo.setText(mYear + "-" + (mMonth + 1) + "-" + mDayTo);
                btnTo.setText(format.format(c.getTime()));


                break;

            case 5:
                // Previous Month
                disaleBtn();
                c.add(Calendar.MONTH, -1);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);

                mDayFr = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                mDayTo = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                mfrom=mYear + "-" + (mMonth + 1) + "-" + mDayFr;
                mTo=mYear + "-" + (mMonth + 1) + "-" + mDayTo;

                btnfrm.setText(Constant.formateDate1(mfrom));
                btnTo.setText(Constant.formateDate1(mTo));


                break;

            case 6:
                disaleBtn();
                // Previous Year


                c.add(Calendar.YEAR, -1);
                c.set(Calendar.MONTH, Calendar.JANUARY);
                mDayFr = c.getActualMinimum(Calendar.DAY_OF_YEAR);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);


                mfrom=mYear + "-" + (mMonth + 1) + "-" + mDayFr;
                btnfrm.setText(Constant.formateDate1(mfrom));

                c.set(Calendar.MONTH, Calendar.DECEMBER);
                mDayTo = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);


                mTo=mYear + "-" + (mMonth + 1) + "-" + mDayTo;
                btnTo.setText(Constant.formateDate1(mTo));

                break;

            case 7:
                //Last 7 days
                disaleBtn();


                mDayTo = c.get(Calendar.DAY_OF_MONTH);// set Today date

                Log.d(TAG,format.format(c.getTime())+"");

                btnTo.setText(format.format(c.getTime())+"");

                c.add(Calendar.DATE, -7);//remove 7 days

                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDayFr = c.get(Calendar.DAY_OF_MONTH);

                btnfrm.setText(format.format(c.getTime())+"");


                break;

            case 8:
                disaleBtn();
                //Last 30 days
                mDayTo = c.get(Calendar.DAY_OF_MONTH);// set Today date

                Log.d(TAG,format.format(c.getTime())+"");

                btnTo.setText(format.format(c.getTime())+"");

                c.add(Calendar.DATE, -30);//remove 7 days

                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDayFr = c.get(Calendar.DAY_OF_MONTH);

                btnfrm.setText(format.format(c.getTime())+"");

                break;

            case 9:
                //Custome
                setCurrentDate();
                btnfrm.setEnabled(true);
                btnTo.setEnabled(true);
                btnfrm.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                btnTo.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                break;

        }

    }

    void  disaleBtn(){

        btnfrm.setEnabled(false);
        btnTo.setEnabled(false);
        btnfrm.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        btnTo.setBackgroundColor(getResources().getColor(R.color.colorGrey));


    }



    public void onDate(View view) {

        switch (view.getId()) {
            case R.id.from:
                //showDate(this, "from", "From");
                setDate();
                break;
            case R.id.to:
                //BDialog.showDate(this, "to", "To");
                setDate1();
                break;

        }
    }

    private void setCurrentDate() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

       /* btnfrm.setText(mYear + "-" + (mMonth + 1) + "-" +mDay);
        btnTo.setText(mYear + "-" + (mMonth + 1) + "-" +mDay);*/


        btnfrm.setText(Constant.formateDate3(mDay + "-" + (mMonth + 1) + "-" +mYear));
        btnTo.setText(Constant.formateDate3(mDay + "-" + (mMonth + 1) + "-" +mYear));

        mMonthTo1=mMonth;
        mYearTo1=mYear;
        mDayTo1=mDay;
    }

    private void setDate() {

        // Get Current Date
        /*final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);*/

       /* final Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, mMonth);
        c.set(Calendar.DAY_OF_MONTH,mDay);
        c.set(Calendar.YEAR, mYear);*/



        DatePickerDialog datePickerDialog = new DatePickerDialog(EarningActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        //btn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        //btnfrm.setText(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth);

                        //btnfrm.setText(Constant.formateDate(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth));
                        // btnfrm.setText(Constant.formateDate1(dayOfMonth + "-" + (monthOfYear + 1) + "-" +year));
                        btnfrm.setText(Constant.formateDate3(dayOfMonth + "-" + (monthOfYear + 1) + "-" +year));

                        mYear=year;
                        mMonth=monthOfYear;
                        mDay=dayOfMonth;

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    private void setDate1() {



        Log.d("CurrentDate",mDayTo1+"-"+mMonthTo1+"-"+mYearTo1);

        //btnTo.setText(format.format(c.getTime()));

        DatePickerDialog datePickerDialog = new DatePickerDialog(EarningActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        //btnTo.setText(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth);
                        //btnTo.setText(Constant.formateDate1(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth));
                        btnTo.setText(Constant.formateDate3(dayOfMonth + "-" + (monthOfYear + 1) + "-" +year));
                        mMonthTo1=monthOfYear;
                        mYearTo1=year;
                        mDayTo1=dayOfMonth;

                    }
                }, mYearTo1, mMonthTo1, mDayTo1);

        datePickerDialog.show();
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
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }


    private void getEarningDriverStatus() {

        final ProgressDialog pDialog = new ProgressDialog(EarningActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", pref.getString(Constant.DRIVER_ID,""));
            jsonObject1.put("from",Constant.formateDate2(btnfrm.getText().toString().trim()) );
            jsonObject1.put("to", Constant.formateDate2(btnTo.getText().toString().trim()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "EARNING_DRIVER_URL" + jsonObject1.toString());
        Log.d(TAG, "EARNING_DRIVER_URL" + Constant.EARNING_DRIVER_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.EARNING_DRIVER_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(EarningActivity.this,msg,Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "getearningStatus_Submit_result" + result);
                            if (result.trim().equals("200")) {
                                linearDetails.setVisibility(View.VISIBLE);
                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);
                                    String online_earning=jobj.getString("online_earning");
                                    String cash_earning=jobj.getString("cash_earning");
                                    String fees=jobj.getString("fees");
                                    String total_earning=jobj.getString("total_earning");
                                    String total_payout=jobj.getString("total_payout");
                                    String trips=jobj.getString("trips");

                                    txtOnlineEarning.setText("\u20B9 "+online_earning);
                                    txtCashEarning.setText("\u20B9 "+cash_earning);
                                    txtFees.setText("\u20B9 "+fees);
                                    txtTotalEarning.setText("\u20B9 "+total_earning);
                                    txtTotalPayout.setText("\u20B9 "+total_payout);
                                    txtTripsNo.setText("  "+trips);
                                    btnAmount.setText("Total Earning "+"\u20B9 "+total_earning);

                                }
                            }else {
                                Toast.makeText(EarningActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                         Toast.makeText(EarningActivity.this,msg,Toast.LENGTH_SHORT).show();
                        linearDetails.setVisibility(View.GONE);

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
                    Toast.makeText(EarningActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
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
