package com.example.simtradergpw.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.simtradergpw.R;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class CompanyDetailsActivity extends AppCompatActivity {
    private TextView cNameTv, cTickerTv, cLastTv, cPChangeTv;
    private TextView ownedNumTv, estimatedNumTv;
    private EditText quantityEt;
    private View changeSymbolView;
    private String cName, cTicker, cPChange;
    private Double cLast, pChangeValue;
    private Integer userId, companyId, ownedQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
        connectVariablesToGui();

        getData();

        // Get user ID
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        // Get company ID
        Statement statement = null;
        try {
            statement = LoginActivity.connection.createStatement();
            String sqlCompanyId = "SELECT cp_id FROM cp__company WHERE cp_ticker ='" + cTicker + "'";
            ResultSet resultCompanyId = statement.executeQuery(sqlCompanyId);

            if (resultCompanyId.next()) companyId = resultCompanyId.getInt("cp_id");

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }


        setData();
        getFromDb();
    }

    /* ######### Database communication ######### */
    public void buyStockBtn(View view) {
        Integer quantity = null;

        try {
            quantity = Integer.parseInt(quantityEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        DatabaseCommunication.buyStock(this, userId, companyId, quantity, cLast);

        Toast.makeText(this, "Kupiono akcje!", Toast.LENGTH_SHORT).show();
        quantityEt.setText("");
        getFromDb();
    }

    public void sellStockBtn(View view) {
        Integer quantity = null;

        try {
            quantity = Integer.parseInt(quantityEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        DatabaseCommunication.sellStock(this, userId, companyId, quantity, cLast);

        Toast.makeText(this, "Sprzedano akcje!", Toast.LENGTH_SHORT).show();
        quantityEt.setText("");
        getFromDb();
    }

    /* ######### Get data from passed extras ######### */
    private void getData() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("ticker") &&
                getIntent().hasExtra("last") && getIntent().hasExtra("pChange")) {

            cName = getIntent().getStringExtra("name");
            cTicker = getIntent().getStringExtra("ticker");
            cPChange = getIntent().getStringExtra("pChange");

            String cLastBufor = getIntent().getStringExtra("last");
            cLastBufor = cLastBufor.replace(",", ".");
            cLast = Double.parseDouble(cLastBufor);
        } else {
            Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT);
        }

        String pChangeBufor = cPChange;
        pChangeBufor = pChangeBufor.replace("%", "");
        pChangeBufor = pChangeBufor.replace(",", ".");
        try {
            pChangeValue = Double.parseDouble(pChangeBufor);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /* ######### Set data in layout from passed extras ######### */
    private void setData() {
        cNameTv.setText(cName);
        cTickerTv.setText(cTicker);
        cLastTv.setText(Double.toString(cLast));
        cPChangeTv.setText(cPChange);

        /* Set adequate text color and symbol depending of change value */
        if (pChangeValue == 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_no_change_grey_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorText));
        } else if (pChangeValue > 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_drop_up_green_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingUp));
        } else if (pChangeValue < 0) {
            changeSymbolView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_drop_down_red_24, null));
            cPChangeTv.setTextColor(ContextCompat.getColor(this, R.color.colorTrendingDown));
        }
    }

    /* ######### Set data in layout from database ######### */
    private void getFromDb() {
        Statement statement = null;
        try {
            statement = LoginActivity.connection.createStatement();

            // Check how many stocks of this company user owns
            String sqlOwnedQuantity = "SELECT SUM(uw_quantity) as quantity FROM us_wallet WHERE uw_usid = "+userId+" AND uw_cpid = "+companyId;
            ResultSet resultOwnedQuantity = statement.executeQuery(sqlOwnedQuantity);
            if (resultOwnedQuantity.next()) {
                ownedQuantity = resultOwnedQuantity.getInt("quantity");
            }

            ownedNumTv.setText(ownedQuantity.toString());
            estimatedNumTv.setText(doubleToTwoDecimal(ownedQuantity * cLast));

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }


    }

    /* ######### Other functions ######### */
    private String doubleToTwoDecimal(Double number) {
        // Format Double to two decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(number);
    }

    // Hook on GUI elements
    private void connectVariablesToGui() {
        cNameTv = findViewById(R.id.act_cdetails_cname_tv);
        cTickerTv = findViewById(R.id.act_cdetails_ticker_tv);
        cLastTv = findViewById(R.id.act_cdetails_last_tv);
        cPChangeTv = findViewById(R.id.act_cdetails_pchange_tv);
        changeSymbolView = findViewById(R.id.act_cdetails_change_symbol_view);

        ownedNumTv = findViewById(R.id.act_cdetails_owned_num_tv);
        estimatedNumTv = findViewById(R.id.act_cdetails_estimated_num_tv);

        quantityEt = findViewById(R.id.act_cdetails_quantity_et);
    }
}