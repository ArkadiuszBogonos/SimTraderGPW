package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simtradergpw.ChartDataXY;
import com.example.simtradergpw.DatabaseCommunication;
import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.FormatHelper;
import com.example.simtradergpw.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

public class StatisticsActivity extends AppCompatActivity {
    private BarChart walletValueBarChart;
    private View scoreArrow;
    private TextView scorePercentageTv, sessionStartTv, numOfRestartsTv, balanceTv, stocksValueTv, turnoverTv,
            activeLoansTv, sumOfPaidLoansValTv, sumOfCommissionTv, numOfPaidLoansTv;


    private ArrayList<ChartDataXY> walletValsMonthly = new ArrayList<>();
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
        getChartData();
        drawWalletValueBarChart();
    }

    private void getDataFromDb() {
        Statement statement = null;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Get user info
            String sqlUser = "SELECT us_balance, us_loan, us_owned_stocks_value, us_paid_loans, us_session_start, us_num_of_restarts FROM us__users WHERE us_id = " + userId;
            ResultSet resultSet = statement.executeQuery(sqlUser);
            resultSet.next();

            Double balance = resultSet.getDouble("us_balance");
            Double loan = resultSet.getDouble("us_loan");
            Double stocksValue = resultSet.getDouble("us_owned_stocks_value");
            Integer numOfPaidLoans = resultSet.getInt("us_paid_loans");
            Integer numOfRestarts = resultSet.getInt("us_num_of_restarts");
            String sessionStart = resultSet.getDate("us_session_start").toString();

            Double scorePercentage = ((balance + stocksValue - loan - DatabaseCommunication.START_BALANCE) / DatabaseCommunication.START_BALANCE);

            scorePercentageTv.setText(FormatHelper.doubleToPercent(scorePercentage, 2));
            if (scorePercentage > 0) {
                scorePercentageTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingUp));
                scoreArrow.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_drop_up_green_24));
            } else if (scorePercentage < 0) {
                scorePercentageTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingDown));
                scoreArrow.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_drop_down_red_24));
            } else {
                scorePercentageTv.setTextColor(ContextCompat.getColor(this, R.color.colorText));
                scoreArrow.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_baseline_no_change_grey_24));
            }

            // Total user turnover
            String sqlTurnover = "SELECT SUM(ut_quantity * ut_price_per_stock) as us_turnover FROM us_transactions_h WHERE ut_usid = " + userId;
            resultSet = statement.executeQuery(sqlTurnover);
            resultSet.next();

            Double turnover = resultSet.getDouble("us_turnover");

            // Loans details
            String sqlLoans = "SELECT SUM(ul_loan) AS ul_loan_sum, SUM(ul_commission) AS ul_commission_sum FROM us_loans WHERE ul_usid = " + userId;
            resultSet = statement.executeQuery(sqlLoans);
            resultSet.next();

            Double totalLoans = resultSet.getDouble("ul_loan_sum");
            Double totalComission = resultSet.getDouble("ul_commission_sum");


            sessionStartTv.setText(sessionStart);
            numOfRestartsTv.setText(numOfRestarts.toString());

            balanceTv.setText(FormatHelper.doubleToTwoDecimal(balance));
            stocksValueTv.setText(FormatHelper.doubleToTwoDecimal(stocksValue));
            turnoverTv.setText(turnover.toString());

            activeLoansTv.setText(loan.toString());
            sumOfPaidLoansValTv.setText(FormatHelper.doubleToTwoDecimal(totalLoans));
            sumOfCommissionTv.setText(FormatHelper.doubleToTwoDecimal(totalComission));
            numOfPaidLoansTv.setText(numOfPaidLoans.toString());

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

    private void getChartData() {
        Statement statement = null;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Integer nextYear = currentYear + 1;

            // Get user info
            String sql = "SELECT * FROM (SELECT uwh.uwh_id, uwh.uwh_value, uwh.uwh_timestamp, " +
                    "ROW_NUMBER() OVER (PARTITION BY YEAR(uwh.uwh_timestamp), Month(uwh.uwh_timestamp) ORDER BY uwh.uwh_timestamp DESC) 'RowRank'" +
                    "FROM us_wallet_value_h uwh WHERE uwh_usid = " + userId + " )sub WHERE RowRank = 1 AND uwh_timestamp BETWEEN '"+ currentYear.toString() +"' AND '" + nextYear.toString() +
                    "' ORDER BY uwh_timestamp ASC";
            ResultSet resultSet = statement.executeQuery(sql);
            Double value;
            Integer month;
            
            while (resultSet.next()) {
                value = resultSet.getDouble("uwh_value");
                Date date = resultSet.getDate("uwh_timestamp");

                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                month = localDate.getMonthValue();


                ChartDataXY record = new ChartDataXY(FormatHelper.doubleToTwoDecimal(value), month.toString());
                walletValsMonthly.add(record);
            }

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

//    private void drawWalletValueBarChart() {
//        ArrayList<BarEntry> records = new ArrayList<>();
//
//        for (int j = 0; j < 12; j++) {
//
//        }
//        for (int i = 0; i < walletValsMonthly.size(); i++) {
//            ChartDataXY chartData = walletValsMonthly.get(i);
//
//            String x = chartData.getX().replace(',', '.');
//            Float value = Float.parseFloat(x);
//
//            String y = chartData.getY().replace(',', '.');
//            Float month = Float.parseFloat(y);
//
//            records.add(new BarEntry(month, value));
//        }
//
//
//        BarDataSet barDataSet = new BarDataSet(records, "");
//        barDataSet.setColors(getColor(R.color.colorBlue));
//
//        BarData barData = new BarData(barDataSet);
//
//        walletValueBarChart.setFitBars(true);
//        walletValueBarChart.setData(barData);
//        walletValueBarChart.getDescription().setText("");
//        walletValueBarChart.animateY(1000);
//    }
//
    private void drawWalletValueBarChart() {
        // Disable chart scaling
        walletValueBarChart.setScaleEnabled(false);

        // Animate X axis data
        walletValueBarChart.animateY(1000);

        // Set chart description
        Description description = new Description();
        description.setText(" ");
        walletValueBarChart.setDescription(description);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        xAxisLabel.add("I");
        xAxisLabel.add("II");
        xAxisLabel.add("III");
        xAxisLabel.add("IV");
        xAxisLabel.add("V");
        xAxisLabel.add("VI");
        xAxisLabel.add("VII");
        xAxisLabel.add("VIII");
        xAxisLabel.add("IX");
        xAxisLabel.add("X");
        xAxisLabel.add("XI");
        xAxisLabel.add("XII");

        // X axis label formatter
        walletValueBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        walletValueBarChart.getXAxis().setLabelCount(12);

        // Hide labels on right Y axis
        walletValueBarChart.getAxisLeft().setDrawLabels(false);


        ArrayList<BarEntry> months = new ArrayList<>();
        months.add(new BarEntry(1, 9321));
        months.add(new BarEntry(2, 8632));
        months.add(new BarEntry(3, 11233));
        months.add(new BarEntry(4, 16399));
        months.add(new BarEntry(5, 20300));
        months.add(new BarEntry(6, 19400));
        months.add(new BarEntry(7, 17999));
        months.add(new BarEntry(8, 18126));
        months.add(new BarEntry(9, 20444));
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
        scoreArrow = findViewById(R.id.act_stats_score_perc_arrow);
        scorePercentageTv = findViewById(R.id.act_stats_score_perc_tv);
        sessionStartTv = findViewById(R.id.act_stats_session_start_tv);
        numOfRestartsTv = findViewById(R.id.act_stats_num_of_restarts_tv);
        balanceTv = findViewById(R.id.act_stats_balance_tv);
        stocksValueTv = findViewById(R.id.act_stats_stock_val_tv);
        turnoverTv = findViewById(R.id.act_stats_turnover_tv);
        activeLoansTv = findViewById(R.id.act_stats_loans_tv);
        sumOfPaidLoansValTv = findViewById(R.id.act_stats_sum_of_paid_loans_tv);
        sumOfCommissionTv = findViewById(R.id.act_stats_loans_commision_tv);
        numOfPaidLoansTv = findViewById(R.id.act_stats_num_of_paid_loans_tv);
    }
}