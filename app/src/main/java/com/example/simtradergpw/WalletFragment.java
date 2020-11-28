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

import com.example.simtradergpw.R;
import com.example.simtradergpw.activity.LoginActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static android.content.Context.MODE_PRIVATE;

public class WalletFragment extends Fragment {
    private TextView uLoginTv, uWalletValueTv, uMoneyTv, uStocksValueTv, uLoanTv;
    private Double userBalance, userLoan, userOwnedStockVal, userWalletValue;
    private String userLogin;
    private Integer userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        // Get user info from sharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userLogin = sharedPreferences.getString("userLogin", "ERROR");
        userId = sharedPreferences.getInt("userId", 0);

        connectVariablesToGui(view);
        getDataFromDb();

        return view;
    }


    private void getDataFromDb() {
        Statement statement = null;
        userWalletValue = 0.0;

        try {
            statement = LoginActivity.connection.createStatement();

            // Check how many stocks of this company user owns
            String sqlUserData = "SELECT us_balance, us_loan FROM us__users WHERE us_id = "+userId;
            ResultSet resultUserData = statement.executeQuery(sqlUserData);
            if (resultUserData.next()) {
                userBalance = resultUserData.getDouble(("us_balance"));
                userLoan = resultUserData.getDouble(("us_loan"));
            }

//            String sqlOwnedStocksVal = "SELECT us_balance, us_loan FROM us_wallet WHERE us_id = "+userId;
//            ResultSet resultOwnedStocksVal = statement.executeQuery(sqlOwnedStocksVal);
//            if (resultOwnedStocksVal.next()) {
//                userBalance = resultOwnedStocksVal.getDouble(("us_balance"));
//            }

            uMoneyTv.setText(userBalance.toString());
            uLoanTv.setText(userLoan.toString());



        } catch (SQLException throwables) {
            Toast.makeText(getContext(), throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }


    // Hook on GUI elements
    private void connectVariablesToGui(View view) {
        uLoginTv = view.findViewById(R.id.fr_wallet_login_tv);
        uLoginTv.setText(userLogin.toUpperCase());

        uWalletValueTv = view.findViewById(R.id.fr_wallet_wallet_value_tv);
        uMoneyTv = view.findViewById(R.id.fr_wallet_money_tv);
        uStocksValueTv = view.findViewById(R.id.fr_wallet_stocks_value_tv);
        uLoanTv = view.findViewById(R.id.fr_wallet_loan_tv);
    }
}
