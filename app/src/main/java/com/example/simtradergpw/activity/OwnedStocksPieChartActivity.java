package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.simtradergpw.ChartDataStock;
import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OwnedStocksPieChartActivity extends AppCompatActivity {
    int[] PIE_CHART_COLORS = {
            Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134), Color.rgb(179, 48, 80), Color.rgb(72, 181, 163),
            Color.rgb(133, 202, 93),Color.rgb(193, 179, 193),
    };

    private PieChart ownedStocksPieChart;
    private ArrayList<ChartDataStock> ownedStocksChartData = new ArrayList<>();
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_stocks_pie_chart);

        // Get user info from sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        connectVariablesToGui();
        getDataFromDb();
        drawOwnedStocksPieChart();
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


                ChartDataStock ownedStock = new ChartDataStock(ticker, ownedStockValue);
                ownedStocksChartData.add(ownedStock);
            }



        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

    private void drawOwnedStocksPieChart(){
        // Add data
        ArrayList<PieEntry> chartValues = new ArrayList<>();

        for (int i = 0; i < ownedStocksChartData.size(); i++) {
            String ticker = ownedStocksChartData.get(i).getTicker();
            Float value = ownedStocksChartData.get(i).getPrice().floatValue();
            chartValues.add(new PieEntry(value, ticker));
        }

        PieDataSet pieDataSet = new PieDataSet(chartValues, "");
        ColorTemplate ct = new ColorTemplate();
        pieDataSet.setColors(PIE_CHART_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        ownedStocksPieChart.setData(pieData);
        ownedStocksPieChart.getDescription().setEnabled(false);
        ownedStocksPieChart.animateXY(500, 700);
    }

    private void connectVariablesToGui() {
        ownedStocksPieChart = findViewById(R.id.act_ownedStocks_pieChart);
    }
}