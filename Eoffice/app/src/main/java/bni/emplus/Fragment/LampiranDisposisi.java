package bni.emplus.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import bni.emplus.Adapter.modelLampiranAdapter;
import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
import bni.emplus.Model.modelLampiran;
import bni.emplus.pva;
import bni.emplus.PublicFunction;
import bni.emplus.app;
import bni.emplus.det;
import bni.emplus.dis;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZulfahPermataIlliyin on 6/30/2016.
 */
public class LampiranDisposisi extends Fragment {
    private List<modelLampiran> lampiranList = new ArrayList<>();
    private modelLampiranAdapter lampiranAdapter;
    private ListView listView;

    private static final String TAG_ID = "Id";
    private static final String TAG_NAMA = "NamaFile";
    private static final String TAG_SIZE ="FileSize";
    private static final String TAG_TYPE = "FileType";
    private static final String TAG_TGL = "CreatedTime_Tgl";
    private static final String TAG_JAM = "CreatedTime_Jam";
    private static final String TAG_ISFIRSTDOC = "IsFirstDoc";

    private static final String TAG_NILAIBYTES = "NilaiBytes";
    private static final String TAG_ERROR = "ErrorMessage";

    String docid,endocid,enkey,fragment;
    String bytedokumen,size,fileName, pathFile;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    PublicFunction pf = new PublicFunction();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.lampiran_disposisi,container,false);

        Bundle args = this.getArguments();
        if(args!=null){
            docid = args.getString("docid");
            fragment = args.getString("fragment");
        }

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(fragment.equals("urgent")||fragment.equals("disposisi")||fragment.equals("hardcopy")||fragment.equals("online")){
            if(!((dis)getActivity()).filedisposisi.isEmpty()){
                lampiranList = ((dis)getActivity()).filedisposisi;
            }else {
                if (isInternetPresent) {
                    mCrypt mCrypt = new mCrypt();
                    try {
                        endocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
                        enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ambilLampiranDis ambil = new ambilLampiranDis();
                    ambil.execute(endocid, enkey);
                } else {
                    showAlertConnection(Config.alertConnection);
                }
            }
        }else{
            if(!((det)getActivity()).filedetail.isEmpty()){
                lampiranList = ((det)getActivity()).filedetail;
            }else {
                if (isInternetPresent) {
                    mCrypt mCrypt = new mCrypt();
                    try {
                        endocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
                        enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ambilLampiranDis ambil = new ambilLampiranDis();
                    ambil.execute(endocid, enkey);
                } else {
                    showAlertConnection(Config.alertConnection);
                }
            }
        }

        listView = (ListView) v.findViewById(R.id.lvLampiranDis);
        lampiranAdapter = new modelLampiranAdapter(getActivity(),lampiranList,fragment);
        listView.setAdapter(lampiranAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileid = lampiranList.get(position).getId();
                size = lampiranList.get(position).getFileSize();
                fileName = lampiranList.get(position).getJudulDokumen();
                String extensi = lampiranList.get(position).getFileType();
                String versi = Config.versi;
                String enid ="",enkey="", enversi = "";

                mCrypt mCrypt = new mCrypt();
                try {
                    enid = mCrypt.bytesToHex(mCrypt.encrypt(fileid));
                    enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                    enversi = mCrypt.bytesToHex(mCrypt.encrypt(versi));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(extensi.equals(".pdf")){
                    Intent i = new Intent(getActivity(), pva.class);
                    i.putExtra("id",enid);
                    i.putExtra("versi",enversi);
                    i.putExtra("apikey",enkey);
                    getActivity().startActivity(i);
                }
                else {
                    cd=new ConnectionDetector(getActivity().getApplication());
                    isInternetPresent = cd.isConnectingToInternet();

                    if(isInternetPresent) {
                        ambilInfoFile ambil = new ambilInfoFile();
                        ambil.execute(enid, enkey, lampiranList.get(position).getFileType(),enversi);
                    }else {
                        pf.showAlertConnection(getActivity());
                    }
                }
            }
        });
        return v;
    }

    class ambilInfoFile extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;
        String enid,enkey,type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;

            enid = params[0];
            enkey = params[1];
            type = params[2];
            String versi = params[3];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY,enkey));
            nameValuePairs.add(new BasicNameValuePair("id", enid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VARIABLETAMBAHAN, versi));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetFileInfo.php");
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

        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(getActivity());
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            String error = "";
            int id;
            if (result != null) {
                try{
                    id = result.getInt(TAG_ID);
                    String bytes =  result.getString(TAG_NILAIBYTES);
                    error = result.getString(TAG_ERROR);

                    bytedokumen = bytes;

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "test");
                folder.mkdir();

                File pdfFile = new File(folder, fileName);

                pathFile = Environment.getExternalStorageDirectory() + "/test/" + fileName;

                try {
                    pdfFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pathFile);
                    fos.write(Base64.decode(bytedokumen, Base64.NO_WRAP));
                    fos.close();
                    loadingDialog.dismiss();
                    view();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                loadingDialog.dismiss();
                pf.showAlert(getActivity(),error,Config.alertError);
            }
            loadingDialog.dismiss();
        }
    }

    private void view() {
        File pdfFile = new File(pathFile);
        //Uri path = Uri.fromFile(pdfFile);
        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".my.package.name.provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);

        if(pathFile.contains("pdf")){
            pdfIntent.setDataAndType(path, "application/pdf");
        }
        else if(pathFile.contains("doc") || pathFile.contains("docx")){
            pdfIntent.setDataAndType(path, "application/msword");
        }
        else if(pathFile.contains("jpg")){
            pdfIntent.setDataAndType(path, "image/*");
        }
        else if(pathFile.contains("xls") || pathFile.contains("xlsx")){
            pdfIntent.setDataAndType(path, "application/vnd.ms-excel");
        }
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try{
            getActivity().startActivity(pdfIntent);
            if(fragment.equals("disposisi")||fragment.equals("inbox")||fragment.equals("hardcopy")||fragment.equals("online"))
                ((dis)getActivity()).statOpenPDF = true;
            if(fragment.equals("approve"))
                ((app)getActivity()).statOpenPDF = true;
            if(fragment.equals("sent") || fragment.equals("monitoring") || fragment.equals("tracking"))
                ((det)getActivity()).statOpenPDF = true;
        }catch(ActivityNotFoundException e){
            Toast.makeText(getActivity(), "No Application available to view", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertConnection(final String pesan) {
        Toast.makeText(getActivity(), pesan,
                Toast.LENGTH_LONG).show();
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

    class ambilLampiranDis extends AsyncTask<String, Void, JSONArray> {

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
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetFilesInfoByDocID.php");
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
                        String nama = e.getString(TAG_NAMA);
                        String size = e.getString(TAG_SIZE);
                        String type = e.getString(TAG_TYPE);
                        String tgl = e.getString(TAG_TGL);
                        String jam = e.getString(TAG_JAM);
                        String isFirstDoc = e.getString(TAG_ISFIRSTDOC);

                        modelLampiran lampiran = new modelLampiran();
                        lampiran.setId(id);
                        lampiran.setJudulDokumen(nama);
                        lampiran.setFileSize(size);
                        lampiran.setFileType(type);
                        lampiran.setTanggalDokumen(tgl);
                        lampiran.setJamDokumen(jam);
                        lampiran.setIsFirstDoc(isFirstDoc);

                        if(type.equals(".pdf")){
                            lampiran.setGambarDokumen(R.mipmap.ic_pdf);
                        } else if(type.contains(".doc")){
                            lampiran.setGambarDokumen(R.mipmap.ic_word);
                        }else if(type.contains(".xls")){
                            lampiran.setGambarDokumen(R.mipmap.ic_xls);
                        }else if(type.contains(".jpg")){
                            lampiran.setGambarDokumen(R.mipmap.ic_jpg);
                        }

                        lampiranList.add(lampiran);

                        if(fragment.equals("urgent")||fragment.equals("disposisi")||fragment.equals("hardcopy")||fragment.equals("online")) {
                            ((dis) getActivity()).filedisposisi = lampiranList;
                        }else{
                            ((det) getActivity()).filedetail = lampiranList;
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            listView.setAdapter(lampiranAdapter);
        }
    }
}

