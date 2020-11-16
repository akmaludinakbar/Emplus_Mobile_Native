package bni.emplus.Adapter;

/**
 * Created by ZulfahPermataIlliyin on 6/27/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import bni.emplus.ConnectionDetector;
import bni.emplus.Model.Surat;
import bni.emplus.PublicFunction;
import bni.emplus.R;
import bni.emplus.UserSessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SuratAdapter extends BaseAdapter {

    private static final String TAG_TOTDIS = "TotalInboxAll_D";
    private static final String TAG_TOTUR = "TotalInboxAll_U";
    private static final String TAG_TOTAPP = "TotalApprovalOnline";
    private static final String TAG_TOTINBOX = "TotalInboxAll";
    private static final String TAG_TOTSENT = "TotalSentItems";
    private static final String TAG_TOTMONITOR = "TotalMonitoring";
    private static final String TAG_TOTTRACK = "TotalTrackingHistory";
    private static final String TAG_ONLINE = "TotalDisposisiOnline";
    private static final String TAG_OFFLINE = "TotalDisposisiOffline";
    private static final String TAG_LIST = "TotalListDokumen";
    private static final String TAG_TOTFAV = "TotalFavourite";

    private Activity activity;
    private List<Surat> suratList;
    private LayoutInflater inflater;
    ArrayList<Surat> arraylist;
    ImageView iconSurat,iconFav;
    String fragment;

    Surat suratselect = new Surat();
    String fav = "";
    View view;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    UserSessionManager session;
    String unitid,empid,roleid,statid,npp;

    PublicFunction pf = new PublicFunction();

    public SuratAdapter(Activity activity, List<Surat> suratList, String fragment){
        this.activity = activity;
        this.suratList = suratList;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return suratList.size();
    }

    @Override
    public Object getItem(int position) {
        return suratList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater==null){
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView==null){
            convertView=inflater.inflate(R.layout.list_item_linlayout,null);
        }

        //session
        session = new UserSessionManager(activity);

        HashMap<String, String> user = session.getUserNPP();
        HashMap<String, String> userRole = session.getUserRole();

        // get roleid,unitid,npp
        unitid = userRole.get(UserSessionManager.KEY_UNIT);
        roleid = userRole.get(UserSessionManager.KEY_ROLE);
        empid = user.get(UserSessionManager.KEY_EMPID);
        npp = user.get(UserSessionManager.KEY_NAME);
        statid = userRole.get(UserSessionManager.KEY_STATID);

        iconSurat = (ImageView)convertView.findViewById(R.id.ivIconSurat);
        iconFav = (ImageView)convertView.findViewById(R.id.ivIconFav);
        TextView JudulSurat = (TextView)convertView.findViewById(R.id.tvJudul);
        TextView DivisiSurat = (TextView)convertView.findViewById(R.id.tvDivisi);
        TextView NomorSurat = (TextView)convertView.findViewById(R.id.tvNomorSurat);
        TextView TanggalSurat = (TextView)convertView.findViewById(R.id.tvTanggal);
        TextView JamSurat = (TextView) convertView.findViewById(R.id.tvJam);
        TextView TanggalAsliSurat = (TextView) convertView.findViewById(R.id.tvTanggalAsliSurat);
        TextView Durasi = (TextView) convertView.findViewById(R.id.tvInboxDurasi);
        ImageView circle = (ImageView) convertView.findViewById(R.id.circle);

        final Surat surat = suratList.get(position);

        iconSurat.setImageResource(surat.getIconSurat());
        iconFav.setImageResource(surat.getIconFav());
        JudulSurat.setText(surat.getJudulSurat());
        DivisiSurat.setText(surat.getDivisiSurat());
        NomorSurat.setText(surat.getNomorSurat());
        TanggalSurat.setText(surat.getTanggalSurat());
        JamSurat.setText(surat.getJamSurat());
        TanggalAsliSurat.setText(surat.getTanggalAsliSurat());
        Durasi.setText(surat.getDurasiDisp());
        circle.setImageResource(surat.getIconStatus());

        if(fragment.equals("monitoring") || fragment.equals("approve"))
        {
            Durasi.setVisibility(View.GONE);
            circle.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        suratList.clear();
        if (charText.length() == 0) {
            suratList.addAll(arraylist);

        } else {
            for (Surat surat : arraylist) {
                if (charText.length() != 0 && surat.getJudulSurat().toLowerCase(Locale.getDefault()).contains(charText)) {
                    suratList.add(surat);
                }
            }
        }
        notifyDataSetChanged();
    }

}

