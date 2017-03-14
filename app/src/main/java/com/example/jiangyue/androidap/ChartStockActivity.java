package com.example.jiangyue.androidap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.jiangyue.androidap.chart.ChartFactory;
import com.example.jiangyue.androidap.chart.GraphicalView;
import com.example.jiangyue.androidap.chart.activity.view.ChartDemo;
import com.example.jiangyue.androidap.chart.chart.PointStyle;
import com.example.jiangyue.androidap.chart.chart.StockBarChart;
import com.example.jiangyue.androidap.chart.chart.StockChart;
import com.example.jiangyue.androidap.chart.chart.StockLinearChart;
import com.example.jiangyue.androidap.chart.chart.StockRangeBarChart;
import com.example.jiangyue.androidap.chart.model.RangeKLineSeries;
import com.example.jiangyue.androidap.chart.model.XYMultipleSeriesDataset;
import com.example.jiangyue.androidap.chart.model.XYSeries;
import com.example.jiangyue.androidap.chart.renderer.XYMultipleSeriesRenderer;
import com.example.jiangyue.androidap.chart.renderer.XYSeriesRenderer;
import com.example.jiangyue.androidap.test.DataParse;
import com.example.jiangyue.androidap.test.KLineBean;
import com.example.jiangyue.androidap.test.MinutesBean;
import com.example.jiangyue.androidap.test.Util;
import com.example.jiangyue.androidap.util.DisplayUtil;
import com.example.jiangyue.androidap.views.textview.MultiTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jiangyue on 16/10/20.
 */
