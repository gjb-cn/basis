package com.yft.zbase.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.yft.zbase.R;

public class RoundLinearLayout extends LinearLayout {
    private String TAG = getClass().getSimpleName();
    private final RectF roundRect = new RectF();
    private float roundCorner = 0;
    private final Paint maskPaint = new Paint();
    private final Paint zonePaint = new Paint();
    private final Paint strokePaint = new Paint();
    public float mStrokeWidth = 0;
    public int mStrokeColor = Color.TRANSPARENT;

    public RoundLinearLayout(Context context) {
        this(context, null);
    }

    public RoundLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundLayoutAttr);
            mStrokeColor = ta.getColor(R.styleable.RoundLayoutAttr_stroke_color, mStrokeColor);
            mStrokeWidth = ta.getDimensionPixelSize(R.styleable.RoundLayoutAttr_stroke_width, 0);
            roundCorner = ta.getDimensionPixelSize(R.styleable.RoundLayoutAttr_round_corner, 0);
            ta.recycle();
        }

        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        maskPaint.setStyle(Paint.Style.FILL);
        //
        zonePaint.setAntiAlias(true);
        zonePaint.setColor(Color.WHITE);
        //
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(mStrokeColor);
        strokePaint.setStrokeWidth(mStrokeWidth);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    public void setCorner(float radius) {
        if (radius > 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            roundCorner = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, radius, displayMetrics);
        } else {
            roundCorner = 0;
        }
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        if (strokeWidth > 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, strokeWidth, displayMetrics);
        } else {
            mStrokeWidth = 0;
        }
        strokePaint.setStrokeWidth(mStrokeWidth);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int w = getWidth();
        int h = getHeight();
        roundRect.set(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        if (roundCorner > 0) {
            canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
            canvas.drawRoundRect(roundRect, roundCorner, roundCorner, zonePaint);
            canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.restore();
            RectF roundRect2 = new RectF();
            roundRect2.set(roundRect.left + mStrokeWidth / 2, roundRect.top + mStrokeWidth / 2, roundRect.right - mStrokeWidth / 2, roundRect.bottom - mStrokeWidth / 2);
            canvas.drawRoundRect(roundRect2, roundCorner - mStrokeWidth / 2, roundCorner - mStrokeWidth / 2, strokePaint);
        } else {
            super.draw(canvas);
        }
    }
}
