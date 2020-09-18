package com.tretakalp.Rikshaapp.Activity;

/**
 * Created by Mac on 04/09/18.
 * In this page we added navigation drawer.
 * use fragement
 * started location service
 * bind location service in activity using service connection
 * use bind locationservice to stop fusedapiclient from getting location on stop methode
 */

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tretakalp.Rikshaapp.Adpter.ReportIssueAdapter;
import com.tretakalp.Rikshaapp.BuildConfig;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Fragment.AboutFragment;
import com.tretakalp.Rikshaapp.Fragment.AccountFragment;
import com.tretakalp.Rikshaapp.Fragment.DemoFragment;
import com.tretakalp.Rikshaapp.Fragment.EarningHistoryMenuFr;
import com.tretakalp.Rikshaapp.Fragment.HelpFragment;
import com.tretakalp.Rikshaapp.Fragment.HomeFragment;
import com.tretakalp.Rikshaapp.Fragment.IncentiveAndNotification;
import com.tretakalp.Rikshaapp.Fragment.LegalFragment;
import com.tretakalp.Rikshaapp.Fragment.PaymentDetailFragment;
import com.tretakalp.Rikshaapp.Fragment.ReferEarnFragment;
import com.tretakalp.Rikshaapp.Fragment.ReportIssueFragment;
import com.tretakalp.Rikshaapp.Fragment.TripHistory;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;
import com.tretakalp.Rikshaapp.Service.LocationService;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;




public class MapActivity extends AppCompatActivity implements ReportIssueAdapter.ItemClickListener /*, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener*/ {


    public static final int RequestPermissionCode = 1;
    final static int REQUEST_LOCATION = 199;
    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String TAG_INCENTIVE_AND_NOTI ="incentive_and_notification" ;
    private static  String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";
    // private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PROFILE = "profile";
    // private FloatingActionButton fab;
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG = "MapActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 69;
    private static final String TAG_LEGAL ="Legal" ;
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    Button btnfixRoute, btnDynamicRoute;
    SharedPreferences pref;
    TextView txt_username, txt_inOutTime;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList<Bean> notificationList=new ArrayList<>();
    private static final String MYTAG = "mytag";

