package com.czl.zldotprogressbar2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class ZLDotProgressBar extends View {
    private List<String> mTexts = new ArrayList<>();
    private List<String> mSubtexts = new ArrayList<>();

    private static final int textmargin = 5;
    private static final int subtextmargin = 3;

    private int mDotsCount;
    private int mDotsRadius;
    private int mDotsProgressWidth;
    private int mDotsProgressWidthHalf;
    private int mSpeed;
    private int mDotsBackColor;
    private int mDotsFrontColor;
    private int mPartWidth; //每段矩形的长度

    private int mDotsRadiusInner;
    private int mDotsProgressWidthInner;
    private int mDotsProgressWidthHalfInner;

    /**
     * 进度，必须<=mDotsCount
     */
    private int oldProgress = 1;
    private int newProgress = 1;
    /**
     * 目前已经进行的时间
     */
    private int mPartTime;
    /**
     * 是否正在进行
     */
    private boolean mIsRunning = false;

    private int[] mCircles;

    private android.view.animation.Interpolator mInterpolator;


    public ZLDotProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZLDotProgressBar);
        mDotsCount = typedArray.getInt(R.styleable.ZLDotProgressBar_barDotsCount, 2);
        mDotsRadius = typedArray.getDimensionPixelSize(R.styleable.ZLDotProgressBar_barDotsRadius, dp2px(8));
        mDotsProgressWidth = typedArray.getDimensionPixelSize(R.styleable.ZLDotProgressBar_barProgressWidth, dp2px(8));
        if ((2 * mDotsRadius) < mDotsProgressWidth)
            mDotsProgressWidth = mDotsRadius * 2; // 如果用户设置进度条的宽度大于点的直径，则设置为半径大小
        mDotsProgressWidthHalf = mDotsProgressWidth / 2;
        mDotsRadiusInner = mDotsRadius-mDotsRadius/4;
        mDotsProgressWidthInner = mDotsProgressWidth-mDotsProgressWidth/4;
        mDotsProgressWidthHalfInner = mDotsProgressWidthInner/2;
        mSpeed = typedArray.getInt(R.styleable.ZLDotProgressBar_barSpeed, 80);
        mDotsBackColor = typedArray.getColor(R.styleable.ZLDotProgressBar_barBackColor, ContextCompat.getColor(context, android.R.color.darker_gray));
        mDotsFrontColor = typedArray.getColor(R.styleable.ZLDotProgressBar_barFrontColor, ContextCompat.getColor(context, android.R.color.holo_blue_light));
        typedArray.recycle();
        // 初始化插值器
        mInterpolator = new LinearInterpolator();

        mCircles = new int[mDotsCount];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int wrap_width = Math.max(mDotsRadius*2+dp2px(50)*mDotsCount, width) ;
        int wrap_height = mDotsRadius*2;
        //if (mTexts.size() > 0) {
            wrap_height += dp2px(textmargin)+dp2px(16);
        //}
        //if (mSubtexts.size() > 0) {
            wrap_height += dp2px(subtextmargin)+dp2px(12);
        //}

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wrap_width, wrap_height);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wrap_width, height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, wrap_height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mPartWidth = width / mDotsCount;
        // 初始化画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mDotsBackColor);
        //画直线
        Rect rect = new Rect(mPartWidth/2,mDotsRadius-mDotsProgressWidthHalf,width-mPartWidth/2,mDotsRadius+mDotsProgressWidthHalf);
        canvas.drawRect(rect, paint);

        //画圆点
        for (int i=0; i<mDotsCount; i++) {
            canvas.drawCircle(rect.left + mPartWidth*i, mDotsRadius, mDotsRadius, paint);
            mCircles[i] = rect.left + mPartWidth*i;
        }

        if (mTexts.size() > 0 && mTexts.size() <= mDotsCount) {
            for (int i=0; i<mDotsCount; i++) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(35f);
                paint.setTextAlign(Paint.Align.CENTER);
                Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                Rect textRect = new Rect(rect.left+mPartWidth*i-mPartWidth/2,
                        mDotsRadius*2+dp2px(textmargin),
                        rect.left+mPartWidth*i+mPartWidth/2,
                        mDotsRadius*2+dp2px(textmargin)+dp2px(16));
                int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                canvas.drawText(mTexts.get(i), textRect.centerX(), baseline, paint);
            }
        }

        if (mSubtexts.size() > 0 && mSubtexts.size() <= mDotsCount) {
            for (int i=0; i<mDotsCount; i++) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(30f);
                paint.setTextAlign(Paint.Align.CENTER);
                Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                Rect textRect = new Rect(
                        rect.left+mPartWidth*i-mPartWidth/2,
                        mDotsRadius*2+dp2px(textmargin)+dp2px(subtextmargin)+dp2px(16),
                        rect.left+mPartWidth*i+mPartWidth/2,
                        mDotsRadius*2+dp2px(textmargin)+dp2px(subtextmargin)+dp2px(16)+dp2px(12));
                int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                canvas.drawText(mSubtexts.get(i), textRect.centerX(), baseline, paint);
            }
        }

        //以下绘制前景部分
        //调整画笔为前景色
        paint.setColor(mDotsFrontColor);
        if (newProgress != oldProgress) {
            if (newProgress > oldProgress) {
                //int start = rect.left;
                int start = rect.left + mPartWidth*(oldProgress-1);
                //绘制不变的部分
                if (oldProgress > 1) {
                    canvas.drawRect(rect.left, rect.top+(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner),
                            rect.left+mPartWidth*(oldProgress-1), rect.bottom-(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner), paint);
                    for (int i=0; i<oldProgress; i++) {
                        canvas.drawCircle(mCircles[i], mDotsRadius, mDotsRadiusInner, paint);
                    }
                }
                float percent = mInterpolator.getInterpolation(((float) mPartTime) / mSpeed);
                int[] params = getParams(percent);
                // 绘制变化的部分
                canvas.drawRect(start, rect.top+(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner),
                        start+params[0], rect.bottom-(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner), paint);
                for (int i=0; i<mCircles.length; i++) {
                    if (newProgress == 1) {
                        break;
                    }
                    if (i<newProgress) {
                        if (start+params[0] > (mCircles[i]+mDotsRadiusInner)) {
                            canvas.drawCircle(mCircles[i], mDotsRadius, mDotsRadiusInner, paint);
                        } else if (start+params[0] > mCircles[i] && start+params[0] < mCircles[i]+mDotsRadiusInner) {
                            canvas.drawCircle(mCircles[i], mDotsRadius, start+params[0]-mCircles[i], paint);
                        }
                    }
                }

                if (mPartTime < mSpeed) {
                    mPartTime++;
                } else {
                    mIsRunning = false;
                    oldProgress = newProgress;
                }
                postInvalidate();
            } else if (oldProgress > newProgress) {
                float percent = mInterpolator.getInterpolation(((float) mPartTime) / mSpeed);
                int[] params = getParams(percent);
                // 绘制变化的部分
                int point = rect.left+(oldProgress-1)*mPartWidth+params[0];//不断变化的右边的点
                canvas.drawRect(rect.left, rect.top+(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner),
                            point, rect.bottom-(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner), paint);
                for (int i=0; i<mCircles.length; i++) {
                    if (i<oldProgress) {
                        if (point > (mCircles[i]+mDotsRadiusInner)) {
                            canvas.drawCircle(mCircles[i], mDotsRadius, mDotsRadiusInner, paint);
                        } else if (point > mCircles[i] && point < mCircles[i]+mDotsRadiusInner) {
                            canvas.drawCircle(mCircles[i], mDotsRadius, point-mCircles[i], paint);
                        }
                    }
                }

                if (mPartTime < mSpeed) {
                    mPartTime++;
                } else {
                    mIsRunning = false;
                    oldProgress = newProgress;
                }
                postInvalidate();
            }
        } else {
            // 说明动画已经结束，我们只需要绘制正确的前景进度
            Rect rc = new Rect(rect.left, rect.top+(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner),
                    rect.left+mPartWidth*(newProgress-1), rect.bottom-(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner));
            canvas.drawRect(rect.left, rect.top+(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner),
                    rect.left+mPartWidth*(newProgress-1), rect.bottom-(mDotsProgressWidthHalf-mDotsProgressWidthHalfInner), paint);
            if (newProgress > 1) {
                for (int i = 0; i < newProgress; i++) {
                    canvas.drawCircle(mCircles[i], mDotsRadius, mDotsRadiusInner, paint);
                }
            }
        }


        //写字
        paint.setColor(Color.WHITE);
        paint.setTextSize(30f);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i=0; i<mDotsCount; i++) {
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            Rect textRect = new Rect(
                    rect.left+mPartWidth*i-mDotsRadius/2,
                    mDotsRadius-mDotsRadius/2,
                    rect.left+mPartWidth*i+mDotsRadius/2,
                    mDotsRadius+mDotsRadius/2);
            int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText(""+(i+1), textRect.centerX(), baseline, paint);
        }
    }

    public void setTexts(List<String> texts) {
        mTexts.clear();
        mTexts.addAll(texts);
        postInvalidate();
    }

    public void setSubTexts(List<String> texts) {
        mSubtexts.clear();
        mSubtexts.addAll(texts);
        postInvalidate();
    }

    /**
     * dp 转 px
     *
     * @param dpValue dp 值
     * @return 返回 px 值
     */
    private int dp2px(int dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获得当前对应进度所需要绘制的尺寸，整型数组第一个代表进度条的长度，第二个代表圆点的半径
     *
     * @param percent 当前一段变化中的比例
     * @return 返回整型数组
     */
    private int[] getParams(float percent) {
        int[] params = new int[2];
//        if (newProgress >= 1) {
            if (percent > 0.9) {
                params[0] = mPartWidth * (newProgress-oldProgress);
                params[1] = (int) (((percent - 0.9) / 0.1) * (mDotsRadiusInner - mDotsProgressWidthHalfInner) + mDotsProgressWidthHalfInner);
            } else {
                params[0] = (int) ((percent / 0.9) * (mPartWidth * (newProgress-oldProgress)));
                params[1] = mDotsProgressWidthHalfInner;
            }
//        } else {
//            params[0] = 0;
//            params[1] = (int) (percent * mDotsRadiusInner);
//        }
        return params;
    }

    public void setNewProgress(int newProgress) {
        this.newProgress = newProgress;
        if (this.newProgress <= mDotsCount && !mIsRunning && this.newProgress != oldProgress) {
            mPartTime = 0;
            mIsRunning = true;
            postInvalidate();
        }
    }

    public void setDotsCount(int dotsCount) {
        this.mDotsCount = dotsCount;
        mCircles = new int[mDotsCount];
        postInvalidate();
    }
}
