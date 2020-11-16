package bni.emplus;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by FubianLathanio on 23/05/2017.
 */
public class PublicFunction {
    private static final String TAG_TOTDIS = "TotalInboxAll_D";
    private static final String TAG_TOTUR = "TotalInboxAll_U";
    private static final String TAG_TOTAPP = "TotalApprovalOnline";
    private static final String TAG_TOTINBOX = "TotalInboxAll";
    private static final String TAG_TOTSENT = "TotalSentItems";
    private static final String TAG_TOTMONITOR = "TotalMonitoring";
    private static final String TAG_TOTTRACK = "TotalTrackingHistory";
    private static final String TAG_ONLINE = "TotalDisposisiOnline";
    private static final String TAG_OFFLINE = "TotalDisposisiOffline";
    private static final String TAG_LIST = "TotalListDokumen";
    private static final String TAG_TOTFAV = "TotalFavourite";

    UserSessionManager session;

    public String validateSearchCharacter(String search)
    {
        String[] sqlCheckList = { "=","--",";--",";","/*","*/","@@","@","char","nchar","varchar","nvarchar",                                       "alter",
                "begin","cast","create","cursor","declare","delete","drop","end","exec",
                "execute","fetch","insert","kill","select","sys","sysobjects","syscolumns",
                "table","update","]","["};

        //String newSearch = "";
        for (int i = 0; i < sqlCheckList.length; i++)
        {
            search = search.replace(sqlCheckList[i], "");
        }
        return search;
    }

    public double getScreenInches(Activity activity)
    {
        //cek ukuran layar dan menentukan layout yang dipakai
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        return  screenInches;
    }

    public boolean isActiveGoogleService(Activity activity)
    {
        boolean flagGooglePlayService = false;
        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(activity.getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(activity.getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            flagGooglePlayService = true;
        }

        return  flagGooglePlayService;
    }

    public void showAlertConnection(final Activity activity){
        try {
            Toast.makeText(activity, Config.alertConnection,
                    Toast.LENGTH_LONG).show();
        }
        catch(Exception e) {}
    }

    public void showAlertTimeOut(final Activity activity){
        try
        {
            Toast.makeText(activity, Config.alertTimeOut,
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e){}
    }

    public void showAlert(final Activity activity, final String Pesan, final String Title){
        try {
            Toast.makeText(activity, Pesan,
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e){}
    }
}
