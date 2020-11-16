package bni.emplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import bni.emplus.Model.Surat;
import bni.emplus.R;

import java.util.List;

/**
 * Created by ZulfahPermataIlliyin on 6/27/2016.
 */
public class SentAdapter extends BaseAdapter{
    private Activity activity;
    private List<Surat> suratList;
    private LayoutInflater inflater;

    public SentAdapter(Activity activity, List<Surat> suratList){
        this.activity = activity;
        this.suratList = suratList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater==null){
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView==null){
            convertView=inflater.inflate(R.layout.list_item_sent,null);
        }

        TextView JudulSurat = (TextView)convertView.findViewById(R.id.tvJudulSent);
        TextView FlowSurat = (TextView)convertView.findViewById(R.id.tvFlow);
        TextView TanggalSurat = (TextView)convertView.findViewById(R.id.tvTanggalSent);
        TextView JamSurat = (TextView)convertView.findViewById(R.id.tvJamSent);

        Surat surat = suratList.get(position);
        JudulSurat.setText(surat.getJudulSurat());
        FlowSurat.setText(surat.getFlowSurat());
        TanggalSurat.setText(surat.getTanggalSurat());
        JamSurat.setText(surat.getJamSurat());

        return convertView;
    }
}

