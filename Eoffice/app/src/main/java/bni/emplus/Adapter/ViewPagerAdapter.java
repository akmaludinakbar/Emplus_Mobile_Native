package bni.emplus.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bni.emplus.Fragment.DetailDisposisi;
import bni.emplus.Fragment.HistoriDisposisi;
import bni.emplus.Fragment.LampiranDisposisi;

/**
 * Created by ZulfahPermataIlliyin on 6/30/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    String docid,doctypename,boxid,docregid,modefirstdoc,regno,mode,crtime,fragment;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, String docid, String doctypename, String boxid,
                            String docregid, String modefirstdoc, String regno, String mode, String crtime, String fragment) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.docid = docid;
        this.doctypename = doctypename;
        this.boxid = boxid;
        this.docregid = docregid;
        this.modefirstdoc = modefirstdoc;
        this.regno = regno;
        this.mode = mode;
        this.crtime = crtime;
        this.fragment = fragment;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            DetailDisposisi tab1 = new DetailDisposisi();
            Bundle args = new Bundle();
            args.putString("docid", docid);
            args.putString("doctype",doctypename);
            args.putString("boxid",boxid);
            args.putString("docregid",docregid);
            args.putString("modefirstdoc", modefirstdoc);
            args.putString("regno",regno);
            args.putString("mode",mode);
            args.putString("tanggal",crtime);
            args.putString("fragment",fragment);
            tab1.setArguments(args);
            return tab1;
        }
        else if(position==1){
            LampiranDisposisi tab2 = new LampiranDisposisi();
            Bundle args = new Bundle();
            args.putString("docid", docid);
            args.putString("fragment",fragment);
            tab2.setArguments(args);
            return tab2;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            HistoriDisposisi tab3 = new HistoriDisposisi();
            Bundle args = new Bundle();
            args.putString("docid", docid);
            args.putString("fragment",fragment);
            tab3.setArguments(args);
            return tab3;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
