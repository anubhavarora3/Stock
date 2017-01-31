package com.example.anubharora.stock.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anubharora.stock.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by anubharora on 1/22/17.
 */

public class StockListAdapter extends BaseAdapter {

    private Activity mactivity;
    private List<String[]> companyDetails;
    private List<String[]> companyFilterDetails;

    public StockListAdapter(Activity mactivity, List<String[]> companyDetails) {
        this.mactivity = mactivity;
        this.companyDetails = companyDetails;
        companyFilterDetails = new ArrayList<String[]>();
        this.companyFilterDetails.addAll(companyDetails);
    }

    @Override
    public int getCount() {
        return companyDetails.size();
    }

    @Override
    public String[] getItem(int position) {
        return companyDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mactivity).inflate(R.layout.stock_list_layout, null);
        }
        TextView name = (TextView) view.findViewById(R.id.name);
        //TextView symbol = (TextView)view.findViewById(R.id.symbol);

        String Name = companyDetails.get(position)[1];
        Name = trimString(Name);
        name.setText(Name);
        //symbol.setText(Symbol);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mactivity, companyDetails.get(position)[0], Toast.LENGTH_SHORT).show();
                String symbolSelected = companyDetails.get(position)[0];
                Intent resultIntent = new Intent();
                resultIntent.putExtra("symbol_selected", symbolSelected);
                mactivity.setResult(Activity.RESULT_OK, resultIntent);
                mactivity.finish();
            }
        });

        return view;
    }

    private String trimString(String str) {
        str = str.replace("\"", "");
        return str;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        companyDetails.clear();
        if (charText.length() == 0) {
            companyDetails.addAll(companyFilterDetails);
        } else {
            for (String[] wp : companyFilterDetails) {
                if (wp[1].toLowerCase(Locale.getDefault()).contains(charText)) {
                    companyDetails.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

