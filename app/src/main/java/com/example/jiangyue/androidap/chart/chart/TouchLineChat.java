package com.example.jiangyue.androidap.chart.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jiangyue on 16/6/1.
 */
public class TouchLineChat extends LineChart {

    private Paint paint = new Paint();
    private LongitudeLatitudePaint longitudePaint = new LongitudeLatitudePaint();
    private PointF touchPoint;
    private int left, right, top, bottom;
    private int longitudeNum = 5;
    private int latitudeNum = 5;
    private float circleY;
    private double valueX, valueY;

    TouchLineChat() {
    }

    /**
     * Builds a new time chart instance.
     *
     * @param dataset  the multiple series dataset
     * @param renderer the multiple series renderer
     */
    public TouchLineChat(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        super(dataset, renderer);
    }

    /**
     * 设置竖线条数
     *
     * @param longitudeNum
     */
    public void setLongitudeNum(int longitudeNum) {
        this.longitudeNum = longitudeNum;
    }

    @Override
    public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
        super.draw(canvas, x, y, width, height, paint);

        int legendSize = getLegendSize(mRenderer, height / 5, mRenderer.getAxisTitleTextSize());
        int[] margins = mRenderer.getMargins();
        top = y + margins[0];
        bottom = y + height - margins[2] - legendSize;
        left = x + margins[1];
        right = x + width - margins[3];

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

        //绘制xy轴显示值
        drawXYValueRec(canvas, bottom);

        //绘制走势类别
        if (mDataset.getSeriesCount() > 1) {
            drawChatLegend(canvas, width);
        }

