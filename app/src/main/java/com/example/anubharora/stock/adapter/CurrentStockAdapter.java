package com.example.anubharora.stock.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.anubharora.stock.helper.AppController;
import com.example.anubharora.stock.R;
import com.example.anubharora.stock.activity.StockGraphActivity;
import com.example.anubharora.stock.model.CurrentStock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by anubharora on 1/18/17.
 */

public class CurrentStockAdapter extends RecyclerView.Adapter<CurrentStockAdapter.CurrentStockViewHolder> {


    private String historicStart = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%22";
    private String historicMid1 = "%22)%20and%20startDate%20%3D%20%22";
    private String historicMid2 = "%22%20and%20endDate%20%3D%20%22";
    private String historicEnd = "%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private String endDate, startDate;
    private List<CurrentStock> stockData;
    Context context;


    public class CurrentStockViewHolder extends RecyclerView.ViewHolder {

        public TextView name, ask, symbol;
        public View view;

        public CurrentStockViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.company_name);
            symbol = (TextView) itemView.findViewById(R.id.ticker);
            ask = (TextView) itemView.findViewById(R.id.askValue);
        }
    }

    public CurrentStockAdapter(List<CurrentStock> stockData, Context context) {
        this.stockData = stockData;
        this.context = context;
    }

    @Override
    public CurrentStockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_current_layout, parent, false);
        return new CurrentStockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CurrentStockAdapter.CurrentStockViewHolder holder, final int position) {

        final CurrentStock currentStock = stockData.get(position);
        holder.name.setText(currentStock.getName());
        holder.symbol.setText(currentStock.getSymbol());

        if (currentStock.getASK() == null) {
            holder.ask.setText(currentStock.getASK() + " $");
        } else {
            holder.ask.setText(currentStock.getPreviousClose() + " $");
        }

        endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        startDate = null;
        Calendar calender = Calendar.getInstance();
        try {
            calender.setTime(formatter.parse(endDate));
            calender.add(Calendar.DATE, -7);
            Date date = new Date(calender.getTimeInMillis());
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, currentStock.getName(), Toast.LENGTH_SHORT).show();
                formUrl(historicStart, historicMid1, historicMid2, historicEnd, startDate, endDate,
                        currentStock.getSymbol(), currentStock.getName());

            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //stockData.remove(position);
                return true;
            }
        });

    }

    private void formUrl(String historicStart, String historicMid1, String historicMid2,
                         String historicEnd, String startDate, String endDate, String symbol, String name) {


        String url = historicStart + symbol + historicMid1 + startDate + historicMid2 + endDate + historicEnd;
        makeJsonObjectRequest(url, name);

    }

    private void makeJsonObjectRequest(String url, final String name) {

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        plotStockData(response, name);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void plotStockData(JSONObject response, String name) {
        ArrayList<String> weekStockPrices = new ArrayList<String>();
        ArrayList<String> weekDates = new ArrayList<String>();
        try {
            JSONObject query = response.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONArray quote = results.getJSONArray("quote");
            String symbol = quote.getJSONObject(0).getString("Symbol");
            for (int i = 0; i < quote.length(); i++) {
                JSONObject currentStockObject = quote.getJSONObject(i);
                weekStockPrices.add(i, currentStockObject.getString("Close"));
                weekDates.add(i, currentStockObject.getString("Date"));
            }

            Intent intent = new Intent(context, StockGraphActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putStringArrayListExtra("weekStockPrices", weekStockPrices);
            intent.putStringArrayListExtra("weekDates", weekDates);
            intent.putExtra("symbol", symbol);
            intent.putExtra("name", name);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return stockData.size();
    }


}
