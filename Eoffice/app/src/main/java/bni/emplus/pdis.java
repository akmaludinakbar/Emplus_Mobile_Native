package bni.emplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import bni.emplus.Adapter.FileAdapter;
import bni.emplus.Adapter.PegawaiAdapter;
import bni.emplus.Fragment.NewSuratMasukFragment;
import bni.emplus.Model.Pegawai;
import bni.emplus.Model.Pendisposisi;
import bni.emplus.Model.attachment;
import bni.emplus.Model.modelDisposisi;
import bni.emplus.Model.modelJenisDokumen;

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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZulfahPermataIlliyin on 10/4/2016.
 */
public class pdis extends AppCompatActivity {
    private static final int REQUEST_PATH = 1;

    Boolean dialogAlert=false;

    Dialog delFile,addRec,upLanding,prosesdisposisi,addDis;
    LinearLayout btsubmit, btdelete, btnupload;
    RelativeLayout rlJenisDokumen;

    private ArrayList<String> namafilesdel = new ArrayList<String>();
    ArrayList<Integer> response = new ArrayList<>();

    private FileAdapter fileAdapter;
    private ListView listView;
    private ArrayList<String> idfilesdel = new ArrayList<String>();
    private ArrayList<Integer> positionfilesdel = new ArrayList<Integer>();
    private ArrayList<Integer> roleidberhakhardcopy = new ArrayList<Integer>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> namafile = new ArrayList<>();

    EditText editIsiWaktuDisposisi, editIsiTanggalDisposisi, editIsiDisposisi;
    Spinner spMonitor, spMetodeKirim, spPetugas, spJenisDokumen;
    RelativeLayout rvMetodeKirim;
    TextView tvPenerima, daftarFile;
    Button btProsesDisposisi,btKirimDisposisi;
    ImageView edit;
    String jam_serv, tanggal_serv, disp_tanggalserv,disp_jamserv;

    private List<Pegawai> pegawaiList = new ArrayList<>();
    private ArrayList<attachment> attachmentList = new ArrayList<>();
    private ArrayList<modelDisposisi> disposisiList = new ArrayList<>();
    private PegawaiAdapter pegawaiAdapter;

    int serverResponseCode = 0;

    //String [] isMonitor = {"No","Yes"};
    String [] metodeKirimHard = {"Tidak", "Ya"};
    //String [] metodeKirimHard = {"Kirim Softcopy", "Kirim Hardcopy"};
    String [] metodeKirimSoft = {"Tidak"};

    String docid, unitid, roleid, empid, idpendisposisi, statid,docregid,modefirstdoc,boxid,doctype,fragment,mode,regno,crtime,idJenisDokumen="0";
    String enunitid,enempid,enroleid,enkey,enid,entime,enmonitor,enmetode,enisi,enstat,enbox,endoc,endocreg,enrec,enfile,enlistfile,enflag,enisdispo,enjenisdok;
    String curFileName,curFilePath,endocid,enfilename,endispid;

    UserSessionManager session;

    ArrayList<String> idpenerima = new ArrayList<>();
    ArrayList<String> namapenerima = new ArrayList<>();
    ArrayList<String> dispPend = new ArrayList<>();
    ArrayList<String> jenisDokumen = new ArrayList<>();
    ArrayList<String> files = new ArrayList<>();
    ArrayAdapter<String> adapterPetugas, adapterJenisDokumen;
    ArrayAdapter<String> adapterMetode;
    private List<Pendisposisi> pendList = new ArrayList<>();
    ArrayList<modelJenisDokumen> listJenisDokumen = new ArrayList<>();

    private static final String TAG_ERID = "erid";
    private static final String TAG_EMPID = "Employee_Id";
    private static final String TAG_UNITID = "Unit_Id";
    private static final String TAG_NPP = "Npp";
    private static final String TAG_EMPNAME = "EmpName";
    private static final String TAG_EMPUNIT = "UnitName";

    private static final String TAG_EMPSTATID = "Status_Id";
    private static final String TAG_EMPROLEID = "Role_Id";
    private static final String TAG_DDLID = "DDLID";
    private static final String TAG_DDLDISP = "DDLDisplay";

    private static final String TAG_KODE = "Kode";
    private static final String TAG_KET = "Keterangan";

    private static final String TAG_ID = "Id";
    private static final String TAG_NAME = "Name";
    private static final String TAG_TIPEREC = "TipeRec";
    private static final String TAG_FIRSTCHAR = "FirstChar";

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    int spinner,spinItem;
    ArrayList<Integer> sizefile = new ArrayList<Integer>();

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;

    PublicFunction pf = new PublicFunction();

    List<Object[]> alphabet = new ArrayList<Object[]>();
    HashMap<String, Integer> sections = new HashMap<String, Integer>();

    private int indexListSize;
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        double screenInches = pf.getScreenInches(this);
        if(screenInches<=4.0){
            setContentView(R.layout.activity_proses_disposisi);
            spinner = R.layout.spinner_item;
            spinItem =android.R.layout.simple_spinner_dropdown_item;
        }else if(screenInches>4.0&&screenInches<=5.0){
            setContentView(R.layout.activity_proses_disposisi);
            spinner = R.layout.spinner_item;
            spinItem =android.R.layout.simple_spinner_dropdown_item;
        }else if(screenInches>5.0&&screenInches<=6.0){
            setContentView(R.layout.activity_proses_disposisi);
            spinner = R.layout.spinner_item;
            spinItem =android.R.layout.simple_spinner_dropdown_item;
        }else if(screenInches>6.0&&screenInches<=7.0){
            setContentView(R.layout.activity_proses_disposisi_tujuh);
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>7.0&&screenInches<=8.0){
            setContentView(R.layout.activity_proses_disposisi_tujuh);
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>8.0&&screenInches<=9.0){
            setContentView(R.layout.activity_proses_disposisi_tujuh);
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>9.0&&screenInches<=10.0) {
            setContentView(R.layout.activity_proses_disposisi_tujuh);
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>10.0&&screenInches<=11.0){
            setContentView(R.layout.activity_proses_disposisi_tujuh);
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }
        getActionBar();
        setTitle("Isi Sirkulasi");

