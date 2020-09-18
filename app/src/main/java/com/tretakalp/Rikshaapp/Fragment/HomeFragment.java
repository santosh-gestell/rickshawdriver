package com.tretakalp.Rikshaapp.Fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tretakalp.Rikshaapp.Activity.MapActivity;
import com.tretakalp.Rikshaapp.Activity.RideSummary;
import com.tretakalp.Rikshaapp.Adpter.AdapterLocationList;
import com.tretakalp.Rikshaapp.Adpter.AdapterLocationList.AdapterLocationListListener;
import com.tretakalp.Rikshaapp.Adpter.CustomAdapter;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Model.PlaceModule;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;
import com.tretakalp.Rikshaapp.Service.MyFirebaseMessagingServiceNew;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Mac on 04/09/18.
 */
public class HomeFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, AdapterLocationListListener{

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String MYTAG = "mytag";
    Button btnfixRoute, btnDynamicRoute;

    public final static int REQUEST_LOCATION = 199;
    private static final String TAG = "HomeFragment";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 69;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    //SharedPreferences pref;
    TextView txt_username, txt_inOutTime;

    List<PlaceModule> placeModuleList; // used for location dialog list
    String LocationID = "";
    AdapterLocationList adapterLocationList;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest,mLocationRequestOfoneMin;
    Location mlocation;
    private GoogleMap mMap;
    Marker mCurrLocationMarker;
    MarkerOptions markerOptions = new MarkerOptions();
    public static final int RequestPermissionCode = 1;
    SupportMapFragment mapFragment;
    View v;
    View mapView;
    Marker marker_dest;
    ImageView imgBackArrow;
    //EditText edtPlaceSearch;
    EditText edtMob;
    BottomSheetBehavior behavior;
    CoordinatorLayout coordinatorLayout;
    LinearLayout linear_sourceDest, linearfragment,linearOnlineBtn;
    String distance, netPay, time, isPepaidRide;
    TextView txtKm, txtFare;
    String source = "", destination = "";
    //MapView mapFragment;
    EditText edtSource, edtDest;
    Dialog dialog1;
    Dialog dialog2;
    boolean isLogin=false;

    Button btnRide, btnEndTrip;
    boolean oneTime = false;
    String startLat, startLong, endLong, endLat;

    FusedLocationProviderClient mFusedLocationClient;
    int locationMode;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    Button btnnavigation;

    CustomAdapter adapter;
    String driverId = "";
    SharedPreferences pref;
    String trip_type, session;
    LinearLayout linear_placefragment, linearBtn;
    Toolbar toolbar;
    ImageView imgMenuBar,imgNavigation;
    RelativeLayout rlToolbar;
    AlertDialog alertPermisionDialogue;
    DatabaseHelper db;
    Button btnResend,btnStartOnlineTrip,btnOnlineCancel,btnCall;
    ImageView btnSpeak;
    Animation animSlideUp;
    Marker marker_cust;
    GetAddressAsyncTask1 getaddressAsync;
    //SupportPlaceAutocompleteFragment autocompleteFragment;
    // Initialize the AutocompleteSupportFragment.
    AutocompleteSupportFragment autocompleteFragment;

    Spinner spinner_reson;
    String language;
    ArrayList<Bean> cancelResonList=new ArrayList<>();
    String reasonId="0";
    LinearLayout progressBar_linear;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // double // current lat-long
    private Double currentLat, currentLong;

    MyFirebaseMessagingServiceNew myFirebaseMessagingServiceNew;

