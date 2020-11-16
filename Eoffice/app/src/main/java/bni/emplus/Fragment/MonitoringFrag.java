package bni.emplus.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import bni.emplus.Adapter.SuratAdapter;
import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
import bni.emplus.Model.Surat;
import bni.emplus.PublicFunction;
import bni.emplus.UserSessionManager;
import bni.emplus.det;
import bni.emplus.inb;
import bni.emplus.log;
import bni.emplus.mCrypt;
import bni.emplus.R;

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

/**
 * Created by ZulfahPermataIlliyin on 6/27/2016.
 */
public class MonitoringFrag extends Fragment {
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    private List<Surat> suratList = new ArrayList<>();
    public SuratAdapter suratAdapter;

    private ListView listView;
    FloatingActionButton fab;

    public String npp, unitID,empID,roleID,statusid,sort="DESC";
    int pageSize = 10;
    int pageNumber = 1;
    int totalRows = 0;
    String kataKunci = "";

    String enunit,enempid,enrole,enpagesize,enpagenumber,enkk,enkey,ensort;

    UserSessionManager session;

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


    private static final String TAG_NO = "NoBaris";
    private static final String TAG_DISPID = "DispID";
    private static final String TAG_DOCID = "Emp_Doc_Id";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_CRTIMETGL = "CreatedTime_tgl";
    private static final String TAG_CRTIMEJAM = "CreatedTime_jam";
    private static final String TAG_DOCTYPE = "Doc_TypeName";
    private static final String TAG_DOCNO = "DocumentNo";
    private static final String TAG_DOCCODE = "KodeInbox";
    private static final String TAG_DOCMODE = "Doc_ModeName";
    private static final String TAG_FROM = "UnitName";
    private static final String TAG_TOTAL = "TotalRows";
    private static final String TAG_DOCREGID = "Emp_DocRegistration_Id";
    private static final String TAG_ISFAVORITE = "IsFavorit";
    private static final String TAG_TANGGALSURAT = "TanggalSurat";
    public boolean loadingMore = false;
    //public String loadingMore = "";

