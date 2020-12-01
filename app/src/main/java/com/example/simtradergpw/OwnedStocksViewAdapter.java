package com.example.simtradergpw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class OwnedStocksViewAdapter extends RecyclerView.Adapter<OwnedStocksViewAdapter.MyViewHolder> {
    private static final String TAG = "OwnedStocksViewAdapter";

    ArrayList<StockRecord> wig20ArrayList;
    Context context;

    public OwnedStocksViewAdapter(Context ct, ArrayList<StockRecord> mArrayList) {
        context = ct;
        wig20ArrayList = mArrayList;
    }

    @NonNull
    @Override
    public OwnedStocksViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_view_owned_stocks, parent, false);
        return new OwnedStocksViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnedStocksViewAdapter.MyViewHolder holder, int position) {
        StockRecord mStockRecord = wig20ArrayList.get(position);
        final String sName = mStockRecord.getName();
        final String sTicker = mStockRecord.getTicker();
        final Integer quantity = mStockRecord.getOwnedQuantity();
        final Double estimatedValue = Double.parseDouble( mStockRecord.getLast().replace(",", ".")) * mStockRecord.getOwnedQuantity();

        holder.nameTv.setText(sName);
        holder.tickerTv.setText(sTicker);
        holder.quantityTv.setText(quantity.toString());
        holder.estimatedValueTv.setText(doubleToTwoDecimal(estimatedValue) + " PLN");
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

    /* ######### Other functions ######### */
    private String doubleToTwoDecimal(Double number) {
        // Format Double to two decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(number);
    }
}
