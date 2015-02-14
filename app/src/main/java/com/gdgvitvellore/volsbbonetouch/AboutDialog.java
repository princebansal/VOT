package com.gdgvitvellore.volsbbonetouch;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by shalini on 01-02-2015.
 */
public class AboutDialog extends ActionBarActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        LayoutInflater inflater= LayoutInflater.from(this);
        View cv=inflater.inflate(R.layout.custombar,null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#394264")));
        getSupportActionBar().setCustomView(cv);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        tv=(TextView)findViewById(R.id.fulltv);
        tv.setText(R.string.fulltv);
    }
}
