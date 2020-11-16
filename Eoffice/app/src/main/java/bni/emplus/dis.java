package bni.emplus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import bni.emplus.Adapter.ViewPagerAdapter;
import bni.emplus.Model.modelHistori;
import bni.emplus.Model.modelLampiran;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class dis extends AppCompatActivity {

    Dialog register,close;
    Calendar myCalendar;

    public boolean dialogConnection = false;
    // Declaring Your View and Variables
    public ViewPager pager;
    public ViewPagerAdapter adapter;
    public boolean statOpenPDF = false, mIsFavorit = false, IsFavoritAwal = false;
    public slid tabs;
    CharSequence Titles[]={"Detail","Lampiran","Riwayat"};
    int Numboftabs =3;
    String isfavorit;
    String docid,doctypename,boxid,docregid,modefirstdoc,regno,mode,crtime;
    String enunitid,enempid,endocregid,entitle,endocno,enaddress,entglsurat,entglregister,ennotes,enkey,endocid,enboxid,enroleid,enkomen;

    public String fragment;
    UserSessionManager session;
    String npp,empid,roleid,unitid,statid,erid;
    Button btDisposisi,btCloseDisposisi;
    public FloatingActionButton favorit;

    public HashMap<String,String> isidisposisi = new HashMap<String,String>();
    public List<modelLampiran> filedisposisi = new ArrayList<>();
    public List<modelHistori> historidisposisi = new ArrayList<>();

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    private static final String TAG_ID = "Id";
    private static final String TAG_NO = "DocumentNo";
    private static final String TAG_MELALUI ="MelaluiName";
    private static final String TAG_TO = "Tujuan_To";
    private static final String TAG_CC = "Tujuan_CC";
    private static final String TAG_BCC = "Tujuan_BCC";
    private static final String TAG_FROM = "Dari";
    private static final String TAG_LAMPIRAN = "Lampiran";
    private static final String TAG_ISI = "IsiSurat";
    private static final String TAG_TempatTanggal = "TempatTanggalSurat";
    private static final String TAG_TANGGAL = "TanggalSurat";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_ISFAVORIT = "IsFavorit";

    PublicFunction pf = new PublicFunction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        double screenInches = pf.getScreenInches(this);
        if(screenInches<=4.0){
            setContentView(R.layout.activity_disposisi);
        }else if(screenInches>4.0&&screenInches<=5.0){
            setContentView(R.layout.activity_disposisi);
        }else if(screenInches>5.0&&screenInches<=6.0){
            setContentView(R.layout.activity_disposisi);
        }else if(screenInches>6.0&&screenInches<=7.0){
            setContentView(R.layout.activity_disposisi_tujuh);
        }else if(screenInches>7.0&&screenInches<=8.0){
            setContentView(R.layout.activity_disposisi_tujuh);
        }else if(screenInches>8.0&&screenInches<=9.0){
            setContentView(R.layout.activity_disposisi_tujuh);
        }else if(screenInches>9.0&&screenInches<=10.0) {
            setContentView(R.layout.activity_disposisi_tujuh);
        }else if(screenInches>10.0&&screenInches<=11.0){
            setContentView(R.layout.activity_disposisi_tujuh);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
        }

        setTitle("Sirkulasi");

        //session
        session = new UserSessionManager(dis.this);
        if (session.checkLogin()) {
            Intent i = new Intent(dis.this, log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        HashMap<String, String> user = session.getUserNPP();
        HashMap<String, String> userRole = session.getUserRole();

        // get npp,empid,roleid,unitid
        npp = user.get(UserSessionManager.KEY_NAME);
        unitid = userRole.get(UserSessionManager.KEY_UNIT);
        roleid = userRole.get(UserSessionManager.KEY_ROLE);
        empid = user.get(UserSessionManager.KEY_EMPID);
        statid = userRole.get(UserSessionManager.KEY_STATID);
        erid = userRole.get(UserSessionManager.KEY_ERID);

        Bundle args = getIntent().getExtras();
        if(args!=null){
            docid = (String) args.get("docid");
            doctypename = (String) args.get("typename");
            boxid = (String) args.get("boxid");
            docregid = (String) args.get("docregid");
            modefirstdoc = (String) args.get("modefirstdoc");
            mode = (String)args.get("mode");
            regno = (String)args.get("regno");
            crtime = (String)args.get("tanggal");
            fragment = (String)args.get("fragment");
            /*isfavorit = (String)args.get("isfavorit");*/
        }

        /*if(isfavorit.equals("1")) //dokumen favorit
        {
            mIsFavorit = true;
        }
        else
        {
            mIsFavorit = false;
        }*/

        favorit = (FloatingActionButton) findViewById(R.id.fab);
        if(mIsFavorit)
        {
            favorit.setImageResource(R.drawable.ic_star);
        }
        else
        {
            favorit.setImageResource(R.drawable.ic_star_white);
        }

        favorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fav = "";
                if(mIsFavorit)
                {
                    //showAlertHapusFavorit();
                    fav = "2";
                    ManageFavorit(fav);
                }
                else
                {
                    //showAlertSetFavorit();
                    fav = "1";
                    ManageFavorit(fav);

                }
            }
        });

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs,docid,doctypename,boxid,docregid,modefirstdoc,regno,mode,crtime,fragment);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (slid) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        btDisposisi = (Button) findViewById(R.id.btDisposisi);
        btCloseDisposisi = (Button) findViewById(R.id.btTutupDisposisi);

        btDisposisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete del = new Delete();
                del.delete();
                /*jika mode=2 dan registrationno is null or empty, maka keluarkan validasi tidak bisa disposisi dan harus meregisterkan surat tsb*/
                if((regno==null||regno.isEmpty())&&mode.equals("2")&&fragment.equals("hardcopy")){
                    showAlert("Lakukan registrasi surat terlebih dahulu!");
                }
                else if(!isidisposisi.isEmpty()){
                    Intent pindah = new Intent(dis.this,pdis.class);
                    pindah.putExtra("docid",docid);
                    pindah.putExtra("boxid",boxid);
                    pindah.putExtra("doctype",doctypename);
                    pindah.putExtra("docregid",docregid);
                    pindah.putExtra("modefirstdoc",modefirstdoc);
                    pindah.putExtra("status","disposisi");
                    pindah.putExtra("mode",mode);
                    pindah.putExtra("regno",regno);
                    pindah.putExtra("tanggal",crtime);
                    pindah.putExtra("fragment",fragment);
                    startActivity(pindah);
                }
            }
        });

        btCloseDisposisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isidisposisi.isEmpty()) {
                    Delete del = new Delete();
                    del.delete();
                    dialogClose();
                }
            }
        });
    }

    public void showAlertSetFavorit(){
        dis.this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(dis.this);
                builder.setMessage(Config.alertTambahFavorit).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String fav = "1";
                        ManageFavorit(fav);
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    public void showAlertHapusFavorit(){
        dis.this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(dis.this);
                builder.setMessage(Config.alertHapusFavorit).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String fav = "2";
                        ManageFavorit(fav);
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    private void ManageFavorit(String Flag) {
        String empdocid="",ennpp="",enunitid="",enroleid="",enstat="",enflag="",enkey="",empdocregid="";

        cd=new ConnectionDetector(this.getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent){
            mCrypt mCrypt = new mCrypt();
            try {
                empdocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
                empdocregid = mCrypt.bytesToHex(mCrypt.encrypt(docregid));
                ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
                enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitid));
                enroleid = mCrypt.bytesToHex(mCrypt.encrypt(roleid));
                enstat = mCrypt.bytesToHex(mCrypt.encrypt(statid));
                enflag = mCrypt.bytesToHex(mCrypt.encrypt(Flag));
                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Favorit fav = new Favorit();
            fav.execute(empdocid,ennpp,enunitid,enroleid,enstat,enflag,empdocregid,enkey);

        }else{
            pf.showAlertConnection(this);

        }
    }

    class Favorit extends AsyncTask<String, Void, JSONObject> {

        int pos;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;
            String Emp_Doc_Id = params[0];
            String Npp = params[1];
            String Unit_Id = params[2];
            String Role_Id = params[3];
            String Emp_Role_Status_Id = params[4];
            String Flag = params[5];
            String Emp_Doc_regid = params[6];
            String key = params[7];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, Emp_Doc_Id));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, Npp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, Role_Id));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, Unit_Id));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_STATID, Emp_Role_Status_Id));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_FLAG, Flag));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCREG, Emp_Doc_regid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "Favorit.php");
                //HttpPost httpPost = new HttpPost(Config.url + "KirimDisposisi");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //TimeOut
                HttpParams httpParameters = httpPost.getParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 50000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 50000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();

                responce = EntityUtils.toString(entity);

                return new JSONObject(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(dis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(dis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(dis.this);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            pf.showAlertTimeOut(dis.this);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                String flags = "";
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");
                    flags = result.getString("FlagKodeError");

                    if (kode.equals("1")||kode.equals("0")) {
                        showAlertFav(keterangan,kode);
                    } else {
                        showAlertFav(keterangan,kode);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showAlertFav(final String message, final String Kode){
        Toast.makeText(dis.this, message,
                Toast.LENGTH_LONG).show();

        if (Kode.equals("1")) {
            favorit.setImageResource(R.drawable.ic_star);
            mIsFavorit = true;
            isidisposisi.put(TAG_ISFAVORIT, String.valueOf(mIsFavorit));
        } else if (Kode.equals("0")) {
            favorit.setImageResource(R.drawable.ic_star_white);
            mIsFavorit = false;
            isidisposisi.put(TAG_ISFAVORIT, String.valueOf(mIsFavorit));
        }
    }

    public void dialogClose(){
        close = new Dialog(dis.this);
        close.requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        double screenInches = pf.getScreenInches(this);
        if (screenInches <= 4.0) {
            close.setContentView(R.layout.activity_add_notes);
        } else if (screenInches > 4.0 && screenInches <= 5.0) {
            close.setContentView(R.layout.activity_add_notes);
        } else if (screenInches > 5.0 && screenInches <= 6.0) {
            close.setContentView(R.layout.activity_add_notes);
        } else if (screenInches > 6.0 && screenInches <= 7.0) {
            close.setContentView(R.layout.activity_add_notes_tujuh);
        } else if (screenInches > 7.0 && screenInches <= 8.0) {
            close.setContentView(R.layout.activity_add_notes_tujuh);
        } else if (screenInches > 8.0 && screenInches <= 9.0) {
            close.setContentView(R.layout.activity_add_notes_tujuh);
        } else if (screenInches > 9.0 && screenInches <= 10.0) {
            close.setContentView(R.layout.activity_add_notes_tujuh);
        } else if (screenInches > 10.0 && screenInches <= 11.0) {
            close.setContentView(R.layout.activity_add_notes_tujuh);
        }

        final EditText editCatatan;
        final Button btTambahCatatan;

        editCatatan = (EditText) close.findViewById(R.id.editCatatanApp);

        btTambahCatatan = (Button) close.findViewById(R.id.btTambahCatatan);

        btTambahCatatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd=new ConnectionDetector(dis.this);
                isInternetPresent = cd.isConnectingToInternet();

                if(isInternetPresent) {
                    mCrypt mCrypt = new mCrypt();
                    try {
                        enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                        endocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
                        enboxid = mCrypt.bytesToHex(mCrypt.encrypt(boxid));
                        enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitid));
                        enempid = mCrypt.bytesToHex(mCrypt.encrypt(empid));
                        enroleid = mCrypt.bytesToHex(mCrypt.encrypt(roleid));
                        if (editCatatan.getText().toString().isEmpty() || editCatatan.getText().toString().equals("")) {
                            enkomen = editCatatan.getText().toString();
                        } else {
                            enkomen = mCrypt.bytesToHex(mCrypt.encrypt(editCatatan.getText().toString()));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    closeDisp clos = new closeDisp();
                    clos.execute(endocid, enboxid, enunitid, enroleid, enempid, enkomen, enkey);
                }else{
                    showAlertConnection(Config.alertConnection,"close");
                }
            }
        });

        close.show();
    }

    public void showAlertConnection(final String pesan, final String status) {
        Toast.makeText(dis.this, pesan,
                Toast.LENGTH_LONG).show();

        if (status.equals("register")) {
            register.dismiss();
        } else if (status.equals("close")) {
            close.dismiss();
        }
    }

    class closeDisp extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(dis.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String docid = params[0];
            String inboxid = params[1];
            String unitid = params[2];
            String roleid = params[3];
            String empid = params[4];
            String komen = params[5];
            String key = params[6];

            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID,docid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_INBOXID, inboxid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_KOMEN, komen));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "SetCloseDisposisi.php");
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
                //pf.showAlertTimeOut(dis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(dis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(dis.this);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(dis.this);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            loadingDialog.dismiss();
            if (result != null) {
                try {
                    String kode = result.getString("Kode");
                    String ket = result.getString("Keterangan");

                    if(kode.equals("1")){
                        String title = "Success";
                        showAlertSukses(ket, title,"close");
                    }
                    else{
                        String title = "Error";
                        showAlertSukses(ket, title,"close");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showAlertSukses(final String pesan, final String title, final String status) {
        Toast.makeText(dis.this, pesan,
                Toast.LENGTH_LONG).show();
        if (status.equals("close")) {
            Intent pindah = new Intent(dis.this, inb.class);
            pindah.putExtra("fragment", fragment);
            startActivity(pindah);
            finish();
        }
    }

    public void dialog(){
        register = new Dialog(this);
        register.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        register.setContentView(R.layout.register_surat);

        ImageView kalender;
        final EditText tanggalRegister, catatanRegister;
        final Button registerbt, cancelbt;

        kalender = (ImageView) register.findViewById(R.id.ivKalenderRegister);

        tanggalRegister = (EditText) register.findViewById(R.id.editIsiTanggalRegister);
        catatanRegister = (EditText) register.findViewById(R.id.editIsiCatatanRegister);

        registerbt = (Button) register.findViewById(R.id.btRegister);
        cancelbt = (Button) register.findViewById(R.id.btCancel);

        myCalendar = Calendar.getInstance(TimeZone.getDefault());
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggalRegister.setText(sdf.format(myCalendar.getTime()));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth,
                                  int mDay) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, mYear);
                myCalendar.set(Calendar.MONTH, mMonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, mDay);

                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                tanggalRegister.setText(sdf.format(myCalendar.getTime()));
            }

        };

        kalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(dis.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tanggalRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(dis.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        registerbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String catatan = catatanRegister.getText().toString();
                String tanggal = tanggalRegister.getText().toString()+"T00:00:00.000";
                String title = isidisposisi.get(TAG_TITLE);
                String docno = isidisposisi.get(TAG_NO);
                String tanggalsurat = isidisposisi.get(TAG_TANGGAL);
                String address = sep(isidisposisi.get(TAG_TempatTanggal));

                cd=new ConnectionDetector(dis.this);
                isInternetPresent = cd.isConnectingToInternet();

                if(isInternetPresent) {
                    mCrypt mCrypt = new mCrypt();
                    try {
                        enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitid));
                        enempid = mCrypt.bytesToHex(mCrypt.encrypt(empid));
                        endocregid = mCrypt.bytesToHex(mCrypt.encrypt(docregid));
                        entitle = mCrypt.bytesToHex(mCrypt.encrypt(title));
                        endocno = mCrypt.bytesToHex(mCrypt.encrypt(docno));
                        enaddress = mCrypt.bytesToHex(mCrypt.encrypt(address));
                        entglsurat = mCrypt.bytesToHex(mCrypt.encrypt(tanggalsurat));
                        entglregister = mCrypt.bytesToHex(mCrypt.encrypt(tanggal));
                        if(catatan.equals("")||catatan.isEmpty()){
                            ennotes = "";
                        }else {
                            ennotes = mCrypt.bytesToHex(mCrypt.encrypt(catatan));
                        }
                        enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    registerSurat register = new registerSurat();
                    register.execute(enunitid, enempid, endocregid, entitle, endocno, enaddress, entglsurat, entglregister, ennotes, enkey);
                }else {
                    showAlertConnection(Config.alertConnection,"register");
                }
            }
        });
        cancelbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.dismiss();
            }
        });
        register.show();
    }

    public static String sep(String s)
    {
        int l = s.indexOf(",");
        if (l >0)
        {
            return s.substring(0, l);
        }
        return "";

    }

    class registerSurat extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(dis.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;
            String current_unit_id = params[0];
            String Current_Employee_Id = params[1];
            String DocRegID = params[2];
            String Title  = params[3];
            String DocumentNo = params[4];
            String Address = params[5];
            String TglSurat  = params[6];
            String TglRegister = params[7];
            String Notes = params[8];
            String key = params[9];


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, current_unit_id));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, Current_Employee_Id));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCREG, DocRegID));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TITLE, Title));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCNO, DocumentNo));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ADDRESS, Address));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TGLSURAT, TglSurat));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TGLREG, TglRegister));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_NOTES, Notes));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY,key));

            try {regid
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "RegisterSurat.php");
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
                //showAlertConnection(Config.alertTimeOut, "register");
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //showAlertConnection(Config.alertTimeOut, "register");
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //showAlertConnection(Config.alertTimeOut, "register");
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            showAlertConnection(Config.alertTimeOut, "register");
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            loadingDialog.dismiss();
            if (result != null) {
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");

                    if(kode.equals("1")){
                        regno="1";
                        showAlertSukses(keterangan,"Success","reg");
                        register.dismiss();
                    }else{
                        showAlertSukses(keterangan,"Error","reg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showAlert(final String pesan) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(dis.this);
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog();

                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        if(mIsFavorit == IsFavoritAwal)
            finish();
        else
        {
            Intent i = new Intent(dis.this, inb.class);
            i.putExtra("fragment",fragment);
            startActivity(i);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_button:
                // search action
                Intent i = new Intent(dis.this, inb.class);
                i.putExtra("fragment",fragment);
                startActivity(i);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(statOpenPDF) {
            Delete dels = new Delete();
            dels.delete();
            statOpenPDF = false;
        }
    }
}
