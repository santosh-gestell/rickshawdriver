package com.tretakalp.Rikshaapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tretakalp.Rikshaapp.Activity.IssueActivity;
import com.tretakalp.Rikshaapp.Activity.MapActivity;

import com.tretakalp.Rikshaapp.Activity.TopicTwo;
import com.tretakalp.Rikshaapp.Adpter.ExpandableHelpAdapter;
import com.tretakalp.Rikshaapp.Database.DatabaseHelper;
import com.tretakalp.Rikshaapp.Model.Bean;
import com.tretakalp.Rikshaapp.Other.Constant;
import com.tretakalp.Rikshaapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mac on 25/09/18.
 */
public class HelpFragment  extends Fragment {


    View v;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    //ArrayList<Bean> tripList=new ArrayList<>();

   ExpandableHelpAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild=new HashMap<>();
    HashMap<Bean, List<String>> triplistDataChild;

    List<Bean> tripList=new ArrayList<>();
    List<Bean> tripDateList=new ArrayList<>();
    List<Bean> tripCountList=new ArrayList<>();
    String driverId;
    SharedPreferences pref;

    DatabaseHelper db;
    CardView cardView_SignIn,cardv_issue;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.help_layout,container,false);
        db=DatabaseHelper.getInstance(getActivity());
        cardView_SignIn=v.findViewById(R.id.card_view2);
        cardv_issue=v.findViewById(R.id.card_issue);

        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.lvExp);
        expListView.setVisibility(View.VISIBLE);
        pref=getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        driverId=pref.getString(Constant.DRIVER_ID,"");

        // preparing list data
        prepareListData1();


        listAdapter = new ExpandableHelpAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);




        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                    if(childPosition==1) {
                        Intent ii = new Intent(getActivity(), TopicTwo.class);
                        ii.putExtra("url",Constant.TOPIC_TWO_URL);
                        ii.putExtra("title",getResources().getString(R.string.topic_two));
                        startActivity(ii);
                    }else {
                        Intent ii = new Intent(getActivity(),TopicTwo.class);
                        ii.putExtra("url",Constant.TOPIC_ONE_URL);
                        ii.putExtra("title",getResources().getString(R.string.topic_one));
                        startActivity(ii);
                    }

                return false;
            }
        });

        cardView_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(getActivity(), TopicTwo.class);
                ii.putExtra("url",Constant.SIGN_IN_URL);
                ii.putExtra("title",getResources().getString(R.string.signIn));
                startActivity(ii);

            }

        });


        cardv_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(getActivity(), IssueActivity.class);
                startActivity(ii);

            }
        });


        //showList();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MapActivity)getActivity()).changeToolbarTitle(getResources().getString(R.string.nav_help));
        ((MapActivity)getActivity()).loadLocale();
    }


    private void prepareListData1() {
        listDataHeader = new ArrayList<String>();

        listDataHeader.add(getResources().getString(R.string.usingapplicationhelp));


        List<String> top250 = new ArrayList<String>();
        top250.add(getResources().getString(R.string.topic_one));
        top250.add(getResources().getString(R.string.topic_two));


        listDataChild.put(listDataHeader.get(0), top250); // Heade




    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
    }

    //Get locale method in preferences
    public void loadLocale() {
        SharedPreferences pref=getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());//Update the config
        // updateTexts();//Update texts according to locale
    }




}
