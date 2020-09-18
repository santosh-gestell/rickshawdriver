package com.tretakalp.Rikshaapp.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tretakalp.Rikshaapp.Other.AppController;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.Other.DetectConnection;
import com.tretakalp.Rikshaapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Mac on 03/09/18.
 */

/**
 * Created by Mac on 03/09/18.
 */
public class BankDetailActivity extends AppCompatActivity {


    private static final int PICK_FILE_REQUEST = 1;
    public static final int RequestPermissionCode = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final String TAG ="BankDetailActivity" ;
    private Uri fileUri;
    private String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "Riksha";
    public static final int MEDIA_TYPE_IMAGE = 1;
    File mediaFile;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PIC_CROP = 3;
    File mediaStorageDir;
    String encodedImage="";
    private static final int REQUEST_PERMISSION_SETTING = 101;

    SharedPreferences sharedPreferences;
    ImageView img_document, imgSelectDate;
    Button btnSave,btnRetake;

    String type;
    EditText edtNumber,edtifsc, etExpiryDate;
    TextView txtConfirm,txt1,txtClick;
    LinearLayout ll;
    String docName,docIfsc="",docNumber="",docImageUrl="";
    String driverId;
    SharedPreferences pref;
    ProgressBar progressBar;
    Button btnClick;
    Dialog dialog1;
    Bitmap imageBitmap;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String expDate="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.bankdetails);
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideKeyboardFrom(BankDetailActivity.this);
                onBackPressed();

            }
        });


        pref=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");
        Log.d(TAG,"BankImage:"+pref.getString(Constant.BANK_IMAGE+driverId,""));
        setAllView();


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle b=getIntent().getExtras();
        type=b.getString("type");
        try {
            docNumber=b.getString("docNumber");
            docIfsc=b.getString("docIfsc");
            docImageUrl=b.getString("docImageUrl");
            Log.d("bankdetails_docIfsc",docIfsc+"");
        }catch (Exception e){

        }



        switch (type){

            case "bank":
                //toolbar.setTitle("Document Details");
                edtNumber.setHint(R.string.accountnumber);
                txtConfirm.setText(R.string.makesurebankdetails);
                txt1.setText(R.string.takephotobankacc);
                img_document.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rickpassbook));
                docName="bank_account";
                if(docNumber!=null) {
                    if (!docNumber.equals("")) {

                        Log.d("bankdetails",docNumber+"");

                        edtNumber.setText(docNumber + "");
                        edtifsc.setText(docIfsc + "");
                        edtifsc.setSelection(edtifsc.getText().length());
                        edtNumber.setSelection(edtNumber.getText().length());
                        progressBar.setVisibility(View.VISIBLE);

                        Glide.with(BankDetailActivity.this)
                                .asBitmap()
                                .load(docImageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                // .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)

                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        progressBar.setVisibility(View.GONE);
                                        img_document.setImageBitmap(resource);
                                        encodeBitmapImage(resource);
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        super.onLoadFailed(errorDrawable);
                                        img_document.setImageDrawable(ContextCompat.getDrawable(BankDetailActivity.this, R.drawable.rickpassbook));
                                    }


                                });


                        ll.setVisibility(View.VISIBLE);
                        txt1.setVisibility(View.GONE);
                        txtClick.setVisibility(View.GONE);
                        btnClick.setVisibility(View.GONE);

                    }else{

                        if(!pref.getString(Constant.BANK_ACCOUNT+driverId,"").equals("")) {

                            docNumber=pref.getString(Constant.BANK_ACCOUNT+driverId,"");
                            docIfsc=pref.getString(Constant.BANK_IFSC+driverId,"");
                            encodedImage=pref.getString(Constant.BANK_IMAGE+driverId,"");
                            setShoworHideview(docNumber,encodedImage,docIfsc);
                            Log.d(TAG,"dl"+" 2");

                        }
                    }
                }else{

                    if(!pref.getString(Constant.BANK_ACCOUNT+driverId,"").equals("")) {

                        docNumber=pref.getString(Constant.BANK_ACCOUNT+driverId,"");
                        docIfsc=pref.getString(Constant.BANK_IFSC+driverId,"");
                        encodedImage=pref.getString(Constant.BANK_IMAGE+driverId,"");
                        setShoworHideview(docNumber,encodedImage,docIfsc);
                        Log.d(TAG,"dl"+" 2");

                    }



                }



                break;

        }



        img_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if( checkPermission()) {
                    selectImage();
                }else {
                    requestPermission();
                }*/
            }
        });

       btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( checkPermission()) {
                    //selectImage();
                    showDialogSelectImage();
                }else {
                    requestPermission();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*Intent i = new Intent();
                i.putExtra("type",type);
                setResult(RESULT_OK, i);
                finish();*/

                if(edtNumber.getText().toString().trim().equals("")||edtifsc.getText().toString().trim().equals("")||encodedImage.equals("")) {

                    Toast.makeText(BankDetailActivity.this, R.string.pleasefillfield, Toast.LENGTH_SHORT).show();

                }else if(edtifsc.getText().toString().equals("")){

                    Toast.makeText(BankDetailActivity.this, R.string.plzenterifsc, Toast.LENGTH_SHORT).show();

                }else if(encodedImage.trim().equals("")) {
                    Toast.makeText(BankDetailActivity.this, R.string.selectImg, Toast.LENGTH_SHORT).show();
                }

//                else if(etExpiryDate.getText().toString().trim().equals("")) {
//                    Toast.makeText(BankDetailActivity.this, "Enter Expiry Date", Toast.LENGTH_SHORT).show();
//
//                }
                else {
                        if (DetectConnection.checkInternetConnection(BankDetailActivity.this)) {

                            sendDocUrl(docName, edtNumber.getText().toString().trim(), edtifsc.getText().toString().trim());
                            //sendDocUrlMultipart(imageBitmap, docName, edtNumber.getText().toString().trim(), edtifsc.getText().toString().trim());
                        } else {
                            Toast.makeText(BankDetailActivity.this, R.string.noInternet, Toast.LENGTH_LONG).show();
                        }
                    }
            }

        });

        txtClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( checkPermission()) {
                    //selectImage();
                    showDialogSelectImage();
                }else {
                    requestPermission();
                }
            }
        });
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( checkPermission()) {
                    //selectImage();
                    showDialogSelectImage();
                }else {
                    requestPermission();
                }
            }
        });
    }



    private void setShoworHideview(String docNumber, String encodedImage, String docIfsc) {

        edtNumber.setText(docNumber + "");
        edtifsc.setText(docIfsc + "");
        edtifsc.setSelection(edtifsc.getText().length());
        edtNumber.setSelection(edtNumber.getText().length());

        Bitmap bitmap=  getBitmapImage(encodedImage);
        img_document.setImageBitmap(bitmap);

        ll.setVisibility(View.VISIBLE);
        txt1.setVisibility(View.GONE);
        txtClick.setVisibility(View.GONE);
        btnClick.setVisibility(View.GONE);

    }

    private Bitmap getBitmapImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return  decodedByte;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(BankDetailActivity.this, new String[]
                {
                        WRITE_EXTERNAL_STORAGE,
                        CAMERA

                }, RequestPermissionCode);
    }


    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();

        /** Encode Image to String.. to send server */
        encodedImage = Base64.encodeToString(byte_arr, 0);

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
                    //selectImage();
                    showDialogSelectImage();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(BankDetailActivity.this);
                    builder.setTitle("Need Camera and Storage Permission");
                    builder.setMessage("This app need Camera and Storage permission.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
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
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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



    private void setAllView() {

        img_document=findViewById(R.id.img_document);
        sharedPreferences=getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE);
        btnRetake=findViewById(R.id.btnRetake);
        imgSelectDate=findViewById(R.id.imageIconCalender);
        btnSave=findViewById(R.id.btnSave);
        etExpiryDate=findViewById(R.id.edtExprityDate);
        edtNumber=findViewById(R.id.edtNumber);
        edtifsc=findViewById(R.id.edtifsc);
        txtConfirm=findViewById(R.id.txtConfirm);
        ll=findViewById(R.id.ll);
        txt1=findViewById(R.id.txt1);
        txtClick=findViewById(R.id.txtClick);
        btnClick=findViewById(R.id.btnClick);
        btnRetake=findViewById(R.id.btnRetake);
        progressBar=findViewById(R.id.progress);

        imgSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( checkPermission()) {
                    getDate();
                }else {
                    requestPermission();
                }
            }
        });

    }

    private void getDate() {
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        etExpiryDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void sendDocUrlMultipart(final Bitmap imageBitmap, final String docName, final String docNo, final String ifscNo) {
        final ProgressDialog pDialog = new ProgressDialog(BankDetailActivity.this);
        pDialog.setMessage(getResources().getString(R.string.uploading));
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d(TAG, "URl" + Constant.ADD_DOCUMENT_URL);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.ADD_DOCUMENT_URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                    pDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(new String(response.data));
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("GotError",""+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("driver_id", driverId);
                param.put("doc_name", docName);
                //jsonObject1.put("expiry_date", expDate);
                //jsonObject1.put("bank_account", docNo);
                param.put("doc_no", docNo);
                param.put("ifsc_no",ifscNo);
                return param;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("doc_img", new DataPart(imagename + ".png", getFileDataFromDrawable(imageBitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    };

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void sendDocUrl(String docName,String docNo,String ifscNo) {

        final ProgressDialog pDialog = new ProgressDialog(BankDetailActivity.this);
        pDialog.setMessage(getResources().getString(R.string.uploading));
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;


        try {

            jsonObject1 = new JSONObject();
            // jsonObject1.put("driver_id", driverId);
            jsonObject1.put("driver_id", driverId);
            jsonObject1.put("doc_name", docName);
            jsonObject1.put("expiry_date", expDate);
            //jsonObject1.put("bank_account", docNo);
            jsonObject1.put("doc_no", docNo);
            jsonObject1.put("ifsc_no",ifscNo);
            jsonObject1.put("doc_img",encodedImage.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // jsonObject1.put("filename",);
        Log.d(TAG, "Camera_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.ADD_DOCUMENT_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.ADD_DOCUMENT_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();

                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "camera_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                SharedPreferences.Editor edt=pref.edit();
                                edt.putString(Constant.BANK_ACCOUNT+driverId,edtNumber.getText().toString().trim()+"");
                                edt.putString(Constant.BANK_IFSC+driverId,edtifsc.getText().toString().trim()+"");
                                edt.putString(Constant.BANK_IMAGE+driverId,encodedImage+"");
                                edt.commit();

                                Intent i = new Intent();
                                i.putExtra("type",type);
                                setResult(RESULT_OK, i);
                                finish();
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
                    Toast.makeText(BankDetailActivity.this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
                }


                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
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
            startActivityForResult(Intent.createChooser(intent,getResources().getString(R.string.choosefiletoupload)),PICK_FILE_REQUEST);
        } else {

            //allows to select data and return it
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            //starts new activity to select file and return data
            startActivityForResult(Intent.createChooser(intent,getResources().getString(R.string.choosefiletoupload)),PICK_FILE_REQUEST);
        }

    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery"/*,
                "Cancel"*/};


        AlertDialog.Builder builder = new AlertDialog.Builder(BankDetailActivity.this);
        builder.setTitle(R.string.addphoto);
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
                            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
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

                } /*else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }*/
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dialog.dismiss();
            }
        });
        builder.show();
    }


    private void showDialogSelectImage() {


        dialog1 = new Dialog(BankDetailActivity.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialogue_camera_gallary);

        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) dialog1.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.takephoto);



        Button btn_cancel = dialog1.findViewById(R.id.btn_cancel);
        ImageView imgCamera=  dialog1.findViewById(R.id.imgCamera);
        ImageView imgGallary=  dialog1.findViewById(R.id.imgGallary);

        imgGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                try {

                    startActivityForResult(intent, PICK_FROM_GALLERY);

                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }

                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialog1.dismiss();

            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        startActivityForResult(intent, CAMERA_REQUEST);

                    }
                }
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialog1.dismiss();

            }
        });



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                dialog1.dismiss();

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
        BankDetailActivity.this.sendBroadcast(mediaScanIntent);
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

                    ll.setVisibility(View.VISIBLE);

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageBitmap = bitmap;
                    saveImage(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    long sizeOfImage = imageInByte.length; //Image size


                    Log.d(TAG,"Bitmap Size: " + sizeOfImage/ 1024+"kb");



                    img_document.setImageBitmap(bitmap);
                    txt1.setVisibility(View.GONE);
                    txtClick.setVisibility(View.GONE);
                    btnClick.setVisibility(View.GONE);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

                ll.setVisibility(View.VISIBLE);

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

                img_document.setImageBitmap(bitmap12);
                txt1.setVisibility(View.GONE);
                txtClick.setVisibility(View.GONE);
                btnClick.setVisibility(View.GONE);

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

                /** Compress Image File*/
                Bitmap compressedImageBitmap = new Compressor(this).compressToBitmap(new File(gallaryimgpath));

                /** Encode Image  */
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
               // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byte_arr = stream.toByteArray();

                /** Encode Image to String.. to send server*/
                encodedImage = Base64.encodeToString(byte_arr, 0);

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

}
