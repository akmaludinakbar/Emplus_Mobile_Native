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
import android.widget.ListView;
import android.widget.Toast;

import bni.emplus.Adapter.SentAdapter;
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
 * Created by ZulfahPermataIlliyin on 6/27/2016.
 */
public class SentFrag extends Fragment {
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    private List<Surat> suratList = new ArrayList<>();
    private SentAdapter sentAdapter;

    private ListView listView;
    FloatingActionButton fab;

    UserSessionManager session;
    String npp,unitID,empID,roleID;
    int pageSize = 10;
    int pageNumber = 1;
    int totalRows = 0;
    String kataKunci = "";
    boolean loadingMore = false;

    String enunit,enempid,enrole,enpagesize,enpagenumber,enkk,enkey;

    private static final String TAG_NO = "NoBaris";
    private static final String TAG_DOCID = "Id";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_DOCTYPE = "DocType_Name";
    private static final String TAG_DOCFLOW = "DocFlow_Name";
    private static final String TAG_CRTIMETGL = "CreatedTime_Tgl";
    private static final String TAG_CRTIMEJAM = "CreatedTime_jam";
    private static final String TAG_TOTAL = "TotalRows";

    boolean DialogConnection=false;
    PublicFunction pf = new PublicFunction();
    boolean isOrderDescending = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.sent_frag, container, false);

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
                }
                else
                {
                    fab.setImageResource(R.drawable.ic_sort_descending);
                    isOrderDescending = true;
                }
            }
        });

        listView = (ListView) v.findViewById(R.id.lvSent);
        sentAdapter = new SentAdapter(getActivity(),suratList);

        listView.setAdapter(sentAdapter);

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
                if (kataKunci.equals("")) {
                    enkk = kataKunci;
                } else {
                    enkk = krip.bytesToHex(krip.encrypt(kataKunci));
                }
                enkey = krip.bytesToHex(krip.encrypt(Config.api_key));

            } catch (Exception e) {
                e.printStackTrace();
            }

            ambilSent ambilSent = new ambilSent();
            ambilSent.execute(enunit, enempid, enrole, String.valueOf(enpagesize), String.valueOf(enpagenumber), enkk, enkey);
        }else{
            showAlert(Config.alertConnection);
        }
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
                pindah.putExtra("fragment","sent");
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
                            ambilSent ambilSent = new ambilSent();
                            ambilSent.execute(enunit, enempid, enrole, enpagesize, enpagenumber, enkk, enkey);
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
    }

    class ambilSent extends AsyncTask<String, Void, JSONArray> {

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
            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PAGESIZE, pagesize));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PAGENUMBER, pagenumber));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_KATAKUNCI, katakunci));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetListSentItem.php");
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
                        String docid = e.getString(TAG_DOCID);
                        String title = e.getString(TAG_TITLE);
                        String type = e.getString(TAG_DOCTYPE);
                        String flow = e.getString(TAG_DOCFLOW);
                        String date = e.getString(TAG_CRTIMETGL);
                        String jam = e.getString(TAG_CRTIMEJAM);
                        String tot = e.getString(TAG_TOTAL);

                        totalRows = Integer.parseInt(tot);

                        Surat surat = new Surat();
                        surat.setBaris(baris);
                        surat.setEmpdocid(docid);
                        surat.setJudulSurat(title);
                        surat.setDoctype(type);
                        surat.setFlowSurat(flow);
                        surat.setTanggalSurat(date);
                        surat.setJamSurat(jam);
                        surat.setTotal(tot);

                        suratList.add(surat);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            sentAdapter.notifyDataSetChanged();
            loadingMore = false;
        }
    }
}

