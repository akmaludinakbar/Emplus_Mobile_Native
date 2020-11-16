package bni.emplus.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import bni.emplus.Config;
import bni.emplus.ConnectionDetector;
import bni.emplus.Model.modelGlosarium;
import bni.emplus.PublicFunction;
import bni.emplus.R;
import bni.emplus.mCrypt;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FubianLathanio on 14/07/2017.
 */
public class AboutFrag extends Fragment {
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    PublicFunction pf;

    private ArrayList<modelGlosarium> glosariumArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.slideabout_frag,container,false);
        setHasOptionsMenu(true);
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) v.findViewById(R.id.layoutDots);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        if(screenInches<=4.1){
            layouts = new int[]{
                    R.layout.slide_first,
                    R.layout.slide_glosarium,
                    R.layout.slide_one,
                    R.layout.slide_two,
                    R.layout.slide_three,
                    R.layout.slide_four
            };
        }else if(screenInches>4.0&&screenInches<=5.0){
            layouts = new int[]{
                    R.layout.slide_first,
                    R.layout.slide_glosarium,
                    R.layout.slide_one,
                    R.layout.slide_two,
                    R.layout.slide_three,
                    R.layout.slide_four
            };
        }else if(screenInches>5.0&&screenInches<=6.0){
            layouts = new int[]{
                    R.layout.slide_first,
                    R.layout.slide_glosarium,
                    R.layout.slide_one,
                    R.layout.slide_two,
                    R.layout.slide_three,
                    R.layout.slide_four
            };
        }else if(screenInches>6.0&&screenInches<=7.0){
            layouts = new int[]{
                    R.layout.slide_first,
                    R.layout.slide_glosarium,
                    R.layout.slide_one_enam,
                    R.layout.slide_two_enam,
                    R.layout.slide_three_enam,
                    R.layout.slide_four_enam
            };
        }else if(screenInches>7.0&&screenInches<=8.0){
            layouts = new int[]{
                    R.layout.slide_first_tujuh,
                    R.layout.slide_glosarium_tujuh,
                    R.layout.slide_one_tujuh,
                    R.layout.slide_two_tujuh,
                    R.layout.slide_three_tujuh,
                    R.layout.slide_four_tujuh
            };
        }else if(screenInches>8.0&&screenInches<=9.0){
            layouts = new int[]{
                    R.layout.slide_first_tujuh,
                    R.layout.slide_glosarium_tujuh,
                    R.layout.slide_one_tujuh,
                    R.layout.slide_two_tujuh,
                    R.layout.slide_three_tujuh};
        }else if(screenInches>9.0&&screenInches<=10.0) {
            layouts = new int[]{
                    R.layout.slide_first_tujuh,
                    R.layout.slide_glosarium_tujuh,
                    R.layout.slide_one_tujuh,
                    R.layout.slide_two_tujuh,
                    R.layout.slide_three_tujuh,
                    R.layout.slide_four_tujuh};
        }else if(screenInches>10.0&&screenInches<=11.0){
            layouts = new int[]{
                    R.layout.slide_first_tujuh,
                    R.layout.slide_glosarium_tujuh,
                    R.layout.slide_one_tujuh,
                    R.layout.slide_two_tujuh,
                    R.layout.slide_three_tujuh,
                    R.layout.slide_four_tujuh};
        }

        // adding bottom dots
        addBottomDots(0);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.inbox, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_light_screen2));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_dark_screen2));
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        public RecyclerView recyclerView;
        public RecyclerView.Adapter adapter;

        ConnectionDetector cd;
        Boolean isInternetPresent = false;
        String enunit,enempid,enrole,enpagesize,enpagenumber,enkk,enkey,enkode;

        private static final String TAG_ISTILAH = "Istilah";
        private static final String TAG_KETERANGAN = "Keterangan";

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            if(position == 0)
            {
                TextView italic = (TextView) view.findViewById(R.id.textView4);
                italic.setText(Html.fromHtml("Dokumen terproteksi dengan <i> watermark </i>"));
            }
            else if(position == 1)
            {
                recyclerView = (RecyclerView) view.findViewById(R.id.rvListGlosarium);
                adapter = new modelGlossariumAdapter(glosariumArrayList,getActivity());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                if(glosariumArrayList.isEmpty()) {
                    cd = new ConnectionDetector(getContext());
                    isInternetPresent = cd.isConnectingToInternet();
                    String kataKunci = Config.versi;

                    if (isInternetPresent) {
                        mCrypt krip = new mCrypt();
                        try {
                            enunit = krip.bytesToHex(krip.encrypt("0"));
                            enempid = krip.bytesToHex(krip.encrypt("0"));
                            enrole = krip.bytesToHex(krip.encrypt("0"));
                            enpagesize = krip.bytesToHex(krip.encrypt(String.valueOf(0)));
                            enpagenumber = krip.bytesToHex(krip.encrypt(String.valueOf(0)));
                            if (kataKunci.equals("")) {
                                enkk = kataKunci;
                            } else {
                                enkk = krip.bytesToHex(krip.encrypt(kataKunci));
                            }
                            enkey = krip.bytesToHex(krip.encrypt(Config.api_key));
                            enkode = krip.bytesToHex(krip.encrypt("0"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ambilGlosarium ambilGlosarium = new ambilGlosarium();
                        ambilGlosarium.execute(enunit, enempid, enrole, enpagesize, enpagenumber, enkk, enkey);
                    } else {
                        pf.showAlertConnection(getActivity());
                    }
                }
            }

            return view;
        }

        class ambilGlosarium extends AsyncTask<String, Void, JSONArray> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(getActivity(), null, "Harap tunggu . . .");
            }

            @Override
            protected JSONArray doInBackground(String... params) {
                String unitid = params[0];
                String empid = params[1];
                String roleid = params[2];
                String pagesize = params[3];
                String pagenumber=params[4];
                String katakunci = params[5];
                String key = params[6];
                String responce;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_APIKEY, key));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_UNITID, unitid));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_EMPID, empid));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_ROLEID, roleid));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_PAGESIZE, pagesize));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_PAGENUMBER, pagenumber));
                nameValuePairs.add(new BasicNameValuePair(Config.TAG_KATAKUNCI, katakunci));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Config.urlLanding + "GetListInboxDisposisi.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //TimeOut
                    HttpParams httpParameters = httpPost.getParams();
                    // Set the timeout in milliseconds until a connection is established.
                    int timeoutConnection = 30000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                    // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
                    int timeoutSocket = 30000;
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    responce = EntityUtils.toString(entity);
                    return new JSONArray(responce);

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    //pf.showAlertTimeOut(getActivity());
                    cancel(true);
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    //pf.showAlertTimeOut(getActivity());
                    cancel(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    //pf.showAlertTimeOut(getActivity());
                    cancel(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                // Show your Toast
                loadingDialog.dismiss();
                pf.showAlertTimeOut(getActivity());
            }

            @Override
            protected void onPostExecute(JSONArray result) {
                loadingDialog.dismiss();
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject e = result.getJSONObject(i);
                            String istilah = e.getString(TAG_ISTILAH);
                            String keterangan = e.getString(TAG_KETERANGAN);

                            modelGlosarium modelGlosarium = new modelGlosarium();
                            modelGlosarium.setIstilah(istilah);
                            modelGlosarium.setKeterangan(keterangan);
                            glosariumArrayList.add(modelGlosarium);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public class modelGlossariumAdapter extends RecyclerView.Adapter<modelGlossariumAdapter.ViewHolder> {
        private ArrayList<modelGlosarium> glosariumList;
        private Activity activity;

        PublicFunction pf = new PublicFunction();

        public modelGlossariumAdapter(ArrayList<modelGlosarium> glosariumList, Activity activity) {
            this.glosariumList = glosariumList;
            this.activity = activity;
        }

        @Override
        public modelGlossariumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            double screenInches = pf.getScreenInches(activity);
            if (screenInches <= 4.1) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium, parent, false);
            } else if (screenInches > 4.0 && screenInches <= 5.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium, parent, false);
            } else if (screenInches > 5.0 && screenInches <= 6.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium, parent, false);
            } else if (screenInches > 6.0 && screenInches <= 7.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium_tujuh, parent, false);
            } else if (screenInches > 7.0 && screenInches <= 8.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium_tujuh, parent, false);
            } else if (screenInches > 8.0 && screenInches <= 9.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium_tujuh, parent, false);
            } else if (screenInches > 9.0 && screenInches <= 10.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium_tujuh, parent, false);
            } else if (screenInches > 10.0 && screenInches <= 11.0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_glosarium_tujuh, parent, false);
            }


            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(modelGlossariumAdapter.ViewHolder holder, int position) {
            String istilah = glosariumList.get(position).getIstilah();
            String keterangan = glosariumList.get(position).getKeterangan();

            holder.istilah.setText(istilah);
            holder.keterangan.setText(keterangan);
        }

        @Override
        public int getItemCount() {
            return glosariumList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView istilah, keterangan;

            public ViewHolder(View view) {
                super(view);

                istilah = (TextView) view.findViewById(R.id.tvistilah);
                keterangan = (TextView) view.findViewById(R.id.tvketerangan);

            }

        }
    }
}
