package arieluniversity.vicationmusic;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewClicker extends Activity implements View.OnTouchListener, Handler.Callback {

    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;

    private final Handler handler = new Handler(this);

    private WebView webView;
    private WebViewClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_clicker);

        webView = (WebView)findViewById(R.id.web);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setOnTouchListener(this);

        client = new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };

        webView.setWebViewClient(client);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl("https://www.youtube.com");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.web && event.getAction() == MotionEvent.ACTION_DOWN){
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == CLICK_ON_URL){
            handler.removeMessages(CLICK_ON_WEBVIEW);
            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW){
            //Toast.makeText(this, "WebView clicked", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, webView.getOriginalUrl(), Toast.LENGTH_SHORT).show();
            MainActivity.url = webView.getOriginalUrl();
            if (MainActivity.url.contains("watch?v")){
                finish();
            }


            return true;
        }
        return false;
    }
}