public class ChartStockActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        DisplayUtil.init(this);
        //初始化头
        initMultiText();
        //初始化五档
        initFive();
        //初始化股票数据
        showStock();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button && !(v instanceof RadioButton)) {
            if (v.getId() == R.id.btn_01) {
                Intent intent = new Intent(this, ChartDemo.class);
                startActivity(intent);
            } else if (v.getId() == R.id.btn_02) {
                //跳转横屏
                Intent intent = new Intent(this, ChartStockLandscapeActivity.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.rbtn_stock_00) {
            showStock();
            findViewById(R.id.llyt_five).setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.rbtn_stock_02) {
            showStockK();
            findViewById(R.id.llyt_five).setVisibility(View.GONE);
        }
    }

    private void initMultiText() {
        MultiTextView multiStock01 = (MultiTextView) findViewById(R.id.stock_01);
        multiStock01.addPiece(new MultiTextView.Piece.Builder("3081.04")
                .textColor(Color.parseColor("#1dc43e"))
                .textSize(DisplayUtil.dp2px(22))
                .build());
        multiStock01.display();

        MultiTextView multiStock02 = (MultiTextView) findViewById(R.id.stock_02);
        multiStock02.addPiece(new MultiTextView.Piece.Builder("开：")
                .textColor(Color.parseColor("#838080"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock02.addPiece(new MultiTextView.Piece.Builder("3084.91")
                .textColor(Color.parseColor("#e00c17"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock02.display();

        MultiTextView multiStock03 = (MultiTextView) findViewById(R.id.stock_03);
        multiStock03.addPiece(new MultiTextView.Piece.Builder("高：")
                .textColor(Color.parseColor("#838080"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock03.addPiece(new MultiTextView.Piece.Builder("3089.91")
                .textColor(Color.parseColor("#e00c17"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock03.display();

        MultiTextView multiStock04 = (MultiTextView) findViewById(R.id.stock_04);
        multiStock04.addPiece(new MultiTextView.Piece.Builder("底：")
                .textColor(Color.parseColor("#838080"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock04.addPiece(new MultiTextView.Piece.Builder("3089.91")
                .textColor(Color.parseColor("#1dc43e"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock04.display();

        //二挡
        MultiTextView multiStock11 = (MultiTextView) findViewById(R.id.stock_11);
        multiStock11.addPiece(new MultiTextView.Piece.Builder("-13.90" + "  " + "-0.90%")
                .textColor(Color.parseColor("#1dc43e"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock11.display();

        MultiTextView multiStock12 = (MultiTextView) findViewById(R.id.stock_12);
        multiStock12.addPiece(new MultiTextView.Piece.Builder("换：")
                .textColor(Color.parseColor("#838080"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock12.addPiece(new MultiTextView.Piece.Builder("3084.91")
                .textColor(Color.parseColor("#4d4d4d"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock12.display();

        MultiTextView multiStock13 = (MultiTextView) findViewById(R.id.stock_13);
        multiStock13.addPiece(new MultiTextView.Piece.Builder("量：")
                .textColor(Color.parseColor("#838080"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock13.addPiece(new MultiTextView.Piece.Builder("3089.91")
                .textColor(Color.parseColor("#4d4d4d"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock13.display();

        MultiTextView multiStock14 = (MultiTextView) findViewById(R.id.stock_14);
        multiStock14.addPiece(new MultiTextView.Piece.Builder("额：")
                .textColor(Color.parseColor("#838080"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock14.addPiece(new MultiTextView.Piece.Builder("3089.91")
                .textColor(Color.parseColor("#4d4d4d"))
                .textSize(DisplayUtil.dp2px(11))
                .build());
        multiStock14.display();

        //分时
        RadioButton rbtn00 = (RadioButton) findViewById(R.id.rbtn_stock_00);
        rbtn00.setChecked(true);
    }

    private void initFive() {
        ((RadioButton) findViewById(R.id.rbtn_five)).setChecked(true);

        MultiTextView mul01 = (MultiTextView) findViewById(R.id.five_11);
        mul01.addPiece(new MultiTextView.Piece.Builder("卖5  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul01.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#d74c44"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul01.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul01.display();

        MultiTextView mul02 = (MultiTextView) findViewById(R.id.five_12);
        mul02.addPiece(new MultiTextView.Piece.Builder("卖4  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul02.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#d74c44"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul02.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul02.display();

        MultiTextView mul03 = (MultiTextView) findViewById(R.id.five_13);
        mul03.addPiece(new MultiTextView.Piece.Builder("卖3  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul03.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#d74c44"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul03.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul03.display();

        MultiTextView mul04 = (MultiTextView) findViewById(R.id.five_14);
        mul04.addPiece(new MultiTextView.Piece.Builder("卖2  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul04.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#d74c44"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul04.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul04.display();

        MultiTextView mul05 = (MultiTextView) findViewById(R.id.five_15);
        mul05.addPiece(new MultiTextView.Piece.Builder("卖1 ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul05.addPiece(new MultiTextView.Piece.Builder("306.91 ")
                .textColor(Color.parseColor("#d74c44"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul05.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul05.display();

        //********************************************************
        MultiTextView mul11 = (MultiTextView) findViewById(R.id.five_21);
        mul11.addPiece(new MultiTextView.Piece.Builder("卖5  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul11.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#4bbb59"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul11.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul11.display();

        MultiTextView mul12 = (MultiTextView) findViewById(R.id.five_22);
        mul12.addPiece(new MultiTextView.Piece.Builder("卖4  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul12.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#4bbb59"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul12.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul12.display();

        MultiTextView mul13 = (MultiTextView) findViewById(R.id.five_23);
        mul13.addPiece(new MultiTextView.Piece.Builder("卖3  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul13.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#4bbb59"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul13.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul13.display();

        MultiTextView mul14 = (MultiTextView) findViewById(R.id.five_24);
        mul14.addPiece(new MultiTextView.Piece.Builder("卖2  ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul14.addPiece(new MultiTextView.Piece.Builder("306.91  ")
                .textColor(Color.parseColor("#4bbb59"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul14.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul14.display();

        MultiTextView mul15 = (MultiTextView) findViewById(R.id.five_25);
        mul15.addPiece(new MultiTextView.Piece.Builder("卖1 ")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul15.addPiece(new MultiTextView.Piece.Builder("306.91 ")
                .textColor(Color.parseColor("#4bbb59"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul15.addPiece(new MultiTextView.Piece.Builder("1889.91")
                .textColor(Color.parseColor("#666666"))
                .textSize(DisplayUtil.dp2px(9))
                .build());
        mul15.display();
    }

    private void showStock() {
        DataParse mData = new DataParse();
        JSONObject object = null;
        try {
            object = new JSONObject(Util.MINUTESURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mData.parseMinutes(object);
        ArrayList<MinutesBean> minuteLine = mData.getDatas();
        int minuteSize = 20;

        //数据处理
        XYSeries series01 = new XYSeries("Line", 0);//均线
        XYSeries series02 = new XYSeries("Line", 0);//时实走势
        XYSeries series03 = new XYSeries("RangeBar", 0);//成交量柱状图
        //极值
        double minValue = 1000000000;
        double maxValue = -1000000000;
        double minTradeValue = 1000000000;
        double maxTradeValue = -1000000000;
        //数据填装
        for (int i = 0, size = minuteSize; i < size; i++) {
            series01.add(i, minuteLine.get(i).cjprice);
            series02.add(i, minuteLine.get(i).avprice);
            //求最小值
            if (minValue > minuteLine.get(i).cjprice) {
                minValue = minuteLine.get(i).cjprice;
            }
            if (minValue > minuteLine.get(i).avprice) {
                minValue = minuteLine.get(i).avprice;
            }
            if (minTradeValue > minuteLine.get(i).cjnum) {
                minTradeValue = minuteLine.get(i).cjnum;
            }
            //求最大值
            if (maxValue < minuteLine.get(i).cjprice) {
                maxValue = minuteLine.get(i).cjprice;
            }
            if (maxValue < minuteLine.get(i).avprice) {
                maxValue = minuteLine.get(i).avprice;
            }
            if (maxTradeValue < minuteLine.get(i).cjnum) {
                maxTradeValue = minuteLine.get(i).cjnum;
            }
        }
        //获取成交量基数，重新设置极小值，留涨幅空隙
        maxValue = maxValue + (maxValue - minValue) * 0.2;
        minValue = minValue - (maxValue - minValue) * 0.2;
        double baseTradeValue = 1 / (maxTradeValue - minTradeValue) * (maxValue - minValue) * (0.4 - 0.06);
        minValue = minValue - (maxValue - minValue) * 0.4;
        //设置成交量柱状图数据
        for (int i = 0, size = minuteSize; i < size; i++) {
            series03.add(i, minValue + (minuteLine.get(i).cjnum - minTradeValue) * baseTradeValue);
        }
        //************************************************************
        //设置颜色其他熟悉下
        int[] colors = new int[]{Color.rgb(183, 147, 100), Color.rgb(124, 153, 184), Color.rgb(184, 184, 184)};
        PointStyle[] styles = new PointStyle[]{PointStyle.POINT, PointStyle.POINT, PointStyle.POINT};
        XYMultipleSeriesRenderer renderer = ChartFactory.buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
            r.setFillPoints(true);
            r.setLineWidth(3);
        }
        ChartFactory.setChartSettings(renderer, "", "", "", 0 - 0.5, minuteSize - 1 + 0.5, minValue, maxValue,
                Color.GRAY, Color.LTGRAY);

        renderer.setMargins(new int[]{DisplayUtil.dp2px(15),
                DisplayUtil.dp2px(5), DisplayUtil.dp2px(5), DisplayUtil.dp2px(5)});
        renderer.setLabelsTextSize(DisplayUtil.sp2px(10));
        renderer.setBackgroundColor(0x00000000);
        renderer.setApplyBackgroundColor(true);
        renderer.setPanEnabled(false);
        renderer.setZoomButtonsVisible(false);
        renderer.setZoomEnabled(false, false);
        renderer.setShowLegend(false);
        renderer.setInScroll(true);
        renderer.setBarSpacing(0.2);
        renderer.setYLabelsColor(0, 0xfff44a28);
        renderer.setYLabelsAlign(Paint.Align.LEFT);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series01);
        dataset.addSeries(series02);
        dataset.addSeries(series03);

        StockChart.StockCombinedChartDef[] types = new StockChart.StockCombinedChartDef[]{
                new StockChart.StockCombinedChartDef(StockLinearChart.TYPE, 0),
                new StockChart.StockCombinedChartDef(StockLinearChart.TYPE, 1),
                new StockChart.StockCombinedChartDef(StockBarChart.TYPE, 2)};
        StockChart chart = new StockChart(dataset, renderer, types);
        //成交量虚线与成交量与实时时价实线
        chart.dividerLineVal01 = minValue + (maxTradeValue - minTradeValue) * baseTradeValue;
        chart.dividerLineVal02 = minValue + (maxTradeValue - minTradeValue) * baseTradeValue * (0.4 / 0.34);
        ((GraphicalView) findViewById(R.id.id_chart_graphv)).setChart(chart);
        (findViewById(R.id.id_chart_graphv)).invalidate();
    }

    private void showStockK() {
        DataParse mData = new DataParse();
        JSONObject object = null;
        try {
            object = new JSONObject(Util.KLINEURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mData.parseKLine(object);
        ArrayList<KLineBean> kLine = mData.getKLineDatas();
        int kLineSize = 60;

        //设置k线图
        XYSeries series00 = new XYSeries("Line", 0);//均线
        RangeKLineSeries series01 = new RangeKLineSeries("RangeKLine");//水蜡烛
        XYSeries series02 = new XYSeries("RangeBar", 0);//成交量柱状图
        XYSeries series03 = new XYSeries("Line", 0);//成交量时实走势
        //设置极值
        double minValue = 1000000000;
        double maxValue = -1000000000;
        double minTradeValue = 1000000000;
        double maxTradeValue = -1000000000;
        //设置值
        for (int i = 0, size = kLineSize; i < size; i++) {
            series00.add(i, kLine.get(i).open);
            series01.add(kLine.get(i));
            //求最小值
            if (minValue > kLine.get(i).open) {
                minValue = kLine.get(i).open;
            }
            if (minValue > kLine.get(i).close) {
                minValue = kLine.get(i).close;
            }
            if (minValue > kLine.get(i).low) {
                minValue = kLine.get(i).low;
            }
            if (minValue > kLine.get(i).high) {
                minValue = kLine.get(i).high;
            }
            if (minTradeValue > kLine.get(i).vol) {
                minTradeValue = kLine.get(i).vol;
            }
            //求最大值
            if (maxValue < kLine.get(i).open) {
                maxValue = kLine.get(i).open;
            }
            if (maxValue < kLine.get(i).close) {
                maxValue = kLine.get(i).close;
            }
            if (maxValue < kLine.get(i).low) {
                maxValue = kLine.get(i).low;
            }
            if (maxValue < kLine.get(i).high) {
                maxValue = kLine.get(i).high;
            }
            if (maxTradeValue < kLine.get(i).vol) {
                maxTradeValue = kLine.get(i).vol;
            }
        }
        //获取成交量基数，重新设置极小值，留涨幅空隙
        maxValue = maxValue + (maxValue - minValue) * 0.2;
        minValue = minValue - (maxValue - minValue) * 0.2;
        double baseTradeValue = 1 / (maxTradeValue - minTradeValue) * (maxValue - minValue) * (0.4 - 0.06);
        minValue = minValue - (maxValue - minValue) * 0.4;
        //设置成交量柱状图数据
        for (int i = 0, size = kLineSize; i < size; i++) {
            series02.add(i, minValue + (kLine.get(i).vol - minTradeValue) * baseTradeValue);
            series03.add(i, minValue + (kLine.get(i).vol - minTradeValue) * baseTradeValue);
        }

        //设置颜色其他熟悉下
        int[] colors = new int[]{Color.rgb(124, 153, 184), Color.rgb(183, 147, 100), Color.rgb(183, 147, 100), Color.rgb(183, 147, 100)};
        PointStyle[] styles = new PointStyle[]{PointStyle.POINT, PointStyle.POINT, PointStyle.POINT, PointStyle.POINT};
        XYMultipleSeriesRenderer renderer = ChartFactory.buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
            r.setFillPoints(true);
            r.setLineWidth(3);
        }
        ChartFactory.setChartSettings(renderer, "", "", "", 0 - 0.5, kLineSize - 1 + 0.5, minValue, maxValue,
                Color.GRAY, Color.LTGRAY);

        renderer.setMargins(new int[]{DisplayUtil.dp2px(15),
                DisplayUtil.dp2px(5), DisplayUtil.dp2px(5), DisplayUtil.dp2px(5)});
        renderer.setLabelsTextSize(DisplayUtil.sp2px(10));
        renderer.setBackgroundColor(0x00000000);
        renderer.setApplyBackgroundColor(true);
        renderer.setmZoomStartPosX(0);
        renderer.setmZoomEndPosX(kLineSize);
        renderer.setZoomInLimitX(kLineSize / 2);
        renderer.setZoomInLimitY(maxValue / 2);
        renderer.setmZoomInMaxX(kLineSize);
        renderer.setmZoomInMaxY(maxValue);
        renderer.setZoomInLimitY(maxValue / 2);
        renderer.setPanLimits(new double[]{0, kLineSize, minValue, maxValue});
        renderer.setPanEnabled(true, false);//移动
        renderer.setZoomEnabled(true, false);//缩放
        renderer.setZoomButtonsVisible(false);
        renderer.setShowLegend(false);
        renderer.setInScroll(true);
        renderer.setBarSpacing(0.5);
        renderer.setYLabelsColor(0, 0xfff44a28);
        renderer.setYLabelsAlign(Paint.Align.LEFT);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series01.toXYSeries());
        dataset.addSeries(series00);
        dataset.addSeries(series02);
        dataset.addSeries(series03);

        StockChart.StockCombinedChartDef[] types = new StockChart.StockCombinedChartDef[]{
                new StockChart.StockCombinedChartDef(StockRangeBarChart.TYPE, 0),
                new StockChart.StockCombinedChartDef(StockLinearChart.TYPE, 1),
                new StockChart.StockCombinedChartDef(StockBarChart.TYPE, 2),
                new StockChart.StockCombinedChartDef(StockLinearChart.TYPE, 0.3f, 3)};
        StockChart chart = new StockChart(dataset, renderer, types);
        //成交量虚线与成交量与实时时价实线
//        chart.setShowTradeBar(false);
        chart.dividerLineVal01 = minValue + (maxTradeValue - minTradeValue) * baseTradeValue;
        chart.dividerLineVal02 = minValue + (maxTradeValue - minTradeValue) * baseTradeValue * (0.4 / 0.34);
        ((GraphicalView) findViewById(R.id.id_chart_graphv)).setChart(chart);
        (findViewById(R.id.id_chart_graphv)).invalidate();
    }
}
