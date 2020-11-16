package bni.emplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import bni.emplus.Adapter.ViewPagerAdapterApprove;
import bni.emplus.Model.modelKomen;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class app extends AppCompatActivity {

    public boolean dialogConnection = false;

    // Declaring Your View and Variables
    private static final int REQUEST_PATH = 1;
    public String npp,unitid,empid,roleid,docid,doctypename,statid,erid,docstatus;

    PublicFunction pf = new PublicFunction();

    UserSessionManager session;

    public ViewPager pager;
    public ViewPagerAdapterApprove adapter;
    public boolean statOpenPDF = false;
    public slid tabs;
    String fragment;
    CharSequence Titles[] = new CharSequence[]{};
    int Numboftabs =3;
    String type;

    Button btTerima, btTolak;

    public HashMap<String,String> isiapprove = new HashMap<String,String>();
    public List<modelLampiran> fileapprove = new ArrayList<>();
    public List<modelKomen> historiapprove = new ArrayList<>();

    String statusApprove;
    Dialog approve;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    String endocid,enkey,enempid,enroleid,enunitid,enstatid,ennpp,enkomen,enaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
        }

        setTitle("Surat persetujuan");

        //session
        session = new UserSessionManager(app.this);
        if (session.checkLogin()) {
            Intent i = new Intent(app.this, log.class);
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
            fragment = (String) args.get("fragment");
            docstatus = (String) args.get("docstatus");
        }

        if(doctypename.equals("Memo")){
            Titles = new CharSequence[]{"Memo", "Lampiran", "Riwayat"};
            type = "Memo";
        }else if(doctypename.equals("Nota Intern")){
            Titles = new CharSequence[]{"Notin", "Lampiran", "Riwayat"};
            type = "Notin";
        }

        btTerima = (Button) findViewById(R.id.btTerima);
        btTolak = (Button) findViewById(R.id.btTolak);
        LinearLayout btapprove = (LinearLayout) findViewById(R.id.btApproval);


        if(docstatus.equals("Reject"))
        if(docstatus.equals("Reject"))
        {
            btapprove.setVisibility(View.GONE);
        }
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapterApprove(getSupportFragmentManager(),Titles,Numboftabs,docid,fragment,type,docstatus);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pagerapp);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (slid) findViewById(R.id.tabsapp);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        btTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete del = new Delete();
                del.delete();
                statusApprove = "2";
                dialog();
            }
        });

        btTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete del = new Delete();
                del.delete();
                statusApprove = "3";
                dialog();
            }
        });

    }

    public void dialog(){
        approve = new Dialog(app.this);
        approve.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        approve.setContentView(R.layout.activity_add_notes);
        final EditText editCatatan;
        final Button btTambahCatatan;

        editCatatan = (EditText) approve.findViewById(R.id.editCatatanApp);

        btTambahCatatan = (Button) approve.findViewById(R.id.btTambahCatatan);

        btTambahCatatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd=new ConnectionDetector(app.this);
                isInternetPresent = cd.isConnectingToInternet();

                if(isInternetPresent) {
                    mCrypt mCrypt = new mCrypt();
                    try {
                        enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                        enempid = mCrypt.bytesToHex(mCrypt.encrypt(empid));
                        enroleid = mCrypt.bytesToHex(mCrypt.encrypt(roleid));
                        enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitid));
                        enstatid = mCrypt.bytesToHex(mCrypt.encrypt(statid));
                        ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
                        endocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
                        enaction = mCrypt.bytesToHex(mCrypt.encrypt(statusApprove));
                        if (editCatatan.getText().toString().isEmpty() || editCatatan.getText().toString().equals("")) {
                            enkomen = editCatatan.getText().toString();
                        } else {
                            enkomen = mCrypt.bytesToHex(mCrypt.encrypt(editCatatan.getText().toString()));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ProsesApprove pros = new ProsesApprove();
                    pros.execute(enempid, enroleid, enunitid, enstatid, ennpp, endocid, enkomen, enaction, enkey);
                }else{
                    showAlertConnection(Config.alertConnection,"proses");
                }
            }
        });

        approve.show();
    }

    class ProsesApprove extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(app.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;

            String empid = params[0];
            String roleid = params[1];
            String unitid = params[2];
            String statid = params[3];
            String npp = params[4];
            String docid = params[5];
            String komen = params[6];
            String action = params[7];
            String key = params[8];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_STATID, statid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, npp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, docid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_KOMEN, komen));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ACTION, action));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "Approval"+type+".php");
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
                //pf.showAlertTimeOut(app.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(app.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(app.this);
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
            pf.showAlertTimeOut(app.this);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            loadingDialog.dismiss();
            if (result != null) {
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");

                    if(kode.equals("1")&&statusApprove.equals("2")){
                        String title = "Success";
                        //showAlert("Approved Success",title);
                        showAlert(keterangan,title);
                    }else if(kode.equals("1")&&statusApprove.equals("3")){
                        String title = "Success";
                        //showAlert("Rejected Success",title);
                        showAlert(keterangan,title);
                    }else{
                        String title = "Error";
                        showAlert(keterangan,title);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void showAlert(final String pesan, final String title) {
        Toast.makeText(app.this, pesan,
                Toast.LENGTH_LONG).show();
        if (title.equals("Success")) {
            Intent pindah = new Intent(app.this, inb.class);
            pindah.putExtra("fragment", fragment);
            startActivity(pindah);
        }
        /*this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(app.this);
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });*/
    }

    public void showAlertConnection(final String pesan, final String status) {
        Toast.makeText(app.this, pesan,
                Toast.LENGTH_LONG).show();
        approve.dismiss();
        /*this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(app.this);
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        approve.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });*/
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
                Intent i = new Intent(app.this, inb.class);
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
    public void onBackPressed() {
        finish();
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