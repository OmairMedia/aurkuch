package com.basitobaid.youtubeapp.activity;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.WebEngine;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webview;
    WebEngine webEngine;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        if (getIntent().getStringExtra(Constant.FROM) != null) {
            from = getIntent().getStringExtra(Constant.FROM);
        }
        initWebView();
    }

    private void initWebView() {

        webEngine = new WebEngine(webview, this);
        webEngine.initWebView();
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
////                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
////                loadShimmer();
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
////                unloadShimmer();
//            }
//        });
//        webView.getSettings().setJavaScriptEnabled(true);

        if (from.equals(Constant.PRIVACY)) {
            toolbar.setTitle("Privacy Policy");
            webEngine.loadHtml(AppPreferences.getPrivacyPolicy() + Constant.CSS_PROPERTIES);

        } else if (from.equals(Constant.TAC)) {
            toolbar.setTitle("Terms and Conditions");
            webEngine.loadHtml(AppPreferences.getTermsAndConditions() + Constant.CSS_PROPERTIES);
        } else {
            toolbar.setTitle("About Us");
            webEngine.loadHtml(AppPreferences.getAboutUs() + Constant.CSS_PROPERTIES);

        }


    }
}
