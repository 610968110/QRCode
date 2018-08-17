package lbx.qrcode.google.zxing.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;

import lbx.qrcode.google.zxing.view.ViewfinderView;

/**
 * .  ┏┓　　　┏┓
 * .┏┛┻━━━┛┻┓
 * .┃　　　　　　　┃
 * .┃　　　━　　　┃
 * .┃　┳┛　┗┳　┃
 * .┃　　　　　　　┃
 * .┃　　　┻　　　┃
 * .┃　　　　　　　┃
 * .┗━┓　　　┏━┛
 * .    ┃　　　┃        神兽保佑
 * .    ┃　　　┃          代码无BUG!
 * .    ┃　　　┗━━━┓
 * .    ┃　　　　　　　┣┓
 * .    ┃　　　　　　　┏┛
 * .    ┗┓┓┏━┳┓┏┛
 * .      ┃┫┫　┃┫┫
 * .      ┗┻┛　┗┻┛
 *
 * @author lbx
 * @date 2018/8/17.
 */

public interface IQrCodeCallback {

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onDestroy();

    void finish();

    ViewfinderView getViewfinderView();

    SurfaceView getSurfaceView();

    void startActivity(Intent intent);

    void setResult(int result, Intent intent);

    void scanResult(String result);
}
