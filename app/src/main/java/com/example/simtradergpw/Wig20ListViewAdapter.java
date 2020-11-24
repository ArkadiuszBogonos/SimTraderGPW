package com.example.simtradergpw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.simtradergpw.R;
import com.example.simtradergpw.StockRecord;

import java.util.ArrayList;


public class Wig20ListViewAdapter extends ArrayAdapter<StockRecord> {

    private static final String TAG = "Wig20ListViewAdapter";
    private Context mContext;
    int mResource;

    public Wig20ListViewAdapter(Context context, int resource, ArrayList<StockRecord> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String ticker = getItem(position).getTicker();
        String last = getItem(position).getLast();
        String percentageChange = getItem(position).getPercentageChange();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);


        TextView tvName = convertView.findViewById(R.id.adapter_wig20_name_tv);
        TextView tvTicker = convertView.findViewById(R.id.adapter_wig20_ticker_tv);
        TextView tvLast = convertView.findViewById(R.id.adapter_wig20_last_tv);
        TextView tvPercentageChange = convertView.findViewById(R.id.adapter_wig20_pchange_tv);

        tvName.setText(name);
        tvTicker.setText(ticker);
        tvLast.setText(last);
        tvPercentageChange.setText(percentageChange);

        return convertView;
    }
}
