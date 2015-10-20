package com.gdgvitvellore.volsbbonetouch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * The instance of this created everytime there is a change in wifi status of the device
 */
public class NetReceiver extends BroadcastReceiver {
    private Context c;
    private ShowNotification s;
    private SharedPreferences shared;
    private String url="http://www.google.com";


    @Override
    public void onReceive(Context context, Intent intent) {
        c=context;
        shared = PreferenceManager.getDefaultSharedPreferences(c);
        s=new ShowNotification(c);
        if(checkConnection())
        {
            //Pop the notification if volsbb is connected
            new Checknet().execute();
            s.notifyInstant();



        }
        else
        {
            //Removes the notification as soon as Volsbb is disconnected
            s.remove();
        }
    }
    /*This function check if the wifi is enabled and connected to Volsbb wifi network
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
        final WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        final ConnectivityManager connMgr = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiManager.isWifiEnabled())
        {
            Log.d("enabled", "enabled");
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            final SupplicantState supp= wifiInfo.getSupplicantState();
            Log.d("wifi",wifiInfo.getSSID());
            Log.d("wificc", String.valueOf(wifi.isAvailable()
            ));
            if((wifiInfo.getSSID().toLowerCase().contains("volsbb")||wifiInfo.getSSID().toLowerCase().contains("\"VOLS\"")||wifiInfo.getSSID().toLowerCase().contains("vit2.4g")||wifiInfo.getSSID().toLowerCase().contains("vit5g"))&&wifi.isConnected()) {
                Log.d("wifistate","connected");
                return true;
            }
            return false;
        }
        else {
            Log.d("wifistate","notconnected");
            return false;
        }
    }
    private class Checknet extends AsyncTask<Void, Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpGet httpGet = new HttpGet(url);
            HttpParams httparams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httparams,10000);
            HttpConnectionParams.setSoTimeout(httparams,5000);
            DefaultHttpClient httpclient=new DefaultHttpClient(httparams);
            try {
                HttpResponse hr=httpclient.execute(httpGet);
                HttpEntity he =hr.getEntity();
                String res=EntityUtils.toString(he).toLowerCase();
                Log.d("conn0", res);
                if(!res.contains("pronto"))
                return "success";
            }
            catch(ClientProtocolException c){

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "fail";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if(result.equals("success"))
                s.setText("Logged In").notifyInstant();
            else{
                String session = shared.getString("autoLogin", "false");
                if (session.equals("true")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Authentication a = new Authentication(c);
                            a.login();
                        }
                    }).start();
                }
            }
        }
    }
}
