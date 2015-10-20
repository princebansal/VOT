package com.gdgvitvellore.volsbbonetouch;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.gdgvitvellore.volsbbonetouch.volley.AppController;

import org.jsoup.Jsoup;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shalini on 04-01-2015.
 */
public class SettingsActivity extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TabActivity.OnActivityPageChangeListener {

    private Switch autoswitch;
    private static SharedPreferences s;
    private Button refreshButton,passButton;
    private TabActivity tabActivity;
    private TextView renewText;
    private String JSessionId="";
    private boolean flag=false;
    private ProgressBar progressBar;
    private String dataLoginUrl="http://115.248.50.60/registration/chooseAuth.do";
    private String dataHistory="http://115.248.50.60/registration/customerSessionHistory.do";
    private String dataUrl="http://115.248.50.60/registration/main.do?content_key=%2FCustomerSessionHistory.jsp";
    private TextView data;
    private ImageView datePickerImage;
    Map<String, String>  map1 = new HashMap<String, String>();
    public SettingsActivity()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.settings_fragment, container, false);
        autoswitch=(Switch)rootView.findViewById(R.id.autologin);
        autoswitch.setOnCheckedChangeListener(this);
        s = PreferenceManager.getDefaultSharedPreferences(getActivity());
        refreshButton=(Button)rootView.findViewById(R.id.refreshbut);
        passButton=(Button)rootView.findViewById(R.id.cpassbut);
        if(s.getString("autoLogin","false").equals("true")){
            autoswitch.setChecked(true);
        }
        data=(TextView)rootView.findViewById(R.id.datatext);
        renewText=(TextView)rootView.findViewById(R.id.renew_text);
        renewText.setOnClickListener(this);
        datePickerImage=(ImageView)rootView.findViewById(R.id.calendar);
        datePickerImage.setOnClickListener(this);
        tabActivity=(TabActivity)getActivity();
        tabActivity.setOnActivityPageChangeListener(this,"settings");
        data.setText(s.getString("data","0.0 GB"));
        progressBar=(ProgressBar)rootView.findViewById(R.id.progress_bar);
        refreshButton.setOnClickListener(this);
        passButton.setOnClickListener(this);
       // refreshButton.setOnTouchListener(this);
        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
        {

            SharedPreferences.Editor editor = s.edit();
            editor.putString("autoLogin", "true");
            editor.commit();
        }
        else
        {

            SharedPreferences.Editor editor = s.edit();
            editor.putString("autoLogin", "false");
            editor.commit();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.refreshbut){
          getData();
        }
        if(v.getId()==R.id.cpassbut){
            ChangePassword changePassword=new ChangePassword();
            changePassword.getSessionId();

        }
        if(v.getId()==R.id.calendar){
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
        if(v.getId()==R.id.renew_text){
            IntentDialog dialog=new IntentDialog(getActivity());
            dialog.setTitle("Select Method");
            dialog.show();
        }
    }
    private void loginData() {

        final HashMap<String,String> datamap=new HashMap<String,String>();
        datamap.put("loginUserId",s.getString("prontousername","a"));
        datamap.put("loginPassword",s.getString("prontopassword","a"));
        datamap.put("authType","Pronto");
        datamap.put("submit","Login");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                dataLoginUrl,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String result) {
                        Log.d("logindata", result.toString());
                       getData();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("perror", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Unknown network", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return datamap;
            }


        };
        AppController.getInstance().addToRequestQueue(strReq, "loginrequest");
    }
    public void getData(){
        final Calendar c = Calendar.getInstance();
        int year = s.getInt("rYear", c.get(Calendar.YEAR));
        int month = s.getInt("rMonth", c.get(Calendar.MONTH));
        int day = s.getInt("rDay",1);
        final HashMap<String,String> datamap=new HashMap<String,String>();
        datamap.put("location", "allLocations");
        datamap.put("parameter","custom");
        datamap.put("customStartMonth", String.valueOf(month));
        datamap.put("customStartYear", String.valueOf(year));
        datamap.put("customStartDay", String.valueOf(day));
        datamap.put("customEndMonth", String.valueOf(c.get(Calendar.MONTH)));
        datamap.put("customEndYear", String.valueOf(c.get(Calendar.YEAR)));
        datamap.put("customEndDay", String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
        datamap.put("button","View");
        dataUrl=s.getString("dataUrl","null");
        progressBar.setVisibility(ProgressBar.VISIBLE);
        data.setVisibility(TextView.INVISIBLE);
        final StringRequest strReq1 = new StringRequest(Request.Method.POST,
                dataHistory,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String result) {
                        Log.d("sessiondata", result.toString());
                        flag=false;
                        analyze(result);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        data.setVisibility(TextView.VISIBLE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("perror1", "Error: " + error.getMessage());
                Log.d("error",error.toString());
                Toast.makeText(getActivity(),"Please try again",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                data.setVisibility(TextView.VISIBLE);
                // hide the progress dialog
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return datamap;

            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                
                Map<String,String> map=new HashMap<String,String>();
                map.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                map.put("Accept-Encoding","gzip, deflate");
                map.put("Accept-Language","en-US,en;q=0.8,hi;q=0.6");
                map.put("Cache-Control","max-age=0");
                map.put("Connection","keep-alive");
                map.put("Content-Type","application/x-www-form-urlencoded");
                Log.d("setcookie",map1.get("Set-Cookie").toString().split(";")[0]);
                map.put("Cookie",map1.get("Set-Cookie").toString().split(";")[0]);
                map.put("DNT", String.valueOf(1));
                map.put("Host","115.248.50.60");
                map.put("Origin","http://115.248.50.60");
                map.put("Referer",dataUrl);
                map.put("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
                return map;
            }

        };
        StringRequest strReq = new StringRequest(Request.Method.POST,
                dataUrl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        Log.d("logindatadada", result.toString());
                        AppController.getInstance().addToRequestQueue(strReq1, "datacustomrequest");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("perror", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Unknown Network", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                data.setVisibility(TextView.VISIBLE);
                // hide the progress dialog
                // hide the progress dialog
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                Log.d("logindatnetw", response.headers.toString());
                map1 = response.headers;

                return super.parseNetworkResponse(response);
            }
        };


        AppController.getInstance().addToRequestQueue(strReq, "datarequest");
    }
    public void analyze(String result){
        /*result=result.substring(result.indexOf("Grand Total"));
        result=result.substring(result.indexOf("<b>"));
        result=result.substring(3);
        result=result.substring(result.indexOf("<b>"));
        result=result.substring(0,15);
        result=result.substring(result.indexOf("<b>")+3,result.indexOf("</b>")-1);*/

        try{
            String dataresult=Jsoup.parse(Jsoup.parse(result).getElementsByClass("subTextRight").last().html().trim()).getElementsByTag("b").first().html();
            data.setText(dataresult);
            SharedPreferences.Editor editor = s.edit();
            editor.putString("data", dataresult);
            editor.commit();

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(),"Error!Please try Logging In",Toast.LENGTH_SHORT).show();
            data.setText(s.getString("data","0.0 GB"));
        }

    }

    public void resetForeground(){

        //refreshButton.setBackground(getResources().getDrawable(R.drawable.refreshbtnbg));
    }

   /* @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.refreshbut) {
            if (v.getId() == R.id.refreshbut) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    refreshButton.setBackground(getResources().getDrawable(R.drawable.refreshbtnbgpressed));
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if ((event.getY() <0) || (event.getY() > v.getHeight()) || (event.getX() < 0) || (event.getX() > v.getWidth()))
                    refreshButton.setBackground(getResources().getDrawable(R.drawable.refreshbtnbg));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    refreshButton.setBackground(getResources().getDrawable(R.drawable.refreshbtnbg));
                }
            }

        }
        return false;
    }
*/
    @Override
    public void OnPageChange(int i) {
        resetForeground();
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener,DatePickerDialog.OnCancelListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = s.getInt("rYear", c.get(Calendar.YEAR));
            int month = s.getInt("rMonth", c.get(Calendar.MONTH));
            int day = s.getInt("rDay",1);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog= new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            DatePicker picker=dialog.getDatePicker();
            picker.setMaxDate(new Date().getTime());
            Calendar c1 = Calendar.getInstance();
            c1.set(2013,00,01);
            picker.setMinDate(c1.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if(view.isShown()){
            //Toast.makeText(getActivity(),"Y:"+year+"m:"+month+"d:"+day,Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = s.edit();
            editor.putInt("rYear", year);
            editor.putInt("rMonth", month);
            editor.putInt("rDay", day);
            editor.commit();
            Log.d("date","set");}
            // Do something with the date chosen by the user
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            Log.d("date","cancel");
        }
    }

    public class ChangePassword{
        private String loginUrl="http://115.248.50.60/registration/Main.jsp?wispId=1&nasId=28:92:4a:3a:bd:00";
        private String passChangeUrl="http://115.248.50.60/registration/changePassword.do";
        private String sessionId="";
        private Map<String,String> headerMap;
        private ChangePassword(){

        }
        public void getSessionId(){
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    loginUrl,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String result) {
                            Log.d("cplogindta", result.toString());
                            login();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("perror", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(), "Unknown Network", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(ProgressBar.INVISIBLE);
                    //data.setVisibility(TextView.VISIBLE);
                    // hide the progress dialog
                    // hide the progress dialog
                }
            }) {
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {

                    Log.d("cplogindatnetw", response.headers.toString());
                    headerMap = response.headers;

                    return super.parseNetworkResponse(response);
                }
            };


            AppController.getInstance().addToRequestQueue(strReq, "cpdatarequest");
        }
        public void login(){
            final HashMap<String,String> datamap=new HashMap<String,String>();
            datamap.put("loginUserId",s.getString("prontousername","a"));
            datamap.put("loginPassword",s.getString("prontopassword","a"));
            datamap.put("authType","Pronto");
            datamap.put("submit","Login");
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    dataLoginUrl,
                    new Response.Listener<String>() {


                        @Override
                        public void onResponse(String result) {
                            Log.d("cploggedindata", result.toString());
                            change();
                            //getData();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("perror", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(), "Unknown network", Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    return datamap;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String,String> map=new HashMap<String,String>();
                    map.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    map.put("Accept-Encoding","gzip, deflate");
                    map.put("Accept-Language","en-US,en;q=0.8,hi;q=0.6");
                    map.put("Cache-Control","max-age=0");
                    map.put("Connection","keep-alive");
                    map.put("Content-Type","application/x-www-form-urlencoded");
                    Log.d("setcookie",headerMap.get("Set-Cookie").toString().split(";")[0]);
                    map.put("Cookie",headerMap.get("Set-Cookie").toString().split(";")[0]);
                    map.put("DNT", String.valueOf(1));
                    map.put("Host","115.248.50.60");
                    map.put("Origin","http://115.248.50.60");
                    map.put("Referer",loginUrl);
                    map.put("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
                    return map;
                }
            };
            AppController.getInstance().addToRequestQueue(strReq, "loginrequest");
        }
        public void change(){
            final HashMap<String,String> changedatamap=new HashMap<String,String>();
            changedatamap.put("changeUserId",s.getString("prontousername","a"));
            changedatamap.put("changePassword",s.getString("prontopassword","a"));
            changedatamap.put("changeNewPassword","eovc1041");
            changedatamap.put("changeConfirmNewPassword","eovc1041");
            changedatamap.put("submit","Update");
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    passChangeUrl,
                    new Response.Listener<String>() {


                        @Override
                        public void onResponse(String result) {
                            Log.d("passdata", result.toString());
                            //getData();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("perror", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(), "Unknown network", Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    return changedatamap;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String,String> map=new HashMap<String,String>();
                    map.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    map.put("Accept-Encoding","gzip, deflate");
                    map.put("Accept-Language","en-US,en;q=0.8,hi;q=0.6");
                    map.put("Cache-Control","max-age=0");
                    map.put("Connection","keep-alive");
                    map.put("Content-Type","application/x-www-form-urlencoded");
                    Log.d("setcookie",headerMap.get("Set-Cookie").toString().split(";")[0]);
                    map.put("Cookie",headerMap.get("Set-Cookie").toString().split(";")[0]);
                    map.put("DNT", String.valueOf(1));
                    map.put("Host","115.248.50.60");
                    map.put("Origin","http://115.248.50.60/registration/main.do?content_key=%2FChangePassword.jsp");
                    map.put("Referer",loginUrl);
                    map.put("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
                    return map;
                }
            };
            AppController.getInstance().addToRequestQueue(strReq, "changepassrequest");
        }

    }
}


