package wm.com.danteater.tab_music;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.HUD;

/**
 * Created by nirav on 30-09-2014.
 */
public class ReadChord extends BaseActivity {
    String filePath;
    private WebView webview;
    private HUD dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_chord_view);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        txtHeader.setText("Noder");
        Intent i=getIntent();
        filePath=i.getStringExtra("file_url");
        Log.e("file path: ",filePath+"");
        webview=(WebView)findViewById(R.id.read_chord_view);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDisplayZoomControls(false);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        dialog = new HUD(ReadChord.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Indlæser noder");
        dialog.show();
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("Error:",""+description);

            }
        });

//        webview.loadUrl("http://www.adobe.com/devnet/acrobat/pdfs/"+filePath);

        String doc="<iframe src='http://docs.google.com/viewer?embedded=true&url="+filePath+"'width='100%' height='100%'style='border: none;'></iframe>";
//        webview.loadUrl(doc);
        webview.loadUrl("https://docs.google.com/gview?embedded=true&url="+filePath);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case android.R.id.home:

                finish();

                break;


        }


        return super.onOptionsItemSelected(item);
    }

}
