package com.kaixin.mykey;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 自定义
 *
 * @Author KenChung
 */
public class ReWebViewClient extends WebViewClient {

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        view.loadUrl("about:blank");// 避免出现默认的错误界面
        view.removeAllViews();
        int width = (int) DeviceUtils.getScreenWidth(CategoryDetailActivity.this);
        int height = (int) DeviceUtils.dip2px(CategoryDetailActivity.this, 230);
        view.addView(hintWeb, width, height);
        iv_live_cover.setVisibility(View.GONE);
        webViewProgress.setVisibility(View.GONE);
        tv_network_error_hint.setVisibility(View.VISIBLE);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

}