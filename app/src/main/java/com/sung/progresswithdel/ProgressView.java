package com.sung.progresswithdel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by sung on 2017/7/4.
 */

public class ProgressView extends View {
    /** 进度条 */
    private Paint mProgressPaint;
    /** 闪 */
    private Paint mActivePaint;
    /** 暂停/中断色块 */
    private Paint mPausePaint;
    /** 回删 */
    private Paint mRemovePaint;
    /** 三秒 */
    private Paint mThreePaint;
    /** 超时 */
    private Paint mOverflowPaint;

    /** 最长时长 */
    private int mMaxDuration, mVLineWidth;
    private int mRecordTimeMin=1500;

    private boolean mStart, mStop, mProgressChanged;
    private boolean mActiveState;
    private MediaObject mMediaObject;

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mProgressPaint = new Paint();
        mActivePaint = new Paint();
        mPausePaint = new Paint();
        mRemovePaint = new Paint();
        mThreePaint = new Paint();
        mOverflowPaint = new Paint();

        mVLineWidth = DeviceUtils.dipToPX(getContext(), 1);

        setBackgroundColor(getResources().getColor(R.color.camera_bg));
        mProgressPaint.setColor(getResources().getColor(
                R.color.camera_progress_three));
        mProgressPaint.setStyle(Paint.Style.FILL);

        mActivePaint.setColor(getResources().getColor(android.R.color.white));
        mActivePaint.setStyle(Paint.Style.FILL);

        mPausePaint.setColor(getResources().getColor(
                R.color.camera_progress_split));
        mPausePaint.setStyle(Paint.Style.FILL);

        mRemovePaint.setColor(getResources().getColor(
                R.color.camera_progress_delete));
        mRemovePaint.setStyle(Paint.Style.FILL);

        mThreePaint.setColor(getResources().getColor(
                R.color.camera_progress_three));
        mThreePaint.setStyle(Paint.Style.FILL);

        mOverflowPaint.setColor(getResources().getColor(
                R.color.camera_progress_overflow));
        mOverflowPaint.setStyle(Paint.Style.FILL);
    }

    /** 闪动 */
    private final static int HANDLER_INVALIDATE_ACTIVE = 0;
    /** 录制中 */
    private final static int HANDLER_INVALIDATE_RECORDING = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_INVALIDATE_ACTIVE:
                    invalidate();
                    mActiveState = !mActiveState;
                    if (!mStop)
                        sendEmptyMessageDelayed(0, 250);
                    break;
                case HANDLER_INVALIDATE_RECORDING:
                    invalidate();
                    if (mProgressChanged)
                        sendEmptyMessageDelayed(0, 50);
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

