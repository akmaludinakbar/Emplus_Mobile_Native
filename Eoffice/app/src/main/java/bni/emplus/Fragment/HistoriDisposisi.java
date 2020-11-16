package bni.emplus.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import bni.emplus.Adapter.modelHistoriAdapter;
import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
import bni.emplus.Model.modelHistori;
import bni.emplus.PublicFunction;
import bni.emplus.UserSessionManager;
import bni.emplus.det;
import bni.emplus.dis;
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
 * Created by ZulfahPermataIlliyin on 6/30/2016.
 */
public class HistoriDisposisi extends Fragment {
    private List<modelHistori> historiList = new ArrayList<>();
    private modelHistoriAdapter historiAdapter;
    private ListView listView;

    private static final String TAG_ID = "id";
    private static final String TAG_UNITDISPOSISI = "UnitDispoName";
    private static final String TAG_EMPDISPOSISI ="EmpDispoName";
    private static final String TAG_NAMEDISPOSISI = "PendisposisiName";
    private static final String TAG_PENERIMA = "ListPenerima";
    private static final String TAG_CONTENT = "Content";
    private static final String TAG_ISMONITOR = "IsMonitored";
    private static final String TAG_ISHARDCOPY = "IsHardcopy";
    private static final String TAG_STATUSID = "Status_Id";
    private static final String TAG_CRTIMETGL = "CreatedTime_tgl";
    private static final String TAG_CRTIMEJAM = "CreatedTime_jam";
    private static final String TAG_DISPTIMETGL = "Disp_Time_tgl";
    private static final String TAG_DISPTIMEJAM = "Disp_Time_jam";
    private static final String TAG_DISPDURATION = "DurationDisp";
    private static final String TAG_DURATION = "Duration";
    private static final String TAG_DISPBYADMIN = "Disp_ByAdmin";

