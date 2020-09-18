/*
package com.tretakalp.Rikshaapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tretakalp.Rikshaapp.R;

*/
/**
 * Created by Mac on 03/09/18.
 *//*

public class BadgeNumberActivity extends AppCompatActivity {
    Button btnSave;
    EditText edBadge;

    String type="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badge_number);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Badge Number");
        toolbar.setTitleTextColor(Color.parseColor("#343F4B"));
        toolbar.setTitleTextAppearance(this, R.style.toolbarFont);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        btnSave=findViewById(R.id.btnNext);
        edBadge=findViewById(R.id.edtBadgeNumber);
        Bundle b=getIntent().getExtras();
        type=b.getString("type");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!edBadge.getText().toString().equals("")) {
                    //startActivity(new Intent(BadgeNumberActivity.this, DocumentActivity.class));
                    Intent i = new Intent();
                    i.putExtra("type",type);
                    setResult(RESULT_OK, i);
                    finish();

                }else{

                    Toast.makeText(BadgeNumberActivity.this,"Please Enter Badge Number",Toast.LENGTH_SHORT).show();

                }
            }
        });





    }
}
*/
