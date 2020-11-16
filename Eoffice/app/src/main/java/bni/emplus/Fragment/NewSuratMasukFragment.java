package bni.emplus.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import bni.emplus.Adapter.FileAdapter;
import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
import bni.emplus.Model.attachment;
import bni.emplus.Model.modelJenisDokumen;
import bni.emplus.PublicFunction;
import bni.emplus.UserSessionManager;
import bni.emplus.fc;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ZulfahPermataIlliyin on 9/29/2016.
 */
public class NewSuratMasukFragment extends Fragment {
    Boolean dialogConnection=false;
    String curFileName,curFilePath,picname;
    File file;
    String uploadFileName="image.jpg" ;

    int serverResponseCode = 0;

    Dialog delFile, upLanding, prosesinput;
    LinearLayout btsubmit, btdelete, btnupload;
    String[] statusUpload;

    EditText tempatSuratMasuk, tanggalSuratMasuk, noSuratMasuk, kepadaSuratMasuk, ccSuratMasuk, nmSuratMasuk, perihalSuratMasuk;
    EditText tanggalRegisterSuratMasuk, catatanSuratMasuk, dariSuratMasuk;
    Spinner spdariSuratMasuk, spprioritasSuratMasuk, spklasifikasiSuratMasuk, sptipeDokumenSuratMasuk, spJenisDokumen;
    RelativeLayout rlJenisDokumen;
    ImageView tanggalDokumen, tanggalRegister;
    Button Submit,delete;
    String tanggal_serv;

    Calendar myCalendar, myCalendarReg;

    ArrayAdapter<String> adapterTipe, adapterDari, adapterPrioritas, adapterKlasifikasi, adapterJenisDokumen;
    ArrayList<String> tipeDok = new ArrayList<>();
    ArrayList<String> dari = new ArrayList<>();
    ArrayList<String> prior = new ArrayList<>();
    ArrayList<String> classification = new ArrayList<>();
    ArrayList<String> jenisDokumen = new ArrayList<>();
    ArrayList<HashMap<String,String>> listDari = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> listDokumen = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> listPrioritas = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> listKlasifikasi = new ArrayList<HashMap<String,String>>();
    ArrayList<String> idfile = new ArrayList<String>();
    ArrayList<Integer> response = new ArrayList<>();
    ArrayList<modelJenisDokumen> listJenisDokumen = new ArrayList<>();
    public ArrayList<String> namafile = new ArrayList<>();

    UserSessionManager session;
    String unitid,roleid,empid,statid,type,appid,listException,status, npp;
    String entype,enapp,enlist,enkey,enlistfile,enrole,enunit,enisdispo,enempdocid;
    String docid,idspdari,namaspdari,klasifikasi,prioritas,idfiles,tipeDokumen,idDokumen, idJenisDokumen = "0",usingSla;

    private static final String TAG_Value = "Value";
    private static final String TAG_Name = "Name";
    private static final String TAG_ID = "Id";
    private static final String TAG_NameRec = "Name";
    private static final String TAG_KODE = "Kode";
    private static final String TAG_KET = "Keterangan";

    private List<attachment> attachmentList = new ArrayList<>();
    TextView daftarFile;
    ImageView edit;

    private FileAdapter fileAdapter;
    private ListView listView;
    private ArrayList<String> idfilesdel = new ArrayList<String>();
    private ArrayList<Integer> positionfilesdel = new ArrayList<Integer>();
    private int spinner,layout,spinItem;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    mCrypt krip = new mCrypt();

