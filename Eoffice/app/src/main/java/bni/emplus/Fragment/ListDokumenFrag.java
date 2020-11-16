package bni.emplus.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
 * Created by ZulfahPermataIlliyin on 9/7/2016.
 */
public class ListDokumenFrag extends Fragment {
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    private List<Surat> suratList = new ArrayList<>();
    private SuratAdapter suratAdapter;

    private ListView listView;
    FloatingActionButton fab;

    public String npp, unitID,empID,roleID,sort="DESC";
    int pageSize = 10;
    int pageNumber = 1;
    int totalRows = 0;
    String kataKunci = "";

    String enunit,enempid,enrole,enpagesize,enpagenumber,enkk,enkey,ensort;

    UserSessionManager session;
    PublicFunction pf = new PublicFunction();

    private static final String TAG_NO = "NoBaris";
    private static final String TAG_BOXID = "id";
    private static final String TAG_DOCID = "Doc_Id";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_CRTIMETGL = "CreatedTime_tgl";
    private static final String TAG_CRTIMEJAM = "CreatedTime_jam";
    private static final String TAG_DOCTYPE = "Type_Name";
    private static final String TAG_DOCNO = "DocumentNo";
    private static final String TAG_DOCCODE = "KodeInbox";
    private static final String TAG_PRIOR = "Priority";
    private static final String TAG_FROM = "Unit_Name";
    private static final String TAG_TOTAL = "TotalRows";
    private static final String TAG_TANGGALSURAT = "TanggalSurat";
    private static final String TAG_DURASI = "SLA_realisasi";
    private static final String TAG_DURASIDISP = "SLA_realisasiDisplay";
    private static final String TAG_STATUSWARNA = "StatusWarna";
    boolean loadingMore = false;

    boolean DialogConnection=false;
    boolean isOrderDescending = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.listdokumen_frag, container, false);

        //session
        session = new UserSessionManager(getActivity());
        if (session.checkLogin()) {
            Intent i = new Intent(getActivity(), log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            getActivity().finish();
        }

        HashMap<String, String> user = session.getUserNPP();
        HashMap<String, String> userRole = session.getUserRole();

        // get npp,empid,roleid,unitid
        npp = user.get(UserSessionManager.KEY_NAME);
        unitID = userRole.get(UserSessionManager.KEY_UNIT);
        roleID = userRole.get(UserSessionManager.KEY_ROLE);
        empID = user.get(UserSessionManager.KEY_EMPID);

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
                    ambilListDokumen();

                    String fragment = "list";
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
                    ambilListDokumen();

                    String fragment = "list";
                    suratAdapter = new SuratAdapter(getActivity(),suratList,fragment);
                    listView.setAdapter(suratAdapter);
                }
            }
        });

        listView = (ListView) v.findViewById(R.id.lvListDokumen);
        String fragment = "list";
        suratAdapter = new SuratAdapter(getActivity(),suratList,fragment);

        listView.setAdapter(suratAdapter);

        ambilListDokumen();

        //Open a new activity when a list item is clicked.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String docid = suratList.get(position).getEmpdocid();
                String typename = suratList.get(position).getDoctype();

                //Declaring Intent
                Intent pindah = new Intent(getActivity(), det.class);
                //Putting required data in intent
                pindah.putExtra("docid",docid);
                pindah.putExtra("typename",typename);
                pindah.putExtra("fragment","list");
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
                            ambilListDok ambil = new ambilListDok();
                            ambil.execute(enunit, enempid, enrole, enpagesize, enpagenumber, enkk, enkey);
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

    public void ambilListDokumen()
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

            ambilListDok ambil = new ambilListDok();
            ambil.execute(enunit, enempid, enrole, enpagesize, enpagenumber, enkk, enkey,ensort);
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

    class ambilListDok extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            loadingMore = true;

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
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetListDokumen.php");
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
            } catch (IOException e) {
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
                        String boxid = e.getString(TAG_BOXID);
                        String empdocid = e.getString(TAG_DOCID);
                        String title = e.getString(TAG_TITLE);
                        String date = e.getString(TAG_CRTIMETGL);
                        String jam = e.getString(TAG_CRTIMEJAM);
                        String type = e.getString(TAG_DOCTYPE);
                        String no = e.getString(TAG_DOCNO);
                        String code = e.getString(TAG_DOCCODE);
                        String prior = e.getString(TAG_PRIOR);
                        String from = e.getString(TAG_FROM);
                        String tot = e.getString(TAG_TOTAL);
                        String tanggalAsliSurat = e.getString(TAG_TANGGALSURAT);
                        String durasi = e.getString(TAG_DURASI);
                        String durasidisp = e.getString(TAG_DURASIDISP);
                        String statuswarna = e.getString(TAG_STATUSWARNA);

                        totalRows = Integer.parseInt(tot);

                        Surat surat = new Surat();
                        surat.setBaris(baris);
                        surat.setBoxId(boxid);
                        surat.setEmpdocid(empdocid);
                        surat.setJudulSurat(title);
                        surat.setTanggalSurat(date);
                        surat.setJamSurat(jam);
                        surat.setDoctype(type);
                        surat.setNomorSurat(no);
                        surat.setDoccode(code);
                        surat.setPrior(prior);
                        surat.setDivisiSurat(from);
                        surat.setTotal(tot);
                        surat.setTanggalAsliSurat(tanggalAsliSurat);
                        surat.setDurasi(durasi);
                        surat.setDurasiDisp(durasidisp);

                        if(code.equals("U")){
                            surat.setIconSurat(R.mipmap.ic_ur);
                        }else if(code.equals("A")){
                            surat.setIconSurat(R.mipmap.ic_app);
                        }else if(code.equals("D")) {
                            surat.setIconSurat(R.mipmap.ic_dis);
                        }

                        if(statuswarna.equals("0"))
                        {
                            surat.setIconStatus(R.drawable.ic_circle_gray);
                        }
                        else if(statuswarna.equals("1"))
                        {
                            surat.setIconStatus(R.drawable.ic_circle_red);
                        }
                        else if(statuswarna.equals("2"))
                        {
                            surat.setIconStatus(R.drawable.ic_circle_green);
                        }

                        suratList.add(surat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            suratAdapter.notifyDataSetChanged();
            loadingMore = false;
            if(suratList.isEmpty()) {
                final FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.fragment_C);
                frameLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        }
    }
}
