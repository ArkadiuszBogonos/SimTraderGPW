package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.R;
import com.example.simtradergpw.StockRecord;
import com.example.simtradergpw.TransactionsRecyclerViewAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SingleCompanyTransactionsActivity extends AppCompatActivity {
    private ArrayList<StockRecord> uTransactionHistory = new ArrayList<>();
    private RecyclerView transactionHistoryRecyclerView;
    private String cTicker;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_transactions);
        connectVariablesToGui();

        // Get user ID
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        // Get ticker from extras
        if (getIntent().hasExtra("ticker")) cTicker = getIntent().getStringExtra("ticker");

        getDataFromDb();
        showOwnedStocks();
    }

    private void getDataFromDb() {
        Statement statement = null;


        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sql = "SELECT cp_name, cp_ticker, ut_quantity, ut_price_per_stock, ut_is_buy, ut_timestamp FROM us_transactions_h " +
                    "INNER JOIN cp__company ON ut_cpid = cp_id WHERE ut_usid = " + userId + " AND cp_ticker = '"+cTicker+"' ORDER BY ut_timestamp DESC";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                // Data from database
                String name = resultSet.getString("cp_name");
                String ticker = resultSet.getString("cp_ticker");
                Integer ownedQuantity = resultSet.getInt("ut_quantity");
                Boolean isBuy = resultSet.getBoolean("ut_is_buy");
                String timeStamp = resultSet.getString("ut_timestamp");
                Double last = resultSet.getDouble("ut_price_per_stock");

                StockRecord record = new StockRecord(name, ticker, last, null, ownedQuantity, timeStamp, isBuy);
                uTransactionHistory.add(record);
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

    private void connectVariablesToGui() {
        transactionHistoryRecyclerView = findViewById(R.id.fr_transactions_transactions_recyclerview);
    }
}