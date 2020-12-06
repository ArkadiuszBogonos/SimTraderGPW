package com.example.simtradergpw;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simtradergpw.activity.LoginActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class TransactionsFragment extends Fragment {
    private ArrayList<StockRecord> uTransactionHistory = new ArrayList<>();
    private RecyclerView transactionHistoryRecyclerView;
    private int userId;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // Get user info from sharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        connectVariablesToGui(view);

        getDataFromDb();
        showOwnedStocks();

        return view;
    }

    private void getDataFromDb() {
        Statement statement = null;


        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sql = "SELECT cp_name, cp_ticker, ut_quantity, ut_price_per_stock, ut_is_buy, ut_timestamp FROM us_transactions_h " +
                         "INNER JOIN cp__company ON ut_cpid = cp_id WHERE ut_usid = " + userId + " ORDER BY ut_timestamp DESC";
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
            Toast.makeText(getContext(), throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

    private void showOwnedStocks() {
        if (uTransactionHistory.size() > 0) {
            TransactionsRecyclerViewAdapter myAdapter = new TransactionsRecyclerViewAdapter(getContext(), uTransactionHistory);
            transactionHistoryRecyclerView.setAdapter(myAdapter);
            transactionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }


    private void connectVariablesToGui(View view) {
        transactionHistoryRecyclerView = view.findViewById(R.id.fr_transactions_transactions_recyclerview);
    }
}
