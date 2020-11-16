package bni.emplus;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class log extends AppCompatActivity {
    PublicFunction pf = new PublicFunction();
    boolean FlagGooglePlayService;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    UserSessionManager session;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public AppCompatEditText editNpp, editPassword;
    public Button btLogin;
    public String npp, pass,token="", imei, empid, imeiSIM1, imeiSIM2;
    String ennpp,enpass,enKey,enToken,enImei,enEmail,enOTP,enImei1,enImei2;

    Dialog ResetDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //set layout by screen inches
        /*double screenInches = pf.getScreenInches(this);

        if(screenInches<=4.1){
            setContentView(R.layout.activity_login_empat);
        }else if(screenInches>4.0&&screenInches<=5.0){
            setContentView(R.layout.activity_login_lima);
        }else if(screenInches>5.0&&screenInches<=6.0){
            setContentView(R.layout.activity_login_enam);
        }else if(screenInches>6.0&&screenInches<=7.0){
            setContentView(R.layout.activity_login_tujuh);
        }else if(screenInches>7.0&&screenInches<=8.0){
            setContentView(R.layout.activity_login_delapan);
        }else if(screenInches>8.0&&screenInches<=9.0){
            setContentView(R.layout.activity_login_delapan);
        }else if(screenInches>9.0&&screenInches<=10.0) {
            setContentView(R.layout.activity_login_sepuluh);
        }else if(screenInches>10.0&&screenInches<=11.0){
            setContentView(R.layout.activity_login_sepuluh);
        }*/

        TextView eoffice = (TextView)findViewById(R.id.tvEoffice);
        TextView mobile = (TextView)findViewById(R.id.tvMobile);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
        eoffice.setTypeface(custom_font);
        mobile.setTypeface(custom_font);

        final TextView ubahPerangkat = (TextView) findViewById(R.id.tvUbahPerangkat);
        ubahPerangkat.setText(Html.fromHtml("<u>Ubah Perangkat</u>"));

        //change status bar color for lollipop and above
        if(Build.VERSION.SDK_INT >= 21){
            ((FrameLayout) log.this.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT)). setForeground(null);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(210, 210, 210));
        }

        Bundle bd = getIntent().getExtras();
        if(bd != null)
        {
            FlagGooglePlayService = (boolean) bd.get("googlePlay");
        }

        if(FlagGooglePlayService == false)
        {
            //checking Google Play Service
            FlagGooglePlayService = pf.isActiveGoogleService(log.this);
        }

        editNpp = (AppCompatEditText) findViewById(R.id.editNPP);
        editPassword = (AppCompatEditText) findViewById(R.id.editPassword);
        btLogin = (Button) findViewById(R.id.btLogin);

        session = new UserSessionManager(log.this);

        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //imei = mngr.getDeviceId();
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(log.this);

        imeiSIM1 = telephonyInfo.getImsiSIM1();
        imeiSIM2 = telephonyInfo.getImsiSIM2();

        imei = imeiSIM1 + "~" + imeiSIM2;

        //cek apakah button ditekan
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cek();
            }
        });

        ubahPerangkat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubahPerangkat();
            }
        });

        /*if(Build.VERSION.SDK_INT >= 22){
            //ask permission for lollipop and above
            askPermission();
        }else {


        }*/
    }

    private void ubahPerangkat()
    {
        ResetDevice = new Dialog(log.this);
        ResetDevice.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ResetDevice.setContentView(R.layout.dialog_ubah_perangkat);

        Button generateOTP = (Button) ResetDevice.findViewById(R.id.btRequestOTP);

        generateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOTP();
            }
        });

        Button ubah = (Button)ResetDevice.findViewById(R.id.btUbah);

        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestResetDevice();
            }
        });

        ResetDevice.show();
        Window window = ResetDevice.getWiLoginndow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private void requestOTP()
    {
        EditText npp2 = (EditText) ResetDevice.findViewById(R.id.editNPP);
        EditText pass2 = (EditText) ResetDevice.findViewById(R.id.editPass);
        EditText email = (EditText) ResetDevice.findViewById(R.id.editEmail);

        boolean cancel = false;
        View focusView = null;

        //reset error
        npp2.setError(null);
        pass2.setError(null);
        email.setError(null);

        //mengambil nilai npp, email, password
        String snpp2 = npp2.getText().toString();
        String spass2 = pass2.getText().toString();
        String semail2 = email.getText().toString();

        //cek apakah npp, email, password sudah terisi
        if(TextUtils.isEmpty(snpp2)){
            npp2.setError("NPP tidak boleh kosong");
            focusView = npp2;
            cancel = true;
        }
        if(TextUtils.isEmpty(spass2)){
            pass2.setError("Password tidak boleh kosong");
            focusView = pass2;
            cancel = true;
        }
        if(TextUtils.isEmpty(semail2)){
            email.setError("Email tidak boleh kosong");
            focusView = email;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }
        else{
            if(snpp2!=null&&spass2!=null&semail2!=null){
                cd=new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();

                if(isInternetPresent) {
                    mCrypt mCrypt = new mCrypt();
                    try {
                        ennpp = mCrypt.bytesToHex(mCrypt.encrypt(snpp2));
                        enpass = mCrypt.bytesToHex(mCrypt.encrypt(spass2));
                        enEmail = mCrypt.bytesToHex(mCrypt.encrypt(semail2));
                        enKey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    OTPRequest cek = new OTPRequest();
                    cek.execute(ennpp, enpass, enEmail,enKey);
                }else{
                    pf.showAlertConnection(this);
                }
            }
        }
    }

    private void requestResetDevice()
    {
        EditText npp2 = (EditText) ResetDevice.findViewById(R.id.editNPP);
        EditText pass2 = (EditText) ResetDevice.findViewById(R.id.editPass);
        EditText email = (EditText) ResetDevice.findViewById(R.id.editEmail);
        EditText otp = (EditText) ResetDevice.findViewById(R.id.editOTP);

        boolean cancel = false;
        View focusView = null;

        //reset error
        npp2.setError(null);
        pass2.setError(null);
        email.setError(null);
        otp.setError(null);

        //mengambil nilai npp, email, password, otp
        String snpp2 = npp2.getText().toString();
        String spass2 = pass2.getText().toString();
        String semail2 = email.getText().toString();
        String sotp = otp.getText().toString();

        //cek apakah npp, email, password, otp sudah terisi
        if(TextUtils.isEmpty(snpp2)){
            npp2.setError("NPP tidak boleh kosong");
            focusView = npp2;
            cancel = true;
        }
        if(TextUtils.isEmpty(spass2)){
            pass2.setError("Password tidak boleh kosong");
            focusView = pass2;
            cancel = true;
        }
        if(TextUtils.isEmpty(semail2)){
            email.setError("Email tidak boleh kosong");
            focusView = email;
            cancel = true;
        }
        if(TextUtils.isEmpty(sotp)){
            email.setError("OTP tidak boleh kosong");
            focusView = email;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }
        else{
            if(snpp2!=null&&spass2!=null&semail2!=null&&sotp!=null){
                cd=new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();

                if(isInternetPresent) {
                    mCrypt mCrypt = new mCrypt();
                    try {
                        ennpp = mCrypt.bytesToHex(mCrypt.encrypt(snpp2));
                        enpass = mCrypt.bytesToHex(mCrypt.encrypt(spass2));
                        enEmail = mCrypt.bytesToHex(mCrypt.encrypt(semail2));
                        enOTP = mCrypt.bytesToHex(mCrypt.encrypt(sotp));
                        enImei1 = mCrypt.bytesToHex(mCrypt.encrypt(imeiSIM1));

                        if(imeiSIM2.isEmpty() || imeiSIM2 == null)
                        {
                            enImei2 = "";
                        }
                        else {
                            enImei2 = mCrypt.bytesToHex(mCrypt.encrypt(imeiSIM2));
                        }

                        enKey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ChangeRequest cek = new ChangeRequest();
                    cek.execute(ennpp, enpass, enEmail,enOTP,enImei1,enImei2,enKey);
                }else{
                    pf.showAlertConnection(this);
                }
            }
        }
    }

    private void cek() {
        boolean cancel = false;
        View focusView = null;

        //reset error
        editNpp.setError(null);
        editPassword.setError(null);

        //mengambil nilai npp dan password
        npp = editNpp.getText().toString();
        pass = editPassword.getText().toString();

        //cek apakah npp dan password sudah terisi
        if(TextUtils.isEmpty(npp)){
            editNpp.setError("NPP tidak boleh kosong");
            focusView = editNpp;
            cancel = true;
        }
        if(TextUtils.isEmpty(pass)){
            editPassword.setError("Password tidak boleh kosong");
            focusView = editPassword;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }
        else{
            if(npp!=null&&pass!=null){
                cd=new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();

                if(isInternetPresent) {
                    if(FlagGooglePlayService == true) {
                        if (FirebaseInstanceId.getInstance().getToken() == null) {
                            token = FirebaseInstanceId.getInstance().getToken();
                            /*Toast.makeText(log.this, token, Toast.LENGTH_SHORT).show();*/
                            login();
                        } else {
                            token = FirebaseInstanceId.getInstance().getToken();
                            login();
                        }
                    }
                    else {
                        login();
                    }
                }else{
                    pf.showAlertConnection(this);
                }
            }else{
                editNpp.setText(null);
                editPassword.setText(null);
            }
        }
    }

    private void login(){
        mCrypt mCrypt = new mCrypt();
        try {
            ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
            enpass = mCrypt.bytesToHex(mCrypt.encrypt(pass));
            enImei = mCrypt.bytesToHex(mCrypt.encrypt(imei + "|" + Config.versi + "|0")); //0 untuk flag login
            if(token != null) {
                enToken = mCrypt.bytesToHex(mCrypt.encrypt(token));
            }
            else enToken = "";
            enKey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        cobaLogin(ennpp, enpass, enImei, enToken, enKey);
    }

    private void KirimNotif(){
        mCrypt mCrypt = new mCrypt();
        try {
            ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
            enpass = mCrypt.bytesToHex(mCrypt.encrypt(pass));
            enImei = mCrypt.bytesToHex(mCrypt.encrypt(imei + "|" + Config.versi + "|1")); //0 untuk flag kirim notif
            if(token != null) {
                enToken = mCrypt.bytesToHex(mCrypt.encrypt(token));
            }
            else enToken = "";
            enKey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        cobaLogin2(ennpp, enpass, enImei, enToken, enKey);
    }

    private void cobaLogin(String np, String pw, final String imeiversi, String token, String key) {
        class LoginAsync extends AsyncTask<String, Void, JSONObject> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(log.this, null, "Harap tunggu . . .");
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                String np = params[0];
                String pw = params[1];
                String imversi = params[2];
                String tokens = params[3];
                String key = params[4];
                String responce;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, np));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_PASSWORD, pw));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_TOKEN, tokens));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_IMEII, imversi));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    //HttpPost httpPost = new HttpPost(Config.urlLanding+"RegisterToken.php");
                    HttpPost httpPost = new HttpPost(Config.urlLanding+"login.php");
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

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    cancel(true);
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    cancel(true);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                loadingDialog.dismiss();
                if (result != null) {
                    try {
                        String kode = result.getString("Kode");
                        String keterangan = result.getString("Keterangan");
                        empid = result.getString("EmployeeID");
                        String disclaimer = result.getString("Disclaimer");
                        String flagLogin = result.getString("Flag");


                        if(flagLogin.equals("0")) {
                            if (kode.equals("1")) {
                                session.createUserLoginSession(npp, empid);
                                Intent pindah = new Intent(log.this, rl.class);
                                pindah.putExtra("empid", empid);
                                pindah.putExtra("npp", npp);
                                pindah.putExtra("disclaimer", disclaimer);
                                pindah.putExtra("fromActivity","login");
                                startActivity(pindah);
                                finish();
                            } else {
                                if(kode.equals("5")) {
                                    KirimNotif();
                                }
                                showAlert(keterangan);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onCancelled() {
                // Show your Toast
                loadingDialog.dismiss();
                pf.showAlertTimeOut(log.this);
            }
        }
        LoginAsync reg = new LoginAsync();
        reg.execute(np, pw, imeiversi, token, key);
    }

    private void cobaLogin2(String np, String pw, final String imeiversi, String token, String key) {
        class LoginAsync extends AsyncTask<String, Void, JSONObject> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                String np = params[0];
                String pw = params[1];
                String imversi = params[2];
                String tokens = params[3];
                String key = params[4];
                String responce;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, np));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_PASSWORD, pw));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_TOKEN, tokens));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_IMEII, imversi));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    //HttpPost httpPost = new HttpPost(Config.urlLanding+"RegisterToken.php");
                    HttpPost httpPost = new HttpPost(Config.urlLanding+"login.php");
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

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                        empid = result.getString("EmployeeID");
                        String disclaimer = result.getString("Disclaimer");
                        String flagLogin = result.getString("Flag");

                        if(flagLogin.equals("0")) {
                            if (kode.equals("1")) {
                                session.createUserLoginSession(npp, empid);
                                Intent pindah = new Intent(log.this, rl.class);
                                pindah.putExtra("empid", empid);
                                pindah.putExtra("npp", npp);
                                pindah.putExtra("disclaimer", disclaimer);
                                startActivity(pindah);
                                finish();
                            } else {
                                if(kode.equals("5")) {
                                    KirimNotif();
                                }
                                showAlert(keterangan);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        LoginAsync reg = new LoginAsync();
        reg.execute(np, pw, imeiversi, token, key);
    }

    class OTPRequest extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(log.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String npp = params[0];
            String pass = params[1];
            String email = params[2];
            String key = params[3];
            String responce;


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, npp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PASSWORD, pass));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMAIL, email));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GenerateOTPResetDevice.php");
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
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                loadingDialog.dismiss();
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Message");

                    Toast.makeText(log.this, keterangan,
                            Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(log.this);
        }
    }

    class ChangeRequest extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(log.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String npp = params[0];
            String pass = params[1];
            String email = params[2];
            String otp = params[3];
            String imei1 = params[4];
            String imei2 = params[5];
            String key = params[6];
            String responce;


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, npp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PASSWORD, pass));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMAIL, email));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_OTP, otp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_IMEII1, imei1));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_IMEII2, imei2));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "ResetDevice.php");
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
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                loadingDialog.dismiss();
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Message");

                    Toast.makeText(log.this, keterangan,
                            Toast.LENGTH_LONG).show();

                    if(kode.equals("1"))
                    {
                        ResetDevice.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(log.this);
        }
    }

    public void showAlert(final String pesan){
        Toast.makeText(log.this, pesan,
                Toast.LENGTH_LONG).show();
        editNpp.setText(null);
        editPassword.setText(null);

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        //String pesan = "Anda yakin akan keluar dari aplikasi?";
        //showAlertBack(pesan);
    }
}

