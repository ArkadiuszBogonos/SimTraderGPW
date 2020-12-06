package com.example.simtradergpw;

import android.content.Context;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Wig20 {
    private ArrayList<StockRecord> wig20records = new ArrayList<>();
    private Statement statement = null;

    private String name, ticker;
    private Double last, pChange;

    public ArrayList<StockRecord> getPricesWig20(Context context) {
        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sql = "SELECT * FROM cp__company WHERE cp_is_active = 1 ORDER BY cp_name ASC";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                name = resultSet.getString("cp_name");
                ticker = resultSet.getString("cp_ticker");
                last = resultSet.getDouble("cp_last");
                pChange = resultSet.getDouble("cp_p_change");

                StockRecord record = new StockRecord(name, ticker, last, pChange);
                wig20records.add(record);
            }

        } catch (SQLException throwables) {
            Toast.makeText(context, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }


        return wig20records;
    }
}
