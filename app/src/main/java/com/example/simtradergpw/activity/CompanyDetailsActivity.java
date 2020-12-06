package com.example.simtradergpw.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.simtradergpw.ChartData;
import com.example.simtradergpw.DatabaseCommunication;
import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CompanyDetailsActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private TextView cNameTv, cTickerTv, cLastTv, cPChangeTv;
    private TextView ownedNumTv, estimatedNumTv, priceConverterTv;
    private EditText quantityEt;
    private View changeSymbolView;
    private String cName, cTicker, cPChange;
    private Double cLast, pChangeValue;
    private Integer userId, companyId, ownedQuantity;
    private LineChart mLineChart;

    ArrayList<ChartData> stockPriceHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
        connectVariablesToGui();

        getData();

        // Get user ID
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        // Get company ID
        Statement statement = null;
        try {
            statement = DatabaseConnection.getConnection().createStatement();
            String sqlCompanyId = "SELECT cp_id FROM cp__company WHERE cp_ticker ='" + cTicker + "'";
            ResultSet resultCompanyId = statement.executeQuery(sqlCompanyId);

            if (resultCompanyId.next()) companyId = resultCompanyId.getInt("cp_id");

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }


        setData();
        getFromDb();
        drawChart();
    }

    /* ######### Database communication ######### */
    public void buyStockBtn(View view) {
        Integer quantity = null;

        try {
            quantity = Integer.parseInt(quantityEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        DatabaseCommunication.buyStock(this, userId, companyId, quantity, cLast);

        Toast.makeText(this, "Kupiono akcje!", Toast.LENGTH_SHORT).show();
        quantityEt.setText("");
        getFromDb();
    }

    public void sellStockBtn(View view) {
        Integer quantity = null;

        try {
            quantity = Integer.parseInt(quantityEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        DatabaseCommunication.sellStock(this, userId, companyId, quantity, cLast);

        Toast.makeText(this, "Sprzedano akcje!", Toast.LENGTH_SHORT).show();
        quantityEt.setText("");
        getFromDb();
    }

    /* ######### Get data from passed extras ######### */
    private void getData() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("ticker") &&
                getIntent().hasExtra("last") && getIntent().hasExtra("pChange")) {

            cName = getIntent().getStringExtra("name");
            cTicker = getIntent().getStringExtra("ticker");
            cPChange = getIntent().getStringExtra("pChange");

            String cLastBufor = getIntent().getStringExtra("last");
            cLastBufor = cLastBufor.replace(",", ".");
            cLast = Double.parseDouble(cLastBufor);
        } else {
            Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT);
        }

        String pChangeBufor = cPChange;
        pChangeBufor = pChangeBufor.replace("%", "");
        pChangeBufor = pChangeBufor.replace(",", ".");
        try {
            pChangeValue = Double.parseDouble(pChangeBufor);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /* ######### Set data in layout from passed extras ######### */
    private void setData() {
        cNameTv.setText(cName);
        cTickerTv.setText(cTicker);
        cLastTv.setText(Double.toString(cLast));
        cPChangeTv.setText(cPChange);

        /* Set adequate text color and symbol depending of change value */
        if (pChangeValue == 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_no_change_grey_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorText));
        } else if (pChangeValue > 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_drop_up_green_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingUp));
        } else if (pChangeValue < 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_drop_down_red_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingDown));
        }

        priceConverterTv.setText("0 x " + Double.toString(cLast) + " = 0");
    }

    /* ######### Get data from database and set it on layout ######### */
    private void getFromDb() {
        Statement statement = null;
        ResultSet resultSet;
        String sql;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Check how many stocks of this company user owns
            sql = "SELECT SUM(uw_quantity) as quantity FROM us_wallet WHERE uw_usid = "+userId+" AND uw_cpid = "+companyId;
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                ownedQuantity = resultSet.getInt("quantity");
            }

            ownedNumTv.setText(ownedQuantity.toString());
            estimatedNumTv.setText(doubleToTwoDecimal(ownedQuantity * cLast));

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

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }

    }

    /* ######### Draw chart ######### */
    private void drawChart(){
        mLineChart.setOnChartValueSelectedListener(this);
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);

        // Hide labels on right Y axis
        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawLabels(false);

        // Display date on X axis
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDate()));


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


    /* ######### Other functions ######### */

    // Convert double value into String with two decimal places precision
    private String doubleToTwoDecimal(Double number) {
        // Format Double to two decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(number);
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

    // Hook on GUI elements
    private void connectVariablesToGui() {
        cNameTv = findViewById(R.id.act_cdetails_cname_tv);
        cTickerTv = findViewById(R.id.act_cdetails_ticker_tv);
        cLastTv = findViewById(R.id.act_cdetails_last_tv);
        cPChangeTv = findViewById(R.id.act_cdetails_pchange_tv);
        changeSymbolView = findViewById(R.id.act_cdetails_change_symbol_view);

        ownedNumTv = findViewById(R.id.act_cdetails_owned_num_tv);
        estimatedNumTv = findViewById(R.id.act_cdetails_estimated_num_tv);
        priceConverterTv = findViewById(R.id.act_cdetails_price_converter_tv);
        quantityEt = findViewById(R.id.act_cdetails_quantity_et);

        mLineChart = findViewById(R.id.act_cdetails_linearchart);
    }

    /* #### Chart interface ####*/
    @Override
    public void onValueSelected(Entry e, Highlight h) {
//        Toast.makeText(this, Float.toString(e.getY()), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, Float.toString(h.getX()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

}