        if(Build.VERSION.SDK_INT >= 21){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
        }

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //session
        session = new UserSessionManager(pdis.this);
        if (session.checkLogin()) {
            Intent i = new Intent(pdis.this, log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        HashMap<String, String> user = session.getUserNPP();
        HashMap<String, String> userRole = session.getUserRole();

        // get roleid,unitid
        unitid = userRole.get(UserSessionManager.KEY_UNIT);
        roleid = userRole.get(UserSessionManager.KEY_ROLE);
        empid = user.get(UserSessionManager.KEY_EMPID);
        statid = userRole.get(UserSessionManager.KEY_STATID);

        Bundle args = getIntent().getExtras();
        if(args!=null){
            docid = args.getString("docid");
            boxid = (String) args.get("boxid");
            doctype = (String) args.get("doctype");
            docregid = (String) args.get("docregid");
            modefirstdoc = (String) args.get("modefirstdoc");
            fragment = (String) args.get("fragment");
            mode = (String)args.get("mode");
            regno = (String)args.get("regno");
            crtime = (String)args.get("tanggal");
        }

        editIsiTanggalDisposisi = (EditText) findViewById(R.id.editIsiTanggalDisposisi);
        editIsiWaktuDisposisi = (EditText) findViewById(R.id.editIsiWaktuDisposisi);
        spPetugas = (Spinner) findViewById(R.id.spPetugas);
        spJenisDokumen = (Spinner) findViewById(R.id.spJenisDokumen);
        btKirimDisposisi = (Button) findViewById(R.id.btKirimDisposisi);
        rlJenisDokumen = (RelativeLayout) findViewById(R.id.rlJenisDokumen);

        cd=new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent) {
            ambilPetugas();
        }else{
            pf.showAlertConnection(this);
        }


    }

    private void prosesDisposisi(){
        adapterPetugas = new ArrayAdapter<String>(pdis.this,spinner,dispPend);
        adapterPetugas.setDropDownViewResource(spinItem);
        spPetugas.setAdapter(adapterPetugas);

        spPetugas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idpendisposisi = pendList.get(position).getDdlid().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rvListDisposisi);
        adapter = new modelDisposisiAdapter(disposisiList, pdis.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapterJenisDokumen = new ArrayAdapter<String>(pdis.this,spinner,jenisDokumen);
        adapterJenisDokumen.setDropDownViewResource(spinItem);
        spJenisDokumen.setAdapter(adapterJenisDokumen);

        spJenisDokumen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idJenisDokumen = listJenisDokumen.get(position).getIdJenisDokumen();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cd=new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent){
            ambilTanggal();
            ambilRolePilihHardcopy();
            ambilJenisDokumen();
        }else{
            pf.showAlertConnection(this);
        }