    MarkerOptions markerOptions = new MarkerOptions();
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    //private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private GoogleMap mMap;
    String mobileNo,driverName;
    TextView txtMob,txtDriverName;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    boolean mBounded;
    LocationService mServer;
    CoordinatorLayout coordinatorLayout;
    DatabaseHelper db;
    SharedPreferences pref_firebase;
    String firebaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(Color.TRANSPARENT);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);

        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        driverName=pref.getString(Constant.DRIVER_NAME,"");
        urlProfileImg=pref.getString(Constant.PROFILE_PATH,"");
        mobileNo=pref.getString(Constant.MOBILE_NO,"");
        db=DatabaseHelper.getInstance(this);
        //db.deleteAllNotificationData();

        Log.d(TAG,"onCreate");
        Log.d("urLProfile",urlProfileImg);
        Log.d("urLProfile",driverName);
        Log.d("urLProfile",mobileNo);
        coordinatorLayout=findViewById(R.id.cordinate);


        pref_firebase = getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
        firebaseId= pref_firebase.getString("regId","");
        //firebaseId= null;
        Log.d(TAG,"firebaseId: "+firebaseId);

        if(firebaseId==null) {
            try {
                if(DetectConnection.checkInternetConnection(this)) {

                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    storeRegIdInPref(refreshedToken);
                    sendFirebaseKey(refreshedToken);//send firebase to server
                    Log.d("Firbase id login1", "Refreshed token: " + refreshedToken);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {

            // sendFirebaseKey(firebaseId);//send firebase to server
        }



        mHandler = new Handler();

        drawer =  findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);



        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtDriverName=navHeader.findViewById(R.id.txtdriver_name);
        txtMob=navHeader.findViewById(R.id.txtMob);

        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile =  navHeader.findViewById(R.id.img_profile);

        btnfixRoute = findViewById(R.id.btnfixRoute);
        btnDynamicRoute = findViewById(R.id.btndynamicRoute);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();

        }


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearbackStack();
                startActivity(new Intent(MapActivity.this, ProfileActivity.class));
                drawer.closeDrawers();
            }
        });


    }

    private void clearNotification() {
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancelAll();
        Log.d(TAG,"NotificationCancel");
    }

    /**Store the firebase key in sharedprefe */
    private void storeRegIdInPref(String refreshedToken) {
        SharedPreferences pref = getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", refreshedToken);
        editor.commit();

        firebaseId=refreshedToken;
    }

    /**Show snackbar  */
    public void showSnackBar(String message) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    public void showSnackBarForNoInternet(String message) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(DetectConnection.checkInternetConnection(MapActivity.this)) {
                            unLockDrawer();
                        }else {
                            lockDrawer();
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




    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        //txtName.setText("Rahul Patel");
        //txtWebsite.setText("9856234512");

        // loading header background image
        /*Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);*/

        // Loading profile image

        txtDriverName.setText(driverName+"");
        txtMob.setText(mobileNo+"");

        /*Glide.with(this).load(urlProfileImg)
                //.crossFade()
                //.thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                //.bitmapTransform(new CircleTransform(this))
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.profile)
                .into(imgProfile);*/

       /* Glide.with(this)
                .load(urlProfileImg)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imgProfile.setImageBitmap(resource);
                        //encodeBitmapImage(resource);
                    }
                });*/

        Glide.with(MapActivity.this).
                load(urlProfileImg.trim())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.profile).
                dontAnimate().into(imgProfile);


       /* Picasso.with(this).
                load(urlProfileImg)
                //.centerCrop()
                //.resize(600, 200)
                //.fit()
                //.centerInside()
                .placeholder(R.drawable.profile)
                .into(imgProfile, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // Image is loaded successfully...
                        Log.d("piasso","Success");
                    }

                    @Override
                    public void onError() {
                        // Unable to load image, may be due to incorrect URL, no network...
                        //e.printStackTrace();
                        Log.d("piasso","Error");
                    }
                });*/

        // showing dot next to notifications label
        navigationView.getMenu().getItem(6).setActionView(R.layout.account_arrow);
        navigationView.getMenu().getItem(12).setActionView(R.layout.legal_text);
        View view=navigationView.getMenu().getItem(12).getActionView();
        TextView txtVerCode=view.findViewById(R.id.txt_legal);
        try {
            PackageInfo pInfo =getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtVerCode.setText(getResources().getString(R.string.codeversion)+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        //selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            //toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
       /* Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {*/
        // update the main content by replacing fragments
        Fragment fragment = getHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                /*fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);*/
        fragmentTransaction.replace(R.id.frame, fragment);
        //fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.commit();

         /*   }
        };*/

        // If mPendingRunnable is not null, then add to the message queue
        /*if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }*/

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private void loadFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            //toggleFab();
            return;
        }


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        Log.d("MapActivity", navItemIndex + "");
        // Toast.makeText(getApplicationContext(),navItemIndex+"",Toast.LENGTH_SHORT).show();
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // profile
                navItemIndex=0;
                CURRENT_TAG=TAG_HOME;
                /*ProfileFragment profileFragment1 = new ProfileFragment();
                return profileFragment1;*/

              /*  DemoFragment profileFragment1 = new DemoFragment();
                return profileFragment1;*/
                HomeFragment homeFragment1 = new HomeFragment();
                return homeFragment1;


            case 2:
                // TripHistory fragment
                /*TripHistory homeFragment2 = new TripHistory();
                return homeFragment2;*/
                DemoFragment aboutFr15 = new DemoFragment();
                return aboutFr15;

            case 3:
                // Earnings fragment
                DemoFragment homeFragment3 = new DemoFragment();
                return homeFragment3;

            case 4:
                // Refer and Earn fragment
                ReferEarnFragment homeFragment4 = new ReferEarnFragment();
                return homeFragment4;

            case 5:
                // Account fragment

                AccountFragment accFr = new AccountFragment();
                return accFr;

            case 6:
                // About fragment
                AboutFragment aboutFr1 = new AboutFragment();
                return aboutFr1;

            case 7:
                // Help fragment
                AboutFragment aboutFr12 = new AboutFragment();
                return aboutFr12;

            case 8:
                // Report fragment
                DemoFragment aboutFr13 = new DemoFragment();
                return aboutFr13;

            case 9:
                // Notification fragment
                DemoFragment aboutFr14 = new DemoFragment();
                return aboutFr14;
                /*AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle("Sign Out");
                builder.setMessage("Do you want to Sign Out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        logOut();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();*/
            case 10:
                // Signout fragment
                LegalFragment legalFragment = new LegalFragment();
                return legalFragment;

            default:
                return new DemoFragment();
        }
    }

    /**Logout */
    private void logOut() {

        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean(Constant.IS_LOGIN,false);
        edt.putInt(Constant.ISRIDE_START,0);
        edt.putInt( Constant.DAYOFWEEK,0);
        edt.putString( Constant.RIDETYPE,"");
        edt.putString( Constant.ACCEPT_RIDE,"0");
        edt.putString( Constant.ACCEPT_RIDE_SECOND,"0");
        // edt.clear();
        edt.commit();

        SharedPreferences.Editor editor=pref_firebase.edit();
        editor.putString(Constant.FIREBASELAST_TIMESTAMP,"");
        editor.commit();

        stopLocationservice();

        startActivity(new Intent(MapActivity.this, LoginActivity.class));
        finish();

    }

    private void stopLocationservice() {
        if(mServer!=null) {
            mServer.disableFloatingWidget();
            mServer.removeLocationUpdates();
        }
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        stopService(new Intent(this, LocationService.class));
    }

    /**Set toolbar by using navItemIndex .. name of toolbar title saved in acivityTitles array which is in string.xml */
    private void setToolbarTitle() {


        toolbar.setTitle(activityTitles[navItemIndex]);
    }

    //it will open driver befor opening drawer it will check .. is ride started
    //if driver started ride or accept ride noti then ite willnot open drawer
    public void openDrawer() {

        if(pref.getInt(Constant.ISRIDE_START,0)==0) {

            if (pref.getString(Constant.ACCEPT_RIDE, "0").equals("0")) {
                if(DetectConnection.checkInternetConnection(MapActivity.this)) {
                    unLockDrawer();

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(Gravity.START);
                }else {
                    lockDrawer();
                    showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
                }
            }
        }

    }


    //it will lock drwaer not allowed to open drwaer
    public void lockDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    public void unLockDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void selectNavMenu() {
        //if(navItemIndex!=1) {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        //}
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {



                //Check to see which item was being clicked and perform appropriate action
                // switch (menuItem.getItemId()) {
                showFragment(menuItem.getItemId(),navItemIndex,"","");
                //}

                //Checking if the item is in checked state or not, if not make it in checked state
                if(navItemIndex!=11) {
                    loadFragment();
                }

                /*if (navItemIndex != 1 || navItemIndex != 9) {
                   // loadHomeFragment();
                }*/

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.d("openDrawer","open_actionBarDrawerToggle");
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Constant.hideKeyboardFrom(MapActivity.this);
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                Log.d(TAG,"openDrawer:"+pref.getInt(Constant.ISRIDE_START,0)+" Accept "+pref.getString(Constant.ACCEPT_RIDE,"0"));
                if(pref.getInt(Constant.ISRIDE_START,0)==0) {

                    if (pref.getString(Constant.ACCEPT_RIDE, "0").equals("0")) {

                        if(DetectConnection.checkInternetConnection(MapActivity.this)) {
                            unLockDrawer();

                            super.onDrawerOpened(drawerView);

                        }else {
                            lockDrawer();
                            showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
                        }


                    }
                }


            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public void showFragment(int itemId,int newNavItemIndex,String fromDate,String toDate) {
        Fragment fragment = null;
        FragmentTransaction fragmentTransaction=null;
        Log.d("navigationNumber","newnavItem "+newNavItemIndex+" itemId "+itemId+" nave "+navItemIndex);

        switch (itemId) {
            //Replacing the main content with ContentFragment Which is our Inbox View;
            case R.id.nav_home1:
                if (navItemIndex != 0) { //if user already opened that fragment then only closedrware
                    clearbackStack();
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);

                    fragment = new HomeFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();

                }


                break;
            case R.id.nav_home:
                clearbackStack();
                //navItemIndex = 1;
                //CURRENT_TAG = TAG_PROFILE;
                        /*fragment = new HomeFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.commit();*/

                //navigationView.getMenu().getItem(navItemIndex).setChecked(true);

                startActivity(new Intent(MapActivity.this, ProfileActivity.class));

                        /*fragment = new ProfileFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.commit();
                        invalidateOptionsMenu();*/

                // navigationView.getMenu().getItem(navItemIndex).setChecked(true);
                drawer.closeDrawers();
                break;
            case R.id.nav_photos:
                Log.d("navigationNumber",newNavItemIndex+"");

                if (newNavItemIndex != 2) {
                    clearbackStack();
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_PHOTOS;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);
                    Log.d(TAG,"MapfromDate:"+fromDate);
                    Bundle args = new Bundle();
                    args.putString("fromDate", fromDate);
                    args.putString("toDate", toDate);


                    fragment = new TripHistory();
                    fragment.setArguments(args);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();


                    //if triphistory open from earning fragment with from and to date..then add
                    if(!fromDate.equals("")) {
                        fragmentTransaction.add(R.id.frame, fragment);
                        fragmentTransaction.addToBackStack(null);//add backstack
                    }else {
                        fragmentTransaction.replace(R.id.frame, fragment);
                    }
                    fragmentTransaction.commit();

                }

                break;
            case R.id.nav_movies:
                // if (navItemIndex != 3) {
                clearbackStack();
                navItemIndex = 3;
                CURRENT_TAG = TAG_MOVIES;
                navigationView.getMenu().getItem(navItemIndex).setChecked(true);
                fragment = new EarningHistoryMenuFr();
                //fragment = new EarningFragment();
                //fragment = new DemoFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();
                // }
                break;

            case R.id.nav_paymentdetails:
                if (navItemIndex != 4) {
                    clearbackStack();
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_MOVIES;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);
                    fragment = new PaymentDetailFragment();
                    //fragment = new DemoFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }
                break;


            case R.id.nav_notifications:
                if (navItemIndex != 5) {
                    clearbackStack();
                    navItemIndex = 5;
                    CURRENT_TAG = TAG_NOTIFICATIONS;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);
                    fragment = new ReferEarnFragment();
                    //fragment = new DemoFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.nav_settings:
                if (navItemIndex != 6) {
                    clearbackStack();
                    navItemIndex = 6;
                    CURRENT_TAG = TAG_SETTINGS;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);

                    fragment = new AccountFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }

                break;


            case R.id.nav_incentiveandnoti:
                if (navItemIndex != 7) {

                    clearbackStack();

                    navItemIndex = 7;
                    CURRENT_TAG = TAG_INCENTIVE_AND_NOTI;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);


                    fragment = new IncentiveAndNotification();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();

                }

                break;

            case R.id.nav_report:
                if (navItemIndex != 8) {

                    clearbackStack();
                    navItemIndex = 8;
                    CURRENT_TAG = TAG_SETTINGS;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);


                    fragment = new ReportIssueFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();

                }

                break;




            case R.id.nav_about:
                if (navItemIndex != 9) {
                    clearbackStack();
                    navItemIndex = 9;
                    CURRENT_TAG = TAG_SETTINGS;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);

                    fragment = new AboutFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }

                break;

            case R.id.nav_help:
                if (navItemIndex != 10) {
                    clearbackStack();

                    navItemIndex = 10;
                    CURRENT_TAG = TAG_SETTINGS;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);

                    fragment = new HelpFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }

                break;



            case R.id.nav_signout:
                clearbackStack();
                navItemIndex = 11;
                CURRENT_TAG = TAG_SETTINGS;
                navigationView.getMenu().getItem(navItemIndex).setChecked(true);


                        /*if(pref.getInt(Constant.ISRIDE_START,0)==1||pref.getString(Constant.ACCEPT_RIDE,"0").equals("1")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                            builder.setTitle(R.string.nav_signout);
                            builder.setMessage(R.string.pleaseCompleteyourpendingRide);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                   // logOut();

                                }
                            });

                            builder.show();

                        }else {*/

                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle(R.string.nav_signout);
                builder.setMessage(R.string.doyousignout);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        logOut();

                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // onBackPressed();
                    }
                });
                builder.show();

                //}

                break;

            case R.id.nav_legal:
                if (navItemIndex != 12) {
                    clearbackStack();
                    navItemIndex = 12;
                    CURRENT_TAG = TAG_LEGAL;
                    navigationView.getMenu().getItem(navItemIndex).setChecked(true);

                    fragment = new LegalFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }


                break;


            default:
                navItemIndex = 0;
        }

    }

    private void clearbackStack() {

        FragmentManager frm = getSupportFragmentManager();
        frm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);//clear backstack fragment
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        FragmentManager frm = getSupportFragmentManager();

        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex == 1) {
                frm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);//clear backstack fragment and go to homefragemnt
                Log.i(TAG, "inProfileBack");
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;

                Fragment fragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();

                loadFragment();

            }else if (navItemIndex == 2||navItemIndex == 3) {
                //

                if (frm.getBackStackEntryCount() > 0) {
                    Log.i("MainActivity", "popping backstack");
                    frm.popBackStack();

                } else {
                    Log.i("MainActivity", "nothing on backstack, calling super");
                    super.onBackPressed();
                }

            }
            else if (navItemIndex != 0) {

                frm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);//clear backstack fragment

                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                Fragment fragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();

                loadFragment();

                return;

            } else {

                frm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);//clear backstack fragment

                FragmentManager fm = getSupportFragmentManager();
                //if you added fragment via layout xml
                //HomeFragment fragment = ( HomeFragment)fm.findFragmentById(R.id.frame);
                Fragment fragment1 = fm.findFragmentById(R.id.frame);
                // HomeFragment fragment = (HomeFragment)fm.findFragmentByTag(TAG_HOME);
                // if(fragment.isVisible()) {
                if(fragment1 instanceof HomeFragment) {
                    HomeFragment fragment = ( HomeFragment)fm.findFragmentById(R.id.frame);

                    if (!fragment.closeFromToLayout()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                        builder.setTitle(R.string.exit);
                        builder.setMessage(R.string.doyouuwanttoexit);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                                finishAffinity();

                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });
                        builder.show();


                    }
                }
                //if()



            }

        }

        // super.onBackPressed();
    }

    public void changeToolbarTitle(String titleName) {

        toolbar.setTitle(titleName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            //getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.profile, menu);
        }

        return true;
    }

    public void hideToolbar() {
        getSupportActionBar().hide();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.action_edit) {

            if(DetectConnection.checkInternetConnection(this)) {
                startActivity(new Intent(MapActivity.this, EditProfileActivity.class));
            }else {
                Toast.makeText(MapActivity.this,R.string.noInternet,Toast.LENGTH_LONG).show();
            }
            return true;
        }


        // user is in notifications fragment
        // and selected 'Mark all as Read'
       /* if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }*/

        // user is in notifications fragment
        // and selected 'Clear All'
       /* if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }*/

        return super.onOptionsItemSelected(item);
    }



    /** started location service
     * bind location service in activity using service connection
     * use bind locationservice to stop fusedapiclient from getting location on stop method
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");

        if(mServer!=null) {
            try {
                boolean islogin=  pref.getBoolean(Constant.IS_LOGIN,false);
                if(islogin) {
                    mServer.showWidget();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        /*if(mServer!=null) {
            mServer.removeLocationUpdates();
        }
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }

        stopService(new Intent(this, LocationService.class));*/



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        /** started location service
         * bind location service in activity using service connection
         * use bind locationservice to stop fusedapiclient from getting location on stop method
         */
        if(mBounded) {
            try {
                Intent mIntent = new Intent(this, LocationService.class);
                bindService(mIntent, mConnection, BIND_AUTO_CREATE);
                getLast_Location();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        //for tripNotifcation boolean value.. decide screen is visible or not
        // Store our shared preference.. this is used in online notification.class
        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(Constant.IS_TRIP_NOTIFICATION_ACTIVE, false);
        ed.commit();
    }

    public Location getLast_Location() {

        if(mServer!=null) {
            return  mServer.getLastLocation();
        }
        return null;
    }



    /** started location service
     * bind location service in activity using service connection
     * use bind locationservice to stop fusedapiclient from getting location on stop method
     */

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"Service is disconnected");
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"Service is connected");
            mBounded = true;
            LocationService.LocalBinder mLocalBinder = (LocationService.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();

            if(mServer!=null) {
                Log.d(TAG,"hideWidget2->>>");
                // mServer.disableFloatingWidget();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        //stop service and unbind it
        /*if(mServer!=null) {
            mServer.removeLocationUpdates();
        }
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }*/
        /* stopService(new Intent(this, LocationService.class));*/



    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        // if (requestCode == LocationFragment.REQUEST_LOCATION){
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
        if (requestCode ==   fragment.REQUEST_LOCATION){
            fragment.onActivityResult(requestCode, resultCode, data);

            //lFrag.onActivityResult(requestCode, resultCode, data);
        }if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                startService(new Intent(MapActivity.this, LocationService.class));
            } else {

                //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showToolbar() {
        // getSupportActionBar().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Constant.PUSH_NOTIFICATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, filter);

        if(DetectConnection.checkInternetConnection(MapActivity.this)) {

            if(pref.getInt(Constant.ISRIDE_START, 0) != 1){
                unLockDrawer();
            }else {

                lockDrawer();
            }

            new GetVersionCode().execute();

            Calendar cal = Calendar.getInstance();
            int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            SharedPreferences sharedPreferences= this.getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
            int dayOfWeek = sharedPreferences.getInt(Constant.DAYOFWEEK, 0);

            if(dayOfWeek != currentDayOfWeek){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constant.DAYOFWEEK, currentDayOfWeek);
                editor.commit();
                // Your once a week code here
                checkDriverCurrentStatus();

               /* pref_firebase = getSharedPreferences(Constant.FIREBASE_SHARED_PREF_NAME, 0);
                firebaseId= pref_firebase.getString("regId","");
                if(firebaseId!=null){
                    sendFirebaseKey(firebaseId);
                }*/
            }

        }else {
            showSnackBarForNoInternet(getResources().getString(R.string.noInternet));
            lockDrawer();
        }


        urlProfileImg=pref.getString(Constant.PROFILE_PATH,"");

        /*Glide.with(this)
                .load(urlProfileImg)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imgProfile.setImageBitmap(resource);
                        //encodeBitmapImage(resource);
                    }
                });*/

        Glide.with(MapActivity.this)
                .load(urlProfileImg.trim()+"?ver=5436789")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                // .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.profile)
                .dontAnimate().into(imgProfile);


        Log.d("imageViewUrl",urlProfileImg+"");
        loadLocale();//set language

        //check DRAW_OVER_OTHER_APP_PERMISSION is allowed or not.. permission is required for showing floating widget
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {

            if(mServer!=null) {
                Log.d(TAG,"hideWidget1->>>");
                try {
                    mServer.disableFloatingWidget();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            startService(new Intent(MapActivity.this, LocationService.class));

            /*if(!isMyServiceRunning(LocationService.class)) {
                startService(new Intent(MapActivity.this, LocationService.class));
            }else {
                Log.d(TAG,"hideWidget->>>");
                if(mServer!=null) {
                    Log.d(TAG,"hideWidget1->>>");
                    mServer.disableFloatingWidget();
                }
            }*/

        }


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref1=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        String language = pref1.getString(Constant.Locale_KeyValue, "");
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


    /**If install version is less than play store version then it will show POP UP to update the app */
    private class GetVersionCode extends AsyncTask<Void, String, String> {

        String currentVersion = BuildConfig.VERSION_NAME;


        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = /*Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.tretakalp.Rikshaapp" + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();*/
                        Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.tretakalp.Rikshaapp"+ "&hl=en")
                                .timeout(30000)
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                .referrer("http://www.google.com")
                                .get()
                                .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                                .first()
                                .ownText();
                return newVersion;
            } catch (Exception e) {
                e.printStackTrace();
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            Log.d("update","onlineVersion"+onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    //show dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    builder.setTitle(R.string.update_application);
                    builder.setMessage(R.string.update_app_msg);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.Update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +"com.tretakalp.Rikshaapp")));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.tretakalp.Rikshaapp")));
                            }

                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            //onBackPressed();
                            finishAffinity();
                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }



    /** This function or server API get the current status of driver means he is activated or deactivated
     * or check is he on trial days or not
     *Note: if internet is off then it will not call .. if user turn on net then network recuver broadcastreceiver invoke
     * and call checkDriverstatus Api
     * */
    private void checkDriverCurrentStatus() {

        final ProgressDialog pDialog = new ProgressDialog(MapActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {


            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", pref.getString(Constant.DRIVER_ID,""));



        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "checkDriverCurrentStatus_URL" + jsonObject1.toString());
        Log.d(TAG, "checkDriverCurrentStatus_URl" + Constant.DRIVER_CURRENT_STATUS_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.DRIVER_CURRENT_STATUS_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");


                        try {
                            pDialog.dismiss();
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getcheckDriverCurrentStatus_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                JSONObject jsonObject= response.getJSONObject("data");
                                String status = jsonObject.getString("status");
                                if(status.equals("1")){
                                    // Toast.makeText(MapActivity.this,msg,Toast.LENGTH_SHORT).show();
                                    if(!TextUtils.isEmpty(msg)) {
                                        addNotification(msg);
                                    }
                                }


                            }else {
                                Toast.makeText(MapActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MapActivity.this,msg,Toast.LENGTH_LONG).show();
                            if(pref.getInt(Constant.ISRIDE_START,0)!=1&&!pref.getString(Constant.ACCEPT_RIDE,"0").equals("1")) {
                                logOut();
                            }
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
                    Toast.makeText(MapActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
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


    /**This func send the genarated firebase key and send to the server
     * and update the firebase key if exist if not then insert on server db
     * */
    private void sendFirebaseKey(String firebaseId) {

        /*final ProgressDialog pDialog = new ProgressDialog(MapActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();*/

        JSONObject jsonObject1 = null;

        try {


            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", pref.getString(Constant.DRIVER_ID,""));
            jsonObject1.put("firebase_id",firebaseId+"");



        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "FirebaseIdStatus_URL" + jsonObject1.toString());
        Log.d(TAG, "firebaseStatus_URl" + Constant.CHANGE_FIREBASE_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.CHANGE_FIREBASE_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        // pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");

                            Log.d(TAG, "getcheckDriverCurrentStatus_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                SharedPreferences.Editor editor=pref_firebase.edit();
                                editor.putString(Constant.FIREBASELAST_TIMESTAMP,Constant.getDateTime()+"");
                                editor.commit();

                            }else {
                                Toast.makeText(MapActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // pDialog.dismiss();


                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    Log.d("Error----->>>>>","networkresponse");
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        // String msg = obj.getString("message");
                        // Toast.makeText(MapActivity.this,msg,Toast.LENGTH_SHORT).show();



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
                    Toast.makeText(MapActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
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


    /**This will show normal notification to driver if didnt submitted full document
     * it will show once in a day or if user login the app
     * */
    private void addNotification(String msg) {

        Intent yesIntent = new Intent(this,LoginActivity.class);
        PendingIntent yesbtnIntent = PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent rejectIntent = new Intent(this,LoginActivity.class);
        PendingIntent rejectbtnIntent = PendingIntent.getActivity(this, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {500,1000};
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification))
                .setContentTitle("Rickshaw Application")
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSound(uri)
                .setVibrate(v)
                .setStyle(bigText)
                .setAutoCancel(true)
                .setContentText(msg);

        Intent notificationIntent = new Intent(this,LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


    /*private void addNotification1() {

        // Create the reply action and add the remote input.
        Intent snoozeIntent = new Intent(this, LoginActivity.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);

        NotificationCompat.Builder mBuilder = new
                NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_snooze, getString(R.string.snooze),
                        snoozePendingIntent);
    }*/




}