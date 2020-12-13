package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.FormatHelper;
import com.example.simtradergpw.R;
import com.example.simtradergpw.dialogs.ConfirmLoanPayDialog;
import com.example.simtradergpw.dialogs.ConfirmLoanTakeDialog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoansActivity extends AppCompatActivity {
    public static final Double INTEREST_RATE = 0.02;
    private static  final Integer DAYS_TO_PAYMENT = 30;
    private final Double LOAN_AMOUNT_LOWEST = 2000.0;
    private final Double LOAN_AMOUNT_MEDIUM = 10000.0;
    private final Double LOAN_AMOUNT_HIGH = 25000.0;

    private TextView loanTv, userLoanInterestTv, loanPaymentDateTv, loanInterestRateTv, userBalanceTv, errorTv;
    private TextView paymentTimeInDaysTv;
    private Button getLoanBtn, payLoanBtn;
    private RadioGroup radioGroup;
    private RadioButton amount1Rb,amount2Rb, amount3Rb;
    private int userId, numberOfPaidLoans;
    private Double userLoan, userLoanInterests, userBalance, checkedLoanAmount;
    private String userLoanPaymentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loans);
        connectVariablesToGui();

        // Get user ID
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        refreshLayout();
    }


    /* Button listeners */
    public void getLoanBtn(View view) {
        ConfirmLoanTakeDialog dialog = new ConfirmLoanTakeDialog();
        Bundle args = new Bundle();
        args.putDouble("amount", checkedLoanAmount);
        args.putInt("daysToPayment", DAYS_TO_PAYMENT);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Confirm loan take");
    }

    public void payLoanButton(View view) {
        ConfirmLoanPayDialog dialog = new ConfirmLoanPayDialog();
        Bundle args = new Bundle();
        args.putDouble("amount", userLoan);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Confirm loan pay");
    }

    public void checkRadioButtons(View view) {
        RadioButton radioButton;
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        checkedLoanAmount = Double.parseDouble(radioButton.getText().toString());
    }

    public void refreshLayout() {
        getDataFromDb();
        setLayout();
    }

    /* Get data from database */
    private void getDataFromDb() {
        Statement statement = null;
        ResultSet resultSet;
        String sql;

        try {
            statement = DatabaseConnection.getConnection().createStatement();

            // Get user balance history
            sql = "SELECT * FROM us__users WHERE us_id = "+ userId;
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                userLoanPaymentDate = resultSet.getString("us_loan_payment_date");
                if (userLoanPaymentDate == null) userLoanPaymentDate = "-";
                userBalance = resultSet.getDouble("us_balance");
                userLoan = resultSet.getDouble("us_loan");
                numberOfPaidLoans = resultSet.getInt("us_paid_loans");
            }

        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

    private void setLayout() {
        userBalanceTv.setText(FormatHelper.doubleToTwoDecimal(userBalance));
        loanTv.setText(FormatHelper.doubleToTwoDecimal(userLoan));
        loanPaymentDateTv.setText(userLoanPaymentDate);

        userLoanInterests = userLoan * INTEREST_RATE;
        userLoanInterestTv.setText(FormatHelper.doubleToTwoDecimal(userLoanInterests));

        loanInterestRateTv.setText(FormatHelper.doubleToPercent(INTEREST_RATE));
        paymentTimeInDaysTv.setText(DAYS_TO_PAYMENT.toString());

        amount1Rb.setText(FormatHelper.cutAfterDot(LOAN_AMOUNT_LOWEST));
        amount2Rb.setText(FormatHelper.cutAfterDot(LOAN_AMOUNT_MEDIUM));
        amount3Rb.setText(FormatHelper.cutAfterDot(LOAN_AMOUNT_HIGH));

        // Disable getting loan or paying loan panel
        if (userLoan > 0) {
            enableButton(payLoanBtn);
            disableButton(getLoanBtn);
            errorTv.setVisibility(View.VISIBLE);
            amount1Rb.setEnabled(false);
            amount2Rb.setEnabled(false);
            amount3Rb.setEnabled(false);

        } else {
            enableButton(getLoanBtn);
            disableButton(payLoanBtn);
            errorTv.setVisibility(View.GONE);
            // Enable radio buttons
            amount1Rb.setEnabled(true);
            if (numberOfPaidLoans > 0) amount2Rb.setEnabled(true);
            if (numberOfPaidLoans > 1) amount3Rb.setEnabled(true);
        }




    }

    private void enableButton(Button button) {
        button.setTextColor(getColor(R.color.colorDelicateGray));
        button.setEnabled(true);
    }

    private void disableButton(Button button) {
        button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorText, null));
        button.setEnabled(false);
    }

    private void connectVariablesToGui() {
        getLoanBtn = findViewById(R.id.act_loans_get_loan_btn);
        payLoanBtn = findViewById(R.id.act_loans_pay_loan_btn);

        loanTv = findViewById(R.id.act_loans_loans_value_tv);
        userLoanInterestTv = findViewById(R.id.act_loans_interest_value_tv);
        loanPaymentDateTv = findViewById(R.id.act_loans_paymentDate_tv);
        userBalanceTv = findViewById(R.id.act_loans_balance_amount_tv);
        loanInterestRateTv = findViewById(R.id.act_loans_interests_tv);
        paymentTimeInDaysTv = findViewById(R.id.act_loans_days_to_pay_tv);
        errorTv = findViewById(R.id.act_loans_error_tv);

        radioGroup = findViewById(R.id.act_loans_radioGroup);
        amount1Rb = findViewById(R.id.act_loans_amount1_rb);
        amount2Rb = findViewById(R.id.act_loans_amount2_rb);
        amount3Rb = findViewById(R.id.act_loans_amount3_rb);
    }
}