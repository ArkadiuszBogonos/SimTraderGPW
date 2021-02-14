package com.example.simtradergpw;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simtradergpw.activity.CompanyDetailsActivity;

import java.util.ArrayList;

/* Adapter for RecyclerView element */
public class Wig20RecyclerViewAdapter extends RecyclerView.Adapter<Wig20RecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = "Wig20RecyclerViewAdapter";

    ArrayList<StockRecord> wig20ArrayList;
    Context context;

    public Wig20RecyclerViewAdapter(Context ct, ArrayList<StockRecord> mArrayList) {
        context = ct;
        wig20ArrayList = mArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_view_wig20, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StockRecord mStockRecord = wig20ArrayList.get(position);
        final String sName = mStockRecord.getName();
        final String sTicker = mStockRecord.getTicker();
        final Double sLast = mStockRecord.getLast();
        final Double sPercentageChange = mStockRecord.getPercentageChange();

        holder.nameTv.setText(sName);
        holder.tickerTv.setText(sTicker);
        holder.lastTv.setText(sLast.toString());
        holder.pchangeTv.setText(sPercentageChange.toString()+"%");


        if (sPercentageChange == 0) {
            holder.changeSymbolIv.setImageResource(R.drawable.ic_baseline_no_change_grey_24);
            holder.pchangeTv.setTextColor(ContextCompat.getColor(context, R.color.colorText));
        }
        else if (sPercentageChange > 0) {
            holder.changeSymbolIv.setImageResource(R.drawable.ic_baseline_arrow_drop_up_green_24);
            holder.pchangeTv.setTextColor(ContextCompat.getColor(context, R.color.colorTrendingUp));
        }
        else if (sPercentageChange < 0) {
            holder.changeSymbolIv.setImageResource(R.drawable.ic_baseline_arrow_drop_down_red_24);
            holder.pchangeTv.setTextColor(ContextCompat.getColor(context, R.color.colorTrendingDown));
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CompanyDetailsActivity.class);
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
        TextView nameTv, tickerTv, lastTv, pchangeTv;
        ImageView changeSymbolIv;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.adapter_wig20_name_tv);
            tickerTv = itemView.findViewById(R.id.adapter_wig20_ticker_tv);
            lastTv = itemView.findViewById(R.id.adapter_wig20_last_tv);
            pchangeTv = itemView.findViewById(R.id.adapter_wig20_pchange_tv);
            changeSymbolIv = itemView.findViewById(R.id.adapter_wig20_change_img);
            mainLayout = itemView.findViewById(R.id.adapter_wig20_main_layout);
        }
    }
}
