package com.example.simtradergpw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simtradergpw.DatabaseCommunication;
import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    // Declare variables
    private static final int MIN_CHARACTERS = 3, PASSWORD_MIN_CHARACTERS = 5;
    private EditText mLoginEditText, mEmailEditText, mPasswordEditText, mConfirmPasswordEditText;
    private TextView mLoginErrorTV, mEmailErrorTV, mPassword1ErrorTV, mPassword2ErrorTV;
    private ProgressBar mProgressBar;

    // Password requirements
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +           //at least 1 digit
//                    "(?=.*[a-z])" +         //at least 1 lower case letter
//                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +        //any letter
//                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +             //no white spaces
                    ".{" + PASSWORD_MIN_CHARACTERS + ",}" +                 //at least 5 characters
                    "$");


    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        connectVariablesToGui();
    }


    public void signUpButton(View view) {
        if (validateLogin() & validateEmail() & validatePassword() & validatePasswordConfirmation()) {
            createUser();
        } else {
            return;
        }
    }


    public void cancelButton(View view) {
        finish();
    }

    private boolean validateLogin() {
        if (mLoginEditText.length() == 0) {
            mLoginErrorTV.setText(getResources().getString(R.string.empty_field));
            mLoginEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else if (mLoginEditText.length() < MIN_CHARACTERS) {
            mLoginErrorTV.setText(getResources().getString(R.string.field_too_short));
            mLoginEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else {
            mLoginErrorTV.setText("");
            mLoginEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border, null));
            return true;
        }
    }

    private boolean validateEmail() {
        final String emailInput = mEmailEditText.getText().toString().trim();

        if (mEmailEditText.length() == 0) {
            mEmailErrorTV.setText(getResources().getString(R.string.empty_field));
            mEmailEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            mEmailErrorTV.setText(getResources().getString(R.string.incorrect_email_structure));
            mEmailEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;

        } else {
            mEmailErrorTV.setText("");
            mEmailEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border, null));
            return true;

        }

    }

    private boolean validatePassword() {
        String passwordInput = mPasswordEditText.getText().toString().trim();

        if (mPasswordEditText.length() == 0) {
            mPassword1ErrorTV.setText(getResources().getString(R.string.empty_field));
            mPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {

            if (mPasswordEditText.length() < PASSWORD_MIN_CHARACTERS)
                mPassword1ErrorTV.setText(getResources().getString(R.string.password_too_short));
            else
                mPassword1ErrorTV.setText(getResources().getString(R.string.incorrect_password_structure));

            mPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else {
            mPassword1ErrorTV.setText("");
            mPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border, null));
            return true;
        }
    }

    private boolean validatePasswordConfirmation() {
        String password = mPasswordEditText.getText().toString();
        String passwordConfirmation = mConfirmPasswordEditText.getText().toString();

        if (mConfirmPasswordEditText.length() == 0) {
            mPassword2ErrorTV.setText(getResources().getString(R.string.empty_field));
            mConfirmPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else if (mConfirmPasswordEditText.length() < MIN_CHARACTERS) {
            mPassword2ErrorTV.setText(getResources().getString(R.string.field_too_short));
            mConfirmPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
            // Check whether both passwords are the same
        } else if (!password.equals(passwordConfirmation)) {
            mPassword2ErrorTV.setText(getResources().getString(R.string.different_passwords));
//            mPassword2ErrorTV.setText(mPasswordEditText.getText() + " != " + mConfirmPasswordEditText.getText());
            mConfirmPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            mPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
            return false;
        } else {
            mPassword2ErrorTV.setText("");
            mConfirmPasswordEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border, null));
            return true;
        }
    }

    private void createUser() {
        final String emailInput = mEmailEditText.getText().toString().trim();
        final String loginInput = mLoginEditText.getText().toString().trim();
        final String passwordInput = mPasswordEditText.getText().toString();

        // Display progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        Statement statement = null;
        try {
            statement = DatabaseConnection.getConnection().createStatement();

            /*#### Check if login and email are not taken ####*/
            String sqlIsUser = "SELECT * FROM us__users WHERE us_login='" + loginInput + "' OR us_email='" + emailInput + "'";
            ResultSet resultIsUser = statement.executeQuery(sqlIsUser);

            if (!resultIsUser.next()){
                // Create new user
                DatabaseCommunication.addNewUser(this, loginInput, emailInput, passwordInput);

                // Hide progress bar
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this, "Utworzono nowe konto!", Toast.LENGTH_SHORT).show();
                finish();
            } else {

                // Check if login taken
                String sqlIsLogin = "SELECT * FROM us__users WHERE us_login='" + loginInput +"'";
                ResultSet resultIsLogin = statement.executeQuery(sqlIsLogin);

                if (resultIsLogin.next()){
                    // Show error - login taken
                    mLoginErrorTV.setText(getResources().getString(R.string.login_occupied));
                    mLoginEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
                }

                // Check if email taken
                String sqlIsEmail = "SELECT * FROM us__users WHERE us_email='" + emailInput +"'";
                ResultSet resultIsEmail = statement.executeQuery(sqlIsEmail);
                if (resultIsEmail.next()){
                    // Show error - email taken
                    mEmailErrorTV.setText(getResources().getString(R.string.email_occupied));
                    mEmailEditText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_border_error, null));
                }

                // Hide progress bar
                mProgressBar.setVisibility(View.GONE);
            }

        } catch (SQLException throwables) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_SHORT).show();
            throwables.printStackTrace();
        }

    }

    private void connectVariablesToGui() {
        mLoginEditText = findViewById(R.id.act_sign_up_et_login);
        mEmailEditText = findViewById(R.id.act_sign_up_et_email);
        mPasswordEditText = findViewById(R.id.act_sign_up_et_password);
        mConfirmPasswordEditText = findViewById(R.id.act_sign_up_et_confirm_password);

        mLoginErrorTV = findViewById(R.id.act_sign_up_tv_login_error);
        mEmailErrorTV = findViewById(R.id.act_sign_up_tv_email_error);
        mPassword1ErrorTV = findViewById(R.id.act_sign_up_tv_password1_error);
        mPassword2ErrorTV = findViewById(R.id.act_sign_up_tv_password2_error);

        mProgressBar = findViewById(R.id.act_sign_up_circuralProgressBar);
    }
}