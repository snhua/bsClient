package com.kaixin.mykey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import wendu.webviewjavascriptbridge.WVJBWebView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends BaseActivity {
    private WebViewClient client = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setStatusBarColor(this,R.color.status_col);

        setContentView(R.layout.activity_fullscreen);


        WVJBWebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(client);
        webView.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);


        webView.registerHandler("result", new WVJBWebView.WVJBHandler() {
            @Override
            public void handler(Object data, WVJBWebView.WVJBResponseCallback callback) {
                result((String) data);
//                callback.onResult("Response from testJavaCallback");
            }
        });

        Intent intent = getIntent();
        String redirectUrl = intent.getStringExtra("redirect_uri");
        String clientId = intent.getStringExtra("client_id");
        String url = String.format("https://mykey.mtx6.com/mykey/#/authorization?redirect_uri=%s&client_id=%s",
                redirectUrl, clientId);
        Log.e("err", url);
        webView.loadUrl(url);
//        webView.addJavascriptInterface(this, "JsBridge");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @JavascriptInterface
    public void result(String data) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", data);
        setResult(Activity.RESULT_OK, resultIntent);
        this.finish();
    }


}