//    long lastTime = 0;
//    int right = 0;
//    boolean draw_status = false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        final int width = getMeasuredWidth(), height = getMeasuredHeight();
//        int left = 0, duration = 0;
//
//        //画已有数据
//        if (mMediaObject != null && mMediaObject.getMediaParts() != null && !draw_status){
//            for (int i = 0; i < mMediaObject.getMediaParts().size(); i++) {
//                MediaObject.MediaPart part = mMediaObject.getMediaParts().get(i);
//                right = right +(int) (part.duration * 1.0F / mMaxDuration *width);
//                canvas.drawRect(left, 0.0f, right, height, mProgressPaint);
//                canvas.drawRect(right-10, 0.0f, right, height, mOverflowPaint);
//                left = right;
//            }
//            draw_status = true;
//        }
//
//        if (mStart) {
//            if ((System.currentTimeMillis() - lastTime) > 1000){
//                Log.e("ProgressView", "onDraw: "+System.currentTimeMillis() );
//                lastTime = System.currentTimeMillis();
//
//                right = right + (int) (1000 * 1.0F / mMaxDuration * width);//+1s
//                if (right >= width)
//                    right = width;
//            }
////            Log.e("ProgressView", "onDraw: "+right +" - mMaxDuration "+mMaxDuration+" - width "+width+" - time "+System.currentTimeMillis());
//        }
//        canvas.drawRect(left, 0.0f, right, height, mProgressPaint);
//
//        if (!mStart){
//            //canvas.drawRect(right-10, 0.0f, right, height, mOverflowPaint);
//        }
//
//        // 画三秒
//        if (duration < mRecordTimeMin) {
//            left = (int) ((mRecordTimeMin*1.0f )/ mMaxDuration * width);
//            canvas.drawRect(left, 0.0F, left + mVLineWidth, height, mThreePaint);
//        }
//
//        // 删
//        //
//        // 闪
//        if (mActiveState) {
//            if (right + 8 >= width)
//                right = width - 8;
//            canvas.drawRect(right, 0.0F, right + 8, getMeasuredHeight(),
//                    mActivePaint);
//        }

        final int width = getMeasuredWidth(), height = getMeasuredHeight();
        int left = 0, right = 0, duration = 0;
        if (mMediaObject != null && mMediaObject.getMediaParts() != null) {

            left = right = 0;

            // final int duration = vp.getDuration();
            int maxDuration = mMaxDuration;
            boolean hasOutDuration = false;
            int currentDuration = (int) mMediaObject.getDuration();
            hasOutDuration = currentDuration > mMaxDuration;
            if (hasOutDuration)
                maxDuration = currentDuration;

            if (mMediaObject.getMediaParts().size()!=0) {
                for (int i = 0; i < mMediaObject.getMediaParts().size(); i++) {
                    MediaObject.MediaPart vp = mMediaObject.getMediaParts().get(i);
                    final int partDuration = (int) vp.getDuration();
                    // Logger.e("[ProgressView]partDuration" + partDuration +
                    // " maxDuration:" + maxDuration);
                    left = right;
                    right = left
                            + (int) (partDuration * 1.0F / maxDuration * width);

                    if (vp.remove) {
                        // 回删
                        canvas.drawRect(left, 0.0F, right, height, mRemovePaint);
                    } else {
                        // 画进度
                        if (hasOutDuration) {
                            // 超时拍摄
                            // 前段
                            right = left
                                    + (int) ((mMaxDuration - duration) * 1.0F
                                    / maxDuration * width);
                            canvas.drawRect(left, 0.0F, right, height,
                                    mProgressPaint);

                            // 超出的段
                            left = right;
                            right = left
                                    + (int) ((partDuration - (mMaxDuration - duration))
                                    * 1.0F / maxDuration * width);
                            canvas.drawRect(left, 0.0F, right, height,
                                    mOverflowPaint);
                        } else {
                            canvas.drawRect(left, 0.0F, right, height,
                                    mProgressPaint);
                        }
                    }

                    if (i+1<mMediaObject.getMediaParts().size()) {
                        // left = right - mVLineWidth;
                        canvas.drawRect(right - mVLineWidth, 0.0F, right, height,
                                mPausePaint);
                    }

                    duration += partDuration;
                    // progress = vp.progress;
                }
            }
        }

        // 画三秒
        if (duration < mRecordTimeMin) {
            left = (int) ((mRecordTimeMin*1.0f )/ mMaxDuration * width);
            canvas.drawRect(left, 0.0F, left + mVLineWidth, height, mThreePaint);
        }

        // 删
        //
        // 闪
        if (mActiveState) {
            if (right + 8 >= width)
                right = width - 8;
            canvas.drawRect(right, 0.0F, right + 8, getMeasuredHeight(),
                    mActivePaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mStop = false;
        mHandler.sendEmptyMessage(HANDLER_INVALIDATE_ACTIVE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mStop = true;
        mHandler.removeMessages(HANDLER_INVALIDATE_ACTIVE);
    }

    public void setData(MediaObject mMediaObject) {
        this.mMediaObject = mMediaObject;
    }

    public void setMaxDuration(int duration) {
        this.mMaxDuration = duration;
    }

    public void start() {
        mProgressChanged = true;
        mStart = true;
        Log.e("ProgressView", "mStart: "+mStart);
    }

    public void stop() {
        mProgressChanged = false;
        mStart = false;
        Log.e("ProgressView", "mStart: "+mStart);
    }

    public void setMinTime(int recordTimeMin) {
        this.mRecordTimeMin=recordTimeMin;
    }
}
