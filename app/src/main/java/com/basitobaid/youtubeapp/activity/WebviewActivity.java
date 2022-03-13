package com.basitobaid.youtubeapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.DialogUtils;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.util.concurrent.ConcurrentHashMap;

public class WebviewActivity extends AppCompatActivity {

    String intentUrl;
    WebView webView;
    private boolean dialogShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = findViewById(R.id.webview);
        if (getIntent().getStringExtra(Constant.URL) != null) {
            intentUrl = getIntent().getStringExtra(Constant.URL);
        }
        loadUrl();

    }

    private void loadUrl() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().equalsIgnoreCase(intentUrl)) {
                        view.loadUrl(request.getUrl().toString());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    view.loadUrl(intentUrl);
                    return true;
                }
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, final WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().equalsIgnoreCase(intentUrl)) {
                        return super.shouldInterceptRequest(view, request);
                    } else
                        return null;
                } else {
                    return null;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("TAG", "All the cookies in a string:" + cookies);
                if (!cookies.contains("SID") && !dialogShown) {
                    dialogShown = true;
                    showInfoDialog(WebviewActivity.this, getString(R.string.subscribe_login));
                }
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.getSaveFormData();
        webSettings.setSupportMultipleWindows(true);
        webView.loadUrl(intentUrl);
    }

    public static void showInfoDialog(Context context, String message) {
        new FancyGifDialog.Builder((Activity) context)
                .setTitle("Info!")
                .setMessage(message)
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground("#636fdf")
                .setGifResource(R.drawable.info)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(() -> {

                })
                .build();
    }

    @Override
    public void onBackPressed() {
//        if (webView.canGoBack()) {
//            webView.goBack();
//        } else {
            Intent intent = new Intent();
            intent.setData(Uri.parse("ok"));
            setResult(12, intent);
            finish();
//            super.onBackPressed();
//        }
    }
}
