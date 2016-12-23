package com.example.jiangyue.androidap.chart.model;

import com.example.jiangyue.androidap.test.KLineBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyue on 16/11/22.
 */
public class RangeKLineSeries extends CategorySeries {
    /**
     * The series values.
     */
    private List<KLineBean> mMaxValues = new ArrayList<KLineBean>();

    /**
     * Builds a new category series.
     *
     * @param title the series title
     */
    public RangeKLineSeries(String title) {
        super(title);
    }

    /**
     * Adds new values to the series
     *
     * @param value the new minimum value
     */
    public synchronized void add(KLineBean value) {
        mMaxValues.add(value);
    }

    /**
     * Removes existing values from the series.
     *
     * @param index the index in the series of the values to remove
     */
    public synchronized void remove(int index) {
        super.remove(index);
        mMaxValues.remove(index);
    }

    /**
     * Removes all the existing values from the series.
     */
    public synchronized void clear() {
        super.clear();
        mMaxValues.clear();
    }

    /**
     * Returns the series item count.
     *
     * @return the series item count
     */
    public synchronized int getItemCount() {
        return mMaxValues.size();
    }


    /**
     * Transforms the range category series to an XY series.
     *
     * @return the XY series
     */
    public XYSeries toXYSeries() {
        XYSeries xySeries = new XYSeries(getTitle());
        int length = getItemCount();
        for (int k = 0; k < length; k++) {
            xySeries.add(k + 1, StringToDecimal(mMaxValues.get(k).open + "", 2));
            // the new fast XYSeries implementation doesn't allow 2 values at the same X,
            // so I had to do a hack until I find a better solution
            xySeries.add(k + 1.000001, StringToDecimal(mMaxValues.get(k).close + "", 2));
            xySeries.add(k + 1.000002, StringToDecimal(mMaxValues.get(k).low + "", 2));
            xySeries.add(k + 1.000003, StringToDecimal(mMaxValues.get(k).high + "", 2));
        }
        return xySeries;
    }

    /**
     * 字符转几位小数
     *
     * @param str
     * @param num
     */
    public static double StringToDecimal(String str, int num) {
        BigDecimal bdVal = null;
        try {
            bdVal = new BigDecimal(str);
            bdVal = bdVal.setScale(num, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
        } finally {
            if (bdVal == null) {
                bdVal = new BigDecimal(0);
                bdVal = bdVal.setScale(num, BigDecimal.ROUND_HALF_UP);
            }
        }
        return bdVal.doubleValue();
    }
}