        //绘制最新净值
//        drawNewValueRec(canvas, top, bottom);
    }


    @Override
    public void drawPoints(Canvas canvas, Paint paint, List<Float> pointsList,
                           XYSeriesRenderer seriesRenderer, float yAxisValue,
                           int seriesIndex, int startIndex) {
    }

    @Override
    protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas, Paint paint, int left, int top, int bottom, double xPixelsPerUnit, double minX, double maxX) {
        //计算val
        double[] values;
        int step = (int) (maxX - minX) / 4;
        if (step == 0) {
            values = new double[]{
                    minX,
                    maxX};
        } else {
            values = new double[]{
                    minX,
                    minX + step * 1,
                    minX + step * 2,
                    minX + step * 3,
                    maxX};
        }
        int length = values.length;

        //绘制view
        boolean showXLabels = mRenderer.isShowXLabels();
        boolean showGridY = mRenderer.isShowGridY();
        if (showGridY) {
            mGridPaint.setStyle(Paint.Style.STROKE);
            mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
        }
        boolean showTickMarks = mRenderer.isShowTickMarks();
        for (int i = 0; i < length; i++) {
            double label = values[i];
            float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
            if (showXLabels) {
                paint.setColor(mRenderer.getXLabelsColor());
                if (showTickMarks) {
                    canvas.drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3, paint);
                }
                drawText(canvas, getLabelX(mRenderer, label), xLabel,
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
        XYMultipleSeriesRenderer.Orientation or = mRenderer.getOrientation();
        boolean showGridX = mRenderer.isShowGridX();
        if (showGridX) {
            mGridPaint.setStyle(Paint.Style.STROKE);
            mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
        }
        boolean showYLabels = mRenderer.isShowYLabels();
        boolean showTickMarks = mRenderer.isShowTickMarks();

        //重新计算间距
        List<Double> yLabels = getLabelY();
        yPixelsPerUnit[0] = (float) (((yPixelsPerUnit[0] * (getRenderer().getYAxisMax() - getRenderer().getYAxisMin())))
                / (yLabels.get(yLabels.size() - 1) - yLabels.get(0)));

        for (int i = 0; i < maxScaleNumber; i++) {
            paint.setTextAlign(mRenderer.getYLabelsAlign(i));
            int length = yLabels.size();
            for (int j = 0; j < length; j++) {
                double label = yLabels.get(j);
                Paint.Align axisAlign = mRenderer.getYAxisAlign(i);
                boolean textLabel = mRenderer.getYTextLabel(label, i) != null;
                float yLabel = (float) (bottom - yPixelsPerUnit[i] * (label - yLabels.get(0)));
                if (or == XYMultipleSeriesRenderer.Orientation.HORIZONTAL) {
                    if (showYLabels && !textLabel) {
                        paint.setColor(mRenderer.getYLabelsColor(i));
                        if (axisAlign == Paint.Align.LEFT) {
                            if (showTickMarks) {
                                canvas.drawLine(left + getLabelLinePos(axisAlign), yLabel, left, yLabel, paint);
                            }
                            drawText(canvas, getLabel(mRenderer.getYLabelFormat(i), label),
                                    left - mRenderer.getYLabelsPadding(),
                                    yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
                                    mRenderer.getYLabelsAngle());
                        } else {
                            if (showTickMarks) {
                                canvas.drawLine(right, yLabel, right + getLabelLinePos(axisAlign), yLabel, paint);
                            }
                            drawText(canvas, getLabel(mRenderer.getYLabelFormat(i), label),
                                    right + mRenderer.getYLabelsPadding(),
                                    yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
                                    mRenderer.getYLabelsAngle());
                        }
                    }
                    if (showGridX) {
                        mGridPaint.setColor(mRenderer.getGridColor(i));
                        canvas.drawLine(left, yLabel, right, yLabel, mGridPaint);
                    }
                } else if (or == XYMultipleSeriesRenderer.Orientation.VERTICAL) {
                    if (showYLabels && !textLabel) {
                        paint.setColor(mRenderer.getYLabelsColor(i));
                        if (showTickMarks) {
                            canvas.drawLine(right - getLabelLinePos(axisAlign), yLabel, right, yLabel, paint);
                        }
                        drawText(canvas, getLabel(mRenderer.getLabelFormat(), label),
                                right + 10 + mRenderer.getYLabelsPadding(),
                                yLabel - mRenderer.getYLabelsVerticalPadding(), paint, mRenderer.getYLabelsAngle());
                    }
                    if (showGridX) {
                        mGridPaint.setColor(mRenderer.getGridColor(i));
                        if (showTickMarks) {
                            canvas.drawLine(right, yLabel, left, yLabel, mGridPaint);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected String getLabel(NumberFormat format, double label) {
        BigDecimal bd = new BigDecimal(label);
        bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        return bd + "    ";
    }

    /* 手势长按触发显示移动坐标 */
    private static final int LONG_PRESS_TIMEOUT = 300;
    private onLongTouchListener longTouchListener;
    private boolean canDrawGesView = false;
    private Handler handler = new Handler();
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

    @Override
    public void onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
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

    /***********************************************************************/
    private int getLabelLinePos(Paint.Align align) {
        int pos = 4;
        if (align == Paint.Align.LEFT) {
            pos = -pos;
        }
        return pos;
    }

    /***********************************************************************/

    /* 获取point */
    public PointF getTouchPoint() {
        return touchPoint;
    }

    /* 绘制垂直横线 */
    protected void drawVerticalLine(Canvas canvas, int top, int bottom) {
        if (touchPoint == null) {
            return;
        }
        canvas.drawLine(touchPoint.x, top, touchPoint.x, bottom, new HorizontalLinePaint());
    }

    /* 绘制水平横线 */
    protected void drawHorizontalLine(Canvas canvas) {
        if (touchPoint == null) {
            return;
        }
        canvas.drawLine(left, circleY, right, circleY, new HorizontalLinePaint());
    }

    /* 绘制经线(竖线) */
    protected void drawLongitudeLine(Canvas canvas, int width, int height, int top, int bottom) {
        float postOffset = (right - left) / (longitudeNum - 1);
        for (int i = 0; i < longitudeNum; i++) {
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

    //涨跌幅
    public String[] performVo = new String[]{"", ""};

    /* 绘制走势图列表 */
    protected void drawChatLegend(Canvas canvas, int width) {
        try {
            int length = mDataset.getSeriesCount();
            String[] originTitles = new String[length];
            String[] titles = new String[length];
            for (int i = 0; i < length; i++) {
                titles[i] = mDataset.getSeriesAt(i).getTitle() + performVo[i];
                originTitles[i] = mDataset.getSeriesAt(i).getTitle();
            }
            final float lineSize = DisplayUtil.dp2px(8);//方块宽度
            final float space = DisplayUtil.dp2px(20);//间距
            float currentX = left;
            float currentY = DisplayUtil.dp2px(10);
            int sLength = Math.min(titles.length, mRenderer.getSeriesRendererCount());
            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(DisplayUtil.sp2px(11));
            //获取文字长度
            float paddingLeft = 0;
            for (int i = 0; i < sLength; i++) {
                String text = titles[i];
                float[] widths = new float[text.length()];
                paint.getTextWidths(text, widths);
                float sum = 0;
                for (float value : widths) {
                    sum += value;
                }
                if (i == sLength - 1) {
                    paddingLeft += lineSize + sum;
                } else {
                    paddingLeft += lineSize + space + sum;
                }
            }
            paddingLeft = (right - left - paddingLeft) / 2;
            //绘制
            for (int i = 0; i < sLength; i++) {
                SimpleSeriesRenderer r = mRenderer.getSeriesRendererAt(i);
                String text = titles[i];
                if (titles.length == mRenderer.getSeriesRendererCount()) {
                    paint.setColor(r.getColor());
                } else {
                    paint.setColor(Color.LTGRAY);
                }
                float[] widths = new float[text.length()];
                paint.getTextWidths(text, widths);
                float sum = 0;
                float titleSum = 0;
                for (float value : widths) {
                    sum += value;
                }
                for (int j = 0, size = originTitles[i].length(); j < size; j++) {
                    titleSum += widths[j];
                }
                float extraSize = lineSize + space + sum;
                float currentWidth = currentX + extraSize;

                if (i > 0 && getExceed(currentWidth, mRenderer, right, width)) {
                    currentX = left;
                    currentY += mRenderer.getLegendTextSize();
                    currentWidth = currentX + extraSize;
                }
                if (getExceed(currentWidth, mRenderer, right, width)) {
                    float maxWidth = right - currentX - lineSize - space;
                    if (isVertical(mRenderer)) {
                        maxWidth = width - currentX - lineSize - space;
                    }
                    int nr = paint.breakText(text, true, maxWidth, widths);
                    text = text.substring(0, nr) + "...";
                }
                canvas.drawRect(paddingLeft + currentX + DisplayUtil.dp2px(5), DisplayUtil.dp2px(7),
                        paddingLeft + currentX + DisplayUtil.dp2px(13), DisplayUtil.dp2px(15),
                        paint);
                paint.setColor(Color.parseColor("#999999"));
                drawString(canvas, text.substring(0, originTitles[i].length()), paddingLeft + currentX + lineSize + DisplayUtil.dp2px(7), currentY + DisplayUtil.dp2px(5), paint);
                //绘制涨跌幅
                String performVo = text.substring(originTitles[i].length(), titles[i].length());
                if (!TextUtils.isEmpty(performVo)) {
                    if (Double.parseDouble(performVo.replace("+", "").replace("%", "")) >= 0) {
                        paint.setColor(Color.parseColor("#f9513e"));
                    } else {
                        paint.setColor(Color.parseColor("#4bbb59"));
                    }
                    //绘制文字
                    drawString(canvas, performVo, titleSum + paddingLeft + currentX + lineSize + DisplayUtil.dp2px(9), currentY + DisplayUtil.dp2px(5), paint);
                }
                currentX += extraSize;
            }
        } catch (Exception e) {
            Log.e("drawChatLegend", e.toString());
        }
    }

    /* 绘制最新净值 */
    private void drawNewValueRec(Canvas canvas, int top, int bottom) {
        if (mDataset.getSeriesAt(0) != null) {
            XYSeries series = mDataset.getSeriesAt(0);
            BigDecimal bd = new BigDecimal(series.getXYMap().getYByIndex(series.getXYMap().size() - 1));
            bd = bd.setScale(4, BigDecimal.ROUND_HALF_UP);
            String value = bd + "";
            //背景
            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(DisplayUtil.sp2px(11));
            paint.setColor(Color.parseColor("#ffb79364"));
            float textWidth = paint.measureText(value);
            canvas.drawRect(right - DisplayUtil.dp2px(15) - textWidth, top + DisplayUtil.dp2px(5),
                    right - DisplayUtil.dp2px(5), top + DisplayUtil.dp2px(20), paint);
            //文字
            paint.setTextSize(DisplayUtil.sp2px(12));
            paint.setColor(Color.parseColor("#ffffffff"));
            canvas.drawText(value, right - DisplayUtil.dp2px(10) - textWidth, top + DisplayUtil.dp2px(17), paint);

        }
    }

    /* 绘制日期 */
    protected String getLabelX(XYMultipleSeriesRenderer renderer, double label) {
        if (renderer.getYValue() != null && label < renderer.getYValue().length) {
            label = renderer.getYValue()[(int) label];
            label = Math.abs(label);
            String month = label / 100 >= 10 ? (int) (label / 100) + "" : "0" + (int) (label / 100);
            String date = label % 100 >= 10 ? (int) (label % 100) + "" : "0" + (int) (label % 100);
            return month + "-" + date;
        }
        return "";
    }

    /* 绘制Y轴 */
    public List<Double> getLabelY() {
        double yMin = getRenderer().getYAxisMin();
        double yMax = getRenderer().getYAxisMax();

        BigDecimal bd01 = new BigDecimal(yMin);
        bd01 = bd01.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal bd02 = new BigDecimal(yMax);
        bd02 = bd02.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal bd03 = new BigDecimal((bd02.doubleValue() - bd01.doubleValue()) / 4);
        bd03 = bd03.setScale(3, BigDecimal.ROUND_HALF_UP);
        double step = bd03.doubleValue();

        List<Double> xVal = new ArrayList<>();
        xVal.add(bd01.doubleValue());
        xVal.add(bd01.doubleValue() + step * 1);
        xVal.add(bd01.doubleValue() + step * 2);
        xVal.add(bd01.doubleValue() + step * 3);
        xVal.add(bd02.doubleValue());
        return xVal;
    }

}
