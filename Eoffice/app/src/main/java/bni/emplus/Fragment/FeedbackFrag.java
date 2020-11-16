package bni.emplus.Fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.HashMap;

import bni.emplus.R;
import bni.emplus.UserSessionManager;
import bni.emplus.log;
import bni.emplus.pva;

/**
 * Created by Zulfah Permata on 14/11/2018.
 */

public class FeedbackFrag extends Fragment {
    UserSessionManager session;
    String npp;
    WebView view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_pdf_view, container, false);

        //session
        session = new UserSessionManager(getActivity());
        if (session.checkLogin()) {
            Intent i = new Intent(getActivity(), log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            getActivity().finish();
        }

        HashMap<String, String> user = session.getUserNPP();

        // get npp,empid,roleid,unitid
        npp = user.get(UserSessionManager.KEY_NAME);

        view = (WebView) v.findViewById(R.id.webView);

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
        view.getSettings().setAllowUniversalAccessFromFileURLs(true);

        view.loadUrl("http://10.70.8.77/feedback/#!/52625/zulfah.permata@bni.co.id/emplus");

        return v;
    }
}
