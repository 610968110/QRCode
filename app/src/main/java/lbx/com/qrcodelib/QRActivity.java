package lbx.com.qrcodelib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import lbx.qrcode.google.zxing.android.IQrCodeCallback;
import lbx.qrcode.google.zxing.decoding.QrManager;
import lbx.qrcode.google.zxing.view.ViewfinderView;


/**
 * @author lbx
 */
public class QRActivity extends AppCompatActivity implements IQrCodeCallback {

    private ViewfinderView viewfinderView;
    private SurfaceView mSurfaceView;
    private QrManager mManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        mManager = new QrManager(this);
        viewfinderView = (ViewfinderView) findViewById(R.id.vfv_main);
        mSurfaceView = (SurfaceView) findViewById(R.id.scanner_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mManager.onPause();
    }

    @Override
    public void onDestroy() {
        mManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    public void scanResult(String result) {
        //这里接收扫描结果
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(RESULT_OK, intent);
        finish();
    }
}