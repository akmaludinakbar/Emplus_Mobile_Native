package bni.emplus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.HashMap;

/**
 * Created by Zulfah Permata on 15/11/2018.
 */

public class fba extends AppCompatActivity {
    WebView view;
    UserSessionManager session;
    String npp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= 21) {
            ((FrameLayout) fba.this.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT)).setForeground(null);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_pdf_view);

        //session
        session = new UserSessionManager(fba.this);
        if (session.checkLogin()) {
            Intent i = new Intent(fba.this, log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        HashMap<String, String> user = session.getUserNPP();

        // get npp,empid,roleid,unitid
        npp = user.get(UserSessionManager.KEY_NAME);
        Log.d("npp",npp);

        view = (WebView) findViewById(R.id.webView);

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
        view.getSettings().setAllowUniversalAccessFromFileURLs(true);

        //setelah pagar dikasih id
        view.loadUrl("http://10.70.8.77/feedback/#!/52625/zulfah.permata@bni.co.id/emplus");
        //view.loadUrl("file:///android_asset/www/index.html#/page1#78ca7597d06287fe3ac67865fdbb9d25#911310a8ae6f5f1bb11c635b71560a10#52625#e91b0e31f9c9ce12e6c952673c53e2ebfe729d53af7156736a8f42e3cb7bf145#http://192.168.46.50/eofficesvc/GetFileInfo.php");
    }
}
