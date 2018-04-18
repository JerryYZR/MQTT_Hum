package com.example.mqtt_test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SuscribeActivity extends Activity {

    int constNum = 100;
    int XLen = 10;
    private Timer timer = new Timer();
    private GraphicalView chart, chart1;
    private TimerTask task;


    private int addY = -1;
    private int addY1 = -1;
    private long addX;
    private TimeSeries series, series1;
    private XYMultipleSeriesDataset dataset, dataset1;

    private Handler handler;
    private Random random=new Random();

    Date[] xcache = new Date[constNum];
    int[] ycache = new int[constNum];
    Date[] xcache1 = new Date[constNum];
    int[] ycache1 = new int[constNum];


    private Myapplication myapplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suscribe);
        LinearLayout layout1 = (LinearLayout)findViewById(R.id.linearlayout1);
        //生成图表Hum
        chart = ChartFactory.getTimeChartView(this, getDateDemoDataset("Humidity"), getDemoRenderer("Humidity", "Humidity"), "mm:ss");
        layout1.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,1000));

        LinearLayout layout2 = (LinearLayout)findViewById(R.id.linearlayout2);
        //生成图表Tem
        chart1 = ChartFactory.getTimeChartView(this, getDateDemoDataset1("Temperature"), getDemoRenderer("Temperature", "Temperature"), "mm:ss");
        layout2.addView(chart1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,1000));


        //进入Main界面
        ImageButton tool = (ImageButton) findViewById(R.id.menu_1);
        tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(SuscribeActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        //一步处理传来的消息，更新图表
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //刷新图表
                updateChart();
                super.handleMessage(msg);
            }
        };
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
        };
        //第二个参数是表示是否有延迟，第三个参数是表示经过多久以后进行下一次run
        //4秒更新一次
        timer.schedule(task, 1000,4*1000);


    }

    //更新Hum和Tem的数据
    private void updateChart() {

        myapplication = (Myapplication) getApplication();



        //设定长度为20
        int length = series.getItemCount();
        addY=(int)myapplication.getHum();
        addY1=(int)myapplication.getTem();
        addX=new Date().getTime();

        if(length < XLen){
            series.add(new Date(addX), addY);
            series1.add(new Date(addX), addY1);
        }else if(length == XLen){
            //将前面的点放入缓存
            for (int i = 1; i < length; i++) {
                xcache[i-1] =  new Date((long)series.getX(i));
                ycache[i-1] = (int) series.getY(i);
                xcache1[i-1] =  new Date((long)series1.getX(i));
                ycache1[i-1] = (int) series1.getY(i);
            }

            series.clear();
            series1.clear();
            //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中

            for (int k = 0; k < length-1; k++) {
                series.add(xcache[k], ycache[k]);
                series1.add(xcache1[k], ycache1[k]);
            }
            series.add(new Date(addX), addY);
            series1.add(new Date(addX), addY1);
        }



        //在数据集中添加新的点集
        dataset.removeSeries(series);
        dataset.addSeries(series);
        //曲线更新
        chart.invalidate();

        //在数据集中添加新的点集
        dataset1.removeSeries(series1);
        dataset1.addSeries(series1);
        //曲线更新
        chart1.invalidate();
    }

    //初始化render
    private XYMultipleSeriesRenderer getDemoRenderer(String ChartTitle, String YTitle) {
        int[] colors = new int[] { Color.BLUE, Color.GREEN };

        //图表样式枚举器,这个类作用是：在趋势图中各个点的样式，有圆形，三角形，正方形，菱形等
        PointStyle[] styles = new PointStyle[] {PointStyle.TRIANGLE, PointStyle.SQUARE };

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle(ChartTitle);//标题
        renderer.setXTitle("Time");    //x轴说明
        renderer.setYTitle(YTitle);

        renderer.setAxisTitleTextSize(30);
        renderer.setChartTitleTextSize(60);
        renderer.setLabelsTextSize(25);
        renderer.setLegendTextSize(50);
        renderer.setPointSize(10f);

        renderer.setAxesColor(Color.LTGRAY);
        renderer.setLabelsColor(Color.LTGRAY);

        renderer.setMargins(new int[] { 110, 60, 100, 60 });//上左下右{ 20, 30, 100, 0 })

        int length = renderer.getSeriesRendererCount();

        for (int i = 0; i < length; i++) {
            //在multiple renderer中得到指定位置的 renderer.
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
                    .setFillPoints(true);
        }

        //是否显示网格
        renderer.setShowGrid(true);
        //x轴，刻度线与刻度标注之间的相对位置关系
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        //y轴，刻度线与刻度标注之间的相对位置关系
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        //是否显示放大缩小按钮
        renderer.setZoomButtonsVisible(true);
        //设置拖动时X轴Y轴允许的最大值最小值.
        renderer.setPanLimits(new double[] { -10, 20, -10, 80 });
        //设置放大缩小时X轴Y轴允许的最大最小值.
        renderer.setZoomLimits(new double[] { -10, 20, -10, 80 });

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.GREEN);
        r.setPointStyle(PointStyle.TRIANGLE);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);

        renderer.setMarginsColor(Color.BLACK);
        //renderer.setPanEnabled(false,false);
        renderer.setShowGrid(true);
        renderer.setYAxisMax(120);//纵坐标最大值
        renderer.setYAxisMin(-10);//纵坐标最小值
        renderer.setInScroll(true);

        return renderer;
    }

    //初始化Hum数据集
    private XYMultipleSeriesDataset getDateDemoDataset(String title) {//初始化的数据
        dataset = new XYMultipleSeriesDataset();
        final int nr = 1;
        long value = new Date().getTime();
        Random r = new Random();
        series = new TimeSeries("Demo series " +  1);
        series.setTitle(title);
        for (int k = 0; k < nr; k++) {
            series.add(new Date(value+k*1000), 20 +r.nextInt() % 10);//初值Y轴以20为中心，X轴初值范围再次定义
        }
        dataset.addSeries(series);
        return dataset;
    }

    //初始化Tem数据集
    private XYMultipleSeriesDataset getDateDemoDataset1(String title) {//初始化的数据
        dataset1 = new XYMultipleSeriesDataset();
        final int nr = 1;
        long value = new Date().getTime();
        Random r = new Random();
        series1 = new TimeSeries("Demo series " +  1);
        series1.setTitle(title);
        for (int k = 0; k < nr; k++) {
            series1.add(new Date(value+k*1000), 20 +r.nextInt() % 10);//初值Y轴以20为中心，X轴初值范围再次定义
        }
        dataset1.addSeries(series1);
        return dataset1;
    }

    @Override
    public void onDestroy() {
        //当结束程序时关掉Timer
        timer.cancel();
        super.onDestroy();
    };

}
