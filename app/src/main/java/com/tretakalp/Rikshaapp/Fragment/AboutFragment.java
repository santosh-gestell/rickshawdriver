package com.tretakalp.Rikshaapp.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.tretakalp.Rikshaapp.Activity.MapActivity;
import com.tretakalp.Rikshaapp.R;

/**
 * Created by Mac on 04/09/18.
 */
public class AboutFragment extends Fragment {

    TextView mytextview,mytxtview2,mytxtview3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);

        mytextview=view.findViewById(R.id.mytxt1);
        mytxtview2=view.findViewById(R.id.mytxt2);
        mytxtview3=view.findViewById(R.id.mytxt3);
        String sourceString = "<b><ul><li>Passenger can get multiple ways to pay: </li></ul></b> Passenger can Pay for your trips in cash or via multiple cashless options like Debit card, Credit card, UPI, 50+ Netbanking, 17+ Mobile wallets etc";
        mytextview.setText(Html.fromHtml(sourceString));

        String sourceString2 = "<b><ul><li>Know fares and ride features : </li></ul></b>Check fares and various features of a ride category before booking";
        mytxtview2.setText(Html.fromHtml(sourceString2));
        String sourceString3 = "<b><ul><li>Travel with safety: </li></ul></b>Passenger can share your travel plan with family and friends so they can track your vehicle and know passenger is safe";
         mytxtview3.setText(Html.fromHtml(sourceString3));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_about_us));
    }
}
