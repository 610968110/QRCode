package lbx.com.qrcodelib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

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
 * @date 2018/8/19.
 */

public class QrScanView extends ViewfinderView {

    private final int[] colors = new int[]{Color.parseColor("#7870FD"), Color.parseColor("#3296FA")};
    /**
     * 扫描区边角的宽
     */
    private static final int CORNER_RECT_WIDTH = 8;
    /**
     * 扫描区边角的高
     */
    private static final int CORNER_RECT_HEIGHT = 14 * 3;
    /**
     * 扫描线宽度
     */
    private static final int SCANNER_LINE_HEIGHT = 3;
    /**
     * 扫描线移动距离
     */
    private static final int SCANNER_LINE_MOVE_DISTANCE = 5;
    private static final int SCANNER_LINE_RECT_HEIGHT = 300;
    private Rect mConnerRect;
    private RectF mLineRectF;
    private Paint mConnerPaint;
    private Paint mLinePaint;

    public QrScanView(Context context) {
        this(context, null);
    }

    public QrScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QrScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mConnerRect = new Rect();
        mConnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView);
        int lineColor = a.getColor(R.styleable.ViewfinderView_laser_color, Color.WHITE);
        a.recycle();
        mLinePaint.setColor(lineColor);
        mLineRectF = new RectF();
    }

    @Override
    public void drawCorner(Canvas canvas, Rect frame) {
        //左上
        mConnerRect.set(frame.left, frame.top, frame.left + CORNER_RECT_HEIGHT, frame.top + CORNER_RECT_HEIGHT);
        mConnerPaint.setShader(autoRender(mConnerRect));
        canvas.drawRect(mConnerRect, mConnerPaint);
        mConnerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.save();
        canvas.translate(CORNER_RECT_WIDTH, CORNER_RECT_WIDTH);
        canvas.drawRect(mConnerRect, mConnerPaint);
        canvas.restore();
        mConnerPaint.setXfermode(null);
        //右上
        mConnerRect.set(frame.right - CORNER_RECT_HEIGHT, frame.top, frame.right, frame.top + CORNER_RECT_HEIGHT);
        mConnerPaint.setShader(autoRender(mConnerRect));
        canvas.drawRect(mConnerRect, mConnerPaint);
        mConnerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.save();
        canvas.translate(-CORNER_RECT_WIDTH, CORNER_RECT_WIDTH);
        canvas.drawRect(mConnerRect, mConnerPaint);
        canvas.restore();
        mConnerPaint.setXfermode(null);
        //左下
        mConnerRect.set(frame.left, frame.bottom - CORNER_RECT_HEIGHT, frame.left + CORNER_RECT_HEIGHT, frame.bottom);
        mConnerPaint.setShader(autoRender(mConnerRect));
        canvas.drawRect(mConnerRect, mConnerPaint);
        mConnerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.save();
        canvas.translate(CORNER_RECT_WIDTH, -CORNER_RECT_WIDTH);
        canvas.drawRect(mConnerRect, mConnerPaint);
        canvas.restore();
        mConnerPaint.setXfermode(null);
        //右下
        mConnerRect.set(frame.right - CORNER_RECT_HEIGHT, frame.bottom - CORNER_RECT_HEIGHT, frame.right, frame.bottom);
        mConnerPaint.setShader(autoRender(mConnerRect));
        canvas.drawRect(mConnerRect, mConnerPaint);
        mConnerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.save();
        canvas.translate(-CORNER_RECT_WIDTH, -CORNER_RECT_WIDTH);
        canvas.drawRect(mConnerRect, mConnerPaint);
        canvas.restore();
        mConnerPaint.setXfermode(null);
    }

    private LinearGradient autoRender(Rect rect) {
        return new LinearGradient(
                rect.left, rect.top,
                rect.right, rect.bottom,
                colors[0], colors[1],
                Shader.TileMode.CLAMP);
    }

    @Override
    public void drawLaserScanner(Canvas canvas, Rect frame) {
        if (scannerStart <= scannerEnd) {
            canvas.save();
            //线
            mLineRectF.set(frame.left, scannerStart,
                    frame.right, scannerStart + SCANNER_LINE_HEIGHT);
            mLinePaint.setAlpha(255);
            canvas.drawRect(mLineRectF, mLinePaint);
            //矩形
            mLineRectF.set(frame.left, Math.max(frame.top, scannerStart - SCANNER_LINE_RECT_HEIGHT),
                    frame.right, scannerStart + SCANNER_LINE_HEIGHT);
            canvas.translate(0, -SCANNER_LINE_HEIGHT);
            mLinePaint.setAlpha(180);
            mLinePaint.setShader(new LinearGradient(
                    mLineRectF.left, mLineRectF.bottom - SCANNER_LINE_RECT_HEIGHT,
                    mLineRectF.left, mLineRectF.bottom,
                    Color.TRANSPARENT, mLinePaint.getColor(),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(mLineRectF, mLinePaint);
            mLinePaint.setShader(null);
            scannerStart += SCANNER_LINE_MOVE_DISTANCE;
            canvas.restore();
        } else {
            scannerStart = frame.top;
        }
    }
}
