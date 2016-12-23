package com.example.jiangyue.androidap.chart.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.jiangyue.androidap.chart.model.XYMultipleSeriesDataset;
import com.example.jiangyue.androidap.chart.model.XYSeries;
import com.example.jiangyue.androidap.chart.renderer.XYMultipleSeriesRenderer;
import com.example.jiangyue.androidap.chart.renderer.XYSeriesRenderer;

import java.util.List;

/**
 * Created by jiangyue on 16/10/31.
 */
public class StockRangeBarChart extends StockBarChart {

    /**
     * The chart type.
     */
    public static final String TYPE = StockRangeBarChart.class.getName();

    StockRangeBarChart() {
    }

    StockRangeBarChart(Type type) {
        super(type);
    }

    /**
     * Builds a new range bar chart instance.
     *
     * @param dataset  the multiple series dataset
     * @param renderer the multiple series renderer
     * @param type     the range bar chart type
     */
    public StockRangeBarChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, Type type) {
        super(dataset, renderer, type);
    }

    /**
     * The graphical representation of a series.
     *
     * @param canvas         the canvas to paint to
     * @param paint          the paint to be used for drawing
     * @param points         the array of points to be used for drawing the series
     * @param seriesRenderer the series renderer
     * @param yAxisValue     the minimum value of the y axis
     * @param seriesIndex    the index of the series currently being drawn
     * @param startIndex     the start index of the rendering points
     */
    @Override
    public void drawSeries(Canvas canvas, Paint paint, List<Float> points,
                           XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, int startIndex) {
        int seriesNr = mDataset.getSeriesCount();
        int length = points.size();
        paint.setColor(seriesRenderer.getColor());
        paint.setStyle(Paint.Style.FILL);
        float halfDiffX = getHalfDiffX(points, length, seriesNr);
        int start = 0;
        if (startIndex > 0) {
            start = 2;
        }
        for (int i = start; i < length; i += 8) {
            if (points.size() > i + 7) {
                float xMin = points.get(i);
                float yMin = points.get(i + 1);
                // xMin = xMax
                float xMax = points.get(i + 2);
                float yMax = points.get(i + 3);
                //line
                float kLow = points.get(i + 5);
                float kHigh = points.get(i + 7);

                if (kLow >= kHigh) {
                    float temp = kHigh;
                    kHigh = kLow;
                    kLow = temp;
                }

                if (yMax < yMin) {
                    paint.setColor(Color.parseColor("#4bbb59"));
                } else {
                    paint.setColor(Color.parseColor("#d74c44"));
                }

                drawBar(canvas, xMin, yMin, xMax, yMax, halfDiffX, seriesNr, seriesIndex, paint);
                //绘制上下阴影
                if (kHigh > yMax && kHigh > yMin) {
                    drawBar(canvas, xMin + (xMax - xMin) / 2, yMax > yMin ? yMax : yMin, xMin + (xMax - xMin) / 2, kHigh, 1, seriesNr, seriesIndex, paint);
                }
                if (kLow < yMax && kLow < yMin) {
                    drawBar(canvas, xMin + (xMax - xMin) / 2, yMax < yMin ? yMax : yMin, xMin + (xMax - xMin) / 2, kLow, 1, seriesNr, seriesIndex, paint);
                }
            }
        }
        paint.setColor(seriesRenderer.getColor());
    }

    /**
     * Calculates and returns the half-distance in the graphical representation of
     * 2 consecutive points.
     *
     * @param points   the points
     * @param length   the points length
     * @param seriesNr the series number
     * @return the calculated half-distance value
     */
    protected float getHalfDiffX(List<Float> points, int length, int seriesNr) {
        float barWidth = mRenderer.getBarWidth();
        if (barWidth > 0) {
            return barWidth / 2;
        }
        int div = length;
        if (length > 4) {
            div = length / 2 - 2;
        }
        float halfDiffX = (points.get(length / 8 * 8 - 8) - points.get(0)) / div;
        if (halfDiffX == 0) {
            halfDiffX = 10;
        }

        if (mType != Type.STACKED && mType != Type.HEAPED) {
            halfDiffX /= seriesNr;
        }
        return halfDiffX / getCoeficient();
    }


    /**
     * The graphical representation of the series values as text.
     *
     * @param canvas      the canvas to paint to
     * @param series      the series to be painted
     * @param renderer    the series renderer
     * @param paint       the paint to be used for drawing
     * @param points      the array of points to be used for drawing the series
     * @param seriesIndex the index of the series currently being drawn
     * @param startIndex  the start index of the rendering points
     */
    protected void drawChartValuesText(Canvas canvas, XYSeries series, XYSeriesRenderer renderer,
                                       Paint paint, List<Float> points, int seriesIndex, int startIndex) {
    }

    /**
     * Returns the value of a constant used to calculate the half-distance.
     *
     * @return the constant value
     */
    protected float getCoeficient() {
        return 0.5f;
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
