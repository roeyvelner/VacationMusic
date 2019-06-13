package arieluniversity.vicationmusic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webexplorer);

        WebView webView =(WebView) this.findViewById(R.id.web);
        //WebView webView = new WebView(this);
        //webView.setWebChromeClient(new WebChromeClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent iData = new Intent();
                iData.putExtra("url", url );

                setResult(android.app.Activity.RESULT_OK, iData );
                finish();
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
      //  webView.getSettings().setBlockNetworkLoads(true);
        //setContentView(webView);
        webView.loadUrl("http://xn----5hccewa2a7fdij.com/");//http://www.xn----5hccebza6a1gejk.com/he/
    }
}
