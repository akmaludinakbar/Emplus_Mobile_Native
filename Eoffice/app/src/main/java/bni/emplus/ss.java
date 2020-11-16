package bni.emplus;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ss extends AppCompatActivity {

    PublicFunction pf = new PublicFunction();
    boolean FlagGooglePlayService;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    long delay = 5000;
    String versi = Config.versi;
    String enkey, enversi;
    String npp, imeii, token;
    String ennpp,enimei,entoken;

    boolean bukaLogin = false;

    UserSessionManager session;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set layout by screen inches
        double screenInches = pf.getScreenInches(this);


        if(screenInches<=4.1){
            setContentView(R.layout.splash_screen);
        }else if(screenInches>4.0&&screenInches<=5.0){
            setContentView(R.layout.splash_screen);
        }else if(screenInches>5.0&&screenInches<=6.0){
            setContentView(R.layout.splash_screen);
        }else if(screenInches>6.0&&screenInches<=7.0){
            setContentView(R.layout.splash_screen);
        }else if(screenInches>7.0&&screenInches<=8.0){
            setContentView(R.layout.splash_screen_tujuh);
        }else if(screenInches>8.0&&screenInches<=9.0){
            setContentView(R.layout.splash_screen_tujuh);
        }else if(screenInches>9.0&&screenInches<=10.0) {
            setContentView(R.layout.splash_screen_tujuh);
        }else if(screenInches>10.0&&screenInches<=11.0){
            setContentView(R.layout.splash_screen_tujuh);
        }

        TextView eoffice = (TextView)findViewById(R.id.tvEoffice);
        TextView mobile = (TextView)findViewById(R.id.tvMobile);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/verdana.ttf");
        eoffice.setTypeface(custom_font);
        mobile.setTypeface(custom_font);

        //change status bar color for lollipop and above
        if(Build.VERSION.SDK_INT >= 21){
            ((FrameLayout) ss.this.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT)). setForeground(null);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(210, 210, 210));
        }

        if(Build.VERSION.SDK_INT >= 22){
            //ask permission for lollipop and above
            askPermission();
        }
        else {
            //checking Google Play Service
            FlagGooglePlayService = pf.isActiveGoogleService(ss.this);
            session = new UserSessionManager(ss.this);

            HashMap<String, String> user = session.getUserNPP();
            HashMap<String, String> userRole = session.getUserRole();

            // get npp,empid,roleid,unitid
            npp = user.get(UserSessionManager.KEY_NAME);

            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //imeii = mngr.getDeviceId();
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(ss.this);

            String imeiSIM1 = telephonyInfo.getImsiSIM1();
            String imeiSIM2 = telephonyInfo.getImsiSIM2();

            imeii = imeiSIM1 + "|" + imeiSIM2;

            if (FlagGooglePlayService == true) {
                if (FirebaseInstanceId.getInstance().getToken() == null) {
                    token = FirebaseInstanceId.getInstance().getToken();
                } else {
                    token = FirebaseInstanceId.getInstance().getToken();
                }
            }

            //run splash
            Timer RunSplash = new Timer();

            TimerTask ShowSplash = new TimerTask() {
                @Override
                public void run() {

                    if (session.checkLogin()) {
                        bukaLogin();
                    } else {
                        cd = new ConnectionDetector(getApplicationContext());
                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
                            mCrypt mCrypt = new mCrypt();
                            try {
                                ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
                                enimei = mCrypt.bytesToHex(mCrypt.encrypt(imeii));
                                if(token != null) {
                                    entoken = mCrypt.bytesToHex(mCrypt.encrypt(token));
                                }
                                else entoken = "";
                                enversi = mCrypt.bytesToHex(mCrypt.encrypt(versi));
                                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cekVersi cek = new cekVersi();
                            cek.execute(ennpp, enimei, entoken, enversi, enkey);
                        } else {
                            showAlertConnection();
                        }
                    }
                }
            };

            RunSplash.schedule(ShowSplash, delay);
        }
    }

    private void askPermission(){
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("read external");
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("write external");
        if (!addPermission(permissionsList, android.Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("read phone");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            return;
        }

        //checking Google Play Service
        FlagGooglePlayService = pf.isActiveGoogleService(ss.this);
        session = new UserSessionManager(ss.this);

        HashMap<String, String> user = session.getUserNPP();
        HashMap<String, String> userRole = session.getUserRole();

        // get npp,empid,roleid,unitid
        npp = user.get(UserSessionManager.KEY_NAME);
        Boolean login = session.checkLogin();

        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //imeii = mngr.getDeviceId();
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(ss.this);

        String imeiSIM1 = telephonyInfo.getImsiSIM1();
        String imeiSIM2 = telephonyInfo.getImsiSIM2();

        imeii = imeiSIM1 + "|" + imeiSIM2;

        if (FlagGooglePlayService == true) {
            if (FirebaseInstanceId.getInstance().getToken() == null) {
                token = FirebaseInstanceId.getInstance().getToken();
            } else {
                token = FirebaseInstanceId.getInstance().getToken();
            }
        }

        //run splash
        Timer RunSplash = new Timer();

        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {

                if(session.checkLogin())
                {
                    bukaLogin();
                }
                else {
                    cd = new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {
                        mCrypt mCrypt = new mCrypt();
                        try {
                            ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
                            enimei = mCrypt.bytesToHex(mCrypt.encrypt(imeii));
                            if(token != null) {
                                entoken = mCrypt.bytesToHex(mCrypt.encrypt(token));
                            }
                            else entoken = "";
                            enversi = mCrypt.bytesToHex(mCrypt.encrypt(versi));
                            enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        cekVersi cek = new cekVersi();
                        cek.execute(ennpp, enimei, entoken, enversi, enkey);
                    } else {
                        showAlertConnection();
                    }
                }
            }

        };

        RunSplash.schedule(ShowSplash, delay);

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ss.this).setMessage(message).setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&
                        perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    //insertDummyContact();
                    //checking Google Play Service
                    FlagGooglePlayService = pf.isActiveGoogleService(ss.this);
                    session = new UserSessionManager(ss.this);

                    HashMap<String, String> user = session.getUserNPP();
                    HashMap<String, String> userRole = session.getUserRole();

                    // get npp,empid,roleid,unitid
                    npp = user.get(UserSessionManager.KEY_NAME);

                    TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    //imeii = mngr.getDeviceId();

                    TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(ss.this);

                    String imeiSIM1 = telephonyInfo.getImsiSIM1();
                    String imeiSIM2 = telephonyInfo.getImsiSIM2();

                    imeii = imeiSIM1 + "|" + imeiSIM2;

                    if (FlagGooglePlayService == true) {
                        if (FirebaseInstanceId.getInstance().getToken() == null) {
                            token = FirebaseInstanceId.getInstance().getToken();
                        } else {
                            token = FirebaseInstanceId.getInstance().getToken();
                        }
                    }

                    //run splash
                    Timer RunSplash = new Timer();

                    TimerTask ShowSplash = new TimerTask() {
                        @Override
                        public void run() {

                            if(session.checkLogin())
                            {
                                bukaLogin();
                            }
                            else {
                                cd = new ConnectionDetector(getApplicationContext());
                                isInternetPresent = cd.isConnectingToInternet();

                                if (isInternetPresent) {
                                    mCrypt mCrypt = new mCrypt();
                                    try {
                                        ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
                                        enimei = mCrypt.bytesToHex(mCrypt.encrypt(imeii));
                                        if(token != null) {
                                            entoken = mCrypt.bytesToHex(mCrypt.encrypt(token));
                                        }
                                        else entoken = "";
                                        enversi = mCrypt.bytesToHex(mCrypt.encrypt(versi));
                                        enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    cekVersi cek = new cekVersi();
                                    cek.execute(ennpp, enimei, entoken, enversi, enkey);
                                } else {
                                    showAlertConnection();
                                }
                            }
                        }

                    };

                    RunSplash.schedule(ShowSplash, delay);
                } else {
                    // Permission Denied
                    Toast.makeText(ss.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void bukaLogin()
    {
        Intent i = new Intent(ss.this, log.class);
        i.putExtra("googlePlay",FlagGooglePlayService);
        startActivity(i);
        finish();
    }
    public void bukaHome(String statusInternet){
        //session
        session = new UserSessionManager(ss.this);
        HashMap<String, String> userRole = session.getUserRole();
        HashMap<String, String> user = session.getUserNPP();

        // get roleid
        String roleid = userRole.get(UserSessionManager.KEY_ROLE);
        String empid = user.get(UserSessionManager.KEY_EMPID);

        Intent i = new Intent(ss.this, rl.class);
        i.putExtra("empid", empid);
        i.putExtra("fromActivity","splash");
        startActivity(i);
        finish();

    }

    public void showAlertConnection(){
        ss.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ss.this, Config.alertConnection, Toast.LENGTH_SHORT).show();
            }
        });
        bukaHome("nointernet");
    }

    public void showAlert(final String pesan){
        ss.this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(ss.this);
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if(bukaLogin)
                            bukaLogin();
                        else
                            bukaHome("internet");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    class cekVersi extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String npp = params[0];
            String imeii = params[1];
            String token = params[2];
            String version = params[3];
            String key = params[4];
            String responce;


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, npp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_IMEII, imeii));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TOKEN, token));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VERSI, version));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {

                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost httpPost = new HttpPost(Config.urlLanding + "UpdateTokenUser.php");
                HttpPost httpPost = new HttpPost(Config.urlLanding + "SplashCheck.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //TimeOut
                HttpParams httpParameters = httpPost.getParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 30000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 30000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                HttpResponse response = httpClient.execute(httpPost);


                HttpEntity entity = response.getEntity();

                responce = EntityUtils.toString(entity);
                return new JSONObject(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                bukaHome("no internet");
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                bukaHome("no internet");
            } catch (IOException e) {
                e.printStackTrace();
                bukaHome("no internet");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");
                    String kodeSession = result.getString("KodeSession");
                    String keteranganSession = result.getString("KeteranganSession");

                    if(kodeSession.equals("2"))
                    {
                        bukaLogin = false;
                    }
                    else
                    {
                        bukaLogin = true;
                    }

                    if(kode.equals("2")) {
                        showAlert(keterangan);
                    }
                    else if(kode.equals("1"))
                    {
                        if(bukaLogin) {
                            bukaLogin();
                        }
                        else
                            bukaHome("internet");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