    PublicFunction pf = new PublicFunction();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        double screenInches = pf.getScreenInches(getActivity());
        if(screenInches<=4.0){
            layout = R.layout.inputsuratmasuk;
            spinner = R.layout.spinner_item;
            spinItem =android.R.layout.simple_spinner_dropdown_item;
        }else if(screenInches>4.0&&screenInches<=5.0){
            layout = R.layout.inputsuratmasuk;
            spinner = R.layout.spinner_item;
            spinItem =android.R.layout.simple_spinner_dropdown_item;
        }else if(screenInches>5.0&&screenInches<=6.0){
            layout = R.layout.inputsuratmasuk;
            spinner = R.layout.spinner_item;
            spinItem =android.R.layout.simple_spinner_dropdown_item;
        }else if(screenInches>6.0&&screenInches<=7.0){
            layout = R.layout.inputsuratmasuk_tujuh;
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>7.0&&screenInches<=8.0){
            layout = R.layout.inputsuratmasuk_tujuh;
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>8.0&&screenInches<=9.0){
            layout = R.layout.inputsuratmasuk_tujuh;
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>9.0&&screenInches<=10.0) {
            layout = R.layout.inputsuratmasuk_tujuh;
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }else if(screenInches>10.0&&screenInches<=11.0){
            layout = R.layout.inputsuratmasuk_tujuh;
            spinner = R.layout.spinner_item_tujuh;
            spinItem = R.layout.spinner_drop_item;
        }
        View v=inflater.inflate(layout, container, false);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args != null) {
            docid = args.getString("docid");
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
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

        //get npp
        npp = user.get(UserSessionManager.KEY_NAME);
        // get roleid,unitid
        unitid = userRole.get(UserSessionManager.KEY_UNIT);
        roleid = userRole.get(UserSessionManager.KEY_ROLE);
        empid = user.get(UserSessionManager.KEY_EMPID);
        statid = userRole.get(UserSessionManager.KEY_STATID);

        sptipeDokumenSuratMasuk = (Spinner)v.findViewById(R.id.spTipeDokumen);
        spdariSuratMasuk = (Spinner)v.findViewById(R.id.spDari);
        spprioritasSuratMasuk = (Spinner)v.findViewById(R.id.spPrioritasDokumen);
        spklasifikasiSuratMasuk = (Spinner)v.findViewById(R.id.spKlasifikasiDokumen);
        spJenisDokumen = (Spinner)v.findViewById(R.id.spJenisDokumen);

        rlJenisDokumen = (RelativeLayout)v.findViewById(R.id.rlJenisDokumen);

        dariSuratMasuk = (EditText)v.findViewById(R.id.tvIsiDariDokumen);

        tanggalRegisterSuratMasuk = (EditText)v.findViewById(R.id.editIsiTanggalRegister);
        tanggalSuratMasuk = (EditText)v.findViewById(R.id.editIsiTanggalSurat);
        tanggalDokumen = (ImageView)v.findViewById(R.id.ivKalenderDokumen);
        tanggalRegister = (ImageView)v.findViewById(R.id.ivKalenderRegister);

        Submit = (Button)v.findViewById(R.id.btSubmit);

        tempatSuratMasuk = (EditText)v.findViewById(R.id.editIsiTempatSurat);
        noSuratMasuk = (EditText)v.findViewById(R.id.editIsiNomorDokumen);
        kepadaSuratMasuk = (EditText)v.findViewById(R.id.editIsiKepadaDokumen);
        ccSuratMasuk = (EditText)v.findViewById(R.id.editIsiCCDokumen);
        nmSuratMasuk = (EditText)v.findViewById(R.id.editIsiNmDokumen);
        perihalSuratMasuk = (EditText)v.findViewById(R.id.editIsiPerihalDokumen);
        catatanSuratMasuk = (EditText)v.findViewById(R.id.editIsiCatatanDokumen);

        daftarFile = (TextView) v.findViewById(R.id.tvIsiAttachDokumenMasuk);
        edit = (ImageView) v.findViewById(R.id.ivEditAttachDokumenMasuk);

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            ambilTipe();
        }else {
            showAlertConnection();
        }

        adapterTipe = new ArrayAdapter<String>(getActivity(), spinner, tipeDok);
        adapterTipe.setDropDownViewResource(spinItem);
        sptipeDokumenSuratMasuk.setAdapter(adapterTipe);

        sptipeDokumenSuratMasuk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!listDokumen.isEmpty()) {
                    tipeDokumen = listDokumen.get(position).get(TAG_Name);
                    idDokumen = listDokumen.get(position).get(TAG_Value);

                    if (tipeDokumen.equals("Memo")) {
                        if (dariSuratMasuk.getVisibility() == View.VISIBLE) {
                            dariSuratMasuk.setVisibility(View.INVISIBLE);
                            spdariSuratMasuk.setVisibility(View.VISIBLE);
                        }

                        dari.clear();
                        listDari.clear();

                        cd=new ConnectionDetector(getContext());
                        isInternetPresent = cd.isConnectingToInternet();
                        if(isInternetPresent) {
                            ambilRecipients ambil = new ambilRecipients();
                            ambil.execute("1", "1", idDokumen, roleid, unitid);
                        }else{
                            showAlertConnection();
                        }
                        adapterDari = new ArrayAdapter<String>(getActivity(), spinner, dari);
                        adapterDari.setDropDownViewResource(spinItem);
                        spdariSuratMasuk.setAdapter(adapterDari);
                    } else if (tipeDokumen.equals("Nota Intern")) {
                        if (dariSuratMasuk.getVisibility() == View.VISIBLE) {
                            dariSuratMasuk.setVisibility(View.INVISIBLE);
                            spdariSuratMasuk.setVisibility(View.VISIBLE);
                        }

                        dari.clear();
                        listDari.clear();

                        cd=new ConnectionDetector(getContext());
                        isInternetPresent = cd.isConnectingToInternet();
                        if(isInternetPresent) {
                            ambilRecipients ambil = new ambilRecipients();
                            ambil.execute("1", "1", idDokumen, roleid, unitid);
                        }else{
                            showAlertConnection();
                        }

                        adapterDari = new ArrayAdapter<String>(getActivity(), spinner, dari);
                        adapterDari.setDropDownViewResource(spinItem);
                        spdariSuratMasuk.setAdapter(adapterDari);
                    } else {
                        spdariSuratMasuk.setVisibility(View.INVISIBLE);
                        dariSuratMasuk.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spdariSuratMasuk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idspdari = listDari.get(position).get(TAG_ID);
                namaspdari = listDari.get(position).get(TAG_NameRec);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFiles();
            }
        });

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            ambilPrioritas();
        }else {
            showAlertConnection();
        }

        adapterJenisDokumen = new ArrayAdapter<String>(getActivity(),spinner,jenisDokumen);
        adapterJenisDokumen.setDropDownViewResource(spinItem);
        spJenisDokumen.setAdapter(adapterJenisDokumen);

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            ambilJenisDokumen();
        }else{
            showAlertConnection();
        }

        spJenisDokumen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idJenisDokumen = listJenisDokumen.get(position).getIdJenisDokumen();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        adapterPrioritas = new ArrayAdapter<String>(getActivity(), spinner, prior);
        adapterPrioritas.setDropDownViewResource(spinItem);
        spprioritasSuratMasuk.setAdapter(adapterPrioritas);

        spprioritasSuratMasuk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prioritas = listPrioritas.get(position).get(TAG_Value);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cd=new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            ambilKlasifikasi();
        }else{
            showAlertConnection();
        }

        adapterKlasifikasi = new ArrayAdapter<String>(getActivity(), spinner, classification);
        adapterKlasifikasi.setDropDownViewResource(spinItem);
        spklasifikasiSuratMasuk.setAdapter(adapterKlasifikasi);

        spklasifikasiSuratMasuk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                klasifikasi = listKlasifikasi.get(position).get(TAG_Value);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        myCalendar = Calendar.getInstance(TimeZone.getDefault());
        myCalendarReg = Calendar.getInstance(TimeZone.getDefault());

        if(isInternetPresent){
            ambilTanggal();
        }else{
            showAlertConnection();
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth,
                                  int mDay) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, mYear);
                myCalendar.set(Calendar.MONTH, mMonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                updateTanggal();
            }

        };
        final DatePickerDialog.OnDateSetListener dateRegister = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth,
                                  int mDay) {
                // TODO Auto-generated method stub
                myCalendarReg.set(Calendar.YEAR, mYear);
                myCalendarReg.set(Calendar.MONTH, mMonth);
                myCalendarReg.set(Calendar.DAY_OF_MONTH, mDay);
                updateTanggalRegister();
            }

        };

        tanggalSuratMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tanggalDokumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tanggalRegisterSuratMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateRegister, myCalendarReg.get(Calendar.YEAR), myCalendarReg.get(Calendar.MONTH), myCalendarReg.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tanggalRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateRegister, myCalendarReg.get(Calendar.YEAR), myCalendarReg.get(Calendar.MONTH), myCalendarReg.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View focusView = null;
                TextView errorText;
                //reset error
                tempatSuratMasuk.setError(null);
                noSuratMasuk.setError(null);
                kepadaSuratMasuk.setError(null);
                perihalSuratMasuk.setError(null);

                /*if(idJenisDokumen.equals("0") && usingSla.equals("1")){
                    errorText = (TextView)spJenisDokumen.getSelectedView();
                    hideKeyboard();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("jenis dokumen harus dipilih");//changes the selected item text to this
                    focusView = spJenisDokumen;
                    focusView.requestFocus();
                    focusView.performClick();
                }else */
                if (tempatSuratMasuk.length() == 0) {
                    tempatSuratMasuk.setError("Tempat tidak boleh kosong");
                    focusView = tempatSuratMasuk;
                    focusView.requestFocus();
                }
                else if(noSuratMasuk.length()==0){
                    noSuratMasuk.setError("No Dokumen/Surat tidak boleh kosong");
                    focusView = noSuratMasuk;
                    focusView.requestFocus();
                }else if(kepadaSuratMasuk.length()==0){
                    kepadaSuratMasuk.setError("Kepada tidak boleh kosong");
                    focusView = kepadaSuratMasuk;
                    focusView.requestFocus();
                }else if(perihalSuratMasuk.length()==0){
                    perihalSuratMasuk.setError("perihal tidak boleh kosong");
                    focusView = perihalSuratMasuk;
                    focusView.requestFocus();
                }else if(prioritas.equals("0")){
                    errorText = (TextView)spprioritasSuratMasuk.getSelectedView();
                    hideKeyboard();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("prioritas harus dipilih");//changes the selected item text to this
                    focusView = spprioritasSuratMasuk;
                    focusView.requestFocus();
                    focusView.performClick();
                }else if(klasifikasi.equals("0")){
                    errorText = (TextView)spklasifikasiSuratMasuk.getSelectedView();
                    hideKeyboard();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("klasifikasi harus dipilih");//changes the selected item text to this
                    focusView = spklasifikasiSuratMasuk;
                    focusView.requestFocus();
                    focusView.performClick();
                }else {
                    if(attachmentList.isEmpty()){
                        inputSuratMasuk();
                    }else {
                        for (int i = 0; i < attachmentList.size(); i++) {
                            cd = new ConnectionDetector(getActivity().getApplicationContext());
                            isInternetPresent = cd.isConnectingToInternet();
                            if (isInternetPresent && attachmentList.get(i).getInLandingPage() == true) {
                                String dispid = "0";

                                uploadFile upload = new uploadFile();
                                upload.execute(docid, dispid, attachmentList.get(i).getNamaFile(), empid, String.valueOf(i + 1));
                            } else {
                                pf.showAlertConnection(getActivity());
                            }
                        }
                    }
                }
            }
        });

        return v;
    }

    private void inputSuratMasuk(){

        String perihal ="";
        String dari = "";
        String address = "";
        String spdaridok = "";

        if(!idDokumen.equals("3")){
            spdaridok = idspdari;
            dari = namaspdari;
        }else{
            spdaridok = unitid;
            dari = dariSuratMasuk.getText().toString();
        }

        perihal = perihalSuratMasuk.getText().toString();
        address = tempatSuratMasuk.getText().toString();
        String releasedate = tanggalSuratMasuk.getText().toString()+"T00:00:00.000000+07:00";
        String rec_time = tanggalRegisterSuratMasuk.getText().toString()+"T00:00:00";

        String to = "";
        String cc = "";
        String nm = "";
        String content = "";
        String notes = "";
        String docno = "";

        notes = catatanSuratMasuk.getText().toString();
        to = kepadaSuratMasuk.getText().toString();
        cc = ccSuratMasuk.getText().toString();
        nm = nmSuratMasuk.getText().toString();
        docno = noSuratMasuk.getText().toString();

        idfiles = TextUtils.join(",", idfile);
        String penerima = "";

        String enempid="",ennpp="",enrole="",enunit="",enstat="",enspdari="",eniddok="",enprior="",enklas="",enperihal="",endari="",
                encontent="",enaddress="",enrelease="",enrectime="",ento="",encc="",ennm="",ennote="",enidfile="",enpenerima="",endocno="",
                endocid="",enjenisdok="";
        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            mCrypt krip = new mCrypt();
            try {
                enempid = krip.bytesToHex(krip.encrypt(empid));
                ennpp = krip.bytesToHex(krip.encrypt(npp));
                enrole = krip.bytesToHex(krip.encrypt(roleid));
                enunit = krip.bytesToHex(krip.encrypt(unitid));
                enstat = krip.bytesToHex(krip.encrypt(statid));
                enspdari = krip.bytesToHex(krip.encrypt(spdaridok));
                eniddok = krip.bytesToHex(krip.encrypt(idDokumen));
                enprior = krip.bytesToHex(krip.encrypt(prioritas));
                enklas = krip.bytesToHex(krip.encrypt(klasifikasi));


                if (perihal.isEmpty() || perihal.equals("")) {
                    enperihal = perihal;
                } else {
                    enperihal = krip.bytesToHex(krip.encrypt(perihal));
                }
                if (dari.isEmpty() || dari.equals("")) {
                    endari = dari;
                } else {
                    endari = krip.bytesToHex(krip.encrypt(dari));
                }
                if (content.isEmpty() || content.equals("")) {
                    encontent = content;
                } else {
                    encontent = krip.bytesToHex(krip.encrypt(content));
                }
                if (address.equals("") || address.isEmpty()) {
                    enaddress = address;
                } else {
                    enaddress = krip.bytesToHex(krip.encrypt(address));
                }
                enrelease = krip.bytesToHex(krip.encrypt(releasedate));
                enrectime = krip.bytesToHex(krip.encrypt(rec_time));

                if (to.equals("") || to.isEmpty()) {
                    ento = to;
                } else {
                    ento = krip.bytesToHex(krip.encrypt(to));
                }
                if (cc.equals("") || cc.isEmpty()) {
                    encc = cc;
                } else {
                    encc = krip.bytesToHex(krip.encrypt(cc));
                }
                if (nm.equals("") || nm.isEmpty()) {
                    ennm = nm;
                } else {
                    ennm = krip.bytesToHex(krip.encrypt(nm));
                }
                if (notes.equals("") || notes.isEmpty()) {
                    ennote = notes;
                } else {
                    ennote = krip.bytesToHex(krip.encrypt(notes));
                }
                if (idfiles.equals("") || idfiles.isEmpty()) {
                    enidfile = idfiles;
                } else {
                    enidfile = krip.bytesToHex(krip.encrypt(idfiles));
                }
                if (penerima.isEmpty() || penerima.equals("")) {
                    enpenerima = penerima;
                } else {
                    enpenerima = krip.bytesToHex(krip.encrypt(penerima));
                }
                if (docno.equals("") || docno.isEmpty()) {
                    endocno = docno;
                } else {
                    endocno = krip.bytesToHex(krip.encrypt(docno));
                }
                endocid = krip.bytesToHex(krip.encrypt(docid));
                enjenisdok = krip.bytesToHex(krip.encrypt(idJenisDokumen));
                enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
            } catch (Exception e) {
                e.printStackTrace();
            }

            inputSuratMasuk input = new inputSuratMasuk();
            input.execute(enempid, ennpp, enrole, enunit, enstat, enspdari, eniddok, enprior, enklas, enperihal, endari, encontent, enaddress, enrelease, enrectime, ento, encc, ennm, ennote, enidfile, enpenerima, endocno, endocid, enjenisdok, enkey);
        }else{
            pf.showAlertConnection(getActivity());
        }

    }

    public void ambilTipe(){
        type = "DocType";
        appid = "4";
        listException = "4";
        status = "1";

        try {
            entype = krip.bytesToHex(krip.encrypt(type));
            enapp = krip.bytesToHex(krip.encrypt(appid));
            if (listException.isEmpty()) {
                enlist = listException;
            } else {
                enlist = krip.bytesToHex(krip.encrypt(listException));
            }
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilIsiSpinner ambilTipe = new ambilIsiSpinner();
        ambilTipe.execute(entype, enapp, enlist, status, enkey);
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
    public void ambilPrioritas(){
        type = "DocPriority";
        appid = "4";

        listException = "";
        status = "2";

        krip = new mCrypt();
        try {
            entype = krip.bytesToHex(krip.encrypt(type));
            enapp = krip.bytesToHex(krip.encrypt(appid));
            if (listException.isEmpty()) {
                enlist = listException;
            } else {
                enlist = krip.bytesToHex(krip.encrypt(listException));
            }
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilIsiSpinner ambilPrioritas = new ambilIsiSpinner();
        ambilPrioritas.execute(entype, enapp, enlist, status, enkey);
    }

    public void ambilKlasifikasi(){
        type = "DocClassification";
        appid = "4";
        listException = "";
        status = "3";
        krip = new mCrypt();
        try {
            entype = krip.bytesToHex(krip.encrypt(type));
            enapp = krip.bytesToHex(krip.encrypt(appid));
            if (listException.isEmpty()) {
                enlist = listException;
            } else {
                enlist = krip.bytesToHex(krip.encrypt(listException));
            }
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilIsiSpinner ambilKlasifikasi = new ambilIsiSpinner();
        ambilKlasifikasi.execute(entype, enapp, enlist, status, enkey);
    }

    public void ambilJenisDokumen(){
        int isDisposisi = 0; //bukan disposisi
        krip = new mCrypt();
        try {
            enrole = krip.bytesToHex(krip.encrypt(roleid));
            enunit = krip.bytesToHex(krip.encrypt(unitid));
            enisdispo = krip.bytesToHex(krip.encrypt(String.valueOf(isDisposisi)));
            enempdocid = krip.bytesToHex(krip.encrypt(docid));
            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        getJenisDokumen getJenisDokumen = new getJenisDokumen();
        getJenisDokumen.execute(enrole, enunit, enisdispo, enempdocid, enkey);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.attach_item, menu);
        MenuItem item = menu.findItem(R.id.upload_button);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void updateTanggal(){

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tanggalSuratMasuk.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateTanggalRegister(){

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tanggalRegisterSuratMasuk.setText(sdf.format(myCalendarReg.getTime()));
    }

    class getServerDate extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
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
            pf.showAlertTimeOut(getActivity());
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        tanggal_serv = e.getString("Server_Tgl2");

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                tanggalSuratMasuk.setText(tanggal_serv);
                tanggalRegisterSuratMasuk.setText(tanggal_serv);
            }
        }
    }
    class ambilIsiSpinner extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;
        private String status; //status 1 = tipedok, 2 = prioritas, 3 = klasifikasi

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String responce;

            String type = params[0];
            String appid = params[1];
            String listexception = params[2];
            status = params[3];
            String key = params[4];

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

        @Override
        protected void onCancelled() {
            // Show your Toast
            loadingDialog.dismiss();
            pf.showAlertTimeOut(getActivity());
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

                        if(status.equals("1")){
                            HashMap<String,String> tipe = new HashMap<>();
                            tipe.put(TAG_Value,value);
                            tipe.put(TAG_Name,name);

                            listDokumen.add(tipe);
                            tipeDok.add(name);
                        }else if(status.equals("2")){
                            HashMap<String,String> priority = new HashMap<>();
                            priority.put(TAG_Value,value);
                            priority.put(TAG_Name, name);

                            listPrioritas.add(priority);
                            prior.add(name);
                        }else if(status.equals("3")){
                            HashMap<String,String> klasifikasi = new HashMap<>();
                            klasifikasi.put(TAG_Value,value);
                            klasifikasi.put(TAG_Name, name);

                            listKlasifikasi.add(klasifikasi);
                            classification.add(name);
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if(status.equals("1")){
                sptipeDokumenSuratMasuk.setAdapter(adapterTipe);
            }else if(status.equals("2")){
                spprioritasSuratMasuk.setAdapter(adapterPrioritas);
            }else if(status.equals("3")){
                spklasifikasiSuratMasuk.setAdapter(adapterKlasifikasi);
            }

        }
    }

    class getJenisDokumen extends AsyncTask<String, Void, JSONObject> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
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
            pf.showAlertTimeOut(getActivity());
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
                        usingSla = result.getString("UsingSla");
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
                        }
                    }
                    else
                    {
                        showAlert(message,Config.alertError);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    class ambilRecipients extends AsyncTask<String, Void, JSONArray> {

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String responce;

            String type = params[0];
            String recipientType = params[1];
            String docType = params[2];
            String roleid = params[3];
            String unitid = params[4];

            String entype="",enrec="",endoctype="",enrole="",enunit="",enkey="";
            mCrypt krip = new mCrypt();
            try {
                entype = krip.bytesToHex(krip.encrypt(type));
                enrec = krip.bytesToHex(krip.encrypt(recipientType));
                endoctype = krip.bytesToHex(krip.encrypt(docType));
                enrole = krip.bytesToHex(krip.encrypt(roleid));
                enunit = krip.bytesToHex(krip.encrypt(unitid));
                enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, enkey));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TYPE, entype));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_RECTYPE, enrec));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCTYPE, endoctype));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, enrole));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, enunit));


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "GetRecipient.php");
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
            }catch (IOException e) {
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
            pf.showAlertTimeOut(getActivity());
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            loadingDialog.dismiss();
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject e = result.getJSONObject(i);
                        String id = e.getString(TAG_ID);
                        String name = e.getString(TAG_NameRec);

                        HashMap<String,String> recipients = new HashMap<>();
                        recipients.put(TAG_ID,id);
                        recipients.put(TAG_NameRec,name);

                        listDari.add(recipients);
                        dari.add(name);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            else{
            }
            spdariSuratMasuk.setAdapter(adapterDari);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=0) {
            if (requestCode == 1111) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + uploadFileName);
                compressImage(file.getAbsolutePath());
            } else if (requestCode == 1) {
                String curFileName = data.getStringExtra("GetFileName");
                String curFilePath= data.getStringExtra("GetPath");

                attachment att = new attachment();
                att.setNamaFile(curFileName);
                att.setPathFile(curFilePath+"/"+curFileName);
                att.setSelected(false);
                att.setInLandingPage(false);
                attachmentList.add(att);

                fileAdapter = new FileAdapter(getActivity(), attachmentList);
                listView.setAdapter(fileAdapter);
            }
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
            dos.writeBytes("Content-Disposition: form-data;" + "name=\"uploaded_file\";" + "filename=\""+ empid + namefile + "\"" + lineEnd);
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
                ((inb)getActivity()).namafiles.add(attachmentList.get(position).getNamaFile());
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

    public String compressImage(String filePath) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//		by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//		you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//		max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//		width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//		setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//		inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//		this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//			load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//		check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//			write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        galleryAddPic(filename);

        String path = Environment.getExternalStorageDirectory().getPath()+ "/MyFolder/Images/" +picname;
        String filenames = picname;
        attachment att = new attachment();
        att.setNamaFile(filenames);
        att.setPathFile(path);
        att.setSelected(false);
        att.setInLandingPage(false);
        attachmentList.add(att);

        fileAdapter = new FileAdapter(getActivity(), attachmentList);
        listView.setAdapter(fileAdapter);

        return filename;

    }
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        picname = System.currentTimeMillis() + ".jpg";
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    class uploadFile extends AsyncTask<String, Void, JSONObject> {

        String namafile;
        String indexFile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prosesinput = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String docid = params[0];
            String dispID = params[1];
            namafile = params[2];
            String uploader=params[3];
            indexFile = params[4];

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
            prosesinput.dismiss();
            pf.showAlertTimeOut(getActivity());
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    String kode = result.getString("Kode");
                    String keterangan = result.getString("Keterangan");
                    String fileid = result.getString("FileID");

                    if(kode.equals("1")){
                        //uploadSukses = keterangan;
                        idfile.add(fileid);

                        int indexAttach = Integer.valueOf(indexFile)-1;

                        cd=new ConnectionDetector(getActivity().getApplicationContext());
                        isInternetPresent = cd.isConnectingToInternet();

                        if(isInternetPresent) {
                            DeleteFileFromLandingPage del = new DeleteFileFromLandingPage();
                            del.execute(empid+namafile);
                        }else{
                            pf.showAlertConnection(getActivity());
                        }

                    } else{
                        showAlertUpload(keterangan, Config.alertWarning);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(attachmentList.size() == Integer.valueOf(indexFile)){
                    inputSuratMasuk();
                }
            }
        }
    }

    private void showAlertUpload(final String isiAlert, final String title) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(isiAlert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if(title.equals(Config.alertWarning)) {
                            prosesinput.dismiss();
                            Intent pindah = new Intent(getActivity(),inb.class);
                            pindah.putExtra("fragment","newsuratmasuk");
                            startActivity(pindah);
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });
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
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            // Show your Toast
            pf.showAlertTimeOut(getActivity());
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                String success = result.toString();
            }
        }
    }
    public void showAlert(final String pesan, final String title){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<String> namaFile = new ArrayList<String>();

                        for (int i = 0; i < attachmentList.size(); i++) {
                            String nama = attachmentList.get(i).getNamaFile();
                            namaFile.add(nama);
                        }

                        fileAdapter = new FileAdapter(getActivity(), attachmentList);
                        listView.setAdapter(fileAdapter);
                        if(attachmentList.size()>1) {
                            daftarFile.setText("( "+attachmentList.size() + " Lampiran )");
                        }else {
                            daftarFile.setText("( "+attachmentList.size() + " Lampiran )");
                        }

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }
    public void dialogFiles() {
        delFile = new Dialog(getActivity());
        delFile.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        delFile.setContentView(R.layout.dialog_edit_file);

        btsubmit = (LinearLayout)delFile.findViewById(R.id.btSubmit);
        btdelete = (LinearLayout)delFile.findViewById(R.id.btDelete);
        btnupload = (LinearLayout)delFile.findViewById(R.id.btUploads);

        listView = (ListView) delFile.findViewById(R.id.lvDaftarPenerima);
        fileAdapter = new FileAdapter(getActivity(), attachmentList);
        listView.setAdapter(fileAdapter);

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
                    showAlertWarning(Config.warningDelete, Config.alertWarning);
                }
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

                    upLanding = ProgressDialog.show(getActivity(), null, "Loading . . .");
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
                                showAlertLandingPage("Unggah lampiran berhasil", Config.alertSuccess);
                            }else{
                                showAlertLandingPage("Unggah lampiran " + error + " gagal", Config.alertError);
                            }
                        }
                    };
                    mThread.start();



                } else {
                    delFile.dismiss();
                }
            }
        });

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] dialogItem = {"Ambil Gambar", "Cari File"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                uploadFileName = Long.toString(Calendar.getInstance().getTimeInMillis()) + "_image.jpg";
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                file = new File(Environment.getExternalStorageDirectory() + File.separator + uploadFileName);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".my.package.name.provider", file));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivityForResult(intent, 1111);
                                break;
                            case 1:
                                //attach action
                                Intent browse = new Intent(getActivity(), fc.class);
                                startActivityForResult(browse, 1);
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        delFile.show();
        Window window = delFile.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
    class inputSuratMasuk extends AsyncTask<String, Void, JSONObject> {

        Dialog pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String responce;

            String empid = params[0];
            String npp = params[1];
            String roleid = params[2];
            String currentunitid = params[3];
            String statusid = params[4];
            String unitid = params[5];
            String type = params[6];
            String prior = params[7];
            String classification = params[8];
            String title = params[9];
            String unitname = params[10];
            String content = params[11];
            String address = params[12];
            String tanggalsurat = params[13];
            String tanggalregister = params[14];
            String to = params[15];
            String cc = params[16];
            String bcc = params[17];
            String notes = params[18];
            String idfiles = params[19];
            String idpenerima = params[20];
            String docno = params[21];
            String docid = params[22];
            String idjenisdok = params[23];
            String key = params[24];

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_USERNAME, npp));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_CURUNIT, currentunitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_CURSTAT, statusid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TYPE, type));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_PRIOR, prior));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_CLASS, classification));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TITLE, title));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITNAME, unitname));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_CONTENT, content));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_ADDRESS, address));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_RELEASEDATE, tanggalsurat));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_RECTIME, tanggalregister));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_TO, to));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_CC, cc));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_BCC, bcc));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_NOTES, notes));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_LISTFILE, idfiles));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_LISTREC, idpenerima));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCNO, docno));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_DOCID, docid));
            nameValuePairs.add(new BasicNameValuePair(Config.TAG_VARIABLETAMBAHAN, idjenisdok));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.urlLanding + "InputSuratMasuk.php");
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
            pdialog.dismiss();
            pf.showAlertTimeOut(getActivity());
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            pdialog.dismiss();
            if (result != null) {
                try {
                    String kode = result.getString(TAG_KODE);
                    String keterangan = result.getString(TAG_KET);

                    if(kode.equals("1")){
                        showAlertSubmit(keterangan, Config.alertSuccess);
                    }
                    else{
                        showAlertSubmit(keterangan, Config.alertWarning);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void showAlertSubmit(final String pesan, final String title){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(title.equals(Config.alertSuccess)) {
                            Intent i = new Intent(getActivity(), inb.class);
                            i.putExtra("fragment", "hardcopy");
                            startActivity(i);
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }
    public void showAlertLandingPage(final String pesan, final String title){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        delFile.dismiss();
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
                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }
    public void showAlertConnection() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(Config.alertConnection).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                            getActivity().setTitle(Config.nointernet);
                            NoInternet no = new NoInternet();

                            android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_container, no);
                            fragmentTransaction.commit();
                    }
                });

                if(dialogConnection==false){
                    dialogConnection=true;
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        });
    }
    public void showAlertWarning(final String pesan, final String title) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(pesan).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //deletefile proses
                        for(int i=positionfilesdel.size()-1;i>=0;i--){
                            int a = positionfilesdel.get(i);
                            positionfilesdel.remove(i);
                            attachmentList.remove(a);
                        }
                        if (attachmentList.size() > 1) {
                            daftarFile.setText("( " + attachmentList.size() + " Files )");
                        } else {
                            daftarFile.setText("( " + attachmentList.size() + " File )");
                        }

                        fileAdapter = new FileAdapter(getActivity(), attachmentList);
                        listView.setAdapter(fileAdapter);

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

