package com.tretakalp.Rikshaapp.Adpter;

/**
 * Created by Mac on 27/05/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.R;

import java.util.ArrayList;

/***** Adapter class extends with ArrayAdapter ******/
public class CustomAdapter extends ArrayAdapter<Bean>{

    public Resources res;
    Bean tempValues=null;
    LayoutInflater inflater;
    private Activity activity;
    private ArrayList data;
    private String key;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList objects,String key)
            //Resources resLocal)
    {
        super(activitySpinner, textViewResourceId, objects);
        /********** Take passed values **********/
        activity = activitySpinner;
        data     = objects;
        this.key=key;
       // res      = resLocal;

        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_row, parent, false);

        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (Bean) data.get(position);

        TextView label        = (TextView)row.findViewById(R.id.cust_view);

            // Set values for spinner each row
            if(key.equals("city")) {
                label.setText(((Bean) data.get(position)).getCityName());
            }else if(key.equals("reason")){
                label.setText(((Bean) data.get(position)).getReason());
            }

        return row;
    }
}