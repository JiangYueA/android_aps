package com.example.jiangyue.androidap.chart.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.jiangyue.androidap.chart.model.XYMultipleSeriesDataset;
import com.example.jiangyue.androidap.chart.renderer.SimpleSeriesRenderer;
import com.example.jiangyue.androidap.chart.renderer.XYMultipleSeriesRenderer;
import com.example.jiangyue.androidap.chart.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyue on 16/10/24.
 */
public class StockLinearChart extends XYChart {
    /**
     * The constant to identify this chart type.
     */
    public static final String TYPE = "Line";
    /**
     * The legend shape width.
     */
    private static final int SHAPE_WIDTH = 30;
    /**
     * The scatter chart to be used to draw the data points.
     */
    private ScatterChart pointsChart;

    /**
     * The main mrender;
     */
    private XYMultipleSeriesRenderer mainRender;
    private double[] yPixelsPerUnit;

    /**
     * The value
     */
    private double tempMinY;
    private double tempMaxY;
    private float seriesFlag = 0.4f;

    public void setMainRender(XYMultipleSeriesRenderer mainRender) {
        this.mainRender = mainRender;
    }

    public void setTempMinY(double tempMinY) {
        this.tempMinY = tempMinY;
    }

    public void setTempMaxY(double tempMaxY) {
        this.tempMaxY = tempMaxY;
    }

    public void setSeriesFlag(float seriesFlag) {
        this.seriesFlag = seriesFlag;
    }

    @Override
    public void setYPixelsPerUnit(int seriesIndex, double[] yPixelsPerUnit) {
        this.yPixelsPerUnit = yPixelsPerUnit;
    }


    StockLinearChart() {
    }

