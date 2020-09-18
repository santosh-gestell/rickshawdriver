package com.tretakalp.Rikshaapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.tretakalp.Rikshaapp.R;

/**
 * Created by Mac on 07/09/18.
 */
public class DemoFragment extends Fragment {

    View v;
    Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.demo1, container, false);


        toolBarSetUp();
        return v;


    }

    private void toolBarSetUp() {

       // toolbar = (Toolbar) v.findViewById(R.id.toolbar);
       // ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  toolbar.setTitle("Demo");
       // toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Demo");
       // ((AppCompatActivity)getActivity()).getSupportActionBar().sette

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onBackPressed();


            }
        });*/
    }

}