    PublicFunction pf = new PublicFunction();
    boolean DialogConnection=false;
    boolean isOrderDescending = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.monitoring_frag, container, false);

        //session
        session = new UserSessionManager(getActivity());
        if (session.checkLogin()) {
            Intent i = new Intent(getActivity(), log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            getActivity().finish();
        }

        session.createLoadingMore("false");
        HashMap<String, String> user = session.getUserNPP();
        HashMap<String, String> userRole = session.getUserRole();
        HashMap<String, String> LoadingMore = session.getLoadingMore();

        // get npp,empid,roleid,unitid
        npp = user.get(UserSessionManager.KEY_NAME);
        unitID = userRole.get(UserSessionManager.KEY_UNIT);
        roleID = userRole.get(UserSessionManager.KEY_ROLE);
        empID = user.get(UserSessionManager.KEY_EMPID);
        //loadingMore = LoadingMore.get(UserSessionManager.KEY_LOADINGMORE);
        statusid = userRole.get(UserSessionManager.KEY_STATID);

        Bundle args = getArguments();

        if(args!=null){
            kataKunci = args.getString("kataKunci");
        }

        fab = (FloatingActionButton) v.findViewById(R.id.fabSorting);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOrderDescending) {
                    fab.setImageResource(R.drawable.ic_sort_ascending);

                    isOrderDescending = false;
                    sort = "ASC";

                    pageSize = 10;
                    pageNumber = 1;
                    totalRows = 0;

                    suratList = new ArrayList<>();
                    ambilListMonitoring();

                    String fragment = "monitoring";
                    suratAdapter = new SuratAdapter(getActivity(),suratList,fragment);
                    listView.setAdapter(suratAdapter);
                }
                else
                {
                    fab.setImageResource(R.drawable.ic_sort_descending);

                    isOrderDescending = true;
                    sort = "DESC";

                    pageSize = 10;
                    pageNumber = 1;
                    totalRows = 0;

                    suratList = new ArrayList<>();
                    ambilListMonitoring();

                    String fragment = "monitoring";
                    suratAdapter = new SuratAdapter(getActivity(),suratList,fragment);
                    listView.setAdapter(suratAdapter);
                }
            }
        });

        listView = (ListView) v.findViewById(R.id.lvMonitor);
        String fragment = "monitoring";
        suratAdapter = new SuratAdapter(getActivity(),suratList,fragment);
        listView.setAdapter(suratAdapter);

        ambilListMonitoring();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String favorit = suratList.get(position).getFavourite();
                    showAlertHapusFavorit(position);
                return true;
            }
        });

        //Open a new activity when a list item is clicked.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = suratList.get(position).getDoccode();

                String docid = suratList.get(position).getEmpdocid();
                String typename = suratList.get(position).getDoctype();
                //Declaring Intent
                Intent pindah = new Intent(getActivity(), det.class);
                //Putting required data in intent
                pindah.putExtra("docid",docid);
                pindah.putExtra("typename",typename);
                pindah.putExtra("fragment","monitoring");
                startActivity(pindah);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int btn_initPosY=fab.getScrollY();
                if(scrollState == SCROLL_STATE_TOUCH_SCROLL){
                    fab.animate().cancel();
                    fab.animate().translationYBy(350);
                }else{
                    fab.animate().cancel();
                    fab.animate().translationY(btn_initPosY);
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int threshold = 1;
                int count = listView.getCount();
                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                //if ((lastInScreen == totalItemCount) && loadingMore.equals("false")) {
                    if (listView.getLastVisiblePosition() >= count - threshold && count < totalRows) {
                        pageNumber++;
                        cd=new ConnectionDetector(getContext());
                        isInternetPresent = cd.isConnectingToInternet();

                        if(isInternetPresent) {
                            mCrypt mCrypt = new mCrypt();
                            try {
                                enpagenumber = mCrypt.bytesToHex(mCrypt.encrypt(String.valueOf(pageNumber)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // Execute LoadMoreDataTask AsyncTask
                            ambilMonitor ambilMonitor = new ambilMonitor();
                            ambilMonitor.execute(enunit, enempid, enrole, enpagesize, enpagenumber, enkk, enkey,ensort);
                        }else{
                            if(DialogConnection==false) {
                                showAlert(Config.alertConnection);
                            }
                        }
                    }
                }
            }
        });
        return v;
    }

    public void ambilListMonitoring()
    {
        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent) {
            mCrypt krip = new mCrypt();
            try {
                enunit = krip.bytesToHex(krip.encrypt(unitID));
                enempid = krip.bytesToHex(krip.encrypt(empID));
                enrole = krip.bytesToHex(krip.encrypt(roleID));
                enpagesize = krip.bytesToHex(krip.encrypt(String.valueOf(pageSize)));
                enpagenumber = krip.bytesToHex(krip.encrypt(String.valueOf(pageNumber)));
                if(kataKunci.equals("")){
                    enkk = kataKunci;
                }else{
                    enkk = krip.bytesToHex(krip.encrypt(kataKunci));
                }
                enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
                ensort = krip.bytesToHex(krip.encrypt(sort));

            } catch (Exception e) {
                e.printStackTrace();
            }

            ambilMonitor ambilMonitor = new ambilMonitor();
            ambilMonitor.execute(enunit, enempid, enrole, enpagesize, enpagenumber, enkk, enkey, ensort);
        }else{
            showAlert(Config.alertConnection);
        }
    }

    public void showAlert(final String pesan) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // runs on UI thread
                Toast.makeText(getActivity(), pesan,
                        Toast.LENGTH_LONG).show();

                fab.setVisibility(View.GONE);

                getActivity().setTitle(Config.nointernet);
                NoInternet no = new NoInternet();

                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, no);
                fragmentTransaction.commit();
            }
        });

        /*getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        getActivity().setTitle(Config.nointernet);
                        NoInternet no = new NoInternet();

                        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, no);
                        fragmentTransaction.commit();
                    }
                });

                if(DialogConnection==false) {
                    DialogConnection=true;
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        });*/
    }

    public void showAlertHapusFavorit(final int pos){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(Config.alertHapusFavorit).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /*if (suratList.get(pos).getDoccode().equals("U"))
                            suratList.get(pos).setIconSurat(R.mipmap.ic_urinv);
                            //icon.setImageResource(R.mipmap.ic_ur);
                        else if (suratList.get(pos).getDoccode().equals("D"))
                            suratList.get(pos).setIconSurat(R.mipmap.ic_disinv);
                        //icon.setImageResource(R.mipmap.ic_dis);*/

                        String fav = "2";
                        ManageFavorit(pos, fav);
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

    private void ManageFavorit(int pos, String Flag ){
        String empdocid="",ennpp="",enunitid="",enroleid="",enstat="",enflag="",enkey="",empdocregid="";

        cd=new ConnectionDetector(getActivity().getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent){
            mCrypt mCrypt = new mCrypt();
            try {
                empdocid = mCrypt.bytesToHex(mCrypt.encrypt(suratList.get(pos).getEmpdocid()));
                empdocregid = mCrypt.bytesToHex(mCrypt.encrypt(suratList.get(pos).getDocReg()));
                ennpp = mCrypt.bytesToHex(mCrypt.encrypt(npp));
                enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitID));
                enroleid = mCrypt.bytesToHex(mCrypt.encrypt(roleID));
                enstat = mCrypt.bytesToHex(mCrypt.encrypt(statusid));
                enflag = mCrypt.bytesToHex(mCrypt.encrypt(Flag));
                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Favorit fav = new Favorit();
            fav.execute(empdocid,ennpp,enunitid,enroleid,enstat,enflag,empdocregid,enkey,String.valueOf(pos));

        }else{
            pf.showAlertConnection(getActivity());

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
            pos = Integer.valueOf(params[8]);

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
                //pf.showAlertTimeOut(getActivity());
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(getActivity());
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(getActivity());
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            pf.showAlertTimeOut(getActivity());
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
                        showAlertFav(keterangan, kode, pos);
                    } else {
                        showAlertFav(keterangan,kode,pos);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showAlertFav(final String message, final String Kode, final int pos){
        Toast.makeText(getActivity(), message,
                Toast.LENGTH_LONG).show();
        if (Kode.equals("1")) {
            suratList.get(pos).setFavourite("1");
            if (suratList.get(pos).getDoccode().equals("U"))
                suratList.get(pos).setIconSurat(R.mipmap.ic_ur);
            else if (suratList.get(pos).getDoccode().equals("D"))
                suratList.get(pos).setIconSurat(R.mipmap.ic_dis);

            listView.setAdapter(suratAdapter);

            HashMap<String, String> total = ((inb) getActivity()).total;
            int TotalFav = Integer.valueOf(total.get(TAG_TOTFAV)) + 1;
            ((inb) getActivity()).total.remove(TAG_TOTFAV);
            ((inb) getActivity()).total.put(TAG_TOTFAV, String.valueOf(TotalFav));

            ((inb) getActivity()).initializeCounterDrawer(total.get(TAG_TOTDIS), total.get(TAG_TOTUR), total.get(TAG_TOTAPP), total.get(TAG_TOTSENT), total.get(TAG_TOTFAV), total.get(TAG_TOTTRACK), total.get(TAG_OFFLINE), total.get(TAG_ONLINE), total.get(TAG_LIST));
        } else if (Kode.equals("0")) {
            suratList.get(pos).setFavourite("0");
            HashMap<String, String> total = ((inb) getActivity()).total;
            int TotalFav = Integer.valueOf(total.get(TAG_TOTFAV)) - 1;
            //totalRows = TotalFav;
            ((inb) getActivity()).total.remove(TAG_TOTFAV);
            ((inb) getActivity()).total.put(TAG_TOTFAV, String.valueOf(TotalFav));

            ((inb) getActivity()).initializeCounterDrawer(total.get(TAG_TOTDIS), total.get(TAG_TOTUR), total.get(TAG_TOTAPP), total.get(TAG_TOTSENT), total.get(TAG_TOTFAV), total.get(TAG_TOTTRACK), total.get(TAG_OFFLINE), total.get(TAG_ONLINE), total.get(TAG_LIST));

            if(TotalFav > 0)
            {
                totalRows = TotalFav;
                suratList.clear();
                cd = new ConnectionDetector(getContext());
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    pageSize = 10;
                    pageNumber = 1;
                    mCrypt krip = new mCrypt();
                    try {
                        enunit = krip.bytesToHex(krip.encrypt(unitID));
                        enempid = krip.bytesToHex(krip.encrypt(empID));
                        enrole = krip.bytesToHex(krip.encrypt(roleID));
                        enpagesize = krip.bytesToHex(krip.encrypt(String.valueOf(pageSize)));
                        enpagenumber = krip.bytesToHex(krip.encrypt(String.valueOf(pageNumber)));
                        if (kataKunci.equals("")) {
                            enkk = kataKunci;
                        } else {
                            enkk = krip.bytesToHex(krip.encrypt(kataKunci));
                        }
                        enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
                        ensort = krip.bytesToHex(krip.encrypt(sort));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ambilMonitor ambilMonitor = new ambilMonitor();
                    ambilMonitor.execute(enunit, enempid, enrole, enpagesize, enpagenumber, enkk, enkey, ensort);
                } else {
                    showAlert(Config.alertConnection);
                }

            }
            else
            {
                totalRows = TotalFav;
                suratList.remove(suratList.get(pos));
                listView.setAdapter(suratAdapter);

            }

        }
    }
    class ambilMonitor extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {


            loadingMore = true;
            //session.createLoadingMore("true");

            String unitid = params[0];
            String empid = params[1];
            String roleid = params[2];
            String pagesize = params[3];
            String pagenumber=params[4];
            String katakunci = params[5];
            String key = params[6];
            String sort = params[7];

            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PAGESIZE, pagesize));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PAGENUMBER, pagenumber));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_KATAKUNCI, katakunci));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VARIABLETAMBAHAN, sort));
            try {

                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost httpPost = new HttpPost(Config.urlLanding + "GetListMonitoringDisposisi.php");
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetListFavorite.php");
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
                //showAlert(Config.alertTimeOut);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //showAlert(Config.alertTimeOut);
                cancel(true);
            }  catch (IOException e) {
                e.printStackTrace();
                //showAlert(Config.alertTimeOut);
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
            showAlert(Config.alertTimeOut);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String baris = e.getString(TAG_NO);
                        String dispid = e.getString(TAG_DISPID);
                        String empdocid = e.getString(TAG_DOCID);
                        String title = e.getString(TAG_TITLE);
                        String date = e.getString(TAG_CRTIMETGL);
                        String jam = e.getString(TAG_CRTIMEJAM);
                        String type = e.getString(TAG_DOCTYPE);
                        String no = e.getString(TAG_DOCNO);
                        String mode = e.getString(TAG_DOCMODE);
                        String code = e.getString(TAG_DOCCODE);
                        String from = e.getString(TAG_FROM);
                        String tot = e.getString(TAG_TOTAL);
                        String docregid = e.getString(TAG_DOCREGID);
                        String isfavorite = e.getString(TAG_ISFAVORITE);
                        String tanggalAsliSurat = e.getString(TAG_TANGGALSURAT);

                        totalRows = Integer.parseInt(tot);

                        Surat surat = new Surat();
                        surat.setBaris(baris);
                        surat.setDispId(dispid);
                        surat.setEmpdocid(empdocid);
                        surat.setJudulSurat(title);
                        surat.setTanggalSurat(date);
                        surat.setJamSurat(jam);
                        surat.setDoctype(type);
                        surat.setDocMode(mode);
                        surat.setNomorSurat(no);
                        surat.setDoccode(code);
                        surat.setDivisiSurat(from);
                        surat.setTotal(tot);
                        surat.setDocReg(docregid);
                        surat.setFavourite(isfavorite);
                        surat.setTanggalAsliSurat(tanggalAsliSurat);

                        if(code.equals("U")){
                            surat.setIconSurat(R.mipmap.ic_ur);
                        }else if(code.equals("A")){
                            surat.setIconSurat(R.mipmap.ic_app);
                        }else{
                            surat.setIconSurat(R.mipmap.ic_dis);
                        }

                        if(isfavorite.equals("1")){
                            surat.setIconFav(R.drawable.ic_star);
                        }
                        suratList.add(surat);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            suratAdapter.notifyDataSetChanged();
            loadingMore = false;
            //session.createLoadingMore("false");
            if(suratList.isEmpty()) {
                final FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.fragment_C);
                frameLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        }
    }
}

