package bni.emplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bni.emplus.Fragment.AboutFrag;
import bni.emplus.Fragment.ApproveFrag;
import bni.emplus.Fragment.DisposisiFrag;
//import bni.emplus.Fragment.FeedbackFrag;
import bni.emplus.Fragment.HardcopyFrag;
import bni.emplus.Fragment.ListDokumenFrag;
import bni.emplus.Fragment.MonitoringFrag;
import bni.emplus.Fragment.NewSuratKeluarFragment;
import bni.emplus.Fragment.NewSuratMasukFragment;
import bni.emplus.Fragment.NoInternet;
import bni.emplus.Fragment.OnlineFrag;
import bni.emplus.Fragment.SentFrag;
import bni.emplus.Fragment.TrackingFrag;
import bni.emplus.Fragment.UrgentFrag;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class inb extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView inbox, disposisi, urgent, approve, sent, monitor, track, inboxHardcopy, inboxOnline, newSuratMasuk, newSuratKeluar, listDokumen;
    UserSessionManager session;
    String npp,empid,roleid,unitid,statid,erid;
    String enerid,enkey,enempid, enunitid, enroleid, ennpp, fragment, menumobile;
    String docid =null;
    TextView nama,email,role;
    int position;

    PublicFunction pf = new PublicFunction();

    public HashMap<String,String> total = new HashMap<String,String>();

    public ArrayList<String> namafiles = new ArrayList<>();

    private static final String TAG_NAME = "Name";
    private static final String TAG_EMAIL = "Email";

    private static final String TAG_TOTDIS = "TotalInboxAll_D";
    private static final String TAG_TOTUR = "TotalInboxAll_U";
    private static final String TAG_TOTAPP = "TotalApprovalOnline";
    private static final String TAG_TOTINBOX = "TotalInboxAll";
    private static final String TAG_TOTSENT = "TotalSentItems";
    private static final String TAG_TOTMONITOR = "TotalMonitoring";
    private static final String TAG_TOTTRACK = "TotalTrackingHistory";
    private static final String TAG_ONLINE = "TotalDisposisiOnline";
    private static final String TAG_OFFLINE = "TotalDisposisiOffline";
    private static final String TAG_LIST = "TotalListDokumen";
    private static final String TAG_TOTFAV = "TotalFavourite";
    private static final String TAG_ROLENAME = "DispDDL";


    ArrayList<HashMap<String,String>> peg = new ArrayList<HashMap<String, String>>();
    String kataKunci = "";

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        double screenInches = pf.getScreenInches(this);

        if (screenInches <= 4.0) {
            setContentView(R.layout.activity_inbox);
        } else if (screenInches > 4.0 && screenInches <= 5.0) {
            setContentView(R.layout.activity_inbox);
        } else if (screenInches > 5.0 && screenInches <= 6.0) {
            setContentView(R.layout.activity_inbox);
        } else if (screenInches > 6.0 && screenInches <= 7.0) {
            setContentView(R.layout.activity_inbox_tujuh);
        } else if (screenInches > 7.0 && screenInches <= 8.0) {
            setContentView(R.layout.activity_inbox_tujuh);
        } else if (screenInches > 8.0 && screenInches <= 9.0) {
            setContentView(R.layout.activity_inbox_tujuh);
        } else if (screenInches > 9.0 && screenInches <= 10.0) {
            setContentView(R.layout.activity_inbox_tujuh);
        } else if (screenInches > 10.0 && screenInches <= 11.0) {
            setContentView(R.layout.activity_inbox_tujuh);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //session
        session = new UserSessionManager(inb.this);
        if (session.checkLogin()) {
            Intent i = new Intent(inb.this, log.class);
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
        menumobile = userRole.get(UserSessionManager.KEY_MenuMobile);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] { android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed},  // pressed
                new int[] { android.R.attr.state_enabled} // enabled
        };

        int[] colors = new int[] {
                Color.GRAY,
                Color.rgb(51, 155, 175),
                Color.rgb(51, 155, 175),
                Color.BLACK
        };

        ColorStateList myList = new ColorStateList(states, colors);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemTextColor(myList);

        //set style according to screen inches


        int size = 14;
        if (screenInches <= 4.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox);
            size = 14;
        } else if (screenInches > 4.0 && screenInches <= 5.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox);
            size = 14;
        } else if (screenInches > 5.0 && screenInches <= 6.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox);
            size = 14;
        } else if (screenInches > 6.0 && screenInches <= 7.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox_tujuh);
            size = 20;
        } else if (screenInches > 7.0 && screenInches <= 8.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox_tujuh);
            size = 20;
        } else if (screenInches > 8.0 && screenInches <= 9.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox_tujuh);
            size = 20;
        } else if (screenInches > 9.0 && screenInches <= 10.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox_tujuh);
            size = 20;
        } else if (screenInches > 10.0 && screenInches <= 11.0) {
            navigationView.inflateHeaderView(R.layout.nav_header_inbox_tujuh);
            size = 20;
        }

        View header = navigationView.getHeaderView(0);
        nama = (TextView) header.findViewById(R.id.tvNamaPegLogin);
        nama.setTextSize(size);
        email = (TextView) header.findViewById(R.id.tvEmailPegLogin);
        email.setTextSize(size);
        role = (TextView) header.findViewById(R.id.tvRolePegLogin);
        role.setTextSize(size);

        //set status bar for lollipop and above
        if(Build.VERSION.SDK_INT >= 21){
            ((FrameLayout) inb.this.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT)). setForeground(null);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
            header.setPadding(16,24,16,16);
        }

        disposisi = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_disposisi));
        urgent = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_urgent));
        approve = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_approve));
        sent = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_sentItem));
        monitor = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_monitoring));
        track = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_trackingHistory));
        inboxHardcopy = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_inboxHardcopy));
        inboxOnline = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_inboxOnline));
        newSuratMasuk = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_newSuratMasuk));
        newSuratKeluar = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_newSuratKeluar));
        listDokumen = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_listDokumenSurat));

        //get string putextra
        Bundle bd = getIntent().getExtras();
        if(bd != null)
        {
            fragment = (String) bd.get("fragment");
        }

        //roleid user sekretaris direksi, sekretaris, sekretaris wakil, admin unit, sekretaris wilayah
        //if (roleid.equals("4") || roleid.equals("6") || roleid.equals("8") || roleid.equals("10") || roleid.equals("16")) {
        if(menumobile.equals("1")){ //menumobile 1 adalah menu untuk sekretatris/admin
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.nav_disposisi).setVisible(false);
            nav_menu.findItem(R.id.nav_urgent).setVisible(false);
            nav_menu.findItem(R.id.nav_approve).setVisible(false);
            nav_menu.findItem(R.id.nav_sentItem).setVisible(false);
        }
        //else if(roleid.equals("1") || roleid.equals("0")) {
        else if(menumobile.equals("0")){ //menu mobile 0 adalah untuk Super admin
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.nav_disposisi).setVisible(false);
            nav_menu.findItem(R.id.nav_urgent).setVisible(false);
            nav_menu.findItem(R.id.nav_approve).setVisible(false);
            nav_menu.findItem(R.id.nav_sentItem).setVisible(false);
            nav_menu.findItem(R.id.nav_inboxHardcopy).setVisible(false);
            nav_menu.findItem(R.id.nav_inboxOnline).setVisible(false);
            nav_menu.findItem(R.id.nav_newSuratMasuk).setVisible(false);
            nav_menu.findItem(R.id.nav_newSuratKeluar).setVisible(false);
            nav_menu.findItem(R.id.nav_monitoring).setVisible(false);
            nav_menu.findItem(R.id.nav_trackingHistory).setVisible(false);
        }
        else {
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.nav_inboxHardcopy).setVisible(false);
            nav_menu.findItem(R.id.nav_inboxOnline).setVisible(false);
            nav_menu.findItem(R.id.nav_newSuratMasuk).setVisible(false);
            nav_menu.findItem(R.id.nav_newSuratKeluar).setVisible(false);
            nav_menu.findItem(R.id.nav_listDokumenSurat).setVisible(false);
            nav_menu.findItem(R.id.nav_sentItem).setVisible(false);
        }

        if(fragment.equals("nointernet")){
            setTitle("Tidak ada koneksi internet");
            NoInternet no = new NoInternet();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, no);
            fragmentTransaction.commit();
        }else {
            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            if (isInternetPresent) {
                ambilDetailPegawai();
            } else {
                pf.showAlertConnection(this);
            }

            if (fragment.equals("hardcopy")) {
                navigationView.getMenu().findItem(R.id.nav_inboxHardcopy).setChecked(true);
                displayView(R.id.nav_inboxHardcopy, kataKunci);
                position = R.id.nav_inboxHardcopy;
            } else if (fragment.equals("newsuratkeluar")) {
                navigationView.getMenu().findItem(R.id.nav_newSuratKeluar).setChecked(true);
                displayView(R.id.nav_newSuratKeluar, kataKunci);
                position = R.id.nav_newSuratKeluar;
            } else if (fragment.equals("newsuratmasuk")) {
                navigationView.getMenu().findItem(R.id.nav_newSuratMasuk).setChecked(true);
                displayView(R.id.nav_newSuratMasuk, kataKunci);
                position = R.id.nav_newSuratMasuk;
            } else if (fragment.equals("disposisi")) {
                navigationView.getMenu().findItem(R.id.nav_disposisi).setChecked(true);
                displayView(R.id.nav_disposisi, kataKunci);
                position = R.id.nav_disposisi;
            } else if (fragment.equals("urgent")) {
                navigationView.getMenu().findItem(R.id.nav_urgent).setChecked(true);
                displayView(R.id.nav_urgent, kataKunci);
                position = R.id.nav_urgent;
            } else if (fragment.equals("approve")) {
                navigationView.getMenu().findItem(R.id.nav_approve).setChecked(true);
                displayView(R.id.nav_approve, kataKunci);
                position = R.id.nav_approve;
            } else if (fragment.equals("sent")) {
                navigationView.getMenu().findItem(R.id.nav_sentItem).setChecked(true);
                displayView(R.id.nav_sentItem, kataKunci);
                position = R.id.nav_sentItem;
            } else if (fragment.equals("monitoring")) {
                navigationView.getMenu().findItem(R.id.nav_monitoring).setChecked(true);
                displayView(R.id.nav_monitoring, kataKunci);
                position = R.id.nav_monitoring;
            } else if (fragment.equals("tracking")) {
                navigationView.getMenu().findItem(R.id.nav_trackingHistory).setChecked(true);
                displayView(R.id.nav_trackingHistory, kataKunci);
                position = R.id.nav_trackingHistory;
            } else if (fragment.equals("online")) {
                navigationView.getMenu().findItem(R.id.nav_inboxOnline).setChecked(true);
                displayView(R.id.nav_inboxOnline, kataKunci);
                position = R.id.nav_inboxOnline;
            } else if (fragment.equals("list")) {
                navigationView.getMenu().findItem(R.id.nav_listDokumenSurat).setChecked(true);
                displayView(R.id.nav_listDokumenSurat, kataKunci);
                position = R.id.nav_listDokumenSurat;
            }
        }
    }

    public void initializeCounterDrawer(String totdis,String totur, String totapp, String totsent, String totfav, String tottrack, String totoff, String toton, String totlist) {
        disposisi.setGravity(Gravity.CENTER_VERTICAL);
        disposisi.setTypeface(null, Typeface.BOLD);
        disposisi.setTextColor(getResources().getColor(R.color.colorAccent));
        //disposisi.setText(total.get(TAG_TOTDIS));
        disposisi.setText(totdis);

        urgent.setGravity(Gravity.CENTER_VERTICAL);
        urgent.setTypeface(null, Typeface.BOLD);
        urgent.setTextColor(getResources().getColor(R.color.colorAccent));
        //urgent.setText(total.get(TAG_TOTUR));
        urgent.setText(totur);

        approve.setGravity(Gravity.CENTER_VERTICAL);
        approve.setTypeface(null, Typeface.BOLD);
        approve.setTextColor(getResources().getColor(R.color.colorAccent));
        //approve.setText(total.get(TAG_TOTAPP));
        approve.setText(totapp);

        sent.setGravity(Gravity.CENTER_VERTICAL);
        sent.setTypeface(null, Typeface.BOLD);
        sent.setTextColor(getResources().getColor(R.color.colorAccent));
        //sent.setText(total.get(TAG_TOTSENT));
        sent.setText(totsent);

        monitor.setGravity(Gravity.CENTER_VERTICAL);
        monitor.setTypeface(null, Typeface.BOLD);
        monitor.setTextColor(getResources().getColor(R.color.colorAccent));
        //monitor.setText(total.get(TAG_TOTFAV));
        monitor.setText(totfav);

        track.setGravity(Gravity.CENTER_VERTICAL);
        track.setTypeface(null, Typeface.BOLD);
        track.setTextColor(getResources().getColor(R.color.colorAccent));
        //track.setText(total.get(TAG_TOTTRACK));
        track.setText(tottrack);

        inboxHardcopy.setGravity(Gravity.CENTER_VERTICAL);
        inboxHardcopy.setTypeface(null, Typeface.BOLD);
        inboxHardcopy.setTextColor(getResources().getColor(R.color.colorAccent));
        //inboxHardcopy.setText(total.get(TAG_OFFLINE));
        inboxHardcopy.setText(totoff);

        inboxOnline.setGravity(Gravity.CENTER_VERTICAL);
        inboxOnline.setTypeface(null, Typeface.BOLD);
        inboxOnline.setTextColor(getResources().getColor(R.color.colorAccent));
        //inboxOnline.setText(total.get(TAG_ONLINE));
        inboxOnline.setText(toton);

        listDokumen.setGravity(Gravity.CENTER_VERTICAL);
        listDokumen.setTypeface(null, Typeface.BOLD);
        listDokumen.setTextColor(getResources().getColor(R.color.colorAccent));
        //listDokumen.setText(total.get(TAG_LIST));
        listDokumen.setText(totlist);
    }

    private void setTextView(){
        String inNama = peg.get(0).get(TAG_NAME);
        String inEmail = peg.get(0).get(TAG_EMAIL);
        String rolename = peg.get(0).get(TAG_ROLENAME);
        nama.setText(inNama);
        email.setText(inEmail);
        role.setText(rolename);
    }

    class DeleteFileFromLandingPage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String namaFile = params[0];
            String ennamafile = "";

            mCrypt mCrypt = new mCrypt();
            try {
                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                ennamafile = mCrypt.bytesToHex(mCrypt.encrypt(namaFile));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, enkey));
            nameValuePairs.add(new BasicNameValuePair("av",ennamafile));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "DeleteFileFromLandingPage.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //TimeOut
                HttpParams httpParameters = httpPost.getParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 30000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 30000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();

                responce = EntityUtils.toString(entity);

                return new String(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            //loadingDialog.dismiss();
            pf.showAlertTimeOut(inb.this);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                String success = result.toString();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if(!namafiles.isEmpty()){
                for(int i=0;i<namafiles.size();i++){
                    cd=new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();
                    if(isInternetPresent) {
                        DeleteFileFromLandingPage del = new DeleteFileFromLandingPage();
                        del.execute(empid+namafiles.get(i));
                    }else{
                        pf.showAlertConnection(this);
                    }
                }
            }
            if(fragment.equals("urgent")||fragment.equals("hardcopy")||fragment.equals("nointernet")){
                finishAffinity();
            }
            else if(fragment.equals("list") && (roleid.equals("0") || roleid.equals("1"))){
                finishAffinity();
            }
            else{
                //if(roleid.equals("4")||roleid.equals("6")||roleid.equals("8")||roleid.equals("10")||roleid.equals("16")){
                if(menumobile.equals("1")){
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.getMenu().findItem(R.id.nav_inboxHardcopy).setChecked(true);
                    displayView(R.id.nav_inboxHardcopy,kataKunci);
                    position = R.id.nav_inboxHardcopy;

                }
                //else if(roleid.equals("1")||roleid.equals("0")){
                else if(menumobile.equals("0")){
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.getMenu().findItem(R.id.nav_listDokumenSurat).setChecked(true);
                    displayView(R.id.nav_listDokumenSurat,kataKunci);
                    position = R.id.nav_listDokumenSurat;
                }
                else{
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.getMenu().findItem(R.id.nav_urgent).setChecked(true);
                    displayView(R.id.nav_urgent,kataKunci);
                    position = R.id.nav_urgent;
                }
            }
        }

    }

    public void showAlertBack(final String pesan){
        inb.this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(inb.this);
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        cd=new ConnectionDetector(getApplicationContext());
                        isInternetPresent = cd.isConnectingToInternet();

                        if(isInternetPresent) {
                            mCrypt mCrypt = new mCrypt();
                            try {
                                ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
                                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Logout logout = new Logout();
                            logout.execute(ennpp, enkey);
                        }else{
                            pf.showAlertConnection(inb.this);
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inbox, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search_button).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Cari...");

        // Get the search close button image view
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.search_src_text);

                //Clear the text from EditText view
                et.setText("");

                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                //searchView.onActionViewCollapsed();
                //Collapse the search widget

                displayView(position,"");
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search_button), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                displayView(position, "");
                return true;
            }
        });

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                query = query.replace(" ", "%20");
                query = pf.validateSearchCharacter(query);
                searchView.clearFocus();
                searchView.setFocusable(false);
                displayView(position, query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return super.onCreateOptionsMenu(menu);
    }

    public void displayView(int id,String kataKunci) {
        if(!namafiles.isEmpty()){
            for(int i=0;i<namafiles.size();i++){
                cd=new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if(isInternetPresent) {
                    DeleteFileFromLandingPage del = new DeleteFileFromLandingPage();
                    del.execute(empid+namafiles.get(i));
                }else{
                    pf.showAlertConnection(this);
                }
            }
        }
        if (id == R.id.nav_approve) {
            setTitle("Kotak surat persetujuan");
            ApproveFrag persetujuanFrag = new ApproveFrag();


            fragment = "approve";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            persetujuanFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, persetujuanFrag);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_disposisi) {
            setTitle("Kotak surat masuk");
            DisposisiFrag disposisiFrag = new DisposisiFrag();

            fragment="disposisi";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            disposisiFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, disposisiFrag);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_urgent) {
            setTitle("Kotak surat prioritas");
            UrgentFrag urgentFrag = new UrgentFrag();

            fragment="urgent";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            urgentFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, urgentFrag);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_sentItem) {
            setTitle("Surat terkirim");
            SentFrag sentFrag = new SentFrag();

            fragment="sent";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            sentFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, sentFrag);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_monitoring) {
            setTitle("Favorit");
            MonitoringFrag monitoringFrag = new MonitoringFrag();

            fragment="monitoring";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            monitoringFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, monitoringFrag);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_trackingHistory) {
            setTitle("Riwayat Surat");
            TrackingFrag trackingFrag = new TrackingFrag();

            fragment="tracking";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            trackingFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, trackingFrag);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_about) {
            setTitle("Tentang");
            AboutFrag aboutfrag = new AboutFrag();

            fragment="tentang";

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, aboutfrag);
            fragmentTransaction.commit();
        }
        else if(id == R.id.nav_changeRole){
            Intent pindah = new Intent(inb.this,rl.class);
            pindah.putExtra("empid",empid);
            pindah.putExtra("fromActivity","home");
            startActivity(pindah);
        }
        /*else if(id == R.id.nav_feedback){
            *//*Intent pindah = new Intent(inb.this,fba.class);
            startActivity(pindah);*//*
            setTitle("Umpan Balik");
            FeedbackFrag feedbackfrag = new FeedbackFrag();

            fragment="umpanbalik";

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, feedbackfrag);
            fragmentTransaction.commit();

        }*/
        else if(id == R.id.nav_logout){
            String pesan = "Anda yakin akan keluar dari aplikasi?";
            showAlertBack(pesan);

        }else if(id==R.id.nav_inboxHardcopy){
            setTitle("Kotak surat versi cetak");
            HardcopyFrag hardcopyFrag = new HardcopyFrag();

            fragment="hardcopy";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            hardcopyFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, hardcopyFrag);
            fragmentTransaction.commit();

        }else if(id==R.id.nav_inboxOnline){
            setTitle("Kotak surat On line");
            OnlineFrag onlineFrag = new OnlineFrag();

            fragment="online";
            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            onlineFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, onlineFrag);
            fragmentTransaction.commit();
        }else if(id==R.id.nav_newSuratMasuk){
            fragment="suratMasuk";
            cd=new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            if(isInternetPresent) {
                mCrypt mCrypt = new mCrypt();
                try {
                    enempid = mCrypt.bytesToHex(mCrypt.encrypt(empid));
                    enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitid));
                    enroleid = mCrypt.bytesToHex(mCrypt.encrypt(roleid));
                    enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bookDocid book = new bookDocid();
                book.execute(enempid, enunitid, enroleid, enkey, "in");
            }else{
                showAlertConnection();
            }
        }else if(id==R.id.nav_newSuratKeluar){
            fragment = "suratKeluar";
            cd=new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            if(isInternetPresent) {
                mCrypt mCrypt = new mCrypt();
                try {
                    enempid = mCrypt.bytesToHex(mCrypt.encrypt(empid));
                    enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitid));
                    enroleid = mCrypt.bytesToHex(mCrypt.encrypt(roleid));
                    enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bookDocid book = new bookDocid();
                book.execute(enempid, enunitid, enroleid, enkey, "out");
            }else{
                showAlertConnection();
            }
        }else if(id==R.id.nav_listDokumenSurat){
            fragment="list";
            setTitle("Daftar semua surat");
            ListDokumenFrag listDokumenFrag = new ListDokumenFrag();

            Bundle args = new Bundle();
            //put string
            args.putString("kataKunci", kataKunci);
            //set argument
            listDokumenFrag.setArguments(args);

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, listDokumenFrag);
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
            int id = item.getItemId();
            displayView(id, kataKunci);
            item.setChecked(true);
            position = id;

            return true;
    }

    class bookDocid extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;
        String stat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(inb.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String empid = params[0];
            String unitid = params[1];
            String roleid = params[2];
            String key = params[3];
            stat = params[4];

            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "BookEmpDocID.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //TimeOut
                HttpParams httpParameters = httpPost.getParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 30000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
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
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            loadingDialog.dismiss();
            if (result != null) {
                try {
                    docid = result.getString("DocID");

                    if(stat.equals("in")){
                        suratMasuk();
                    }else if(stat.equals("out")) {
                        suratKeluar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            initializeCounterDrawer(total.get(TAG_TOTDIS),total.get(TAG_TOTUR),total.get(TAG_TOTAPP),total.get(TAG_TOTSENT),total.get(TAG_TOTFAV),total.get(TAG_TOTTRACK),total.get(TAG_OFFLINE),total.get(TAG_ONLINE),total.get(TAG_LIST));
        }
        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(inb.this);
        }
    }

    class ambilPegawai extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(inb.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            String erid = params[0];
            String key = params[1];
            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ERID, erid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetEmpRoleObject.php");
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

                return new JSONArray(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
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
            pf.showAlertTimeOut(inb.this);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String name = e.getString(TAG_NAME);
                        String email = e.getString(TAG_EMAIL);
                        String totdis = e.getString(TAG_TOTDIS);
                        String toturgent = e.getString(TAG_TOTUR);
                        String totapp = e.getString(TAG_TOTAPP);
                        String totinbox = e.getString(TAG_TOTINBOX);
                        String totsent = e.getString(TAG_TOTSENT);
                        String totmonitor = e.getString(TAG_TOTMONITOR);
                        String totfav = e.getString(TAG_TOTFAV);
                        String tottrack = e.getString(TAG_TOTTRACK);
                        String totonline = e.getString(TAG_ONLINE);
                        String totoffline = e.getString(TAG_OFFLINE);
                        String totlist = e.getString(TAG_LIST);
                        String rolename = e.getString(TAG_ROLENAME);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_NAME, name);
                        map.put(TAG_EMAIL, email);
                        map.put(TAG_ROLENAME,rolename);
                        total.put(TAG_TOTDIS,totdis);
                        total.put(TAG_TOTUR, toturgent);
                        total.put(TAG_TOTAPP,totapp);
                        total.put(TAG_TOTINBOX,totinbox);
                        total.put(TAG_TOTSENT,totsent);
                        total.put(TAG_TOTMONITOR,totmonitor);
                        total.put(TAG_TOTTRACK,tottrack);
                        total.put(TAG_ONLINE,totonline);
                        total.put(TAG_OFFLINE,totoffline);
                        total.put(TAG_LIST,totlist);
                        total.put(TAG_TOTFAV,totfav);

                        peg.add(map);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                setTextView();
                initializeCounterDrawer(total.get(TAG_TOTDIS),total.get(TAG_TOTUR),total.get(TAG_TOTAPP),total.get(TAG_TOTSENT),total.get(TAG_TOTFAV),total.get(TAG_TOTTRACK),total.get(TAG_OFFLINE),total.get(TAG_ONLINE),total.get(TAG_LIST));
            }
        }
    }
    void suratKeluar(){
        setTitle("Registrasi surat keluar");
        NewSuratKeluarFragment newSuratKeluarFrag = new NewSuratKeluarFragment();

        Bundle args = new Bundle();
        args.putString("docid", docid);
        newSuratKeluarFrag.setArguments(args);

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, newSuratKeluarFrag);
        fragmentTransaction.commit();
    }
    void suratMasuk(){
        setTitle("Registrasi surat masuk");
        NewSuratMasukFragment newSuratMasukFrag = new NewSuratMasukFragment();

        Bundle args = new Bundle();

        args.putString("docid",docid);

        newSuratMasukFrag.setArguments(args);

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, newSuratMasukFrag);
        fragmentTransaction.commit();
    }
    class Logout extends AsyncTask<String, Void, String> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(inb.this, null, "Harap tunggu . . .");
        }

        @Override
        protected String doInBackground(String... params) {
            String npp = params[0];
            String key = params[1];

            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, npp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "logout.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //TimeOut
                HttpParams httpParameters = httpPost.getParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 30000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 30000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();

                responce = EntityUtils.toString(entity);
                return new String(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(inb.this);
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(inb.this);
        }
        @Override
        protected void onPostExecute(String result) {
            loadingDialog.dismiss();
            if (result != null) {
                if (result.trim().equals("1")) {
                    session.logoutUser();
                }
            }
        }
    }
    public void ambilDetailPegawai(){
        peg = new ArrayList<HashMap<String, String>>();

        mCrypt mCrypt = new mCrypt();
        try {
            enerid = mCrypt.bytesToHex(mCrypt.encrypt(erid));
            enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilPegawai amPegawai = new ambilPegawai();
        amPegawai.execute(enerid, enkey);
    }

    public void showAlertConnection() {
        Toast.makeText(inb.this, Config.alertConnection,
                Toast.LENGTH_LONG).show();

        setTitle(Config.nointernet);
        NoInternet no = new NoInternet();

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, no);
        fragmentTransaction.commit();
        /*inb.this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(inb.this);
                builder.setMessage(Config.alertConnection).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        setTitle(Config.nointernet);
                        NoInternet no = new NoInternet();

                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, no);
                        fragmentTransaction.commit();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });*/
    }

}