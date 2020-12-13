package com.example.simtradergpw;

import android.content.Context;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.simtradergpw.activity.LoansActivity;
import com.example.simtradergpw.activity.LoginActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DatabaseCommunication {
    private static final String TAG = "DatabaseCommunication";
    static Statement statement = null;
    public static final Double COMISSION_PERCENT = 0.003;
    public static final Double MIN_COMISSION = 3.0;
    public static final Double START_BALANCE = 10000.0;

    private DatabaseCommunication() {

    }

    public static void buyStock(Context context, int userId, int companyId, int quantity, double pricePerStock) {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        final int ACTION_TYPE = 1; // Type of action: 1 means its buy, 0 means its sell

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            Double total = quantity * pricePerStock;

            // Reduce user balance
            String sqlUpdateBalance = "UPDATE us__users SET us_balance = us_balance - " + total + " WHERE us_id=" + userId;
            statement.executeUpdate(sqlUpdateBalance);

//            // Save in user balance history
//            String sqlInsertBalanceHistory = "INSERT INTO us_balance_h (ub_usid, ub_balance, ub_timestamp) " +
//                    "VALUES (" + userId + ", (SELECT us_balance FROM us__users WHERE us_id = " + userId + "), '" + timeStamp + "')";
//            statement.executeUpdate(sqlInsertBalanceHistory);

            // Check whether user already have that company stock in wallet
            String sqlIsInWallet = "SELECT uw_id FROM us_wallet WHERE uw_usid = " + userId + " AND uw_cpid= " + companyId;
            ResultSet resultIsInWallet = statement.executeQuery(sqlIsInWallet);

            // Update number of owned stocks
            if (resultIsInWallet.next()) {
                Integer wallet_id = resultIsInWallet.getInt("uw_id");

                String sqlUpdateWallet = "UPDATE us_wallet SET uw_quantity = uw_quantity + " + quantity + " WHERE uw_id =" + wallet_id;
                statement.executeUpdate(sqlUpdateWallet);
            }
            // Insert new record
            else {
                String sqlInsertIntoWallet = "INSERT INTO us_wallet VALUES (" + userId + ", " + companyId + ", " + quantity + " )";
                statement.executeUpdate(sqlInsertIntoWallet);
            }

//            saveInWUserWalletValueHistory(context, userId, timeStamp);
            saveInTransactionHistory(context, userId, companyId, ACTION_TYPE, quantity, pricePerStock);

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static void sellStock(Context context, int userId, int companyId, int quantity, double pricePerStock) {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        final int ACTION_TYPE = 0; // Type of action: 1 means its buy, 0 means its sell

        // Calculate comission
        Double comission;
        if (COMISSION_PERCENT * (quantity * pricePerStock) > 3)
            comission = COMISSION_PERCENT * (quantity * pricePerStock);
        else comission = MIN_COMISSION;

        // Calculate total income
        Double total = quantity * pricePerStock - comission;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Update wallet
            String sqlUpdateWallet = "UPDATE us_wallet SET uw_quantity = uw_quantity - " + quantity + " WHERE uw_usid =" + userId + " AND  uw_cpid=" + companyId;
            Integer updatedRows = statement.executeUpdate(sqlUpdateWallet);

            // Update user balance
            String sqlUpdateBalance = "UPDATE us__users SET us_balance = us_balance + " + total + " WHERE us_id=" + userId;
            updatedRows = statement.executeUpdate(sqlUpdateBalance);

//            // Save in user balance history
//            String sqlInsertBalanceHistory = "INSERT INTO us_balance_h (ub_usid, ub_balance, ub_timestamp) " +
//                    "VALUES (" + userId + ", (SELECT us_balance FROM us__users WHERE us_id = " + userId + "), '" + timeStamp + "')";
//            Integer insertedRows = statement.executeUpdate(sqlInsertBalanceHistory);

//            saveInWUserWalletValueHistory(context, userId, timeStamp);
            saveInTransactionHistory(context, userId, companyId, ACTION_TYPE, quantity, pricePerStock);

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static void getLoan(Context context, int userId, Double amount, Integer paymentInDays) {
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE, paymentInDays);
        Date targetDate =calendar.getTime();
        final String paymentDate = new SimpleDateFormat("yyyy-MM-dd").format(targetDate);


        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sql = "UPDATE us__users SET us_balance = us_balance+"+amount+", us_loan = "+amount+", us_loan_payment_date='"+paymentDate+"'  WHERE us_id="+userId;
            statement.executeUpdate(sql);
            Toast.makeText(context, "Zaciągnięto " + amount+ "PLN pożyczki", Toast.LENGTH_LONG).show();

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static void payLoan(Context context, int userId, Double amount) {
        Double totalToPay = amount + amount * LoansActivity.INTEREST_RATE;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sql = "UPDATE us__users SET us_balance = us_balance-"+totalToPay+
                    ", us_loan = us_loan-"+amount+", us_loan_payment_date=NULL, " +
                    "us_paid_loans = us_paid_loans + 1 WHERE us_id="+userId;

            statement.executeUpdate(sql);
            Toast.makeText(context, "Spłacono pożyczkę!", Toast.LENGTH_LONG).show();

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static void addNewUser(Context context, String uLogin, String uEmail, String uPassword) {
        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sqlAddNewUser = "INSERT INTO us__users VALUES ('" + uLogin + "', '" + uEmail + "', '" + uPassword + "', " + START_BALANCE + ", NULL, NULL, 0, NULL, 0)";
            statement.executeUpdate(sqlAddNewUser);

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static void resetProgress(Context context, int userId){
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        try {
            statement= DatabaseConnection.getConnection().createStatement();

            String sql = "UPDATE us__users SET us_balance = 10000, us_loan = 0, us_owned_stocks_value = 0," +
                    " us_loan_payment_date = NULL, us_paid_loans = 0 WHERE us_id="+userId;
            statement.executeUpdate(sql);

            sql = "DELETE FROM us_wallet WHERE uw_usid = " + userId;
            statement.executeUpdate(sql);

            sql = "DELETE FROM us_transactions_h WHERE ut_usid = " + userId;
            statement.executeUpdate(sql);

            sql = "DELETE FROM us_wallet_value_h WHERE uwh_usid = " + userId;
            statement.executeUpdate(sql);


            sql = "INSERT INTO us_wallet_value_h (uwh_usid, uwh_value, uwh_timestamp) " +
                    "VALUES (" + userId + ", 10000, '" + timeStamp + "')";
            statement.executeUpdate(sql);
            statement.executeUpdate(sql);

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static boolean userHaveEnoughMoney(Context context, int userId, Double amount){
        try {
            statement = DatabaseConnection.getConnection().createStatement();
            Double userBalance = -1.0;

            String sql = "SELECT us_balance FROM us__users WHERE us_id = " + userId;
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                userBalance = resultSet.getDouble("us_balance");
            }

            if (userBalance < amount) return false;
            else return true;

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean userHaveEnoughStocks(Context context, int userId, int companyId, Integer quantity){
        try {
            statement = DatabaseConnection.getConnection().createStatement();
            int ownedQuantity = 0;

            String sql = "SELECT uw_quantity FROM us_wallet WHERE uw_usid = " + userId + " AND uw_cpid = " +companyId;
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                ownedQuantity = resultSet.getInt("uw_quantity");
            }

            if (ownedQuantity < quantity) return false;
            else return true;

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
            return false;
        }
    }

    private static void saveInTransactionHistory(Context context, int userId, int companyId, int actionType, int quantity, double pricePerStock) {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sqlInsertIntoWallet = "INSERT INTO us_transactions_h VALUES (" + userId + ", " + companyId + ", " + quantity + ", " + pricePerStock + ", " + actionType + ", '" + timeStamp + "')";
            Integer insertedRows = statement.executeUpdate(sqlInsertIntoWallet);
        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    private static void saveInWUserWalletValueHistory(Context context, int userId, String timeStamp) {
        Double userWalletValue;
        Double userLoan = 0.0;
        Double userBalance = 0.0;
        Double userOwnedStocksValue = 0.0;

        // Get user balance and loan
        String sqlGetUserDetails = "SELECT us_balance, us_loan FROM us__users WHERE us_id =" + userId;
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sqlGetUserDetails);

            if (resultSet.next()) {
                userBalance = resultSet.getDouble(("us_balance"));
                userLoan = resultSet.getDouble(("us_loan"));
            }
            // Get user owned stocks value
            String sqlOwnedStocks = "SELECT uw_quantity, cp_last FROM us_wallet INNER JOIN cp__company ON uw_cpid = cp_id WHERE uw_usid = " + userId
                    + " AND uw_quantity > 0";
            resultSet = statement.executeQuery(sqlOwnedStocks);

            while (resultSet.next()) {
                // Data from database
                Integer ownedQuantity = resultSet.getInt(("uw_quantity"));
                Double currentPrice = resultSet.getDouble("cp_last");

                userOwnedStocksValue += ownedQuantity * currentPrice;
            }

            userWalletValue = userBalance + userOwnedStocksValue - userLoan;

            String sqlInsertWalletHistory = "INSERT INTO us_wallet_value_h (uw_usid, uw_value, uw_timestamp) " +
                    "VALUES (" + userId + ", "+userWalletValue+", '" + timeStamp + "')";
            statement.executeUpdate(sqlInsertWalletHistory);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
