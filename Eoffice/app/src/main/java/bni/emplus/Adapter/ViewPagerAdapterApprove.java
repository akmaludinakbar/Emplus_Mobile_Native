package bni.emplus.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bni.emplus.Fragment.DetailApprove;
import bni.emplus.Fragment.KomentarApprove;
import bni.emplus.Fragment.LampiranApprove;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapterApprove extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    String docid,fragment,type,docstatus;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterApprove(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, String docid, String fragment, String type, String docstatus) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.docid = docid;
        this.fragment = fragment;
        this.type = type;
        this.docstatus = docstatus;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            DetailApprove tab1 = new DetailApprove();
            Bundle args = new Bundle();
            args.putString("docid", docid);
            args.putString("fragment",fragment);
            args.putString("type",type);
            args.putString("docstatus",docstatus);
            tab1.setArguments(args);

            return tab1;
        } else if (position == 1)             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            LampiranApprove tab2 = new LampiranApprove();
            Bundle args = new Bundle();
            args.putString("docid", docid);
            tab2.setArguments(args);
            return tab2;
        }
        else {
            KomentarApprove tab3 = new KomentarApprove();
            Bundle args = new Bundle();
            args.putString("docid", docid);
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
