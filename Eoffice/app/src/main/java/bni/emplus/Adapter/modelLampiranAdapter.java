package bni.emplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import bni.emplus.ConnectionDetector;
import bni.emplus.Model.modelLampiran;
import bni.emplus.PublicFunction;
import bni.emplus.R;

import java.util.List;

/**
 * Created by ZulfahPermataIlliyin on 6/29/2016.
 */
public class modelLampiranAdapter extends BaseAdapter {
    private Activity activity;
    private List<modelLampiran> lampiranList;
    private LayoutInflater inflater;
    private modelLampiranAdapter adapter;
    private String from;

    private static final String TAG_ID = "Id";
    private static final String TAG_NILAIBYTES = "NilaiBytes";
    private static final String TAG_ERROR = "ErrorMessage";
    private static final String TAG_KODE = "Kode";
    private static final String TAG_KET = "Keterangan";

    String bytedokumen,size,fileName, pathFile;
    String enid = "", enkey = "";
    modelLampiran modelLampiran;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    PublicFunction pf = new PublicFunction();

    public modelLampiranAdapter(Activity activity, List<modelLampiran> lampiranList, String from) {
        this.activity = activity;
        this.lampiranList = lampiranList;
        this.from = from;
    }

    @Override
    public int getCount() {
        return lampiranList.size();
    }

    @Override
    public Object getItem(int position) {
        return lampiranList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }if(convertView == null){
            double screenInches = pf.getScreenInches(activity);

            if(screenInches<=4.0){
                convertView = inflater.inflate(R.layout.list_item_lampiran, null);
            }else if(screenInches>4.0&&screenInches<=5.0){
                convertView = inflater.inflate(R.layout.list_item_lampiran, null);
            }else if(screenInches>5.0&&screenInches<=6.0){
                convertView = inflater.inflate(R.layout.list_item_lampiran, null);
            }else if(screenInches>6.0&&screenInches<=7.0){
                convertView = inflater.inflate(R.layout.list_item_lampiran_tujuh, null);
            }else if(screenInches>7.0&&screenInches<=8.0){
                convertView = inflater.inflate(R.layout.list_item_lampiran_tujuh, null);
            }else if(screenInches>8.0&&screenInches<=9.0){
                convertView = inflater.inflate(R.layout.list_item_lampiran_tujuh, null);
            }else if(screenInches>9.0&&screenInches<=10.0) {
                convertView = inflater.inflate(R.layout.list_item_lampiran_tujuh, null);
            }else if(screenInches>10.0&&screenInches<=11.0){
                convertView = inflater.inflate(R.layout.list_item_lampiran_tujuh, null);
            }

        }

        convertView.setBackgroundColor(Color.rgb(226, 226, 226));

        ImageView gambarDokumen = (ImageView) convertView.findViewById(R.id.ivDokumen);
        TextView judulDokumen = (TextView) convertView.findViewById(R.id.tvJudulDokumen);
        TextView tanggalDokumen = (TextView) convertView.findViewById(R.id.tvTanggalDokumen);
        ImageView ivDownload = (ImageView)convertView.findViewById(R.id.ivDownload);

        if(lampiranList.get(position).getIsFirstDoc().equals("1")){
            convertView.setBackgroundColor(Color.rgb(187, 187, 187));
        }

        modelLampiran = lampiranList.get(position);
        gambarDokumen.setImageResource(modelLampiran.getGambarDokumen());
        judulDokumen.setText(modelLampiran.getJudulDokumen());
        tanggalDokumen.setText(modelLampiran.getTanggalDokumen());

        return convertView;
    }

}
