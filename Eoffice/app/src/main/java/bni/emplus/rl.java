package bni.emplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import bni.emplus.Adapter.RoleAdapter;

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

public class rl extends AppCompatActivity {
    PublicFunction pf = new PublicFunction();
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    UserSessionManager session;

    ArrayList<HashMap<String,String>> roles = new ArrayList<HashMap<String, String>>();
    String empid,enempid,enKey,npp,stringDisclaimer,fromActivity;
    ListView listView;

    Dialog disclaimer;
    private PrefManager prefManager;

    RoleAdapter adapter;

    private static final String TAG_ID = "Id";
    private static final String TAG_DISPLAY = "DispDDL";
    private static final String TAG_ROLEID = "Role_Id";
    private static final String TAG_UNITID = "Unit_Id";
    private static final String TAG_STATUSID = "Status_Id"; //status 1 = bukan PGS, status 2 = PGS
    private static final String TAG_MENUMOBILE = "MenuMobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);
        setTitle("Pilih Peran");

        //change status bar color for lollipop and above
        if(Build.VERSION.SDK_INT >= 21){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
        }

        //session
        session = new UserSessionManager(rl.this);
        if (session.checkLogin()) {
            Intent i = new Intent(rl.this, log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        //get put extra
        Bundle bd = getIntent().getExtras();
        if(bd != null)
        {
            empid = (String) bd.get("empid");
            npp = (String) bd.get("npp");
            stringDisclaimer = (String) bd.get("disclaimer");
            fromActivity = (String) bd.get("fromActivity");
        }

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            dialogDisclaimer();

        }else{
            roleActivity();
        }
    }

    private void dialogDisclaimer(){
        disclaimer = new Dialog(rl.this);
        disclaimer.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        disclaimer.setContentView(R.layout.dialog_disclaimer);

        final CheckBox cbAgree = (CheckBox) disclaimer.findViewById(R.id.cbAgree);
        final Button accept = (Button) disclaimer.findViewById(R.id.btAgree);
        TextView disclaim = (TextView) disclaimer.findViewById(R.id.disclaim);

        if(stringDisclaimer!=null){
            disclaim.setText(stringDisclaimer);
        }

        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbAgree.isChecked()){
                    accept.setEnabled(true);
                }else{
                    accept.setEnabled(false);
                }
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setFirstTimeLaunch(false);
                disclaimer.dismiss();
                roleActivity();
            }
        });
        disclaimer.setCancelable(false);
        disclaimer.show();
    }

    private void roleActivity(){
        //get string putextra
        cd=new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent) {
            loadRole();
        } else{
            pf.showAlertConnection(this);
        }
    }

    public void loadRole(){
        mCrypt mCrypt = new mCrypt();
        try {
            enempid = mCrypt.bytesToHex(mCrypt.encrypt(empid));
            enKey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.lvRole);

        adapter = new RoleAdapter(rl.this, roles);

        ambilRole amRole = new ambilRole();
        amRole.execute(enempid, enKey);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String erid = roles.get(position).get(TAG_ID);
                String unitid = roles.get(position).get(TAG_UNITID);
                String roleid = roles.get(position).get(TAG_ROLEID);
                String statid = roles.get(position).get(TAG_STATUSID);
                String menumobile = roles.get(position).get(TAG_MENUMOBILE);
                String fragment = "urgent";

                //if (roleid.equals("4") || roleid.equals("6") || roleid.equals("8") || roleid.equals("10") || roleid.equals("16")) {
                if (menumobile.equals("1")) {
                    fragment = "hardcopy";
                }
                else if(menumobile.equals("0")) {
                    fragment = "list";
                }

                session.createUserRoleSession(roleid, unitid, statid, erid,menumobile);

                Intent pindah = new Intent(rl.this, inb.class);
                pindah.putExtra("fragment", fragment);
                //pindah.putExtra("menumobile", menumobile);
                startActivity(pindah);
                finish();
            }
        });
    }

    public void bukaHome()
    {
        int position = 0;
        String erid = roles.get(position).get(TAG_ID);
        String unitid = roles.get(position).get(TAG_UNITID);
        String roleid = roles.get(position).get(TAG_ROLEID);
        String statid = roles.get(position).get(TAG_STATUSID);
        String menumobile = roles.get(position).get(TAG_MENUMOBILE);
        String fragment = "urgent";

        //if (roleid.equals("4") || roleid.equals("6") || roleid.equals("8") || roleid.equals("10") || roleid.equals("16")) {
        if (menumobile.equals("1")) {
            fragment = "hardcopy";
        }
        //else if(roleid.equals("0") || roleid.equals("1")) {
        else if(menumobile.equals("0")){
            fragment = "list";
        }

        session.createUserRoleSession(roleid, unitid, statid, erid, menumobile);

        Intent pindah = new Intent(rl.this, inb.class);
        pindah.putExtra("fragment", fragment);
        //pindah.putExtra("menumobile",menumobile);
        startActivity(pindah);
        finish();
    }
    class ambilRole extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(rl.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            String idemploy = params[0];
            String key = params[1];
            String responce;


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, idemploy));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetListEmpRole.php");
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
                //pf.showAlertTimeOut(rl.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(rl.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(rl.this);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String id = e.getString(TAG_ID);
                        String dispDdl = e.getString(TAG_DISPLAY);
                        String roleid = e.getString(TAG_ROLEID);
                        String unitid = e.getString(TAG_UNITID);
                        String statid = e.getString(TAG_STATUSID);
                        String menumobile = e.getString(TAG_MENUMOBILE);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID, id);
                        map.put(TAG_DISPLAY, dispDdl);
                        map.put(TAG_ROLEID,roleid);
                        map.put(TAG_UNITID,unitid);
                        map.put(TAG_STATUSID,statid);
                        map.put(TAG_MENUMOBILE,menumobile);

                        roles.add(map);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            loadingDialog.dismiss();
            if(roles.size() > 1 || fromActivity.equals("home")) {
                listView.setAdapter(adapter);
            }
            else if(roles.size() == 1)
            {
                bukaHome();
            }
        }
        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(rl.this);
        }
    }
    @Override
    public void onBackPressed() {

    }
}
