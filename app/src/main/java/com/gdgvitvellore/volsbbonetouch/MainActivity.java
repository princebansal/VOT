package com.gdgvitvellore.volsbbonetouch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener{
    private String url = "http://phc.prontonetworks.com/cgi-bin/authlogin?URI=http://www.msftncsi.com/redirect";
    EditText uname, password;
    TextView res;
    Button login, logout, slogin;
    List<NameValuePair> details;
    ProgressDialog pDialog;
    SharedPreferences s;
    ImageView passclear, unameclear;
    Authentication a;
    String u, p, toasttext, session;
    LinearLayout about, tfu, tfp;
    String service = "ProntoAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_layout);
        //if(!isMyServiceRunning(MyService.class))
        //startService(new Intent(this,MyService.class));
        initvar();
        /*Checking if this is user's first session,
        if yes, then starting the about dialog activity*/

        session = s.getString("session", "first");
        if (session.equals("first")) {
            about.performClick();
            new NetReceiver().onReceive(this, new Intent());
            SharedPreferences.Editor editor = s.edit();
            editor.putString("session", "used");
            editor.commit();
        }

    }

    //Initialize all the variables here
    private void initvar() {
        about = (LinearLayout) findViewById(R.id.about);
        tfu = (LinearLayout) findViewById(R.id.tfu);
        tfp = (LinearLayout) findViewById(R.id.tfp);
        uname = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);
        s = PreferenceManager.getDefaultSharedPreferences(this);
        uname.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        uname.setText(s.getString("prontousername", null));
        password.setText(s.getString("prontopassword", null));
        a = new Authentication(this);
        login = (Button) findViewById(R.id.login);
        logout = (Button) findViewById(R.id.logout);
        slogin = (Button) findViewById(R.id.savelogin);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        slogin.setOnClickListener(this);
        about.setOnClickListener(this);
        unameclear = (ImageView) findViewById(R.id.unameclear);
        passclear = (ImageView) findViewById(R.id.passclear);
        unameclear.setOnClickListener(this);
        passclear.setOnClickListener(this);
        uname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                unameclear.setVisibility(ImageView.VISIBLE);
                else
                {
                    unameclear.setVisibility(ImageView.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                    passclear.setVisibility(ImageView.VISIBLE);
                else
                {
                    passclear.setVisibility(ImageView.GONE);
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login:
                if (checkConnection()) {
                    details = new ArrayList<NameValuePair>();
                    //Set the url to login
                    url = "http://phc.prontonetworks.com/cgi-bin/authlogin?URI=http://www.msftncsi.com/redirect";
                    u = uname.getText().toString();
                    p = password.getText().toString();
                    //Adding credentials to the list
                    details.add(new BasicNameValuePair("userId", u));
                    details.add(new BasicNameValuePair("password", p));
                    details.add(new BasicNameValuePair("serviceName", service));
                    //Calling Authentication class to perform login
                    new Authentication(this).login(details);
                } else
                    Toast.makeText(this, "Not connected to Volsbb", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                if (checkConnection()) {
                    details = null;
                    //Set the url to logout
                    url = "http://phc.prontonetworks.com/cgi-bin/authlogout";
                    //Calling Authentication class to perform logout
                    new Authentication(this).logout();
                } else
                    Toast.makeText(this, "Not connected to Volsbb", Toast.LENGTH_SHORT).show();

                break;
            case R.id.savelogin:
                if (checkConnection()) {
                    details = new ArrayList<NameValuePair>();
                    //Set the url to login
                    url = "http://phc.prontonetworks.com/cgi-bin/authlogin?URI=http://www.msftncsi.com/redirect";
                    u = uname.getText().toString();
                    p = password.getText().toString();
                    //Saving credentials in the shared preferences
                    SharedPreferences.Editor editor = s.edit();
                    editor.putString("prontousername", u);
                    editor.putString("prontopassword", p);
                    editor.commit();
                    details.add(new BasicNameValuePair("userId", u));
                    details.add(new BasicNameValuePair("password", p));
                    details.add(new BasicNameValuePair("serviceName", service));
                    //Calling Authentication class to perform logout
                    new Authentication(this).login(details);
                } else
                    Toast.makeText(this, "Not connected to Volsbb", Toast.LENGTH_SHORT).show();

            case R.id.about:
                //Starting About Dialog on click about icon
                Intent i = new Intent(this, AboutDialog.class);
                startActivity(i);
                break;
            case R.id.unameclear:
                uname.setText("");
                unameclear.setVisibility(ImageView.GONE);
                break;
            case R.id.passclear:
                password.setText("");
                passclear.setVisibility(ImageView.GONE);
                break;
        }

    }

    /*private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }*/
    /*This function check if thewifi is enabled and connected to Volsbb wifi network
     *If connected- return true
     * Else- return false
     */
    public boolean checkConnection() {
        /*final ConnectivityManager connMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi.isConnected())
        {
            Log.d("wifi",wifi.getExtraInfo());
            if(wifi.getExtraInfo().equalsIgnoreCase("\"VOLSBB\"")) {
                Log.d("wifistate","connected");
                return true;
            }
            return false;
        }
        else {
            return false;
        }*/
        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiManager.isWifiEnabled()) {
            Log.d("enabled", "enabled");
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            final SupplicantState supp = wifiInfo.getSupplicantState();
            Log.d("wifi", wifiInfo.getSSID());
            //Compared with ssid of connected wifi network
            if ((wifiInfo.getSSID().toLowerCase().contains("volsbb") || wifiInfo.getSSID().equalsIgnoreCase("\"VOLS\"")) && wifi.isConnected()) {
                Log.d("wifistate", "connected");
                return true;
            }
            return false;
        } else {
            Log.d("wifistate", "notconnected");
            return false;
        }
    }

    //Changing the outline stroke of text fields on focus change
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.user:
                    tfu.setBackground(getResources().getDrawable(R.drawable.roundedrecfilled));
                    if (!uname.getText().toString().equals(""))
                        unameclear.setVisibility(ImageView.VISIBLE);
                    break;
                case R.id.pass:
                    tfp.setBackground(getResources().getDrawable(R.drawable.roundedrecfilled));
                    if (!password.getText().toString().equals(""))
                        passclear.setVisibility(ImageView.VISIBLE);
                    break;
            }

        } else {
            tfu.setBackground(getResources().getDrawable(R.drawable.roundedrec));
            tfp.setBackground(getResources().getDrawable(R.drawable.roundedrec));
            passclear.setVisibility(ImageView.GONE);
            unameclear.setVisibility(ImageView.GONE);
        }
    }


    /*private class GetEvents extends AsyncTask<Void, Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Processing");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected String doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String valresponse = sh.makeServiceCall(url, ServiceHandler.POST,details);
            Log.d("Response: ", ">" + valresponse);
            return valresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(result.contains("Logout successful"))
                toasttext="Logged out";
            else if(result.contains("Successful Pronto Authentication"))
                toasttext="Logged in";
            else if(result.contains("There is no active session to logout"))
                toasttext="There is no active session";
            else if(result.contains("Sorry, please check your username and password"))
                toasttext="Invalid username/password";
            else if(result.contains("Sorry, your free access quota is over"))
                toasttext="Your free access qouta is over";
            else
                toasttext="Already Logged in";
            Toast.makeText(getApplicationContext(),toasttext,Toast.LENGTH_SHORT).show();
        }



}*/
}
