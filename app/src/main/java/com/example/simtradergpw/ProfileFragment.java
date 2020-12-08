package com.example.simtradergpw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.simtradergpw.activity.LoansActivity;
import com.example.simtradergpw.activity.LoginActivity;
import com.example.simtradergpw.activity.MainActivity;
import com.example.simtradergpw.activity.RankingActivity;
import com.example.simtradergpw.activity.StatisticsActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    Integer userId;
    LineChart walletValueLineChart;
    Button loansBtn, statsBtn, rankBtn;
    ArrayList<ChartData> userBalanceHistoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get user ID
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        connectVariablesToGui(view);
        getDataFromDb();
        drawLineChart(walletValueLineChart, userBalanceHistoryList, "Wartość portfela");



        return view;
    }

    /* ######### Get data from database ######### */
    private void getDataFromDb() {
        Statement statement = null;
        ResultSet resultSet;
        String sql;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Get user balance history
            sql = "SELECT * FROM us_wallet_value_h WHERE uw_usid = "+ userId +" AND uw_timestamp > dateadd(day, -365, getdate())";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Double price = resultSet.getDouble("uw_value");
                String timeStamp = resultSet.getString("uw_timestamp");

                ChartData record = new ChartData(price, timeStamp);
                userBalanceHistoryList.add(record);
            }

        } catch (SQLException throwables) {
            Toast.makeText(getContext(), throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }

    }


    /* ######### Draw chart ######### */
    private void drawLineChart(LineChart lineChart, ArrayList<ChartData> chartDataArrayList, String label){
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        // Hide labels on right Y axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawLabels(false);

        // Display date on X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDate(chartDataArrayList)));

        // Add data
        ArrayList<Entry> chartValues = new ArrayList<>();

        for (int i = 0; i < chartDataArrayList.size(); i++) {
            chartValues.add(new Entry(i, chartDataArrayList.get(i).getPrice().floatValue()));
        }

        LineDataSet set1 = new LineDataSet(chartValues, label);

        set1.setFillAlpha(110);
        set1.setColor(ContextCompat.getColor(getContext(), R.color.colorBlue));
        set1.setDrawCircles(false);
        set1.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        lineChart.setData(data);
    }

    // Function that is used to format X axis on the chart
    private ArrayList<String> getDate(ArrayList<ChartData> chartDataArrayList) {
        SimpleDateFormat formatFromDatabase = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM");

        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i < chartDataArrayList.size(); i++) {
            try {
                String date = chartDataArrayList.get(i).getDate();
                date = myFormat.format(formatFromDatabase.parse(date));

                label.add(date);
            } catch (ParseException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        return label;
    }

    // Hook on GUI elements
    private void connectVariablesToGui(View view) {
        walletValueLineChart = view.findViewById(R.id.fr_profile_wallet_value_linechart);
        loansBtn = view.findViewById(R.id.fr_profile_loans_btn);
        statsBtn = view.findViewById(R.id.fr_profile_stats_btn);
        rankBtn = view.findViewById(R.id.fr_profile_rank_btn);

        loansBtn.setOnClickListener(this);
        statsBtn.setOnClickListener(this);
        rankBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.fr_profile_loans_btn:
                intent = new Intent(getContext(), LoansActivity.class);
                startActivity(intent);
                break;

            case R.id.fr_profile_stats_btn:
                intent = new Intent(getContext(), StatisticsActivity.class);
                startActivity(intent);
                break;

            case R.id.fr_profile_rank_btn:
                intent = new Intent(getContext(), RankingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
