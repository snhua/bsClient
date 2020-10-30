package com.kaixin.mykey;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.kaixin.mykey.zbar.CaptureActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import wendu.webviewjavascriptbridge.WVJBWebView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends BaseActivity implements ReWebChomeClient.OpenFileChooserCallBack {
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

//        Intent intent = new Intent(this, SplashActivity.class);
//        startActivity(intent);

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
        hasInternetConnected();
//        webView.loadUrl("http://192.168.101.71:9099/webqr/qr2.html");
//        webView.loadUrl("https://mykey.mtx6.com/mykey/#/");
          webView.loadUrl("https://k.mykeyets.com/mykey/#/");
//          webView.loadUrl("http://192.168.1.9:8085/key/#/");
//        webView.loadUrl("http://192.168.101.242:8085/key/#/");
//        webView.loadUrl("http://192.168.101.100:8085/mykey/#/");
//       webView.loadUrl("http://192.168.101.71/key/#/");
//        webView.loadUrl("http://192.168.101.100/mykey/#/");
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // TODO: 2017-5-6 处理下载事件
                downloadByBrowser(url);
            }
        });

    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    void setting(WebSettings settings) {

        settings.setAppCacheEnabled(true);
        //设置 缓存模式
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(this, "您拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 0:
//                mSourceIntent = ImageUtil.takeBigPicture();
//                startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);

                openCamera(REQUEST_CODE_IMAGE_CAPTURE);
                break;
            case 2:
                Log.d(TAG, "onRequestPermissionsResult: 2");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSourceIntent = ImageUtil.choosePicture();
                    startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                }else{
                    Toast.makeText(this, "您拒绝了读取文件的权限申请，可能无法获取图片！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE: {
                Log.d(TAG, "onActivityResult: "+data);
                String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);
                Log.e("sourcePath",sourcePath);
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

                File photoFile = null;
                Uri photoUri = null;

                if (isAndroidQ) {
                    // 适配android 10
                    photoUri = createImageUri();
                } else {
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        mCameraImagePath = photoFile.getAbsolutePath();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                        } else {
                            photoUri = Uri.fromFile(photoFile);
                        }
                    }
                }
                Bitmap bitmap = getBitmapWithRightRotation(sourcePath);

                File file= null;//new File(mCameraImagePath);//将要保存图片的路径
                if(isAndroidQ) {
                    try {
                        file = new File(createImageFile().getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    file = new File(mCameraImagePath);
                }
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }


                Uri uri = Uri.fromFile(file);//new File(sourcePath)
                if (mUploadMsg != null) {
                    Log.e("mUploadMsg", Objects.requireNonNull(uri.getPath()));
                    mUploadMsg.onReceiveValue(uri);
                    mUploadMsg = null;
                } else {

                    Log.e("mUploadMsg5Plus",uri.getPath());
                    mUploadMsg5Plus.onReceiveValue(new Uri[]{uri});
                    mUploadMsg5Plus = null;
                }
            }
            break;
            case REQUEST_CODE_IMAGE_CAPTURE: {
                try {
                    if (mUploadMsg == null && mUploadMsg5Plus == null) {
                        return;
                    }

                    if (isAndroidQ) {
                        // Android 10 使用图片uri加载
                        if (mUploadMsg != null) {
                            mUploadMsg.onReceiveValue(mCameraUri);
                            mUploadMsg = null;
                        } else {
                            mUploadMsg5Plus.onReceiveValue(new Uri[]{mCameraUri});
                            mUploadMsg5Plus = null;
                        }
                    } else {
                        // 使用图片路径加载

                        Bitmap bitmap = getBitmapWithRightRotation(mCameraImagePath);
                        File file=new File(mCameraImagePath);//将要保存图片的路径
                        try {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        Uri uri = Uri.fromFile(file);//(new File(mCameraImagePath));
                        if (mUploadMsg != null) {
                            mUploadMsg.onReceiveValue(uri);
                            mUploadMsg = null;
                        } else {
                            mUploadMsg5Plus.onReceiveValue(new Uri[]{uri});
                            mUploadMsg5Plus = null;
                        }
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
                        Log.d(TAG, "onClick: permissions");
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    } else {
                        Log.d(TAG, "onClick: picture");
                        mSourceIntent = ImageUtil.choosePicture();
                        startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                    }
                } else {
                    //动态权限申请
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                    } else {
//                        mSourceIntent = ImageUtil.takeBigPicture();
//                        startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                        openCamera(REQUEST_CODE_IMAGE_CAPTURE);
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


    //用于保存拍照图片的uri
    private Uri mCameraUri;

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;

    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    /**
     * 调起相机拍照
     */
    private void openCamera(int code) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    mCameraImagePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, code);
            }
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 创建保存图片的文件
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date())+".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    /**
     * 获取正确的旋转角度的图片——一般由系统相机拍照才会导致此情况
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static Bitmap getBitmapWithRightRotation(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.e("CameraUtils", "degree: " + degree);
        Bitmap bm = BitmapFactory.decodeFile(path);
        //照片没有被旋转角度，直接返回原图片
        if (degree == 0) {
            return bm;
        }
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public boolean hasInternetConnected() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null){
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info !=null && info.isConnectedOrConnecting() ){
                return true;
            }
        }

        openWirelessSet();
        return false;
    }
    public void openWirelessSet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("请检查您的网络连接")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

}
