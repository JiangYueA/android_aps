package com.example.jiangyue.androidap.test;

/**
 * Created by jiangyue on 16/6/23.
 */
public class DateModel {
    public String id;//	基金ID
    public String strDateSeries; //	日期
    public long dtDateSeries; //	日期
    public double dValueSeries;    //净值(活期：7日年化收益)
    public double dReturnSeries;    //日收益(活期：万份收益)

    public DateModel() {
    }

    public DateModel(double value, long date) {
        this.dValueSeries = value;
        this.dtDateSeries = date;
    }
}