        ImageView addDisposisi = (ImageView) findViewById(R.id.ivAddDisposisi);
        addDisposisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddDisposisi();
            }
        });

        btKirimDisposisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<disposisiList.size();i++){

                    if(disposisiList.get(i).getAttachmentList().isEmpty()){
                        int flag = 0;
                        kirimdisposisi(i, flag);
                    }else {
                        ArrayList<attachment> listfile = new ArrayList<attachment>();
                        listfile = disposisiList.get(i).getAttachmentList();
                        sizefile.add(i,listfile.size());
                        for (int j = 0; j < listfile.size(); j++) {

                            cd = new ConnectionDetector(getApplicationContext());
                            isInternetPresent = cd.isConnectingToInternet();
                            if (isInternetPresent) {
                                String dispid = "1";

                                uploadFile upload = new uploadFile();
                                upload.execute(docid, dispid, listfile.get(j).getNamaFile(), empid, String.valueOf(i), String.valueOf(j+1));
                            } else {
                                pf.showAlertConnection(pdis.this);
                            }
                        }
                    }
                }
            }
        });
    }

    public void ambilJenisDokumen(){
        int isDisposisi = 1; //disposisi
        mCrypt krip = new mCrypt();
        try {
            enroleid = krip.bytesToHex(krip.encrypt(roleid));
            enunitid = krip.bytesToHex(krip.encrypt(unitid));
            enisdispo = krip.bytesToHex(krip.encrypt(String.valueOf(isDisposisi)));
            endocid = krip.bytesToHex(krip.encrypt(docid));
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        getJenisDokumen getJenisDokumen = new getJenisDokumen();
        getJenisDokumen.execute(enroleid, enunitid, enisdispo, endocid, enkey);
    }

    public void dialogAddDisposisi(){
        addDis = new Dialog(pdis.this);
        addDis.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);
        if(screenInches<=4.0){
            addDis.setContentView(R.layout.isi_disposisi);
        }else if(screenInches>4.0&&screenInches<=5.0){
            addDis.setContentView(R.layout.isi_disposisi);
        }else if(screenInches>5.0&&screenInches<=6.0){
            addDis.setContentView(R.layout.isi_disposisi);
        }else if(screenInches>6.0&&screenInches<=7.0){
            addDis.setContentView(R.layout.isi_disposisi_tujuh);
        }else if(screenInches>7.0&&screenInches<=8.0){
            addDis.setContentView(R.layout.isi_disposisi_tujuh);
        }else if(screenInches>8.0&&screenInches<=9.0){
            addDis.setContentView(R.layout.isi_disposisi_tujuh);
        }else if(screenInches>9.0&&screenInches<=10.0) {
            addDis.setContentView(R.layout.isi_disposisi_tujuh);
        }else if(screenInches>10.0&&screenInches<=11.0){
            addDis.setContentView(R.layout.isi_disposisi_tujuh);
        }

        //spMonitor = (Spinner) addDis.findViewById(R.id.spInginDimonitor);
        spMetodeKirim = (Spinner) addDis.findViewById(R.id.spMetodeKirim);
        daftarFile = (TextView) addDis.findViewById(R.id.tvIsiAttachDisposisi);
        edit = (ImageView) addDis.findViewById(R.id.ivEditAttachDisposisi);
        tvPenerima = (TextView) addDis.findViewById(R.id.tvIsiPenerima);
        btProsesDisposisi = (Button) addDis.findViewById(R.id.btProsesDisposisi);
        editIsiDisposisi = (EditText) addDis.findViewById(R.id.editIsiDisposisi);
        rvMetodeKirim = (RelativeLayout) addDis.findViewById(R.id.rvMetodeKirim);


        //initializa and set Adapter
        /*ArrayAdapter<String> adapterIsMonitor = new ArrayAdapter<String>(pdis.this,spinner,isMonitor);
        adapterIsMonitor.setDropDownViewResource(spinItem);
        spMonitor.setAdapter(adapterIsMonitor);*/

        adapterMetode = new ArrayAdapter<String>(pdis.this,spinner,metodeKirimHard);

        /*if(modefirstdoc.equals("2")){
            adapterMetode = new ArrayAdapter<String>(pdis.this,spinner,metodeKirimHard);
        }else if(modefirstdoc.equals("1")){
            adapterMetode = new ArrayAdapter<String>(pdis.this,spinner,metodeKirimSoft);
        }*/
        if(!roleidberhakhardcopy.isEmpty() ) {
            if (roleidberhakhardcopy.contains(Integer.valueOf(roleid))) {
                adapterMetode = new ArrayAdapter<String>(pdis.this, spinner, metodeKirimHard);
            } else {
                adapterMetode = new ArrayAdapter<String>(pdis.this, spinner, metodeKirimSoft);
                rvMetodeKirim.setVisibility(View.GONE);
            }
        }

        adapterMetode.setDropDownViewResource(spinItem);
        spMetodeKirim.setAdapter(adapterMetode);

        daftarFile.setText("0 Lampiran");
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!daftarFile.getText().equals("")) {
                    dialogFiles();
                }
            }
        });

        btProsesDisposisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idpenerima.isEmpty()){
                    tvPenerima.setError("penerima tidak boleh kosong");


                }else{
                    String metode,isMonitor;
                    //String disptime = editIsiTanggalDisposisi.getText().toString() + "T" + editIsiWaktuDisposisi.getText().toString() + ":00+07:00";
                    String disptime = tanggal_serv + "T" + jam_serv + ":00+07:00";
                    /*String disptime = editIsiTanggalDisposisi.getText().toString() + " " + editIsiWaktuDisposisi.getText().toString();*/
                    /*if (spMonitor.getSelectedItem().toString().equals("Yes")) {
                        isMonitor = "1";
                    } else {
                        isMonitor = "0";
                    }*/
                    isMonitor = "0";

                    String listRecipient = TextUtils.join(",", idpenerima);
                    //pegawaiList.clear();
                    for(int i=0;i<pegawaiList.size();i++)
                    {
                        if(pegawaiList.get(i).getSelected() == true)
                            pegawaiList.get(i).setSelected(false);
                    }
                    idpenerima.clear();
                    //if (spMetodeKirim.getSelectedItem().toString().equals("Kirim Softcopy")) {
                    if (spMetodeKirim.getSelectedItem().toString().equals("Tidak")) {
                        metode = "0";
                    } else {
                        metode = "1";
                    }
                    String isiDisposisi = editIsiDisposisi.getText().toString();
                    String listFiles = TextUtils.join(",", files);
                    String listnamapenerima = TextUtils.join(", ", namapenerima);
                    namapenerima.clear();

                    ArrayList<attachment> attachments = new ArrayList<attachment>();
                    for(int i=0;i<attachmentList.size();i++){
                        if(attachmentList.get(i).getInLandingPage()==true) {
                            attachments.add(attachmentList.get(i));
                        }
                    }
                    attachmentList.clear();

                    modelDisposisi disposisi = new modelDisposisi();
                    disposisi.setIdpendisposisi(idpendisposisi);
                    disposisi.setDispositiontime(disptime);
                    disposisi.setIsMonitor(isMonitor);
                    disposisi.setMetode(metode);
                    disposisi.setIsidisposisi(isiDisposisi);
                    disposisi.setEmpid(empid);
                    disposisi.setUnitid(unitid);
                    disposisi.setRoleid(roleid);
                    disposisi.setStatid(statid);
                    disposisi.setBoxid(boxid);
                    disposisi.setDocid(docid);
                    disposisi.setDocregid(docregid);
                    disposisi.setListpenerima(listRecipient);
                    disposisi.setListfile(listFiles);
                    disposisi.setListnamapenerima(listnamapenerima);
                    disposisi.setAttachmentList(attachments);
                    disposisiList.add(disposisi);

                    addDis.dismiss();

                    adapter = new modelDisposisiAdapter(disposisiList, pdis.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        ImageView add = (ImageView) addDis.findViewById(R.id.ivAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (pegawaiList.isEmpty()) {
                        cd = new ConnectionDetector(pdis.this);
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            mCrypt mCrypt = new mCrypt();
                            try {
                                enunitid = mCrypt.bytesToHex(mCrypt.encrypt(unitid));
                                enroleid = mCrypt.bytesToHex(mCrypt.encrypt(roleid));
                                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                                enempid = mCrypt.bytesToHex(mCrypt.encrypt(empid));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            ambilRecipients ambil = new ambilRecipients();
                            ambil.execute(enunitid, enroleid, enkey, enempid);
                        } else {
                            showAlertToast(Config.alertConnection, Config.alertWarning, "KoneksiTambahPenerima");
                        }
                    } else {
                        dialog();
                    }
            }
        });

        addDis.show();
        Window window = addDis.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void ambilRolePilihHardcopy(){
        String type = "RoleMenuSekretaris";
        String appid = "4";
        String listException = "";

        String entype="",enapp="",enlist="";

        mCrypt krip = new mCrypt();
        try {
            entype = krip.bytesToHex(krip.encrypt(type));
            enapp = krip.bytesToHex(krip.encrypt(appid));
            if(listException.isEmpty()){
                enlist = listException;
            }else{
                enlist = krip.bytesToHex(krip.encrypt(listException));
            }
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilRolePilihHard ambilRolePilihHard = new ambilRolePilihHard();
        ambilRolePilihHard.execute(entype,enapp,enlist,enkey);
    }

    class getJenisDokumen extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(pdis.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;

            String roleid = params[0];
            String unitid = params[1];
            String isdisposisi = params[2];
            String empdocid = params[3];
            String key = params[4];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ISDISP, isdisposisi));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, empdocid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetJenisDokumen.php");
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

        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            loadingDialog.dismiss();
            if (result != null) {
                try
                {
                    String kode = result.getString("Kode");
                    String message = result.getString("Message");

                    if(kode.equals("1"))
                    {
                        String usingSla = result.getString("UsingSla");
                        if(usingSla.equals("1"))
                        {
                            rlJenisDokumen.setVisibility(View.VISIBLE);

                            JSONArray arrayJenisDok = result.getJSONArray("DdlJenisDokumen");

                            for(int i=0; i< arrayJenisDok.length(); i++)
                            {
                                JSONObject objJenisDok = arrayJenisDok.getJSONObject(i);
                                String id = objJenisDok.getString("IdJenisDokumen");
                                String nama = objJenisDok.getString("NamaJenisDokumen");

                                modelJenisDokumen modelJenisDokumen = new modelJenisDokumen();
                                modelJenisDokumen.setIdJenisDokumen(id);
                                modelJenisDokumen.setNamaJenisDokumen(nama);

                                listJenisDokumen.add(modelJenisDokumen);
                                jenisDokumen.add(nama);
                            }

                            spJenisDokumen.setAdapter(adapterJenisDokumen);

                            String editable = result.getString("Editable");
                            if(editable.equals("0"))
                            {
                                spJenisDokumen.setEnabled(false);
                            }
                            String JenisDokumenSelected = result.getString("JenisDokumenSelected");
                            int pos = 0;
                            for (int i=0; i<listJenisDokumen.size(); i++)
                            {
                                if(listJenisDokumen.get(i).getIdJenisDokumen().equals(JenisDokumenSelected))
                                {
                                    pos = i;
                                    break;
                                }
                            }
                            spJenisDokumen.setSelection(pos);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    class ambilRolePilihHard extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(pdis.this, null, "Harap tunggu . . .");
        }


        @Override
        protected JSONArray doInBackground(String... params) {

            String responce;

            String type = params[0];
            String appid = params[1];
            String listexception = params[2];
            String key = params[3];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TYPE, type));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APPID, appid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_LISEXC, listexception));


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "SelectLookup.php");
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
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String name = e.getString("Name");
                        String value = e.getString("Value");

                        roleidberhakhardcopy.add(Integer.valueOf(value));

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private void kirimdisposisi(int i, int flag){
        cd=new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent){
            mCrypt mCrypt = new mCrypt();
            try {
                enid = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getIdpendisposisi()));
                entime = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getDispositiontime()));
                enmonitor = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getIsMonitor()));
                enmetode = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getMetode()));
                if (disposisiList.get(i).getIsidisposisi().isEmpty() || disposisiList.get(i).getIsidisposisi() == null) {
                    enisi = disposisiList.get(i).getIsidisposisi();
                } else {
                    enisi = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getIsidisposisi()));
                }
                enempid = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getEmpid()));
                enroleid = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getRoleid()));
                enunitid = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getUnitid()));
                enstat = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getStatid()));
                enbox = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getBoxid()));
                endoc = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getDocid()));
                endocreg = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getDocregid()));
                enrec = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getListpenerima()));
                if (disposisiList.get(i).getListfile().isEmpty() || disposisiList.get(i).getListfile() == null) {
                    enfile = disposisiList.get(i).getListfile();
                } else {
                    enfile = mCrypt.bytesToHex(mCrypt.encrypt(disposisiList.get(i).getListfile()));
                }
                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                enflag = mCrypt.bytesToHex(mCrypt.encrypt(String.valueOf(flag)));
                enjenisdok = mCrypt.bytesToHex(mCrypt.encrypt(String.valueOf(idJenisDokumen)));
            } catch (Exception e) {
                e.printStackTrace();
            }


            if(flag==0)
            {
                kirimDisposisi kirim = new kirimDisposisi();
                kirim.execute(enid, entime, enmonitor, enmetode, enisi, enempid, enroleid, enunitid, enstat, enbox, endoc, endocreg, enrec, enfile, enkey, String.valueOf(i), enflag, enjenisdok);
            }
            else if(flag==1)
            {
                kirimDisposisi2 kirims = new kirimDisposisi2();
                kirims.execute(enid, entime, enmonitor, enmetode, enisi, enempid, enroleid, enunitid, enstat, enbox, endoc, endocreg, enrec, enfile, enkey, String.valueOf(i), enflag, enjenisdok);
            }
        }else{
            pf.showAlertConnection(this);

        }
    }

    private void ambilPetugas() {
        mCrypt krip = new mCrypt();
        try {
            enunitid = krip.bytesToHex(krip.encrypt(unitid));
            enroleid = krip.bytesToHex(krip.encrypt(roleid));
            enempid = krip.bytesToHex(krip.encrypt(empid));
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));

        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilList ambil = new ambilList();
        ambil.execute(enunitid, enroleid, enempid, enkey);
    }
    private void ambilTanggal() {
        mCrypt krip = new mCrypt();
        try {
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));

        } catch (Exception e) {
            e.printStackTrace();
        }

        getServerDate ambilTanggal = new getServerDate();
        ambilTanggal.execute(enkey);
    }

    public void dialog() {
        addRec = new Dialog(pdis.this);
        addRec.requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        double screenInches = pf.getScreenInches(this);

        if (screenInches <= 4.0) {
            addRec.setContentView(R.layout.activity_list_recipients);
        } else if (screenInches > 4.0 && screenInches <= 5.0) {
            addRec.setContentView(R.layout.activity_list_recipients);
        } else if (screenInches > 5.0 && screenInches <= 6.0) {
            addRec.setContentView(R.layout.activity_list_recipients);
        } else if (screenInches > 6.0 && screenInches <= 7.0) {
            addRec.setContentView(R.layout.activity_list_recipients_tujuh);
        } else if (screenInches > 7.0 && screenInches <= 8.0) {
            addRec.setContentView(R.layout.activity_list_recipients_tujuh);
        } else if (screenInches > 8.0 && screenInches <= 9.0) {
            addRec.setContentView(R.layout.activity_list_recipients_tujuh);
        } else if (screenInches > 9.0 && screenInches <= 10.0) {
            addRec.setContentView(R.layout.activity_list_recipients_tujuh);
        } else if (screenInches > 10.0 && screenInches <= 11.0) {
            addRec.setContentView(R.layout.activity_list_recipients_tujuh);
        }

        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;

        alphabet = new ArrayList<Object[]>();

        for(int i = 0; i < pegawaiList.size(); i++)
        {
            String firstLetter =  pegawaiList.get(i).getFirstChar();

            if(previousLetter != null && !firstLetter.equals(previousLetter))
            {
                end = i-1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);

                start = end + 1;
            }

            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                //rows.add(new AlphabetListAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }

            previousLetter = firstLetter;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = pegawaiList.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        updateList();

        listView = (ListView) addRec.findViewById(R.id.lvDaftarPenerima);
        pegawaiAdapter = new PegawaiAdapter(pdis.this, pegawaiList);
        listView.setAdapter(pegawaiAdapter);

        EditText searchRecipients = (EditText) addRec.findViewById(R.id.searchRecipient);
        searchRecipients.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textLength = s.length();

                ArrayList<Pegawai> tempArrayList = new ArrayList<Pegawai>();
                for(Pegawai p: pegawaiList){
                    if (textLength <= p.getNamaPegawai().length()) {
                        if (p.getNamaPegawai().toLowerCase().contains(s.toString().toLowerCase())) {
                            tempArrayList.add(p);
                        }
                    }
                }

                pegawaiAdapter = new PegawaiAdapter(pdis.this, tempArrayList);
                listView.setAdapter(pegawaiAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button btTambahPenerima = (Button) addRec.findViewById(R.id.btTambahPenerima);
        btTambahPenerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idpenerima.clear();
                namapenerima.clear();
                for (int i = 0; i < pegawaiList.size(); i++) {
                    String isSelect = pegawaiList.get(i).getSelected().toString();
                    if (isSelect.equals("true") && !pegawaiList.get(i).getTipeRec().equals("0")) {
                        idpenerima.add(pegawaiList.get(i).getIdPegawai());
                        namapenerima.add(pegawaiList.get(i).getNamaPegawai());
                    }
                }
                tvPenerima.setText(TextUtils.join(", ",namapenerima));
                addRec.dismiss();
            }
        });

        addRec.show();
        Window window = addRec.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void updateList()
    {
        LinearLayout sideIndex = (LinearLayout) addRec.findViewById(R.id.sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            tmpTV = new TextView(this);
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();

        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();

                return false;
            }
        });
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) addRec.findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);
            int subitemPosition = sections.get(indexItem[0]);

            //ListView listView = (ListView) findViewById(android.R.id.list);
            listView.setSelection(subitemPosition);
        }
    }

    public class getServerDate extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(pdis.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String responce;
            String key = params[0];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetServerDate.php");
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
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
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
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        tanggal_serv = e.getString("Server_Tgl2");
                        jam_serv = e.getString("Server_jam");
                        disp_tanggalserv = e.getString("Server_Tgl");
                        disp_jamserv = e.getString("Disp_Server_Jam");

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                editIsiTanggalDisposisi.setText(disp_tanggalserv);
                editIsiWaktuDisposisi.setText(disp_jamserv);
            }
        }
    }

    public class ambilRecipients extends AsyncTask<String, Void, JSONArray> {

        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(pdis.this, null, "Harap tunggu . . .");
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String responce;

                String unitid = params[0];
            String roleid = params[1];
            String key = params[2];
            String empid = params[3];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VARIABLETAMBAHAN, empid));


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetListPenerimaDisposisi.php");
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
                Log.d("responce",responce);

                return new JSONArray(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //showAlertToast(Config.alertTimeOut, Config.alertWarning, "KoneksiTambahPenerima");
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //showAlertToast(Config.alertTimeOut, Config.alertWarning, "KoneksiTambahPenerima");
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //showAlertToast(Config.alertTimeOut, Config.alertWarning, "KoneksiTambahPenerima");
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            dialog.dismiss();
            showAlertToast(Config.alertTimeOut, Config.alertWarning, "KoneksiTambahPenerima");
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            dialog.dismiss();
            boolean findMember = false;
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String id = e.getString(TAG_ID);
                        String nama = e.getString(TAG_NAME);
                        String tiperec = e.getString(TAG_TIPEREC);
                        String firstchar = e.getString(TAG_FIRSTCHAR);

                        Boolean same = false;

                        if(ids!=null){
                            for (String string : ids){
                                if(string.equals(id)){
                                    same = true;
                                    break;
                                }
                            }
                        }

                        if(tiperec.equals("2") && !findMember)
                        {
                            findMember = true;
                            Pegawai peg = new Pegawai();
                            peg.setIdPegawai("0");
                            peg.setNamaPegawai("Pilih Semua Member");
                            peg.setTipeRec("0");
                            peg.setSelected(false);
                            peg.setFirstChar("A");

                            pegawaiList.add(peg);
                        }

                        Pegawai peg = new Pegawai();
                        peg.setIdPegawai(id);
                        peg.setNamaPegawai(nama);
                        peg.setTipeRec(tiperec);
                        peg.setFirstChar(firstchar);

                        if(same){
                            peg.setSelected(true);
                        } else{
                            peg.setSelected(false);
                        }

                        pegawaiList.add(peg);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                dialog();
                listView.setAdapter(pegawaiAdapter);
            }
        }
    }

    class ambilList extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(pdis.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String responce;
            String unitid = params[0];
            String roleid = params[1];
            String empid = params[2];
            String key = params[3];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "ListDispositionEmployees.php");
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
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
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
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String erid = e.getString(TAG_ERID);
                        String empid = e.getString(TAG_EMPID);
                        String unitid = e.getString(TAG_UNITID);
                        String npp = e.getString(TAG_NPP);
                        String empname = e.getString(TAG_EMPNAME);
                        String unitname = e.getString(TAG_EMPUNIT);
                        String statid = e.getString(TAG_EMPSTATID);
                        String roleid = e.getString(TAG_EMPROLEID);
                        String ddlid = e.getString(TAG_DDLID);
                        String ddldisp = e.getString(TAG_DDLDISP);

                        Pendisposisi pend = new Pendisposisi();
                        pend.setErid(erid);
                        pend.setEmpid(empid);
                        pend.setUnitid(unitid);
                        pend.setNpp(npp);
                        pend.setEmpname(empname);
                        pend.setUnitname(unitname);
                        pend.setStatid(statid);
                        pend.setRoleid(roleid);
                        pend.setDdlid(ddlid);
                        pend.setDdldisp(ddldisp);

                        pendList.add(pend);
                        dispPend.add(ddldisp);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                prosesDisposisi();
                spPetugas.setAdapter(adapterPetugas);
            }

        }
    }
    class kirimDisposisi2 extends AsyncTask<String, Void, JSONObject> {

        String indexdisposisi;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;
            String dispEmpId = params[0];
            String disptime = params[1];
            String isMonitor = params[2];
            String isHardcpy = params[3];
            String isiDisposisi = params[4];
            String empid = params[5];
            String roleid = params[6];
            String unitid = params[7];
            String statid = params[8];
            String inboxid = params[9];
            String docid = params[10];
            String docregid = params[11];
            String recipients = params[12];
            String files = params[13];
            String key = params[14];
            indexdisposisi = params[15];
            String flag = params[16];
            String idJenisDokumen = params[17];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DISPEMPID, dispEmpId));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DISPTIME, disptime));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ISMON, isMonitor));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ISHARD, isHardcpy));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ISIDIS, isiDisposisi));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_STATID, statid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_INBOXID, inboxid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, docid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCREG, docregid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_LISTREC, recipients));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_LISTFILE, files));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VARIABLETAMBAHAN, flag));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PERSEDIAAN, idJenisDokumen));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "KirimDisposisi.php");
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
               // pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                String flags = "";
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");
                    flags = result.getString("FlagKodeError");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class kirimDisposisi extends AsyncTask<String, Void, JSONObject> {

        String indexdisposisi;
        Dialog pdialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = ProgressDialog.show(pdis.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;
            String dispEmpId = params[0];
            String disptime = params[1];
            String isMonitor = params[2];
            String isHardcpy = params[3];
            String isiDisposisi = params[4];
            String empid = params[5];
            String roleid = params[6];
            String unitid = params[7];
            String statid = params[8];
            String inboxid = params[9];
            String docid = params[10];
            String docregid = params[11];
            String recipients = params[12];
            String files = params[13];
            String key = params[14];
            indexdisposisi = params[15];
            String flag = params[16];
            String idjenisdok = params[17];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DISPEMPID, dispEmpId));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DISPTIME, disptime));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ISMON, isMonitor));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ISHARD, isHardcpy));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ISIDIS, isiDisposisi));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_STATID, statid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_INBOXID, inboxid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, docid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCREG, docregid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_LISTREC, recipients));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_LISTFILE, files));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VARIABLETAMBAHAN, flag));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PERSEDIAAN, idjenisdok));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "KirimDisposisi.php");

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
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            pdialog.dismiss();
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                String flags = "";
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");
                    flags = result.getString("FlagKodeError");

                    if(flags.equals("0")) {
                        if (kode.equals("1")) {
                            String title = Config.alertSuccess;
                            showAlertToast(keterangan, title, "disposisi");
                        } else {
                            String title = Config.alertError;
                            showAlertToast(keterangan, title, "disposisi");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(Integer.valueOf(indexdisposisi)+1==disposisiList.size()){
                    pdialog.dismiss();
                    for(int i=0;i<disposisiList.size();i++){
                        int flag=1;
                        kirimdisposisi(i, flag);
                    }
                }
            }
            else
            {
                pdialog.dismiss();
            }
        }
    }

    public void showAlertToast(final String pesan, final String title, final String status)
    {
        if(status.equals("KoneksiTambahPenerima")){
            Toast.makeText(pdis.this, pesan,
                    Toast.LENGTH_LONG).show();
        }else if(status.equals("disposisi")){
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(pdis.this, pesan,
                            Toast.LENGTH_LONG).show();
                    if (title.equals(Config.alertSuccess)) {
                        Intent pindah = new Intent(pdis.this, inb.class);
                        pindah.putExtra("fragment", fragment);
                        startActivity(pindah);
                    } else {
                        //prosesdisposisi.dismiss();
                        onBackPressed();
                    }
                }
            });

        }else if(status.equals("upload")) {
            Toast.makeText(pdis.this, pesan,
                    Toast.LENGTH_LONG).show();
            if (title.equals(Config.alertWarning)) {
                prosesdisposisi.dismiss();
                onBackPressed();
            }
        }else if(status.equals("delete")){
            Toast.makeText(pdis.this, pesan,
                    Toast.LENGTH_LONG).show();

            dialogAlert=false;
            fileAdapter = new FileAdapter(pdis.this, attachmentList);
            listView.setAdapter(fileAdapter);
            if (attachmentList.size() > 1) {
                daftarFile.setText("( " + attachmentList.size() + " Lampiran )");
            } else {
                daftarFile.setText("( " + attachmentList.size() + " Lampiran )");
            }
        }
        else if(status.equals("UploadLandingPage")){
            this.runOnUiThread(new Runnable() {
               public void run() {
                   Toast.makeText(pdis.this, pesan,
                           Toast.LENGTH_LONG).show();
                   delFile.dismiss();
                   dialogAlert = false;
                   if (title.equals(Config.alertSuccess)) {
                       if (attachmentList.size() > 1) {
                           daftarFile.setText("( " + attachmentList.size() + " Lampiran )");
                       } else {
                           daftarFile.setText("( " + attachmentList.size() + " Lampiran )");
                       }
                   } else if (title.equals(Config.alertError)) {

                   }
               }
           });

        }
    }
    public void showAlert(final String pesan, final String title, final String status) {
        pdis.this.runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                final AlertDialog.Builder builder = new AlertDialog.Builder(pdis.this);
                if(status.equals("warningDelete")){
                    builder.setMessage(pesan).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            //deletefile proses
                            dialogAlert = false;
                            for (int i = positionfilesdel.size() - 1; i >= 0; i--) {
                                int a = positionfilesdel.get(i);
                                positionfilesdel.remove(i);
                                attachmentList.remove(a);
                            }
                            if (attachmentList.size() > 1) {
                                daftarFile.setText("( " + attachmentList.size() + " Lampiran )");
                            } else {
                                daftarFile.setText("( " + attachmentList.size() + " Lampiran )");
                            }

                            fileAdapter = new FileAdapter(pdis.this, attachmentList);
                            listView.setAdapter(fileAdapter);

                            if(attachmentList.isEmpty())
                            {
                                btdelete.setVisibility(View.INVISIBLE);
                                btsubmit.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Overridef
                        public void onClick(DialogInterface dialog, int which) {
                            dialogAlert = false;
                            idfilesdel.clear();
                        }
                    });
                }
                else if(status.equals("UploadLandingPage")){
                    builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            delFile.dismiss();
                            dialogAlert = false;
                            if (title.equals("Success")) {
                                if (attachmentList.size() > 1) {
                                    daftarFile.setText("( " + attachmentList.size() + " Files )");
                                } else {
                                    daftarFile.setText("( " + attachmentList.size() + " File )");
                                }
                            } else if (title.equals("Error")) {

                            }
                        }
                    });
                }

                if(dialogAlert == false) {
                    dialogAlert = true;
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        });
    }
    public void dialogFiles() {
        delFile = new Dialog(pdis.this);
        delFile.requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        double screenInches = pf.getScreenInches(this);

        if (screenInches <= 4.0) {
            delFile.setContentView(R.layout.dialog_edit_file);
        } else if (screenInches > 4.0 && screenInches <= 5.0) {
            delFile.setContentView(R.layout.dialog_edit_file);
        } else if (screenInches > 5.0 && screenInches <= 6.0) {
            delFile.setContentView(R.layout.dialog_edit_file);
        } else if (screenInches > 6.0 && screenInches <= 7.0) {
            delFile.setContentView(R.layout.dialog_edit_file_tujuh);
        } else if (screenInches > 7.0 && screenInches <= 8.0) {
            delFile.setContentView(R.layout.dialog_edit_file_tujuh);
        } else if (screenInches > 8.0 && screenInches <= 9.0) {
            delFile.setContentView(R.layout.dialog_edit_file_tujuh);
        } else if (screenInches > 9.0 && screenInches <= 10.0) {
            delFile.setContentView(R.layout.dialog_edit_file_tujuh);
        } else if (screenInches > 10.0 && screenInches <= 11.0) {
            delFile.setContentView(R.layout.dialog_edit_file_tujuh);
        }


        btsubmit = (LinearLayout)delFile.findViewById(R.id.btSubmit);
        btdelete = (LinearLayout)delFile.findViewById(R.id.btDelete);
        btnupload = (LinearLayout)delFile.findViewById(R.id.btUploads);

        if(attachmentList.isEmpty()) {
            btdelete.setVisibility(View.INVISIBLE);
            btsubmit.setVisibility(View.INVISIBLE);
        }

        listView = (ListView) delFile.findViewById(R.id.lvDaftarPenerima);
        fileAdapter = new FileAdapter(pdis.this, attachmentList);
        listView.setAdapter(fileAdapter);

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browse = new Intent(pdis.this, fc.class);
                startActivityForResult(browse, REQUEST_PATH);
                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 1);*/
            }
        });

        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sizeFileUpload=0;
                final ArrayList<attachment> upload = new ArrayList<attachment>();
                final ArrayList<Integer> positionfile = new ArrayList<Integer>();
                for(int i=0; i<attachmentList.size();i++){
                    if(attachmentList.get(i).getInLandingPage()==false){
                        sizeFileUpload++;
                        upload.add(attachmentList.get(i));
                        positionfile.add(i);
                    }
                }

                if (sizeFileUpload>0) {

                    upLanding = ProgressDialog.show(pdis.this, null, "Loading . . .");
                    final int size = sizeFileUpload;
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            for (int i = size-1; i >= 0 ; i--) {
                                response.add(uploadFiles(upload.get(i).getPathFile(), upload.get(i).getNamaFile(), positionfile.get(i)));
                            }
                            upLanding.dismiss();
                            String error = "";
                            for(int i=0;i<response.size();i++){
                                if(response.get(i)!=200){
                                    if(i==response.size()-1) {
                                        error += upload.get(i).getNamaFile()+ " ";
                                    }else{
                                        error += upload.get(i).getNamaFile() + ", ";
                                    }
                                }
                            }

                            if(error==""){
                                showAlertToast("Unggah lampiran berhasil", Config.alertSuccess, "UploadLandingPage");
                            }else{
                                showAlertToast("Unggah lampiran " + error + " gagal", Config.alertError, "UploadLandingPage");
                            }
                        }
                    };
                    mThread.start();



                } else {
                    delFile.dismiss();
                }
            }
        });

        btdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idfilesdel.clear();
                positionfilesdel.clear();
                for (int i = 0; i < attachmentList.size(); i++) {
                    String isSelect = attachmentList.get(i).getSelected().toString();
                    if (isSelect.equals("true")) {
                        positionfilesdel.add(i);
                    }
                }
                if (!positionfilesdel.isEmpty()) {
                    showAlert(Config.warningDelete, Config.alertWarning, "warningDelete");
                }
            }
        });

        delFile.show();
        Window window = delFile.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onBackPressed() {

        for(int i=0;i<namafile.size();i++){
            cd=new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            if(isInternetPresent) {
                DeleteFileFromLandingPage del = new DeleteFileFromLandingPage();
                del.execute(empid+namafile.get(i));
            }else{
                pf.showAlertConnection(this);
            }

        }
        finish();
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
                // home action
                Intent i = new Intent(pdis.this, inb.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        if(resultCode!=0){
            /*String PathHolder = data.getData().getPath();

            Toast.makeText(pdis.this, PathHolder , Toast.LENGTH_LONG).show();*/
            String curFileName = data.getStringExtra("GetFileName");
            String curFilePath= data.getStringExtra("GetPath");

            attachment att = new attachment();
            att.setNamaFile(curFileName);
            att.setPathFile(curFilePath+"/"+curFileName);
            att.setSelected(false);
            att.setInLandingPage(false);
            attachmentList.add(att);

            fileAdapter = new FileAdapter(pdis.this, attachmentList);
            listView.setAdapter(fileAdapter);
        }

        if(!attachmentList.isEmpty()) {
            btdelete.setVisibility(View.VISIBLE);
            btsubmit.setVisibility(View.VISIBLE);
        }
    }

    private int uploadFiles(String pathfile, String namefile, int position) {
        HttpURLConnection conn = null;
        String boundary = "*****";
        DataOutputStream dos = null;
        String twoHyphens = "--";
        String lineEnd = "\r\n";
        int bytesRead, bytesAvailable, bufferSize;
        int maxBufferSize = 1 * 1024 * 1024;
        byte[] buffer;

        String file = pathfile;


        File upload_file = new File(file);
        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(upload_file);
            URL url = new URL(Config.urlLanding+"Upload_File.php");
            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", curFileName);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data;" + "name=\"uploaded_file\";" + "filename=\"" + empid + namefile + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            if(serverResponseCode != 200){
                attachmentList.remove(position);
            }else{
                attachmentList.get(position).setInLandingPage(true);
                namafile.add(attachmentList.get(position).getNamaFile());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverResponseCode;
    }

    class uploadFile extends AsyncTask<String, Void, JSONObject> {

        String namafile;
        String indexDisposisi, indexfile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prosesdisposisi = ProgressDialog.show(pdis.this, null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String docid = params[0];
            String dispID = params[1];
            namafile = params[2];
            String uploader=params[3];
            indexDisposisi = params[4];
            indexfile = params[5];

            String endocid="",endispid="",enfilename="",enempid="",enkey="";
            mCrypt mCrypt = new mCrypt();
            try {
                endocid = mCrypt.bytesToHex(mCrypt.encrypt(docid));
                endispid = mCrypt.bytesToHex(mCrypt.encrypt(dispID));
                enfilename = mCrypt.bytesToHex(mCrypt.encrypt(namafile));
                enempid = mCrypt.bytesToHex(mCrypt.encrypt(uploader));
                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, endocid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DISPID, endispid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_NAMAFILE, enfilename));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UPLOADER, enempid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, enkey));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost httpPost = new HttpPost(Config.url + "UploadDocFile");
                HttpPost httpPost = new HttpPost(Config.urlLanding + "UploadDocFile.php");
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
                Log.d("responce",responce);

                return new JSONObject(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            prosesdisposisi.dismiss();
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            prosesdisposisi.dismiss();
            if (result != null) {
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");
                    String fileid = result.getString("FileID");
                    if(kode.equals("1")){
                        //uploadSukses = keterangan;
                        files.add(fileid);

                        int indexdisposisi = Integer.valueOf(indexDisposisi);

                        if(disposisiList.get(indexdisposisi).getListfile().equals("")) {
                            disposisiList.get(indexdisposisi).setListfile(fileid);
                        }else{
                            disposisiList.get(indexdisposisi).setListfile(","+fileid);
                        }

                        cd=new ConnectionDetector(getApplicationContext());
                        isInternetPresent = cd.isConnectingToInternet();

                        if(isInternetPresent) {
                            DeleteFileFromLandingPage del = new DeleteFileFromLandingPage();
                            del.execute(empid+namafile);
                        }else{
                            pf.showAlertConnection(pdis.this);
                        }

                    } else {
                        //showAlert("Your Connection is not stable. Please check your disposition.", "Warning", "Upload");
                        showAlertToast(keterangan, Config.alertWarning, "Upload");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(sizefile.get(Integer.valueOf(indexDisposisi)) == Integer.valueOf(indexfile)){
                    int flag = 0;
                    kirimdisposisi(Integer.valueOf(indexDisposisi), flag);
                }
            }
        }
    }

    class DeleteFileFromLandingPage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String namaFile = params[0];
            String ennamafile = "";

            mCrypt mCrypt = new mCrypt();
            try {
                enkey = mCrypt.bytesToHex(mCrypt.encrypt(Config.api_key));
                ennamafile = mCrypt.bytesToHex(mCrypt.encrypt(namaFile));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String responce;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, enkey));
            nameValuePairs.add(new BasicNameValuePair("av",ennamafile));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "DeleteFileFromLandingPage.php");
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

                return new String(responce);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                //pf.showAlertTimeOut(pdis.this);
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            pf.showAlertTimeOut(pdis.this);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                String success = result.trim();
            }
        }
    }

    public class modelDisposisiAdapter extends RecyclerView.Adapter<modelDisposisiAdapter.ViewHolder> {

        private ArrayList<modelDisposisi> disposisiList;
        private Activity activity;

        PublicFunction pf = new PublicFunction();

        public modelDisposisiAdapter(ArrayList<modelDisposisi> disposisiList, Activity activity){
            this.disposisiList = disposisiList;
            this.activity = activity;
        }

        @Override
        public modelDisposisiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            double screenInches = pf.getScreenInches(activity);
            if(screenInches<=4.1){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi,parent,false);
            }else if(screenInches>4.0&&screenInches<=5.0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi,parent,false);
            }else if(screenInches>5.0&&screenInches<=6.0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi,parent,false);
            }else if(screenInches>6.0&&screenInches<=7.0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi_tujuh,parent,false);
            }else if(screenInches>7.0&&screenInches<=8.0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi_tujuh,parent,false);
            }else if(screenInches>8.0&&screenInches<=9.0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi_tujuh,parent,false);
            }else if(screenInches>9.0&&screenInches<=10.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi_tujuh,parent,false);
            }else if(screenInches>10.0&&screenInches<=11.0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_disposisi_tujuh,parent,false);
            }


            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(modelDisposisiAdapter.ViewHolder holder, int position) {
            String namapenerima = disposisiList.get(position).getListnamapenerima();
            String isidisposisi = disposisiList.get(position).getIsidisposisi();

            holder.namapenerima.setText(namapenerima);
            holder.isidisposisi.setText(isidisposisi);
        }

        @Override
        public int getItemCount() {
            return disposisiList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView namapenerima, isidisposisi;
            private ImageView delete,edit;

            public ViewHolder(View view) {
                super(view);

                namapenerima = (TextView)view.findViewById(R.id.PenerimaDisposisi);
                isidisposisi = (TextView)view.findViewById(R.id.IsiDisposisi);
                delete = (ImageView)view.findViewById(R.id.btDeletedis);
                //edit = (ImageView)view.findViewById(R.id.btEditdis);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlert("Anda yakin menghapus data ini?","Warning",getPosition());
                    }
                });
                /*edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAddDisposisi(getPosition());
                    }
                });*/
            }

        }

        public void showAlert(final String pesan, final String title, final int pos) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(pesan).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            disposisiList.remove(pos);
                            ((pdis) activity).recyclerView.setAdapter(((pdis) activity).adapter);
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
    }
}
