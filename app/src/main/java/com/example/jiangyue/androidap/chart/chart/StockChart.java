package com.example.jiangyue.androidap.chart.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.example.jiangyue.androidap.chart.model.XYMultipleSeriesDataset;
import com.example.jiangyue.androidap.chart.model.XYSeries;
import com.example.jiangyue.androidap.chart.paint.HorizontalLinePaint;
import com.example.jiangyue.androidap.chart.paint.LongitudeLatitudePaint;
import com.example.jiangyue.androidap.chart.renderer.SimpleSeriesRenderer;
import com.example.jiangyue.androidap.chart.renderer.XYMultipleSeriesRenderer;
import com.example.jiangyue.androidap.chart.renderer.XYSeriesRenderer;
import com.example.jiangyue.androidap.util.DisplayUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jiangyue on 16/10/20.
 * 股票分时图
 */
public class StockChart extends XYChart {

    private LongitudeLatitudePaint longitudePaint = new LongitudeLatitudePaint();
    private HorizontalLinePaint horizontalPaint = new HorizontalLinePaint();
    private Paint paint = new Paint();

    private StockCombinedChartDef[] chartDefinitions;
    private PointF touchPoint;
    private float circleY;
    private double valueX, valueY;
    private int left, right, top, bottom;
    private boolean showTradeBar = true;
    private int longitudeNum = 3;
    private int latitudeNum = 0;

    //设置分界线
    public double dividerLineVal01 = Float.NaN;
    public double dividerLineVal02 = Float.NaN;

    /**
     * The embedded XY charts.
     */
    private XYChart[] mCharts;

    /**
     * The supported charts for being combined.
     */
    private Class<?>[] xyChartTypes = new Class<?>[]{StockLinearChart.class, StockBarChart.class, StockRangeBarChart.class};

