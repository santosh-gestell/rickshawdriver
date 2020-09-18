package com.tretakalp.Rikshaapp.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tretakalp.Rikshaapp.Adpter.CustomAdapter;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Mac on 05/09/18.
 */
public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    public static final int RequestPermissionCode = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final String TAG ="EditProfileActivity" ;
    private Uri fileUri;
    private String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "Riksha";
    public static final int MEDIA_TYPE_IMAGE = 1;
    File mediaFile;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PIC_CROP = 3;
    File mediaStorageDir;
    String encodedImage;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    SharedPreferences sharedPreferences;
    ImageView img_profile;
    EditText editTextName,editTextPhone,editTextPassword,editTextConfPass,editTextVehicleCity,editTextVehicle;

    Button btnSave;
    String driverId;
    //SharedPreferences pref;
    ArrayList<Bean> cityList=new ArrayList<>();
    Spinner spinner_loc;
    CustomAdapter adapter;
    int spinnerIndex;
    String  cityId="";
    String profilePicUrl;
    Button btn_cancel;
    LinearLayout linearChangepass,linearCp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.editprofile);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        //pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        sharedPreferences=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        driverId=sharedPreferences.getString(Constant.DRIVER_ID,"");
       // driverId="14";

        img_profile=findViewById(R.id.expandedImage);
        editTextName=findViewById(R.id.editTextName);
       // editTextLocation=findViewById(R.id.editTextLocation);
        editTextPhone=findViewById(R.id.editTextPhone);
        editTextPassword=findViewById(R.id.editTextPassword);
        editTextConfPass=findViewById(R.id.editTextConfPass);
        editTextVehicle=findViewById(R.id.editTextVehicle);
        editTextVehicleCity=findViewById(R.id.edtVehicleCityNo);
        btnSave=findViewById(R.id.btnSave);
        btn_cancel=findViewById(R.id.btn_cancel);
        spinner_loc=findViewById(R.id.spinner_loc);
        linearChangepass=findViewById(R.id.linearChangePass);
        linearCp=findViewById(R.id.linearCP);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        linearCp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(EditProfileActivity.this,ChangePasswordActivity.class));

            }
        });
        linearChangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(EditProfileActivity.this,ChangePasswordActivity.class));

            }
        });



        if(DetectConnection.checkInternetConnection(this)) {

            getCityList();
        }else {
            Toast.makeText(EditProfileActivity.this,R.string.noInternet,Toast.LENGTH_SHORT).show();
        }

        /*if(pref.getInt(Constant.LANGUAGE,1)!=1){
            editTextName.setHint(getResources().getString(R.string.nameh));
            editTextLocation.setHint(getResources().getString(R.string.locationh));
            editTextPhone.setHint(getResources().getString(R.string.mobileNumberh));
            editTextPassword.setHint(getResources().getString(R.string.passwordh));
            editTextConfPass.setHint(getResources().getString(R.string.confpasswordh));
        }*/

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(checkPermission()){

                    selectImage();
                }else {
                    requestPermission();
                }*/

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!editTextName.getText().toString().equals("")) {

                    if (DetectConnection.checkInternetConnection(EditProfileActivity.this)) {

                        updateProfile();

                    } else {
                        Toast.makeText(EditProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        spinner_loc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i!=0) {

                    editTextVehicleCity.setText(cityList.get(i).getRtoCode());
                    cityId=cityList.get(i).getCityId();
                    spinnerIndex=i;

                }else{
                    editTextVehicleCity.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]
                {

                        WRITE_EXTERNAL_STORAGE,
                        CAMERA

                }, RequestPermissionCode);

    }
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED
                && FourthPermissionResult == PackageManager.PERMISSION_GRANTED ;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RequestPermissionCode){

            if (grantResults.length > 0) {


                boolean ExternalPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean CameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                // boolean ExternalPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                // boolean ReadPhoneStatePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (ExternalPermission && CameraPermission) {
                    selectImage();

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setTitle("Need Camera and Storage Permission");
                    builder.setMessage("This app need Camera and Storage permission.");
                    builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            // sentToSettings = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getBaseContext(), "Go to Permissions to Grant Camera and Storage", Toast.LENGTH_LONG).show();

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


    //Fetch Profile Data
    private void getProfile() {

        final ProgressDialog pDialog = new ProgressDialog(EditProfileActivity.this);
        pDialog.setMessage(getResources().getString(R.string.fetchingdata));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();

            jsonObject1.put("driver_id", driverId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "Profile_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.GET_DRIVER_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.GET_DRIVER_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Profile_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                JSONArray jsonArray=response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);
                                    editTextName.setText(jobj.getString("name"));
                                    editTextPhone.setText(jobj.getString("mobile_no"));
                                    // spinner_loc.setSelection(city_id);
                                    //editTextLocation.setText(jobj.getString("city_name"));
                                    //String array[]=jobj.getString("vehicle_no").split(" ");
                                    //editTextVehicleCity.setText(array[0]);

                                    int position = jobj.getString("name").length();
                                    //Editable etext = etmsg.getText();
                                    editTextName.setSelection(position);

                                    editTextVehicle.setText(jobj.getString("vehicle_no"));
                                    Log.d("imageView",jobj.getString("profile_img"));
                                    profilePicUrl=jobj.getString("profile_img");




                                }
                                for(int i=0;i<jsonArray.length();i++) {
                                    if (cityId == cityList.get(i).getCityId()){
                                        spinner_loc.setSelection(i);
                                        break;

                                    }
                                }

                                Log.i("Result","ProfilePicUrl: " + profilePicUrl);




                               /* Glide.with(EditProfileActivity.this)
                                        .asBitmap()
                                        .load(profilePicUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        // .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                img_profile.setImageBitmap(resource);
                                                encodeBitmapImage(resource);

                                            }
                                        });*/

                                Glide.with(EditProfileActivity.this)
                                        .asBitmap()
                                        .load(profilePicUrl+"?ver=5436789")

                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                img_profile.setImageBitmap(resource);
                                                encodeBitmapImage(resource);

                                            }
                                        });}

                            /*Bitmap bitmap= Glide.
                                    with(EditProfileActivity.this).
                                    load(profilePicUrl).
                                    asBitmap().
                                    into(100, 100). // Width and height
                                    get();
                            encodeBitmapImage(bitmap);*/


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                // Toast.makeText(CameraActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditProfileActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    //Update Profile Data
    private void updateProfile() {

        final ProgressDialog pDialog = new ProgressDialog(EditProfileActivity.this);
        pDialog.setMessage(getResources().getString(R.string.updateprofile));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;

        try {

            jsonObject1 = new JSONObject();

            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("name", editTextName.getText().toString().trim());
            //jsonObject1.put("city_id",cityId+"");
            //jsonObject1.put("vehical_no", editTextVehicle.getText().toString().trim());
            jsonObject1.put("profile_img", encodedImage+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "UpdateProfile_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.UPDATE_PROFILE_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.UPDATE_PROFILE_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Profile_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                JSONArray jsonArray=response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jobj=jsonArray.getJSONObject(i);
                                   // profile_img=jobj.getString("profile_img");

                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                // Toast.makeText(CameraActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditProfileActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }


    private void getCityList() {

        final ProgressDialog pDialog = new ProgressDialog(EditProfileActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;



        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("ss", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // jsonObject1.put("filename",);
        Log.d(TAG, "city_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.FIND_CITY_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.FIND_CITY_URL, jsonObject1,
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

                                /*Bean b1 = new Bean();
                                b1.setCityName("Select City");
                                b1.setCityId("");
                                b1.setRtoCode("");
                                cityList.add(b1);*/

                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int j=0;j<jsonArray.length();j++){


                                    JSONObject jsonObject=jsonArray.getJSONObject(j);
                                    Bean b = new Bean();
                                    b.setCityName(jsonObject.getString("city_name"));
                                    b.setCityId(jsonObject.getString("city_id"));
                                    b.setRtoCode(jsonObject.getString("rto_code"));
                                    cityList.add(b);

                                }

                                addCitySpinner();

                                getProfile();

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
                    Toast.makeText(EditProfileActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    private void addCitySpinner() {
        Log.d("Customer_Detail","branchDetails_size "+cityList.size()+"");
        /** Create custom adapter object ( see below CustomAdapter.java )*/
        adapter = new CustomAdapter(EditProfileActivity.this, R.layout.spinner_row,cityList,"city");
        spinner_loc.setEnabled(false);
        spinner_loc.setClickable(false);
        // Set adapter to spinner
        spinner_loc.setAdapter(adapter);
    }



    private void showFileChooser() {

        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");

        if (Build.VERSION.SDK_INT <19){
            //allows to select data and return it
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            //starts new activity to select file and return data
            startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
        } else {

            //allows to select data and return it
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            //starts new activity to select file and return data
            startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
        }

    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {


                    // call android default camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {

                        // Create the File where the photo should go

                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        SharedPreferences.Editor edt = sharedPreferences.edit();
                        edt.putString("fileUri", fileUri.toString());
                        edt.commit();


                        // Continue only if the File was successfully created
                        if (fileUri != null) {

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                            startActivityForResult(intent, CAMERA_REQUEST);

                        }
                    }

                } else if (items[item].equals("Choose from Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    // Intent intent = new Intent();
                    // Intent intent = new Intent("com.android.camera.action.CROP");
                    // call android default gallery
                    intent.setType("image/*");
                    // intent.setAction(Intent.ACTION_GET_CONTENT);
                    // ******** code for crop image
                    try {
                        //intent.putExtra("return-data", true);
                       /* startActivityForResult(Intent.createChooser(intent,
                                "Complete action using"), PICK_FROM_GALLERY);*/
                        startActivityForResult(intent, PICK_FROM_GALLERY);

                    } catch (ActivityNotFoundException e) {
                        // Do nothing for now
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    /**
     * Creating file uri to store image
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(
                getOutputMediaFile(type));
    }

    /**
     * returning image
     */
    private File getOutputMediaFile(int type) {

        // External sdcard location
        mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        if (type == MEDIA_TYPE_IMAGE) {
            String ImageName = "IMG_" + timeStamp /*+ ".jpg"*/;

            //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            try {
                mediaFile = File.createTempFile(
                        ImageName,  /* prefix */
                        ".jpg",         /* suffix */
                        mediaStorageDir      /* directory */
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            return null;
        }


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = mediaFile.getAbsolutePath();

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("mCurrentPhotoPath", mCurrentPhotoPath);
        edit.commit();

        Log.d("mpath", mCurrentPhotoPath);
        //this code is required to show pic in gallery

        /** this code is required to show pic from gallery otherwise it will not show pic from gallary*/
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);


        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        EditProfileActivity.this.sendBroadcast(mediaScanIntent);
      /*  } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
        }*/

        return mediaFile;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FROM_GALLERY) {
            try {

                //  Bundle extras2 = data.getExtras();
                if (data != null) {

                    //ll.setVisibility(View.VISIBLE);

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


                    Uri uriPhoto = data.getData();
                    Log.d(TAG , "PICK_GALLERY"+ "Selected image uri path :" + uriPhoto.toString());
                    //sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));

                    img_profile.setImageURI(uriPhoto);

                    //saveImage(bitmap);
                    //img_profile.setImageBitmap(bitmap);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

                //ll.setVisibility(View.VISIBLE);

                fileUri= Uri.parse(sharedPreferences.getString("fileUri",""));
                String filepath = fileUri.toString();
                // String filepath = fileUri.toString();

                String fileNameSegments[] = filepath.split("/");
                String fileName = fileNameSegments[fileNameSegments.length - 1];

                /** This function delete copy of image which is saved in DCIM folder by default..
                 * So it will not show twice images
                 * **/
                // deleteLatest();

                mCurrentPhotoPath=sharedPreferences.getString("mCurrentPhotoPath","");
                Log.d("fileuripath", fileUri.getPath());


                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap12 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

                //Bitmap bitmap1 = scaleImage(bitmap12);
                saveImage(bitmap12);

                img_profile.setImageBitmap(bitmap12);


            } else if (requestCode == PIC_CROP) {

            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    private Bitmap scaleImage(Bitmap bitMap) {


        int currentBitmapWidth = bitMap.getWidth();
        int currentBitmapHeight = bitMap.getHeight();

        int ivWidth = 600;
        int ivHeight = 600;
        int newWidth = ivWidth;

        int newHeight = (int) Math.floor((double) currentBitmapHeight * ((double) newWidth / (double) currentBitmapWidth));

        Bitmap newbitMap = Bitmap.createScaledBitmap(bitMap, newWidth, ivHeight, true);

        //imageView.setImageBitmap(newbitMap);
        return newbitMap;

    }


    private void saveImage(Bitmap bitmap) {


        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Riksha");

        if (!myDir.exists()) {
            if (!myDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");

            }
        } else {

            /** save compress image from original images of  four_point folder */
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fname = "FourPointImage-" + timeStamp + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                /** save image path */
                String gallaryimgpath = myDir.getAbsolutePath() + "/" + fname;


                /** Encode Image  */
                encodeBitmapImage(bitmap);

                //Toast.makeText(getApplicationContext(),"Save",Toast.LENGTH_SHORT).show();

                // File dir = new File(Environment.getExternalStorageDirectory()+"Dir_name_here");

                /** Delete original file from fileuri path to avoid duplicate image.. compress image will be in four_point_images folder **/
                if (myDir.isDirectory()) {
                    String[] children = myDir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(myDir, children[i]).delete();
                    }
                }
                /** after delete file it will show no thumbnail..
                 *  to remove thumblnail use follwing function. so it won't show in gallary*/
                deleteFileFromMediaStore(this.getContentResolver(), myDir);

              /*  if (mediaStorageDir.isDirectory()) {
                    String[] children = mediaStorageDir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(mediaStorageDir, children[i]).delete();
                    }
                }
                *//** after delete file it will show no thumbnail..
                 *  to remove thumblnail use follwing function. so it won't show in gallary*//*
                deleteFileFromMediaStore(this.getContentResolver(), mediaStorageDir);*/


            } catch (Exception e) {
                e.printStackTrace();
            }
            /** when new image saved.. it doesnt show in gallary.. to show image use following line*/
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }
    }

    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();

        /** Encode Image to String.. to send server*/
        encodedImage = Base64.encodeToString(byte_arr, 0);

    }


    /**
     * after delete file it will show thumbnail pic..
     * to remove thumblnail use follwing function. so it won't show in gallary
     */
    public void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
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
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }


   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    //useful for hiding the soft-keyboard is:
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }*/
}
