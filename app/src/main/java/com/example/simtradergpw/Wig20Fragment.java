package com.example.simtradergpw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class Wig20Fragment extends Fragment {

    ListView wig20ListView;
    ArrayList<StockRecord> mWig20records = null;


    Runnable mWig20Updater = new Runnable() {
        @Override
        public void run() {
            WebGateway webGateway = new WebGateway();
            mWig20records = webGateway.getPricesWig20();
            if (mWig20records != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWig20();
                    }
                });

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wig20list, container, false);

        wig20ListView = (ListView) view.findViewById(R.id.wig20_listview);

        Thread t = new Thread(mWig20Updater);
        t.start();
        return view;
    }

    void showWig20() {
        Wig20ListViewAdapter adapter = new Wig20ListViewAdapter(getContext(), R.layout.adapter_view_wig20, mWig20records);
        wig20ListView.setAdapter(adapter);
    }
}