    /**
     * Builds a new combined XY chart instance.
     *
     * @param dataset          the multiple series dataset
     * @param renderer         the multiple series renderer
     * @param chartDefinitions the XY chart definitions
     */
    public StockChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
                      StockCombinedChartDef[] chartDefinitions) {
        super(dataset, renderer);
        this.chartDefinitions = chartDefinitions;
        this.longitudePaint.setColor(Color.parseColor("#30000000"));
        this.horizontalPaint.setColor(Color.parseColor("#ff000000"));
        this.horizontalPaint.setStrokeWidth(2);
        int length = chartDefinitions.length;
        mCharts = new XYChart[length];
        for (int i = 0; i < length; i++) {
            try {
                mCharts[i] = getXYChart(chartDefinitions[i].getType());
            } catch (Exception e) {
                // ignore
            }
            if (mCharts[i] == null) {
                throw new IllegalArgumentException("Unknown chart type " + chartDefinitions[i].getType());
            } else {
                XYMultipleSeriesDataset newDataset = new XYMultipleSeriesDataset();
                XYMultipleSeriesRenderer newRenderer = new XYMultipleSeriesRenderer();
                for (int seriesIndex : chartDefinitions[i].getSeriesIndex()) {
                    newDataset.addSeries(dataset.getSeriesAt(seriesIndex));
                    newRenderer.addSeriesRenderer(renderer.getSeriesRendererAt(seriesIndex));
                }
                newRenderer.setBarSpacing(renderer.getBarSpacing());
                newRenderer.setPointSize(renderer.getPointSize());

                mCharts[i].setDatasetRenderer(newDataset, newRenderer);
            }
        }
    }

    @Override
    public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
        //计算参数
        int legendSize = getLegendSize(mRenderer, height / 5, mRenderer.getAxisTitleTextSize());
        int[] margins = mRenderer.getMargins();
        top = y + margins[0];
        bottom = y + height - margins[2] - legendSize;
        left = x + margins[1];
        right = x + width - margins[3];

        //绘制图形
        super.draw(canvas, x, y, width, height, paint);

        //计算touch 的Y坐标
        if (mDataset.getSeriesAt(0) != null && touchPoint != null) {
            XYSeries series = mDataset.getSeriesAt(0);
            double percent = (touchPoint.x - left) / (right - left);
            int pos = (int) (percent * series.getXYMap().size());
            boolean valid = pos < series.getXYMap().size() && pos >= 0;
            //计算坐标
            if (valid) {
                valueX = valid ? series.getXYMap().getXByIndex(pos) : 0;
                valueY = valid ? series.getXYMap().getYByIndex(pos) : 0;
                double i = 1 - (valueY - mRenderer.getYAxisMin()) / (mRenderer.getYAxisMax() - mRenderer.getYAxisMin());
                circleY = (float) (i * (bottom - top)) + top;
                //吸附到坐标值位置
                touchPoint.x = pos * ((right - left) * 1.0f / (series.getXYMap().size() - 1)) + left;
                Log.e("", pos + "........................." + touchPoint.x);
            } else {
                touchPoint = null;
            }
        }

        //背景经纬线
        drawLongitudeLine(canvas, width, height, top, bottom);
        drawLatitudeLine(canvas, width, height, top, bottom);

        //绘制手势十字星
        drawVerticalLine(canvas, top, bottom);
        drawHorizontalLine(canvas);
        drawTouchPoint(canvas);

        //绘制xy轴显示值
        drawXYValueRec(canvas, bottom);

        //绘制分割线
        if (dividerLineVal02 != Float.NaN) {
            //实线
            longitudePaint.setPathEffect(null);
            drawDividerLine(canvas, dividerLineVal02, true);
        }
        if (dividerLineVal01 != Float.NaN) {
            //虚线
            longitudePaint.setPathEffect();
            drawDividerLine(canvas, dividerLineVal01, true);
        }

        //绘制文字
        if (dividerLineVal01 != Float.NaN && dividerLineVal02 != Float.NaN) {
            drawCanvasText(canvas, dividerLineVal02, dividerLineVal01);
        }
    }

    public void setShowTradeBar(boolean showTradeBar) {
        this.showTradeBar = showTradeBar;
    }

    /* 手势长按触发显示移动坐标 */
    private static final int LONG_PRESS_TIMEOUT = 1000;
    private Handler handler = new Handler();
    private boolean canDrawGesView = false;
    private onLongTouchListener longTouchListener;
    private MotionEvent motionEvent;

    public interface onLongTouchListener {
        public void longTouchListener();
    }

    public void setLongTouchListener(onLongTouchListener longTouchListener) {
        this.longTouchListener = longTouchListener;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            canDrawGesView = true;
            if (motionEvent != null && longTouchListener != null) {
                motionEvent.setAction(MotionEvent.ACTION_MOVE);
                onTouchEvent(motionEvent);
                longTouchListener.longTouchListener();
            }
        }
    };


    /**
     * The old y2 coordinate.
     */
    private float oldY2;
    /**
     * The old x2 coordinate.
     */
    private float oldX2;

    @Override
    public void onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        //判断是否是两个手指缩放
        if (event.getPointerCount() > 1 && (oldX2 >= 0 || oldY2 >= 0) && mRenderer.isZoomEnabled()) {
            float newX2 = event.getX(1);
            float newY2 = event.getY(1);
            oldX2 = newX2;
            oldY2 = newY2;
            return;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (left < event.getX() && event.getX() < right) {
                    motionEvent = event;
                    handler.postDelayed(runnable, LONG_PRESS_TIMEOUT);
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                touchPoint = null;
                motionEvent = null;
                canDrawGesView = false;
                handler.removeCallbacks(runnable);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!canDrawGesView) {
                    motionEvent = event;
                    break;
                }
                PointF pointF = new PointF(event.getX(), event.getY());
                if (left < pointF.x && pointF.x < right) {
                    touchPoint = pointF;
                }
                break;
            }
        }
    }


    @Override
    protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas, Paint paint, int left, int top, int bottom, double xPixelsPerUnit, double minX, double maxX) {
        //绘制x坐标参数
        int length = xLabels.size();
        boolean showXLabels = mRenderer.isShowXLabels();
        boolean showGridY = mRenderer.isShowGridY();
        if (showGridY) {
            mGridPaint.setStyle(Paint.Style.STROKE);
            mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
        }
        boolean showTickMarks = mRenderer.isShowTickMarks();
        for (int i = 0; i < length; i++) {
            double label = xLabels.get(i);
            float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
            if (showXLabels) {
                paint.setColor(mRenderer.getXLabelsColor());
                if (showTickMarks) {
                    canvas.drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3, paint);
                }
                drawText(canvas, getLabel(mRenderer.getXLabelFormat(), label), xLabel,
                        bottom + mRenderer.getLabelsTextSize() * 4 / 3 + mRenderer.getXLabelsPadding(), paint,
                        mRenderer.getXLabelsAngle());
            }
            if (showGridY) {
                mGridPaint.setColor(mRenderer.getGridColor(0));
                canvas.drawLine(xLabel, bottom, xLabel, top, mGridPaint);
            }
        }
        drawXTextLabels(xTextLabelLocations, canvas, paint, showXLabels, left, top, bottom,
                xPixelsPerUnit, minX, maxX);
    }

    @Override
    protected void drawYLabels(Map<Integer, List<Double>> allYLabels, Canvas canvas, Paint paint, int maxScaleNumber, int left, int right, int bottom, double[] yPixelsPerUnit, double[] minY) {
        //绘制y坐标参数
        XYMultipleSeriesRenderer.Orientation or = mRenderer.getOrientation();
        boolean showGridX = mRenderer.isShowGridX();
        if (showGridX) {
            mGridPaint.setStyle(Paint.Style.STROKE);
            mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
        }

        //重新计算间距
        List<Double> yLabels = getLabelY();
        yPixelsPerUnit[0] = (float) (((yPixelsPerUnit[0] * (getRenderer().getYAxisMax() - getRenderer().getYAxisMin())))
                / (yLabels.get(yLabels.size() - 1) - yLabels.get(0)));

        float yLabel = bottom - DisplayUtil.dp2px(3) - (float) ((bottom - top) * (dividerLineVal02 - mRenderer.getYAxisMin()) / (mRenderer.getYAxisMax() - mRenderer.getYAxisMin()));
        float[] space = new float[]{yLabel, yLabel / 2, top + DisplayUtil.dp2px(8)};
        for (int i = 0; i < yLabels.size(); i++) {
            double label = yLabels.get(i);
            float labelWidth = paint.measureText(label + "") / 2 + DisplayUtil.dp2px(3);
            paint.setColor(mRenderer.getYLabelsColor(0));
            drawText(canvas, getLabel(mRenderer.getYLabelFormat(0), label),
                    left + labelWidth,
                    space[i], paint,
                    mRenderer.getYLabelsAngle());

            //绘制分割线
            if (i == 1) {
                drawDividerLine(canvas, space[i] + DisplayUtil.dp2px(3), false);
            }

            if (showTradeBar) {
                //绘制右边涨跌%
                float textLengthY = paint.measureText("10%") / 2 + DisplayUtil.dp2px(3);
                drawText(canvas, "10%",
                        right - mRenderer.getYLabelsPadding() - textLengthY,
                        space[i], paint,
                        mRenderer.getYLabelsAngle());
            }
        }

//
//        for (int i = 0; i < maxScaleNumber; i++) {
//            paint.setTextAlign(mRenderer.getYLabelsAlign(i));
//            int length = yLabels.size();
//            int startIndex = 0;
//            for (int j = startIndex; j < length; j++) {
//                double label = yLabels.get(j);
//                Paint.Align axisAlign = mRenderer.getYAxisAlign(i);
//                boolean textLabel = mRenderer.getYTextLabel(label, i) != null;
//                float yLabel = (float) (bottom - yPixelsPerUnit[i] * (label - minY[i]));
//                if (or == XYMultipleSeriesRenderer.Orientation.HORIZONTAL) {
//                    if (showYLabels && !textLabel) {
//                        paint.setColor(mRenderer.getYLabelsColor(i));
//                        if (axisAlign == Paint.Align.LEFT) {
//                            if (showTickMarks) {
//                                canvas.drawLine(left + getLabelLinePos(axisAlign), yLabel, left, yLabel, paint);
//                            }
//                            //绘制左边涨跌幅
//                            drawText(canvas, getLabel(mRenderer.getYLabelFormat(i), label),
//                                    left - mRenderer.getYLabelsPadding(),
//                                    yLabel - (float) dividerLineVal02 - mRenderer.getYLabelsVerticalPadding(), paint,
//                                    mRenderer.getYLabelsAngle());
//                            if (showTradeBar) {
//                                //绘制右边涨跌%
//                                float textLengthY = paint.measureText("10%");
//                                drawText(canvas, "10%",
//                                        this.right - mRenderer.getYLabelsPadding() - textLengthY,
//                                        yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
//                                        mRenderer.getYLabelsAngle());
//                            }
//                        } else {
//                            if (showTickMarks) {
//                                canvas.drawLine(right, yLabel, right + getLabelLinePos(axisAlign), yLabel, paint);
//                            }
//                            drawText(canvas, getLabel(mRenderer.getYLabelFormat(i), label),
//                                    right + mRenderer.getYLabelsPadding(),
//                                    yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
//                                    mRenderer.getYLabelsAngle());
//                        }
//                    }
//                    if (showGridX) {
//                        mGridPaint.setColor(mRenderer.getGridColor(i));
//                        canvas.drawLine(left, yLabel, right, yLabel, mGridPaint);
//                    }
//                } else if (or == XYMultipleSeriesRenderer.Orientation.VERTICAL) {
//                    if (showYLabels && !textLabel) {
//                        paint.setColor(mRenderer.getYLabelsColor(i));
//                        if (showTickMarks) {
//                            canvas.drawLine(right - getLabelLinePos(axisAlign), yLabel, right, yLabel, paint);
//                        }
//                        drawText(canvas, getLabel(mRenderer.getLabelFormat(), label),
//                                right + 10 + mRenderer.getYLabelsPadding(),
//                                yLabel - mRenderer.getYLabelsVerticalPadding(), paint, mRenderer.getYLabelsAngle());
//                    }
//                    if (showGridX) {
//                        mGridPaint.setColor(mRenderer.getGridColor(i));
//                        if (showTickMarks) {
//                            canvas.drawLine(right, yLabel, left, yLabel, mGridPaint);
//                        }
//                    }
//                }
//            }
//        }
    }

    /**
     * Returns a chart instance based on the provided type.
     *
     * @param type the chart type
     * @return an instance of a chart implementation
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private XYChart getXYChart(String type) throws IllegalAccessException, InstantiationException {
        XYChart chart = null;
        int length = xyChartTypes.length;
        for (int i = 0; i < length && chart == null; i++) {
            XYChart newChart = (XYChart) xyChartTypes[i].newInstance();
            if (type.equals(newChart.getChartType())) {
                chart = newChart;
            }
        }
        return chart;
    }

    @Override
    public void drawSeries(Canvas canvas, Paint paint, List<Float> points, XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, int startIndex) {
        XYChart chart = getXYChart(seriesIndex);
        chart.setScreenR(getScreenR());
        chart.setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex).getScaleNumber()), 0);
        chart.drawSeries(canvas, paint, points, seriesRenderer, yAxisValue,
                getChartSeriesIndex(seriesIndex), startIndex);
    }

    @Override
    protected void drawSeries(XYSeries series, Canvas canvas, Paint paint, List<Float> pointsList,
                              XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, XYMultipleSeriesRenderer.Orientation or,
                              int startIndex) {
        XYChart chart = getXYChart(seriesIndex);
        chart.setScreenR(getScreenR());
        chart.setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex).getScaleNumber()), 0);
        chart.drawSeries(series, canvas, paint, pointsList, seriesRenderer, yAxisValue,
                getChartSeriesIndex(seriesIndex), or, startIndex);
    }

    @Override
    protected ClickableArea[] clickableAreasForPoints(List<Float> points, List<Double> values, float yAxisValue, int seriesIndex, int startIndex) {
        XYChart chart = getXYChart(seriesIndex);
        return chart.clickableAreasForPoints(points, values, yAxisValue,
                getChartSeriesIndex(seriesIndex), startIndex);
    }

    @Override
    public String getChartType() {
        return "Combined";
    }

    @Override
    public int getLegendShapeWidth(int seriesIndex) {
        XYChart chart = getXYChart(seriesIndex);
        return chart.getLegendShapeWidth(getChartSeriesIndex(seriesIndex));
    }

    @Override
    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y, int seriesIndex, Paint paint) {
        XYChart chart = getXYChart(seriesIndex);
        chart.drawLegendShape(canvas, renderer, x, y, getChartSeriesIndex(seriesIndex), paint);
    }

    private XYChart getXYChart(int seriesIndex) {
        for (int i = 0; i < chartDefinitions.length; i++) {
            if (chartDefinitions[i].containsSeries(seriesIndex)) {
                return mCharts[i];
            }
        }
        throw new IllegalArgumentException("Unknown series with index " + seriesIndex);
    }

    private int getChartSeriesIndex(int seriesIndex) {
        for (int i = 0; i < chartDefinitions.length; i++) {
            if (chartDefinitions[i].containsSeries(seriesIndex)) {
                return chartDefinitions[i].getChartSeriesIndex(seriesIndex);
            }
        }
        throw new IllegalArgumentException("Unknown series with index " + seriesIndex);
    }

    /**********************************************************************************************/
    public static class StockCombinedChartDef implements Serializable {
        /**
         * The chart type.
         */
        private String type;
        /**
         * The series index.
         */
        private int[] seriesIndex;

        /**
         * Constructs a chart definition.
         *
         * @param type        XY chart type
         * @param seriesIndex corresponding data series indexes
         */
        public StockCombinedChartDef(String type, int... seriesIndex) {
            this.type = type;
            this.seriesIndex = seriesIndex;
        }

        public boolean containsSeries(int seriesIndex) {
            return getChartSeriesIndex(seriesIndex) >= 0;
        }

        public int getChartSeriesIndex(int seriesIndex) {
            for (int i = 0; i < getSeriesIndex().length; i++) {
                if (this.seriesIndex[i] == seriesIndex) {
                    return i;
                }
            }
            return -1;
        }

        public String getType() {
            return type;
        }

        public int[] getSeriesIndex() {
            return seriesIndex;
        }
    }

    /***********************************************************************/
    /* 绘制经线(竖线) */
    protected void drawLongitudeLine(Canvas canvas, int width, int height, int top, int bottom) {
        float postOffset = (right - left) / (longitudeNum - 1);
        for (int i = 1; i < longitudeNum; i++) {
            Path path = new Path();
            path.moveTo(i * postOffset + left, top);
            path.lineTo(i * postOffset + left, bottom);
            canvas.drawPath(path, longitudePaint);
        }
    }

    /* 绘制纬线(横线) */
    protected void drawLatitudeLine(Canvas canvas, int width, int height, int top, int bottom) {
        float postOffset = (bottom - top) / (latitudeNum - 1);
        float offset = bottom;
        for (int i = 0; i < latitudeNum; i++) {
            Path path = new Path();
            path.moveTo(left, offset - i * postOffset);
            path.lineTo(right, offset - i * postOffset);
            canvas.drawPath(path, longitudePaint);
        }
    }

    /* 绘制分割线 */
    protected void drawDividerLine(Canvas canvas, double y, boolean transform) {
        //计算位置
        float val;
        if (transform) {
            val = bottom - (float) ((bottom - top) * (y - mRenderer.getYAxisMin()) / (mRenderer.getYAxisMax() - mRenderer.getYAxisMin()));
        } else {
            val = (float) y;
        }
        //绘制分割线
        Path path = new Path();
        path.moveTo(left, val);
        path.lineTo(right, val);
        canvas.drawPath(path, longitudePaint);
    }

    /* 绘制图标文字 */
    protected void drawCanvasText(Canvas canvas, double y1, double y2) {
        if (paint.getTypeface() == null) {
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.create(mRenderer.getTextTypefaceName(),
                    mRenderer.getTextTypefaceStyle()));
        }

        //计算坐标
        float val01 = bottom - (float) ((bottom - top) * (y2 - mRenderer.getYAxisMin()) / (mRenderer.getYAxisMax() - mRenderer.getYAxisMin()));

        paint.setTextSize(DisplayUtil.sp2px(9));
        paint.setStrokeWidth(1);
        //取两位小数点
        float textWidth = paint.measureText("1289.89亿万");
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#666666"));
        canvas.drawText("1289.89亿万", right - textWidth, val01 - DisplayUtil.dp2px(2), paint);
    }

    /* 绘制XY轴值 */
    protected void drawXYValueRec(Canvas canvas, int bottom) {
        if (paint.getTypeface() == null) {
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.create(mRenderer.getTextTypefaceName(),
                    mRenderer.getTextTypefaceStyle()));
        }
        if (touchPoint == null) {
            return;
        }

        paint.setTextSize(DisplayUtil.sp2px(10));
        paint.setStrokeWidth(1);
        //取两位小数点
        BigDecimal bd = new BigDecimal(valueY);
        bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        String strX = "" + getLabelX(mRenderer, valueX);
        String strY = bd + "";
        float textLengthX = paint.measureText(strX) / 2;
        float textLengthY = paint.measureText(strY);

        //文字边距5
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ffaaaaaa"));
        canvas.drawRect(touchPoint.x - textLengthX - DisplayUtil.dp2px(4),
                bottom + DisplayUtil.dp2px(3), touchPoint.x + textLengthX + DisplayUtil.dp2px(4),
                bottom + DisplayUtil.dp2px(15), paint);
        canvas.drawRect(left * 0.10f, circleY - DisplayUtil.dp2px(6),
                left * 0.10f + textLengthY + DisplayUtil.dp2px(8),
                circleY + DisplayUtil.dp2px(6), paint);

        paint.setColor(Color.parseColor("#ff000000"));
        canvas.drawText(strX, touchPoint.x - textLengthX, bottom + DisplayUtil.dp2px(12), paint);
        //文字left位置为left*0.10f + 4；
        canvas.drawText(strY, left * 0.10f + DisplayUtil.dp2px(4), circleY + DisplayUtil.dp2px(4), paint);
    }

    /* 绘制Y轴 */
    public List<Double> getLabelY() {
        double yMin = getRenderer().getYAxisMin();
        double yMax = getRenderer().getYAxisMax();

        BigDecimal bd01 = new BigDecimal(yMin);
        bd01 = bd01.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal bd02 = new BigDecimal(yMax);
        bd02 = bd02.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal bd03 = new BigDecimal((bd02.doubleValue() + bd01.doubleValue()) / 2);
        bd03 = bd03.setScale(2, BigDecimal.ROUND_HALF_UP);

        List<Double> xVal = new ArrayList<>();
        xVal.add(bd01.doubleValue());
        xVal.add(bd03.doubleValue());
        xVal.add(bd02.doubleValue());
        return xVal;
    }

    /* 绘制日期 */
    protected String getLabelX(XYMultipleSeriesRenderer renderer, double label) {
        return "09:30";
    }

    /* 绘制垂直横线 */
    protected void drawVerticalLine(Canvas canvas, int top, int bottom) {
        if (touchPoint == null) {
            return;
        }
        canvas.drawLine(touchPoint.x, top, touchPoint.x, bottom, horizontalPaint);
    }

    /* 绘制水平横线 */
    protected void drawHorizontalLine(Canvas canvas) {
        if (touchPoint == null) {
            return;
        }
        canvas.drawLine(left, circleY, right, circleY, horizontalPaint);
    }

    /* 绘制横竖交汇点 */
    protected void drawTouchPoint(Canvas canvas) {
        if (touchPoint == null) {
            return;
        }
        canvas.drawCircle(touchPoint.x, circleY, DisplayUtil.dp2px(2), horizontalPaint);
    }

    /* 获取point */
    public PointF getTouchPoint() {
        return touchPoint;
    }

    private int getLabelLinePos(Paint.Align align) {
        int pos = 4;
        if (align == Paint.Align.LEFT) {
            pos = -pos;
        }
        return pos;
    }
}
