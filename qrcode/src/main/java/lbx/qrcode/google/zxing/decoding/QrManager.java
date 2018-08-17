package lbx.qrcode.google.zxing.decoding;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import lbx.qrcode.R;
import lbx.qrcode.google.zxing.android.IQrCodeCallback;
import lbx.qrcode.google.zxing.camera.CameraManager;
import lbx.qrcode.google.zxing.encoding.EncodingHandler;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

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

public class QrManager {

    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
        return EncodingHandler.createQRCode(str, 500);
    }

    private static final long VIBRATE_DURATION = 200L;
    private boolean hasSurface;
    private Context mContext;
    private CaptureActivityHandler handler;
    private static final float BEEP_VOLUME = 0.10f;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private InactivityTimer inactivityTimer;
    private Activity mActivity;
    private boolean vibrate;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private IQrCodeCallback mCallback;

    public QrManager(Activity activity) {
        if (!(activity instanceof IQrCodeCallback)) {
            throw new RuntimeException("Activity must be implements IQrCodeCallback");
        }
        this.mContext = activity.getApplicationContext();
        this.mActivity = activity;
        mCallback = (IQrCodeCallback) activity;
        inactivityTimer = new InactivityTimer(activity);
        CameraManager.init(mContext);
    }

    public void onResume() {
        SurfaceView surfaceView = mCallback.getSurfaceView();
        if (surfaceView != null) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            if (hasSurface) {
                initCamera(surfaceHolder);
            } else {
                surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        if (!hasSurface) {
                            hasSurface = true;
                            initCamera(holder);
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        hasSurface = false;
                    }
                });
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.getInstance().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(mCallback, this, decodeFormats, characterSet);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = mActivity.getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    public void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.getInstance().closeDriver();
    }

    public void onDestroy() {
        inactivityTimer.shutdown();
    }

    private final MediaPlayer.OnCompletionListener beepListener = mediaPlayer1 -> mediaPlayer1.seekTo(0);

    void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if (mCallback != null) {
            mCallback.scanResult(result.getText());
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public static String scanningImage(String path) throws Exception {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        //设置二维码内容的编码
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 先获取原大小
        options.inJustDecodeBounds = true;
        Bitmap scanBitmap;
        // 获取新的大小
        options.inJustDecodeBounds = false;
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        if (scanBitmap == null) {
            return "";
        }
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        return reader.decode(bitmap1, hints).toString();
    }
}
