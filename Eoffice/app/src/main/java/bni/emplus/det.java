package bni.emplus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import bni.emplus.Adapter.ViewPagerDetailAdapter;
import bni.emplus.Model.modelHistori;
import bni.emplus.Model.modelLampiran;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class det extends AppCompatActivity {
    public boolean dialogConnection = false;

    public ViewPager pager;
    public ViewPagerDetailAdapter adapter;
    public boolean statOpenPDF = false;
    public slid tabs;
    CharSequence Titles[]={"Detail","Lampiran","Riwayat"};
    int Numboftabs =3;
    String docid,doctypename,fragment;

    public HashMap<String,String> isidetail = new HashMap<String,String>();
    public List<modelLampiran> filedetail = new ArrayList<>();
    public List<modelHistori> historidetail = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
        }

        setTitle("Detail");

        Bundle args = getIntent().getExtras();
        if(args!=null){
            docid = (String) args.get("docid");
            doctypename = (String) args.get("typename");
            fragment = (String) args.get("fragment");
        }


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerDetailAdapter(getSupportFragmentManager(),Titles,Numboftabs,docid,doctypename,fragment);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pagerdetail);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (slid) findViewById(R.id.tabsdetail);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }
    @Override
    public void onBackPressed() {
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
                // search action
                Intent i = new Intent(det.this, inb.class);
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
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(statOpenPDF) {
            Delete dels = new Delete();
            dels.delete();
            statOpenPDF = false;
        }
    }
}
