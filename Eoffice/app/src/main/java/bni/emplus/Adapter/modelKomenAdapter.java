package bni.emplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import bni.emplus.Model.modelKomen;
import bni.emplus.PublicFunction;
import bni.emplus.R;

import java.util.List;

/**
 * Created by ZulfahPermataIlliyin on 6/29/2016.
 */
public class modelKomenAdapter extends BaseAdapter {
    private Activity activity;
    private List<modelKomen> komenList;
    private LayoutInflater inflater;

    PublicFunction pf = new PublicFunction();
    public modelKomenAdapter(Activity activity, List<modelKomen> komenList) {
        this.activity = activity;
        this.komenList = komenList;
    }

    @Override
    public int getCount() {
        return komenList.size();
    }

    @Override
    public Object getItem(int position) {
        return komenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }if(convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            double screenInches = pf.getScreenInches(activity);

            if(screenInches<=4.0){
                convertView = inflater.inflate(R.layout.list_item_komentar,null);
            }else if(screenInches>4.0&&screenInches<=5.0){
                convertView = inflater.inflate(R.layout.list_item_komentar,null);
            }else if(screenInches>5.0&&screenInches<=6.0){
                convertView = inflater.inflate(R.layout.list_item_komentar,null);
            }else if(screenInches>6.0&&screenInches<=7.0){
                convertView = inflater.inflate(R.layout.list_item_komentar_tujuh,null);
            }else if(screenInches>7.0&&screenInches<=8.0){
                convertView = inflater.inflate(R.layout.list_item_komentar_tujuh,null);
            }else if(screenInches>8.0&&screenInches<=9.0){
                convertView = inflater.inflate(R.layout.list_item_komentar_tujuh,null);
            }else if(screenInches>9.0&&screenInches<=10.0) {
                convertView = inflater.inflate(R.layout.list_item_komentar_tujuh,null);
            }else if(screenInches>10.0&&screenInches<=11.0){
                convertView = inflater.inflate(R.layout.list_item_komentar_tujuh,null);
            }

        }

        ImageView gambarKomen = (ImageView) convertView.findViewById(R.id.ivKomen);
        TextView pegawai = (TextView) convertView.findViewById(R.id.tvPegawai);
        TextView isiKomen = (TextView) convertView.findViewById(R.id.tvIsiKomen);
        TextView tanggalKomen = (TextView) convertView.findViewById(R.id.tvTanggalKomen);


        modelKomen modelKomen = komenList.get(position);
        gambarKomen.setImageResource(modelKomen.getGambarKomen());
        pegawai.setText(modelKomen.getPegawai());
        isiKomen.setText(Html.fromHtml(modelKomen.getIsiKomen()));
        tanggalKomen.setText(modelKomen.getTanggalKomen() + " " + modelKomen.getJamKomen());

        return convertView;
    }
}
