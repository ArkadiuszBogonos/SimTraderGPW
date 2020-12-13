package com.example.simtradergpw.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.simtradergpw.DatabaseCommunication;
import com.example.simtradergpw.R;
import com.example.simtradergpw.activity.LoansActivity;
import com.example.simtradergpw.activity.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class ConfirmLoanPayDialog extends AppCompatDialogFragment {
    TextView dialogMessage;
    int userId;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_simple_message, null);

        // Get passed data
        final Double amount = getArguments().getDouble("amount");
        Double totalToPay = amount + amount * LoansActivity.INTEREST_RATE;

        // Set dialog message
        dialogMessage = view.findViewById(R.id.dialog_text_tv);
        dialogMessage.setText(getResources().getString(R.string.pay_loan_message_1) +" " + totalToPay
                + " " + getResources().getString(R.string.pay_loan_message_2));

        // Get user ID
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        builder.setView(view).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DatabaseCommunication.userHaveEnoughMoney(getContext(), userId,amount)){
                    DatabaseCommunication.payLoan(getContext(), userId, amount);
                    ((LoansActivity)getActivity()).refreshLayout();
                } else {
                    Toast.makeText(getContext(), "Niewystarczająca ilość gotówki", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return builder.create();
    }


}
