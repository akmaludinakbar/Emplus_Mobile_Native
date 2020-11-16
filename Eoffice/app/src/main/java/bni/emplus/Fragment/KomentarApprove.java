package bni.emplus.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import bni.emplus.Adapter.modelKomenAdapter;
import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
import bni.emplus.Model.modelKomen;
import bni.emplus.PublicFunction;
import bni.emplus.app;
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
import java.util.List;
/**
 * Created by ZulfahPermataIlliyin on 6/29/2016.
 */
public class KomentarApprove extends Fragment {
    private List<modelKomen> komenList = new ArrayList<>();
    private modelKomenAdapter komenAdapter;
    private ListView listView;

    private static final String TAG_ACTION = "Action";
    private static final String TAG_COMMENT = "Comment";
    private static final String TAG_NPP ="Employee_Npp";
    private static final String TAG_NAME = "EmployeeName";
    private static final String TAG_ROLE = "RoleName";
    private static final String TAG_ROLESTATUS = "RoleStatusName";
    private static final String TAG_TANGGAL = "CreatedTime_tgl";
    private static final String TAG_JAM = "CreatedTime_jam";

    String docid,endocid,enkey;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    PublicFunction pf = new PublicFunction();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.komen_approve, container, false);

        Bundle args = this.getArguments();
        if(args!=null){
            docid = args.getString("docid");
        }

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!((app) getActivity()).historiapprove.isEmpty()) {
            komenList = ((app) getActivity()).historiapprove;
        }else {
            if (isInternetPresent) {
                mCrypt krip = new mCrypt();
                try {
                    endocid = krip.bytesToHex(krip.encrypt(docid));
                    enkey = krip.bytesToHex(krip.encrypt(Config.api_key));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                ambilKomenApp ambil = new ambilKomenApp();
                ambil.execute(endocid, enkey);
            } else {
                showAlertConnection(Config.alertConnection);
            }
        }

        listView = (ListView) v.findViewById(R.id.lvkomen);
        komenAdapter = new modelKomenAdapter(getActivity(), komenList);
        listView.setAdapter(komenAdapter);
        return v;
    }
    public void showAlertConnection(final String pesan) {
        Toast.makeText(getActivity(), pesan,
                Toast.LENGTH_LONG).show();
        getActivity().setTitle(Config.nointernet);
        ((app) getActivity()).adapter = null;
        ((app) getActivity()).pager = null;
        ((app) getActivity()).tabs = null;
    }

    class ambilKomenApp extends AsyncTask<String, Void, JSONArray> {

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
                HttpPost httpPost = new HttpPost(Config.urlLanding + "ListDocApprovalComment.php");
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
                        String action = e.getString(TAG_ACTION);
                        String comment = e.getString(TAG_COMMENT);
                        String npp = e.getString(TAG_NPP);
                        String nama = e.getString(TAG_NAME);
                        String role = e.getString(TAG_ROLE);
                        String rolestat = e.getString(TAG_ROLESTATUS);
                        String tgl = e.getString(TAG_TANGGAL);
                        String jam = e.getString(TAG_JAM);

                        modelKomen komen = new modelKomen();
                        komen.setActionKomen(action);
                        komen.setIsiKomen(comment);
                        komen.setNppPeg(npp);
                        komen.setPegawai(nama);
                        komen.setRolePeg(role);
                        komen.setRolestatPeg(rolestat);
                        komen.setTanggalKomen(tgl);
                        komen.setJamKomen(jam);
                        komen.setGambarKomen(R.mipmap.ic_komen);

                        komenList.add(komen);
                        ((app) getActivity()).historiapprove = komenList;

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            listView.setAdapter(komenAdapter);
        }
    }
}
