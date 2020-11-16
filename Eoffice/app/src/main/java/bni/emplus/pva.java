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


public class pva extends AppCompatActivity {

    String id,versi,npp,apikey;
    WebView view;
    UserSessionManager session;

    private static final String TAG_ID = "Id";
    private static final String TAG_ERROR = "ErrorMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(Build.VERSION.SDK_INT >= 21){
            ((FrameLayout) pva.this.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT)). setForeground(null);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(51, 155, 175));
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_pdf_view);

        //session
        session = new UserSessionManager(pva.this);
        if (session.checkLogin()) {
            Intent i = new Intent(pva.this, log.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        HashMap<String, String> user = session.getUserNPP();

        // get npp,empid,roleid,unitid
        npp = user.get(UserSessionManager.KEY_NAME);


        Bundle args = getIntent().getExtras();
        if(args!=null){
            id = (String) args.get("id");
            versi = (String) args.get("versi");
            apikey = (String) args.get("apikey");
        }

        view = (WebView) findViewById(R.id.webView);

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
        view.getSettings().setAllowUniversalAccessFromFileURLs(true);

        String url = Config.urlLanding + "GetFileInfo.php";
        //setelah pagar dikasih id

        view.loadUrl("file:///android_asset/www/index.html#/page1#" + id + "#" + versi + "#" + npp + "#" + apikey + "#" + url);

}
