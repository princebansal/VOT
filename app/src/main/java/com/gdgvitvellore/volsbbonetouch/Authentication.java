package com.gdgvitvellore.volsbbonetouch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.gdgvitvellore.volsbbonetouch.volley.AppController;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shalini on 31-01-2015.
 */
public class Authentication {
    private String url="http://phc.prontonetworks.com/cgi-bin/authlogin";
    HashMap<String,String> details;
    String service="ProntoAuthentication";
    private Context context;
    public static String dataSession="";
    private String toasttext="";
    private SharedPreferences s;
    private String u,p;
    private String[] contentText={"Login/Logout",""};
    private ShowNotification sn;
    public Authentication(){

    }
    public Authentication(Context c)
    {
        context=c;
        sn= new ShowNotification(context);
        s= PreferenceManager.getDefaultSharedPreferences(context);
        u=s.getString("prontousername","a");
        p=s.getString("prontopassword","a");
        details= new HashMap<String,String>();
        details=null;
    }
    public void login(){
        url="http://phc.prontonetworks.com/cgi-bin/authlogin";
        contentText[0]="Logging In";
        contentText[1]="Logged In";
        details= new HashMap<String,String>();
        details.put("userId", u);
        details.put("password", p);
        details.put("serviceName", service);
        execute();
    }
    public void login(HashMap<String, String> det){
        url="http://phc.prontonetworks.com/cgi-bin/authlogin";
        contentText[0]="Logging In";
        contentText[1]="Logged In";
        details= new HashMap<String,String>();
        details=det;
        execute();
    }
    public void logout(){
        details=null;
        contentText[0]="Logging Out";
        contentText[1]="Logged Out";
        url="http://phc.prontonetworks.com/cgi-bin/authlogout";
        execute();
    }
    public void execute() {
        sn.setText(contentText[0]).notifyInstant();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String result) {
                        Log.d("presponse", result.toString());
                        if(result==null)
                        {
                            Toast.makeText(context,"Unknown network",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (result.contains("Logout successful")) {
                                toasttext = "Logged out";
                                Log.d("enter", "0");
                                sn.setText(contentText[1]).notifyInstant();
                            } else if (result.contains("Successful Pronto Authentication")) {
                                toasttext = "Logged in";
                                dataSession= Jsoup.parse(result).getElementsByClass("orangeText10").get(1).attr("href");
                                SharedPreferences.Editor editor = s.edit();
                                editor.putString("dataUrl", dataSession);
                                editor.commit();
                                Log.d("enter", "1");
                                sn.setText(contentText[1]).notifyInstant();
                            } else if (result.contains("There is no active session to logout")) {
                                toasttext = "There is no active session";
                                Log.d("enter", "2");
                                sn.setText("Login/Logout").notifyInstant();
                            } else if (result.contains("Sorry, please check your username and password")||result.contains("Sorry, that password was not accepted")||result.contains("Sorry, that account does not exist")) {
                                toasttext = "Invalid username/password";
                                Log.d("enter", "3");
                                sn.setText("Login/Logout").notifyInstant();
                            } else if (result.contains("Sorry, your free access quota is over")) {
                                toasttext = "Your free access quota is over";
                                Log.d("enter", "4");
                                sn.setText("Login/Logout").notifyInstant();
                            }
                            else if (result.contains("ACTIVE SESSION ERROR")) {
                                toasttext = "Sorry! User Already has an active session";
                                Log.d("enter", "5");
                                sn.setText("Login/Logout").notifyInstant();
                            } else {
                                toasttext = "Already Logged in";
                                Log.d("enter", "6");
                                sn.setText(contentText[1]).notifyInstant();
                            }
                            Toast.makeText(context, toasttext, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("perror", "Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(context,"Unknown network",Toast.LENGTH_SHORT).show();
                sn.setText("Login/Logout").notifyInstant();
                // hide the progress dialog
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return details;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d("parsenet",response.data.toString());

                return super.parseNetworkResponse(response);
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, "request");

    }

    /*private class GetEvents extends AsyncTask<Void, Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //sn.setText(contentText[0]).notifyInstant();
            // Showing progress dialog
           /* pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Signing In");
            pDialog.setCancelable(false);
            pDialog.show();*/

       //}
        /*@Override
        protected String doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            sn.setText(contentText[0]).notifyInstant();
            // Making a request to url and getting response
            String valresponse = sh.makeServiceCall(url, ServiceHandler.POST,details);
            Log.d("Response: ", ">" + valresponse);
            return valresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null)
            {
                Toast.makeText(context,"Unknown network",Toast.LENGTH_SHORT).show();
            }
            else {
                if (result.contains("Logout successful")) {
                    toasttext = "Logged out";
                    Log.d("enter", "0");
                    sn.setText(contentText[1]).notifyInstant();
                } else if (result.contains("Successful Pronto Authentication")) {
                    toasttext = "Logged in";
                    Log.d("enter", "1");
                    sn.setText(contentText[1]).notifyInstant();
                } else if (result.contains("There is no active session to logout")) {
                    toasttext = "There is no active session";
                    Log.d("enter", "2");
                    sn.setText("Login/Logout").notifyInstant();
                } else if (result.contains("Sorry, please check your username and password")||result.contains("Sorry, that password was not accepted")||result.contains("Sorry, that account does not exist")) {
                    toasttext = "Invalid username/password";
                    Log.d("enter", "3");
                    sn.setText("Login/Logout").notifyInstant();
                } else if (result.contains("Sorry, your free access quota is over")) {
                    toasttext = "Your free access quota is over";
                    Log.d("enter", "4");
                    sn.setText("Login/Logout").notifyInstant();
                } else {
                    toasttext = "Already Logged in";
                    Log.d("enter", "5");
                    sn.setText(contentText[1]).notifyInstant();
                }
                Toast.makeText(context, toasttext, Toast.LENGTH_SHORT).show();
            }
            // Dismiss the progress dialog
            /*if (pDialog.isShowing())
                pDialog.dismiss();
            res.setText(result);*/
     //   }



    //}
}

