package bni.emplus.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bni.emplus.Model.Pegawai;
import bni.emplus.PublicFunction;
import bni.emplus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZulfahPermataIlliyin on 7/15/2016.
 */
public class PegawaiAdapter extends BaseAdapter{
    private Activity activity;
    private List<Pegawai> pegawaiList;
    private LayoutInflater inflater;
    PublicFunction pf = new PublicFunction();

    public PegawaiAdapter(Activity activity, List<Pegawai> pegawaiList) {
        this.activity = activity;
        this.pegawaiList = pegawaiList;
    }

    static class ViewHolder {
        protected TextView pegawai;
        protected CheckBox cbpeg;
        protected RelativeLayout rlItemPegawai;
    }

    @Override
    public int getCount() {
        return pegawaiList.size();
    }

    @Override
    public Object getItem(int position) {
        return pegawaiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            double screenInches = pf.getScreenInches(activity);

            if(screenInches<=4.0){
                convertView = inflater.inflate(R.layout.list_item_penerima,null);
            }else if(screenInches>4.0&&screenInches<=5.0){
                convertView = inflater.inflate(R.layout.list_item_penerima,null);
            }else if(screenInches>5.0&&screenInches<=6.0){
                convertView = inflater.inflate(R.layout.list_item_penerima,null);
            }else if(screenInches>6.0&&screenInches<=7.0){
                convertView = inflater.inflate(R.layout.list_item_penerima_tujuh,null);
            }else if(screenInches>7.0&&screenInches<=8.0){
                convertView = inflater.inflate(R.layout.list_item_penerima_tujuh,null);
            }else if(screenInches>8.0&&screenInches<=9.0){
                convertView = inflater.inflate(R.layout.list_item_penerima_tujuh,null);
            }else if(screenInches>9.0&&screenInches<=10.0) {
                convertView = inflater.inflate(R.layout.list_item_penerima_tujuh,null);
            }else if(screenInches>10.0&&screenInches<=11.0){
                convertView = inflater.inflate(R.layout.list_item_penerima_tujuh,null);
            }

            viewHolder = new ViewHolder();
            viewHolder.pegawai = (TextView) convertView.findViewById(R.id.namaPenerima);
            viewHolder.cbpeg = (CheckBox) convertView.findViewById(R.id.cbpeg);
            viewHolder.rlItemPegawai = (RelativeLayout) convertView.findViewById(R.id.rlListPenerima);

            viewHolder.cbpeg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            viewHolder.cbpeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (Integer)buttonView.getTag();
                    pegawaiList.get(position).setSelected(buttonView.isChecked());
                    Log.d("tes position", String.valueOf(position));
                    Log.d("tes selected", String.valueOf(pegawaiList.get(position).getSelected()));

                    if(pegawaiList.get(position).getTipeRec().equals("0"))
                    {
                        if(pegawaiList.get(position).getSelected()) {
                            for (int i = 0; i < pegawaiList.size(); i++) {
                                if (pegawaiList.get(i).getTipeRec().equals("2")) {
                                    pegawaiList.get(i).setSelected(buttonView.isChecked());
                                }
                            }
                        }
                        else{
                            boolean allSelect = true;
                            for (int i = 0; i < pegawaiList.size(); i++) {
                                if (pegawaiList.get(i).getTipeRec().equals("2") && !pegawaiList.get(i).getSelected()) {
                                    allSelect = false;
                                }
                            }

                            if(allSelect)
                            {
                                for (int i = 0; i < pegawaiList.size(); i++) {
                                    if (pegawaiList.get(i).getTipeRec().equals("2")) {
                                        pegawaiList.get(i).setSelected(buttonView.isChecked());
                                    }
                                }
                            }
                        }
                        notifyDataSetChanged();
                    }
                    if(pegawaiList.get(position).getTipeRec().equals("2") && !pegawaiList.get(position).getSelected())
                    {
                        for (int i = 0; i < pegawaiList.size(); i++) {
                            if (pegawaiList.get(i).getTipeRec().equals("0")) {
                                pegawaiList.get(i).setSelected(false);
                            }
                        }
                        notifyDataSetChanged();
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.namaPenerima,viewHolder.pegawai);
            convertView.setTag(R.id.cbpeg,viewHolder.cbpeg);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.cbpeg.setTag(position);
        viewHolder.pegawai.setText(pegawaiList.get(position).getNamaPegawai());
        viewHolder.cbpeg.setChecked(pegawaiList.get(position).getSelected());

        if(pegawaiList.get(position).getTipeRec().equals("0"))
        {
            viewHolder.rlItemPegawai.setBackgroundColor(Color.rgb(187, 187, 187));
        }
        else
        {
            viewHolder.rlItemPegawai.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        return convertView;
    }
}
