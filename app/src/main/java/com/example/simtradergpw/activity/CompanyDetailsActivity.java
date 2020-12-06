package com.example.simtradergpw.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
import com.example.simtradergpw.FormatHelper;
import com.example.simtradergpw.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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

public class CompanyDetailsActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private TextView cNameTv, cTickerTv, cLastTv, cPChangeTv;
    private TextView ownedNumTv, estimatedNumTv, priceConverterTv, quantityConverterTv, resultConverterTv;
    private EditText quantityEt;
    private View changeSymbolView;
    private String cName, cTicker;
    private Double cLast, cPChange;
    private Integer userId, companyId, ownedQuantity;
    private LineChart mLineChart;

    ArrayList<ChartData> stockPriceHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
        connectVariablesToGui();


        // Get user ID
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        // Get ticker from extras
        if (getIntent().hasExtra("ticker")) cTicker = getIntent().getStringExtra("ticker");

        // Get company ID
        Statement statement = null;
        try {
            statement = DatabaseConnection.getConnection().createStatement();
            String sqlCompanyDetails = "SELECT * FROM cp__company WHERE cp_ticker ='" + cTicker + "'";
            ResultSet resultCompanyDetails = statement.executeQuery(sqlCompanyDetails);

            if (resultCompanyDetails.next()) {
                companyId = resultCompanyDetails.getInt("cp_id");
                cName = resultCompanyDetails.getString("cp_name");
                cLast = resultCompanyDetails.getDouble("cp_last");
                cPChange = resultCompanyDetails.getDouble("cp_p_change");

            }
        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }


        setData();
        getDataFromDb();
        drawChart();
        setMaxQuantityLength();

        // Action on quantity edited
        quantityEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer quantity = null;

                if (s.length() > 0) {
                    try {
                        quantity = Integer.parseInt(s.toString());
                        Double result = quantity * cLast;

                        quantityConverterTv.setText(s);
                        resultConverterTv.setText(FormatHelper.doubleToFourDecimal(result));
                    } catch (NumberFormatException e) {
                        Toast.makeText(CompanyDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        quantityConverterTv.setText("0");
                        resultConverterTv.setText("0");
                        e.printStackTrace();
                    }
                } else {
                    quantityConverterTv.setText("0");
                    resultConverterTv.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        getDataFromDb();
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
        getDataFromDb();
    }

    /* ######### Set data in layout from passed extras ######### */
    private void setData() {
        cNameTv.setText(cName);
        cTickerTv.setText(cTicker);
        cLastTv.setText(Double.toString(cLast));
        cPChangeTv.setText(cPChange.toString());

        /* Set adequate text color and symbol depending of change value */
        if (cPChange == 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_no_change_grey_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorText));
        } else if (cPChange > 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_drop_up_green_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingUp));
        } else if (cPChange < 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_drop_down_red_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingDown));
        }

        quantityConverterTv.setText("0");
        priceConverterTv.setText(Double.toString(cLast));
        resultConverterTv.setText("0");
    }

    /* ######### Get data from database and set it on layout ######### */
    private void getDataFromDb() {
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
            estimatedNumTv.setText(FormatHelper.doubleToTwoDecimal(ownedQuantity * cLast));

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

    private void setMaxQuantityLength(){
        // Function must be called after getting last stock price
        InputFilter[] editFilters = quantityEt.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);

        if (cLast < 9.99) newFilters[editFilters.length] = new InputFilter.LengthFilter(6);
        else if ((cLast < 99.99)) newFilters[editFilters.length] = new InputFilter.LengthFilter(5);
        else if ((cLast < 999.99)) newFilters[editFilters.length] = new InputFilter.LengthFilter(4);
        else if ((cLast < 9999.99)) newFilters[editFilters.length] = new InputFilter.LengthFilter(3);
        else if ((cLast < 99999.99)) newFilters[editFilters.length] = new InputFilter.LengthFilter(2);

        quantityEt.setFilters(newFilters);
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
        priceConverterTv = findViewById(R.id.act_cdetails_price_price_converter_tv);
        quantityConverterTv = findViewById(R.id.act_cdetails_quantity_price_converter_tv);
        resultConverterTv = findViewById(R.id.act_cdetails_result_price_converter_tv);

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