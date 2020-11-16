package bni.emplus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import bni.emplus.PublicFunction;
import bni.emplus.R;

/**
 * Created by ZulfahPermataIlliyin on 6/24/2016.
 */
public class RoleAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> roles;
    private LayoutInflater inflater;
    private Activity activity;

    PublicFunction pf = new PublicFunction();

    public RoleAdapter(Activity activity, ArrayList<HashMap<String, String>> roles){
        this.activity = activity;
        this.roles = roles;
    }

    @Override
    public int getCount() {
        return roles.size();
    }

    @Override
    public Object getItem(int position) {
        return roles.get(position);
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
            double screenInches = pf.getScreenInches(activity);

            if(screenInches<=4.0){
                convertView=inflater.inflate(R.layout.list_item_role,null);
            }else if(screenInches>4.0&&screenInches<=5.0){
                convertView=inflater.inflate(R.layout.list_item_role,null);
            }else if(screenInches>5.0&&screenInches<=6.0){
                convertView=inflater.inflate(R.layout.list_item_role,null);
            }else if(screenInches>6.0&&screenInches<=7.0){
                convertView=inflater.inflate(R.layout.list_item_role_tujuh,null);
            }else if(screenInches>7.0&&screenInches<=8.0){
                convertView=inflater.inflate(R.layout.list_item_role_tujuh,null);
            }else if(screenInches>8.0&&screenInches<=9.0){
                convertView=inflater.inflate(R.layout.list_item_role_tujuh,null);
            }else if(screenInches>9.0&&screenInches<=10.0) {
                convertView=inflater.inflate(R.layout.list_item_role_tujuh,null);
            }else if(screenInches>10.0&&screenInches<=11.0){
                convertView=inflater.inflate(R.layout.list_item_role_tujuh,null);
            }

        }

        TextView itemRole = (TextView)convertView.findViewById(R.id.item_role);
        String disp = roles.get(position).get("DispDDL");
        itemRole.setText(disp);

        return convertView;
    }
}
