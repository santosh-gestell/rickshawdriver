package com.tretakalp.Rikshaapp.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.tretakalp.Rikshaapp.Activity.TripIdActivity;
import com.tretakalp.Rikshaapp.Adpter.ExpandableCustAdapter;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mac on 08/09/18.
 */
public class TripHistory extends Fragment {

    private static final String TAG ="TripHistory" ;
    View v;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    //ArrayList<Bean> tripList=new ArrayList<>();

    ExpandableCustAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Bean>> listDataChild;
    HashMap<Bean, List<Bean>> triplistDataChild;

    List<Bean> tripList=new ArrayList<>();
    List<Bean> tripDateList=new ArrayList<>();
    List<Bean> tripCountList=new ArrayList<>();
    String driverId;
    SharedPreferences pref;

    DatabaseHelper db;
    TextView txtmsg;

    String fromDate ;
    String toDate;
    Spinner spinner_period;
    private int mYear, mMonth, mDay, mHour, mMinute,mDayFr,mDayTo;
    //Button btnfrm,btnTo;
    EditText btnfrm,btnTo;
    LinearLayout linearDetails;
    Button btnGetDetails,btnAmount;
    ArrayList<Bean> tripIdList=new ArrayList<>();

    int mMonthTo1,mYearTo1, mDayTo1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadLocale();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.trip_history,container,false);
        db=DatabaseHelper.getInstance(getActivity());
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_tripshistory));
        btnGetDetails=v.findViewById(R.id.btnGetDetails);
        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.lvExp);
        txtmsg=v.findViewById(R.id.txtmsg);
        pref=getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");

        btnfrm= (EditText) v.findViewById(R.id.from);
        btnTo= (EditText) v.findViewById(R.id.to);


        Bundle args = getArguments();
        fromDate = args.getString("fromDate");
        toDate = args.getString("toDate");

        if(DetectConnection.checkInternetConnection(getActivity())) {
            Log.d(TAG,"LastTimeStamp: "+pref.getString(Constant.LAST_HISTORY_TIMESTAMP,""));
            if(pref.getString(Constant.LAST_HISTORY_TIMESTAMP,"").equals("")) {
                db.deleteAllTripHistory(driverId);
                getAllTripEarnHistory();
            }
        }

        btnGetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                   fromDate=Constant.formateDate2(btnfrm.getText().toString().trim());
                   toDate=Constant.formateDate2(btnTo.getText().toString().trim());

                    Date date1=getDateFromString(fromDate);
                    Date date2=getDateFromString(toDate);

                    long diff = date2.getTime() - date1.getTime();
                    long seconds = diff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                Log.d("dayyyy",days+"");
                if(days>=0) {
                    if (days <= 31) {

                        // preparing list data
                        //prepareListData1();
                        //setExapandebleAdpterView();
                        if (DetectConnection.checkInternetConnection(getActivity())) {
                            getTripEarnHistory();

                        } else {

                            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_LONG).show();
                            prepareListData1();
                            setExapandebleAdpterView();

                        }
                    } else {

                        Toast.makeText(getContext(), R.string.dateshouldbetwo, Toast.LENGTH_LONG).show();

                    }
                }else {
                    Toast.makeText(getContext(),R.string.senconddateshouldbegreater,Toast.LENGTH_LONG).show();
                }



            }
        });


        setCurrentDate();

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


        /* // preparing list data
        prepareListData1();
        setExapandebleAdpterView();*/


        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                Intent ii=new Intent(getActivity(), TripIdActivity.class);
                ii.putExtra("source",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getPickupLocName());
                ii.putExtra("destination",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getDropLocName());
                ii.putExtra("amount",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getTotalAmount());
                ii.putExtra("time",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getTime());
                ii.putExtra("tripId",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getTripId()+"");
                ii.putExtra("distance",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getTotalDistance()+"");
                ii.putExtra("paymentMode",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getPaymentMode()+"");
                //ii.putExtra("startTime",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getStartRideTime()+"");
                ii.putExtra("endTime",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getTime()+"");
                ii.putExtra("fee",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getFee()+"");
                ii.putExtra("rideCharge",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getRideCharge()+"");
                ii.putExtra("payment",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).getPayment()+"");

                //ii.putExtra("tripId",triplistDataChild.get(tripList.get(groupPosition)).get(childPosition).isTripId()+"");
                startActivity(ii);

                return false;
            }
        });





        //showList();

        return v;
    }



    private void setExapandebleAdpterView() {

        //listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        listAdapter = new ExpandableCustAdapter(getContext(), tripList, triplistDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

    }

    private Date getDateFromString(String stringDate) {
        Date date=null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {

            date = format.parse(stringDate);
            System.out.println(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_tripshistory));
        ((MapActivity)getActivity()).loadLocale();
    }

    private void prepareListData1() {
        tripDateList.clear();
        tripList.clear();
        tripCountList.clear();
        //parseData();

        //listDataHeader = new ArrayList<String>();
        triplistDataChild = new HashMap<Bean, List<Bean>>();
       /* if(fromDate.equals("")) {
            ////////tripDateList = db.getTripDate(driverId);
            tripDateList = db.getTripDateNew(driverId);
            Log.d("TripHistory","fromDate Null");
        }else {*/
            //tripDateList=db.getTripDateBySpecificDate(driverId,fromDate,toDate);//Fetch data by using specific date
            tripDateList=db.getTripDateBySpecificDateNew(driverId,fromDate,toDate);//Fetch data by using specific date
            Log.d("TripHistory","fromDate NotNull");
        //}

        //If empty then show text No Data found
        if(tripDateList.isEmpty()||tripDateList.size()==0){

            txtmsg.setVisibility(View.VISIBLE);
            expListView.setVisibility(View.GONE);

        }else {

            txtmsg.setVisibility(View.GONE);
            expListView.setVisibility(View.VISIBLE);
        }


        for(int i=0;i<tripDateList.size();i++){

            //get count of trips tht particular date
           // int tripCount=db.getTripCountDetails(driverId,tripDateList.get(i).getTimeStamp());
            int tripCount=db.getTripCountDetailsNew(driverId,tripDateList.get(i).getTimeStamp());
            /*Bean b=new Bean();
            b.setTripCount(tripCount+"");
            tripCountList.add(b);*/

            //add tripcount and tripdate to show in main row
            Bean b=new Bean();
            b.setTripDate(tripDateList.get(i).getTimeStamp());
            b.setTripCount(tripCount+" "+getResources().getString(R.string.trips));
            tripList.add(b);

        }


        List<Bean> top250 = new ArrayList<Bean>();

        //add details of trip list
        for(int i=0;i<tripList.size();i++){

            //top250=db.getTripDetailsByDate(driverId,tripList.get(i).getTripDate());
            top250=db.getTripDetailsByDateNew(driverId,tripList.get(i).getTripDate());
            triplistDataChild.put(tripList.get(i),top250);

        }




    }




    // add items into spinner dynamically
    public void addItemsOnSpinner() {

        spinner_period = (Spinner) v.findViewById(R.id.spinner_period);
        List<String> list = new ArrayList<String>();
        /*list.add("This day");
        list.add("This Week");
       // list.add("This Month");
        list.add("Yesterday");
        list.add("Previous Week");
       // list.add("Previous Month");
        //list.add("Previous Year");
        list.add("Last 7 days");
        list.add("Last 30 days");
        list.add("Custom");*/

        list.add(getResources().getString(R.string.thisday));
        list.add(getResources().getString(R.string.thisweek));
        list.add(getResources().getString(R.string.yesterday));
        list.add(getResources().getString(R.string.previweek));
        list.add(getResources().getString(R.string.lastsevenday));
        list.add(getResources().getString(R.string.lastthirtyday));
        list.add(getResources().getString(R.string.custom));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_period.setAdapter(dataAdapter);
    }

    private void selectDate(int arg2) {

        Calendar c = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
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

            //case 2:
                //This Month
                /*disaleBtn();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);

                mDayFr = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                mDayTo = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                mfrom=mYear + "-" + (mMonth + 1) + "-" + mDayFr;
                mTo=mYear + "-" + (mMonth + 1) + "-" + mDayTo;


                btnfrm.setText(Constant.formateDate1(mfrom));


                btnTo.setText(Constant.formateDate1(mTo));*/


               // break;

            case 2:
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

            case 3:
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

           // case 5:
                // Previous Month
                /*disaleBtn();
                c.add(Calendar.MONTH, -1);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);

                mDayFr = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                mDayTo = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                mfrom=mYear + "-" + (mMonth + 1) + "-" + mDayFr;
                mTo=mYear + "-" + (mMonth + 1) + "-" + mDayTo;

                btnfrm.setText(Constant.formateDate1(mfrom));
                btnTo.setText(Constant.formateDate1(mTo));*/


               // break;

            //case 6:
                //disaleBtn();
                // Previous Year


                /*c.add(Calendar.YEAR, -1);
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
                btnTo.setText(Constant.formateDate1(mTo));*/

                //break;

            case 4:
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

            case 5:
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

            case 6:
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

    public void getLastOneMonthDate(){

        Calendar c = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        String mfrom,mTo;

        mDayTo = c.get(Calendar.DAY_OF_MONTH);// set Today date

        Log.d(TAG,format.format(c.getTime())+"");

        btnTo.setText(format.format(c.getTime())+"");

        c.add(Calendar.DATE, -30);//remove 7 days

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDayFr = c.get(Calendar.DAY_OF_MONTH);

        btnfrm.setText(format.format(c.getTime())+"");


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


        btnfrm.setText(Constant.formateDate3(mDay + "-" + (mMonth + 1) + "-" +mYear));
        btnTo.setText(Constant.formateDate3(mDay + "-" + (mMonth + 1) + "-" +mYear));

        mMonthTo1=mMonth;
        mYearTo1=mYear;
        mDayTo1=mDay;
    }

    private void setDate() {


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        btnfrm.setText(Constant.formateDate3(dayOfMonth + "-" + (monthOfYear + 1) + "-" +year));

                        mYear=year;
                        mMonth=monthOfYear;
                        mDay=dayOfMonth;

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }


    private void setDate1() {



        Log.d("CurrentDate",mDayTo1+"-"+mMonthTo1+"-"+mYearTo1);

        //btnTo.setText(format.format(c.getTime()));

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        btnTo.setText(Constant.formateDate3(dayOfMonth + "-" + (monthOfYear + 1) + "-" +year));
                        mMonthTo1=monthOfYear;
                        mYearTo1=year;
                        mDayTo1=dayOfMonth;

                    }
                }, mYearTo1, mMonthTo1, mDayTo1);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();


    }


    private void getTripEarnHistory() {

       // getLastOneMonthDate();
       // db.deleteAllTripHistory(driverId);
        tripIdList.clear();
        tripIdList= db.getTripIdData(Constant.formateDate2(btnfrm.getText().toString().trim()),Constant.formateDate2(btnTo.getText().toString().trim()),driverId);
        Log.d(TAG,"tripIdSize:"+tripIdList.size());

        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("from",Constant.formateDate2(btnfrm.getText().toString().trim()));
            jsonObject1.put("to", Constant.formateDate2(btnTo.getText().toString().trim()));


            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<tripIdList.size();i++){
                jsonArray.put(tripIdList.get(i).getTripId());
            }
            jsonObject1.put("trip_id", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "TripHistory_URL" + jsonObject1.toString());
        Log.d(TAG, "TripHistory_URL" + Constant.TRIP_HISTORY_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.TRIP_HISTORY_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "getearningStatus_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);
                                    JSONArray jsonArray1=jobj.getJSONArray("details");
                                    for(int j=0;j<jsonArray1.length();j++){
                                        JSONObject jobj1=jsonArray1.getJSONObject(j);
                                        Bean b=new Bean();
                                        b.setTotalDistance(jobj1.getString("distance"));
                                        b.setDuration(jobj1.getString("duration"));
                                        b.setPickupLocName(jobj1.getString("pickup_location"));
                                        b.setDropLocName(jobj1.getString("drop_location"));
                                        b.setNetAmount(jobj1.getString("amount"));
                                        b.setRideCharge(jobj1.getString("ride_charge"));
                                        b.setFee(jobj1.getString("fee"));
                                        b.setTotalAmount(jobj1.getString("total_bill"));
                                        b.setTripNumber(jobj1.getString("trip_number"));
                                        b.setTripId(jobj1.getString("trip_id"));
                                        b.setPaymentMode(jobj1.getString("payment_mode"));
                                        b.setPayment(jobj1.getString("payment"));
                                        b.setTripDate(jobj1.getString("created_date"));
                                        b.setDriverId(driverId);


                                        db.insertTripHistoryDetails(b);

                                    }


                                }


                                prepareListData1();
                                setExapandebleAdpterView();


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
                        if(!msg.equalsIgnoreCase("No data found")) {
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                        prepareListData1();
                        setExapandebleAdpterView();

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
                    Toast.makeText(getContext(), R.string.networkproblem, Toast.LENGTH_SHORT).show();
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


    private void getAllTripEarnHistory() {

         getLastOneMonthDate();
        // db.deleteAllTripHistory(driverId);
        tripIdList.clear();
        tripIdList= db.getTripIdData(Constant.formateDate2(btnfrm.getText().toString().trim()),Constant.formateDate2(btnTo.getText().toString().trim()),driverId);
        Log.d(TAG,"tripIdSize:"+tripIdList.size());

        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("from",Constant.formateDate2(btnfrm.getText().toString().trim()));
            jsonObject1.put("to", Constant.formateDate2(btnTo.getText().toString().trim()));


            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<tripIdList.size();i++){
                jsonArray.put(tripIdList.get(i).getTripId());
            }
            jsonObject1.put("trip_id", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "EARNING_DRIVER_URL" + jsonObject1.toString());
        Log.d(TAG, "EARNING_DRIVER_URL" + Constant.TRIP_HISTORY_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.TRIP_HISTORY_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "getearningStatus_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);
                                    JSONArray jsonArray1=jobj.getJSONArray("details");
                                    for(int j=0;j<jsonArray1.length();j++){
                                        JSONObject jobj1=jsonArray1.getJSONObject(j);
                                        Bean b=new Bean();
                                        b.setTotalDistance(jobj1.getString("distance"));
                                        b.setDuration(jobj1.getString("duration"));
                                        b.setPickupLocName(jobj1.getString("pickup_location"));
                                        b.setDropLocName(jobj1.getString("drop_location"));
                                        b.setNetAmount(jobj1.getString("amount"));
                                        b.setRideCharge(jobj1.getString("ride_charge"));
                                        b.setFee(jobj1.getString("fee"));
                                        b.setTotalAmount(jobj1.getString("total_bill"));
                                        b.setTripNumber(jobj1.getString("trip_number"));
                                        b.setTripId(jobj1.getString("trip_id"));
                                        b.setPaymentMode(jobj1.getString("payment_mode"));
                                        b.setPayment(jobj1.getString("payment"));
                                        b.setTripDate(jobj1.getString("created_date"));
                                        b.setDriverId(driverId);

                                        db.insertTripHistoryDetails(b);

                                    }

                                }

                                SharedPreferences.Editor editor=pref.edit();
                                editor.putString(Constant.LAST_HISTORY_TIMESTAMP,Constant.getDateTime());
                                editor.commit();

                                prepareListData1();
                                setExapandebleAdpterView();


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
                        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                        prepareListData1();
                        setExapandebleAdpterView();

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
                    Toast.makeText(getContext(), R.string.networkproblem, Toast.LENGTH_SHORT).show();
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
