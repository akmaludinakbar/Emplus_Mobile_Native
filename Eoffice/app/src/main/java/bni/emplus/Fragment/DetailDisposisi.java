package bni.emplus.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
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
public class DetailDisposisi extends Fragment {
    HashMap<String,String> surat = new HashMap<String, String>();

    Button btDisposisi, btCloseDisposisi;
    LinearLayout btclos,btdis, btback;
    String docid = "",doctype = "",boxid = "",npp = "",empID = "",unitID = "",roleID = "",modefirstdoc ="",mode = "", crtime = "",fragment = "";
    String enunitid,enempid,endocregid,entitle,endocno,enaddress,entglsurat,entglregister,ennotes,enkey,endocid,enboxid,enroleid,enkomen,enemproleid;
    String regno=null;
    String docregid = null;
    UserSessionManager session;
    TextView typename,tanggal,nomor,kepada,dari,perihal,lampiran,isi,JenisDokumen,jenisdokumen;

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
    private static final String TAG_JENISDOKUMEN = "JenisDokumen";

    int layout;
    View v;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    PublicFunction pf = new PublicFunction();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = R.layout.detail_disposisi;
        v =inflater.inflate(layout,container,false);

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

        Bundle args = this.getArguments();
        if(args!=null){
            docid = args.getString("docid");
            doctype = args.getString("doctype");
            boxid = args.getString("boxid");
            docregid = args.getString("docregid");
            modefirstdoc = args.getString("modefirstdoc");
            regno = args.getString("regno");
            mode = args.getString("mode");
            crtime = args.getString("tanggal");
            fragment = args.getString("fragment");
        }

        typename = (TextView)v.findViewById(R.id.tvMemo);
        tanggal = (TextView)v.findViewById(R.id.tvTanggal);
        nomor = (TextView)v.findViewById(R.id.tvnomorsurat);
        kepada = (TextView)v.findViewById(R.id.tvisikepada);
        dari = (TextView)v.findViewById(R.id.tvisidari);
        perihal = (TextView)v.findViewById(R.id.tvisiperihal);
        lampiran = (TextView)v.findViewById(R.id.tvisilampiran);
        isi = (TextView)v.findViewById(R.id.tvIsiDisposisi);
        jenisdokumen = (TextView)v.findViewById(R.id.tvisijenisdok);

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(!((dis)getActivity()).isidisposisi.isEmpty()){
            surat = ((dis)getActivity()).isidisposisi;
            isiTextView(doctype, surat.get(TAG_TempatTanggal).toString(), surat.get(TAG_NO).toString(), surat.get(TAG_TO).toString(),
                    surat.get(TAG_FROM).toString(), surat.get(TAG_TITLE).toString(), surat.get(TAG_LAMPIRAN).toString(),
                    surat.get(TAG_ISI).toString(), surat.get(TAG_ISFAVORIT).toString(), surat.get(TAG_JENISDOKUMEN).toString());
        }
        else{
            if(isInternetPresent) {
                ambilDetailDisposisi();
            }else{
                showAlertConnection(Config.alertConnection);
            }
        }

        return v;
    }

    public void ambilDetailDisposisi(){
        mCrypt mCrypt = new mCrypt();
        try {
            endocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
            enemproleid = mCrypt.bytesToHex(mCrypt.encrypt(empID+"|"+roleID));
            enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitID));
            endocregid = mCrypt.bytesToHex(mCrypt.encrypt(docregid));
            enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilDetailDisposisi ambil = new ambilDetailDisposisi();
        ambil.execute(endocid, enkey, enemproleid, enunitid, endocregid);
    }

    class ambilDetailDisposisi extends AsyncTask<String, Void, JSONArray> {

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
            String key = params[1];
            String empidroleid = params[2];
            String unitid = params[3];
            String docregid = params[4];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, docid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PERSEDIAAN, empidroleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VARIABLETAMBAHAN, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TAMBAHAN, docregid));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetDetailSurat.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //TimeOut
                HttpParams httpParameters = httpPost.getParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 30000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 30000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpConnectionParams.setTcpNoDelay(httpParameters, true);

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
                        String baris = e.getString(TAG_NO);
                        String melalui = e.getString(TAG_MELALUI);
                        String to = e.getString(TAG_TO);
                        String cc = e.getString(TAG_CC);
                        String bcc = e.getString(TAG_BCC);
                        String from = e.getString(TAG_FROM);
                        String lampiran = e.getString(TAG_LAMPIRAN);
                        String isi = e.getString(TAG_ISI);
                        String tempattanggal = e.getString(TAG_TempatTanggal);
                        String title = e.getString(TAG_TITLE);
                        String tanggal = e.getString(TAG_TANGGAL);
                        String jenisdokumen = e.getString(TAG_JENISDOKUMEN);
                        String IsFavorit = "";

                        try
                        {
                            if(e.getString(TAG_ISFAVORIT) != null)
                            {
                                IsFavorit = e.getString(TAG_ISFAVORIT);
                            }
                        }
                        catch(Exception ex) {}

                        surat.put(TAG_ID,id);
                        surat.put(TAG_NO,baris);
                        surat.put(TAG_MELALUI,melalui);
                        surat.put(TAG_TO,to);
                        surat.put(TAG_CC,cc);
                        surat.put(TAG_BCC,bcc);
                        surat.put(TAG_FROM,from);
                        surat.put(TAG_LAMPIRAN,lampiran);
                        surat.put(TAG_ISI,isi);
                        surat.put(TAG_TempatTanggal,tempattanggal);
                        surat.put(TAG_TITLE,title);
                        surat.put(TAG_TANGGAL,tanggal);
                        surat.put(TAG_ISFAVORIT,IsFavorit);
                        surat.put(TAG_JENISDOKUMEN,jenisdokumen);
                        ((dis)getActivity()).isidisposisi = surat;
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

                isiTextView(doctype,surat.get(TAG_TempatTanggal).toString(),surat.get(TAG_NO).toString(),surat.get(TAG_TO).toString(),
                        surat.get(TAG_FROM).toString(),surat.get(TAG_TITLE).toString(),surat.get(TAG_LAMPIRAN).toString(),
                        surat.get(TAG_ISI).toString(), surat.get(TAG_ISFAVORIT).toString(), surat.get((TAG_JENISDOKUMEN).toString()));
            }
        }
    }

    public void isiTextView(String doctype, String tvtanggal, String tvnomor, String tvkepada, String tvdari, String tvperihal,
                            String tvlampiran, String tvisi, String isFavorit, String tvjenisdokumen){
        typename.setText(doctype);
        tanggal.setText(tvtanggal);
        nomor.setText(tvnomor);
        kepada.setText(tvkepada);
        dari.setText(tvdari);
        perihal.setText(tvperihal);
        lampiran.setText(tvlampiran);
        isi.setText(Html.fromHtml(tvisi));
        jenisdokumen.setText(tvjenisdokumen);

        if (fragment.equals("urgent") || fragment.equals("disposisi") || fragment.equals("hardcopy") || fragment.equals("online")) {
            if(isFavorit.equals("1"))
            {
                ((dis) getActivity()).favorit.setImageResource(R.drawable.ic_star);
                ((dis) getActivity()).IsFavoritAwal = true;
                ((dis) getActivity()).mIsFavorit = true;
            }
            else if (isFavorit.equals("0")) {
                ((dis) getActivity()).favorit.setImageResource(R.drawable.ic_star_white);
                ((dis) getActivity()).IsFavoritAwal = false;
                ((dis) getActivity()).mIsFavorit = false;
            }
        }
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

}
