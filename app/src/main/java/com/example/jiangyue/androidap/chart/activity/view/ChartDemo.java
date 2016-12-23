/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jiangyue.androidap.chart.activity.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.jiangyue.androidap.chart.activity.AverageCubicTemperatureChart;
import com.example.jiangyue.androidap.chart.activity.AverageTemperatureChart;
import com.example.jiangyue.androidap.chart.activity.BudgetDoughnutChart;
import com.example.jiangyue.androidap.chart.activity.BudgetPieChart;
import com.example.jiangyue.androidap.chart.activity.CombinedTemperatureChart;
import com.example.jiangyue.androidap.chart.activity.IDemoChart;
import com.example.jiangyue.androidap.chart.activity.MultipleTemperatureChart;
import com.example.jiangyue.androidap.chart.activity.PieChartBuilder;
import com.example.jiangyue.androidap.chart.activity.ProjectStatusBubbleChart;
import com.example.jiangyue.androidap.chart.activity.ProjectStatusChart;
import com.example.jiangyue.androidap.chart.activity.SalesBarChart;
import com.example.jiangyue.androidap.chart.activity.SalesComparisonChart;
import com.example.jiangyue.androidap.chart.activity.SalesGrowthChart;
import com.example.jiangyue.androidap.chart.activity.SalesStackedBarChart;
import com.example.jiangyue.androidap.chart.activity.ScatterChart;
import com.example.jiangyue.androidap.chart.activity.SensorValuesChart;
import com.example.jiangyue.androidap.chart.activity.TemperatureChart;
import com.example.jiangyue.androidap.chart.activity.TrigonometricFunctionsChart;
import com.example.jiangyue.androidap.chart.activity.WeightDialChart;
import com.example.jiangyue.androidap.chart.activity.XYChartBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartDemo extends ListActivity {
  private IDemoChart[] mCharts = new IDemoChart[] { new AverageTemperatureChart(),
      new AverageCubicTemperatureChart(), new SalesStackedBarChart(), new SalesBarChart(),
      new TrigonometricFunctionsChart(), new ScatterChart(), new SalesComparisonChart(),
      new ProjectStatusChart(), new SalesGrowthChart(), new BudgetPieChart(),
      new BudgetDoughnutChart(), new ProjectStatusBubbleChart(), new TemperatureChart(),
      new WeightDialChart(), new SensorValuesChart(), new CombinedTemperatureChart(),
      new MultipleTemperatureChart() };

  private String[] mMenuText;

  private String[] mMenuSummary;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int length = mCharts.length;
    mMenuText = new String[length + 3];
    mMenuSummary = new String[length + 3];
    mMenuText[0] = "Embedded line chart demo";
    mMenuSummary[0] = "A demo on how to include a clickable line chart into a graphical activity";
    mMenuText[1] = "Embedded pie chart demo";
    mMenuSummary[1] = "A demo on how to include a clickable pie chart into a graphical activity";
    for (int i = 0; i < length; i++) {
      mMenuText[i + 2] = mCharts[i].getName();
      mMenuSummary[i + 2] = mCharts[i].getDesc();
    }
    mMenuText[length + 2] = "Random values charts";
    mMenuSummary[length + 2] = "Chart demos using randomly generated values";
    setListAdapter(new SimpleAdapter(this, getListValues(), android.R.layout.simple_list_item_2,
        new String[] { IDemoChart.NAME, IDemoChart.DESC }, new int[] { android.R.id.text1,
            android.R.id.text2 }));
  }

  private List<Map<String, String>> getListValues() {
    List<Map<String, String>> values = new ArrayList<Map<String, String>>();
    int length = mMenuText.length;
    for (int i = 0; i < length; i++) {
      Map<String, String> v = new HashMap<String, String>();
      v.put(IDemoChart.NAME, mMenuText[i]);
      v.put(IDemoChart.DESC, mMenuSummary[i]);
      values.add(v);
    }
    return values;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Intent intent = null;
    if (position == 0) {
      intent = new Intent(this, XYChartBuilder.class);
    } else if (position == 1) {
      intent = new Intent(this, PieChartBuilder.class);
    } else if (position <= mCharts.length + 1) {
      intent = mCharts[position - 2].execute(this);
    } else {
      intent = new Intent(this, GeneratedChartDemo.class);
    }
    startActivity(intent);
  }
}