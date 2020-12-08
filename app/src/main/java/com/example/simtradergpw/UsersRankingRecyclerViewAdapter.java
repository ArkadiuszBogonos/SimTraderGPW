package com.example.simtradergpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simtradergpw.activity.LoginActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class UsersRankingRecyclerViewAdapter extends RecyclerView.Adapter<UsersRankingRecyclerViewAdapter.MyViewHolder>{
    ArrayList<UserRecord> usersArrayList;
    Context context;
    String currentUserLogin;


    public UsersRankingRecyclerViewAdapter(Context ct, ArrayList<UserRecord> mArrayList) {
        context = ct;
        usersArrayList = mArrayList;

        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        currentUserLogin = sharedPreferences.getString("userLogin", "");
    }

    @NonNull
    @Override
    public UsersRankingRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_view_ranking, parent, false);
        return new UsersRankingRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRankingRecyclerViewAdapter.MyViewHolder holder, int position) {
        UserRecord user = usersArrayList.get(position);

        String login = user.getLogin();
        Double money = user.getBalance();
        Double loans = user.getLoans();
        Double ownedStocksVal = user.getOwnedStocksValue();
        Double walletValue = money + ownedStocksVal - loans;

        holder.placeTv.setText(Integer.toString(position + 1));
        holder.loginTv.setText(login);
        holder.walletTv.setText(FormatHelper.cutAfterDot(walletValue));
        holder.moneyTv.setText(FormatHelper.cutAfterDot(money));
        holder.loansTv.setText(FormatHelper.cutAfterDot(loans));
        holder.ownedStocksValTv.setText(FormatHelper.cutAfterDot(ownedStocksVal));

        if (login.equals(currentUserLogin)) holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorDelicateGray));
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView loginTv, walletTv, moneyTv, ownedStocksValTv, loansTv, placeTv;
        CardView cardView;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeTv = itemView.findViewById(R.id.adapter_ranking_place_tv);
            loginTv = itemView.findViewById(R.id.adapter_ranking_login_tv);
            walletTv = itemView.findViewById(R.id.adapter_ranking_wallet_val_tv);
            moneyTv = itemView.findViewById(R.id.adapter_ranking_money_tv);
            loansTv = itemView.findViewById(R.id.adapter_ranking_loans_tv);
            ownedStocksValTv = itemView.findViewById(R.id.adapter_ranking_stocks_val_tv);

            mainLayout = itemView.findViewById(R.id.adapter_ranking_main_layout);

            cardView = itemView.findViewById(R.id.adapter_ranking_card_view);
        }
    }
}
