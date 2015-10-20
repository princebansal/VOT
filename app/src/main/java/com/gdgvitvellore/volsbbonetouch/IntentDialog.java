package com.gdgvitvellore.volsbbonetouch;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;


/**
 * Created by shalini on 01-02-2015.
 */
public class IntentDialog extends Dialog implements View.OnClickListener {
    private ImageView call,email;
    private Context c;
    private SharedPreferences sharedPreferences;
    private String emailSubject,emailContent;
    String[] emailAddress;
    private String callNumber="9585556071";
    public IntentDialog(Context context) {
        super(context);
        c=context;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(c);
        emailSubject=c.getResources().getString(R.string.email_subject);
        emailContent=c.getResources().getString(R.string.email_content);
        emailAddress=new String[1];
        emailAddress[0]=c.getResources().getString(R.string.email_address);
        emailContent=emailContent+sharedPreferences.getString("prontousername","null");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_intent);
        call=(ImageView)findViewById(R.id.call);
        email=(ImageView)findViewById(R.id.mail);
        call.setOnClickListener(this);
        email.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.call:
                TelephonyManager telephonyManager= (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);

                if(telephonyManager.getLine1Number()!=null) {
                    String uri = "tel:" + callNumber.trim();
                    Intent callintent = new Intent(Intent.ACTION_DIAL);
                    callintent.setData(Uri.parse(uri));
                    c.startActivity(callintent);
                }
                else{

                    Toast.makeText(c,"No calling feature found",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.mail:
            if(!sharedPreferences.getString("prontousername","null").equals("null")) {
                Intent emailintent = new Intent(Intent.ACTION_SEND);
                emailintent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                emailintent.putExtra(Intent.EXTRA_TEXT, emailContent);
                emailintent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
                emailintent.setType("message/rfc822");
                c.startActivity(emailintent);
            }
                else
            {
                Toast.makeText(c,"No username found",Toast.LENGTH_SHORT).show();
            }
                break;
        }
    }
}
