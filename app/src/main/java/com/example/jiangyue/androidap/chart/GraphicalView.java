/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jiangyue.androidap.chart;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.jiangyue.androidap.chart.chart.AbstractChart;
import com.example.jiangyue.androidap.chart.chart.RoundChart;
import com.example.jiangyue.androidap.chart.chart.StockChart;
import com.example.jiangyue.androidap.chart.chart.TouchLineChat;
import com.example.jiangyue.androidap.chart.chart.XYChart;
import com.example.jiangyue.androidap.chart.model.Point;
import com.example.jiangyue.androidap.chart.model.SeriesSelection;
import com.example.jiangyue.androidap.chart.renderer.DefaultRenderer;
import com.example.jiangyue.androidap.chart.renderer.XYMultipleSeriesRenderer;
import com.example.jiangyue.androidap.chart.tools.FitZoom;
import com.example.jiangyue.androidap.chart.tools.PanListener;
import com.example.jiangyue.androidap.chart.tools.Zoom;
import com.example.jiangyue.androidap.chart.tools.ZoomListener;

/**
 * The view that encapsulates the graphical chart.
 */
public class GraphicalView extends View {
    /**
     * The chart to be drawn.
     */
    private AbstractChart mChart;
    /**
     * The chart renderer.
     */
    private DefaultRenderer mRenderer;
    /**
     * The view bounds.
     */
    private Rect mRect = new Rect();
    /**
     * The user interface thread handler.
     */
    private Handler mHandler;
    /**
     * The zoom buttons rectangle.
     */
    private RectF mZoomR = new RectF();
    /**
     * The zoom in icon.
     */
    private Bitmap zoomInImage;
    /**
     * The zoom out icon.
     */
    private Bitmap zoomOutImage;
    /**
     * The fit zoom icon.
     */
    private Bitmap fitZoomImage;
    /**
     * The zoom area size.
     */
    private int zoomSize = 50;
    /**
     * The zoom buttons background color.
     */
    private static final int ZOOM_BUTTONS_COLOR = Color.argb(175, 150, 150, 150);
    /**
     * The zoom in tool.
     */
    private Zoom mZoomIn;
    /**
     * The zoom out tool.
     */
    private Zoom mZoomOut;
    /**
     * The fit zoom tool.
     */
    private FitZoom mFitZoom;
    /**
     * The paint to be used when drawing the chart.
     */
    private Paint mPaint = new Paint();
    /**
     * The touch handler.
     */
    private ITouchHandler mTouchHandler;
    /**
     * The old x coordinate.
     */
    private float oldX;
    /**
     * The old y coordinate.
     */
    private float oldY;
    /**
     * If the graphical view is drawn.
     */
    private boolean mDrawn;

    /**
     * Creates a new graphical view.
     *
     * @param context the context
     */
    public GraphicalView(Context context) {
        super(context);
    }

