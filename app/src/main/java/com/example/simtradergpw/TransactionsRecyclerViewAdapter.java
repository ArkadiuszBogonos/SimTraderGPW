package com.example.simtradergpw;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simtradergpw.activity.SingleCompanyTransactionsActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class TransactionsRecyclerViewAdapter extends RecyclerView.Adapter<TransactionsRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = "TransactionsRecyclerViewAdapter";

    ArrayList<StockRecord> wig20ArrayList;
    Context context;

    public TransactionsRecyclerViewAdapter(Context ct, ArrayList<StockRecord> mArrayList) {
        context = ct;
        wig20ArrayList = mArrayList;
    }

    @NonNull
    @Override
    public TransactionsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_view_transactions, parent, false);
        return new TransactionsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsRecyclerViewAdapter.MyViewHolder holder, int position) {
        StockRecord mStockRecord = wig20ArrayList.get(position);
        final String sTicker = mStockRecord.getTicker();
        final String sTimeStamp = mStockRecord.getTimeStamp();
        final Double sPrice = mStockRecord.getLast();
        final Integer sQuantity = mStockRecord.getOwnedQuantity();
        final Boolean isBuy = mStockRecord.getIsBuy();

        final Double sTotal = sPrice * sQuantity;

        holder.tickerTv.setText(sTicker);
        holder.timestampTv.setText(sTimeStamp);
        holder.quantityTv.setText(sQuantity.toString());
        holder.priceTv.setText(sPrice.toString());
        holder.totalTv.setText(FormatHelper.doubleToTwoDecimal(sTotal));

        if (isBuy) {
            holder.directionTv.setText("Kupno");
            holder.directionSymblView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_buy_24, null));
        } else {
            holder.directionTv.setText("Sprzedaż");
            holder.directionSymblView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_sell_24, null));
        }

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
        TextView tickerTv, timestampTv, quantityTv, priceTv, totalTv, directionTv;
        View directionSymblView;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tickerTv = itemView.findViewById(R.id.adapter_transactions_ticker_tv);
            timestampTv = itemView.findViewById(R.id.adapter_transactions_timestamp_tv);
            quantityTv = itemView.findViewById(R.id.adapter_transactions_quantity_tv);
            priceTv = itemView.findViewById(R.id.adapter_transactions_price_tv);
            totalTv = itemView.findViewById(R.id.adapter_transactions_total_tv);
            directionTv = itemView.findViewById(R.id.adapter_transactions_direction_tv);
            directionSymblView = itemView.findViewById(R.id.adapter_transactions_direction_symbol);
            mainLayout = itemView.findViewById(R.id.adapter_transactions_main_layout);
        }
    }
}
