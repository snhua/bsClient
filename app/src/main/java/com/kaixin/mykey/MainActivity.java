package com.kaixin.mykey;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.kaixin.mykey.zbar.CaptureActivity;

import java.io.File;

import wendu.webviewjavascriptbridge.WVJBWebView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends Activity implements ReWebChomeClient.OpenFileChooserCallBack {
    private static final int REQUEST_CODE_SCAN = 0x0000;// 扫描二维码
    private static final String TAG = "MyActivity";
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private Intent mSourceIntent;
    private ValueCallback<Uri> mUploadMsg;
    private ValueCallback<Uri[]> mUploadMsg5Plus;

    WVJBWebView webView;
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

        setContentView(R.layout.activity_main);


        webView = findViewById(R.id.webview);
        webView.setWebChromeClient(new ReWebChomeClient(this));
        webView.setWebViewClient(new ReWebViewClient());


        setting(webView.getSettings());

        webView.registerHandler("scan", new WVJBWebView.WVJBHandler() {
            @Override
            public void handler(Object data, WVJBWebView.WVJBResponseCallback callback) {
                scan();
//                callback.onResult("Response from testJavaCallback");
            }
        });

        fixDirPath();
//        webView.loadUrl("http://192.168.101.71:9099/webqr/qr2.html");
        webView.loadUrl("http://192.168.101.242:8085/mykey/#/");
//        webView.loadUrl("http://192.168.101.71:8085/mykey/#/");


    }

    void setting(WebSettings settings) {

        settings.setAppCacheEnabled(true);
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
//        = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        settings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.setAllowUniversalAccessFromFileURLs(false);
        //开启JavaScript支持
        settings.setJavaScriptEnabled(true);
        // 支持缩放
        settings.setSupportZoom(true);


    }

    @JavascriptInterface
    public void scan() {
        //动态权限申请
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            goScan();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
//        super.onBackPressed();
    }

    /**
     * 跳转到扫码界面扫码
     */
    private void goScan() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode) {

            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 0:
                mSourceIntent = ImageUtil.takeBigPicture();
                startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                break;
            case 2:
                mSourceIntent = ImageUtil.choosePicture();
                startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                break;
            default:
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
            case REQUEST_CODE_PICK_IMAGE: {
                    try {
                        if (mUploadMsg == null && mUploadMsg5Plus == null) {
                            return;
                        }
                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);
                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
                            Log.w(TAG, "sourcePath empty or not exists.");


                            if (mUploadMsg != null) {
                                mUploadMsg.onReceiveValue(null);
                                mUploadMsg = null;
                            } else {
                                mUploadMsg5Plus.onReceiveValue(null);
                                mUploadMsg5Plus = null;
                            }
                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        if (mUploadMsg != null) {
                            mUploadMsg.onReceiveValue(uri);
                            mUploadMsg = null;
                        } else {
                            mUploadMsg5Plus.onReceiveValue(new Uri[]{uri});
                            mUploadMsg5Plus = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_SCAN:// 二维码
                // 扫描二维码回传
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        //获取扫描结果
                        Bundle bundle = data.getExtras();
                        String result = bundle.getString(CaptureActivity.EXTRA_STRING);
//                        webView.loadUrl(String.format("javascript:window.scanResult('%s')",result));
                        webView.callHandler("scanResult", result, new WVJBWebView.WVJBResponseCallback() {
                            @Override
                            public void onResult(Object data) {

                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUploadMsg = uploadMsg;
        showOptions();
    }


    @Override
    public void showFileChooserCallBack(ValueCallback<Uri[]> filePathCallback) {
        mUploadMsg5Plus = filePathCallback;
        showOptions();
    }


    public void showOptions() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setOnCancelListener(new ReOnCancelListener());
        alertDialog.setTitle(R.string.options);
        alertDialog.setItems(R.array.options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //动态权限申请
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    } else {
                        mSourceIntent = ImageUtil.choosePicture();
                        startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                    }
                } else {
                    //动态权限申请
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                    } else {
                        mSourceIntent = ImageUtil.takeBigPicture();
                        startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                    }
                }
            }
        });
        alertDialog.show();
    }


    private void fixDirPath() {
        String path = ImageUtil.getDirPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    private class ReOnCancelListener implements DialogInterface.OnCancelListener {


        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (mUploadMsg != null) {
                mUploadMsg.onReceiveValue(null);
                mUploadMsg = null;
            }
            if (mUploadMsg5Plus != null) {
                mUploadMsg5Plus.onReceiveValue(null);
                mUploadMsg5Plus = null;
            }
        }
    }
}