    // button
    private Button buttonQueue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.home_fragment_new, container, false);

        Log.d(TAG,"LifecycleMethodOfFragmetn: onCreateView()");
         if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.api_key));
        }
        PlacesClient placesClient = Places.createClient(getContext());
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        setAllView();
        db=DatabaseHelper.getInstance(getActivity());
       //db.copyDatabase(getActivity());

        dialog2 = new Dialog(getContext());//permisstionDialogue
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_location);

        animSlideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        pref = getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        driverId = pref.getString(Constant.DRIVER_ID, "");
        isLogin=pref.getBoolean(Constant.IS_LOGIN,false);

        placeModuleList = new ArrayList<>();
        //toolBarSetUp();
        //setLocationToHighAccuracy();

        LocationID = pref.getString(Constant.LOCATIONID,"");

        String deviceName = android.os.Build.MODEL;
        Log.d("DeviceName",deviceName);

        // ger lat long to check distance range
        getLatLongForDist(Constant.LOCAL_HOST+"static_calculation/location");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (checkPlayServices()) {

            Log.i("playservice", checkPlayServices() + "");
            //Building the GoogleApi client
            buildGoogleApiClient();
            //createLocationRequest();
            //mGoogleApiClient.connect();
            initialiseMap();
            //connectDriver();

        } else {
            Toast.makeText(getContext(), "Google Play Services Error", Toast.LENGTH_SHORT).show();
        }

        setSearchGooglePlaceFragment();





        // button queue
        buttonQueue = (Button) v.findViewById(R.id.buttonQueue_newFragment);

       /* String isPrepaid=pref.getString(Constant.IS_PREPAID_RIDE,"");
        Log.d(TAG,"isPrepaid: "+isPrepaid);
        if (isPrepaid.equals("prepaid")){
            buttonQueue.setVisibility(View.GONE);
        }else{
            buttonQueue.setVisibility(View.VISIBLE);
        }*/

        // enable disable button on ride status == if ride is on disable button else visible it
        Log.d(MYTAG,"ON_RIDE_STATUS : "+pref.getString(Constant.ON_RIDE_STATUS,""));
        if (pref.getString(Constant.ON_RIDE_STATUS,"").equals("1")){
            buttonQueue.setVisibility(View.GONE);
        }else{buttonQueue.setVisibility(View.VISIBLE);}

        if (pref.getString(Constant.QUEUE_STATUS,"").equals("1")){
            buttonQueue.setBackgroundResource(R.drawable.button_desable);
            buttonQueue.setText("DEQUE");

        }else{
            buttonQueue.setBackgroundResource(R.drawable.que_button);
            buttonQueue.setText(R.string.button_que);
        }



        //postDriverInQueue();
        buttonQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (buttonQueue.getText().toString().equals("QUEUE")){
                    Log.d("mytag","queue");

                    showListDialog();


                }else{

                    dequeueDriver();
                    Log.d("mytag","De-queue");
                }

            }
        });

        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                slidUp(linear_sourceDest);//slide up animation

                //edtPlaceSearch.setText("");
                // edtPlaceSearch.setCursorVisible(true);
                //edtPlaceSearch.setFocusable(true);

               // ((ImageButton)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)).setVisibility(View.INVISIBLE);

                linear_placefragment.setVisibility(View.VISIBLE);
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                behavior.setPeekHeight(0);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
                // params.width = 200; params.leftMargin = 100;
                params.topMargin = -200;

                if(marker_dest!=null){
                    marker_dest.remove();
                    marker_dest=null;
                }

              //  Constant.hideKeyboardFrom1(getActivity(),edtPlaceSearch);

            }
        });

        btnnavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
                startActivity(intent);*/
                if(pref.getInt(Constant.ISRIDE_START,0)==1) {
                    /**If Ride started then show current and destination route*/

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + pref.getString(Constant.END_LAT, "")+","+pref.getString(Constant.END_LONG, "")));
                    startActivity(intent);
                }else if(pref.getInt(Constant.ISRIDE_START,0)==0) {
                    /**If online Ride accept and ride not started then show current and customer location route*/

                    if (pref.getString(Constant.ACCEPT_RIDE, "0").equals("1")){
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + pref.getString(Constant.CUST_LAT, "")+","+pref.getString(Constant.CUST_LONG, "")));
                        startActivity(intent);
                        Log.d("StartNavigation","hello");
                        Log.d("StartNavigation",pref.getString(Constant.START_LOCATION_NAME, ""));
                    }

                }

            }
        });


        imgMenuBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DetectConnection.checkInternetConnection(getContext())){
                    ((MapActivity) getActivity()).openDrawer();
                }else {
                    //((MapActivity) getActivity()).showSnackBar(getResources().getString(R.string.noInternet));
                    ((MapActivity) getActivity()).showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
                }

            }
        });


        btnfixRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trip_type="static";
                setTripTypeInLocal(trip_type);
                Toast.makeText(getContext(),R.string.staticrideselected,Toast.LENGTH_SHORT).show();
               /* btnfixRoute.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                btnDynamicRoute.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

                btnfixRoute.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                btnDynamicRoute.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));*/
                changeColorOfBtn(btnfixRoute,btnDynamicRoute);

            }
        });

        btnDynamicRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),R.string.dynamicrideselected,Toast.LENGTH_SHORT).show();
                trip_type="dynamic";

                setTripTypeInLocal(trip_type);

                /*btnfixRoute.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                btnDynamicRoute.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                btnfixRoute.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                btnDynamicRoute.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));*/
                changeColorOfBtn(btnDynamicRoute,btnfixRoute);

            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String phone=pref.getString(Constant.CUSTOMER_CONTACT,"");


                if (!checkPermission()) {
                    requestPermission();
                }else {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone));

                    if (ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
            }


        });

        btnOnlineCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DetectConnection.checkInternetConnection(getContext())){

                   // cancelTripRequest();
                    getCancelTripReson();

                }else {
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));
                }



                /*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.cancelRide);
                builder.setMessage(R.string.doyouwanttocanceltrip);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(DetectConnection.checkInternetConnection(getContext())){

                            cancelTripRequest();

                        }else {
                            ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));
                        }

                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //onBackPressed();

                    }
                });
                AlertDialog alertDialog=builder.create();

                alertDialog.show();*/





            }
        });

        btnStartOnlineTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             showOTP_DialogBox();

                /*if(DetectConnection.checkInternetConnection(getContext())) {
                    insertStartRide(pref.getString(Constant.CUSTOMER_CONTACT,""),
                            pref.getString(Constant.START_LOCATION_NAME,""),
                            pref.getString(Constant.END_LOCATION_NAME,""),
                            pref.getString(Constant.SESSION_ID,""),
                            "online", pref.getString(Constant.TIME,""),
                            pref.getString(Constant.NETPAY,""),
                            pref.getString(Constant.DISTANCE,""), "1544");
                }else {
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));
                }*/
            }
        });

        btnRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogBox();

            }
        });


        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                promptSpeechInput();

            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    // endTrip(source, session, time, distance, mCurrLocationMarker.getPosition().latitude + "", mCurrLocationMarker.getPosition().longitude + "");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.resendOtp);
                    builder.setMessage(R.string.doyouwanttoresendotp);

                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DetectConnection.checkInternetConnection(getContext())) {
                            resendOtp();
                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            //onBackPressed();

                        }
                    });
                    AlertDialog alertDialog=builder.create();

                    alertDialog.show();


            }
        });

        btnEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DetectConnection.checkInternetConnection(getContext())) {

                    //sometimes source get last postion name.. to avoid this bug call again asynctask
                    if(mCurrLocationMarker!=null) {

                        if(getaddressAsync!=null) {
                            getaddressAsync.cancel(true);
                            getaddressAsync=null;
                        }

                         getaddressAsync =new GetAddressAsyncTask1(new LatLng(mCurrLocationMarker.getPosition().latitude, mCurrLocationMarker.getPosition().longitude));
                         getaddressAsync.execute();
                    }else {

                        if(getaddressAsync!=null) {
                            getaddressAsync.cancel(true);
                            getaddressAsync=null;
                        }

                        Location location=((MapActivity)getActivity()).getLast_Location();//get location from LocationService bind
                        getaddressAsync =  new GetAddressAsyncTask1(new LatLng(location.getLatitude(), location.getLongitude()));
                        getaddressAsync.execute();
                    }

                    locationMode = Constant.getLocationMode(getContext());
                    if (Constant.isgpsAvailable(getContext())&&locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                        showDialogBox1();
                    }else {
                        checkLocationSetting();

                        Toast.makeText(getContext(),R.string.noGps,Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
                }
            }
        });


        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                /*if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }*/
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events

            }
        });


        if(DetectConnection.checkInternetConnection(getContext())) {

            if (isLogin) {
                // if two online notification is accepted and want to accept third ride notitification then dont allow
                //driver can accept two ride at a time
                // if(pref.getInt(Constant.ISRIDE_START,0)==1){
                Log.d(MYTAG,"login true");


            }else {
               // Toast.makeText(getContext(), R.string.pleasLogin,Toast.LENGTH_LONG).show();
            }
        }else {

            //Toast.makeText(getContext(), R.string.noInternet,Toast.LENGTH_LONG).show();
        }




        return v;
    }

    private void showListDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_locationlist);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        final ListView listView = (ListView)dialog.findViewById(R.id.locationDialogList);
        listView.setAdapter(adapterLocationList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(MYTAG,"name: "+placeModuleList.get(position).getPlaceName());
                pref.edit().putString(Constant.LOCATIONID,placeModuleList.get(position).getId());
                LocationID = placeModuleList.get(position).getId();

                pref.edit().putString("lat",placeModuleList.get(position).getLat()).commit();
                pref.edit().putString("long",placeModuleList.get(position).getLong()).commit();

                dialog.hide();
                postDriverInQueue(LocationID);
            }
        });


        dialog.show();
    }

    private void getLatLongForDist(String url) {
        Log.d(MYTAG,"URL: "+url);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(MYTAG,"Location Result: "+response);

                parseJson(response);

                try {

                    JSONObject object = new JSONObject(response);

                    String data = object.getString("data");

                    JSONObject obj = new JSONObject(data);

                   // pref.edit().putString("lat",obj.getString("lat")).commit();
                   // pref.edit().putString("long",obj.getString("long")).commit();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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



        request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(request, Constant.tag_json_obj);

    }

    private void parseJson(String response) {

        try {
            JSONObject object = new JSONObject(response);
            String data = object.getString("data");
            JSONArray array = new JSONArray(data);
            for (int i=0; i<array.length(); i++){
                JSONObject object1 = array.getJSONObject(i);
                placeModuleList.add(new PlaceModule(object1));
            }
        adapterLocationList = new AdapterLocationList(getContext(), placeModuleList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void clearNotification() {
        NotificationManager notificationmanager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancelAll();
        Log.d(TAG,"NotificationCancel");
    }

    // remove driver form queue list
    private void dequeueDriver() {

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("driver_id", pref.getString(Constant.DRIVER_ID,""));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "de-queue param" + jsonObject1.toString());
        Log.d(TAG, "de-queue URl" + Constant.DE_QUEUE);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.DE_QUEUE, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getCityList_Submit_result" + result);

                            if (result.trim().equals("200")) {
                                    changeButtonState();
                                //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();


                            }else {
                                //  mMap.clear();
                                //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {



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

    private void changeButtonState() {
        pref.edit().putString(Constant.QUEUE_STATUS,"0").commit();
        buttonQueue.setBackgroundResource(R.drawable.que_button);
        buttonQueue.setText("QUEUE");
    }


    //post driver queue
    private void postDriverInQueue(String id) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("mobile_no", pref.getString(Constant.MOBILE_NO,""));
            jsonObject1.put("location_id", id);
            jsonObject1.put("latitude",  String.valueOf(currentLat));
            jsonObject1.put("longitude", String.valueOf(currentLong));
            jsonObject1.put("driver_id", pref.getString(Constant.DRIVER_ID,""));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "queue param" + jsonObject1.toString());
        Log.d(TAG, "queue URl" + Constant.ADD_QUEUE);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.ADD_QUEUE, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getCityList_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                desableQueueButton();

                            }else {
                                //  mMap.clear();
                                //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

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

    // after getting driver added in queue disable button
    private void desableQueueButton() {
        pref.edit().putString(Constant.QUEUE_STATUS,"1").commit();
        buttonQueue.setBackgroundResource(R.drawable.button_desable);
        buttonQueue.setText("DEQUE");
    }


    private void setTripTypeInLocal(String trip_type) {
        SharedPreferences.Editor editor=pref.edit();
        editor.putString(Constant.TRIP_TYPE,trip_type);
        editor.commit();
    }

    private void changeColorOfBtn(Button btnGreen, Button btnWhite) {

        btnGreen.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        btnWhite.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

        btnGreen.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        btnWhite.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
    }


    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

/**
 * Check device location is on High accuracy or not
 * if not then show dialouge.
 * **/
    private void setLocationToHighAccuracy() {

        int locationMode = Constant.getLocationMode(getContext());

        if(Constant.isgpsAvailable(getContext())&&locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
           try {
               if (dialog2 != null) {
                   dialog2.dismiss();
               }
           }catch (Exception e){
               e.printStackTrace();
           }
            //request location updates
        } else {
            //redirect user to settings page
            if(!dialog2.isShowing()) {
                showDialogBoxPermission();
            }

        }

    }

    /*private void setAutoCompleteSerch() {

        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

    }*/
    /**
     * set filter, view of SupportPlaceAutocompleteFragment
     * for searching location
     * */

    private void setSearchGooglePlaceFragment() {

        final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();

        //PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
       // autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
       // edtPlaceSearch=  ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));


        ImageButton btnSearch= ((ImageButton) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button));
        //edtPlaceSearch.setHint(R.string.enter_destination);
       // edtPlaceSearch.setTextAppearance(getActivity(), R.style.headlineFont);
       // edtPlaceSearch.setTextSize(20f);
        //edtPlaceSearch.setHintTextColor(ContextCompat.getColor(getContext(),R.color.colorWhite));


        /*edtPlaceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(!DetectConnection.checkInternetConnection(getActivity())){
                    ((MapActivity)getActivity()).showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
                }else {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .setFilter(typeFilter)
                                        .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!DetectConnection.checkInternetConnection(getActivity())){
                    ((MapActivity)getActivity()).showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
                }else {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }

            }
        });*/


        autocompleteFragment.setCountry("IN");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteFragment.setHint("Destination");
        autocompleteFragment.a.setTextSize(14f);
        //autocompleteFragment.setFilter(typeFilter);
        //autocompleteFragment.seton


        //autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d(MYTAG,"place: "+place);
                Log.d(MYTAG,"placeAddress: "+place.getAddress());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                // onSearchCalled();
                setGooglePlaceSearchPlaceText(place);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }

    /**
     * when user search destination then set marker ofr destination and send
     * current driver location and dest loc to server and getDistance Fare
     * */
    private void setGooglePlaceSearchPlaceText(Place place) {

        if(place.getLatLng().latitude!=0) {



            if (mCurrLocationMarker != null) {

                locationMode = Constant.getLocationMode(getContext());

                if (Constant.isgpsAvailable(getContext()) && locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {

                   // Log.d(TAG, "Destination: " + place.getAddress() + " --Place Name: " + place.getName() + " " + place.getLocale());
                    destination = place.getAddress().toString();

                    if (marker_dest != null) {

                        marker_dest.remove();
                        marker_dest=null;

                    }
                    marker_dest = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));

                    if(DetectConnection.checkInternetConnection(getContext())) {
                        getDistanceFare(source, destination);
                    }else {
                        ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));
                    }


                } else {

                    Toast.makeText(getContext(), R.string.noGps, Toast.LENGTH_SHORT).show();
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noGps));

                    setLocationToHighAccuracy();
                    //edtPlaceSearch.setText("");

                }
            }

        }else {
            Toast.makeText(getContext(),R.string.cantFindlatlong,Toast.LENGTH_SHORT).show();
            ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.cantFindlatlong));
        }

    }

    /**
     * set toolbar
     * */
    private void toolBarSetUp() {

        toolbar = (Toolbar) v.findViewById(R.id.toolbar1);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onBackPressed();

                //linearLayout.setVisibility(View.GONE);
                //linearLayout.startAnimation(animSlideUp);
                //slideDown(linearLayout);
                slidUp(linear_sourceDest);

                linear_placefragment.setVisibility(View.VISIBLE);
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                behavior.setPeekHeight(0);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
                // params.width = 200; params.leftMargin = 100;
                params.topMargin = -200;

            }
        });
    }

    private void setAllView() {

        btnfixRoute=v.findViewById(R.id.btnfixRoute);
        btnDynamicRoute=v.findViewById(R.id.btndynamicRoute);
        btnnavigation=v.findViewById(R.id.btnnavigation);
        btnCall=v.findViewById(R.id.btnCall);
        btnOnlineCancel=v.findViewById(R.id.btnOnlineCancel);
        btnStartOnlineTrip=v.findViewById(R.id.btnStartOnlineTrip);

        linear_sourceDest=v.findViewById(R.id.l2);
        linearOnlineBtn=v.findViewById(R.id.linearOnlineBtn);
        progressBar_linear=v.findViewById(R.id.progressBar_linear);

        txtKm=v.findViewById(R.id.txtKm);
        txtFare=v.findViewById(R.id.txtFare);
       // txtNaviagtion=v.findViewById(R.id.txtnavigation);

        edtDest=v.findViewById(R.id.edtDest);
        edtSource=v.findViewById(R.id.edtsource);
        btnRide=v.findViewById(R.id.btnRide);
        btnSpeak=v.findViewById(R.id.btnSpeak);
        btnEndTrip=v.findViewById(R.id.btnEndTrip);
        btnResend=v.findViewById(R.id.btnResend);
        linear_placefragment=v.findViewById(R.id.linear_placefragment);
        linearBtn=v.findViewById(R.id.linearBtn);
        imgBackArrow=v.findViewById(R.id.imgBackArrow);
        //imgNavigation=v.findViewById(R.id.imgNavigation);
        imgMenuBar=v.findViewById(R.id.imgMenuBar);
        rlToolbar=v.findViewById(R.id.toolbar);

        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayout);
        View bottomSheet = v.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        mAutocompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);

        SharedPreferences pref_firebase = getContext().getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
        String firebaseId = pref_firebase.getString("regId","");
        Log.d(TAG,"firebaseId: "+firebaseId);
        if(firebaseId==null) {
            try {
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                storeRegIdInPref(refreshedToken);

                Log.d("Firbase id login1", "Refreshed token: " + refreshedToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void storeRegIdInPref(String refreshedToken) {
        SharedPreferences pref = getActivity().getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", refreshedToken);
        editor.commit();
    }

    private void initialiseMap() {

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }


   /* private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);

        }
    };


        //Autocomplete editBox
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {

        }
    };*/


    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){

                if(getContext()!=null){

                    mLastLocation = location;
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();

                    Log.d("onLocationResultFu","fusedApiClient:"+location.getLatitude()+" long:"+location.getLongitude()+" acc"+location.getAccuracy());
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                }
            }

            String endAddress = pref.getString(Constant.END_LOCATION_NAME,"");
            Log.d(TAG,"address: "+endAddress);
            edtDest.setText(endAddress);
            mAutocompleteTextView.setText(endAddress);
        }
    };



    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        CALL_PHONE,
                        ACCESS_FINE_LOCATION

                }, RequestPermissionCode);

    }

    /**
     *Check User Permission
     * */
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getContext(),CALL_PHONE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED
                && SecondPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    //permission dialogue result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RequestPermissionCode){
            if (grantResults.length > 0) {

                boolean CallPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean GPSPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                // boolean ExternalPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                // boolean ReadPhoneStatePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (GPSPermission && CallPermission) {


                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getResources().getString(R.string.needphonecallpermission));
                    builder.setMessage(getResources().getString(R.string.thisappneedphonecallpermisssion));
                    builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getContext(), R.string.gotopermissionsetting, Toast.LENGTH_LONG).show();

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

        }
    }


    private void checkLocationSetting() {
        Log.d(TAG,"checkLocationSetting");

        if (mGoogleApiClient == null) {

            buildGoogleApiClient();
        }else {
            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        }


        showLocationSetting();
    }

    /**
     *if location of then show google location setting dilogue
     * */
    private void showLocationSetting() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                // final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                       // edtPlaceSearch.setText("");
                        Log.d(TAG,"LocationSettingsStatusCodes.SUCCESS:"+LocationSettingsStatusCodes.SUCCESS);

                        try {
                            if (dialog2 != null||dialog2.isShowing()) {
                                dialog2.dismiss();
                                //edtPlaceSearch.setText("");
                                Log.d(TAG,"inLocationSuccess");
                            }
                        }catch (Exception e){

                        }

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG,"RESOLUTION_REQUIRED");
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG,"SETTINGS_CHANGE_UNAVAILABLE");
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode,  resultCode,  data);
        Log.d("onActivityResult()",resultCode+"");

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:

                switch (resultCode) {
                    case Activity.RESULT_OK: {

                        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                            mFusedLocationClient.requestLocationUpdates(mLocationRequestOfoneMin, mLocationCallback, Looper.myLooper());
                            mMap.setMyLocationEnabled(true);

                            progressBar_linear.setVisibility(View.VISIBLE);
                            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                   // removeWorkingDialog();
                                    try {
                                        progressBar_linear.setVisibility(View.GONE);
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    //getActivity().onBackPressed();
                                }

                            }, 7000);

                        }
                        if(alertPermisionDialogue!=null){
                            alertPermisionDialogue.dismiss();
                        }
                        if(dialog2!=null) {
                            //edtPlaceSearch.setText("");
                            dialog2.dismiss();
                        }

                        break;
                    }
                    case Activity.RESULT_CANCELED: {

                        if(dialog2!=null||dialog2.isShowing()) {
                            dialog2.dismiss();
                           // edtPlaceSearch.setText("");
                        }


                        break;
                    }


                    default: {
                        break;
                    }
                }
                break;

            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   // edtPlaceSearch.setText(result.get(0));
                   // Constant.hideKeyboardFrom1(getContext(),edtPlaceSearch);

                    //((ImageButton)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button)).performClick();
                    //autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input) .performClick();
                    // new PlaceSelectionListener.onPlaceSelected(result.get(0));
                    /// onPlaceSelected(result.get(0));
                    //edtPlaceSearch.setText("");
                }
                break;
            }

            case PLACE_AUTOCOMPLETE_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    //Place place = PlaceAutocomplete.getPlace(getContext(), data);
                    Place place = Autocomplete.getPlaceFromIntent(data);

                    Log.i(TAG, "Place: " + place.getName());
                    setGooglePlaceSearchPlaceText(place);


                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getContext(), data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }

                break;

        }

    }


    /**
     *if linear_sourceDest is visible and ride not started yet then
     * if user take back then hide linear_sourceDest and reset other view
     * */
    public boolean closeFromToLayout() {

        if(linear_sourceDest.getVisibility()==View.VISIBLE) {
            if(pref.getInt(Constant.ISRIDE_START,0)==1){

                return false;
            }


            //linearLayout.setVisibility(View.GONE);
            //linearLayout.startAnimation(animSlideUp);
            slidUp(linear_sourceDest);

            linear_placefragment.setVisibility(View.VISIBLE);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            behavior.setPeekHeight(0);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
            // params.width = 200; params.leftMargin = 100;
            params.topMargin = -200;

            return true;
        }

        return false;
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
       // animate.setFillAfter(true);
        view.startAnimation(animate);
        //view.setVisibility(View.GONE);
    }


    // slide the view from its current position to below itself
    public void slidUp(View view){
        //if(marker_dest!=null)
            //marker_dest.remove();

        view.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                -view.getHeight()); // toYDelta
        animate.setDuration(500);
        //animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }




    /** Add address
     * get address name using latlong
     *
     * */
    private class GetAddressAsyncTask1 extends AsyncTask<String, String, List<Address>> {
        LatLng latLng;

        public  GetAddressAsyncTask1(LatLng latlong) {
            this.latLng=latlong;
        }

        @Override
        protected List<Address> doInBackground(String... params) {

            //logEvent("doInBackground "+latLng.latitude+" "+latLng.longitude);


            return  getAddressFromLatLong1(latLng.latitude,latLng.longitude);
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);
            try {
              //  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else retu
                Log.d("LocalAddress", "state:" + state + " postalCode:" + postalCode + " knownName:" + knownName);
                source=addresses.get(0).getAddressLine(0);

                //sometime getting source address take time.. when source and destination view is showing and source is empty then set
                //set edtsource to avoid bug
                if(edtSource.getText().toString().trim().equals("")){
                    edtSource.setText(source);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //get address name
    private List<Address> getAddressFromLatLong1(Double latitude, Double longitude) {

        Geocoder geocoder;
        List<Address> addresses = null;

        if(getContext()==null){
            return null;
        }
        try {

            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            String address;
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"LifecycleMethodOfFragmetn: onDestroyView()");
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    //Fetch distance and Fare from server
    private void getDistanceFare(final  String source, final String destinationName) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("source_addr", source);
            jsonObject1.put("destination_addr", destinationName);
            jsonObject1.put("slat", mCurrLocationMarker.getPosition().latitude);
            jsonObject1.put("slong", mCurrLocationMarker.getPosition().longitude);
            jsonObject1.put("dlat",marker_dest.getPosition().latitude);
            jsonObject1.put("dlong",marker_dest.getPosition().longitude);
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("slat", mCurrLocationMarker.getPosition().latitude);
            jsonObject1.put("slong", mCurrLocationMarker.getPosition().longitude);
            //jsonObject1.put("trip_type","static");
            jsonObject1.put("trip_type",pref.getString(Constant.TRIP_TYPE,"static"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "city_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.STATIC_CALC_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.STATIC_CALC_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getCityList_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                               // edtPlaceSearch.setText("");//clear text of place search

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int j=0;j<jsonArray.length();j++){

                                    JSONObject jsonObject=jsonArray.getJSONObject(j);
                                    distance=jsonObject.getString("distance");
                                    time=jsonObject.getString("time");
                                    netPay=jsonObject.getString("netPay");
                                    session=jsonObject.getString("session_code");
                                    trip_type=jsonObject.getString("trip_type");
                                    txtKm.setText(""+distance);
                                    txtFare.setText("\u20B9"+netPay);

                                    SharedPreferences.Editor edit= pref.edit();
                                    edit.putString(Constant.SESSION_ID,session);
                                    edit.putString(Constant.NETPAY,netPay);
                                    edit.putString(Constant.TIME,time);
                                    edit.putString(Constant.DISTANCE,distance);
                                    edit.commit();


                                    linear_sourceDest.setVisibility(View.VISIBLE);
                                    //btnnavigation.setVisibility(View.VISIBLE);
                                    //slideDown(linearLayout);
                                    edtSource.setText(source);
                                    edtDest.setText(destinationName);
                                    linear_placefragment.setVisibility(View.GONE);
                                    mAutocompleteTextView.setText(destinationName);//keep this

                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
                                    // params.width = 200; params.leftMargin = 100;
                                    params.topMargin = 200;

                                }

                                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                showMapZoomToMarker();

                              //  showPolygonLine();

                            }else {
                              //  mMap.clear();
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



    //Fetch distance and Fare using
    /**
     * If trip notification accepted bt not yet satrted and driver want to cancel ride
     * then call this func.. when driver cancel this ride, he wont get this custmer ride noti.
     * */
    private void cancelTripRequest() {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

         String isPrepaid=pref.getString(Constant.IS_PREPAID_RIDE,"");
         String URL = "";
         JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();

            if (isPrepaid.equals("prepaid")){
                URL = Constant.TRIP_CANCEL_URL_PREPAID;
                jsonObject1.put("driver_id", driverId);
                jsonObject1.put("location_id", LocationID);
                jsonObject1.put("booking_id", pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("contact", pref.getString(Constant.CUSTOMER_CONTACT_SECOND,""));
                jsonObject1.put("session_code",pref.getString(Constant.SESSION_ID,""));
                jsonObject1.put("slat",pref.getString(Constant.CUST_LAT,""));
                jsonObject1.put("slong",pref.getString(Constant.CUST_LONG,""));
                jsonObject1.put("dlat",pref.getString(Constant.END_LAT,""));
                jsonObject1.put("dlong",pref.getString(Constant.END_LONG,""));
                jsonObject1.put("source_addr",pref.getString(Constant.START_LOCATION_NAME,""));
                jsonObject1.put("destination_addr",pref.getString(Constant.END_LOCATION_NAME,""));
                jsonObject1.put("distance",pref.getString(Constant.DISTANCE,""));
                jsonObject1.put("amount",pref.getString(Constant.NETPAY,""));
                jsonObject1.put("duration",pref.getString(Constant.TIME,""));
                jsonObject1.put("reasonId",reasonId);
                jsonObject1.put("lang",pref.getString(Constant.Locale_KeyValue, "en"));
                jsonObject1.put("trip_type","prepaid");
            }else if(isPrepaid.equals("app")){
                URL = Constant.TRIP_CANCEL_URL;
                jsonObject1.put("driver_id", driverId);
                jsonObject1.put("location_id", LocationID);
                jsonObject1.put("booking_id", pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("contact", pref.getString(Constant.CUSTOMER_CONTACT_SECOND,""));
                jsonObject1.put("session_code",pref.getString(Constant.SESSION_ID,""));
                jsonObject1.put("cust_lat",pref.getString(Constant.CUST_LAT,""));
                jsonObject1.put("cust_long",pref.getString(Constant.CUST_LONG,""));
                jsonObject1.put("end_lat",pref.getString(Constant.END_LAT,""));
                jsonObject1.put("end_long",pref.getString(Constant.END_LONG,""));
                jsonObject1.put("startLoc",pref.getString(Constant.START_LOCATION_NAME,""));
                jsonObject1.put("endLoc",pref.getString(Constant.END_LOCATION_NAME,""));
                jsonObject1.put("distance",pref.getString(Constant.DISTANCE,""));
                jsonObject1.put("amount",pref.getString(Constant.NETPAY,""));
                jsonObject1.put("duration",pref.getString(Constant.TIME,""));
                jsonObject1.put("reasonId",reasonId);
                jsonObject1.put("lang",pref.getString(Constant.Locale_KeyValue, "en"));
                jsonObject1.put("trip_type","app");
            }else{
                URL = Constant.TRIP_CANCEL_URL;
                jsonObject1.put("driver_id", driverId);
                jsonObject1.put("location_id", LocationID);
                jsonObject1.put("booking_id", pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("contact", pref.getString(Constant.CUSTOMER_CONTACT_SECOND,""));
                jsonObject1.put("session_code",pref.getString(Constant.SESSION_ID,""));
                jsonObject1.put("cust_lat",pref.getString(Constant.CUST_LAT,""));
                jsonObject1.put("cust_long",pref.getString(Constant.CUST_LONG,""));
                jsonObject1.put("end_lat",pref.getString(Constant.END_LAT,""));
                jsonObject1.put("end_long",pref.getString(Constant.END_LONG,""));
                jsonObject1.put("startLoc",pref.getString(Constant.START_LOCATION_NAME,""));
                jsonObject1.put("endLoc",pref.getString(Constant.END_LOCATION_NAME,""));
                jsonObject1.put("distance",pref.getString(Constant.DISTANCE,""));
                jsonObject1.put("amount",pref.getString(Constant.NETPAY,""));
                jsonObject1.put("duration",pref.getString(Constant.TIME,""));
                jsonObject1.put("reasonId",reasonId);
                jsonObject1.put("lang",pref.getString(Constant.Locale_KeyValue, "en"));
            }

           // jsonObject1.put("bookingId",pref.getString(Constant.TRIP_ID,""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "cancelJson_data->>>>>" + jsonObject1.toString());
        Log.d(TAG, "URl" + URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "cancelRequest_result->>>>>" + result);
                            if (result.trim().equals("200")) {
                                // enable que button
                                pref.edit().putString(Constant.ON_RIDE_STATUS,"0").commit();
                                changeButtonState();
                                buttonQueue.setVisibility(View.VISIBLE);
                                pref.edit().putString(Constant.IS_PREPAID_RIDE,"online").commit();
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor edit = pref.edit();
                                if(pref.getString(Constant.ACCEPT_RIDE_SECOND,"0").equals("1")) {
                                    Log.d(TAG,"setSecondNotiInCancel");
                                    //SharedPreferences.Editor edit= pref.edit();
                                    edit.putString(Constant.CUSTOMER_CONTACT,pref.getString(Constant.CUSTOMER_CONTACT_SECOND,"") );
                                    edit.putString(Constant.START_LOCATION_NAME, pref.getString(Constant.START_LOCATION_NAME_SECOND,""));
                                    edit.putString(Constant.END_LOCATION_NAME, pref.getString(Constant.END_LOCATION_NAME_SECOND,""));

                                    edit.putString(Constant.TRIP_ID, pref.getString(Constant.TRIP_ID_SECOND,""));
                                    edit.putString(Constant.NETPAY, pref.getString(Constant.NETPAY_SECOND,""));
                                    edit.putString(Constant.DISTANCE, pref.getString(Constant.DISTANCE_SECOND,""));
                                    edit.putString(Constant.TIME, pref.getString(Constant.TIME_SECOND,""));
                                    edit.putString(Constant.RIDETYPE, "online");
                                    edit.putString(Constant.ACCEPT_RIDE, "1");
                                    edit.putString(Constant.SESSION_ID,pref.getString(Constant.SESSION_ID_SECOND,"") );
                                    edit.putString(Constant.END_LAT,pref.getString(Constant.END_LAT_SECOND,""));
                                    edit.putString(Constant.END_LONG,pref.getString(Constant.END_LONG_SECOND,""));
                                    edit.putString(Constant.CUST_LAT,pref.getString(Constant.CUST_LAT_SECOND,""));
                                    edit.putString(Constant.CUST_LONG,pref.getString(Constant.CUST_LONG_SECOND,""));

                                    edit.putString(Constant.ACCEPT_RIDE_SECOND,"0");//reset it
                                    // edit.putInt(Constant.ACCEPTED_NOTIFICATION_ID, Integer.parseInt(intent.getString(Constant.NOTIFICATION_ID)));

                                    hideviewIfAcceptedNotificationRide();//if second ride is there then show related view to user to start ride


                                }else{

                                    edit.putString(Constant.CUSTOMER_CONTACT, "");
                                    edit.putString(Constant.START_LOCATION_NAME, "");
                                    edit.putString(Constant.END_LOCATION_NAME, "");
                                    edit.putString(Constant.TRIP_ID, "");
                                    edit.putString(Constant.NETPAY, "");
                                    edit.putString(Constant.DISTANCE, "");
                                    edit.putString(Constant.TIME, "");
                                    edit.putString(Constant.RIDETYPE, "");
                                    edit.putString(Constant.ACCEPT_RIDE,"0");

                                    resetMapView();
                                }

                                edit.putString(Constant.TRIP_TYPE,"static");
                                edit.commit();


                                /*linearOnlineBtn.setVisibility(View.GONE);
                                linear_sourceDest.setVisibility(View.GONE);

                                linear_placefragment.setVisibility(View.VISIBLE);
                                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                behavior.setPeekHeight(0);

                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
                                params.topMargin = -200;
                                if(marker_cust!=null){
                                    marker_cust.remove();
                                }
                                if(marker_dest!=null){
                                    marker_dest.remove();
                                }*/



                            }else {
                                //  mMap.clear();
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



    //Fetch distance and Fare using
    /**when driver rejct ride then he need to give reason why he is
     * cancelling the ride.. so this api fetch the reason from server and will show  in popup
     * */
    private void getCancelTripReson() {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {
            jsonObject1 = new JSONObject();

            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("language", language);

            // jsonObject1.put("bookingId",pref.getString(Constant.TRIP_ID,""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "cancelResonJson_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.CANCEL_REASON_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.CANCEL_REASON_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "cancelRequest_result" + result);
                            if (result.trim().equals("200")) {

                                cancelResonList.clear();

                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                Bean b1=new Bean();
                                b1.setReasonId("0");
                                b1.setReason(getResources().getString(R.string.selectreson));
                                cancelResonList.add(b1);

                                JSONArray jsonArray=response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    Bean b=new Bean();
                                    b.setReasonId(jsonObject.getString("id"));
                                    if(language.equals("en")) {
                                        b.setReason(jsonObject.getString("reason_eng"));
                                    }else {
                                        b.setReason(jsonObject.getString("reason_marathi"));
                                    }
                                    cancelResonList.add(b);
                                }

                                showCancelTripDialogBox();

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


    /**
     * when driver start ride then related data will send to server
     * and with session code and booking id
     * to insert started ride on server db and start the ride
     * */
    private void insertStartRide(String mob, final String source1, final String destination1, String session, String trip_type, String time, String netPay, String distance,String otp,String booking_id) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;
        final Bean b=new Bean();
        String isPrepaid=pref.getString(Constant.IS_PREPAID_RIDE,"");
        try {
            startLat= mCurrLocationMarker.getPosition().latitude+"";
            startLong= mCurrLocationMarker.getPosition().longitude+"";
            endLat= marker_dest.getPosition().latitude+"";
            endLong= marker_dest.getPosition().longitude+"";

            //start= mCurrLocationMarker.getPosition().latitude+"";
            //startLong= mCurrLocationMarker.getPosition().longitude+"";

            jsonObject1 = new JSONObject();
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("distance", distance);
            jsonObject1.put("duration", time);
            jsonObject1.put("amount", netPay);
            jsonObject1.put("session_code", session);
            if (isPrepaid.equals("prepaid")){
                jsonObject1.put("trip_type", "prepaid");
            }else{
                jsonObject1.put("trip_type", trip_type);
            }
            jsonObject1.put("pickup_location", source1);
            jsonObject1.put("drop_location", destination1);
            jsonObject1.put("mobile_no",mob );
            jsonObject1.put("slat",startLat+"");
            jsonObject1.put("slong", startLong+"");
            jsonObject1.put("dlat",marker_dest.getPosition().latitude+"");
            jsonObject1.put("dlong",marker_dest.getPosition().longitude+"");
            jsonObject1.put("otp",otp);
            jsonObject1.put("booking_id",booking_id);


            b.setDriverId(driverId);
            b.setDistance(distance);
            b.setDuration(time);
            b.setNetAmount(netPay);
            b.setSessionId(session);
            b.setTripType(trip_type);
            b.setPickupLocName(source1);
            b.setEndLocationName(destination1);
            b.setCustMobileNo(mob);
            b.setIsPrepaid("");
            b.setStartLat(startLat+"");
            b.setStartLong(startLong+"");
            b.setEndLat(marker_dest.getPosition().latitude+"");
            b.setEndLong(marker_dest.getPosition().longitude+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "STARTRIDE_URL" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.STARTRIDE_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.STARTRIDE_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "startRide_Submit_result" + result);
                            if (result.trim().equals("200")) {
                                desableQueueButton();
                                // disable que button
                                buttonQueue.setVisibility(View.GONE);
                                pref.edit().putString(Constant.ON_RIDE_STATUS,"1").commit();
                                 Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                 dialog1.dismiss();
                                Log.d(TAG,"StartRide--------1>"+pref.getInt(Constant.ISRIDE_START,0));
                                 JSONObject  jsonObject=response.getJSONObject("data");
                                 String tripId=jsonObject.getString("trip_id");
                                 b.setTripId(tripId);

                                Log.d(TAG,"StartRide--------2>"+pref.getInt(Constant.ISRIDE_START,0));
                                //THIS VALUE STORED IN SESSION
                                SharedPreferences.Editor edit= pref.edit();
                                edit.putInt(Constant.ISRIDE_START,1);
                                edit.putString(Constant.START_LAT,startLat+"");
                                edit.putString(Constant.START_LONG,startLong+"");
                                edit.putString(Constant.END_LAT,endLat+"");
                                edit.putString(Constant.END_LONG,endLong+"");
                                edit.putString(Constant.START_LOCATION_NAME,source1+"");
                                edit.putString(Constant.END_LOCATION_NAME,destination1+"");
                                edit.commit();
                                Log.d(TAG,"StartRide--------3>"+pref.getInt(Constant.ISRIDE_START,0));

                                 dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                 linearBtn.setVisibility(View.VISIBLE);
                                 linearOnlineBtn.setVisibility(View.GONE);
                                //txtNaviagtion.setVisibility(View.VISIBLE);
                                 btnnavigation.setVisibility(View.VISIBLE);

                                 behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                 behavior.setPeekHeight(0);
                                 rlToolbar.setVisibility(View.GONE);
                                ((MapActivity)getActivity()).lockDrawer();

                                if(marker_cust!=null){
                                    marker_cust.remove();
                                }
                                /**insert trip data into local db*/
                                 db.insertTripData(b);

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


    private void endTrip(final String source1, final String session, String time, String distance, String dlat, String dlong, String otp) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.endtriploader));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;




        try {

            jsonObject1 = new JSONObject();
            jsonObject1.put("driver_id", driverId);
            String isPrepaid=pref.getString(Constant.IS_PREPAID_RIDE,"");
            trip_type= pref.getString(Constant.TRIP_TYPE,"static");
            Log.d(TAG,"tripType: "+trip_type);
            //jsonObject1.put("distance", pref.getString(Constant.DISTANCE, ""));
            //tripType is dynamic then get distance from service
            if(trip_type.equals("static")||trip_type.equals("online")) {
                jsonObject1.put("distance", pref.getString(Constant.DISTANCE, ""));
            }else if(trip_type.equals("dynamic")){
                jsonObject1.put("distance", String.format("%.2f", Double.parseDouble(pref.getString(Constant.DYANAMIC_DISTANCE, "00")))+"");
            }

            jsonObject1.put("drop_location", source1);
            jsonObject1.put("location_id", LocationID);
            jsonObject1.put("slat", pref.getString(Constant.START_LAT,""));
            jsonObject1.put("slong", pref.getString(Constant.START_LONG,""));
            jsonObject1.put("dlat", mCurrLocationMarker.getPosition().latitude);
            jsonObject1.put("dlong",  mCurrLocationMarker.getPosition().longitude);
            jsonObject1.put("elat", pref.getString(Constant.END_LAT,""));
            jsonObject1.put("elong", pref.getString(Constant.END_LONG,""));
            jsonObject1.put("contact",  pref.getString(Constant.CUSTOMER_CONTACT,""));
            jsonObject1.put("session_id", pref.getString(Constant.SESSION_ID,""));
            jsonObject1.put("otp_no", otp);
            if (isPrepaid.equals("app")){
                jsonObject1.put("trip_type", "app");
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "EndRIDE_URL" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.ENDTRIP_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.ENDTRIP_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response: "+response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Log.d(MYTAG,"result1: "+result);

                            Log.d(TAG, "getCityList_Submit_result" + result);
                            if (result.equalsIgnoreCase("200")) {
                                Log.d(MYTAG,"Log.d(MYTAG,\"Response: \"+result);: "+result);
                                pref.edit().putString(Constant.ON_RIDE_STATUS,"0").commit();
                                changeButtonState();
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                if(dialog1!=null) {
                                    dialog1.dismiss();
                                }
                                linearBtn.setVisibility(View.VISIBLE);
                                //rlToolbar.setVisibility(View.VISIBLE);
                                ((MapActivity)getActivity()).unLockDrawer();

                                btnnavigation.setVisibility(View.INVISIBLE);

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                JSONObject jobj=  jsonArray.getJSONObject(i);

                                   String totalAmount=jobj.getString("amount");
                                   String totalDistance=jobj.getString("distance");
                                   String rideCharge=jobj.getString("distance_amount");
                                   String fee=jobj.getString("fees");
                                   Log.d(MYTAG,"totalAmount: "+totalAmount);

                                    SharedPreferences.Editor edit= pref.edit();
                                    edit.putInt(Constant.ISRIDE_START,2);
                                    edit.putString(Constant.TOTAL_AMOUNT,totalAmount+"");
                                    edit.putString(Constant.TOTAL_DISTANCE,totalDistance+"");
                                    edit.putString(Constant.DYANAMIC_DISTANCE,"0");
                                    edit.commit();

                                    db.updateTripTable_withEndTripData(pref.getString(Constant.SESSION_ID,session+""),2,totalAmount+"",totalDistance+"", mCurrLocationMarker.getPosition().latitude+"",mCurrLocationMarker.getPosition().longitude+"",source1,rideCharge,fee);

                                    //getActivity().onBackPressed();
                                    Intent ii=new Intent(getActivity(), RideSummary.class);
                                    ii.putExtra("totalAmount",totalAmount);
                                    ii.putExtra("totalDistance",totalDistance);
                                    startActivity(ii);
                                    getActivity().finish();


                                }

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



    private void resendOtp() {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.resendingOtp));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();
           // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("session_code", pref.getString(Constant.SESSION_ID,""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "resendotp_URL" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.RESEND_OTP_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.RESEND_OTP_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getResend_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=  jsonArray.getJSONObject(i);
                                    Log.d("resendOtp",jobj.getString("otp_no"));
                                }

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


    //This Function will invoke only when online ride notification come
    //And when driver accept then it invoke this fucntion and related data send to server.
    //tripType will be online
    private void sendOnlineAcceptDataToServer() {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;
        String isPrepaid=pref.getString(Constant.IS_PREPAID_RIDE,"");
        String URL = "";
        Log.d(TAG,"isprepaid: "+isPrepaid);
        if (isPrepaid.equals("prepaid")){

            URL =  Constant.TRIP_ACCEPT_URL_PREPAID;
            try {

                jsonObject1 = new JSONObject();
                // jsonObject1.put("driver_id", driverId);
                jsonObject1.put("driver_id", driverId);
                jsonObject1.put("trip_id",  pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("source_addr", pref.getString(Constant.START_LOCATION_NAME,""));
                jsonObject1.put("destination_addr",  pref.getString(Constant.END_LOCATION_NAME,""));
                jsonObject1.put("amount",  pref.getString(Constant.NETPAY,""));
                jsonObject1.put("duration",  pref.getString(Constant.TIME,""));
                jsonObject1.put("distance",  pref.getString(Constant.DISTANCE,""));
                jsonObject1.put("booking_id",  pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("trip_type",  "prepaid");
                jsonObject1.put("contact",  pref.getString(Constant.CUSTOMER_CONTACT_SECOND,""));
                jsonObject1.put("session_code",  pref.getString(Constant.SESSION_ID,"")+"");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (isPrepaid.equals("app")){

            URL =  Constant.TRIP_ACCEPT_URL;
            try {

                jsonObject1 = new JSONObject();
                // jsonObject1.put("driver_id", driverId);
                jsonObject1.put("driver_id", driverId);
                jsonObject1.put("trip_id",  pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("pickup", pref.getString(Constant.START_LOCATION_NAME,""));
                jsonObject1.put("drop",  pref.getString(Constant.END_LOCATION_NAME,""));
                jsonObject1.put("amount",  pref.getString(Constant.NETPAY,""));
                jsonObject1.put("duration",  pref.getString(Constant.TIME,""));
                jsonObject1.put("distance",  pref.getString(Constant.DISTANCE,""));
                jsonObject1.put("booking_id",  pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("trip_type",  "app");
                jsonObject1.put("contact",  pref.getString(Constant.CUSTOMER_CONTACT,""));
                jsonObject1.put("session_code",  pref.getString(Constant.SESSION_ID,"")+"");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            URL =  Constant.TRIP_ACCEPT_URL;
            try {

                jsonObject1 = new JSONObject();
                // jsonObject1.put("driver_id", driverId);
                jsonObject1.put("driver_id", driverId);
                jsonObject1.put("trip_id",  pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("pickup", pref.getString(Constant.START_LOCATION_NAME,""));
                jsonObject1.put("drop",  pref.getString(Constant.END_LOCATION_NAME,""));
                jsonObject1.put("amount",  pref.getString(Constant.NETPAY,""));
                jsonObject1.put("duration",  pref.getString(Constant.TIME,""));
                jsonObject1.put("distance",  pref.getString(Constant.DISTANCE,""));
                jsonObject1.put("booking_id",  pref.getString(Constant.TRIP_ID,""));
                jsonObject1.put("trip_type",  "online");
                jsonObject1.put("contact",  pref.getString(Constant.CUSTOMER_CONTACT,""));
                jsonObject1.put("session_code",  pref.getString(Constant.SESSION_ID,"")+"");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }





        // jsonObject1.put("filename",);
        Log.d(TAG, "TRIP_ACCEPT_URL->>>>>: " + jsonObject1.toString());

        Log.d(TAG, "URl->>>>>: " + URL);
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response: "+response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "TRIP_ACCEPT_URL_Submit_result->>>>>" + result);
                            if (result.trim().equals("200")) {
                                    buttonQueue.setVisibility(View.GONE);
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                ((MapActivity)getActivity()).showSnackBar(msg);

                                String slat = "",slong="",dlat="",dlong="";
                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int j=0;j<jsonArray.length();j++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                                    distance = jsonObject.getString("distance");
                                    time = jsonObject.getString("time");
                                    netPay = jsonObject.getString("netPay");
                                    session = jsonObject.getString("session_code");
                                    trip_type = jsonObject.getString("trip_type");
                                    slat = jsonObject.getString("slat");
                                    slong = jsonObject.getString("slong");
                                    dlat = jsonObject.getString("dlat");
                                    dlong = jsonObject.getString("dlong");

                                }

                                //show online button linear layou,call,startride,endride

                                SharedPreferences.Editor edit= pref.edit();
                                edit.putString(Constant.SESSION_ID,session);
                                edit.putString(Constant.NETPAY,netPay);
                                edit.putString(Constant.TIME,time);
                                edit.putString(Constant.DISTANCE,distance);
                                edit.putString(Constant.ACCEPT_RIDE,"1");
                                edit.putString(Constant.END_LAT,dlat);
                                edit.putString(Constant.END_LONG,dlong);
                                edit.putString(Constant.CUST_LAT,slat);
                                edit.putString(Constant.CUST_LONG,slong);
                                edit.commit();

                                Log.d("OnlineLatLog","Source:"+slat+","+slong+" dest:"+dlat+","+dlong);

                                if(marker_dest!=null){
                                    marker_dest.remove();
                                }
                                LatLng endLatLong=new LatLng(Double.parseDouble(dlat), Double.parseDouble(dlong));
                                marker_dest = mMap.addMarker(new MarkerOptions().position(endLatLong).title(pref.getString(Constant.END_LOCATION_NAME,"")+"").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(endLatLong));
                                Log.d(MYTAG,"dlat: "+dlat+", dlong: "+dlong);

                                LatLng custLatLong=new LatLng(Double.parseDouble(slat), Double.parseDouble(slong));
                                marker_cust = mMap.addMarker(new MarkerOptions().position(custLatLong).title(pref.getString(Constant.START_LOCATION_NAME,"")+"").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(custLatLong));

                                buttonQueue.setVisibility(View.GONE);
                                linearOnlineBtn.setVisibility(View.VISIBLE);
                                linear_sourceDest.setVisibility(View.VISIBLE);
                                btnnavigation.setVisibility(View.VISIBLE);
                                rlToolbar.setVisibility(View.GONE);
                                ((MapActivity)getActivity()).lockDrawer();
                                //slideDown(linearLayout);
                                edtSource.setText(pref.getString(Constant.START_LOCATION_NAME,""));
                                edtDest.setText(pref.getString(Constant.END_LOCATION_NAME,""));
                                Log.d(MYTAG,"EndLocationName: "+pref.getString(Constant.END_LOCATION_NAME,""));
                                linear_placefragment.setVisibility(View.GONE);
                                mAutocompleteTextView.setText(pref.getString(Constant.END_LOCATION_NAME,""));



                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
                                params.topMargin = 200;

                                showMapZoomToMarker();
                                disableNotification();//hide notification

                            }else {
                                //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

               // parseVolleyError( error);
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        String msg = obj.getString("message");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        ((MapActivity)getActivity()).showSnackBar(msg);
                        JSONObject jobj=obj.getJSONObject("data");
                        for(int i=0;i<jobj.length();i++){
                            String status=jobj.getString("status");
                            if(status.equals("1")){

                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString(Constant.CUSTOMER_CONTACT, "");
                                edit.putString(Constant.START_LOCATION_NAME, "");
                                edit.putString(Constant.END_LOCATION_NAME, "");
                                edit.putString(Constant.TRIP_ID, "");
                                edit.putString(Constant.NETPAY, "");
                                edit.putString(Constant.DISTANCE, "");
                                edit.putString(Constant.TIME, "");
                                edit.putString(Constant.RIDETYPE, "");
                                edit.putString(Constant.ACCEPT_RIDE,"0");
                                edit.commit();


                            }else  if(status.equals("0")){//

                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString(Constant.CUSTOMER_CONTACT, "");
                                edit.putString(Constant.START_LOCATION_NAME, "");
                                edit.putString(Constant.END_LOCATION_NAME, "");
                                edit.putString(Constant.TRIP_ID, "");
                                edit.putString(Constant.NETPAY, "");
                                edit.putString(Constant.DISTANCE, "");
                                edit.putString(Constant.TIME, "");
                                edit.putString(Constant.RIDETYPE, "");
                                edit.putString(Constant.ACCEPT_RIDE,"0");
                                edit.commit();

                               disableNotification();
                            }

                            resetMapView();

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
                    Toast.makeText(getContext(), R.string.networkproblem, Toast.LENGTH_SHORT).show();
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.networkproblem));
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


    public void parseVolleyError(VolleyError error) {
        try {


              if (error instanceof ParseError) {
                // Indicates that the server response could not be parsed
                  Log.d("Error->>>>","parseError");
            }

            String responseBody = new String(error.networkResponse.data, "UTF-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
           Log.d("Error->>>>","AcceptRide->"+message);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    private void resetMapView() {

        linearOnlineBtn.setVisibility(View.GONE);
        linear_sourceDest.setVisibility(View.GONE);
        btnnavigation.setVisibility(View.GONE);
        linear_placefragment.setVisibility(View.VISIBLE);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        behavior.setPeekHeight(0);
        //btnnavigation.setVisibility(View.GONE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
        params.topMargin = -200;
        if(marker_cust!=null){
            marker_cust.remove();
        }
        if(marker_dest!=null){
            marker_dest.remove();
        }

        String tripType=pref.getString(Constant.TRIP_TYPE,"static");
        if(tripType.equals("static")){
            changeColorOfBtn(btnfixRoute,btnDynamicRoute);

        }else if(tripType.equals("dynamic")){
            changeColorOfBtn(btnDynamicRoute,btnfixRoute);
        }

    }

    private void disableNotification() {
        int notificationId=pref.getInt(Constant.ACCEPTED_NOTIFICATION_ID,0);
        Log.d(TAG,"Notificationid:"+notificationId);

        NotificationManager notificationmanager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        //notificationmanager.cancel(0);
        notificationmanager.cancelAll();
        //notificationmanager.cancel(notificationId);
    }


    private void showMapZoomToMarker() {


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //the include method will calculate the min and max bound.
        builder.include(mCurrLocationMarker.getPosition());

        if(pref.getString(Constant.RIDETYPE,"").equals("online")) {

            if(pref.getInt(Constant.ISRIDE_START,0)==1) {
                builder.include(marker_dest.getPosition());
            }else {
                builder.include(marker_cust.getPosition());
            }
        }else {
            builder.include(marker_dest.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels/2;
        int height = getResources().getDisplayMetrics().heightPixels/2;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }

    private void showPolygonLine() {

        PolylineOptions line=
                new PolylineOptions().add(new LatLng(mCurrLocationMarker.getPosition().latitude,
                                mCurrLocationMarker.getPosition().longitude),
                        new LatLng(marker_dest.getPosition().latitude,
                                marker_dest.getPosition().longitude))
                        .width(8).color(Color.BLUE);

        mMap.addPolyline(line);

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"LifecycleMethodOfFragmetn: onStop()");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        //unregisterReceiver(gpsReceiver);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            Log.i(TAG, "mGoogleApiClient.connect()");
        }

        /*if (mapFragment != null)
            mapFragment.onStop();*/
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

       /* if (mapFragment != null)
            mapFragment.onSaveInstanceState(outState);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       //getActivity().unregisterReceiver(mMessageReceiver);
        /*if (mapFragment != null)
            mapFragment.onDestroy();*/

        if(getaddressAsync!=null) {
            getaddressAsync.cancel(true);
            getaddressAsync=null;
        }

        Log.d(TAG,"LifecycleMethodOfFragmetn: onDestroy()");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"LifecycleMethodOfFragmetn: onAttach()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"LifecycleMethodOfFragmetn: onDetach()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"LifecycleMethodOfFragmetn:onActivityCreated()");
    }



    private void showDialogBox() {


        dialog1 = new Dialog(getContext());
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.customer_dialogue);

        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

       /* Toolbar toolbar = (Toolbar) dialog1.findViewById(R.id.toolbar);
        toolbar.setTitle("");*/


        Button btn_submit = dialog1.findViewById(R.id.btn_submit);
        //Button btn_cancel =  dialog1.findViewById(R.id.btn_cancel);
        edtMob=dialog1.findViewById(R.id.edtMob);
        ImageView imgBtn= (ImageView) dialog1.findViewById(R.id.imgclose);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constant.hideKeyboardFrom1(getActivity(),edtMob);
                Constant.hideKeyboardFrom(getActivity());
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                Log.d("sss","ssssss");
                dialog1.dismiss();

            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (DetectConnection.checkInternetConnection(getContext())) {
                    if(edtMob.getText().toString().trim()!="") {
                        if(edtMob.getText().toString().length()>9) {
                            dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                            Constant.hideKeyboardFrom1(getActivity(),edtMob);

                            locationMode = Constant.getLocationMode(getContext());
                            if (Constant.isgpsAvailable(getContext())&&locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {


                                    insertStartRide(edtMob.getText().toString().trim(), source, destination, session, trip_type, time, netPay, distance,"","" );


                            }else {

                                checkLocationSetting();
                                Toast.makeText(getContext(),R.string.noGps,Toast.LENGTH_SHORT).show();
                                ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noGps));
                            }

                        }else{
                            Toast.makeText(getContext(), R.string.mobilenumbershouldbe, Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(getContext(), R.string.enterotpcode, Toast.LENGTH_LONG).show();
                    }

                } else {

                    Toast.makeText(getContext(), getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));
                }

            }
        });



        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog1.getWindow().setAttributes(lp);
        //dialog1.setCancelable(false);
        dialog1.show();

    }


    private void showCancelTripDialogBox() {


        dialog1 = new Dialog(getContext());
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.cancel_dialogue);

        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        addItemsOnSpinner(dialog1);
        Button btn_submit = dialog1.findViewById(R.id.btn_submit);
        ImageView imgBtn=  dialog1.findViewById(R.id.imgclose);


        spinner_reson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                reasonId=cancelResonList.get(i).getResonId();
                Log.d(TAG,"resonId:"+reasonId);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Constant.hideKeyboardFrom1(getActivity(),edtMob);
               // Constant.hideKeyboardFrom(getActivity());
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                dialog1.dismiss();

            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(DetectConnection.checkInternetConnection(getContext())){
                    if(!reasonId.equals("0")) {
                        cancelTripRequest();
                        dialog1.dismiss();
                    }else{
                        Toast.makeText(getContext(),R.string.pleaseselectreson,Toast.LENGTH_SHORT).show();
                    }


                }else {
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));
                }


            }
        });




        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog1.getWindow().setAttributes(lp);
        //dialog1.setCancelable(false);
        dialog1.show();

    }


    // add items into spinner dynadmically
    public void addItemsOnSpinner(Dialog dialog1) {

        spinner_reson= (Spinner) dialog1.findViewById(R.id.spinner_reason);
        adapter = new CustomAdapter(getActivity(), R.layout.spinner_row,cancelResonList,"reason");
       // spinner_reson.setEnabled(false);
       // spinner_reson.setClickable(false);
        // Set adapter to spinner
        spinner_reson.setAdapter(adapter);
    }


    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
               // .addApi(Places.GEO_DATA_API)
                .enableAutoManage((FragmentActivity) getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        Log.d(TAG,"LifecycleMethodOfFragmetn: onPause()");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity) getActivity());
            mGoogleApiClient.disconnect();
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
      //  mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

        Log.i(TAG, "GoogleApiClient connected!");
        String tripType=pref.getString(Constant.TRIP_TYPE,"static");
        if(tripType.equals("static")){
            createLocationRequest();
        }else if(tripType.equals("dynamic")){

        }



       // initialiseMap();

    }

    protected void createLocationRequest() {

        locationRequest();
        locationRequestOfOneMinute();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if(mGoogleApiClient!=null) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    Log.i(TAG,"onLocationResult111"+ locationResult + "");
                    Log.i(TAG,"onLocationResult"+ locationResult.getLastLocation().getLatitude() + ","
                            +locationResult.getLastLocation().getLongitude()+","+locationResult.getLastLocation().getAccuracy());
                    Location location = locationResult.getLastLocation();

                    if(location.getAccuracy()<50) {
                        /** set marker and view */
                        setMarkerAndView(location);

                    }else {
                        if(mCurrLocationMarker!=null)
                            mCurrLocationMarker.remove();
                    }





                    float[] dist = new float[1];
                    NumberFormat f = NumberFormat.getInstance(); // Gets a NumberFormat with the default locale, you can specify a Locale as first parameter (like Locale.FRENCH)

                        double _lat = 0; double _long = 0;
                        try {
                            _lat = f.parse(pref.getString("lat","")).doubleValue();
                            _long = f.parse(pref.getString("long","")).doubleValue();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //double _lat = Double.parseDouble(pref.getString("lat",""));
                        //double _long = Double.parseDouble(pref.getString("long",""));

                        Log.d(MYTAG,"lat long : "+_lat+","+_long);

                        Location.distanceBetween(_lat,_long,locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude(),dist);
                        Log.d(MYTAG,"dist[0] : "+dist[0]);
                        if(dist[0]>300){
                            Log.d(MYTAG,"dist[0] : "+dist[0]);
                            //Toast.makeText(getActivity(), "Distance Covered"+String.valueOf(dist[0]), Toast.LENGTH_LONG).show();
                            //here your code or alert box for outside 1Km radius area
                            dequeueDriver();
                           /* if (pref.getString(Constant.QUEUE_STATUS,"").equals("1")){

                            }*/

                        }

                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    Log.i(TAG, "onLocationAvailability: isLocationAvailable =  " + locationAvailability.isLocationAvailable());
                }
            }, null);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
            mFusedLocationClient.requestLocationUpdates(mLocationRequestOfoneMin, mLocationCallback, Looper.myLooper());

        }
    }

    private void locationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void locationRequestOfOneMinute() {

        mLocationRequestOfoneMin = new LocationRequest();
        mLocationRequestOfoneMin.setInterval(40000);
        mLocationRequestOfoneMin.setFastestInterval(20000);
        mLocationRequestOfoneMin.setSmallestDisplacement(1*1000);//1 km
        mLocationRequestOfoneMin.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private Location getLastLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        mlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return mlocation;

    }

    @Override
    public void onConnectionSuspended(int i) {
       // mPlaceArrayAdapter.setGoogleApiClient(null);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"LifecycleMethodOfFragmetn: onStart()");

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.i(TAG, "mGoogleApiClient.connect()");
        }

    }

    @Override
    public void onLocationChanged(Location location) {

       // Log.i(TAG, "locationChanged " + location.getLongitude() + " ");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }



        /*markerOptions.position(new LatLng(18.528597,  73.873497));//default set to pune
        markerOptions.title("Pune Station");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));*/


        mMap.setMyLocationEnabled(true);

        changePostionButton();

        if (mMap != null) {

            mMap.setMyLocationEnabled(true);


            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub

                    Log.d(TAG,"onMapReady "+":"+arg0.getLatitude()+" long:"+arg0.getLongitude()+" acc"+arg0.getAccuracy());

                   // if(arg0.getAccuracy()<50) {
                            progressBar_linear.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            setMarkerAndView(arg0);

                    /*}else{
                        if(mCurrLocationMarker!=null)
                            mCurrLocationMarker.remove();
                    }*/

                }
            });


            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
                @Override
                public boolean onMyLocationButtonClick()
                {
                    //TODO: Any custom actions
                    setLocationToHighAccuracy();
                    return false;
                }
            });


            /**  Constant.ISRIDE_START=0 means Ride not started
             * Constant.ISRIDE_START=1 means Ride started and thre source and destination stored in sharedprefe
             *
             * */
            Log.d(TAG,"ISRIDESTARTED:"+pref.getInt(Constant.ISRIDE_START,0)+"");
            //This will execute after Map initialised.. onmapready will execute sometimes after resume or before resume
            // so mainintan view to visible or hide in onmapready and resume function
            if(pref.getInt(Constant.ISRIDE_START,0)==1){

                Log.d("mytag","ISRIDE_START: "+pref.getInt(Constant.ISRIDE_START, 0));

                linear_sourceDest.setVisibility(View.VISIBLE);
                //slideDown(linearLayout);//visible
                btnnavigation.setVisibility(View.VISIBLE);
                linear_placefragment.setVisibility(View.GONE);
                linearBtn.setVisibility(View.VISIBLE);
                linearOnlineBtn.setVisibility(View.GONE);


                //rlToolbar.setVisibility(View.GONE);
                ((MapActivity)getActivity()).lockDrawer();

                edtSource.setText(pref.getString(Constant.START_LOCATION_NAME,""));
                Log.d("HomeFragment"," Saved_destination:"+pref.getString(Constant.END_LOCATION_NAME,"")+"");
                String endLocation=pref.getString(Constant.END_LOCATION_NAME,"");
                edtDest.setText(endLocation+"");
                mAutocompleteTextView.setText(pref.getString(Constant.END_LOCATION_NAME,"")+"");


                if(marker_dest!=null){
                    marker_dest.remove();
                }

                LatLng endLatLong=new LatLng(Double.parseDouble(pref.getString(Constant.END_LAT,"18.5204")), Double.parseDouble(pref.getString(Constant.END_LONG,"73.8567")));
                marker_dest = mMap.addMarker(new MarkerOptions().position(endLatLong).title(endLocation+"").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(endLatLong));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endLatLong, 16));
               // showPolygonLine();
                if(mCurrLocationMarker!=null) {
                    showMapZoomToMarker();
                }

            }else if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("1"))
            {
                //if online notification accepted but yet not started ride then when user
                //came back to app from backgroubnd then hide view and show related view
                hideviewIfAcceptedNotificationRide();

                if(marker_dest!=null){
                    marker_dest.remove();
                }

                if(marker_cust!=null){
                    marker_cust.remove();
                }
                LatLng endLatLong= null;
                try {
                    endLatLong = new LatLng(Double.parseDouble(pref.getString(Constant.END_LAT,"")), Double.parseDouble(pref.getString(Constant.END_LONG,"")));
                    marker_dest = mMap.addMarker(new MarkerOptions().position(endLatLong).title(pref.getString(Constant.END_LOCATION_NAME,"")+"").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    LatLng custLatLong=new LatLng(Double.parseDouble(pref.getString(Constant.CUST_LAT,"")), Double.parseDouble(pref.getString(Constant.CUST_LONG,"")));
                    marker_cust = mMap.addMarker(new MarkerOptions().position(custLatLong).title(pref.getString(Constant.START_LOCATION_NAME,"")+"").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                //mMap.moveCamera(CameraUpdateFactory.newLatLng(endLatLong));
                if(mCurrLocationMarker!=null) {
                    showMapZoomToMarker();
                }

            }

        }



    }

    private void setMarkerAndView(Location arg0) {

       // Log.d(TAG,"onMyLocationChange "+"Location:"+arg0.getLongitude());
        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.remove();
        }
        currentLat = arg0.getLatitude();
        currentLong = arg0.getLongitude();

        markerOptions.position(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //Log.d(TAG,"LocationAccuracy"+" lat"+arg0.getLatitude()+","+arg0.getLatitude()+" "+arg0.getAccuracy());


        if(!oneTime) {

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 16));
            oneTime=true;
            Log.d(TAG,"OneTime:-"+oneTime);

            //set placegooglevisible only when we get location...don't show searchplaceview until we get location
            linear_placefragment.setVisibility(View.VISIBLE);
            //hide placegoogle fragment if ride already started
            if(pref.getInt(Constant.ISRIDE_START,0)==1){
                linear_placefragment.setVisibility(View.GONE);

            }else if(pref.getString(Constant.RIDETYPE,"").equals("online")){// if ride not started__bt it is online state

                if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("1")) {
                    linear_placefragment.setVisibility(View.GONE);
                    linearOnlineBtn.setVisibility(View.VISIBLE);
                }
            }

        }

        if (DetectConnection.checkInternetConnection(getContext())) {
            if(getaddressAsync!=null) {
                getaddressAsync.cancel(true);
                getaddressAsync=null;
            }
            /** get address name from lat and long */
            getaddressAsync=   new  GetAddressAsyncTask1(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
            getaddressAsync.execute();

        } else {
           // ((MapActivity)getActivity()).showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
        }

        mLastLocation=arg0;

    }

    private void changePostionButton() {

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        // and next place it, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
       // layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 220);
       // layoutParams.setMargins(0,200,

    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"LifecycleMethodOfFragmetn: onResume()");


        ((MapActivity)getActivity()).loadLocale();
        setLocationToHighAccuracy();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();//hide activity toolbar
        //check gps and permission
        locationMode = Constant.getLocationMode(getContext());
        Log.d(TAG,"RideType:"+pref.getString(Constant.RIDETYPE,"").equals("online"));

        if(Constant.isgpsAvailable(getContext())){
            //just check
            Location location1=((MapActivity)getActivity()).getLast_Location();//get location from LocationService bind
            if(location1!=null) {
                Log.d("lastLocation", location1.getLatitude() + "," + location1.getLongitude());
            }
            }

        if(!pref.getString(Constant.RIDETYPE,"").equals("online")) {

            //if value is 2 means ride started and ended.. but payment not done yet so naviagate to the ridesummary
            if (pref.getInt(Constant.ISRIDE_START, 0) == 2) {
                Intent ii = new Intent(getActivity(), RideSummary.class);
                ii.putExtra("totalAmount", pref.getString(Constant.TOTAL_AMOUNT, ""));
                ii.putExtra("totalDistance", pref.getString(Constant.TOTAL_DISTANCE, ""));
                startActivity(ii);
            }

            if(pref.getInt(Constant.ISRIDE_START, 0) == 1){
                if(mCurrLocationMarker!=null) {
                    showMapZoomToMarker();
                }
            }

            //change the color of btn and state of ride.. mean fixed or dynamic
            String tripType=pref.getString(Constant.TRIP_TYPE,"static");
            if(tripType.equals("static")){
                changeColorOfBtn(btnfixRoute,btnDynamicRoute);

            }else if(tripType.equals("dynamic")){
                changeColorOfBtn(btnDynamicRoute,btnfixRoute);
            }

            //if source and destination fragment is showing then hide bottome sheet if visible
            /*if (ll.getVisibility() == View.VISIBLE) {

                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                behavior.setPeekHeight(0);

            } else {

                if (pref.getInt(Constant.ISRIDE_START, 0) == 0) {

                    // behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }*/

        }else {

            /** Online Notification Section */
            //if value is 2 means ride trip has completed but payment not done yet so naviagate to the ridesummary
            if (pref.getInt(Constant.ISRIDE_START, 0) == 2) {

                Intent ii = new Intent(getActivity(), RideSummary.class);
                ii.putExtra("totalAmount", pref.getString(Constant.TOTAL_AMOUNT, ""));
                ii.putExtra("totalDistance", pref.getString(Constant.TOTAL_DISTANCE, ""));
                startActivity(ii);

            }else if(pref.getInt(Constant.ISRIDE_START,0)==0){


                        if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("0")) {
                            Log.d(TAG,"accepted");
                            sendOnlineAcceptDataToServer();

                        }else if(pref.getInt(Constant.ISRIDE_START,0)==0){
                            Log.d(TAG,"ridestarted");
                                    if(pref.getString(Constant.ACCEPT_RIDE,"0").equals("1")){
                                        Log.d(TAG,"hideornot");
                                        hideviewIfAcceptedNotificationRide();
                                    }

                         }

                  }

        }

        //if source and destination fragment is showing then hide bottome sheet if visible
        if (linear_placefragment.getVisibility() == View.VISIBLE) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            behavior.setPeekHeight(0);
        } else {
            if (pref.getInt(Constant.ISRIDE_START, 0) == 0) {
                // behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

    }

    private void hideviewIfAcceptedNotificationRide() {

        linear_sourceDest.setVisibility(View.VISIBLE);
        //rlToolbar.setVisibility(View.GONE);
        ((MapActivity)getActivity()).lockDrawer();//lock naviagtion drawer
        //slideDown(linearLayout);
        edtSource.setText(pref.getString(Constant.START_LOCATION_NAME,""));
        edtDest.setText(pref.getString(Constant.END_LOCATION_NAME,""));
        linear_placefragment.setVisibility(View.GONE);
        mAutocompleteTextView.setText(pref.getString(Constant.END_LOCATION_NAME,""));
        btnnavigation.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.findViewById(R.id.linear_map).getLayoutParams();
        params.topMargin = 200;

    }


    private void showDialogBox1() {


        dialog1 = new Dialog(getContext());
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.otp);

        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) dialog1.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.verifyotp);


        Button btn_submit = dialog1.findViewById(R.id.btn_submit);
        Button btn_cancel =  dialog1.findViewById(R.id.btn_cancel);
        final EditText edtOtp=dialog1.findViewById(R.id.edtotp);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (DetectConnection.checkInternetConnection(getContext())) {

                    if(!edtOtp.getText().toString().trim().equals("")) {
                        if(edtOtp.length()>3) {

                        locationMode = Constant.getLocationMode(getContext());
                        if (Constant.isgpsAvailable(getContext())&&locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                            Constant.hideKeyboardFrom1(getContext(),edtOtp);
                            endTrip(source, session, time, distance, mCurrLocationMarker.getPosition().latitude + "", mCurrLocationMarker.getPosition().longitude + "", edtOtp.getText().toString().trim());

                        }else {
                            checkLocationSetting();

                            Toast.makeText(getContext(),R.string.noGps,Toast.LENGTH_SHORT).show();
                        }

                        }else {
                            Toast.makeText(getContext(), R.string.pleaseentervalidotp, Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(getContext(),R.string.otp,Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getContext(), getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));

                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Constant.hideKeyboardFrom(getActivity());
                Constant.hideKeyboardFrom1(getContext(),edtOtp);
                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
                // sendOtpVerification();
                dialog1.dismiss();


            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog1.getWindow().setAttributes(lp);
        dialog1.setCancelable(false);
        dialog1.show();

    }


    private void showOTP_DialogBox() {

        dialog1 = new Dialog(getContext());
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.otp);

        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) dialog1.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.verifyotp);

        Button btn_submit = dialog1.findViewById(R.id.btn_submit);
        Button btn_cancel =  dialog1.findViewById(R.id.btn_cancel);
        final EditText edtOtp=dialog1.findViewById(R.id.edtotp);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog1.dismiss();
                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
                // sendOtpVerification();
                Constant.hideKeyboardFrom1(getContext(),edtOtp);
                if (DetectConnection.checkInternetConnection(getContext())) {

                    if(!edtOtp.getText().toString().trim().equals("")) {
                        if(edtOtp.length()>3) {

                            locationMode = Constant.getLocationMode(getContext());
                            if (Constant.isgpsAvailable(getContext())&&locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                                trip_type="online";
                                setTripTypeInLocal(trip_type);

                                    insertStartRide(pref.getString(Constant.CUSTOMER_CONTACT,""),
                                            pref.getString(Constant.START_LOCATION_NAME,""),
                                            pref.getString(Constant.END_LOCATION_NAME,""),
                                            pref.getString(Constant.SESSION_ID,""),
                                            trip_type, pref.getString(Constant.TIME,""),
                                            pref.getString(Constant.NETPAY,""),
                                            pref.getString(Constant.DISTANCE,""),
                                            edtOtp.getText().toString().trim(),pref.getString(Constant.TRIP_ID,""));

                            }else {
                                checkLocationSetting();
                                Toast.makeText(getContext(),R.string.noGps,Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(getContext(), R.string.pleaseentervalidotp, Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(getContext(),R.string.otp,Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getContext(), getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
                    ((MapActivity)getActivity()).showSnackBar(getResources().getString(R.string.noInternet));

                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constant.hideKeyboardFrom(getActivity());
                Constant.hideKeyboardFrom1(getContext(),edtOtp);
                //startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
                // sendOtpVerification();
                dialog1.dismiss();

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog1.getWindow().setAttributes(lp);
        dialog1.setCancelable(false);
        dialog1.show();

    }


    private void showDialogBoxPermission() {

        dialog2.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog2.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) dialog2.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.turnonlocation);

        Button btn_submit = dialog2.findViewById(R.id.btn_submit);

        //final EditText edtOtp=dialog1.findViewById(R.id.edtotp);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLocationSetting();

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog2.getWindow().setAttributes(lp);
        dialog2.setCancelable(false);
        dialog2.show();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        Log.d(TAG,"LifecycleMethodOfFragment: onCreate()");
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }

}
