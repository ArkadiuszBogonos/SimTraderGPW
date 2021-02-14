package com.example.simtradergpw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class LoginActivity extends AppCompatActivity {
    // Declare variables
    public static final String SHARED_PREFS = "sharedPrefs";

    private EditText mLoginEditText, mPasswordEditText;
    private TextView mLoginErrorTV, mPasswordErrorTV;
    private ProgressBar mProgressBar;

    private String mLogin, mPassword;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectVariablesToGui();
        setConnection();
    }

    /* ######### CONTROLS ######### */
    public void loginButton(View view) {

        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    public void signUpButton(View view) {
        openSignUpActivity();
    }


    /* ######### Validate input functions ######### */
    private boolean validateUsername() {
        if (mLoginEditText.length() == 0) {
            mLoginErrorTV.setText(getResources().getString(R.string.empty_field));
            mLoginEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else if (mLoginEditText.length() < 3) {
            mLoginErrorTV.setText(getResources().getString(R.string.field_too_short));
            mLoginEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else {
            mLoginErrorTV.setText("");
            mLoginEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border, null));
            return true;
        }
    }

    private boolean validatePassword() {
        if (mPasswordEditText.length() == 0) {
            mPasswordErrorTV.setText(getResources().getString(R.string.empty_field));
            mPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else if (mPasswordEditText.length() < 3) {
            mPasswordErrorTV.setText(getResources().getString(R.string.field_too_short));
            mPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else {
            mPasswordErrorTV.setText("");
            mPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border, null));
            return true;
        }
    }

    /* ######### Database functions ######### */
    private void isUser() {
        // ----------------------------------------------------------------------------------
        // Functions checks whether combination of given login and password exists in database.
        // If so, opens MainActivity, otherwise shows an error.
        // ----------------------------------------------------------------------------------

        // Display progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        // Read user input
        final String userEnteredLogin = mLoginEditText.getText().toString().trim();
        final String userEnteredPassword = mPasswordEditText.getText().toString().trim();

        // Check if connection is ok
        setConnection();

        Statement statement = null;
        try {
            statement = DatabaseConnection.getConnection().createStatement();

            String sqlIsUser = "SELECT * FROM us__users WHERE us_login='" + userEnteredLogin + "' AND us_password='" + userEnteredPassword+"'";
            ResultSet resultIsUser = statement.executeQuery(sqlIsUser);

            if (resultIsUser.next()){
                String userLogin, userEmail;
                Float userBalance;
                Integer userId;

                userId = resultIsUser.getInt("us_id");
                userLogin = resultIsUser.getString("us_login");
                userEmail = resultIsUser.getString("us_email");
                userBalance = resultIsUser.getFloat("us_balance");
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt("userId", userId);
                editor.putString("userLogin", userLogin);
                editor.putString("userEmail", userEmail);
                editor.putFloat("userBalance", userBalance);
                editor.apply();

                mProgressBar.setVisibility(View.INVISIBLE);
                openMainActivity();
            } else {
//                mLoginErrorTV.setText(getResources().getString(R.string.wrong_credentials));
                mProgressBar.setVisibility(View.INVISIBLE);
                mLoginEditText.setBackground(getResources().getDrawable(R.drawable.round_border_error));
                mPasswordEditText.setBackground(getResources().getDrawable(R.drawable.round_border_error));
                Toast.makeText(LoginActivity.this, "Nieprawidłowy login lub hasło.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException throwables) {
            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }
    }

    private void setConnection() {
        try {
            if (DatabaseConnection.getConnection() == null || DatabaseConnection.getConnection().isClosed()) {
                DatabaseConnection.setConnection();
            }
        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }

    /* ######### GO TO functions ######### */
    // Go to app's main panel
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Go to sign up panel
    private void openSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


    /* ######### Other functions ######### */
    // Hook on GUI elements
    private void connectVariablesToGui() {
        mLoginEditText = findViewById(R.id.act_login_et_login);
        mPasswordEditText = findViewById(R.id.act_login_et_password);
        mLoginErrorTV = findViewById(R.id.act_login_tv_login_error);
        mPasswordErrorTV = findViewById(R.id.act_login_tv_password_error);
        mProgressBar = findViewById(R.id.act_login_circuralProgressBar);
    }

}

