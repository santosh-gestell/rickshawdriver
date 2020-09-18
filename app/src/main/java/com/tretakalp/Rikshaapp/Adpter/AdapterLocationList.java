package com.tretakalp.Rikshaapp.Adpter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tretakalp.Rikshaapp.Model.PlaceModule;
import com.tretakalp.Rikshaapp.R;

import java.util.List;

public class AdapterLocationList extends ArrayAdapter<PlaceModule> {

    Context context;
    List<PlaceModule> placeModuleList;
    public AdapterLocationList(Context context, List<PlaceModule> placeModuleList) {
        super(context, 0, placeModuleList);
        this.placeModuleList = placeModuleList;
        this.context = context;


    }


    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        PlaceModule placeModule = placeModuleList.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.locationdialog_rowlist, parent, false);

        TextView text = (TextView) convertView.findViewById(R.id.textPlaceModule);
        text.setText(placeModule.getPlaceName());
        Log.d("mytag","dataname : "+placeModule.getPlaceName());

        return convertView;
    }


    public interface AdapterLocationListListener {

    }
}
