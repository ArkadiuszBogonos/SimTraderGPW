package com.example.simtradergpw.activity;

import android.content.Context;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public final class DatabaseCommunication {
    static Statement statement = null;
    public static final Double COMISSION_PERCENT = 0.003;
    public static final Double MIN_COMISSION = 3.0;
    public static final Double START_BALANCE = 10000.0;

    private DatabaseCommunication() {

    }

    public static void buyStock(Context context, int userId, int companyId, int quantity, double pricePerStock) {
        final int ACTION_TYPE = 1; // Type of action: 1 means its buy, 0 means its sell

        try {
            statement = LoginActivity.connection.createStatement();

            Double total = quantity * pricePerStock;

            // Reduce user balance
            String sqlUpdateBalance = "UPDATE us__users SET us_balance = us_balance - "+ total +" WHERE us_id=" + userId;
            Integer updatedRows = statement.executeUpdate(sqlUpdateBalance);

            // Check wheter user already have that company stock in wallet
            String sqlIsInWallet = "SELECT uw_id FROM us_wallet WHERE uw_usid = " + userId + " AND uw_cpid= " + companyId;
            ResultSet resultIsInWallet = statement.executeQuery(sqlIsInWallet);

            // Update number of owned stocks
            if (resultIsInWallet.next()) {
                Integer wallet_id = resultIsInWallet.getInt("uw_id");

                String sqlUpdateWallet = "UPDATE us_wallet SET uw_quantity = uw_quantity + " + quantity + " WHERE uw_id =" + wallet_id;
                updatedRows = statement.executeUpdate(sqlUpdateWallet);
            }
            // Insert new record
            else {
                String sqlInsertIntoWallet = "INSERT INTO us_wallet VALUES (" + userId + ", " + companyId + ", " + quantity + " )";
                Integer insertedRows = statement.executeUpdate(sqlInsertIntoWallet);
            }

            saveInTransactionHistory(context, userId, companyId, ACTION_TYPE, quantity, pricePerStock);
        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static void sellStock(Context context, int userId, int companyId, int quantity, double pricePerStock) {
        final int ACTION_TYPE = 0; // Type of action: 1 means its buy, 0 means its sell

        // Calculate comission
        Double comission;
        if (COMISSION_PERCENT * (quantity * pricePerStock) > 3) comission = COMISSION_PERCENT * (quantity * pricePerStock);
        else comission = MIN_COMISSION;

        // Calculate total income
        Double total = quantity * pricePerStock - comission;

        try {
            statement = LoginActivity.connection.createStatement();

            // Update wallet
            String sqlUpdateWallet = "UPDATE us_wallet SET uw_quantity = uw_quantity - " + quantity + " WHERE uw_usid =" + userId + " AND  uw_cpid=" + companyId;
            Integer updatedRows = statement.executeUpdate(sqlUpdateWallet);

            // Update user balance
            String sqlUpdateBalance = "UPDATE us__users SET us_balance = us_balance + "+ total +" WHERE us_id=" + userId;
            updatedRows = statement.executeUpdate(sqlUpdateBalance);

            saveInTransactionHistory(context, userId, companyId, ACTION_TYPE, quantity, pricePerStock);

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    public static void addNewUser(Context context, String uLogin, String uEmail, String uPassword) {
        try {
            statement = LoginActivity.connection.createStatement();

            String sqlAddNewUser = "INSERT INTO us__users VALUES ('" + uLogin + "', '" + uEmail + "', '" + uPassword + "', " + START_BALANCE + ", NULL, NULL, NULL)";
            Integer insertedRows = statement.executeUpdate(sqlAddNewUser);
        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    private static void saveInTransactionHistory(Context context, int userId, int companyId, int actionType, int quantity, double pricePerStock) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        try {
            statement = LoginActivity.connection.createStatement();

            String sqlInsertIntoWallet = "INSERT INTO us_transactions_h VALUES (" + userId + ", " + companyId + ", " + quantity + ", " + pricePerStock + ", " + actionType + ", '" + timeStamp + "')";
            Integer insertedRows = statement.executeUpdate(sqlInsertIntoWallet);
        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }
}