    /**
     * Creates a new graphical view.
     *
     * @param context
     * @param attrs
     */
    public GraphicalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphicalView(Context context, AbstractChart chart) {
        super(context);
        mChart = chart;
        mHandler = new Handler();
        if (mChart instanceof XYChart) {
            mRenderer = ((XYChart) mChart).getRenderer();
        } else {
            mRenderer = ((RoundChart) mChart).getRenderer();
        }
        if (mRenderer.isZoomButtonsVisible()) {
            zoomInImage = BitmapFactory.decodeStream(GraphicalView.class
                    .getResourceAsStream("image/zoom_in.png"));
            zoomOutImage = BitmapFactory.decodeStream(GraphicalView.class
                    .getResourceAsStream("image/zoom_out.png"));
            fitZoomImage = BitmapFactory.decodeStream(GraphicalView.class
                    .getResourceAsStream("image/zoom-1.png"));
        }

        if (mRenderer instanceof XYMultipleSeriesRenderer
                && ((XYMultipleSeriesRenderer) mRenderer).getMarginsColor() == XYMultipleSeriesRenderer.NO_COLOR) {
            ((XYMultipleSeriesRenderer) mRenderer).setMarginsColor(mPaint.getColor());
        }
        if (mRenderer.isZoomEnabled() && mRenderer.isZoomButtonsVisible()
                || mRenderer.isExternalZoomEnabled()) {
            mZoomIn = new Zoom(mChart, true, mRenderer.getZoomRate());
            mZoomOut = new Zoom(mChart, false, mRenderer.getZoomRate());
            mFitZoom = new FitZoom(mChart);
        }
        int version = 7;
        try {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (Exception e) {
            // do nothing
        }
        if (version < 7) {
            mTouchHandler = new TouchHandlerOld(this, mChart);
        } else {
            mTouchHandler = new TouchHandler(this, mChart);
        }
    }

    /**
     * 设置图标类型
     *
     * @param chart
     */
    public void setChart(AbstractChart chart) {
        mChart = chart;
        mHandler = new Handler();
        if (mChart instanceof XYChart) {
            mRenderer = ((XYChart) mChart).getRenderer();
        } else {
            mRenderer = ((RoundChart) mChart).getRenderer();
        }
        if (mChart instanceof TouchLineChat) {
            ((TouchLineChat) mChart).setLongTouchListener(new TouchLineChat.onLongTouchListener() {
                @Override
                public void longTouchListener() {
                    invalidate();
                }
            });
        }
        if (mChart instanceof StockChart) {
            ((StockChart) mChart).setLongTouchListener(new StockChart.onLongTouchListener() {
                @Override
                public void longTouchListener() {
                    invalidate();
                }
            });
        }
        if (mRenderer.isZoomButtonsVisible()) {
            zoomInImage = BitmapFactory.decodeStream(GraphicalView.class
                    .getResourceAsStream("/image/zoom_in.png"));
            zoomOutImage = BitmapFactory.decodeStream(GraphicalView.class
                    .getResourceAsStream("/image/zoom_out.png"));
            fitZoomImage = BitmapFactory.decodeStream(GraphicalView.class
                    .getResourceAsStream("/image/zoom_1.png"));
        }

        if (mRenderer instanceof XYMultipleSeriesRenderer
                && ((XYMultipleSeriesRenderer) mRenderer).getMarginsColor() == XYMultipleSeriesRenderer.NO_COLOR) {
//            ((XYMultipleSeriesRenderer) mRenderer).setMarginsColor(mPaint.getColor());
        }
        if (mRenderer.isZoomEnabled() && mRenderer.isZoomButtonsVisible()
                || mRenderer.isExternalZoomEnabled()) {
            mZoomIn = new Zoom(mChart, true, mRenderer.getZoomRate());
            mZoomOut = new Zoom(mChart, false, mRenderer.getZoomRate());
            mFitZoom = new FitZoom(mChart);
        }
        int version = 7;
        try {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (Exception e) {
            // do nothing
        }
        if (version < 7) {
            mTouchHandler = new TouchHandlerOld(this, mChart);
        } else {
            mTouchHandler = new TouchHandler(this, mChart);
        }
    }

    /**
     * Returns the current series selection object.
     *
     * @return the series selection
     */
    public SeriesSelection getCurrentSeriesAndPoint() {
        return mChart.getSeriesAndPointForScreenCoordinate(new Point(oldX, oldY));
    }

    /**
     * Returns the drawn state of the chart.
     *
     * @return the drawn state of the chart
     */
    public boolean isChartDrawn() {
        return mDrawn;
    }

    /**
     * Transforms the currently selected screen point to a real point.
     *
     * @param scale the scale
     * @return the currently selected real point
     */
    public double[] toRealPoint(int scale) {
        if (mChart instanceof XYChart) {
            XYChart chart = (XYChart) mChart;
            return chart.toRealPoint(oldX, oldY, scale);
        }
        return null;
    }

    public AbstractChart getChart() {
        return mChart;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRenderer != null) {
            canvas.getClipBounds(mRect);
            int top = mRect.top;
            int left = mRect.left;
            int width = mRect.width();
            int height = mRect.height();
            if (mRenderer.isInScroll()) {
                top = 0;
                left = 0;
                width = getMeasuredWidth();
                height = getMeasuredHeight();
            }
            mChart.draw(canvas, left, top, width, height, mPaint);
            if (mRenderer != null && mRenderer.isZoomEnabled() && mRenderer.isZoomButtonsVisible()) {
                mPaint.setColor(ZOOM_BUTTONS_COLOR);
                zoomSize = Math.max(zoomSize, Math.min(width, height) / 7);
                mZoomR.set(left + width - zoomSize * 3, top + height - zoomSize * 0.775f, left + width, top
                        + height);
                canvas.drawRoundRect(mZoomR, zoomSize / 3, zoomSize / 3, mPaint);
                float buttonY = top + height - zoomSize * 0.625f;
                //是否为空
                if (zoomInImage != null && zoomOutImage != null && zoomOutImage != null) {
                    canvas.drawBitmap(zoomInImage, left + width - zoomSize * 2.75f, buttonY, null);
                    canvas.drawBitmap(zoomOutImage, left + width - zoomSize * 1.75f, buttonY, null);
                    canvas.drawBitmap(zoomOutImage, left + width - zoomSize * 0.75f, buttonY, null);
                }
            }
            mDrawn = true;
        }
    }

    /**
     * Sets the zoom rate.
     *
     * @param rate the zoom rate
     */
    public void setZoomRate(float rate) {
        if (mZoomIn != null && mZoomOut != null) {
            mZoomIn.setZoomRate(rate);
            mZoomOut.setZoomRate(rate);
        }
    }

    /**
     * Do a chart zoom in.
     */
    public void zoomIn() {
        if (mZoomIn != null) {
            mZoomIn.apply(Zoom.ZOOM_AXIS_XY);
            repaint();
        }
    }

    /**
     * Do a chart zoom out.
     */
    public void zoomOut() {
        if (mZoomOut != null) {
            mZoomOut.apply(Zoom.ZOOM_AXIS_XY);
            repaint();
        }
    }

    /**
     * Do a chart zoom reset / fit zoom.
     */
    public void zoomReset() {
        if (mFitZoom != null) {
            mFitZoom.apply();
            mZoomIn.notifyZoomResetListeners();
            repaint();
        }
    }

    /**
     * Adds a new zoom listener.
     *
     * @param listener zoom listener
     */
    public void addZoomListener(ZoomListener listener, boolean onButtons, boolean onPinch) {
        if (onButtons) {
            if (mZoomIn != null) {
                mZoomIn.addZoomListener(listener);
                mZoomOut.addZoomListener(listener);
            }
        }
        if (onPinch) {
            mTouchHandler.addZoomListener(listener);
        }
    }

    /**
     * Removes a zoom listener.
     *
     * @param listener zoom listener
     */
    public synchronized void removeZoomListener(ZoomListener listener) {
        if (mZoomIn != null) {
            mZoomIn.removeZoomListener(listener);
            mZoomOut.removeZoomListener(listener);
        }
        mTouchHandler.removeZoomListener(listener);
    }

    /**
     * Adds a new pan listener.
     *
     * @param listener pan listener
     */
    public void addPanListener(PanListener listener) {
        mTouchHandler.addPanListener(listener);
    }

    /**
     * Removes a pan listener.
     *
     * @param listener pan listener
     */
    public void removePanListener(PanListener listener) {
        mTouchHandler.removePanListener(listener);
    }

    protected RectF getZoomRectangle() {
        return mZoomR;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //传递touch事件，只有touch图表
        if (mChart != null && (mChart instanceof TouchLineChat || mChart instanceof StockChart)) {
            mChart.onTouchEvent(event);
            this.postInvalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // save the x and y so they can be used in the click and long press
            // listeners
            oldX = event.getX();
            oldY = event.getY();
        }
        if (mRenderer != null && mDrawn && (mRenderer.isPanEnabled()
                || mRenderer.isZoomEnabled()
                || mChart instanceof TouchLineChat
                || mChart instanceof StockChart)) {
            if (mTouchHandler.handleTouch(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取xy轴图表的touch point
     *
     * @return
     */
    public PointF getTouchChatPoint() {
        if (mChart instanceof TouchLineChat) {
            return ((TouchLineChat) mChart).getTouchPoint();
        }
        if (mChart instanceof StockChart) {
            return ((StockChart) mChart).getTouchPoint();
        }
        return null;
    }

    /**
     * Schedule a view content repaint.
     */
    public void repaint() {
        mHandler.post(new Runnable() {
            public void run() {
                invalidate();
            }
        });
    }

    /**
     * Schedule a view content repaint, in the specified rectangle area.
     *
     * @param left   the left position of the area to be repainted
     * @param top    the top position of the area to be repainted
     * @param right  the right position of the area to be repainted
     * @param bottom the bottom position of the area to be repainted
     */
    public void repaint(final int left, final int top, final int right, final int bottom) {
        mHandler.post(new Runnable() {
            public void run() {
                invalidate(left, top, right, bottom);
            }
        });
    }

    /**
     * Saves the content of the graphical view to a bitmap.
     *
     * @return the bitmap
     */
    public Bitmap toBitmap() {
        setDrawingCacheEnabled(false);
        if (!isDrawingCacheEnabled()) {
            setDrawingCacheEnabled(true);
        }
        if (mRenderer.isApplyBackgroundColor()) {
            setDrawingCacheBackgroundColor(mRenderer.getBackgroundColor());
        }
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        return getDrawingCache(true);
    }

}