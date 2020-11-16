package bni.emplus.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
import bni.emplus.PublicFunction;
import bni.emplus.UserSessionManager;
import bni.emplus.app;
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
 * Created by ZulfahPermataIlliyin on 6/29/2016.
 */
public class DetailApprove extends Fragment {
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
    private static final String TAG_TITLE = "Title";
    private static final String TAG_PRIORITY = "PriorityName";
    private static final String TAG_CLASSIFICATION = "ClassificationName";
    private static final String TAG_REV1 ="Reviewer1Name";
    private static final String TAG_REV2 ="Reviewer2Name";
    private static final String TAG_APPROVAL ="ApprovalName";
    private static final String TAG_REV1NPP ="Reviewer1NPP";
    private static final String TAG_REV2NPP ="Reviewer2NPP";
    private static final String TAG_APPROVALNPP ="ApprovalNPP";
    private static final String TAG_REV1UNIT ="Reviewer1UnitName";
    private static final String TAG_REV2UNIT ="Reviewer2UnitName";
    private static final String TAG_APPROVALUNIT ="ApprovalUnitName";
    private static final String TAG_REV1ROLE ="Reviewer1RoleName";
    private static final String TAG_REV2ROLE ="Reviewer2RoleName";
    private static final String TAG_APPROVALROLE ="ApprovalRoleName";
    private static final String TAG_REV1DISP ="DispReviewer1";
    private static final String TAG_REV2DISP ="DispReviewer2";
    private static final String TAG_APPROVALDISP ="DispApproval";
    private static final String TAG_AUDIT ="IsAuditDesc";

    HashMap<String,String> surat = new HashMap<String, String>();

    Dialog approve;
    View v;


    TextView kepada,dari,melalui,cc,bcc,perihal,lampiran,isi,prior,klasifikasi,audit,rev1,rev2,approval,status;
    String endocid,enkey,enempid,enroleid,enunitid,enstatid,ennpp,enkomen,enaction;
    String statusApprove;
    String docid,fragment,type,docstatus;

    UserSessionManager session;
    String npp,empid,roleid,unitid,statid,erid;
    int layout;

    PublicFunction pf = new PublicFunction();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        double screenInches = pf.getScreenInches(getActivity());

        if(screenInches<=4.0){
            layout = R.layout.detail_approve;
        }else if(screenInches>4.0&&screenInches<=5.0){
            layout = R.layout.detail_approve;
        }else if(screenInches>5.0&&screenInches<=6.0){
            layout = R.layout.detail_approve;
        }else if(screenInches>6.0&&screenInches<=7.0){
            layout = R.layout.detail_approve_tujuh;
        }else if(screenInches>7.0&&screenInches<=8.0){
            layout = R.layout.detail_approve_tujuh;
        }else if(screenInches>8.0&&screenInches<=9.0){
            layout = R.layout.detail_approve_tujuh;
        }else if(screenInches>9.0&&screenInches<=10.0) {
            layout = R.layout.detail_approve_tujuh;
        }else if(screenInches>10.0&&screenInches<=11.0){
            layout = R.layout.detail_approve_tujuh;
        }

        v =inflater.inflate(layout,container,false);

        Bundle args = this.getArguments();
        if(args!=null){
            docid = args.getString("docid");
            fragment = args.getString("fragment");
            type = args.getString("type");
            docstatus = args.getString("docstatus");
        }

        if(docstatus.equals("Reject"))
        {
            RelativeLayout status = (RelativeLayout)v.findViewById(R.id.rldocstatus);
            status.setVisibility(View.VISIBLE);
        }

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
        unitid = userRole.get(UserSessionManager.KEY_UNIT);
        roleid = userRole.get(UserSessionManager.KEY_ROLE);
        empid = user.get(UserSessionManager.KEY_EMPID);
        statid = userRole.get(UserSessionManager.KEY_STATID);
        erid = userRole.get(UserSessionManager.KEY_ERID);

