package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simtradergpw.ChartData;
import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.FormatHelper;
import com.example.simtradergpw.R;
import com.example.simtradergpw.StockRecord;
import com.example.simtradergpw.TransactionsRecyclerViewAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SingleCompanyTransactionsActivity extends AppCompatActivity {
    private ArrayList<StockRecord> uTransactionHistory = new ArrayList<>();
    ArrayList<ChartData> stockPriceHistoryList = new ArrayList<>();
    private RecyclerView transactionHistoryRecyclerView;
    private LineChart mLineChart;
    private TextView investedTv, balanceTv, ownedQuantityTv, priceTv;
    private String cTicker;
    private int userId;
    private Double price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_company_transactions_history);
        connectVariablesToGui();

        // Get user ID
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        // Get ticker from extras
        if (getIntent().hasExtra("ticker")) cTicker = getIntent().getStringExtra("ticker");

        getDataFromDb();
        drawChart();
        setTextViews();
        showOwnedStocks();
    }

    private void getDataFromDb() {
        Statement statement = null;


        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Get list of transactions
            String sql = "SELECT cp_name, cp_ticker, ut_quantity, ut_price_per_stock, ut_is_buy, ut_timestamp FROM us_transactions_h " +
                    "INNER JOIN cp__company ON ut_cpid = cp_id WHERE ut_usid = " + userId + " AND cp_ticker = '"+cTicker+"' ORDER BY ut_timestamp DESC";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String name = resultSet.getString("cp_name");
                String ticker = resultSet.getString("cp_ticker");
                Integer ownedQuantity = resultSet.getInt("ut_quantity");
                Boolean isBuy = resultSet.getBoolean("ut_is_buy");
                String timeStamp = resultSet.getString("ut_timestamp");
                Double last = resultSet.getDouble("ut_price_per_stock");

                StockRecord record = new StockRecord(name, ticker, last, null, ownedQuantity, timeStamp, isBuy);
                uTransactionHistory.add(record);
            }

            // Get company historic values from last 30 days
            sql = "SELECT * FROM cp_history WHERE ch_ticker = '"+ cTicker +"' AND ch_timestamp > " +
                    "dateadd(day, -30, getdate())  ORDER BY ch_timestamp ASC";
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Double price = resultSet.getDouble("ch_price");
                String timeStamp = resultSet.getString("ch_timestamp");

                ChartData record = new ChartData(price, timeStamp);
                stockPriceHistoryList.add(record);
            }

            // Get owned quantity and last
            sql = "SELECT uw_quantity, cp_last FROM us__users INNER JOIN us_wallet ON uw_usid = us_id " +
                   "INNER JOIN cp__company ON cp_id = uw_cpid WHERE us_id = "+userId+" AND cp_ticker = '"+cTicker+"'";
            resultSet = statement.executeQuery(sql);

            Integer ownedQuantity;

            if (resultSet.next()) {
                ownedQuantity = resultSet.getInt("uw_quantity");
                price = resultSet.getDouble("cp_last");

                ownedQuantityTv.setText(ownedQuantity.toString());
                priceTv.setText(price.toString());
            }

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

    private void showOwnedStocks() {
        if (uTransactionHistory.size() > 0) {
            TransactionsRecyclerViewAdapter myAdapter = new TransactionsRecyclerViewAdapter(this, uTransactionHistory);
            transactionHistoryRecyclerView.setAdapter(myAdapter);
            transactionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    /* ######### Draw chart ######### */
    private void drawChart(){
//        mLineChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);

        // Set chart description
        Description description = new Description();
        description.setText(" ");
        mLineChart.setDescription(description);

        // Hide labels on right Y axis
        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawLabels(false);

        // Display date on X axis
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDate()));


////        for (int i = 0; i < uTransactionHistory.size(); i++) {
//            Highlight h1 = new Highlight(5f,5f,5);
//            Highlight h2 = new Highlight(1f,1f,1);
////        }
//
//        mLineChart.highlightValues(new Highlight[] {h1, h2});


        // Add data
        ArrayList<Entry> chartValues = new ArrayList<>();

        for (int i = 0; i < stockPriceHistoryList.size(); i++) {
            chartValues.add(new Entry(i, stockPriceHistoryList.get(i).getPrice().floatValue()));
        }

        LineDataSet set1 = new LineDataSet(chartValues, "Wartość akcji");

        set1.setFillAlpha(110);
        set1.setColor(ContextCompat.getColor(this, R.color.colorBlue));
        set1.setDrawCircles(false);

        set1.setHighlightEnabled(true);
        set1.setDrawHighlightIndicators(true);



        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mLineChart.setData(data);
    }

    // Function that is used to format X axis on the chart
    private ArrayList<String> getDate() {
        SimpleDateFormat formatFromDatabase = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM");


        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i < stockPriceHistoryList.size(); i++) {
            try {
                String date = stockPriceHistoryList.get(i).getDate();
                date = myFormat.format(formatFromDatabase.parse(date));

                label.add(date);
            } catch (ParseException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        return label;
    }

    private void setTextViews() {
        Double invested = 0.0;
        Double sold = 0.0;
        Double balance;

        for(int i = 0; i < uTransactionHistory.size(); i++) {
            if (uTransactionHistory.get(i).getIsBuy()) {
                invested += uTransactionHistory.get(i).getLast() * uTransactionHistory.get(i).getOwnedQuantity();
            } else {
                sold += uTransactionHistory.get(i).getLast() * uTransactionHistory.get(i).getOwnedQuantity();
            }
        }

        balance = sold - invested;

        investedTv.setText(FormatHelper.cutAfterDot(invested));
        balanceTv.setText(FormatHelper.cutAfterDot(balance));


    }

    private void connectVariablesToGui() {
        ownedQuantityTv = findViewById(R.id.act_SCTH_owned_quantity_tv);
        priceTv = findViewById(R.id.act_SCTH_last_tv);
        investedTv = findViewById(R.id.act_SCTH_invested_tv);
        balanceTv = findViewById(R.id.act_SCTH_balance_tv);
        mLineChart = findViewById(R.id.act_SCTH_value_linechart);
        transactionHistoryRecyclerView = findViewById(R.id.act_SCTH_recyclerview);
    }
}