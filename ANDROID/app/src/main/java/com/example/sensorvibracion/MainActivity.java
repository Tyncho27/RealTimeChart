package com.example.sensorvibracion;

import android.os.AsyncTask;
import android.service.autofill.DateValueSanitizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class MainActivity extends AppCompatActivity {
    private String sdatos;
    private static final String TAG = "MainActivity";
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart = (LineChart) findViewById(R.id.GRAFICO);
        //set description
        Description des = mChart.getDescription();
        des.setEnabled(true);
        des.setText("MARTIN RUBIO - Superior Energy Services");
        des.setTextSize(10f);
        des.setTextColor(Color.WHITE);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);
        mChart.setGridBackgroundColor(Color.BLACK);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(Color.WHITE);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter());



        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(40f);
        leftAxis.setAxisMinimum(-40f);
        //leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMaximum(40f);
        rightAxis.setAxisMinimum(-40f);
        rightAxis.setEnabled(true);

        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisRight().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(true);



        /*TimerTask get= new TimerTask() {
            @Override
            public void run() {
                GetHoraServidor horaservidor = new GetHoraServidor();
                horaservidor.execute();

            }};
        Timer timerget = new Timer();
        timerget.scheduleAtFixedRate(get, 0,1);*/

        feedMultiple();
    }


    private void addEntry(float medicion) {
        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), medicion), 0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();// let the chart know it's data has changed
            mChart.setVisibleXRangeMaximum(100);// limit the number of visible entries
            mChart.moveViewToX(data.getEntryCount());// move to the latest entry
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Detección de Disparo");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.RED);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    public class GetDatos extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                Socket cliente = new Socket("192.168.4.1", 80);
                PrintWriter salida = new PrintWriter(cliente.getOutputStream());
                salida.write("g-");
                salida.flush();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                String mensaje = entrada.readLine();
                cliente.close();
                publishProgress(mensaje);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //datos.setText(values[0]);
            sdatos = values[0];
            float medicion = Float.parseFloat(sdatos);
            addEntry(medicion);
            //plotData = true;


        }
    }

   private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                GetDatos horaservidor = new GetDatos();
                horaservidor.execute();

                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

   
}
