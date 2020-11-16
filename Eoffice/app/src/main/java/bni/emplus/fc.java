package bni.emplus;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import bni.emplus.Adapter.FileArrayAdapter;
import bni.emplus.Model.FileUpload;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZulfahPermataIlliyin on 6/16/2016.
 */
public class fc extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;

    PublicFunction pf = new PublicFunction();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mencari direktori sdcard di handphone
        //currentDir = new File("/sdcard/");
        currentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        fill(currentDir);
    }
    private void fill(File f)
    {
        //mendapatkan file2 yang ada di dalam direktori
        File[] dirs = f.listFiles();
        this.setTitle("Current Dir: /"+f.getName());
        List<FileUpload>dir = new ArrayList<FileUpload>();
        List<FileUpload>fls = new ArrayList<FileUpload>();
        try{
            //looping sebanyak file yang ada di dalam direktori
            for(File ff: dirs)
            {
                //menset date modify
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String myFormat = "dd/MM/yyyy HH:mm"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                String date_modify = sdf.format(lastModDate);

                if(ff.isDirectory()){
                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //menambahkan fileUpload ke dalam list dir untuk ditampilkan
                    dir.add(new FileUpload(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"ic_folder"));
                }
                else
                {
                    String size = "";
                    if((int)ff.length()<=1024){
                        size = ff.length() + " B";
                    }else if ((int)ff.length()>=1024&&(int)ff.length()<1048576){
                        int saiz = (int) (ff.length()/1024);
                        size = String.valueOf(saiz)+" Kb";
                    }else if((int)ff.length()>=1048576){
                        int saiz = (int) (ff.length()/1048576);
                        size = String.valueOf(saiz)+" Mb";
                    }

                    if(ff.getName().contains(".pdf")) {
                        fls.add(new FileUpload(ff.getName(),size, date_modify, ff.getAbsolutePath(),"ic_pdf",String.valueOf(ff.length())));
                    }else if(ff.getName().contains(".doc")){
                        fls.add(new FileUpload(ff.getName(),size, date_modify, ff.getAbsolutePath(),"ic_word",String.valueOf(ff.length())));
                    }else if(ff.getName().contains(".xls")) {
                        fls.add(new FileUpload(ff.getName(),size, date_modify, ff.getAbsolutePath(),"ic_xls",String.valueOf(ff.length())));
                    }else if(ff.getName().contains(".jpg")){
                        fls.add(new FileUpload(ff.getName(),size, date_modify, ff.getAbsolutePath(),"ic_jpg",String.valueOf(ff.length())));
                    }
                }
            }
        }catch(Exception e)
        {
        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase(""))
            dir.add(0,new FileUpload("..","Parent Directory","",f.getParent(),"ic_back"));
        adapter = new FileArrayAdapter(fc.this, R.layout.item_file_view,dir);
        this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        FileUpload o = adapter.getItem(position);
        if(o.getImage().equalsIgnoreCase("ic_folder")||o.getImage().equalsIgnoreCase("ic_back")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }
    private void onFileClick(FileUpload o)
    {
        if(Integer.valueOf(o.getSize()) > 2097152){
            pf.showAlert(this,"Batas besar lampiran 2MB", Config.alertWarning);

        }else {
            Intent intent = new Intent();
            intent.putExtra("GetPath", currentDir.toString());
            intent.putExtra("GetFileName", o.getName());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