    String docid,unitid,roleid,endocid,enunitid,enroleid,enkey,fragment;
    UserSessionManager session;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    PublicFunction pf = new PublicFunction();
    Dialog info;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.histori_komen,container,false);

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

        // get roleid,unitid
        unitid = userRole.get(UserSessionManager.KEY_UNIT);
        roleid = userRole.get(UserSessionManager.KEY_ROLE);

        Bundle args = this.getArguments();
        if(args!=null){
            docid = args.getString("docid");
            fragment = args.getString("fragment");
        }

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(fragment.equals("urgent")||fragment.equals("disposisi")||fragment.equals("hardcopy")||fragment.equals("online")) {
            if (!((dis) getActivity()).historidisposisi.isEmpty()) {
                historiList = ((dis) getActivity()).historidisposisi;
            } else {
                if (isInternetPresent) {
                    mCrypt krip = new mCrypt();
                    try {
                        endocid = krip.bytesToHex(krip.encrypt(docid));
                        enunitid = krip.bytesToHex(krip.encrypt(unitid));
                        enroleid = krip.bytesToHex(krip.encrypt(roleid));
                        enkey = krip.bytesToHex(krip.encrypt(Config.api_key));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ambilHistoriDisp ambil = new ambilHistoriDisp();
                    ambil.execute(endocid, enunitid, enroleid, enkey);
                } else {
                    showAlertConnection(Config.alertConnection);
                }
            }
        }else{
            if (!((det) getActivity()).historidetail.isEmpty()) {
                historiList = ((det) getActivity()).historidetail;
            } else {
                if (isInternetPresent) {
                    mCrypt krip = new mCrypt();
                    try {
                        endocid = krip.bytesToHex(krip.encrypt(docid));
                        enunitid = krip.bytesToHex(krip.encrypt(unitid));
                        enroleid = krip.bytesToHex(krip.encrypt(roleid));
                        enkey = krip.bytesToHex(krip.encrypt(Config.api_key));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ambilHistoriDisp ambil = new ambilHistoriDisp();
                    ambil.execute(endocid, enunitid, enroleid, enkey);
                } else {
                    showAlertConnection(Config.alertConnection);
                }
            }
        }

        listView = (ListView) v.findViewById(R.id.lvkomen);
        historiAdapter = new modelHistoriAdapter(getActivity(),historiList);
        listView.setAdapter(historiAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dialog(position);
            }
        });

        return v;
    }

    public void dialog(int position) {
        info = new Dialog(getActivity(),R.style.CustomDialog);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        double screenInches = pf.getScreenInches(getActivity());
        if (screenInches <= 4.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi);
        } else if (screenInches > 4.0 && screenInches <= 5.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi);
        } else if (screenInches > 5.0 && screenInches <= 6.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi);
        } else if (screenInches > 6.0 && screenInches <= 7.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi_tujuh);
        } else if (screenInches > 7.0 && screenInches <= 8.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi_tujuh);
        } else if (screenInches > 8.0 && screenInches <= 9.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi_tujuh);
        } else if (screenInches > 9.0 && screenInches <= 10.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi_tujuh);
        } else if (screenInches > 10.0 && screenInches <= 11.0) {
            info.setContentView(R.layout.dialog_informasi_disposisi_tujuh);
        }

        TextView tvPendisposisi = (TextView) info.findViewById(R.id.tvisiPengirimInfo);
        TextView tvPenerima = (TextView) info.findViewById(R.id.tvIsiPenerimaInfo);
        TextView tvIsi = (TextView) info.findViewById(R.id.tvIsiDisposisiInfo);
        TextView tvTanggal = (TextView) info.findViewById(R.id.tvisiTanggalInfo);
        TextView tvSLA = (TextView) info.findViewById(R.id.tvIsiSLAInfo);

        tvPendisposisi.setText(historiList.get(position).getNamaPendisposisi());
        tvPenerima.setText(historiList.get(position).getListPenerima().replace("#", ", "));
        tvIsi.setText(historiList.get(position).getContent());
        tvTanggal.setText(historiList.get(position).getDisptgl() + " " + historiList.get(position).getDispjam());
        tvSLA.setText(historiList.get(position).getDispKomenDuration());

        info.show();
    }

    public void showAlertConnection(final String pesan) {
        Toast.makeText(getActivity(), pesan,
                Toast.LENGTH_LONG).show();

        getActivity().setTitle(Config.nointernet);
        if (fragment.equals("urgent") || fragment.equals("disposisi")) {
            ((dis) getActivity()).adapter = null;
            ((dis) getActivity()).pager = null;
            ((dis) getActivity()).tabs = null;
        } else {
            ((det) getActivity()).adapter = null;
            ((det) getActivity()).pager = null;
            ((det) getActivity()).tabs = null;
        }
    }

    class ambilHistoriDisp extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String responce;

            String docid = params[0];
            String unitid = params[1];
            String roleid = params[2];
            String key = params[3];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, docid));

            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "ListHistoryDisposisi.php");
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
                //showAlertConnection(Config.alertTimeOut);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //showAlertConnection(Config.alertTimeOut);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //showAlertConnection(Config.alertTimeOut);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            showAlertConnection(Config.alertTimeOut);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String id = e.getString(TAG_ID);
                        String unitdisp = e.getString(TAG_UNITDISPOSISI);
                        String empdisp = e.getString(TAG_EMPDISPOSISI);
                        String namadisp = e.getString(TAG_NAMEDISPOSISI);
                        String penerima = e.getString(TAG_PENERIMA);
                        String content = e.getString(TAG_CONTENT);
                        String ismonitor = e.getString(TAG_ISMONITOR);
                        String ishardcpy = e.getString(TAG_ISHARDCOPY);
                        String statid = e.getString(TAG_STATUSID);
                        String crtgl = e.getString(TAG_CRTIMETGL);
                        String crjam = e.getString(TAG_CRTIMEJAM);
                        String distgl = e.getString(TAG_DISPTIMETGL);
                        String disjam = e.getString(TAG_DISPTIMEJAM);
                        String dispduration = e.getString(TAG_DISPDURATION);
                        String duration = e.getString(TAG_DURATION);
                        String dispbyadmin = e.getString(TAG_DISPBYADMIN);

                        modelHistori histori = new modelHistori();
                        histori.setGambarHistori(R.mipmap.ic_komen);
                        histori.setId(id);
                        histori.setUnitPendisposisi(unitdisp);
                        histori.setEmpPendisposisi(empdisp);
                        histori.setNamaPendisposisi(namadisp);
                        histori.setListPenerima(penerima);
                        histori.setContent(content);
                        histori.setIsMonitor(ismonitor);
                        histori.setStatId(statid);
                        histori.setIsHardcopy(ishardcpy);
                        histori.setCrtgl(crtgl);
                        histori.setCrjam(crjam);
                        histori.setDispjam(disjam);
                        histori.setDisptgl(distgl);
                        histori.setDispKomenDuration(dispduration);
                        histori.setKomenDuration(duration);
                        histori.setDisp_ByAdmin(dispbyadmin);

                        historiList.add(histori);
                        if(fragment.equals("urgent")||fragment.equals("disposisi")||fragment.equals("hardcopy")||fragment.equals("online")) {
                            ((dis) getActivity()).historidisposisi = historiList;
                        }else{
                            ((det) getActivity()).historidetail = historiList;
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            listView.setAdapter(historiAdapter);
        }
    }

}
