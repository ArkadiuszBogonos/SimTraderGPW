package com.example.simtradergpw;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simtradergpw.activity.LoginActivity;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class WalletFragment extends Fragment {
    RecyclerView ownedStocksRecyclerView;
    private TextView uLoginTv, uWalletValueTv, uMoneyTv, uStocksValueTv, uLoanTv;
    private Double userBalance, userLoan, userOwnedStockVal, userWalletValue;
    private Integer userId;

    ArrayList<StockRecord> uOwnedStocksList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
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
        userWalletValue = 0.0;
        userOwnedStockVal = 0.0;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Check how many stocks of this company user owns
            String sqlUserData = "SELECT us_balance, us_loan FROM us__users WHERE us_id = " + userId;
            ResultSet resultSet = statement.executeQuery(sqlUserData);
            if (resultSet.next()) {
                userBalance = resultSet.getDouble(("us_balance"));
                userLoan = resultSet.getDouble(("us_loan"));
            }


            String sqlOwnedStocks = "SELECT uw_quantity, cp_ticker FROM us_wallet INNER JOIN cp__company ON uw_cpid = cp_id WHERE uw_usid = " + userId
                    + " AND uw_quantity > 0";
            resultSet = statement.executeQuery(sqlOwnedStocks);

            while (resultSet.next()) {
                for (Integer i = 0; i < Wig20Fragment.mWig20records.size(); i++) {
                    // Data from database
                    String tickerFromDb = resultSet.getString(("cp_ticker"));
                    Integer ownedQuantity = resultSet.getInt(("uw_quantity"));

                    StockRecord record = Wig20Fragment.mWig20records.get(i);

                    if (tickerFromDb.equals(record.getTicker())) {
                        String name = record.getName();
                        String ticker = record.getTicker();
                        Double currentPrice = record.getLast();

                        userOwnedStockVal += ownedQuantity * currentPrice;

                        StockRecord ownedStockRecord = new StockRecord(name, ticker, currentPrice, null, ownedQuantity);
                        uOwnedStocksList.add(ownedStockRecord);
                    }
                }
            }

            // Total wallet value
            userWalletValue = userBalance + userOwnedStockVal - userLoan;

            uMoneyTv.setText(FormatHelper.doubleToTwoDecimal(userBalance));
            uLoanTv.setText(FormatHelper.doubleToTwoDecimal(userLoan));
            uStocksValueTv.setText(FormatHelper.doubleToTwoDecimal(userOwnedStockVal));
            uWalletValueTv.setText(FormatHelper.doubleToTwoDecimal(userWalletValue));


        } catch (SQLException throwables) {
            Toast.makeText(getContext(), throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }


    private void showOwnedStocks() {
        if (uOwnedStocksList.size() > 0) {
            WalletOwnedStocksRecyclerViewAdapter myAdapter = new WalletOwnedStocksRecyclerViewAdapter(getContext(), uOwnedStocksList);
            ownedStocksRecyclerView.setAdapter(myAdapter);
            ownedStocksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }


    // Hook on GUI elements
    private void connectVariablesToGui(View view) {
        ownedStocksRecyclerView = view.findViewById(R.id.fr_wallet_owned_stocks_recyclerview);

        uWalletValueTv = view.findViewById(R.id.fr_wallet_wallet_value_tv);
        uMoneyTv = view.findViewById(R.id.fr_wallet_money_tv);
        uStocksValueTv = view.findViewById(R.id.fr_wallet_stocks_value_tv);
        uLoanTv = view.findViewById(R.id.fr_wallet_loan_tv);
    }
}
