package com.example.simtradergpw.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.simtradergpw.DatabaseCommunication;
import com.example.simtradergpw.R;
import com.example.simtradergpw.activity.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class ResetProgressDialog extends AppCompatDialogFragment {
    int userId;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reset_progress, null);

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
                DatabaseCommunication.resetProgress(getContext(), userId);

                Toast.makeText(getContext(), getString(R.string.reset_progress_toast), Toast.LENGTH_LONG).show();
            }
        });

        return builder.create();
    }
}
