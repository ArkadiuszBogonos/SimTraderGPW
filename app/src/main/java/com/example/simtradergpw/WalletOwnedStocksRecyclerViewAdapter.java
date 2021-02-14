package com.example.simtradergpw;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simtradergpw.activity.CompanyDetailsActivity;
import com.example.simtradergpw.activity.SingleCompanyTransactionsActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class WalletOwnedStocksRecyclerViewAdapter extends RecyclerView.Adapter<WalletOwnedStocksRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = "OwnedStocksViewAdapter";

    ArrayList<StockRecord> wig20ArrayList;
    Context context;

    public WalletOwnedStocksRecyclerViewAdapter(Context ct, ArrayList<StockRecord> mArrayList) {
        context = ct;
        wig20ArrayList = mArrayList;
    }

    @NonNull
    @Override
    public WalletOwnedStocksRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_view_owned_stocks, parent, false);
        return new WalletOwnedStocksRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletOwnedStocksRecyclerViewAdapter.MyViewHolder holder, int position) {
        StockRecord mStockRecord = wig20ArrayList.get(position);
        final String sName = mStockRecord.getName();
        final String sTicker = mStockRecord.getTicker();
        final Integer quantity = mStockRecord.getOwnedQuantity();
        final Double sPrice = mStockRecord.getLast();
        final Double estimatedValue = sPrice * quantity;

        holder.nameTv.setText(sName);
        holder.tickerTv.setText(sTicker);
        holder.quantityTv.setText(quantity.toString());
        holder.estimatedValueTv.setText(FormatHelper.doubleToTwoDecimal(estimatedValue) + " PLN");

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleCompanyTransactionsActivity.class);
                intent.putExtra("ticker", sTicker);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wig20ArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, tickerTv, quantityTv, estimatedValueTv;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.adapter_owned_stocks_name_tv);
            tickerTv = itemView.findViewById(R.id.adapter_owned_stocks_ticker_tv);
            quantityTv = itemView.findViewById(R.id.adapter_owned_stocks_quantity_tv);
            estimatedValueTv = itemView.findViewById(R.id.adapter_owned_stocks_estimated_value_tv);
            mainLayout = itemView.findViewById(R.id.adapter_owned_stocks_main_layout);
        }
    }
}
