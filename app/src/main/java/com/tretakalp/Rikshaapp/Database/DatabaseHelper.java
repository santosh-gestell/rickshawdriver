package com.tretakalp.Rikshaapp.Database;/*




/**
 * Created by Mac on 16/03/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.tretakalp.Rikshaapp.Model.Bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {



    private static DatabaseHelper mInstance = null;
    public static DatabaseHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }




    private static String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Riksha";
    // Database Name
    private static String DATABASE_NAME = "Rickshaw.db";
    // Current version of database
    private static final int DATABASE_VERSION = 18;

    // Name of tables
    private static final String TABLE_LOCATION = "Location1";

    private static final String TIME_STAMP = "TimeStamp";
    private static final String SR_ID = "sr";

    private static final String USER_ID = "user_id";


    private static final String TABLE_TRIPS = "Trip_ride";
    private static final String TABLE_NOTIFICATION = "TripNotification";

    private static final String ISPREPAID ="isprepaid" ;
    private static final String DRIVER_ID ="driverId" ;
    private static final String SESSION_ID ="seesionId" ;
    private static final String DISTANCE ="distance" ;
    private static final String DURATION = "duration";
    private static final String NET_AMOUNT ="netAmount" ;
    private static final String TRIP_TYPE ="tripType" ;
    private static final String PICKUP_LOCATION ="pickupName" ;

    private static final String END_LOCATION ="endLocName" ;
    private static final String CUST_MOBILE_NO ="cust_MobileNo" ;
    private static final String START_LAT ="slat" ;
    private static final String START_LONG ="slong" ;
    private static final String DROP_LAT = "droplat";
    private static final String DROP_LONG = "droplong";
    private static final String END_LAT ="endlat" ;
    private static final String END_LONG = "endlong";
    private static final String DROP_LOCATION_NAME = "droplocName";
    private static final String TOATAL_AMOUNT ="totalAmount" ;
    private static final String TOTAL_DISTANCE = "totalDistance";
    private static final String PAYMENT_TYPE = "payType";

    private static final String IS_PAYMENT_DONE ="ispaymentDone" ;
    private static final String IS_RIDECOMPLETE ="isRideComplete" ;
    private static final String START_RIDE_TIME ="startRideTime" ;
    private static final String END_RIDE_TIME ="endRideTime" ;
    private static final String PAYMENT_MODE ="paymentMode" ;

    private static final String TRIP_ID ="tripId" ;
    private static final String FEE ="fee" ;
    private static final String TAX ="tax" ;

    private static final String DISCOUNT ="discount" ;
    private static final String RIDE_CHARGE ="ride_charge" ;
    private static final String CREATE_TABLE_TRIP= "CREATE TABLE "
            + TABLE_TRIPS
            + "("
            +SR_ID
            +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + DRIVER_ID
            + " TEXT ,"
            + SESSION_ID
            + " TEXT ,"
            + DISTANCE
            + " TEXT ,"
            + DURATION
            + " TEXT ,"
            + NET_AMOUNT
            + " TEXT ,"
            + TRIP_TYPE
            + " TEXT ,"
            + TRIP_ID
            + " TEXT ,"
            + PICKUP_LOCATION
            + " TEXT ,"
            + END_LOCATION
            + " TEXT ,"
            + CUST_MOBILE_NO
            + " TEXT ,"
            + START_LAT
            + " TEXT ,"
            + START_LONG
            + " TEXT ,"
            + DROP_LAT
            + " TEXT ,"
            + DROP_LONG
            + " TEXT ,"
            + END_LAT
            + " TEXT ,"
            + END_LONG
            + " TEXT ,"
            + DROP_LOCATION_NAME
            + " TEXT ,"
            +TOATAL_AMOUNT
            +" TEXT,"
            + TOTAL_DISTANCE
            + " TEXT ,"
            + PAYMENT_TYPE
            + " TEXT ,"
            +IS_PAYMENT_DONE
            +" TEXT,"
            + IS_RIDECOMPLETE
            + " TEXT ,"
            + START_RIDE_TIME
            + " DATETIME ,"
            + END_RIDE_TIME
            + " DATETIME ,"
            + TIME_STAMP
            + " DATETIME ,"
            + FEE
            + " TEXT ,"
            + TAX
            + " TEXT ,"
            + RIDE_CHARGE
            + " TEXT ,"
            + DISCOUNT
            + " TEXT ,"
            + ISPREPAID
            + " TEXT );";


    private static final String TABLE_TRIPS_HISTORY_DETAILS = "TripHistory_Details";
    private static final String TOTAL_TIME ="totalTime" ;

    private static final String TRIP_NUMBER ="tripnumber" ;
    private static final String TRIP_DATE = "TripDate";

    private static final String PAYMENT ="payment" ;
    private static final String CREATE_TABLE_TRIPS_HISTORY_DETAILS= "CREATE TABLE "
            + TABLE_TRIPS_HISTORY_DETAILS
            + "("
            +SR_ID
            +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + DRIVER_ID
            + " TEXT ,"
            + NET_AMOUNT
            + " TEXT ,"
            + TRIP_TYPE
            + " TEXT ,"
            + TRIP_ID
            + " TEXT ,"
            + TRIP_NUMBER
            + " TEXT ,"
            + PICKUP_LOCATION
            + " TEXT ,"
            + DROP_LOCATION_NAME
            + " TEXT ,"
            +TOATAL_AMOUNT
            +" TEXT,"
            + TOTAL_DISTANCE
            + " TEXT ,"
            + PAYMENT_TYPE
            + " TEXT ,"
            + PAYMENT
            + " TEXT ,"
            + TOTAL_TIME
            + " DATETIME ,"
            + TRIP_DATE
            + " DATETIME ,"
            + TIME_STAMP
            + " DATETIME ,"
            + FEE
            + " TEXT ,"
            + RIDE_CHARGE
            + " TEXT ,"
            + ISPREPAID
            + " TEXT );";


    private static final String TABLE_TRIPS_DATE ="triphistorydate" ;

    private static final String TRIP_COUNT ="TripCount" ;

    private static final String CREATE_TABLE_TRIPS_DATE= "CREATE TABLE "
            + TABLE_TRIPS_DATE
            + "("
            +SR_ID
            +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + DRIVER_ID
            + " TEXT ,"
            + TRIP_DATE
            + " TEXT ,"
            + TRIP_COUNT
            + " TEXT ,"
            + DISCOUNT
            + " TEXT );";


    /*String title = data.getString("title");
    String message = data.getString("message");
    // String name = data.getString("name");
    String phone = data.getString("contact");
    String drop = data.getString("drop");
    String pickup = data.getString("pickup");
    String trip_id = data.getString("trip_id");
    String amount = data.getString("amount");
    String distance = data.getString("distance");
    String time = data.getString("timestamp");
    String duration = data.getString("duration");
    String seesionId=data.getString("session_code");*/

    private static final String TITLE ="title" ;

    private static final String MESSAGE ="message" ;

    private static final String BOOKING_ID ="bookingId" ;

    private static final String IS_ACCEPT ="isAcceptedNoti" ;
    private static final String CREATE_TABLE_NOTI= "CREATE TABLE "
            + TABLE_NOTIFICATION
            + "("
            +SR_ID
            +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + DRIVER_ID
            + " TEXT ,"
            + TITLE
            + " TEXT ,"
            + MESSAGE
            + " TEXT ,"
            + CUST_MOBILE_NO
            + " TEXT ,"
            + DROP_LOCATION_NAME
            + " TEXT ,"
            + PICKUP_LOCATION
            + " TEXT ,"
            + BOOKING_ID
            + " TEXT ,"
            + NET_AMOUNT
            + " TEXT ,"
            + DISTANCE
            + " TEXT ,"
            + DURATION
            + " TEXT ,"
            + SESSION_ID
            + " TEXT ,"
            + IS_ACCEPT
            + " TEXT ,"
            + ISPREPAID
            + " TEXT ,"
            + TIME_STAMP
            + " DATETIME );";


    private static final String TABLE_INCENTIVE_NOTIFICATION ="Incentive_Notification" ;
    private static final String NOTIFICATION_ID ="notificationId" ;
    private static final String IMAGE ="image" ;
    private static final String DATE_FROM = "dateFrom";
    private static final String DATE_TO = "dateTo";

    private static final String CREATE_TABLE_INCENTIVE_NOTIFICATION= "CREATE TABLE "
            + TABLE_INCENTIVE_NOTIFICATION
            + "("
            +SR_ID
            +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + DRIVER_ID
            + " TEXT ,"
            + TITLE
            + " TEXT ,"
            + MESSAGE
            + " TEXT ,"
            + NOTIFICATION_ID
            + " TEXT ,"
            + IMAGE
            + " TEXT ,"
            + DATE_FROM
            + " TEXT ,"
            + DATE_TO
            + " TEXT ,"
            + ISPREPAID
            + " TEXT ,"
            + TIME_STAMP
            + " DATETIME );";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRIP);
        db.execSQL(CREATE_TABLE_NOTI);
        db.execSQL(CREATE_TABLE_INCENTIVE_NOTIFICATION);
        db.execSQL(CREATE_TABLE_TRIPS_HISTORY_DETAILS);

       // db.execSQL(CREATE_TABLE_);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        switch(oldVersion) {

            case 1:
                db.execSQL(CREATE_TABLE_NOTI);

            case 2:
                db.execSQL(CREATE_TABLE_INCENTIVE_NOTIFICATION);
                //break;
            case 3:
                db.execSQL("ALTER TABLE "+TABLE_TRIPS+" ADD COLUMN "+FEE+" TEXT ");
                db.execSQL("ALTER TABLE "+TABLE_TRIPS+" ADD COLUMN "+TAX+" TEXT ");
                db.execSQL("ALTER TABLE "+TABLE_TRIPS+" ADD COLUMN "+DISCOUNT+" TEXT ");

                db.execSQL(CREATE_TABLE_TRIPS_HISTORY_DETAILS);
            case 4:
                //db.execSQL("ALTER TABLE "+TABLE_TRIPS+" ADD COLUMN "+RIDE_CHARGE+" TEXT ");

            case 5:
                //db.execSQL("Drop "+);


            case 6:

                if(!columnExistsInTable(db,TABLE_TRIPS,RIDE_CHARGE)) {
                    db.execSQL("ALTER TABLE " + TABLE_TRIPS + " ADD COLUMN " + RIDE_CHARGE + " TEXT ");
                }else {
                    Log.d("Database","exist");

                }

                break;
        }


    }


    public static boolean columnExistsInTable(SQLiteDatabase db, String table, String columnToCheck) {
        Cursor cursor = null;
        //SQLiteDatabase db1 = .getReadableDatabase();
        try {
            //query a row. don't acquire db lock
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TRIPS + " LIMIT 0", null);

            // getColumnIndex()  will return the index of the column
            //in the table if it exists, otherwise it will return -1
            if (cursor.getColumnIndex(RIDE_CHARGE) != -1) {
                //great, the column exists
                return true;
            }else {
                //sorry, the column does not exist
                return false;
            }

        } catch (SQLiteException Exp) {
            //Something went wrong with SQLite.
            //If the table exists and your query was good,
            //the problem is likely that the column doesn't exist in the table.
            return false;
        } finally {
            //close the db  if you no longer need it
           // if (db != null) db.close();
            //close the cursor
            if (cursor != null) cursor.close();
        }
    }


    public void insertTripNotificationData(Bean b) {

        SQLiteDatabase db = this.getReadableDatabase();

        /*String Query = "Select * from " + TABLE_NOTIFICATION + " where " + BOOKING_ID + " = " + b.getBookingId();
        Cursor cursor = db.rawQuery(Query, null);*/

        ContentValues values1 = new ContentValues();

       // if(cursor.getCount()==0) {

            values1.put(DRIVER_ID, b.getDriverId() + "");
            values1.put(TITLE, b.getNotificationTitle() + "");
            values1.put(MESSAGE, b.getMessage() + "");
            values1.put(PICKUP_LOCATION, b.getPickupLocName() + "");
            values1.put(DROP_LOCATION_NAME, b.getDropLocName() + "");
            values1.put(BOOKING_ID, b.getBookingId() + "");
            values1.put(CUST_MOBILE_NO, b.getCustMobileNo() + "");
            values1.put(NET_AMOUNT, b.getNetAmount() + "");
            values1.put(DURATION, b.getDuration() + "");
            values1.put(DISTANCE, b.getDistance() + "");
            values1.put(SESSION_ID, b.getSessionId() + "");
            values1.put(IS_ACCEPT, "0" + "");
            values1.put(ISPREPAID, b.getIsPrepaid() + "");
            values1.put(TIME_STAMP, getDateTime());

            long ins = db.insert(TABLE_NOTIFICATION, null, values1);
            Log.d("TABLE_Noti", "in Location Query returning " + ins+b.getBookingId());

       /* }else{

            values1.put(DRIVER_ID, b.getDriverId() + "");
            values1.put(TITLE, b.getNotificationTitle() + "");
            values1.put(MESSAGE, b.getMessage() + "");
            values1.put(PICKUP_LOCATION, b.getPickupLocName() + "");
            values1.put(DROP_LOCATION_NAME, b.getDropLocName() + "");
            values1.put(BOOKING_ID, b.getBookingId() + "");
            values1.put(CUST_MOBILE_NO, b.getCustMobileNo() + "");
            values1.put(NET_AMOUNT, b.getNetAmount() + "");
            values1.put(DURATION, b.getDuration() + "");
            values1.put(DISTANCE, b.getDistance() + "");
            values1.put(SESSION_ID, b.getSessionId() + "");
            values1.put(IS_ACCEPT, "0" + "");
            values1.put(TIME_STAMP, getDateTime());

            long ins = db.update(TABLE_NOTIFICATION, values1, BOOKING_ID+"="+b.getBookingId(),null);
            Log.d("TABLE_Noti", "in Updated Query returning " + ins+b.getBookingId());
        }*/

    }


    //insertTripHistoryDetais
    public void insertTripHistoryDetails(Bean b) {

        SQLiteDatabase db = this.getReadableDatabase();

        String Query = "Select * from " + TABLE_TRIPS_HISTORY_DETAILS + " where " + TRIP_ID + " = " + b.getTripId();
        Cursor cursor = db.rawQuery(Query, null);

        ContentValues values1 = new ContentValues();

         if(cursor.getCount()==0) {

        values1.put(DRIVER_ID, b.getDriverId() + "");
        values1.put(TOTAL_DISTANCE, b.getTotalDistance() + "");
        values1.put(TOTAL_TIME, b.getDuration() + "");
        values1.put(PICKUP_LOCATION, b.getPickupLocName() + "");
        values1.put(DROP_LOCATION_NAME, b.getDropLocName() + "");
        values1.put(TRIP_ID, b.getTripId() + "");
        values1.put(TRIP_NUMBER, b.getTripNumber() + "");
        values1.put(NET_AMOUNT, b.getNetAmount() + "");
        values1.put(RIDE_CHARGE, b.getRideCharge() + "");
        values1.put(FEE, b.getFee() + "");
        values1.put(TOATAL_AMOUNT, b.getTotalAmount() + "");
        values1.put(PAYMENT_TYPE, b.getPaymentMode() + "");
        values1.put(PAYMENT,b.getPayment());
             values1.put(ISPREPAID,b.getIsPrepaid());
        values1.put(TRIP_DATE,b.getTripDate());
        values1.put(TIME_STAMP, getDateTime());

        long ins = db.insert(TABLE_TRIPS_HISTORY_DETAILS, null, values1);
        Log.d("TABLE_TripHistoryDetails", "in Location Query returning " + ins);

        }/*else{

            values1.put(DRIVER_ID, b.getDriverId() + "");
            values1.put(TITLE, b.getNotificationTitle() + "");
            values1.put(MESSAGE, b.getMessage() + "");
            values1.put(PICKUP_LOCATION, b.getPickupLocName() + "");
            values1.put(DROP_LOCATION_NAME, b.getDropLocName() + "");
            values1.put(BOOKING_ID, b.getBookingId() + "");
            values1.put(CUST_MOBILE_NO, b.getCustMobileNo() + "");
            values1.put(NET_AMOUNT, b.getNetAmount() + "");
            values1.put(DURATION, b.getDuration() + "");
            values1.put(DISTANCE, b.getDistance() + "");
            values1.put(SESSION_ID, b.getSessionId() + "");
            values1.put(IS_ACCEPT, "0" + "");
            values1.put(TIME_STAMP, getDateTime());

            long ins = db.update(TABLE_NOTIFICATION, values1, BOOKING_ID+"="+b.getBookingId(),null);
            Log.d("TABLE_Noti", "in Updated Query returning " + ins+b.getBookingId());
        }*/

    }

    public ArrayList<Bean> getTripNotification(String driverId) {

        String currentdate=getDateTime();
        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM "+ TABLE_NOTIFICATION +" WHERE "+DRIVER_ID+" ="+driverId +" AND (strftime('%s','"+currentdate+"')-strftime('%s',"+TIME_STAMP+")< 30)" +" ORDER BY "+SR_ID+" DESC" ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();

                    contact.setSrId(cursor.getInt(cursor.getColumnIndex(SR_ID)));
                    contact.setPickupLocName(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));
                    contact.setCustMobileNo(cursor.getString(cursor.getColumnIndex(CUST_MOBILE_NO)));
                    contact.setDuration(cursor.getString(cursor.getColumnIndex(DURATION)));
                    contact.setNetAmount(cursor.getString(cursor.getColumnIndex(NET_AMOUNT)));
                    contact.setDistance(cursor.getString(cursor.getColumnIndex(DISTANCE)));
                    contact.setIsPrepaid(cursor.getString(cursor.getColumnIndex(ISPREPAID)));
                    contact.setDropLocName(cursor.getString(cursor.getColumnIndex(DROP_LOCATION_NAME)));
                    contact.setBookingId(cursor.getString(cursor.getColumnIndex(BOOKING_ID)));
                    contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(TIME_STAMP)));
                    contact.setSessionId(cursor.getString(cursor.getColumnIndex(SESSION_ID)));
                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }


    public void insertIncentiveNotiData(Bean b) {

        SQLiteDatabase db = this.getReadableDatabase();


        String Query = "Select * from " + TABLE_INCENTIVE_NOTIFICATION ;
        Cursor cursor = db.rawQuery(Query, null);

        ContentValues values1 = new ContentValues();
        Log.d("Database","cursorValue "+cursor.getCount());
        if(cursor.getCount()>0) {

            String Query1 = "DELETE FROM  " + TABLE_INCENTIVE_NOTIFICATION ;
            Cursor cursor1 = db.rawQuery(Query1, null);
            Log.d("Database","InDelete cursorValue "+cursor1.getCount());

        }

        values1.put(DRIVER_ID, b.getDriverId() + "");
        values1.put(MESSAGE, b.getMessage() + "");
        values1.put(TITLE, b.getNotificationTitle() + "");
        values1.put(NOTIFICATION_ID, b.getNotifyId() + "");
        values1.put(DATE_FROM, b.getDate_From() + "");
        values1.put(DATE_TO, b.getDate_To() + "");
        values1.put(ISPREPAID, b.getIsPrepaid() + "");
        values1.put(TIME_STAMP, getDateTime() + "");
        values1.put(IMAGE, b.getImage() + "");

        long ins = db.insert(TABLE_INCENTIVE_NOTIFICATION, null, values1);
        Log.d(TABLE_INCENTIVE_NOTIFICATION, "in Location Query returning " + ins);


    }


    public ArrayList<Bean> getIncenetiveNotiData() {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM "+ TABLE_INCENTIVE_NOTIFICATION ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    Bean contact = new Bean();
                    contact.setNotificationTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                    contact.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                    contact.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
                    contact.setDate_From(cursor.getString(cursor.getColumnIndex(DATE_FROM)));
                    contact.setDate_To(cursor.getString(cursor.getColumnIndex(DATE_TO)));

                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }



    public void insertTripData(Bean b) {

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values1 = new ContentValues();
        values1.put(DRIVER_ID,b.getDriverId()+"");
        values1.put(SESSION_ID,b.getSessionId()+"");
        values1.put(DURATION,b.getDuration()+"");
        values1.put(DISTANCE,b.getDistance()+"");
        values1.put(NET_AMOUNT,b.getNetAmount()+"");
        values1.put(TRIP_TYPE,b.getTripType()+"");
        values1.put(TRIP_ID,b.getTripId()+"");
        values1.put(PICKUP_LOCATION,b.getPickupLocName()+"");
        values1.put(END_LOCATION,b.getEndLocationName()+"");
        values1.put(CUST_MOBILE_NO,b.getCustMobileNo()+"");
        values1.put(START_LAT,b.getStartLat()+"");
        values1.put(START_LONG,b.getStartLong()+"");
        values1.put(DROP_LAT,b.getDropLat()+"");
        values1.put(DROP_LONG,b.getDropLong()+"");
        values1.put(END_LAT,b.getEndLat()+"");
        values1.put(END_LONG,b.getEndLong()+"");
        values1.put(DROP_LOCATION_NAME,b.getDropLocName()+"");
        values1.put(TOATAL_AMOUNT,b.getTotalAmount()+"");
        values1.put(TOTAL_DISTANCE,b.getTotalDistance()+"");
        values1.put(IS_PAYMENT_DONE,"No");
        values1.put(IS_RIDECOMPLETE,"No");
        values1.put(TIME_STAMP,getDateTime());
        values1.put(START_RIDE_TIME,getDateTime());
        values1.put(END_RIDE_TIME,getDateTime());
        values1.put(ISPREPAID,b.getIsPrepaid());
        values1.put(PAYMENT_TYPE,"2");//2 means not done
        // values1.put(PAYMENT_MODE,getDateTime());

        long ins = db.insert(TABLE_TRIPS, null, values1);
        Log.d("TABLE_TRIPS", "in Location Query returning " + ins);
    }


    public void updateTripTable_withEndTripData(String session, int isRidecomplete, String totalAmount, String totalDistance,String dlat,
                                                String dlong,String droplocationName,String fee,String rideCharge) {
        //isRideComplte==2 mean complete
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IS_RIDECOMPLETE,isRidecomplete+""); //These Fields should be your String values of actual column names
        cv.put(TOATAL_AMOUNT,totalAmount);
        cv.put(TOTAL_DISTANCE,totalDistance);
        cv.put(DROP_LAT,dlat);
        cv.put(DROP_LONG,dlong);
        cv.put(DROP_LOCATION_NAME,droplocationName);
        cv.put(END_RIDE_TIME,getDateTime());//update end ride time
        cv.put(FEE,fee);//update end ride time
        cv.put(RIDE_CHARGE,rideCharge);//update end ride time
        cv.put(DISCOUNT,"");//update end ride time
        db.update(TABLE_TRIPS, cv, SESSION_ID+"='"+session+"'", null);

    }

    //Update Trip Table when payment done.
    public void updateTripTable_withPaymentMode(String session, String paymentMode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(PAYMENT_TYPE,paymentMode);
        cv.put(IS_PAYMENT_DONE,"YES");
        db.update(TABLE_TRIPS, cv, SESSION_ID+"='"+session+"'", null);
    }



    public ArrayList<Bean> getTripDate(String driverId) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
       // String selectQuery = "SELECT  DISTINCT "+ TIME_STAMP +" FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+" ="+driverId +" AND "+"strftime('%Y-%m-%d','"+TIME_STAMP+"') = strftime('%Y-%m-%d', '" + TIME_STAMP + "' ) "+" ORDER BY "+SR_ID ;
        String selectQuery = "SELECT  "+" strftime('%Y-%m-%d',"+TIME_STAMP+")" +", count(*) FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+PAYMENT_TYPE+"!=2"+" Group by "+" strftime('%Y-%m-%d',"+TIME_STAMP+")"+" ORDER BY "+SR_ID +" DESC" ;

       // String selectQuery="  SELECT datetime("+TIME_STAMP+",'YYYY-MM-DD') From "+TABLE_TRIPS;
       // String selectQuery="  SELECT strftime('%Y-%m-%d',"+TIME_STAMP+") From "+TABLE_TRIPS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();
                    Log.d("tripDate",cursor.getString(0)+"");

                    contact.setTimeStamp(cursor.getString(0));
                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }


    public ArrayList<Bean> getTripDateNew(String driverId) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        // String selectQuery = "SELECT  DISTINCT "+ TIME_STAMP +" FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+" ="+driverId +" AND "+"strftime('%Y-%m-%d','"+TIME_STAMP+"') = strftime('%Y-%m-%d', '" + TIME_STAMP + "' ) "+" ORDER BY "+SR_ID ;
        String selectQuery = "SELECT  "+" strftime('%Y-%m-%d',"+TRIP_DATE+")" +", count(*) FROM " + TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+"="+driverId+" Group by "+" strftime('%Y-%m-%d',"+TRIP_DATE+")"+" ORDER BY "+SR_ID +" DESC" ;

        // String selectQuery="  SELECT datetime("+TIME_STAMP+",'YYYY-MM-DD') From "+TABLE_TRIPS;
        // String selectQuery="  SELECT strftime('%Y-%m-%d',"+TIME_STAMP+") From "+TABLE_TRIPS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();
                    Log.d("tripDate",cursor.getString(0)+"");

                    contact.setTimeStamp(cursor.getString(0));
                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }

    public ArrayList<Bean> getTripDateBySpecificDate(String driverId,String fromDate, String toDate) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        // String selectQuery = "SELECT  DISTINCT "+ TIME_STAMP +" FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+" ="+driverId +" AND "+"strftime('%Y-%m-%d','"+TIME_STAMP+"') = strftime('%Y-%m-%d', '" + TIME_STAMP + "' ) "+" ORDER BY "+SR_ID ;
        String selectQuery = "SELECT  "+" strftime('%Y-%m-%d',"+TIME_STAMP+")" +", count(*) FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+PAYMENT_TYPE+"!=2"
                +" AND "+" strftime('%Y-%m-%d',"+TIME_STAMP+")" + " BETWEEN " + " strftime('%Y-%m-%d','"+fromDate+"')" + " AND " + " strftime('%Y-%m-%d','"+toDate+"')"
                +" Group by "+" strftime('%Y-%m-%d',"+TIME_STAMP+")"+" ORDER BY "+SR_ID +" DESC" ;


        // String selectQuery="  SELECT datetime("+TIME_STAMP+",'YYYY-MM-DD') From "+TABLE_TRIPS;
        // String selectQuery="  SELECT strftime('%Y-%m-%d',"+TIME_STAMP+") From "+TABLE_TRIPS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();
                    Log.d("tripDate",cursor.getString(0)+"");

                    contact.setTimeStamp(cursor.getString(0));
                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }


    public ArrayList<Bean> getTripDateBySpecificDateNew(String driverId,String fromDate, String toDate) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        // String selectQuery = "SELECT  DISTINCT "+ TIME_STAMP +" FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+" ="+driverId +" AND "+"strftime('%Y-%m-%d','"+TIME_STAMP+"') = strftime('%Y-%m-%d', '" + TIME_STAMP + "' ) "+" ORDER BY "+SR_ID ;
        String selectQuery = "SELECT  "+" strftime('%Y-%m-%d',"+TRIP_DATE+")" +", count(*) FROM " + TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+"="+driverId
                +" AND "+" strftime('%Y-%m-%d',"+TRIP_DATE+")" + " BETWEEN " + " strftime('%Y-%m-%d','"+fromDate+"')" + " AND " + " strftime('%Y-%m-%d','"+toDate+"')"
                +" Group by "+" strftime('%Y-%m-%d',"+TRIP_DATE+")"+" ORDER BY "+" strftime('%Y-%m-%d',"+TRIP_DATE+")" +" DESC" ;



        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();
                    Log.d("tripDate",cursor.getString(0)+"");

                    contact.setTimeStamp(cursor.getString(0));
                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }

    public ArrayList<Bean> getTripDetailsByDate(String driverId,String tripDate) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM "+ TABLE_TRIPS +" WHERE "+DRIVER_ID+" ="+driverId +" AND "+PAYMENT_TYPE+"!=2"+" AND "+"strftime('%Y-%m-%d',"+TIME_STAMP+") = strftime('%Y-%m-%d', '" + tripDate + "' ) "+" ORDER BY "+SR_ID+" DESC" ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();

                    contact.setPickupLocName(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));
                    contact.setEndLocationName(cursor.getString(cursor.getColumnIndex(END_LOCATION)));
                    contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(TIME_STAMP)));
                    contact.setTime(cursor.getString(cursor.getColumnIndex(DURATION)));
                    contact.setNetAmount(cursor.getString(cursor.getColumnIndex(NET_AMOUNT)));
                    contact.setTotalAmount(cursor.getString(cursor.getColumnIndex(TOATAL_AMOUNT)));
                    contact.setDistance(cursor.getString(cursor.getColumnIndex(DISTANCE)));
                    contact.setDropLocName(cursor.getString(cursor.getColumnIndex(DROP_LOCATION_NAME)));
                    contact.setIsPaymentDone(cursor.getString(cursor.getColumnIndex(IS_PAYMENT_DONE)));
                    contact.setTotalDistance(cursor.getString(cursor.getColumnIndex(TOTAL_DISTANCE)));
                    contact.setPaymentMode(cursor.getString(cursor.getColumnIndex(PAYMENT_TYPE)));
                    contact.setStartRideTime(cursor.getString(cursor.getColumnIndex(START_RIDE_TIME)));
                    contact.setDropRideTime(cursor.getString(cursor.getColumnIndex(END_RIDE_TIME)));
                    contact.setTripId(cursor.getString(cursor.getColumnIndex(TRIP_ID)));
                    contact.setIsPrepaid(cursor.getString(cursor.getColumnIndex(ISPREPAID)));

                    /*contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));
                    contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));
                    contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));*/
                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }


    public ArrayList<Bean> getTripDetailsByDateNew(String driverId,String tripDate) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM "+ TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+" ="+driverId +" AND "+"strftime('%Y-%m-%d',"+TRIP_DATE+") = strftime('%Y-%m-%d', '" + tripDate + "' ) "+" ORDER BY "+SR_ID+" DESC" ;
        Log.d("DatabaseHelper","selectQuery:"+selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("DatabaseHelper","cursorCount:"+cursor.getCount());
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();

                    contact.setPickupLocName(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));
                    contact.setEndLocationName(cursor.getString(cursor.getColumnIndex(DROP_LOCATION_NAME)));
                    contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(TRIP_DATE)));
                    contact.setTime(cursor.getString(cursor.getColumnIndex(TOTAL_TIME)));
                    contact.setNetAmount(cursor.getString(cursor.getColumnIndex(NET_AMOUNT)));
                    contact.setTotalAmount(cursor.getString(cursor.getColumnIndex(TOATAL_AMOUNT)));
                    contact.setRideCharge(cursor.getString(cursor.getColumnIndex(RIDE_CHARGE)));
                    contact.setDistance(cursor.getString(cursor.getColumnIndex(TOTAL_DISTANCE)));
                    contact.setDropLocName(cursor.getString(cursor.getColumnIndex(DROP_LOCATION_NAME)));
                    contact.setTotalDistance(cursor.getString(cursor.getColumnIndex(TOTAL_DISTANCE)));
                    contact.setPaymentMode(cursor.getString(cursor.getColumnIndex(PAYMENT_TYPE)));
                    contact.setPayment(cursor.getString(cursor.getColumnIndex(PAYMENT)));
                    contact.setFee(cursor.getString(cursor.getColumnIndex(FEE)));

                   // set.setDuration(cursor.getString(cursor.getColumnIndex(DURATION)));
                    contact.setTripId(cursor.getString(cursor.getColumnIndex(TRIP_NUMBER)));

                    /*contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));
                    contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));
                    contact.setTimeStamp(cursor.getString(cursor.getColumnIndex(PICKUP_LOCATION)));*/
                    // Adding contact to list
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }

    public int getTripCountDetails(String driverId,String tripDate) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * "+" FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+PAYMENT_TYPE+"<>2"+" AND "+"strftime('%Y-%m-%d',"+TIME_STAMP+") = strftime('%Y-%m-%d', '" + tripDate + "' ) "+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        // return contact list
        return count;
    }

    public int getTripCountDetailsNew(String driverId,String tripDate) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * "+" FROM " + TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+"strftime('%Y-%m-%d',"+TRIP_DATE+") = strftime('%Y-%m-%d', '" + tripDate + "' ) "+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        // return contact list
        return count;
    }

    public int getTotalTripCount(String driverId) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * "+" FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+PAYMENT_TYPE+"<>2"+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        // return contact list
        return count;
    }

    public int getTotalTripCountNew(String driverId) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  * "+" FROM " + TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+"="+driverId+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        // return contact list
        return count;
    }

    public int getTotalSum(String driverId) {


        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  SUM( CAST("+TOATAL_AMOUNT+" as Integer )) AS TOTAL_SALARY  FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+PAYMENT_TYPE+"<>2"+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int total=0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("TOTAL_SALARY"));// get final total
        }
       // int count = cursor.getCount();
        // return contact list
        return total;
    }

    public double getTodayTotalSum(String driverId) {

        String currentdate=getDateTime();
        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  SUM( CAST("+TOATAL_AMOUNT+" as Double )) AS TOTAL_SALARY  FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+PAYMENT_TYPE+"<>2"+" AND "+"strftime('%Y-%m-%d',"+TIME_STAMP+") = strftime('%Y-%m-%d', '" + currentdate + "' ) "+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int total=0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("TOTAL_SALARY"));// get final total
        }
       // int count = cursor.getCount();
        // return contact list
        return total;
    }



    //show todays earning in profile
    public double getTodayTotalSumNew(String driverId) {

        String currentdate=getDateTime();
        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  SUM( CAST("+TOATAL_AMOUNT+" as Double )) AS TOTAL_SALARY  FROM " + TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+"strftime('%Y-%m-%d',"+TRIP_DATE+") = strftime('%Y-%m-%d', '" + currentdate + "' ) "+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int total=0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("TOTAL_SALARY"));// get final total
        }
        // int count = cursor.getCount();
        // return contact list
        return total;
    }


    public int getTotalEarningCash(String driverId) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  SUM( CAST("+TOATAL_AMOUNT+" as Integer )) AS TOTAL_SALARY  FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int total=0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("TOTAL_SALARY"));// get final total
        }
        // int count = cursor.getCount();
        // return contact list
        return total;
    }

    public int getTotalEarningOnline(String driverId) {

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        String selectQuery = "SELECT  SUM( CAST("+TOATAL_AMOUNT+" as Integer )) AS TOTAL_SALARY  FROM " + TABLE_TRIPS +" WHERE "+DRIVER_ID+"="+driverId+" AND "+PAYMENT_TYPE+" ORDER BY "+SR_ID ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int total=0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("TOTAL_SALARY"));// get final total
        }
        // int count = cursor.getCount();
        // return contact list
        return total;
    }


    public String getDateTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }



    // Copy to sdcard for debug use
    public static void copyDatabase(Context c) {


        String databasePath = c.getDatabasePath(DATABASE_NAME).getPath();
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
        Log.d("testing", " testing db path " + databasePath);
        Log.d("testing", " testing db exist " + f.exists());

        if (f.exists()) {
            try {

                File directory = new File(DIRECTORY);
                if (!directory.exists())
                    directory.mkdir();

                myOutput = new FileOutputStream(directory.getAbsolutePath()
                        + "/" + DATABASE_NAME);
                myInput = new FileInputStream(databasePath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (myOutput != null) {
                        myOutput.close();
                        myOutput = null;
                    }
                    if (myInput != null) {
                        myInput.close();
                        myInput = null;
                    }
                } catch (Exception e) {
                }
            }
        }
    }


    public void deleteLocationData(String userId){

       String date =getDateTime();
        SQLiteDatabase db = this.getWritableDatabase();
        //String sql = "DELETE FROM "+ TABLE_LOCATION /*+" WHERE "+TIME_STAMP+" <= date('now','-4 day')"*/;
      //  String sql = "DELETE FROM "+ TABLE_LOCATION +" WHERE "+DRIVER_ID+"=' "+userId+"'";
        db.delete(TABLE_LOCATION, USER_ID + " = ?", new String[] { userId });
        //Log.d("sqlDate",sql);
        //db.execSQL(sql);

    }



    public void deleteGps(String userId) {

       /* SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_GPSCHNAGE +" WHERE "+USER_ID+"="+userId;
        db.execSQL(sql);*/
    }


    public void deleteLastRecord() {
         SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_TRIPS +" WHERE "+SR_ID +" = (SELECT MAX("+SR_ID+") FROM "+TABLE_TRIPS +")";
        db.execSQL(sql);
       // DELETE FROM notes WHERE id = (SELECT MAX(id) FROM notes);
    }

    public void deleteNotiRecord(int srId) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_NOTIFICATION +" WHERE "+SR_ID +" ="+srId;
        db.execSQL(sql);
    }

    public void deleteAllNotificationData() {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_NOTIFICATION ;
        db.execSQL(sql);
    }

    public void deleteNotification(String trip_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_NOTIFICATION +" WHERE "+BOOKING_ID +" ="+trip_id;
        db.execSQL(sql);
    }

    //To get Trip id of Trip
    public ArrayList<Bean> getTripIdData(String fromDate, String toDate,String driverId) {
        //Log.d("getTripIdData",fromDate)

        ArrayList<Bean> contactList = new ArrayList<Bean>();
        // Select All Query
        //String selectQuery = "SELECT  "+TRIP_NUMBER+","+TRIP_ID+" FROM "+ TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+" ="+driverId +" AND "+" strftime('%Y-%m-%d',"+TRIP_DATE+")" + " BETWEEN " + " strftime('%Y-%m-%d','"+fromDate+"')" + " AND " + " strftime('%Y-%m-%d','"+toDate+"')" +" ORDER BY " +SR_ID +" DESC"  ;
        String selectQuery = "SELECT  "+TRIP_NUMBER+","+TRIP_ID+" FROM "+ TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID+" ="+driverId
               +" ORDER BY "+SR_ID +" DESC"  ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // read data from the cursor in here

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Bean contact = new Bean();


                    contact.setTripId(cursor.getString(cursor.getColumnIndex(TRIP_ID)));

                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
            db.close();
        }
        // return contact list
        return contactList;
    }

    //delete trip history data before calling insertTripHistoryDetails()

    public void deleteAllTripHistory(String driverId) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_TRIPS_HISTORY_DETAILS +" WHERE "+DRIVER_ID +" ="+driverId;
        db.execSQL(sql);
    }
}

