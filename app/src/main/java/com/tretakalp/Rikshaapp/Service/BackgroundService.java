package com.tretakalp.Rikshaapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created by Mac on 07/09/18.
 */
public class BackgroundService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {





        stopSelf();


        return START_NOT_STICKY;
    }


   /* private void signUpUrl() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sign in...");
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject jsonObject1 = null;


        try {
            jsonObject1 = new JSONObject();
            jsonObject1.put("name", edtName.getText().toString().trim());
            jsonObject1.put("mobile_no",edtMob.getText().toString().trim());
            jsonObject1.put("city_id",edtLoc.getText().toString().trim());
            jsonObject1.put("password",edtPass.getText().toString().trim());
            jsonObject1.put("vehical_no",edtvehicle.getText().toString().trim());
            jsonObject1.put("referral_code",edtRefCod.getText().toString().trim());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // jsonObject1.put("filename",);
        Log.d(TAG, "SignUp_data" + jsonObject1.toString());
        Log.d(TAG, "URl" + Constant.SIGN_UP_URL);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constant.SIGN_UP_URL, jsonObject1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "");

                        pDialog.dismiss();
                        try {
                            String result = response.getString("status_code");
                            String msg = response.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signup_Submit_result" + result);
                            if (result.trim().equals("200")) {

                                String  driverId="";
                                JSONArray jsonArray= response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){

                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    driverId=jsonObject.getString("driver_id");


                                }
                                SharedPreferences.Editor edit=pref.edit();
                                edit.putString(Constant.DRIVER_ID,driverId);
                                edit.commit();



                                startActivity(new Intent(SignUpActivity.this,DocumentActivity.class));
                                finish();
                                // showDialogBox();
                                dialog1.dismiss();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Network Problem,please try again", Toast.LENGTH_SHORT).show();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                error.printStackTrace();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }*/

}
