# 一款扫描二维码/条形码的简单实现框架 

需要权限
````
    <uses-permission android:name="android.permission.INTERNET" /> <!-- 网络权限 -->
    <uses-permission android:name="android.permission.VIBRATE" /> <!-- 震动权限 -->
    <uses-permission android:name="android.permission.CAMERA" /> <!-- 摄像头权限 -->
    <uses-feature andrgit poid:name="android.hardware.camera.autofocus" /> <!-- 自动聚焦权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

````

依赖框架：
````
compile 'com.google.zxing:core:3.3.0'
````

本项目导入：
````
compile 'com.lbx:qrcode:1.0.0'
````

## 1、初始化：
````
 mManager = new QrManager(this)
````

## 2、扫描二维码

扫描二维码的页面实现接口:IQrCodeCallback

在IQrCodeCallback的回调里调用QrManager对应的方法，例如：
````
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
````

## 3、扫描图片中的二维码
````
  String s;
  try {
      //耗时操作，建议在子线程处理
      s = QrManager.scanningImage(path);
      qrCodeText.setText(s);
  } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(this, "未找到二维码！", Toast.LENGTH_SHORT).show();
  }
````

## 4、文字转二维码：
````
    //根据输入的文本生成对应的二维码
    Bitmap bitmap = QrManager.createQRCode(text, 500);
    if (bitmap != null) {
        Toast.makeText(this, "二维码生成成功！", Toast.LENGTH_SHORT).show();
    }
````

## 5、控件说明
ViewfinderView是扫码界面中间的方框:

属性 | 说明
------- | ---------
corner_color |  边角颜色
scanner_color | 扫描线颜色
possible_result_color | 扫描点颜色
frame_color | 扫描框边线颜色
mask_color | 模糊区域颜色
label_text  | 框上方提示
label_text_position | 文字位置(top/bottom/none)
frame_offset | 扫描框的上下偏移量

