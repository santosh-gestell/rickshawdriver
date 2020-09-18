/*
package com.tretakalp.Rikshaapp.Other;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tretakalp.Rikshaapp.Activity.CameraActivity;
import com.tretakalp.Rikshaapp.Activity.ProfilePicActivity;
import com.tretakalp.Rikshaapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

*/
/**
 * Created by Mac on 06/09/18.
 *//*

public class CameraClass  {

    Activity activity;
    CameraClass(Activity activity){
        this.activity=activity;

    }




    private void selectImage(final Activity activity) {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {


                    // call android default camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {

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
                    intent.setType("image*/
/*");
                    // intent.setAction(Intent.ACTION_GET_CONTENT);
                    // ******** code for crop image
                    try {
                        //intent.putExtra("return-data", true);
                       */
/* startActivityForResult(Intent.createChooser(intent,
                                "Complete action using"), PICK_FROM_GALLERY);*//*

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


    */
/**
     * Creating file uri to store image
     *//*

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(
                getOutputMediaFile(type));
    }

    */
/**
     * returning image
     *//*

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
            String ImageName = "IMG_" + timeStamp */
/*+ ".jpg"*//*
;

            //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            try {
                mediaFile = File.createTempFile(
                        ImageName,  */
/* prefix *//*

                        ".jpg",         */
/* suffix *//*

                        mediaStorageDir      */
/* directory *//*

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

        */
/** this code is required to show pic from gallery otherwise it will not show pic from gallary*//*

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);


        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        ProfilePicActivity.this.sendBroadcast(mediaScanIntent);
      */
/*  } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
        }*//*


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

                    saveImage(bitmap);
                    img_document.setImageBitmap(bitmap);

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

                */
/** This function delete copy of image which is saved in DCIM folder by default..
                 * So it will not show twice images
                 * **//*

                // deleteLatest();

                mCurrentPhotoPath=sharedPreferences.getString("mCurrentPhotoPath","");
                Log.d("fileuripath", fileUri.getPath());


                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap12 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

                //Bitmap bitmap1 = scaleImage(bitmap12);
                saveImage(bitmap12);

                img_document.setImageBitmap(bitmap12);


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

            */
/** save compress image from original images of  four_point folder *//*

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

                */
/** save image path *//*

                String gallaryimgpath = myDir.getAbsolutePath() + "/" + fname;








                */
/** Encode Image  *//*

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byte_arr = stream.toByteArray();

                */
/** Encode Image to String.. to send server*//*

                encodedImage = Base64.encodeToString(byte_arr, 0);

                //Toast.makeText(getApplicationContext(),"Save",Toast.LENGTH_SHORT).show();

                // File dir = new File(Environment.getExternalStorageDirectory()+"Dir_name_here");

                */
/** Delete original file from fileuri path to avoid duplicate image.. compress image will be in four_point_images folder **//*

                if (myDir.isDirectory()) {
                    String[] children = myDir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(myDir, children[i]).delete();
                    }
                }
                */
/** after delete file it will show no thumbnail..
                 *  to remove thumblnail use follwing function. so it won't show in gallary*//*

                deleteFileFromMediaStore(this.getContentResolver(), myDir);

              */
/*  if (mediaStorageDir.isDirectory()) {
                    String[] children = mediaStorageDir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(mediaStorageDir, children[i]).delete();
                    }
                }
                *//*
*/
/** after delete file it will show no thumbnail..
                 *  to remove thumblnail use follwing function. so it won't show in gallary*//*
*/
/*
                deleteFileFromMediaStore(this.getContentResolver(), mediaStorageDir);*//*



            } catch (Exception e) {
                e.printStackTrace();
            }
            */
/** when new image saved.. it doesnt show in gallary.. to show image use following line*//*

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }
    }


    */
/**
     * after delete file it will show thumbnail pic..
     * to remove thumblnail use follwing function. so it won't show in gallary
     *//*

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


}
*/