    /**
     * Builds a new line chart instance.
     *
     * @param dataset  the multiple series dataset
     * @param renderer the multiple series renderer
     */
    public StockLinearChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        super(dataset, renderer);
        pointsChart = new ScatterChart(dataset, renderer);
    }

    /**
     * Sets the series and the renderer.
     *
     * @param dataset  the series dataset
     * @param renderer the series renderer
     */
    protected void setDatasetRenderer(XYMultipleSeriesDataset dataset,
                                      XYMultipleSeriesRenderer renderer) {
        super.setDatasetRenderer(dataset, renderer);
        pointsChart = new ScatterChart(dataset, renderer);
    }

    /**
     * The graphical representation of a series.
     *
     * @param canvas      the canvas to paint to
     * @param paint       the paint to be used for drawing
     * @param points      the array of points to be used for drawing the series
     * @param yAxisValue  the minimum value of the y axis
     * @param seriesIndex the index of the series currently being drawn
     * @param startIndex  the start index of the rendering points
     */
    @Override
    public void drawSeries(Canvas canvas, Paint paint, List<Float> points, XYSeriesRenderer renderer,
                           float yAxisValue, int seriesIndex, int startIndex) {
        float lineWidth = paint.getStrokeWidth();
        paint.setStrokeWidth(renderer.getLineWidth());
        final XYSeriesRenderer.FillOutsideLine[] fillOutsideLine = renderer.getFillOutsideLine();

        //获取成交量最大最下值
        if (seriesFlag == 0.4f) {
            double tempTradeMaxY = -1000000000000d;
            double tempTradeMinY = 1000000000000d;
            int minX = (int) mainRender.getXAxisMin();
            int maxX = (int) mainRender.getXAxisMax();
            if (seriesFlag != 0.4f) {
                for (int j = minX; j < maxX; j++) {
                    double val = mDataset.getSeriesAt(0).getXYMap().getYByIndex(j);
                    if (val > tempTradeMaxY) {
                        tempTradeMaxY = val;
                    }
                    if (val < tempTradeMinY) {
                        tempTradeMinY = val;
                    }
                }
            }

            //获取最新的val
            // TODO seriesFlag 原先使用seriesFlag作为temp的系数
            double curTempMinY = tempMinY - (tempMaxY - tempMinY) * 0.4f;
            double curYPixelsPerUnit = ((getScreenR().bottom - getScreenR().top) / (tempMaxY - curTempMinY));
            double baseTradeValue = (0.4 - 0.1) * (tempMaxY - tempMinY) / (tempTradeMaxY - tempTradeMinY);

            //重设points值
            for (int i = 0, length = points.size(); i < length; i += 2) {
                if (points.size() > i + 1 && mDataset.getSeriesAt(0).getXYMap().size() > minX + i / 2) {
                    if (yPixelsPerUnit != null && yPixelsPerUnit.length > 0) {
                        double value = mDataset.getSeriesAt(0).getXYMap().getYByIndex(minX + i / 2);
                        //成交量转换
                        value = seriesFlag != 0.4f ? (curTempMinY + (value - tempTradeMinY) * baseTradeValue) : value;
                        //坐标点转换
                        points.set(i + 1, getScreenR().bottom - (float) ((value - curTempMinY) * curYPixelsPerUnit));
                    }
                }
            }
        }

        for (XYSeriesRenderer.FillOutsideLine fill : fillOutsideLine) {
            if (fill.getType() != XYSeriesRenderer.FillOutsideLine.Type.NONE) {
                paint.setColor(fill.getColor());
                // TODO: find a way to do area charts without duplicating data
                List<Float> fillPoints = new ArrayList<Float>();
                int[] range = fill.getFillRange();
                if (range == null) {
                    fillPoints.addAll(points);
                } else {
                    if (points.size() > range[0] * 2 && points.size() > range[1] * 2) {
                        fillPoints.addAll(points.subList(range[0] * 2, range[1] * 2));
                    }
                }

                final float referencePoint;
                switch (fill.getType()) {
                    case BOUNDS_ALL:
                        referencePoint = yAxisValue;
                        break;
                    case BOUNDS_BELOW:
                        referencePoint = yAxisValue;
                        break;
                    case BOUNDS_ABOVE:
                        referencePoint = yAxisValue;
                        break;
                    case BELOW:
                        referencePoint = canvas.getHeight();
                        break;
                    case ABOVE:
                        referencePoint = 0;
                        break;
                    default:
                        throw new RuntimeException(
                                "You have added a new type of filling but have not implemented.");
                }
                if (fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ABOVE
                        || fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_BELOW) {
                    List<Float> boundsPoints = new ArrayList<Float>();
                    boolean add = false;
                    int length = fillPoints.size();
                    if (length > 0 && fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ABOVE
                            && fillPoints.get(1) < referencePoint
                            || fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_BELOW
                            && fillPoints.get(1) > referencePoint) {
                        boundsPoints.add(fillPoints.get(0));
                        boundsPoints.add(fillPoints.get(1));
                        add = true;
                    }

                    for (int i = 3; i < length; i += 2) {
                        float prevValue = fillPoints.get(i - 2);
                        float value = fillPoints.get(i);

                        if (prevValue < referencePoint && value > referencePoint || prevValue > referencePoint
                                && value < referencePoint) {
                            float prevX = fillPoints.get(i - 3);
                            float x = fillPoints.get(i - 1);
                            boundsPoints.add(prevX + (x - prevX) * (referencePoint - prevValue)
                                    / (value - prevValue));
                            boundsPoints.add(referencePoint);
                            if (fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ABOVE && value > referencePoint
                                    || fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_BELOW && value < referencePoint) {
                                i += 2;
                                add = false;
                            } else {
                                boundsPoints.add(x);
                                boundsPoints.add(value);
                                add = true;
                            }
                        } else {
                            if (add || fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ABOVE
                                    && value < referencePoint || fill.getType() == XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_BELOW
                                    && value > referencePoint) {
                                boundsPoints.add(fillPoints.get(i - 1));
                                boundsPoints.add(value);
                            }
                        }
                    }

                    fillPoints.clear();
                    fillPoints.addAll(boundsPoints);
                }
                int length = fillPoints.size();
                if (length > 0) {
                    fillPoints.set(0, fillPoints.get(0) + 1);
                    fillPoints.add(fillPoints.get(length - 2));
                    fillPoints.add(referencePoint);
                    fillPoints.add(fillPoints.get(0));
                    fillPoints.add(fillPoints.get(length + 1));
                    for (int i = 0; i < length + 4; i += 2) {
                        if (fillPoints.get(i + 1) < 0) {
                            fillPoints.set(i + 1, 0f);
                        }
                    }

                    paint.setStyle(Paint.Style.FILL);
                    drawPath(canvas, fillPoints, paint, true);
                }
            }
        }
        paint.setColor(renderer.getColor());
        paint.setStyle(Paint.Style.STROKE);
        drawPath(canvas, points, paint, false);
        paint.setStrokeWidth(lineWidth);
    }

    @Override
    protected ClickableArea[] clickableAreasForPoints(List<Float> points, List<Double> values,
                                                      float yAxisValue, int seriesIndex, int startIndex) {
        int length = points.size();
        ClickableArea[] ret = new ClickableArea[length / 2];
        for (int i = 0; i < length; i += 2) {
            int selectableBuffer = mRenderer.getSelectableBuffer();
            ret[i / 2] = new ClickableArea(new RectF(points.get(i) - selectableBuffer, points.get(i + 1)
                    - selectableBuffer, points.get(i) + selectableBuffer, points.get(i + 1)
                    + selectableBuffer), values.get(i), values.get(i + 1));
        }
        return ret;
    }

    /**
     * Returns the legend shape width.
     *
     * @param seriesIndex the series index
     * @return the legend shape width
     */
    public int getLegendShapeWidth(int seriesIndex) {
        return SHAPE_WIDTH;
    }

    /**
     * The graphical representation of the legend shape.
     *
     * @param canvas      the canvas to paint to
     * @param renderer    the series renderer
     * @param x           the x value of the point the shape should be drawn at
     * @param y           the y value of the point the shape should be drawn at
     * @param seriesIndex the series index
     * @param paint       the paint to be used for drawing
     */
    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {
        float oldWidth = paint.getStrokeWidth();
        paint.setStrokeWidth(((XYSeriesRenderer) renderer).getLineWidth());
        canvas.drawLine(x, y, x + SHAPE_WIDTH, y, paint);
        paint.setStrokeWidth(oldWidth);
        if (isRenderPoints(renderer)) {
            pointsChart.drawLegendShape(canvas, renderer, x + 5, y, seriesIndex, paint);
        }
    }

    /**
     * Returns if the chart should display the points as a certain shape.
     *
     * @param renderer the series renderer
     */
    public boolean isRenderPoints(SimpleSeriesRenderer renderer) {
        return ((XYSeriesRenderer) renderer).getPointStyle() != PointStyle.POINT;
    }

    /**
     * Returns the scatter chart to be used for drawing the data points.
     *
     * @return the data points scatter chart
     */
    public ScatterChart getPointsChart() {
        return pointsChart;
    }

    /**
     * Returns the chart type identifier.
     *
     * @return the chart type
     */
    public String getChartType() {
        return TYPE;
    }

}
