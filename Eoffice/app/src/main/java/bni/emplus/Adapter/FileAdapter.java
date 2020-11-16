package bni.emplus.Adapter;

/**
 * Created by ZulfahPermataIlliyin on 9/20/2016.
 */

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import bni.emplus.Model.attachment;
import bni.emplus.PublicFunction;
import bni.emplus.R;

import java.util.List;

public class FileAdapter extends BaseAdapter {
    private Activity activity;
    private List<attachment> attachmentList;

    PublicFunction pf = new PublicFunction();

    public FileAdapter(Activity activity, List<attachment> attachmentList) {
        this.activity = activity;
        this.attachmentList = attachmentList;
    }

    static class ViewHolder {
        protected TextView namafile;
        protected CheckBox cbfile;
        protected ImageView image;
    }

    @Override
    public int getCount() {
        return attachmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return attachmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            double screenInches = pf.getScreenInches(activity);

            if(screenInches<=4.0){
                convertView = inflater.inflate(R.layout.list_item_files,null);
            }else if(screenInches>4.0&&screenInches<=5.0){
                convertView = inflater.inflate(R.layout.list_item_files,null);
            }else if(screenInches>5.0&&screenInches<=6.0){
                convertView = inflater.inflate(R.layout.list_item_files,null);
            }else if(screenInches>6.0&&screenInches<=7.0){
                convertView = inflater.inflate(R.layout.list_item_files_tujuh,null);
            }else if(screenInches>7.0&&screenInches<=8.0){
                convertView = inflater.inflate(R.layout.list_item_files_tujuh,null);
            }else if(screenInches>8.0&&screenInches<=9.0){
                convertView = inflater.inflate(R.layout.list_item_files_tujuh,null);
            }else if(screenInches>9.0&&screenInches<=10.0) {
                convertView = inflater.inflate(R.layout.list_item_files_tujuh,null);
            }else if(screenInches>10.0&&screenInches<=11.0){
                convertView = inflater.inflate(R.layout.list_item_files_tujuh,null);
            }

            viewHolder = new ViewHolder();
            viewHolder.namafile = (TextView) convertView.findViewById(R.id.namaFiles);
            viewHolder.cbfile = (CheckBox) convertView.findViewById(R.id.cbfiles);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.ivFIle);

            viewHolder.cbfile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer)buttonView.getTag();
                    attachmentList.get(getPosition).setSelected(buttonView.isChecked());
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.namaFiles,viewHolder.namafile);
            convertView.setTag(R.id.cbfiles,viewHolder.cbfile);
            convertView.setTag(R.id.ivFIle,viewHolder.image);
            convertView.setBackgroundColor(Color.rgb(226, 226, 226));
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.cbfile.setTag(position);
        viewHolder.namafile.setText(attachmentList.get(position).getNamaFile());
        viewHolder.cbfile.setChecked(attachmentList.get(position).getSelected());

        if(attachmentList.get(position).getInLandingPage()==true){
            convertView.setBackgroundColor(Color.rgb(187, 187, 187));
        }

        if(attachmentList.get(position).getNamaFile().contains(".pdf")){
            viewHolder.image.setImageResource(R.mipmap.ic_pdf);
        }else if(attachmentList.get(position).getNamaFile().contains(".doc")){
            viewHolder.image.setImageResource(R.mipmap.ic_word);
        }else if(attachmentList.get(position).getNamaFile().contains(".xls")) {
            viewHolder.image.setImageResource(R.mipmap.ic_xls);
        }else if(attachmentList.get(position).getNamaFile().contains(".jpg")) {
            viewHolder.image.setImageResource(R.mipmap.ic_jpg);
        }


        return convertView;
    }
}
