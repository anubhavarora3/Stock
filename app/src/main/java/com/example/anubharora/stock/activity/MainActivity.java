package com.example.anubharora.stock.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.anubharora.stock.helper.AppController;
import com.example.anubharora.stock.R;
import com.example.anubharora.stock.adapter.CurrentStockAdapter;
import com.example.anubharora.stock.model.CurrentStock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static String currentStockUrlStart = "https://query.yahooapis.com/v1/public/yql?q=" +
            "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(";
    private static String currentStockUrlMid = "";
    private static String currentStockUrlEnd = ")&format=json&diagnostics=true&env=store%3A%2F%" +
            "2Fdatatables.org%2Falltableswithkeys&callback=";
    private static String currentStockUrl = "";
    private String stockNames = "";

    private static String TAG = MainActivity.class.getSimpleName();

    private static final int list_request = 1;

    private List<String> stockList = new ArrayList<String>();


    private RecyclerView recyclerView;
    private CurrentStockAdapter currentStockAdapter;
    private FloatingActionButton floatingActionButton;
    private String selectedSymbol = "";

    private List<CurrentStock> stockData = new ArrayList<CurrentStock>();
    private SharedPreferences.Editor sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPrefs = getSharedPreferences("StockNames", MODE_PRIVATE).edit();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);


        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        currentStockAdapter = new CurrentStockAdapter(stockData, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(currentStockAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StockListActivity.class);
                startActivityForResult(intent, list_request);
            }
        });


        SharedPreferences prefs = getSharedPreferences("StockNames", MODE_PRIVATE);
        Set<String> data = prefs.getStringSet("stocks", null);


        List<String> list = null;
        if (data != null) {
            stockList.addAll(data);
            list = new ArrayList<String>(data);
            if (list.size() == 1) {
                currentStockUrlMid = "\"" + list.get(0) + "\"";
            } else if (list.size() > 1) {
                currentStockUrlMid = "\"" + list.get(0) + "\"";
                for (int i = 1; i < list.size(); i++) {
                    currentStockUrlMid += ",\"" + list.get(i) + "\"";
                }
            }
            String savedUrl = currentStockUrlStart + currentStockUrlMid + currentStockUrlEnd;
            makeJsonObjectRequest(savedUrl, list.size());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == list_request) {
            if (resultCode == Activity.RESULT_OK) {
                selectedSymbol = data.getStringExtra("symbol_selected");
                selectedSymbol = selectedSymbol.replace("\"", "");

                SharedPreferences prefs = getSharedPreferences("StockNames", MODE_PRIVATE);

                currentStockUrlMid = "\"" + selectedSymbol + "\"";
                if (!stockList.contains(selectedSymbol)) {
                    stockList.add(selectedSymbol);

                    Set<String> set = new HashSet<String>();
                    set.addAll(stockList);
                    sharedPrefs.putStringSet("stocks", set);
                    sharedPrefs.putBoolean("flag",false);
                    sharedPrefs.commit();

                    currentStockUrl = currentStockUrlStart + currentStockUrlMid + currentStockUrlEnd;
                    makeJsonObjectRequest(currentStockUrl, 1);
                }


                //storing in sp

            }
        }
    }


    private void makeJsonObjectRequest(String currentStockUrl, final int size) {

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, currentStockUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (size == 1) {
                    setOneStockData(response, false);
                } else {
                    setMultipleStockData(response, false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void setMultipleStockData(JSONObject response, boolean b) {
        try {
            JSONObject query = response.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONArray quote = results.getJSONArray("quote");
            for (int i = 0; i < quote.length(); i++) {
                JSONObject stock = quote.getJSONObject(i);
                String name = stock.getString("Name");
                String symbol = stock.getString("symbol");
                String ask = stock.getString("Ask");
                String previousClose = stock.getString("PreviousClose");
                CurrentStock currentStock = new CurrentStock(name, symbol, ask, previousClose);
                stockData.add(currentStock);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentStockAdapter.notifyDataSetChanged();
    }

    private void setOneStockData(JSONObject response, Boolean isCache) {

        try {
            JSONObject query = response.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            //if (size == 1) {
            JSONObject quote = results.getJSONObject("quote");

            String name = quote.getString("Name");
            String symbol = quote.getString("symbol");
            String ask = quote.getString("Ask");
            String previousClose = quote.getString("PreviousClose");
            CurrentStock currentStock = new CurrentStock(name, symbol, ask, previousClose);
            stockData.add(currentStock);


        } catch (Exception e) {
            Log.e("@@--@@", e.toString());
        }
        currentStockAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
