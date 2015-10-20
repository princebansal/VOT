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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.gdgvitvellore.volsbbonetouch.Database.Account;
import com.gdgvitvellore.volsbbonetouch.Database.DatabaseHandler;
import com.gdgvitvellore.volsbbonetouch.Database.RecyclerAdapter;
import com.gdgvitvellore.volsbbonetouch.volley.AppController;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Fragment implements View.OnClickListener, View.OnFocusChangeListener, TabActivity.OnActivityPageChangeListener {
    private String url = "http://phc.prontonetworks.com/cgi-bin/authlogin";
    private RecyclerView accountsRecycler;
    private RecyclerAdapter adapter;
    EditText uname, password;
    TextView res,data;
    Button login, logout, slogin;
    HashMap<String,String> details;
    ProgressDialog pDialog;
    SharedPreferences s;
    ImageView logo;
    DatabaseHandler databaseHandler;
    Switch autoswitch;
    ImageView passclear, unameclear,about,uImage;
    Authentication a;
    TabActivity tabActivity;
    String u, p, toasttext, session;
    LinearLayout tfu, tfp,recContainer;
    String service = "ProntoAuthentication",dataString="";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.new_layout, container, false);
        databaseHandler=new DatabaseHandler(getActivity());
        setAll(rootView);
        return rootView;
    }

    private boolean checkAccounts() {
        int n=databaseHandler.getContactsCount();
        if(n>1){
            Log.d("db","acc>1");
            return true;
        }
        else {
            Log.d("db","acc<1");
            return false;
        }
    }

    private void setAll(ViewGroup rootView) {

        initvar(rootView);
        /*Checking if this is user's first session,
        if yes, then starting the about dialog activity*/

        session = s.getString("session", "first");
        if (session.equals("first")) {
            about.performClick();
            new NetReceiver().onReceive(getActivity(), new Intent());
            SharedPreferences.Editor editor = s.edit();
            editor.putString("session", "used");
            editor.commit();
        }
    }


    //Initialize all the variables here
    private void initvar(ViewGroup v) {
        tabActivity=(TabActivity)getActivity();
        tabActivity.setOnActivityPageChangeListener(this,"main");
        about = (ImageView) v.findViewById(R.id.about);
        accountsRecycler=(RecyclerView)v.findViewById(R.id.accounts_recycler_view);
        accountsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recContainer=(LinearLayout)v.findViewById(R.id.recycler_container);
        tfu = (LinearLayout) v.findViewById(R.id.tfu);
        tfp = (LinearLayout) v.findViewById(R.id.tfp);
        uname = (EditText) v.findViewById(R.id.user);
        uImage=(ImageView)v.findViewById(R.id.uname_image);
        uImage.setOnClickListener(this);
        password = (EditText) v.findViewById(R.id.pass);
        s = PreferenceManager.getDefaultSharedPreferences(getActivity());
        uname.setOnFocusChangeListener(this);
        logo=(ImageView)v.findViewById(R.id.imageView);
        password.setOnFocusChangeListener(this);
        uname.setText(s.getString("prontousername", null));
        password.setText(s.getString("prontopassword", null));
        a = new Authentication(getActivity());
        login = (Button) v.findViewById(R.id.login);
        logout = (Button) v.findViewById(R.id.logout);
        slogin = (Button) v.findViewById(R.id.savelogin);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        slogin.setOnClickListener(this);
        //login.setOnTouchListener(this);
        //logout.setOnTouchListener(this);
        //slogin.setOnTouchListener(this);
        if(checkAccounts()){
            uImage.setRotationX(90);
        }
        about.setOnClickListener(this);
        unameclear = (ImageView) v.findViewById(R.id.unameclear);
        passclear = (ImageView) v.findViewById(R.id.passclear);
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
                logo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.size));
                if (checkConnection()) {
                    details = new HashMap<String,String>();
                    //Set the url to login
                    url = "http://phc.prontonetworks.com/cgi-bin/authlogin";
                    u = uname.getText().toString();
                    p = password.getText().toString();
                    //Adding credentials to the list
                    details.put("userId", u);
                    details.put("password", p);
                    details.put("serviceName", service);
                    //Calling Authentication class to perform login
                    new Authentication(getActivity()).login(details);
                } else
                    Toast.makeText(getActivity(), "Not connected to Volsbb", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                logo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.size));
                if (checkConnection()) {
                    details = null;
                    //Set the url to logout
                    url = "http://phc.prontonetworks.com/cgi-bin/authlogout";
                    //Calling Authentication class to perform logout
                    new Authentication(getActivity()).logout();
                } else
                    Toast.makeText(getActivity(), "Not connected to Volsbb", Toast.LENGTH_SHORT).show();

                break;
            case R.id.savelogin:
                logo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.size));

                if (checkConnection()) {
                    details = new HashMap<String,String>();
                    //Set the url to login
                    url = "http://phc.prontonetworks.com/cgi-bin/authlogin";
                    u = uname.getText().toString();
                    p = password.getText().toString();
                    //Saving credentials in the shared preferences
                    if(!databaseHandler.checkContact(u)){
                        Account a=new Account();
                        a.setName(u);
                        a.setPassword(p);
                        databaseHandler.addAccount(a);
                    }
                    SharedPreferences.Editor editor = s.edit();
                    editor.putString("prontousername", u);
                    editor.putString("prontopassword", p);
                    editor.commit();
                    details.put("userId", u);
                    details.put("password", p);
                    details.put("serviceName", service);
                    //Calling Authentication class to perform logout
                    new Authentication(getActivity()).login(details);
                } else
                    Toast.makeText(getActivity(), "Not connected to Volsbb", Toast.LENGTH_SHORT).show();
                break;

            case R.id.about:
                //Starting About Dialog on click about icon
                Intent i = new Intent(getActivity(), AboutDialog.class);
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
            case R.id.uname_image:
                if(checkAccounts()) {
                    uImage.setRotationX(180);
                    adapter=new RecyclerAdapter(getActivity(),databaseHandler.getAllContacts(),this);
                    recContainer.setVisibility(LinearLayout.VISIBLE);
                    accountsRecycler.setAdapter(adapter);
                }
                break;
        }

    }
    public void resetForeground(){
       // login.setBackground(getResources().getDrawable(R.drawable.restbtnbg));
        //logout.setBackground(getResources().getDrawable(R.drawable.logoutbtnbg));
        //slogin.setBackground(getResources().getDrawable(R.drawable.restbtnbg));
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
        final WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        final ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiManager.isWifiEnabled()) {
            Log.d("enabled", "enabled");
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            final SupplicantState supp = wifiInfo.getSupplicantState();
            Log.d("wifi", wifiInfo.getSSID()+"lll");
            Log.d("wificc", String.valueOf(wifi.isConnected())+"lll");
            //Compared with ssid of connected wifi network
            if((wifiInfo.getSSID().toLowerCase().contains("volsbb")||wifiInfo.getSSID().toLowerCase().contains("\"VOLS\"")||wifiInfo.getSSID().toLowerCase().contains("\"VIT2.4G\"")||wifiInfo.getSSID().toLowerCase().contains("\"VIT5G\"")||wifiInfo.getSSID().toLowerCase().contains("vit2.4g")||wifiInfo.getSSID().toLowerCase().contains("vit5g"))&&wifi.isConnected()) {
                Log.d("wifistate","connected");
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
                    if (!uname.getText().toString().equals("")){
                        unameclear.setVisibility(ImageView.VISIBLE);
                    passclear.setVisibility(ImageView.INVISIBLE);}
                    break;
                case R.id.pass:
                    tfp.setBackground(getResources().getDrawable(R.drawable.roundedrecfilled));
                    if (!password.getText().toString().equals("")){
                        passclear.setVisibility(ImageView.VISIBLE);
                    unameclear.setVisibility(ImageView.INVISIBLE);}
                    break;
            }

        } else {
            tfu.setBackground(getResources().getDrawable(R.drawable.roundedrec));
            tfp.setBackground(getResources().getDrawable(R.drawable.roundedrec));
            passclear.setVisibility(ImageView.GONE);
            unameclear.setVisibility(ImageView.GONE);
        }
    }

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.login:
                    login.setBackground(getResources().getDrawable(R.drawable.restbtnbgpressed));
                    break;
                case R.id.logout:
                    logout.setBackground(getResources().getDrawable(R.drawable.logoutbtnbgpressed));
                    break;
                case R.id.savelogin:
                    slogin.setBackground(getResources().getDrawable(R.drawable.restbtnbgpressed));
                    break;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if ((event.getY() <0) || (event.getY() > v.getHeight()) || (event.getX() < 0) || (event.getX() > v.getWidth())) {
                switch (v.getId()) {
                    case R.id.login:
                        login.setBackground(getResources().getDrawable(R.drawable.restbtnbg));
                        break;
                    case R.id.logout:
                        logout.setBackground(getResources().getDrawable(R.drawable.logoutbtnbg));
                        break;
                    case R.id.savelogin:
                        slogin.setBackground(getResources().getDrawable(R.drawable.restbtnbg));
                        break;
                }
            }
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            switch (v.getId()) {
                case R.id.login:
                    login.setBackground(getResources().getDrawable(R.drawable.restbtnbg));
                    break;
                case R.id.logout:
                    logout.setBackground(getResources().getDrawable(R.drawable.logoutbtnbg));
                    break;
                case R.id.savelogin:
                    slogin.setBackground(getResources().getDrawable(R.drawable.restbtnbg));
                    break;
            }
        }
        return false;
    }*/

    @Override
    public void OnPageChange(int i) {
        resetForeground();
    }

    public void applyItem(Account account) {
        recContainer.setVisibility(LinearLayout.GONE);
        uname.setText(account.getName());
        password.setText(account.getPassword());
    }

    public void deleteAccount(Account account) {
        databaseHandler.deleteContact(account);
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
