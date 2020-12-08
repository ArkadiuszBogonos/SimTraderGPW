package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.R;
import com.example.simtradergpw.StockRecord;
import com.example.simtradergpw.TransactionsRecyclerViewAdapter;
import com.example.simtradergpw.UserRecord;
import com.example.simtradergpw.UsersRankingRecyclerViewAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RankingActivity extends AppCompatActivity {
    ArrayList<UserRecord> usersArrayList = new ArrayList<>();
    RecyclerView rankingRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        connectVariablesToGui();
        getDataFromDb();
        sortArrayList();
        showOwnedStocks();
    }

    private void getDataFromDb() {
        Statement statement;
        String sql;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            sql = "SELECT * FROM us__users";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Integer userId;
                String login;
                Double money, loans;

                userId = resultSet.getInt("us_id");
                login = resultSet.getString("us_login");
                money = resultSet.getDouble("us_balance");
                loans = resultSet.getDouble("us_loan");

                UserRecord user = new UserRecord(userId,login,money, loans);
                usersArrayList.add(user);
            }

            // Get owned stocks value
            for (int i = 0; i < usersArrayList.size(); i++) {
                Integer userId = usersArrayList.get(i).getUserId();

                sql = "SELECT uw_quantity, cp_last FROM us_wallet INNER JOIN cp__company ON uw_cpid = cp_id WHERE uw_usid = " + userId
                        + " AND uw_quantity > 0";
                resultSet = statement.executeQuery(sql);

                Double price;
                Integer quantity;
                Double ownedStocksValue = 0.0;

                while (resultSet.next()) {
                    quantity = resultSet.getInt("uw_quantity");
                    price = resultSet.getDouble("cp_last");
                    ownedStocksValue += price * quantity;
                }
                usersArrayList.get(i).setOwnedStocksValue(ownedStocksValue);
            }


        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

    private void sortArrayList() {
        Collections.sort(usersArrayList);
    }

    private void showOwnedStocks() {
        if (usersArrayList.size() > 0) {
            UsersRankingRecyclerViewAdapter myAdapter = new UsersRankingRecyclerViewAdapter(this, usersArrayList);
            rankingRecyclerView.setAdapter(myAdapter);
            rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }


    private void connectVariablesToGui() {
        rankingRecyclerView = findViewById(R.id.act_ranking_recyclerview);
    }
}