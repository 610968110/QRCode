package lbx.com.qrcodelib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lbx.qrcode.google.zxing.decoding.QrManager;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.openQrCodeScan)
    Button openQrCodeScan;
    @BindView(R.id.text)
    EditText text;
    @BindView(R.id.iv_img)
    ImageView imageView;
    @BindView(R.id.qrCodeText)
    TextView qrCodeText;
    //打开扫描界面请求码
    private static final int REQUEST_CODE_SCAN = 0x01;
    private static final int PHOTO_REQUEST_CODE = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @OnClick({R.id.openQrCodeScan, R.id.btn_createQrCode, R.id.btn_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openQrCodeScan:
                //打开二维码扫描界面
                Intent intent = new Intent(MainActivity.this, QRActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            case R.id.btn_createQrCode:
                try {
                    //获取输入的文本信息
                    String str = text.getText().toString().trim();
                    if (!"".equals(str.trim())) {
                        //根据输入的文本生成对应的二维码并且显示出来
                        Bitmap bitmap = QrManager.createQRCode(text.getText().toString(), 500);
                        if (bitmap != null) {
                            Toast.makeText(this, "二维码生成成功！", Toast.LENGTH_SHORT).show();
                            imageView.setImageBitmap(bitmap);
                        }
                    } else {
                        Toast.makeText(this, "文本信息不能为空！", Toast.LENGTH_SHORT).show();
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_photo:
                //扫描图片
                Intent picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picIntent, PHOTO_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    String scanResult = data.getStringExtra("result");
                    //将扫描出的信息显示出来
                    qrCodeText.setText(scanResult);
                    break;
                case PHOTO_REQUEST_CODE:
                    String path = "-1";
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        }
                        cursor.close();
                    }
                    String s;
                    try {
                        //耗时操作，建议在子线程处理
                        s = QrManager.scanningImage(path);
                        qrCodeText.setText(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "未找到二维码！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
