package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.simtradergpw.ChartData;
import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private BarChart walletValueBarChart;
    private ArrayList<ChartData> ownedStocksChartData = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        connectVariablesToGui();

        // Get user info from sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        getDataFromDb();
        drawWalletValueBarChart();
    }

    private void getDataFromDb() {
        Statement statement = null;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Check how many stocks of this company user owns
            String sqlOwnedStocks = "SELECT cp_ticker, cp_last, uw_quantity FROM us_wallet INNER JOIN cp__company ON uw_cpid = cp_id WHERE uw_usid = " + userId
                    + " AND uw_quantity > 0";
            ResultSet resultSet = statement.executeQuery(sqlOwnedStocks);

            while (resultSet.next()) {
                    // Data from database
                    String ticker = resultSet.getString("cp_ticker");
                    Integer ownedQuantity = resultSet.getInt("uw_quantity");
                    Double currentPrice = resultSet.getDouble("cp_last");
                    Double ownedStockValue = currentPrice * ownedQuantity;


                    ChartData ownedStock = new ChartData(ticker, ownedStockValue);
                    ownedStocksChartData.add(ownedStock);
            }



        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

//    private void drawWig20Chart(){
//        wig20LineChart.setDragEnabled(false);
//        wig20LineChart.setScaleEnabled(false);
//
//        // Add data
//        ArrayList<Entry> chartValues = new ArrayList<>();
//
//        for (int i = 0; i < 20; i++) {
//            chartValues.add(new Entry(i, i));
//        }
//
//        LineDataSet set1 = new LineDataSet(chartValues, "Wartość akcji");
//
//        set1.setFillAlpha(110);
//        set1.setColor(ContextCompat.getColor(this, R.color.colorBlue));
//        set1.setDrawCircles(true);
//        set1.setDrawValues(false);
//
//
//        set1.setHighlightEnabled(true);
//        set1.setDrawHighlightIndicators(true);
//
//
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//
//        LineData data = new LineData(dataSets);
//
//        wig20LineChart.setData(data);
//    }

    private void drawWalletValueBarChart(){
        ArrayList<BarEntry> months = new ArrayList<>();

        // add data
        months.add(new BarEntry(1, 10000));
        months.add(new BarEntry(2, 8632));
        months.add(new BarEntry(3, 11233));
        months.add(new BarEntry(4, 16399));
        months.add(new BarEntry(5, 20300));
        months.add(new BarEntry(6, 19400));
        months.add(new BarEntry(7, 17999));
        months.add(new BarEntry(8, 22126));
        months.add(new BarEntry(9, 26444));
        months.add(new BarEntry(10, 18399));
        months.add(new BarEntry(11, 19033));
        months.add(new BarEntry(12, 20500));

        BarDataSet barDataSet = new BarDataSet(months, "");
        barDataSet.setColors(getColor(R.color.colorBlue));

        BarData barData = new BarData(barDataSet);

        walletValueBarChart.setFitBars(true);
        walletValueBarChart.setData(barData);
        walletValueBarChart.getDescription().setText("");
        walletValueBarChart.animateY(1000);
    }


    private void connectVariablesToGui() {
        walletValueBarChart = findViewById(R.id.act_stats_walletValue_montly_barChart);
    }
}