        kepada = (TextView)v.findViewById(R.id.tvisikepadaApp);
        dari = (TextView)v.findViewById(R.id.tvIsiDariApp);
        melalui = (TextView)v.findViewById(R.id.tvIsiMelalui);
        cc = (TextView)v.findViewById(R.id.tvIsiCC);
        bcc = (TextView)v.findViewById(R.id.tvIsiBCC);
        perihal = (TextView)v.findViewById(R.id.tvIsiPerihalApp);
        lampiran = (TextView)v.findViewById(R.id.tvisilampiran);
        isi = (TextView)v.findViewById(R.id.tvIsiApp);
        prior = (TextView)v.findViewById(R.id.tvIsiPrioritas);
        klasifikasi = (TextView)v.findViewById(R.id.tvIsiKlasifikasi);
        audit = (TextView)v.findViewById(R.id.tvIsiAudit);
        rev1 = (TextView)v.findViewById(R.id.tvIsiReviewer);
        rev2 = (TextView)v.findViewById(R.id.tvIsiReviewer2);
        approval = (TextView)v.findViewById(R.id.tvIsiApproval);
        status = (TextView)v.findViewById(R.id.tvIsiStatus);

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(!((app)getActivity()).isiapprove.isEmpty()){
            surat = ((app)getActivity()).isiapprove;
            isiTextview();
        }else {
            if (isInternetPresent) {
                ambilDetailApprove();
            } else {
                showAlertConnection(Config.alertConnection, "detail");
            }
        }



        return v;
    }
    public void showAlertConnection(final String pesan, final String status) {
        Toast.makeText(getActivity(), pesan,
                Toast.LENGTH_LONG).show();
        if (status.equals("proses")) {
            approve.dismiss();
        } else {
            getActivity().setTitle(Config.nointernet);
            ((app) getActivity()).adapter = null;
            ((app) getActivity()).pager = null;
            ((app) getActivity()).tabs = null;
        }

        /*getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if (status.equals("proses")) {
                            approve.dismiss();
                        } else {
                            getActivity().setTitle(Config.nointernet);
                            ((app) getActivity()).adapter = null;
                            ((app) getActivity()).pager = null;
                            ((app) getActivity()).tabs = null;
                        }
                    }
                });

                if(status.equals("proses")){
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    if (((app) getActivity()).dialogConnection == false) {
                        ((app) getActivity()).dialogConnection = true;
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }

        });*/
    }
    public void ambilDetailApprove(){
        mCrypt mCrypt = new mCrypt();
        try {
            endocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
            enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilDetailApprove ambil = new ambilDetailApprove();
        ambil.execute(endocid, enkey);
    }

    class ambilDetailApprove extends AsyncTask<String, Void, JSONArray> {

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

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, docid));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetDetailForApproval.php");
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
                //showAlertConnection(Config.alertTimeOut, "detail");
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //showAlertConnection(Config.alertTimeOut, "detail");
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //showAlertConnection(Config.alertTimeOut, "detail");
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
            showAlertConnection(Config.alertTimeOut, "detail");
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
                        String prior = e.getString(TAG_PRIORITY);
                        String classification = e.getString(TAG_CLASSIFICATION);
                        String rev1 = e.getString(TAG_REV1DISP);
                        String rev2 = e.getString(TAG_REV2DISP);
                        String approval = e.getString(TAG_APPROVALDISP);
                        String audit = e.getString(TAG_AUDIT);

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
                        surat.put(TAG_PRIORITY,prior);
                        surat.put(TAG_CLASSIFICATION,classification);
                        surat.put(TAG_REV1DISP,rev1);
                        surat.put(TAG_REV2DISP,rev2);
                        surat.put(TAG_APPROVALDISP,approval);
                        surat.put(TAG_AUDIT,audit);

                        ((app)getActivity()).isiapprove = surat;
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            isiTextview();
        }
    }

    public void isiTextview(){
        kepada.setText(surat.get(TAG_TO).toString());
        dari.setText(surat.get(TAG_FROM).toString());
        melalui.setText(surat.get(TAG_MELALUI).toString());
        cc.setText(surat.get(TAG_CC).toString());
        bcc.setText(surat.get(TAG_BCC).toString());
        perihal.setText(surat.get(TAG_TITLE).toString());
        lampiran.setText(surat.get(TAG_LAMPIRAN).toString());
        isi.setText(Html.fromHtml(surat.get(TAG_ISI).toString()));
        prior.setText(surat.get(TAG_PRIORITY).toString());
        klasifikasi.setText(surat.get(TAG_CLASSIFICATION).toString());
        audit.setText(surat.get(TAG_AUDIT).toString());
        rev1.setText(surat.get(TAG_REV1DISP).toString());
        rev2.setText(surat.get(TAG_REV2DISP).toString());
        approval.setText(surat.get(TAG_APPROVALDISP).toString());
        status.setText(docstatus);
    }
}

